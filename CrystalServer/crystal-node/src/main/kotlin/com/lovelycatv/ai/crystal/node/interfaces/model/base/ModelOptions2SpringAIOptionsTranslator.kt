package com.lovelycatv.ai.crystal.node.interfaces.model.base

import com.lovelycatv.ai.crystal.common.data.message.model.AbstractModelOptions
import org.springframework.ai.model.ModelOptions

/**
 * @author lovelycat
 * @since 2025-03-01 15:49
 * @version 1.0
 */
interface ModelOptions2SpringAIOptionsTranslator<ABSTRACT_OPTIONS: AbstractModelOptions, MODEL_OPTIONS: ModelOptions> {
    fun translate(original: ABSTRACT_OPTIONS?): MODEL_OPTIONS
}