package com.lovelycatv.crystal.rag.api.query.condition.bool

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.lovelycatv.crystal.rag.api.query.condition.AbstractQueryCondition
import com.lovelycatv.crystal.rag.api.query.condition.FilterCondition

/**
 * @author lovelycat
 * @since 2025-03-29 21:02
 * @version 1.0
 */
class BoolCondition : AbstractQueryCondition(Type.BOOL) {
    private val _conditions: MutableList<SubCondition> = mutableListOf()
    val conditions: List<SubCondition> get() = this._conditions

    fun must(builder: MustCondition.() -> Unit): BoolCondition {
        val cond = MustCondition()
        builder.invoke(cond)
        this._conditions.add(cond)
        return this
    }

    fun should(builder: ShouldCondition.() -> Unit): BoolCondition {
        val cond = ShouldCondition()
        builder.invoke(cond)
        this._conditions.add(cond)
        return this
    }

    @JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
    )
    @JsonSubTypes(
        JsonSubTypes.Type(value = MustCondition::class, name = "BOOL_MUST"),
        JsonSubTypes.Type(value = ShouldCondition::class, name = "BOOL_SHOULD"),
        JsonSubTypes.Type(value = FilterCondition::class, name = "FILTER")
    )
    interface SubCondition
}