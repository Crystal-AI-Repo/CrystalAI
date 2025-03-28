package com.lovelycatv.ai.crystal.node.controller.v1

import com.lovelycatv.ai.crystal.common.GlobalConstants
import com.lovelycatv.ai.crystal.common.GlobalConstants.ApiVersionControl.API_PREFIX_VERSION_1
import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.node.service.NodeAuthService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author lovelycat
 * @since 2025-03-28 16:19
 * @version 1.0
 */
@RestController
@RequestMapping(API_PREFIX_VERSION_1 + GlobalConstants.Api.Node.AuthController.MAPPING)
class AuthControllerV1(
    private val nodeAuthService: NodeAuthService
) : IAuthControllerV1 {
    @PostMapping(GlobalConstants.Api.Node.AuthController.LOGIN)
    override fun login(secretKey: String): Result<*> {
        val accessKey = nodeAuthService.loginWithSecretKey(secretKey)
        return if (accessKey != null) {
            Result.success("", accessKey)
        } else {
            Result.badRequest("Secret key is invalid")
        }
    }
}