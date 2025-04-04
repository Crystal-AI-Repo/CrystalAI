package com.lovelycatv.crystal.rag.manager

import com.lovelycatv.ai.crystal.common.client.getFeignClient
import com.lovelycatv.ai.crystal.common.client.safeRequest
import com.lovelycatv.ai.crystal.common.data.MutableLiveData
import com.lovelycatv.crystal.rag.config.CrystalRAGConfig
import com.lovelycatv.crystal.rag.rpc.NodeAuthClient
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * @author lovelycat
 * @since 2025-03-29 03:17
 * @version 1.0
 */
@Component
@EnableScheduling
@ConditionalOnProperty(name = ["crystal.node.enabled"], havingValue = "true", matchIfMissing = false)
class NodeAccessKeyManager(
    private val crystalRAGConfig: CrystalRAGConfig
) {
    private var currentAccessKey = MutableLiveData("")

    fun getAccessKey(fetchNow: Boolean = false) = if (fetchNow) {
        this.updateKey()
        this.currentAccessKey.get()
    } else {
        this.currentAccessKey.get()
    }

    @Scheduled(cron = "* 0/30 * * * ?")
    fun updateKey() {
        val url = "${crystalRAGConfig.node.host}:${crystalRAGConfig.node.port}"
        val client = getFeignClient<NodeAuthClient>(url)
        val result = client.safeRequest { loginWithSecretKey(crystalRAGConfig.node.secretKey) }
        if (result?.isSuccessful() == true && result.data != null) {
            currentAccessKey.post(result.data!!)
        } else {
            throw IllegalStateException("Could not update access key of node: $url")
        }
    }
}