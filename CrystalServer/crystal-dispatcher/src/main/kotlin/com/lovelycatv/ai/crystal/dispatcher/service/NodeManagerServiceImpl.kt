package com.lovelycatv.ai.crystal.dispatcher.service

import com.lovelycatv.ai.crystal.common.client.getFeignClient
import com.lovelycatv.ai.crystal.common.response.dispatcher.NodeRegisterResult
import com.lovelycatv.ai.crystal.common.client.NodeActuatorClient
import com.lovelycatv.ai.crystal.dispatcher.client.NodeProbeClient
import com.lovelycatv.ai.crystal.dispatcher.data.node.RegisteredNode
import org.springframework.stereotype.Component
import java.util.UUID

/**
 * @author lovelycat
 * @since 2025-02-07 02:03
 * @version 1.0
 */
@Component
class NodeManagerServiceImpl : NodeManagerService {
    private val registeredNodes = mutableMapOf<String, RegisteredNode>()

    fun getRegisteredNode(nodeId: String): RegisteredNode? {
        return registeredNodes[nodeId]
    }

    fun getRegisteredNode(host: String, port: Int): RegisteredNode? {
        return registeredNodes.values.find { it.host == host && it.port == port }
    }

    override fun registerNode(nodeHost: String, nodePort: Int, ssl: Boolean): NodeRegisterResult {
        val url = "${if (ssl) "https" else "http"}://$nodeHost:$nodePort"

        val client: NodeActuatorClient = getFeignClient(url)

        val result = client.getHealthStatus()
        return if (result.isUp()) {
            val nodeInfoResult = getFeignClient<NodeProbeClient>(url).getNodeInfo()
            if (nodeInfoResult.isSuccessful()) {
                val nodeInfo = nodeInfoResult.data
                    ?: return NodeRegisterResult(
                        success = false,
                        message = "Node returned error code: ${nodeInfoResult.code}, message: ${nodeInfoResult.message}"
                    )

                val existingNode = this.getRegisteredNode(nodeInfo.nodeIp, nodeInfo.nodePort)

                // UUID of existing node. If does not exist, generate a random UUID
                val nodeId = existingNode?.nodeId ?: UUID.randomUUID().toString()

                this.registeredNodes[nodeId] = if (this.registeredNodes.containsKey(nodeId)) {
                    this.registeredNodes[nodeId]!!.copy(
                        host = nodeInfo.nodeIp,
                        port = nodeInfo.nodePort,
                        nodeId = nodeId,
                        lastAliveTimestamp = System.currentTimeMillis(),
                        ollamaModels = nodeInfo.ollamaModels
                    )
                } else {
                    RegisteredNode(
                        host = nodeInfo.nodeIp,
                        port = nodeInfo.nodePort,
                        nodeId = nodeId,
                        registeredTimestamp = System.currentTimeMillis(),
                        lastAliveTimestamp = System.currentTimeMillis(),
                        ollamaModels = nodeInfo.ollamaModels
                    )
                }
                NodeRegisterResult(success = true, uuid = nodeId)
            } else {
                return NodeRegisterResult(success = false, message = "Could not fetch node information")
            }
        } else {
            return NodeRegisterResult(success = false, message = "Node is not available")
        }
    }

    override fun unregisterNode(nodeHost: String, nodePort: Int): Boolean {
        val existingNode = this.getRegisteredNode(nodeHost, nodePort) ?: return false
        this.registeredNodes.remove(existingNode.nodeId)
        return true
    }
}