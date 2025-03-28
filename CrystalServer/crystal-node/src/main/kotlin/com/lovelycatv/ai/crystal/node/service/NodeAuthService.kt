package com.lovelycatv.ai.crystal.node.service

/**
 * @author lovelycat
 * @since 2025-03-28 16:20
 * @version 1.0
 */
interface NodeAuthService {
    /**
     * Login with secretKey. Returns null when failed.
     *
     * @param secretKey secretKey
     * @return AccessToken
     */
    fun loginWithSecretKey(secretKey: String): String?
}