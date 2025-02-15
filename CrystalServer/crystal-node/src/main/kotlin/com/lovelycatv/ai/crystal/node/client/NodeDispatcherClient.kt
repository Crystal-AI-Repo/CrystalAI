package com.lovelycatv.ai.crystal.node.client

import com.lovelycatv.ai.crystal.common.client.IFeignClient
import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.common.response.dispatcher.NodeRegisterResult
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 * @author lovelycat
 * @since 2025-02-15 15:25
 * @version 1.0
 */
@FeignClient(name = "nodeDispatcherClient")
interface NodeDispatcherClient : IFeignClient {
    @PostMapping("/node/register")
    fun registerNode(
        @RequestParam("host")
        host: String,
        @RequestParam("port")
        port: Int,
        @RequestParam("ssl")
        ssl: Boolean,
    ): Result<NodeRegisterResult>
}