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