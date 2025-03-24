package com.lovelycatv.ai.crystal.common

object GlobalConstants {
    object ApiVersionControl {
        const val API_PREFIX_VERSION_1 = "/api/v1"
        const val API_PREFIX_VERSION_2 = "/api/v2"

        const val API_PREFIX_FOR_DISPATCHER = API_PREFIX_VERSION_1
        const val API_PREFIX_FOR_NODE = API_PREFIX_VERSION_1
    }

    /**
     * All apis in this project should be declared here.
     */
    object Api {
        object Dispatcher {
            object NodeController {
                const val MAPPING = "/node"
                const val NODE_REGISTER = "/register"
                const val NODE_UNREGISTER = "/unregister"
                const val NODE_CHECK = "/check"
            }

            object OpenApiController {
                const val CHAT_COMPLETION = "/v1/chat/completions"
                const val EMBEDDINGS = "/v1/embeddings"
            }

            object WebManagerController {
                const val MAPPING = "/web-manager"
                const val LIST_NODES = "/listNodes"
                const val GET_NODE_BY_ID = "/getNodeById"

                // Test functions
                const val TEST_SEND_ONE_TIME_CHAT_REQUEST = "/test/sendOneTimeChatRequest"
                const val TEST_SEND_STREAM_CHAT_REQUEST = "/test/sendStreamChatRequest"
                const val TEST_SEND_ONE_TIME_EMBEDDING = "/test/sendOneTimeEmbeddingRequest"
            }
        }

        object Node {
            object ProbeController {
                const val MAPPING = "/probe"
                const val NODE_INFO = "/info"
                const val NODE_TASKS = "/tasks"
                const val NODE_AVAILABLE = "/available"
            }
        }
    }

    object Flags {
        const val MESSAGE_FINISHED = "EOF"
        const val STREAMING_MESSAGE_FINISHED = "STREAM_EOF"

        fun isFinishedFlag(flag: String?) = flag == MESSAGE_FINISHED || flag == STREAMING_MESSAGE_FINISHED
    }

}