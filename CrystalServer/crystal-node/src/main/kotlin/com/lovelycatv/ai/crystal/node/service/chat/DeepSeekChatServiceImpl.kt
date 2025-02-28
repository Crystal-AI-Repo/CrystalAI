package com.lovelycatv.ai.crystal.node.service.chat

import com.lovelycatv.ai.crystal.common.data.message.chat.options.DeepSeekChatOptions
import com.lovelycatv.ai.crystal.node.config.NodeConfiguration
import org.springframework.ai.openai.OpenAiChatModel
import org.springframework.ai.openai.OpenAiChatOptions
import org.springframework.ai.openai.api.OpenAiApi
import org.springframework.stereotype.Service

/**
 * @author lovelycat
 * @since 2025-02-28 15:18
 * @version 1.0
 */
@Service
class DeepSeekChatServiceImpl(
    private val nodeConfiguration: NodeConfiguration
) : DeepSeekChatService() {
    override fun buildChatModel(): OpenAiChatModel {
        return OpenAiChatModel(
            OpenAiApi(
                nodeConfiguration.deepseek.baseUrl,
                nodeConfiguration.deepseek.apiKey
            ),
            OpenAiChatOptions.builder().apply {
                nodeConfiguration.deepseek.defaultModel?.let {
                    this.model(it)
                }
                nodeConfiguration.deepseek.defaultTemperature.let {
                    this.temperature(it)
                }
            }.build()
        )
    }

    override fun applyOptionsToSpringAIModelOptions(customOptions: DeepSeekChatOptions?): OpenAiChatOptions {
        return OpenAiChatOptions.builder().apply {
            customOptions?.modelName?.let {
                this.model(it)
            }
            customOptions?.temperature.let {
                this.temperature(it)
            }
        }.build()
    }
}