package com.lovelycatv.ai.crystal.common.util

/**
 * @author lovelycat
 * @since 2025-02-15 17:09
 * @version 1.0
 */
class CollectionExtensions private constructor()

fun <E> Collection<E>.divide(condition: (E) -> Boolean): Pair<List<E>, List<E>> {
    val left = mutableListOf<E>()
    val right = mutableListOf<E>()
    this.forEach {
        if (condition(it)) {
            left.add(it)
        } else {
            right.add(it)
        }
    }
    return left to right
}

inline fun <T> Iterable<T>.forEachAndThen(action: (T) -> Unit): Iterable<T> {
    for (element in this) action(element)
    return this
}