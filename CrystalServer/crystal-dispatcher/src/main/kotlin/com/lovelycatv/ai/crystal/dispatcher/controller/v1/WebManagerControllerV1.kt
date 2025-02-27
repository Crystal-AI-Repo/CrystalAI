package com.lovelycatv.ai.crystal.dispatcher.controller.v1

import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Dispatcher.WebManagerController.LIST_NODES
import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Dispatcher.WebManagerController.MAPPING
import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Dispatcher.WebManagerController.TEST_SEND_ONE_TIME_OLLAMA_CHAT
import com.lovelycatv.ai.crystal.common.GlobalConstants.ApiVersionControl.API_PREFIX_VERSION_1
import com.lovelycatv.ai.crystal.common.data.message.chat.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.chat.PromptMessage
import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.dispatcher.data.node.OneTimeChatRequestResult
import com.lovelycatv.ai.crystal.dispatcher.data.node.RegisteredNode
import com.lovelycatv.ai.crystal.dispatcher.service.NodeManagerService
import com.lovelycatv.ai.crystal.dispatcher.service.OllamaChatService
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
    private val ollamaChatService: OllamaChatService
) : IWebManagerControllerV1 {
    @GetMapping(TEST_SEND_ONE_TIME_OLLAMA_CHAT)
    override suspend fun testSendOneTimeChatTask(
        model: String,
        message: String,
        waitForResult: Boolean,
        timeout: Long
    ): Result<OneTimeChatRequestResult> {
        val result = ollamaChatService.sendOneTimeChatTask(
            options = OllamaChatOptions(modelName = model, temperature = null),
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
            Result.badRequest("", result)
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