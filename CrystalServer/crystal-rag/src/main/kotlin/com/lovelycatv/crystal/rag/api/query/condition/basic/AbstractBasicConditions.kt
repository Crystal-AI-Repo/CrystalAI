package com.lovelycatv.crystal.rag.api.query.condition.basic

import com.lovelycatv.crystal.rag.api.query.condition.AbstractQueryCondition

/**
 * @author lovelycat
 * @since 2025-03-29 21:10
 * @version 1.0
 */
abstract class AbstractBasicConditions(type: Type) : AbstractQueryCondition(type) {
    private val _conditions: MutableList<BasicQueryCondition> = mutableListOf()
    val conditions: List<BasicQueryCondition> get() = this._conditions

    fun match(builder: () -> Pair<String, String>): AbstractBasicConditions {
        val (field, query) = builder.invoke()
        this.addCondition(MatchCondition(field, query))
        return this
    }

    fun term(builder: () -> Pair<String, String>): AbstractBasicConditions {
        val (field, query) = builder.invoke()
        this.addCondition(TermCondition(field, query))
        return this
    }

    fun range(field: String, builder: RangeCondition.() -> Unit): AbstractBasicConditions {
        val cond = RangeCondition(field)
        builder.invoke(cond)
        this.addCondition(cond)
        return this
    }

    fun addCondition(condition: BasicQueryCondition): AbstractBasicConditions {
        this._conditions.add(condition)
        return this
    }
}