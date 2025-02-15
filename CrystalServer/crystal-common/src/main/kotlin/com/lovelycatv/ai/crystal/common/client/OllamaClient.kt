package com.lovelycatv.ai.crystal.common.client

import com.lovelycatv.ai.crystal.common.response.ollama.OllamaModelsResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping

@FeignClient("ollamaClient")
interface OllamaClient : IFeignClient {
    @GetMapping("/api/tags")
    fun getOllamaModels(): OllamaModelsResponse
}
