package com.lovelycatv.ai.crystal.node.client

import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Dispatcher.NodeController.MAPPING
import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Dispatcher.NodeController.NODE_CHECK
import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Dispatcher.NodeController.NODE_REGISTER
import com.lovelycatv.ai.crystal.common.GlobalConstants.ApiVersionControl.API_PREFIX_FOR_DISPATCHER
import com.lovelycatv.ai.crystal.common.client.IFeignClient
import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.common.response.dispatcher.NodeRegisterResult
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 * @author lovelycat
 * @since 2025-02-15 15:25
 * @version 1.0
 */
@FeignClient(name = "nodeDispatcherClient")
interface NodeDispatcherClient : IFeignClient {
    @PostMapping("$API_PREFIX_FOR_DISPATCHER$MAPPING$NODE_REGISTER")
    fun registerNode(
        @RequestParam("host")
        host: String,
        @RequestParam("port")
        port: Int,
        @RequestParam("ssl")
        ssl: Boolean,
    ): Result<NodeRegisterResult>

    @GetMapping("$API_PREFIX_FOR_DISPATCHER$MAPPING$NODE_CHECK")
    fun checkNode(@RequestParam("uuid") uuid: String): Result<Boolean>
}