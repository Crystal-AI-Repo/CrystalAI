package com.lovelycatv.ai.crystal.dispatcher.exception

/**
 * @author lovelycat
 * @since 2025-02-27 01:11
 * @version 1.0
 */
class DuplicateSessionIdException(sessionId: String, usingNodeName: String) : RuntimeException("Duplicated sessionId: [${sessionId}] which is using by node: [${usingNodeName}]")