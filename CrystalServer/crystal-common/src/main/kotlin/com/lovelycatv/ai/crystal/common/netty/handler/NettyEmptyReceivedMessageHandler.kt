package com.lovelycatv.ai.crystal.common.netty.handler

import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.util.logger
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

/**
 * @author lovelycat
 * @since 2025-02-17 23:03
 * @version 1.0
 */
class NettyEmptyReceivedMessageHandler : ChannelInboundHandlerAdapter() {
    private val log = this.logger()

    override fun channelRead(ctx: ChannelHandlerContext, msg: Any?) {
        if (msg == null) {
            log.warn("Received null message. Sender: [{}]", ctx.channel().remoteAddress().toString())
        } else if (msg is MessageChain) {
            if (msg.isEmpty()) {
                log.warn(
                    "Received empty message. Sender: [{}], SessionId: [{}], StreamId: [{}]",
                    ctx.channel().remoteAddress().toString(),
                    msg.sessionId,
                    msg.streamId
                )
            } else {
                // Reorder the messages in the chain
                ctx.fireChannelRead(msg.copy(messages = msg.reorderMessages()))
            }
        } else {
            ctx.fireChannelRead(msg)
        }
    }
}