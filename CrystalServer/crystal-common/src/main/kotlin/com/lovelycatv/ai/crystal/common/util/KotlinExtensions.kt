package com.lovelycatv.ai.crystal.common.util

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * @author lovelycat
 * @since 2025-03-01 15:39
 * @version 1.0
 */
class KotlinExtensions private constructor()

@OptIn(ExperimentalContracts::class)
fun <T> T?.isNull(): Boolean {
    contract {
        returns(true) implies (this@isNull == null)
    }
    return this == null
}

@OptIn(ExperimentalContracts::class)
fun <T> T?.isNotNull(): Boolean {
    contract {
        returns(true) implies (this@isNotNull != null)
    }
    return this != null
}

@OptIn(ExperimentalContracts::class)
inline fun <reified T, reified R> T.implies(smartCast: Any?): Boolean {
    contract {
        returns(true) implies (smartCast is R)
    }

    return this.implies<T, R>(smartCast) {
        T::class.isInstance(it)
    }
}

/**
 * Implies the given [smartCast] is [R] by function [check]
 *
 * @param T Proof dependence
 * @param R Actual Type
 * @param smartCast Recognised as Unknown Type by compiler
 * @param check Return the proof implying smartCast is actually R
 * @return [smartCast] is [R]
 */
@OptIn(ExperimentalContracts::class)
inline fun <T, reified R> T.implies(smartCast: Any?, check: (T) -> Boolean): Boolean {
    contract {
        returns(true) implies (smartCast is R)
    }

    return check.invoke(this)
}