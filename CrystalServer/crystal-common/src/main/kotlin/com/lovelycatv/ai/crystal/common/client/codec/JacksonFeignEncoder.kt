package com.lovelycatv.ai.crystal.common.client.codec

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import feign.RequestTemplate
import feign.codec.EncodeException
import feign.codec.Encoder
import java.lang.reflect.Type

class JacksonFeignEncoder(private val objectMapper: ObjectMapper) : Encoder {
    @Throws(EncodeException::class)
    override fun encode(obj: Any, bodyType: Type, template: RequestTemplate) {
        try {
            val json = objectMapper.writeValueAsString(obj)
            template.body(json)
        } catch (e: JsonProcessingException) {
            throw EncodeException("Failed to encode object", e)
        }
    }
}