package com.lovelycatv.ai.crystal.dispatcher.controller

import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.dispatcher.service.NodeManagerService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController


/**
 * @author lovelycat
 * @since 2025-02-06 19:11
 * @version 1.0
 */
@RestController
@RequestMapping("/node")
class NodeController(
    private val nodeManagerService: NodeManagerService
) {
    @PostMapping("/register")
    fun nodeRegister(
        @RequestParam("host")
        nodeHost: String,
        @RequestParam("port")
        nodePort: Int,
        @RequestParam("ssl", defaultValue = "0", required = false)
        ssl: Boolean
    ): Result<*> {
        val result = nodeManagerService.registerNode(nodeHost, nodePort, ssl)
        return if (result.success) {
            Result.success(result.message, result)
        } else {
            Result.badRequest(result.message)
        }
    }

    @PostMapping("/unregister")
    fun nodeUnregister(
        @RequestParam("host")
        nodeHost: String,
        @RequestParam("port")
        nodePort: Int
    ): Result<*> {
        return if (nodeManagerService.unregisterNode(nodeHost, nodePort)) {
            Result.success("Node has been unregistered")
        } else {
            Result.badRequest("Node does not exist or had been unregistered")
        }
    }

    @GetMapping("/check")
    fun nodeCheck(
        @RequestParam("uuid")
        uuid: String
    ): Result<*> {
        return Result.success("", nodeManagerService.isNodeRegistered(uuid))
    }
}