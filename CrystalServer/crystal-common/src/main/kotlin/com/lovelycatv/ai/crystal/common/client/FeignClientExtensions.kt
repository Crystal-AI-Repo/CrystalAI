package com.lovelycatv.ai.crystal.common.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.lovelycatv.ai.crystal.common.client.codec.JacksonFeignDecoder
import com.lovelycatv.ai.crystal.common.client.codec.JacksonFeignEncoder
import com.lovelycatv.ai.crystal.common.util.catchException
import feign.Client
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
    return getFeignClient(T::class.java, url, encoderObjectMapper, decoderObjectMapper, feignEncoder, feignDecoder, processor)
}

inline fun <T> getFeignClient(
    clazz: Class<T>,
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
    ).target(clazz, url)
}

fun <C: IFeignClient, R> C.safeRequest(
    onException: ((Exception) -> Unit)? = null,
    returnOnException: R? = null,
    fx: C.() -> R
): R? {
    return catchException(
        printStackTrace = false,
        onException = { onException?.invoke(it) },
        returnOnException = returnOnException
    ) { fx.invoke(this) }
}