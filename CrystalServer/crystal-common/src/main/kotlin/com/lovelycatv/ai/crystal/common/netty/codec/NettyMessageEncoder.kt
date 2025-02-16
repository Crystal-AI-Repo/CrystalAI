package com.lovelycatv.ai.crystal.common.netty.codec

import com.lovelycatv.ai.crystal.common.util.toJSONString
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.MessageToMessageEncoder

/**
 * @author lovelycat
 * @since 2025-02-16 19:11
 * @version 1.0
 */
open class NettyMessageEncoder<T>(
    val encode: (T) -> String = { it.toJSONString() }
) : MessageToMessageEncoder<T>() {
    /**
     * Encode from one message to an other. This method will be called for each written message that can be handled
     * by this encoder.
     *
     * @param ctx           the [ChannelHandlerContext] which this [MessageToMessageEncoder] belongs to
     * @param msg           the message to encode to an other one
     * @param out           the [List] into which the encoded msg should be added
     * needs to do some kind of aggregation
     * @throws Exception    is thrown if an error occurs
     */
    override fun encode(ctx: ChannelHandlerContext?, msg: T, out: MutableList<Any>) {
        val encodedString: String = encode.invoke(msg)
        val encodedBytes: ByteArray = encodedString.toByteArray()

        val buffer = Unpooled.buffer()
        // Write the size as an integer
        buffer.writeInt(encodedBytes.size)
        // Write the actual data
        buffer.writeBytes(encodedBytes)

        out.add(buffer)
    }
}