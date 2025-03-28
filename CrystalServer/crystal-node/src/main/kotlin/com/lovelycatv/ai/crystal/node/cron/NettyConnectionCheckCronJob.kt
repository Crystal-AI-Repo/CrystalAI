package com.lovelycatv.ai.crystal.node.cron

import com.lovelycatv.ai.crystal.node.Global
import com.lovelycatv.ai.crystal.node.NodeRunningMode
import com.lovelycatv.ai.crystal.node.config.NodeConfiguration
import com.lovelycatv.ai.crystal.node.netty.NodeNettyClient
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * @author lovelycat
 * @since 2025-02-16 22:56
 * @version 1.0
 */
@Component
@EnableScheduling
class NettyConnectionCheckCronJob(
    private val nodeNettyClient: NodeNettyClient,
    private val nodeConfiguration: NodeConfiguration
) {
    @Scheduled(cron = "0/3 * * * * ?")
    fun checkNettyConnection() {
        if (nodeConfiguration._mode == NodeRunningMode.STANDALONE) {
            return
        }
        // Check if the node is registered to the dispatcher.
        if (Global.isNodeRegistered()) {
            if (!nodeNettyClient.isConnected) {
                // Connect to dispatcher
                nodeNettyClient.startClient(
                    host = nodeConfiguration.dispatcher.host,
                    port = Global.Variables.dispatcherCommunicationPort
                )
            }
        }
    }
}