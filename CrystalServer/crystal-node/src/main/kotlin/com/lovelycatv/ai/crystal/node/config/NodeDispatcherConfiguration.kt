package com.lovelycatv.ai.crystal.node.config

data class NodeDispatcherConfiguration(
    var host: String = "0.0.0.0",
    var port: Int = 5210
) {
    fun getBaseUrl(ssl: Boolean): String {
        return "${if (ssl) "https" else "http"}://$host:$port"
    }
}