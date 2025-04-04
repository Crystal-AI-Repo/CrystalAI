package com.lovelycatv.crystal.rag.api.query.condition.basic

/**
 * @author lovelycat
 * @since 2025-03-29 21:09
 * @version 1.0
 */
class TermCondition(val field: String, val query: String) : BasicQueryCondition(Type.TERM)