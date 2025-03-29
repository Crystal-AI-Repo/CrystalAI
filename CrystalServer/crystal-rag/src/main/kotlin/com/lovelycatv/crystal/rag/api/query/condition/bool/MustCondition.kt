package com.lovelycatv.crystal.rag.api.query.condition.bool

import com.lovelycatv.crystal.rag.api.query.condition.basic.AbstractBasicConditions

/**
 * @author lovelycat
 * @since 2025-03-30 02:09
 * @version 1.0
 */
class MustCondition : AbstractBasicConditions(Type.BOOL_MUST), BoolCondition.SubCondition