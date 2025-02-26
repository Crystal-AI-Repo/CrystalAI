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

suspend fun Channel?.sendMessage(message: MessageChain): Boolean {
    return suspendCancellableCoroutine { continuation ->
        if (this != null) {
            this.writeAndFlush(message).addListener {
                try {
                    if (it.isSuccess) {
                        continuation.resume(true)
                    } else {
                        continuation.resume(false)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    continuation.resume(false)
                }
            }
        } else {
            continuation.resume(false)
        }
    }
}

suspend fun Channel?.sendMessage(messageBuilder: MessageChain.Builder.() -> Unit): Boolean {
    val builder = MessageChain.Builder()
    messageBuilder.invoke(builder)
    return sendMessage(builder.build())
}