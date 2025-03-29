package com.lovelycatv.crystal.rag.api.query.condition.basic

/**
 * @author lovelycat
 * @since 2025-03-29 21:03
 * @version 1.0
 */
class MatchCondition(val field: String, val query: String) : BasicQueryCondition(Type.MATCH)