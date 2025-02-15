package com.lovelycatv.ai.crystal.dispatcher.manager

import com.lovelycatv.ai.crystal.common.client.NodeActuatorClient
import com.lovelycatv.ai.crystal.common.client.getFeignClient
import com.lovelycatv.ai.crystal.common.client.safeRequest
import com.lovelycatv.ai.crystal.common.response.dispatcher.NodeRegisterResult
import com.lovelycatv.ai.crystal.common.util.catchException
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.dispatcher.client.NodeProbeClient
import com.lovelycatv.ai.crystal.dispatcher.data.node.RegisteredNode
import java.util.*

/**
 * @author lovelycat
 * @since 2025-02-15 18:23
 * @version 1.0
 */
abstract class AbstractNodeManager {
    private val log = this.logger()

    protected val registeredNodes = mutableMapOf<String, RegisteredNode>()

    val allRegisteredNodes get() = this.registeredNodes.values

    fun getRegisteredNode(nodeId: String): RegisteredNode? {
        return registeredNodes[nodeId]
    }

    fun getRegisteredNode(host: String, port: Int): RegisteredNode? {
        return registeredNodes.values.find { it.host == host && it.port == port }
    }

    protected fun updateNode(nodeId: String, fx: RegisteredNode.() -> RegisteredNode): Boolean {
        val node = this.registeredNodes[nodeId] ?: return false
        this.registeredNodes[nodeId] = fx.invoke(node)
        return true
    }

    fun registerNode(nodeHost: String, nodePort: Int, ssl: Boolean): NodeRegisterResult {
        log.info("Received register request from [{}:{}] (SSL: {})", nodeHost, nodePort, ssl)

        val url = "${if (ssl) "https" else "http"}://$nodeHost:$nodePort"

        val client: NodeActuatorClient = getFeignClient(url)

        val result = client.safeRequest { getHealthStatus() }

        return if (result?.isUp() == true) {
            val nodeInfoResult = getFeignClient<NodeProbeClient>(url).safeRequest { getNodeInfo() }

            if (nodeInfoResult?.isSuccessful() == true) {
                val nodeInfo = nodeInfoResult.data
                    ?: return NodeRegisterResult(
                        success = false,
                        message = "Node returned error code: ${nodeInfoResult.code}, message: ${nodeInfoResult.message}"
                    )

                val existingNode = this.getRegisteredNode(nodeInfo.nodeIp, nodeInfo.nodePort)

                // UUID of existing node. If does not exist, generate a random UUID
                val nodeId = existingNode?.nodeId ?: UUID.randomUUID().toString()

                val currentTimestamp = System.currentTimeMillis()
                this.registeredNodes[nodeId] = if (this.registeredNodes.containsKey(nodeId)) {
                    this.registeredNodes[nodeId]!!.copy(
                        host = nodeInfo.nodeIp,
                        port = nodeInfo.nodePort,
                        ssl = nodeInfo.ssl,
                        nodeId = nodeId,
                        isAlive = true,
                        lastAliveTimestamp = currentTimestamp,
                        lastAliveCheckTimestamp = currentTimestamp,
                        lastUpdateTimestamp = currentTimestamp,
                        ollamaModels = nodeInfo.ollamaModels
                    )
                } else {
                    RegisteredNode(
                        host = nodeInfo.nodeIp,
                        port = nodeInfo.nodePort,
                        ssl = nodeInfo.ssl,
                        nodeId = nodeId,
                        isAlive = true,
                        registeredTimestamp = currentTimestamp,
                        lastAliveTimestamp = currentTimestamp,
                        lastAliveCheckTimestamp = currentTimestamp,
                        lastUpdateTimestamp = currentTimestamp,
                        ollamaModels = nodeInfo.ollamaModels
                    )
                }

                log.info("Node [{}:{}] (SSL: {}) is registered.", nodeHost, nodePort, ssl)

                NodeRegisterResult(success = true, uuid = nodeId)
            } else {
                log.info("Node [{}:{}] (SSL: {}) register failed. Reason: Could not fetch node information", nodeHost, nodePort, ssl)

                return NodeRegisterResult(success = false, message = "Could not fetch node information")
            }
        } else {
            log.info("Node [{}:{}] (SSL: {}) register failed. Reason: Node is not available", nodeHost, nodePort, ssl)

            return NodeRegisterResult(success = false, message = "Node is not available")
        }
    }

    fun unregisterNode(nodeHost: String, nodePort: Int): Boolean {
        val existingNode = this.getRegisteredNode(nodeHost, nodePort) ?: return false
        this.registeredNodes.remove(existingNode.nodeId)
        return true
    }

    /**
     * Check and update node alive status
     *
     * @param nodeId NodeId
     * @return Is the node still alive && Node status successfully updated
     */
    abstract fun checkNodeStatus(nodeId: String): Boolean

    /**
     * Update node information (supported models, ...)
     *
     * @param nodeId NodeId
     * @return Node has been successfully updated
     */
    abstract fun updateNodeInfo(nodeId: String): Boolean
}