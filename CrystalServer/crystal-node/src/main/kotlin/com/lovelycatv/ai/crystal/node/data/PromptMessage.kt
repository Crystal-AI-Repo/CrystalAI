package com.lovelycatv.ai.crystal.node.data

import org.springframework.core.io.ClassPathResource
import org.springframework.core.io.Resource

/**
 * @author lovelycat
 * @since 2025-02-15 16:35
 * @version 1.0
 */
data class PromptMessage(
    val role: Role,
    val message: List<Content>,
    val messageSeparator: CharSequence = " "
) {
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

    data class Content(
        val type: Type,
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