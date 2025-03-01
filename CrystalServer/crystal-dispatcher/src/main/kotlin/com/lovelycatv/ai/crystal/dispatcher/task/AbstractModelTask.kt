package com.lovelycatv.ai.crystal.dispatcher.task

import com.lovelycatv.ai.crystal.common.data.message.PromptMessage
import com.lovelycatv.ai.crystal.common.data.message.model.AbstractModelOptions
import java.util.*

/**
 * @author lovelycat
 * @since 2025-03-01 21:10
 * @version 1.0
 */
abstract class AbstractModelTask<OPTIONS: AbstractModelOptions>(
    val options: OPTIONS,
    val prompts: List<PromptMessage>,
    timeout: Long = 0L,
    taskId: String = UUID.randomUUID().toString(),
) : AbstractTask(taskId, timeout)