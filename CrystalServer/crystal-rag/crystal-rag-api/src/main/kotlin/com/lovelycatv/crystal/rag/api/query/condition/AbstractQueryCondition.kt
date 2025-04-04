package com.lovelycatv.crystal.rag.api.query.condition

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.lovelycatv.crystal.rag.api.query.condition.basic.MatchCondition
import com.lovelycatv.crystal.rag.api.query.condition.basic.RangeCondition
import com.lovelycatv.crystal.rag.api.query.condition.basic.TermCondition
import com.lovelycatv.crystal.rag.api.query.condition.bool.BoolCondition
import com.lovelycatv.crystal.rag.api.query.condition.func.SizeCondition
import com.lovelycatv.crystal.rag.api.query.condition.func.SortCondition

/**
 * @author lovelycat
 * @since 2025-03-29 21:00
 * @version 1.0
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "type",
    visible = true
)
@JsonSubTypes(
    JsonSubTypes.Type(value = MatchCondition::class, name = "MATCH"),
    JsonSubTypes.Type(value = TermCondition::class, name = "TERM"),
    JsonSubTypes.Type(value = RangeCondition::class, name = "RANGE"),
    JsonSubTypes.Type(value = BoolCondition::class, name = "BOOL"),
    JsonSubTypes.Type(value = FilterCondition::class, name = "FILTER"),
    JsonSubTypes.Type(value = SortCondition::class, name = "SORT"),
    JsonSubTypes.Type(value = SizeCondition::class, name = "SIZE")
)
abstract class AbstractQueryCondition(val type: Type) {
    enum class Type {
        BOOL,
        BOOL_MUST,
        BOOL_SHOULD,
        MATCH,
        TERM,
        RANGE,
        FILTER,
        SORT,
        SIZE
    }
}