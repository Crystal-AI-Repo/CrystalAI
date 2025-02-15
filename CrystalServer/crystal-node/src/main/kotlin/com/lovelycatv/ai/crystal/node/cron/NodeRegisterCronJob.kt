package com.lovelycatv.ai.crystal.node.cron

import com.lovelycatv.ai.crystal.common.client.getFeignClient
import com.lovelycatv.ai.crystal.common.client.safeRequest
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.common.util.toJSONString
import com.lovelycatv.ai.crystal.node.Global
import com.lovelycatv.ai.crystal.node.client.NodeDispatcherClient
import com.lovelycatv.ai.crystal.node.config.NetworkConfig
import com.lovelycatv.ai.crystal.node.config.NodeConfiguration
import jakarta.annotation.Resource
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * @author lovelycat
 * @since 2025-02-15 21:02
 * @version 1.0
 */
@Component
@EnableScheduling
class NodeRegisterCronJob(
    private val networkConfig: NetworkConfig,
    private val nodeConfiguration: NodeConfiguration
) {
    private val log = this.logger()

    @Scheduled(cron = "0/3 * * * * ?")
    fun checkRegisterStatus() {
        val dispatcherClient = getFeignClient<NodeDispatcherClient>(nodeConfiguration.dispatcher.baseUrl)
        if (Global.isNodeRegistered()) {
            // Check registration status
            val currentUUID = Global.Variables.currentNodeUUID!!
            val result = dispatcherClient.safeRequest { checkNode(currentUUID) }
            if (result == null || !result.isSuccessful() || result.data == false) {
                log.warn("This node was not discovered by the dispatcher. Current UUID: [{}]", currentUUID)

                // Clear registration status
                Global.clearNodeRegistrationStatus()
            }
        } else {
            // Register with dispatcher
            val result = dispatcherClient.safeRequest { registerNode(networkConfig.localIpAddress(), networkConfig.applicationPort, false) }
            if (result == null || !result.isSuccessful() || result.data == null || result.data?.success == false || result.data?.uuid == null) {
                log.warn("Failed to register to dispatcher service. Response: {}", result?.toJSONString())
            } else {
                val uuid = result.data!!.uuid!!
                log.info("Registered to dispatcher, uuid: [{}]", uuid)

                // Update current node uuid
                Global.nodeRegistered(uuid)
            }
        }
    }
}