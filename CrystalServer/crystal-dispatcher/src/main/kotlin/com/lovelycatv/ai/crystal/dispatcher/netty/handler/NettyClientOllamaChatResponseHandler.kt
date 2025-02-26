package com.lovelycatv.ai.crystal.dispatcher.netty.handler

import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.data.message.chat.OllamaChatResponseMessage
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.common.util.toJSONString
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler

/**
 * @author lovelycat
 * @since 2025-02-26 23:21
 * @version 1.0
 */
class NettyClientOllamaChatResponseHandler : SimpleChannelInboundHandler<MessageChain>() {
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
        val filteredMessage = msg.messages.filterIsInstance<OllamaChatResponseMessage>()
        if (filteredMessage.isEmpty()) {
            ctx.fireChannelRead(msg)
        } else {
            filteredMessage.forEach { _ ->
                // ...
            }
        }
    }
}