package com.lovelycatv.ai.crystal.dispatcher.manager

import com.lovelycatv.ai.crystal.common.client.NodeActuatorClient
import com.lovelycatv.ai.crystal.common.client.getFeignClient
import com.lovelycatv.ai.crystal.common.client.safeRequest
import com.lovelycatv.ai.crystal.dispatcher.client.NodeProbeClient
import com.lovelycatv.ai.crystal.dispatcher.config.DispatcherConfiguration
import org.springframework.stereotype.Component

/**
 * @author lovelycat
 * @since 2025-02-15 18:26
 * @version 1.0
 */
@Component
class NodeManager(
    dispatcherConfiguration: DispatcherConfiguration
) : AbstractNodeManager(dispatcherConfiguration) {
    /**
     * Check and update node alive status
     *
     * @param nodeId NodeId
     * @return Is the node still alive && Node status successfully updated
     */
    override fun checkNodeStatus(nodeId: String): Boolean {
        val node = super.getRegisteredNode(nodeId) ?: return false

        val client: NodeActuatorClient = getFeignClient(node.requestUrl)

        val isUp = client.safeRequest { getHealthStatus().isUp() }

        val currentTimestamp = System.currentTimeMillis()

        return super.updateNode(nodeId) {
            this.copy(
                isAlive = isUp == true,
                lastAliveTimestamp = if (isUp == true) currentTimestamp else this.lastAliveTimestamp,
                lastAliveCheckTimestamp = currentTimestamp
            )
        } && isUp == true
    }

    /**
     * Update node information (supported models, ...)
     *
     * @param nodeId NodeId
     * @return Node has been successfully updated
     */
    override fun updateNodeInfo(nodeId: String): Boolean {
        val node = super.getRegisteredNode(nodeId) ?: return false

        val nodeInfoResult = getFeignClient<NodeProbeClient>(node.requestUrl).safeRequest { getNodeInfo() }

        return if (nodeInfoResult == null) false else if (nodeInfoResult.isSuccessful() && nodeInfoResult.data != null) {
            val responseData = nodeInfoResult.data!!
            val currentTimestamp = System.currentTimeMillis()

            super.updateNode(nodeId) {
                this.copy(
                    lastUpdateTimestamp = currentTimestamp,
                    ollamaModels = responseData.ollamaModels
                )
            }
        } else false

    }
}