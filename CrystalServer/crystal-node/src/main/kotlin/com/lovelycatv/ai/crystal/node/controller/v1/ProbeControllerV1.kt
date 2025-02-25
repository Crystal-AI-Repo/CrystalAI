package com.lovelycatv.ai.crystal.node.controller.v1

import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Node.ProbeController.NODE_INFO
import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Node.ProbeController.MAPPING
import com.lovelycatv.ai.crystal.common.GlobalConstants.ApiVersionControl.API_PREFIX_VERSION_1
import com.lovelycatv.ai.crystal.common.client.OllamaClient
import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.common.response.node.probe.NodeProbeResult
import com.lovelycatv.ai.crystal.node.config.NetworkConfig
import com.lovelycatv.ai.crystal.node.config.NodeConfiguration
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author lovelycat
 * @since 2025-02-06 19:31
 * @version 1.0
 */
@RestController
@RequestMapping(API_PREFIX_VERSION_1 + MAPPING)
class ProbeControllerV1(
    @Value("\${spring.application.name}")
    private val applicationName: String,
    private val networkConfig: NetworkConfig,
    private val nodeConfiguration: NodeConfiguration,
    private val ollamaFeignClient: OllamaClient
) : IProbeControllerV1 {
    @GetMapping(NODE_INFO)
    override fun nodeInfo(): Result<NodeProbeResult> {
        return Result.success(
            "",
            NodeProbeResult(
                applicationName,
                networkConfig.localIpAddress(),
                networkConfig.applicationPort,
                nodeConfiguration.isSsl,
                ollamaModels = ollamaFeignClient.getOllamaModels().models
            )
        )
    }
}