package com.lovelycatv.ai.crystal.dispatcher.controller.v1

import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Dispatcher.WebManagerController.LIST_NODES
import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Dispatcher.WebManagerController.MAPPING
import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Dispatcher.WebManagerController.TEST_SEND_ONE_TIME_OLLAMA_CHAT
import com.lovelycatv.ai.crystal.common.GlobalConstants.ApiVersionControl.API_PREFIX_VERSION_1
import com.lovelycatv.ai.crystal.common.data.message.chat.options.DeepSeekChatOptions
import com.lovelycatv.ai.crystal.common.data.message.chat.options.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.chat.PromptMessage
import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.dispatcher.data.node.OneTimeChatRequestResult
import com.lovelycatv.ai.crystal.dispatcher.data.node.RegisteredNode
import com.lovelycatv.ai.crystal.dispatcher.service.NodeManagerService
import com.lovelycatv.ai.crystal.dispatcher.service.DefaultChatService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author lovelycat
 * @since 2025-02-26 16:11
 * @version 1.0
 */
@RestController
@RequestMapping(API_PREFIX_VERSION_1 + MAPPING)
class WebManagerControllerV1(
    private val nodeManagerService: NodeManagerService,
    private val chatService: DefaultChatService
) : IWebManagerControllerV1 {
    @GetMapping(TEST_SEND_ONE_TIME_OLLAMA_CHAT)
    override suspend fun testSendOneTimeChatTask(
        model: String,
        message: String,
        waitForResult: Boolean,
        timeout: Long
    ): Result<OneTimeChatRequestResult> {
        val (platformName, modelName) = model.split("@")

        val result = chatService.sendOneTimeChatTask(
            options = when (platformName.lowercase()) {
                "ollama" -> OllamaChatOptions(modelName = modelName, temperature = null)
                "deepseek" -> DeepSeekChatOptions(modelName = modelName, temperature = null)
                else -> throw IllegalStateException("$platformName is not supported yet.")
            },
            messages = listOf(
                PromptMessage(
                    role = PromptMessage.Role.USER,
                    message = listOf(PromptMessage.Content.fromString(message)),
                )
            ),
            ignoreResult = !waitForResult,
            timeout = timeout
        )

        return if (result.isRequestSent) {
            Result.success("", result)
        } else {
            Result.badRequest("Could not send request to node", result)
        }
    }

    /**
     * List all registered nodes
     *
     * @return All [RegisteredNode]
     */
    @GetMapping(LIST_NODES)
    override fun listAllNodes(): Result<List<RegisteredNode>> {
        return Result.success("", nodeManagerService.listAllNodes())
    }
}