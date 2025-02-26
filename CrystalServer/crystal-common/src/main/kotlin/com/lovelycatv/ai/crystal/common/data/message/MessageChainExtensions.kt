package com.lovelycatv.ai.crystal.common.data.message

import io.netty.channel.ChannelHandlerContext

/**
 * @author lovelycat
 * @since 2025-02-16 22:38
 * @version 1.0
 */
class MessageChainExtensions private constructor()

fun MessageChainBuilder(fx: MessageChain.Builder.() -> Unit): MessageChain {
    val builder = MessageChain.Builder()
    fx.invoke(builder)
    return builder.build()
}

fun MessageChain.transferToNextPipeLineIfNotEmpty(context: ChannelHandlerContext) {
    if (this.messages.isNotEmpty()) {
        context.fireChannelRead(this)
    }
}