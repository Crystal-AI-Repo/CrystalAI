package com.lovelycatv.ai.crystal.dispatcher.controller.v1

import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.dispatcher.controller.IWebManagerController
import com.lovelycatv.ai.crystal.dispatcher.response.model.chat.OneTimeChatRequestResult
import com.lovelycatv.ai.crystal.dispatcher.response.model.embedding.OneTimeEmbeddingRequestResult
import com.lovelycatv.ai.crystal.dispatcher.response.model.chat.StreamChatRequestResult
import org.springframework.web.bind.annotation.RequestParam

/**
 * @author lovelycat
 * @since 2025-02-26 16:11
 * @version 1.0
 */
interface IWebManagerControllerV1 : IWebManagerController {
    suspend fun testSendOneTimeChatTask(
        @RequestParam("model")
        model: String,
        @RequestParam("message")
        message: String,
        @RequestParam("waitForResult")
        waitForResult: Boolean,
        @RequestParam("timeout", required = false, defaultValue = "0")
        timeout: Long
    ): Result<OneTimeChatRequestResult>

    suspend fun testSendStreamChatTask(
        @RequestParam("model")
        model: String,
        @RequestParam("message")
        message: String,
        @RequestParam("timeout", required = false, defaultValue = "0")
        timeout: Long
    ): Result<StreamChatRequestResult>

    suspend fun testSendOneTimeEmbeddingTask(
        @RequestParam("model")
        model: String,
        @RequestParam("message")
        message: String,
        @RequestParam("waitForResult")
        waitForResult: Boolean,
        @RequestParam("timeout", required = false, defaultValue = "0")
        timeout: Long
    ): Result<OneTimeEmbeddingRequestResult>
}