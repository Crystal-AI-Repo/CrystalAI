package com.lovelycatv.ai.crystal.dispatcher.service

import com.lovelycatv.ai.crystal.common.response.dispatcher.NodeRegisterResult

/**
 * @author lovelycat
 * @since 2025-02-09 22:58
 * @version 1.0
 */
interface NodeManagerService {
    fun registerNode(nodeHost: String, nodePort: Int, ssl: Boolean): NodeRegisterResult

    fun unregisterNode(nodeHost: String, nodePort: Int): Boolean
}