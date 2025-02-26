package com.lovelycatv.ai.crystal.dispatcher.service

import com.lovelycatv.ai.crystal.common.client.getFeignClient
import com.lovelycatv.ai.crystal.common.response.dispatcher.NodeRegisterResult
import com.lovelycatv.ai.crystal.common.client.NodeActuatorClient
import com.lovelycatv.ai.crystal.dispatcher.client.NodeProbeClient
import com.lovelycatv.ai.crystal.dispatcher.data.node.RegisteredNode
import com.lovelycatv.ai.crystal.dispatcher.manager.AbstractNodeManager
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import java.util.UUID

/**
 * @author lovelycat
 * @since 2025-02-07 02:03
 * @version 1.0
 */
@Service
class NodeManagerServiceImpl(
    private val nodeManager: AbstractNodeManager
) : NodeManagerService {
    override fun registerNode(nodeHost: String, nodePort: Int, ssl: Boolean): NodeRegisterResult {
        return nodeManager.registerNode(nodeHost, nodePort, ssl)
    }

    override fun unregisterNode(nodeHost: String, nodePort: Int): Boolean {
        return nodeManager.unregisterNode(nodeHost, nodePort)
    }

    override fun isNodeRegistered(nodeUUID: String): Boolean {
        val node = nodeManager.getRegisteredNode(nodeUUID)
        return node != null && node.isAlive
    }

    /**
     * List all registered nodes
     *
     * @return List of all [RegisteredNode]
     */
    override fun listAllNodes(): List<RegisteredNode> {
        return nodeManager.allRegisteredNodes
    }
}