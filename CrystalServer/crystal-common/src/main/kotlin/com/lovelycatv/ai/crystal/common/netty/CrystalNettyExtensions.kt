package com.lovelycatv.ai.crystal.common.netty

import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.util.toJSONString
import io.netty.channel.Channel
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * @author lovelycat
 * @since 2025-02-27 00:20
 * @version 1.0
 */
class CrystalNettyExtensions private constructor()

data class NettyMessageSendResult(
    val reason: Reason,
    val success: Boolean,
    val cause: Throwable? = null,
) {
    enum class Reason {
        SUCCESS,
        FAILED,
        NULL_CHANNEL,
        EXCEPTION
    }
}

suspend fun Channel?.sendMessage(message: MessageChain): NettyMessageSendResult {
    return suspendCancellableCoroutine { continuation ->
        if (this != null) {
            this.writeAndFlush(message).addListener {
                try {
                    if (it.isSuccess) {
                        continuation.resume(NettyMessageSendResult(NettyMessageSendResult.Reason.SUCCESS, true, null))
                    } else {
                        continuation.resume(NettyMessageSendResult(NettyMessageSendResult.Reason.FAILED, false, it.cause()))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    continuation.resume(NettyMessageSendResult(NettyMessageSendResult.Reason.EXCEPTION, false, e))
                }
            }
        } else {
            continuation.resume(NettyMessageSendResult(NettyMessageSendResult.Reason.NULL_CHANNEL, false, null))
        }
    }
}

suspend fun Channel?.sendMessage(messageBuilder: MessageChain.Builder.() -> Unit): NettyMessageSendResult {
    val builder = MessageChain.Builder()
    messageBuilder.invoke(builder)
    return sendMessage(builder.build())
}