package com.lovelycatv.crystal.rag.api.query

import com.lovelycatv.ai.crystal.common.util.divide
import com.lovelycatv.crystal.rag.api.query.condition.AbstractQueryCondition
import com.lovelycatv.crystal.rag.api.query.condition.basic.BasicQueryCondition
import com.lovelycatv.crystal.rag.api.query.condition.bool.BoolCondition
import com.lovelycatv.crystal.rag.api.query.condition.func.FunctionalQueryCondition
import com.lovelycatv.crystal.rag.api.query.condition.func.SizeCondition
import com.lovelycatv.crystal.rag.api.query.condition.func.SortCondition

/**
 * @author lovelycat
 * @since 2025-03-30 03:57
 * @version 1.0
 */
abstract class AbstractQueryConditionTranslator<IN, OUT> : QueryConditionTranslator<IN, OUT> {
    override fun translate(queryConditions: List<AbstractQueryCondition>, `in`: IN): OUT {
        val (normalQueryConditions, functionalConditions) = queryConditions.divide { it !is FunctionalQueryCondition }

        for (queryCondition in normalQueryConditions) {
            when (queryCondition) {
                /** As the BoolCondition is a sub-type of BasicQueryCondition,
                 * please process the BoolCondition in this.applyBasicQueryCondition()
                 *
                 * is BoolCondition -> {
                 *     this.applyBoolCondition(`in`, queryCondition)
                 * }
                 */

                is BasicQueryCondition -> {
                    this.applyBasicQueryCondition(`in`, queryCondition)
                }

                else -> {
                    throw IllegalStateException("Unsupported query condition type: ${queryCondition::class.qualifiedName}")
                }
            }
        }

        for (functionalCondition in functionalConditions) {
            when (functionalCondition) {
                is SortCondition -> {
                    this.applySortCondition(`in`, functionalCondition)
                }

                is SizeCondition -> {
                    this.applySizeCondition(`in`, functionalCondition)
                }

                else -> {
                    throw IllegalStateException("Unsupported query condition type: ${functionalCondition::class.qualifiedName} in FunctionalQueryCondition")
                }
            }
        }

        return this.produceOutput(`in`)
    }

    abstract fun produceOutput(`in`: IN): OUT

    abstract fun applyBoolCondition(`in`: IN, condition: BoolCondition)

    abstract fun applyBasicQueryCondition(`in`: IN, condition: BasicQueryCondition)

    abstract fun applySortCondition(`in`: IN, condition: SortCondition)

    abstract fun applySizeCondition(`in`: IN, condition: SizeCondition)
}