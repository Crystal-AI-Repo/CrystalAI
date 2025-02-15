package com.lovelycatv.ai.crystal.node.task

import com.lovelycatv.ai.crystal.common.client.safeRequest
import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.common.response.dispatcher.NodeRegisterResult
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.common.util.toJSONString
import com.lovelycatv.ai.crystal.node.client.NodeDispatcherClient
import com.lovelycatv.ai.crystal.node.config.NetworkConfig
import jakarta.annotation.Resource
import org.springframework.context.ConfigurableApplicationContext

/**
 * @author lovelycat
 * @since 2025-02-15 21:02
 * @version 1.0
 */
class NodeRegisterTask(
    private val localIpAddress: String,
    private val serverPort: Int = 8080,
    private val nodeDispatcherClient: NodeDispatcherClient
) : Thread("NodeRegister") {
    private val log = this.logger()

    override fun run() {
        var result: Result<NodeRegisterResult>? = null
        var counter = 0L
        while (result == null || !result.isSuccessful()) {
            if (counter > 0) {
                log.warn("({}) Failed to register to dispatcher service. Response: {}", counter, result?.toJSONString())
            }
            result = nodeDispatcherClient.safeRequest { registerNode(localIpAddress, serverPort, false) }
            counter++
            Thread.sleep(3000)
        }
        log.warn("({}) Failed to register to dispatcher service. Response: {}", counter, result.toJSONString())
    }
}