package com.lovelycatv.crystal.rag.query

import co.elastic.clients.elasticsearch._types.SortOrder
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.elasticsearch.core.SearchRequest
import co.elastic.clients.json.JsonData
import com.lovelycatv.crystal.rag.api.query.AbstractQueryConditionTranslator
import com.lovelycatv.crystal.rag.api.query.condition.FilterCondition
import com.lovelycatv.crystal.rag.api.query.condition.basic.BasicQueryCondition
import com.lovelycatv.crystal.rag.api.query.condition.basic.MatchCondition
import com.lovelycatv.crystal.rag.api.query.condition.basic.RangeCondition
import com.lovelycatv.crystal.rag.api.query.condition.basic.TermCondition
import com.lovelycatv.crystal.rag.api.query.condition.bool.BoolCondition
import com.lovelycatv.crystal.rag.api.query.condition.bool.MustCondition
import com.lovelycatv.crystal.rag.api.query.condition.bool.ShouldCondition
import com.lovelycatv.crystal.rag.api.query.condition.func.SizeCondition
import com.lovelycatv.crystal.rag.api.query.condition.func.SortCondition

/**
 * @author lovelycat
 * @since 2025-03-29 21:19
 * @version 1.0
 */
class ElasticSearchQueryConditionTranslator : AbstractQueryConditionTranslator<SearchRequest.Builder, SearchRequest.Builder>() {
    override fun produceOutput(`in`: SearchRequest.Builder) = `in`

    private fun Query.Builder.applyBasicCondition(sub: BasicQueryCondition): Query.Builder {
        when (sub) {
            // Nested condition
            is BoolCondition -> {
                this.bool {
                    it.applyConditions(sub)
                }
            }

            is MatchCondition -> {
                this.match(MatchQuery.of { it.field(sub.field).query(sub.query) })
            }

            is TermCondition -> {
                this.term {
                    it.field(sub.field).value(sub.query)
                }
            }

            is RangeCondition -> {
                this.range { rangeBuilder ->
                    rangeBuilder.field(sub.field).run {
                        sub.map.forEach { (rangeConditionType, value) ->
                            when (rangeConditionType) {
                                RangeCondition.Type.GREAT_THAN -> {
                                    this.gt(JsonData.of(value))
                                }

                                RangeCondition.Type.LESS_THAN -> {
                                    this.lt(JsonData.of(value))
                                }

                                RangeCondition.Type.GREAT_OR_EQUALS -> {
                                    this.gte(JsonData.of(value))
                                }

                                RangeCondition.Type.LESS_OR_EQUALS -> {
                                    this.lte(JsonData.of(value))
                                }
                            }
                        }
                    }
                    rangeBuilder
                }
            }

            else -> {
                throw IllegalStateException("Unsupported query condition type: ${sub::class.qualifiedName}")
            }
        }

        return this
    }

    private fun BoolQuery.Builder.applyConditions(boolCondition: BoolCondition): BoolQuery.Builder {
        boolCondition.conditions.forEach { sub ->
            when (sub) {
                is MustCondition -> {
                    sub.conditions.forEach {
                        this.must { mustQueryBuilder ->
                            mustQueryBuilder.applyBasicCondition(it)
                            mustQueryBuilder
                        }
                    }
                }


                is ShouldCondition -> {
                    sub.conditions.forEach {
                        this.should { shouldQueryBuilder ->
                            shouldQueryBuilder.applyBasicCondition(it)
                            shouldQueryBuilder
                        }
                    }
                    this.minimumShouldMatch(sub.getMinimumShouldMatch().toString())
                }

                is FilterCondition -> {
                    sub.conditions.forEach {
                        this.filter { filterQueryBuilder ->
                            filterQueryBuilder.applyBasicCondition(it)
                            filterQueryBuilder
                        }
                    }

                }

                else -> {
                    throw IllegalStateException("Unsupported query condition type: ${sub::class.qualifiedName} in BoolCondition")
                }
            }
        }

        return this
    }

    override fun applyBoolCondition(`in`: SearchRequest.Builder, condition: BoolCondition) {
        `in`.query { queryBuilder ->
            queryBuilder.bool {
                it.applyConditions(condition)
            }
        }
    }

    override fun applyBasicQueryCondition(`in`: SearchRequest.Builder, condition: BasicQueryCondition) {
        `in`.query { queryBuilder ->
            queryBuilder.applyBasicCondition(condition)
        }
    }

    override fun applySortCondition(`in`: SearchRequest.Builder, condition: SortCondition) {
        `in`.sort { sortBuilder ->
            sortBuilder.field {
                it.field(condition.field)
                    .order(
                        if (condition.decrease)
                            SortOrder.Desc
                        else
                            SortOrder.Asc
                    )
            }
        }
    }

    override fun applySizeCondition(`in`: SearchRequest.Builder, condition: SizeCondition) {
        `in`.size(condition.size)
    }
}