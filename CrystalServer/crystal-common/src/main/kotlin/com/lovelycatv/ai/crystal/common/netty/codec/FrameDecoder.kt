package com.lovelycatv.ai.crystal.common.netty.codec

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.LengthFieldBasedFrameDecoder

/**
 * @author lovelycat
 * @since 2025-02-16 18:37
 * @version 1.0
 *
 * @constructor
 * FrameDecoder for netty
 *
 * @param maxFrameLength Maximum length of data
 * @param lengthFieldLength The number of bytes used to reveal the length of data
 * @param includingFieldLength Including the lengthFieldLength. If only the message body needs to be received, set it to false
 */
class FrameDecoder(
    maxFrameLength: Int = 4096,
    lengthFieldLength: Int = 2,
    includingFieldLength: Boolean = false
) : LengthFieldBasedFrameDecoder(
     maxFrameLength,
    0,
    lengthFieldLength,
    0,
    if (includingFieldLength) 0 else lengthFieldLength
) {
    override fun decode(ctx: ChannelHandlerContext?, `in`: ByteBuf?): Any {
        return super.decode(ctx, `in`)
    }
}