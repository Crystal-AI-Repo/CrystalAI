package com.lovelycatv.ai.crystal.node.exception

/**
 * @author lovelycat
 * @since 2025-02-26 21:35
 * @version 1.0
 */
class InvalidSessionIdException(sessionId: String?) : RuntimeException("Invalid session id: [$sessionId]")