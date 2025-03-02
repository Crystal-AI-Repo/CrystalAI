package com.lovelycatv.ai.crystal.node.netty.handler

import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.data.message.MessageChainBuilder
import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.DeepSeekChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.PromptMessage
import com.lovelycatv.ai.crystal.common.data.message.model.AbstractModelOptions
import com.lovelycatv.ai.crystal.common.data.message.model.ModelResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.AbstractEmbeddingOptions
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.EmbeddingResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.OllamaEmbeddingOptions
import com.lovelycatv.ai.crystal.common.data.message.transferToNextPipeLineIfNotEmpty
import com.lovelycatv.ai.crystal.common.util.implies
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.node.config.NodeConfiguration
import com.lovelycatv.ai.crystal.node.data.*
import com.lovelycatv.ai.crystal.node.exception.UnsupportedModelOptionsType
import com.lovelycatv.ai.crystal.node.exception.UnsupportedTaskTypeException
import com.lovelycatv.ai.crystal.node.queue.TaskQueue
import com.lovelycatv.ai.crystal.node.task.AbstractTask
import com.lovelycatv.ai.crystal.node.task.toDeepSeekTask
import com.lovelycatv.ai.crystal.node.task.toOllamaEmbeddingTask
import com.lovelycatv.ai.crystal.node.task.toOllamaTask
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 * @author lovelycat
 * @since 2025-02-26 21:20
 * @version 1.0
 */
class NettyChatMessageHandler(
    private val taskQueue: TaskQueue<AbstractTask>,
    private val nodeConfiguration: NodeConfiguration
) : SimpleChannelInboundHandler<MessageChain>() {
    private val log = this.logger()

    /**
     * Is called for each message of type [I].
     *
     * @param ctx           the [ChannelHandlerContext] which this [SimpleChannelInboundHandler]
     * belongs to
     * @param msg           the message to handle
     * @throws Exception    is thrown if an error occurred
     */
    override fun channelRead0(ctx: ChannelHandlerContext, msg: MessageChain) {
        if (msg.messages[0] is AbstractModelOptions) {
            val opts = msg.messages[0] as AbstractModelOptions

            if (msg.messages.size >= 2) {
                if (msg.messages[1] is PromptMessage) {

                    if (opts is AbstractChatOptions) {
                        submitChatTask(opts, msg)
                    } else if (opts is AbstractEmbeddingOptions) {
                        submitEmbeddingTask(opts, msg)
                    } else {
                        throw UnsupportedTaskTypeException(opts::class.qualifiedName)
                    }

                    msg.dropMessages {
                        it is AbstractModelOptions || it is PromptMessage
                    }.transferToNextPipeLineIfNotEmpty(ctx)
                } else {
                    // Message is invalid
                    log.warn("Invalid message received: " +
                        "trying to call Model but provided invalid messages " +
                        "that Prompt Message not found at the following location after Model Options")

                    ctx.writeAndFlush(MessageChainBuilder {
                        this.addMessage(
                            buildFailedResponseMessage(opts, "Chat Options received but the following message is not Prompt Message")
                        )
                    })
                }
            } else {
                // Message is invalid
                log.warn("Invalid message received: trying to call Model but provided invalid messages that Prompt Message not found")

                ctx.writeAndFlush(MessageChainBuilder {
                    this.addMessage(
                        buildFailedResponseMessage(opts, "Chat Options received but Prompt Message not found")
                    )
                })
            }
        }else {
            ctx.fireChannelRead(msg)
        }
    }

    private inline fun <reified T: AbstractModelOptions, reified R: ModelResponseMessage> buildFailedResponseMessage(
        opts: T,
        message: String
    ): R {
        val a = ChatResponseMessage.failed(message)
        val b = EmbeddingResponseMessage.failed(message)

        return if (opts.implies<T, R>(a)) {
            a as R
        } else if (opts.implies<T, R>(b)) {
            b as R
        } else {
            throw UnsupportedTaskTypeException(opts::class.qualifiedName)
        }
    }

    private fun submitEmbeddingTask(options: AbstractEmbeddingOptions?, messageChain: MessageChain) {
        val task = when (options) {
            is OllamaEmbeddingOptions, null -> messageChain.toOllamaEmbeddingTask(nodeConfiguration.ollama.maxExecutionTimeMillis)
            else -> throw UnsupportedModelOptionsType(options::class)
        }
        taskQueue.submitTask(task)
    }

    private fun submitChatTask(options: AbstractChatOptions?, messageChain: MessageChain) {
        val task = when (options) {
            is OllamaChatOptions, null -> messageChain.toOllamaTask(nodeConfiguration.ollama.maxExecutionTimeMillis)
            is DeepSeekChatOptions -> messageChain.toDeepSeekTask(nodeConfiguration.deepseek.maxExecutionTimeMillis)
            else -> throw UnsupportedModelOptionsType(options::class)
        }
        taskQueue.submitTask(task)
    }
}