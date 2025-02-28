package com.lovelycatv.ai.crystal.common.client

import com.lovelycatv.ai.crystal.common.response.deepseek.DeepSeekModelResults
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader

/**
 * @author lovelycat
 * @since 2025-02-28 18:09
 * @version 1.0
 */
@FeignClient("DeepSeekClient")
interface DeepSeekClient : IFeignClient {
    @GetMapping("/models")
    fun getModels(@RequestHeader("Authorization") token: String): DeepSeekModelResults
}