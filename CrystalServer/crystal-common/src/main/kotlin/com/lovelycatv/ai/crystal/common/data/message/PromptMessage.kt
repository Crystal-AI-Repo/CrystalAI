package com.lovelycatv.ai.crystal.common.data.message

import com.alibaba.fastjson2.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonTypeName
import org.springframework.core.io.Resource

/**
 * @author lovelycat
 * @since 2025-02-15 16:35
 * @version 1.0
 */
@JsonTypeName("PROMPT")
data class PromptMessage @JsonCreator constructor(
    @JSONField(name = "role")
    val role: Role,
    @JSONField(name = "message")
    val message: List<Content>,
    @JSONField(name = "messageSeparator")
    val messageSeparator: CharSequence = " "
) : AbstractMessage(Type.PROMPT) {
    class Builder {
        private var role: Role = Role.USER
        private var message: MutableList<Content> = mutableListOf()
        private var messageSeparator: CharSequence = " "

        fun fromUser(): Builder {
            this.role = Role.USER
            return this
        }

        fun fromAssistant(): Builder {
            this.role = Role.ASSISTANT
            return this
        }

        fun fromSystem(): Builder {
            this.role = Role.SYSTEM
            return this
        }

        fun addMessage(content: Content): Builder {
            this.message.add(content)
            return this
        }

        fun separatedBy(separator: CharSequence) {
            this.messageSeparator = separator
        }

        fun build(): PromptMessage {
            return PromptMessage(role = role, message = message, messageSeparator = messageSeparator)
        }
    }

    data class Content @JsonCreator constructor(
        @JSONField(name = "type")
        val type: Type,
        @JSONField(name = "content")
        val content: Any
    ) {
        companion object {
            fun <C: CharSequence> fromString(text: C): Content {
                return Content(type = Type.TEXT, content = text.toString())
            }

            fun <R: Resource> fromImage(resource: R): Content {
                return Content(type = Type.IMAGE, content = resource)
            }
        }

        enum class Type {
            TEXT,
            IMAGE
        }
    }
    enum class Role {
        USER,
        ASSISTANT,
        SYSTEM
    }
}