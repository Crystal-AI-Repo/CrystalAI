package com.lovelycatv.ai.crystal.node.exception

import kotlin.reflect.KClass

/**
 * @author lovelycat
 * @since 2025-02-28 16:10
 * @version 1.0
 */
class UnsupportedModelOptionsType(clazz: KClass<*>) : RuntimeException("Unsupported ChatOptions type: [${clazz::qualifiedName}]")