package com.lovelycatv.ai.crystal.node.controller

import com.lovelycatv.ai.crystal.common.client.OllamaClient
import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.common.response.node.probe.NodeProbeResult
import com.lovelycatv.ai.crystal.node.config.NodeConfiguration
import jakarta.annotation.Resource
import org.springframework.beans.factory.annotation.Value
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author lovelycat
 * @since 2025-02-06 19:31
 * @version 1.0
 */
@RestController
@RequestMapping("/probe")
class ProbeController(
    @Resource(name = "localIpAddress")
    private val localIpAddress: String,
    @Value("\${server.port}")
    private val serverPort: Int,
    private val nodeConfiguration: NodeConfiguration,
    private val ollamaFeignClient: OllamaClient
) {
    @GetMapping("/info")
    fun nodeInfo(): Result<NodeProbeResult> {
        return Result.success(
            "",
            NodeProbeResult(
                localIpAddress,
                serverPort,
                ollamaModels = ollamaFeignClient.getOllamaModels().models
            )
        )
    }
}