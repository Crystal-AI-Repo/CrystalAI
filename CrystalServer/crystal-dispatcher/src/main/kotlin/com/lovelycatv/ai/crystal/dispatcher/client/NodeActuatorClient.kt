package com.lovelycatv.ai.crystal.dispatcher.client

import com.lovelycatv.ai.crystal.common.response.actuator.NodeHealthResponse
import com.lovelycatv.ai.crystal.common.client.IFeignClient
import feign.RequestLine
import org.springframework.cloud.openfeign.FeignClient

/**
 * @author lovelycat
 * @since 2025-02-07 22:48
 * @version 1.0
 */
@FeignClient(name = "nodeActuatorClient")
interface NodeActuatorClient : IFeignClient {
    @RequestLine("GET /actuator/health")
    fun getHealthStatus(): NodeHealthResponse
}