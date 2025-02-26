package com.lovelycatv.ai.crystal.dispatcher.controller

import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.dispatcher.data.node.RegisteredNode

/**
 * @author lovelycat
 * @since 2025-02-26 16:11
 * @version 1.0
 */
interface IWebManagerController {
    /**
     * List all registered nodes
     *
     * @return All [RegisteredNode]
     */
    fun listAllNodes(): Result<List<RegisteredNode>>
}