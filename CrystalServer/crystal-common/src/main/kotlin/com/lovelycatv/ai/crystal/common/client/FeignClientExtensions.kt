package com.lovelycatv.ai.crystal.common.client

import com.fasterxml.jackson.databind.ObjectMapper
import feign.Feign
import feign.codec.Decoder
import feign.codec.Encoder

/**
 * @author lovelycat
 * @since 2025-02-08 01:23
 * @version 1.0
 */
class FeignClientExtensions private constructor()

inline fun <reified T> getFeignClient(
    url: String,
    encoderObjectMapper: ObjectMapper = ObjectMapper(),
    decoderObjectMapper: ObjectMapper = ObjectMapper(),
    feignEncoder: Encoder = JacksonFeignEncoder(encoderObjectMapper),
    feignDecoder: Decoder = JacksonFeignDecoder(decoderObjectMapper),
    processor: Feign.Builder.() -> Feign.Builder = { this }
): T {
    return processor(
        Feign.builder()
            .encoder(feignEncoder)
            .decoder(feignDecoder)
    ).target(T::class.java, url)
}