package com.lovelycatv.crystal.plugin.nvidia.node.service

import com.lovelycatv.crystal.plugin.nvidia.common.message.NvidiaChatOptions
import com.lovelycatv.crystal.plugin.nvidia.node.NodeMainClass
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.openai.api.OpenAiApi
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.reactive.function.client.WebClient


/**
 * @author lovelycat
 * @since 2025-03-02 16:49
 * @version 1.0
 */
@Service
open class NvidiaPlatformChatServiceImpl : NvidiaPlatformChatService() {
    override fun buildChatModel(): OpenAiChatModel {
        return OpenAiChatModel(
            OpenAiApi(
                "https://integrate.api.nvidia.com",
                NodeMainClass.plugin!!.getConfig().getString("apiKey"),
                RestClient.builder(),
                WebClient.builder()
            ),
            this.translate(
                NvidiaChatOptions(
                    modelName = "google/gemma2-27b-it"
                )
            )
        )
    }
}