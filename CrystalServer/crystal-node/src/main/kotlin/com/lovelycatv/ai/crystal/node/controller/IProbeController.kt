package com.lovelycatv.ai.crystal.node.controller

import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.common.response.node.probe.NodeProbeResult
import com.lovelycatv.ai.crystal.common.response.node.probe.NodeTasksResult

/**
 * @author lovelycat
 * @since 2025-02-26 00:43
 * @version 1.0
 */
interface IProbeController {
    fun getNodeInfo(): Result<NodeProbeResult>

    fun getNodeTasks(): Result<NodeTasksResult>

    fun isNodeAvailable(): Result<Boolean>
}