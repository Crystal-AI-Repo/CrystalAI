package com.lovelycatv.ai.crystal.dispatcher.controller.v1

import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Dispatcher.WebManagerController.GET_NODE_BY_ID
import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Dispatcher.WebManagerController.LIST_NODES
import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Dispatcher.WebManagerController.MAPPING
import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Dispatcher.WebManagerController.TEST_SEND_ONE_TIME_EMBEDDING
import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Dispatcher.WebManagerController.TEST_SEND_ONE_TIME_CHAT_REQUEST
import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Dispatcher.WebManagerController.TEST_SEND_STREAM_CHAT_REQUEST
import com.lovelycatv.ai.crystal.common.GlobalConstants.ApiVersionControl.API_PREFIX_VERSION_1
import com.lovelycatv.ai.crystal.common.data.message.model.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.chat.DeepSeekChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.PromptMessage
import com.lovelycatv.ai.crystal.common.data.message.model.ModelResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.AbstractEmbeddingOptions
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.OllamaEmbeddingOptions
import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.common.util.toJSONString
import com.lovelycatv.ai.crystal.dispatcher.data.node.ChatRequestSessionContainer
import com.lovelycatv.ai.crystal.dispatcher.response.OneTimeChatRequestResult
import com.lovelycatv.ai.crystal.dispatcher.data.node.RegisteredNode
import com.lovelycatv.ai.crystal.dispatcher.response.OneTimeEmbeddingRequestResult
import com.lovelycatv.ai.crystal.dispatcher.response.StreamChatRequestResult
import com.lovelycatv.ai.crystal.dispatcher.service.NodeManagerService
import com.lovelycatv.ai.crystal.dispatcher.service.DefaultChatService
import com.lovelycatv.ai.crystal.dispatcher.service.DefaultEmbeddingService
import com.lovelycatv.ai.crystal.dispatcher.task.manager.ListenableTaskManager
import com.lovelycatv.ai.crystal.dispatcher.task.manager.TaskManager
import kotlinx.coroutines.*
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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
    private val chatService: DefaultChatService,
    private val embeddingService: DefaultEmbeddingService,
    private val taskManager: TaskManager
) : IWebManagerControllerV1 {
    @GetMapping(TEST_SEND_ONE_TIME_CHAT_REQUEST)
    override suspend fun testSendOneTimeChatTask(
        model: String,
        message: String,
        waitForResult: Boolean,
        timeout: Long
    ): Result<OneTimeChatRequestResult> {
        val result = chatService.sendOneTimeChatTask(
            options = buildChatOptions(model),
            messages = buildMessages(message),
            ignoreResult = !waitForResult,
            timeout = timeout
        )

        return if (result.isRequestSent) {
            Result.success("", result)
        } else {
            Result.badRequest("Could not send request to node", result)
        }
    }

    @GetMapping(TEST_SEND_STREAM_CHAT_REQUEST)
    override suspend fun testSendStreamChatTask(model: String, message: String, timeout: Long): Result<StreamChatRequestResult> {
        val result = chatService.sendStreamChatTask(
            options = buildChatOptions(model),
            messages = buildMessages(message),
            timeout = timeout
        )

        return if (result.isRequestSent) {
            // Test listener
            taskManager.subscribe(result.sessionId!!, object : ListenableTaskManager.SimpleSubscriber {
                override fun onReceived(container: ChatRequestSessionContainer, message: ModelResponseMessage) {
                    print((message as ChatResponseMessage).content)
                }

                override fun onFinished(container: ChatRequestSessionContainer) {
                    val last = container.recentReceived()!! as ChatResponseMessage
                    println("Tokens: ${last.totalTokens}, Generated Tokens: ${last.generatedTokens}")
                    println("Session [${container.originalMessageChain.sessionId}] completed!")
                }

                override fun onFailed(container: ChatRequestSessionContainer?, failedMessage: ModelResponseMessage?) {
                    if (container != null) {
                        println("Session [${container.originalMessageChain.sessionId}] received failed message: ${failedMessage.toJSONString()}")
                    } else {
                        println("Session [${result.sessionId}] has been removed but dispatcher still received message")
                    }
                }

            })

            Result.success("", result)
        } else {
            Result.badRequest("Could not send request to node", result)
        }
    }

    @GetMapping(TEST_SEND_ONE_TIME_EMBEDDING)
    override suspend fun testSendOneTimeEmbeddingTask(
        model: String,
        message: String,
        waitForResult: Boolean,
        timeout: Long
    ): Result<OneTimeEmbeddingRequestResult> {
        val result = embeddingService.sendOneTimeEmbeddingTask(
            options = buildEmbeddingOptions(model),
            messages = buildMessages(message),
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

    /**
     * Get registered node by id
     *
     * @param nodeId nodeId
     * @return [RegisteredNode]?
     */
    @GetMapping(GET_NODE_BY_ID)
    override fun getNodeById(@RequestParam("nodeId") nodeId: String): Result<RegisteredNode?> {
        return Result.success("", nodeManagerService.listAllNodes().find { it.nodeId == nodeId })
    }

    private fun buildChatOptions(model: String): AbstractChatOptions {
        val (platformName, modelName) = model.split("@")

        return when (platformName.lowercase()) {
            "ollama" -> OllamaChatOptions(modelName = modelName, temperature = null)
            "deepseek" -> DeepSeekChatOptions(modelName = modelName, temperature = null)
            else -> throw IllegalStateException("$platformName is not supported yet.")
        }
    }

    private fun buildEmbeddingOptions(model: String): AbstractEmbeddingOptions {
        val (platformName, modelName) = model.split("@")

        return when (platformName.lowercase()) {
            "ollama" -> OllamaEmbeddingOptions(modelName = modelName)
            else -> throw IllegalStateException("$platformName is not supported yet.")
        }
    }

    private fun buildMessages(message: String): List<PromptMessage> {
        return listOf(
            PromptMessage(
                role = PromptMessage.Role.USER,
                message = listOf(PromptMessage.Content.fromString(message)),
            )
        )
    }
}