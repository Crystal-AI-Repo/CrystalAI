package com.lovelycatv.ai.crystal.common.data.message.model.embedding

import com.alibaba.fastjson2.annotation.JSONField
import com.fasterxml.jackson.annotation.JsonCreator
import com.lovelycatv.ai.crystal.common.data.message.AbstractMessage
import com.lovelycatv.ai.crystal.common.data.message.model.AbstractModelOptions

/**
 * @author lovelycat
 * @since 2025-03-01 15:01
 * @version 1.0
 */
abstract class AbstractEmbeddingOptions @JsonCreator constructor(
    modelName: String?,
    type: Type,
) : AbstractModelOptions(modelName, type)