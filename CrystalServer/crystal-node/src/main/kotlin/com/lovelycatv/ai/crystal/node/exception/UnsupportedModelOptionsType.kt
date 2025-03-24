package com.lovelycatv.ai.crystal.node.exception

/**
 * @author lovelycat
 * @since 2025-02-28 16:10
 * @version 1.0
 */
class UnsupportedModelOptionsType(className: String?) : RuntimeException("Unsupported ModelOptions type: [$className]")