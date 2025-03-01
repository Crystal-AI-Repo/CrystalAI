package com.lovelycatv.ai.crystal.dispatcher.service

import com.lovelycatv.ai.crystal.common.data.message.model.AbstractModelOptions
import com.lovelycatv.ai.crystal.common.data.message.model.ModelResponseMessage
import com.lovelycatv.ai.crystal.common.data.message.model.chat.ChatResponseMessage
import com.lovelycatv.ai.crystal.dispatcher.data.node.ChatRequestSessionContainer
import com.lovelycatv.ai.crystal.dispatcher.response.ModelRequestResult
import com.lovelycatv.ai.crystal.dispatcher.task.AbstractModelTask
import com.lovelycatv.ai.crystal.dispatcher.task.TaskPerformResult
import com.lovelycatv.ai.crystal.dispatcher.task.dispatcher.TaskDispatcher
import com.lovelycatv.ai.crystal.dispatcher.task.manager.ListenableTaskManager
import com.lovelycatv.ai.crystal.dispatcher.task.manager.TaskManager
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * @author lovelycat
 * @since 2025-03-01 21:23
 * @version 1.0
 */
abstract class ModelRequestService {
    protected suspend fun <OPTIONS: AbstractModelOptions, R: ModelRequestResult> sendTask(
        taskDispatcher: TaskDispatcher,
        taskManager: TaskManager,
        task: AbstractModelTask<OPTIONS>,
        ignoreResult: Boolean,
        resultTransform: (ModelRequestResult, args: Array<Any?>) -> R
    ): R {
        val result = taskDispatcher.performTask(task)

        when (result.status) {
            TaskPerformResult.Status.SUCCESS -> {
                val sessionId = result.data
                if (!ignoreResult) {
                    return suspendCoroutine { continuation ->
                        taskManager.subscribe(sessionId, object : ListenableTaskManager.SimpleSubscriber {
                            override fun onReceived(container: ChatRequestSessionContainer, message: ModelResponseMessage) {}

                            override fun onFinished(container: ChatRequestSessionContainer) {
                                continuation.resume(resultTransform.invoke(ModelRequestResult(
                                    isRequestSent = true,
                                    isSuccess = true,
                                    message = "",
                                    sessionId = sessionId
                                ), arrayOf(container.getResponses())))
                            }

                            override fun onFailed(container: ChatRequestSessionContainer?, failedMessage: ModelResponseMessage?) {
                                continuation.resume(resultTransform.invoke(ModelRequestResult(
                                    isRequestSent = true,
                                    isSuccess = false,
                                    message = failedMessage?.message ?: "",
                                    sessionId = sessionId
                                ), arrayOf(container?.getResponses() ?: emptyList<ModelResponseMessage>())))
                            }
                        })

                        taskManager.subscribe(sessionId, ListenableTaskManager.OnTimeoutSubscriber {
                            continuation.resume(
                                resultTransform.invoke(
                                    ModelRequestResult(
                                        isRequestSent = true,
                                        isSuccess = false,
                                        message = "Timeout",
                                        sessionId = sessionId
                                    ),
                                    arrayOf()
                                )
                            )
                        })
                    }
                } else {
                    return resultTransform.invoke(ModelRequestResult(
                        isRequestSent = true,
                        isSuccess = true,
                        message = "Async",
                        sessionId = sessionId
                    ), arrayOf())
                }
            }
            TaskPerformResult.Status.FAILED -> {
                return resultTransform.invoke(ModelRequestResult(
                    isRequestSent = false,
                    isSuccess = false,
                    message = result.message ?: "",
                    sessionId = null
                ), arrayOf())
            }
        }
    }

}