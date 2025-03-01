package com.lovelycatv.ai.crystal.node.netty.handler

import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.data.message.MessageChainBuilder
import com.lovelycatv.ai.crystal.common.data.message.model.chat.AbstractChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.DeepSeekChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.model.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.PromptMessage
import com.lovelycatv.ai.crystal.common.data.message.transferToNextPipeLineIfNotEmpty
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.node.config.NodeConfiguration
import com.lovelycatv.ai.crystal.node.data.toDeepSeekTask
import com.lovelycatv.ai.crystal.node.data.toOllamaTask
import com.lovelycatv.ai.crystal.node.exception.UnsupportedChatOptionsType
import com.lovelycatv.ai.crystal.node.queue.DefaultChatTaskQueue
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 * @author lovelycat
 * @since 2025-02-26 21:20
 * @version 1.0
 */
class NettyChatMessageHandler(
    private val chatTaskQueue: DefaultChatTaskQueue,
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
        if (msg.messages[0] is AbstractChatOptions) {
            val opts = msg.messages[0] as AbstractChatOptions

            if (msg.messages.size >= 2) {
                if (msg.messages[1] is PromptMessage) {
                    submitTask(opts, msg)

                    msg.dropMessages {
                        it is AbstractChatOptions || it is PromptMessage
                    }.transferToNextPipeLineIfNotEmpty(ctx)
                } else {
                    // Message is invalid
                    log.warn("Invalid message received: " +
                        "trying to call Chat but provided invalid messages " +
                        "that Prompt Message not found at the following location after Ollama Chat Options")
                    ctx.writeAndFlush(MessageChainBuilder {
                        this.addMessage(
                            ChatResponseMessage.failed("Chat Options received but the following message is not Prompt Message")
                        )
                    })
                }
            } else {
                // Message is invalid
                log.warn("Invalid message received: trying to call Chat but provided invalid messages that Prompt Message not found")
                ctx.writeAndFlush(MessageChainBuilder {
                    this.addMessage(
                        ChatResponseMessage(
                            success = false,
                            message = "Chat Options received but Prompt Message not found"
                        )
                    )
                })
            }
        } else if (msg.messages[0] is PromptMessage) {
            // No Chat Options, using default settings
            submitTask(null, msg)

            msg.dropMessages {
                it is AbstractChatOptions || it is PromptMessage
            }.transferToNextPipeLineIfNotEmpty(ctx)
        } else {
            ctx.fireChannelRead(msg)
        }
    }

    private fun submitTask(options: AbstractChatOptions?, messageChain: MessageChain) {
        val task = when (options) {
            is OllamaChatOptions, null -> messageChain.toOllamaTask(nodeConfiguration.ollama.maxExecutionTimeMillis)
            is DeepSeekChatOptions -> messageChain.toDeepSeekTask(nodeConfiguration.deepseek.maxExecutionTimeMillis)
            else -> throw UnsupportedChatOptionsType(options::class)
        }
        chatTaskQueue.submitTask(task)
    }
}