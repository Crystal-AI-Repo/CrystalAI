package com.lovelycatv.crystal.rag.api.query

import com.lovelycatv.crystal.rag.api.query.condition.AbstractQueryCondition

/**
 * @author lovelycat
 * @since 2025-03-29 21:19
 * @version 1.0
 */
fun interface QueryConditionTranslator<IN, OUT> {
    fun translate(queryConditions: List<AbstractQueryCondition>, `in`: IN): OUT
}