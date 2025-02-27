package com.lovelycatv.ai.crystal.dispatcher.task

/**
 * @author lovelycat
 * @since 2025-02-28 01:59
 * @version 1.0
 */
data class TaskPerformResult<T>(
    val taskId: String,
    val status: Status,
    val data: T,
    val message: String? = null,
    val cause: Throwable? = null,
) {
    companion object {
        fun <T> success(taskId: String, message: String? = null, data: T): TaskPerformResult<T> {
            return TaskPerformResult(taskId, Status.SUCCESS, data, message = message)
        }
        fun <T> failed(taskId: String, message: String, data: T, cause: Throwable? = null): TaskPerformResult<T> {
            return TaskPerformResult(taskId, Status.FAILED, data, message, cause)
        }
    }

    enum class Status {
        SUCCESS,
        FAILED
    }
}