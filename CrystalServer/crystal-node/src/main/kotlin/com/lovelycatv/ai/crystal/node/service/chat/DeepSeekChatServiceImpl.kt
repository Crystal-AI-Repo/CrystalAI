package com.lovelycatv.ai.crystal.node.service.chat

import com.lovelycatv.ai.crystal.common.data.message.model.chat.DeepSeekChatOptions
import com.lovelycatv.ai.crystal.node.config.NodeConfiguration
import org.springframework.ai.openai.OpenAiChatModel
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
            this.translate(DeepSeekChatOptions(
                modelName = nodeConfiguration.deepseek.defaultModel,
                temperature = nodeConfiguration.deepseek.defaultTemperature
            ))
        )
    }
}