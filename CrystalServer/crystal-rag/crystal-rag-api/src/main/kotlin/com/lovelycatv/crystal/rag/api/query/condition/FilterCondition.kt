package com.lovelycatv.crystal.rag.api.query.condition

import com.lovelycatv.crystal.rag.api.query.condition.basic.AbstractBasicConditions
import com.lovelycatv.crystal.rag.api.query.condition.bool.BoolCondition

/**
 * @author lovelycat
 * @since 2025-03-30 02:59
 * @version 1.0
 */
class FilterCondition : AbstractBasicConditions(Type.FILTER), BoolCondition.SubCondition