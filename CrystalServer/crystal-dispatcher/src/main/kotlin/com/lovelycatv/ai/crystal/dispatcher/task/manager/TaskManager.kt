package com.lovelycatv.ai.crystal.dispatcher.task.manager

import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.dispatcher.manager.AbstractNodeManager
import org.springframework.stereotype.Component

/**
 * @author lovelycat
 * @since 2025-02-27 01:04
 * @version 1.0
 */
@Component
class TaskManager(
    nodeManager: AbstractNodeManager
) : ListenableTaskManager(nodeManager) {
    private val logger = logger()
}