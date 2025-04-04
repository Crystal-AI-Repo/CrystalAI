package com.lovelycatv.crystal.rag.api.query.condition.bool

import com.lovelycatv.crystal.rag.api.query.condition.basic.AbstractBasicConditions

/**
 * @author lovelycat
 * @since 2025-03-29 21:10
 * @version 1.0
 */
class ShouldCondition : AbstractBasicConditions(Type.BOOL_SHOULD), BoolCondition.SubCondition {
    private var minimumShouldMatch: Int = 1

    fun setMinimumShouldMatch(count: Int): ShouldCondition {
        this.minimumShouldMatch = count
        return this
    }

    fun getMinimumShouldMatch() = this.minimumShouldMatch
}