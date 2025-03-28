package com.lovelycatv.ai.crystal.node.controller

import com.lovelycatv.ai.crystal.common.response.Result

/**
 * @author lovelycat
 * @since 2025-03-28 16:17
 * @version 1.0
 */
interface IAuthController {
    fun login(secretKey: String): Result<*>
}