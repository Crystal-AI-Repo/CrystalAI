package com.lovelycatv.ai.crystal.dispatcher.task

import com.lovelycatv.ai.crystal.common.data.message.PromptMessage
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.AbstractEmbeddingOptions
import java.util.*

/**
 * @author lovelycat
 * @since 2025-03-01 20:52
 * @version 1.0
 */
abstract class AbstractEmbeddingTask<OPTIONS: AbstractEmbeddingOptions>(
    options: OPTIONS,
    prompts: List<PromptMessage>,
    timeout: Long,
    taskId: String = UUID.randomUUID().toString(),
) : AbstractModelTask<OPTIONS>(options, prompts, timeout, taskId)