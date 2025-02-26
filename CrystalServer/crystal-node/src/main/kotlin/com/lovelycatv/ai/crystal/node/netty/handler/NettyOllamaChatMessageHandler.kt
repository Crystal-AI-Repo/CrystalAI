package com.lovelycatv.ai.crystal.node.netty.handler

import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.data.message.MessageChainBuilder
import com.lovelycatv.ai.crystal.common.data.message.chat.OllamaChatOptions
import com.lovelycatv.ai.crystal.common.data.message.chat.OllamaChatResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.chat.PromptMessage
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.node.config.NodeConfiguration
import com.lovelycatv.ai.crystal.node.data.toOllamaTask
import com.lovelycatv.ai.crystal.node.queue.OllamaTaskQueue
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 * @author lovelycat
 * @since 2025-02-26 21:20
 * @version 1.0
 */
class NettyOllamaChatMessageHandler(
    private val ollamaTaskQueue: OllamaTaskQueue,
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
        if (msg.messages[0] is OllamaChatOptions) {
            if (msg.messages.size >= 2) {
                if (msg.messages[1] is PromptMessage) {
                    submitTask(msg)
                    ctx.fireChannelRead(msg.copy(messages = msg.messages.filter { it !is OllamaChatOptions && it !is PromptMessage }))
                } else {
                    // Message is invalid
                    log.warn("Invalid message received: " +
                        "trying to call Ollama Chat but provided invalid messages " +
                        "that Prompt Message not found at the following location after Ollama Chat Options")
                    ctx.writeAndFlush(MessageChainBuilder {
                        this.addMessage(
                            OllamaChatResponseMessage.failed("Ollama Chat Options received but the following message is not Prompt Message")
                        )
                    })
                }
            } else {
                // Message is invalid
                log.warn("Invalid message received: trying to call Ollama Chat but provided invalid messages that Prompt Message not found")
                ctx.writeAndFlush(MessageChainBuilder {
                    this.addMessage(
                        OllamaChatResponseMessage(
                            success = false,
                            message = "Ollama Chat Options received but Prompt Message not found"
                        )
                    )
                })
            }
        } else if (msg.messages[0] is PromptMessage) {
            // No Ollama Chat Options, using default settings
            submitTask(msg)
            ctx.fireChannelRead(msg.copy(messages = msg.messages.filter { it !is OllamaChatOptions && it !is PromptMessage }))
        } else {
            ctx.fireChannelRead(msg)
        }
    }

    private fun submitTask(messageChain: MessageChain) {
        val task = messageChain.toOllamaTask(nodeConfiguration.ollama.maxExecutionTimeMillis)
        ollamaTaskQueue.submitTask(task)
    }
}