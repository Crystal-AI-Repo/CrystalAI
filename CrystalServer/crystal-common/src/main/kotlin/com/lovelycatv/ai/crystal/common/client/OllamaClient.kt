package com.lovelycatv.ai.crystal.common.client

import com.lovelycatv.ai.crystal.common.response.ollama.OllamaModelsResponse
import feign.RequestLine
import org.springframework.cloud.openfeign.FeignClient

@FeignClient("ollamaClient")
interface OllamaClient : IFeignClient {
    @RequestLine("GET /api/tags")
    fun getOllamaModels(): OllamaModelsResponse
}
