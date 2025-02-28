package com.lovelycatv.ai.crystal.common.response.node.probe

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty
import com.lovelycatv.ai.crystal.common.vo.NodeTaskVO

/**
 * @author lovelycat
 * @since 2025-02-28 19:46
 * @version 1.0
 */
data class NodeTasksResult @JsonCreator constructor(
    @JsonProperty("runningTask")
    val runningTask: NodeTaskVO?,
    @JsonProperty("pendingTasks")
    val pendingTasks: List<NodeTaskVO>,
) {
    fun isAvailable() = runningTask == null

    val pendingTasksCount: Int get() = this.pendingTasks.size
}