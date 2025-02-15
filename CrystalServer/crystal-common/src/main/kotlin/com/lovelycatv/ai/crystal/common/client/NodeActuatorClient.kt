package com.lovelycatv.ai.crystal.common.client

import com.lovelycatv.ai.crystal.common.response.actuator.NodeHealthResponse
import com.lovelycatv.ai.crystal.common.client.IFeignClient
import feign.RequestLine
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping

/**
 * @author lovelycat
 * @since 2025-02-07 22:48
 * @version 1.0
 */
@FeignClient(name = "nodeActuatorClient")
interface NodeActuatorClient : IFeignClient {
    @GetMapping("/actuator/health")
    fun getHealthStatus(): NodeHealthResponse
}