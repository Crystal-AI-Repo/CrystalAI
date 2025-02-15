package com.lovelycatv.ai.crystal.common.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.lovelycatv.ai.crystal.common.client.codec.JacksonFeignDecoder
import com.lovelycatv.ai.crystal.common.client.codec.JacksonFeignEncoder
import feign.Feign
import feign.codec.Decoder
import feign.codec.Encoder
import org.springframework.cloud.openfeign.support.SpringMvcContract

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
            .contract(SpringMvcContract())
    ).target(T::class.java, url)
}