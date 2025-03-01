package com.lovelycatv.ai.crystal.dispatcher.task

import com.lovelycatv.ai.crystal.common.data.message.PromptMessage
import com.lovelycatv.ai.crystal.common.data.message.model.embedding.AbstractEmbeddingOptions

/**
 * @author lovelycat
 * @since 2025-03-01 20:53
 * @version 1.0
 */
class OneTimeEmbeddingTask<OPTIONS: AbstractEmbeddingOptions>(
    options: OPTIONS,
    prompts: List<PromptMessage>,
    timeout: Long = 0L
) : AbstractEmbeddingTask<OPTIONS>(options, prompts, timeout)