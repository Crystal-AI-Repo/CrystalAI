package com.lovelycatv.ai.crystal.dispatcher.client

import com.lovelycatv.ai.crystal.common.client.IFeignClient
import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.common.response.node.probe.NodeProbeResult
import feign.RequestLine
import org.springframework.cloud.openfeign.FeignClient

/**
 * @author lovelycat
 * @since 2025-02-09 22:15
 * @version 1.0
 */
@FeignClient("nodeProbeClient")
interface NodeProbeClient : IFeignClient {
    @RequestLine("GET /probe/info")
    fun getNodeInfo(): Result<NodeProbeResult>
}