package com.lovelycatv.ai.crystal.node.service

import com.lovelycatv.ai.crystal.node.config.NodeConfiguration
import com.lovelycatv.ai.crystal.node.utils.JwtUtils
import org.springframework.stereotype.Service

/**
 * @author lovelycat
 * @since 2025-03-28 16:20
 * @version 1.0
 */
@Service
class NodeAuthServiceImpl(
    private val nodeConfiguration: NodeConfiguration
) : NodeAuthService {
    /**
     * Login with secretKey. Returns null when failed.
     *
     * @param secretKey secretKey
     * @return AccessToken
     */
    override fun loginWithSecretKey(secretKey: String): String? {
        return if (nodeConfiguration.secretKey == secretKey) {
            JwtUtils.generateToken("anonymous", 24 * 3600 * 1000L, nodeConfiguration.secretKey)
        } else {
            null
        }
    }
}