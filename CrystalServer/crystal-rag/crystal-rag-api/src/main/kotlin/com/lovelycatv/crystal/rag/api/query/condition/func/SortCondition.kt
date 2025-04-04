package com.lovelycatv.crystal.rag.api.query.condition.func

/**
 * @author lovelycat
 * @since 2025-03-30 03:26
 * @version 1.0
 */
class SortCondition(
    val field: String,
    val decrease: Boolean
) : FunctionalQueryCondition(Type.SORT)