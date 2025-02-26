package com.lovelycatv.ai.crystal.dispatcher.controller.v1

import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.dispatcher.controller.IWebManagerController
import com.lovelycatv.ai.crystal.dispatcher.data.node.OllamaChatRequestResult
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
        waitForResult: Boolean
    ): Result<OllamaChatRequestResult>
}