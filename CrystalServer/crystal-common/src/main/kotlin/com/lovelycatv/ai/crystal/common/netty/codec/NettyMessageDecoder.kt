package com.lovelycatv.ai.crystal.common.netty.codec

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageDecoder
import java.nio.charset.Charset

/**
 * @author lovelycat
 * @since 2025-02-16 19:14
 * @version 1.0
 */
open class NettyMessageDecoder<T : Any>(val decoder: (String) -> T) : ByteToMessageDecoder() {
    /**
     * Decode the from one [ByteBuf] to an other. This method will be called till either the input
     * [ByteBuf] has nothing to read when return from this method or till nothing was read from the input
     * [ByteBuf].
     *
     * @param ctx           the [ChannelHandlerContext] which this [ByteToMessageDecoder] belongs to
     * @param in            the [ByteBuf] from which to read data
     * @param out           the [List] to which decoded messages should be added
     * @throws Exception    is thrown if an error occurs
     */
    override fun decode(ctx: ChannelHandlerContext?, `in`: ByteBuf, out: MutableList<Any>) {
        val bytes = ByteArray(`in`.readableBytes())
        `in`.readBytes(bytes)
        val decodedString = String(bytes, Charset.defaultCharset())
        out.add(decoder.invoke(decodedString))
    }
}