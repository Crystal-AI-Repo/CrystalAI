package com.lovelycatv.ai.crystal.node.config

data class NodeOllamaConfiguration(
    val host: String = "localhost",
    val port: Int = 11434
) {
    fun getBaseUrl(ssl: Boolean): String {
        return "${if (ssl) "https" else "http"}://$host:$port"
    }
}