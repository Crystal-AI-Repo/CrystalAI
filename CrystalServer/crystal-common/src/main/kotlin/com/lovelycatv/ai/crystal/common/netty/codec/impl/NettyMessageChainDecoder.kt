package com.lovelycatv.ai.crystal.common.netty.codec.impl

import com.fasterxml.jackson.databind.ObjectMapper
import com.lovelycatv.ai.crystal.common.netty.codec.NettyMessageDecoder
import com.lovelycatv.ai.crystal.common.data.message.MessageChain
import com.lovelycatv.ai.crystal.common.util.toExplicitObject
import com.lovelycatv.ai.crystal.common.util.toJSONString

/**
 * @author lovelycat
 * @since 2025-02-16 19:35
 * @version 1.0
 */
class NettyMessageChainDecoder(mapper: ObjectMapper = ObjectMapper()) : NettyMessageDecoder<MessageChain>(
    decoder = { it.toExplicitObject(mapper) }
)