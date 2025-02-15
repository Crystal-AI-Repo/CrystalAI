package com.lovelycatv.ai.crystal.dispatcher.client

import com.lovelycatv.ai.crystal.common.client.IFeignClient
import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.common.response.node.probe.NodeProbeResult
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping

/**
 * @author lovelycat
 * @since 2025-02-09 22:15
 * @version 1.0
 */
@FeignClient("nodeProbeClient")
interface NodeProbeClient : IFeignClient {
    @GetMapping("/probe/info")
    fun getNodeInfo(): Result<NodeProbeResult>
}