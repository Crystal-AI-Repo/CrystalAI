package com.lovelycatv.ai.crystal.common.client.codec

import com.fasterxml.jackson.databind.ObjectMapper
import feign.Response
import feign.codec.DecodeException
import feign.codec.Decoder
import java.io.InputStreamReader
import java.lang.reflect.Type

class JacksonFeignDecoder(private val objectMapper: ObjectMapper) : Decoder {

    @Throws(DecodeException::class)
    override fun decode(response: Response?, type: Type?): Any {
        if (response?.body() == null) {
            throw DecodeException(500, "Response body is null", response?.request())
        }

        val responseBody = InputStreamReader(response.body().asInputStream()).readText()
        // println("==> ${response.status()}: ${response.request()?.httpMethod()?.name} ${response.request()?.url()}")
        // println("Headers: ${response.request()?.headers()}")
        // println("==> $responseBody")
        try {
            return objectMapper.readValue(responseBody, objectMapper.constructType(type))
        } catch (e: Exception) {
            throw DecodeException(500, "Failed to decode response", response.request(), e)
        }
    }
}