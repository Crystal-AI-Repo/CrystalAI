package com.lovelycatv.ai.crystal.dispatcher.controller.v1

import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Dispatcher.WebManagerController.GET_NODE_BY_ID
import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Dispatcher.WebManagerController.LIST_NODES
import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Dispatcher.WebManagerController.MAPPING
import com.lovelycatv.ai.crystal.common.GlobalConstants.ApiVersionControl.API_PREFIX_VERSION_1
import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.dispatcher.data.node.RegisteredNode
import com.lovelycatv.ai.crystal.dispatcher.service.NodeManagerService
import kotlinx.coroutines.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * @author lovelycat
 * @since 2025-02-26 16:11
 * @version 1.0
 */
@RestController
@RequestMapping(API_PREFIX_VERSION_1 + MAPPING)
class WebManagerControllerV1(
    private val nodeManagerService: NodeManagerService
) : IWebManagerControllerV1 {
    /**
     * List all registered nodes
     *
     * @return All [RegisteredNode]
     */
    @GetMapping(LIST_NODES)
    override fun listAllNodes(): Result<List<RegisteredNode>> {
        return Result.success("", nodeManagerService.listAllNodes())
    }

    /**
     * Get registered node by id
     *
     * @param nodeId nodeId
     * @return [RegisteredNode]?
     */
    @GetMapping(GET_NODE_BY_ID)
    override fun getNodeById(@RequestParam("nodeId") nodeId: String): Result<RegisteredNode?> {
        return Result.success("", nodeManagerService.listAllNodes().find { it.nodeId == nodeId })
    }
}