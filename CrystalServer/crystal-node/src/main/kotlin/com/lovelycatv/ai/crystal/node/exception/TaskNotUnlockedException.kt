package com.lovelycatv.ai.crystal.node.exception

import com.lovelycatv.ai.crystal.common.util.toJSONString
import com.lovelycatv.ai.crystal.node.data.AbstractTask

/**
 * @author lovelycat
 * @since 2025-03-01 17:10
 * @version 1.0
 */
class TaskNotUnlockedException(task: AbstractTask) : RuntimeException("Task not unlocked after performance. Task: ${task.toJSONString()}")