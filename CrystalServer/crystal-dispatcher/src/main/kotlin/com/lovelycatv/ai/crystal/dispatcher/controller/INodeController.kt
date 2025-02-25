package com.lovelycatv.ai.crystal.dispatcher.controller

import com.lovelycatv.ai.crystal.common.response.Result
import org.springframework.web.bind.annotation.RequestParam

/**
 * @author lovelycat
 * @since 2025-02-26 01:03
 * @version 1.0
 */
interface INodeController {
    fun nodeRegister(
        @RequestParam("host")
        nodeHost: String,
        @RequestParam("port")
        nodePort: Int,
        @RequestParam("ssl", defaultValue = "0", required = false)
        ssl: Boolean
    ): Result<*>

    fun nodeUnregister(
        @RequestParam("host")
        nodeHost: String,
        @RequestParam("port")
        nodePort: Int
    ): Result<*>

    fun nodeCheck(
        @RequestParam("uuid")
        uuid: String
    ): Result<*>
}