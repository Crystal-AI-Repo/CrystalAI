package com.lovelycatv.crystal.rag.api.query.condition.basic

import com.lovelycatv.crystal.rag.api.query.condition.AbstractQueryCondition

/**
 * @author lovelycat
 * @since 2025-03-29 21:14
 * @version 1.0
 */
class RangeCondition(
    val field: String
) : BasicQueryCondition(AbstractQueryCondition.Type.RANGE) {
    private val _map: MutableMap<Type, Number> = mutableMapOf()
    val map: Map<Type, Number> get() = this._map

    enum class Type {
        GREAT_THAN,
        LESS_THAN,
        GREAT_OR_EQUALS,
        LESS_OR_EQUALS
    }

    fun gt(value: Number): RangeCondition {
        this._map[Type.GREAT_THAN] = value
        return this
    }

    fun lt(value: Number): RangeCondition {
        this._map[Type.LESS_THAN] = value
        return this
    }

    fun gte(value: Number): RangeCondition {
        this._map[Type.GREAT_OR_EQUALS] = value
        return this
    }

    fun lte(value: Number): RangeCondition {
        this._map[Type.LESS_OR_EQUALS] = value
        return this
    }
}