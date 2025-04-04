package com.lovelycatv.crystal.rag.rpc

import com.lovelycatv.ai.crystal.common.client.IFeignClient
import com.lovelycatv.ai.crystal.common.response.Result
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

/**
 * @author lovelycat
 * @since 2025-03-29 03:16
 * @version 1.0
 */
interface NodeAuthClient : IFeignClient {
    @PostMapping("/api/v1/auth/login")
    fun loginWithSecretKey(@RequestParam("secretKey") secretKey: String): Result<String?>
}