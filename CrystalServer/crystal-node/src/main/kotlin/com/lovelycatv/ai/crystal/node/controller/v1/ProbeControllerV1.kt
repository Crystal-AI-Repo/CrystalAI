package com.lovelycatv.ai.crystal.node.controller.v1

import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Node.ProbeController.NODE_INFO
import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Node.ProbeController.MAPPING
import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Node.ProbeController.NODE_AVAILABLE
import com.lovelycatv.ai.crystal.common.GlobalConstants.Api.Node.ProbeController.NODE_TASKS
import com.lovelycatv.ai.crystal.common.GlobalConstants.ApiVersionControl.API_PREFIX_VERSION_1
import com.lovelycatv.ai.crystal.common.client.DeepSeekClient
import com.lovelycatv.ai.crystal.common.client.OllamaClient
import com.lovelycatv.ai.crystal.common.client.safeRequest
import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.common.response.node.probe.NodeProbeResult
import com.lovelycatv.ai.crystal.common.response.node.probe.NodeTasksResult
import com.lovelycatv.ai.crystal.common.vo.NodeTaskVO
import com.lovelycatv.ai.crystal.node.Global
import com.lovelycatv.ai.crystal.node.config.NetworkConfig
import com.lovelycatv.ai.crystal.node.config.NodeConfiguration
import com.lovelycatv.ai.crystal.node.task.AbstractTask
import com.lovelycatv.ai.crystal.node.queue.TaskQueue
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * @author lovelycat
 * @since 2025-02-06 19:31
 * @version 1.0
 */
@RestController
@RequestMapping(API_PREFIX_VERSION_1 + MAPPING)
class ProbeControllerV1(
    @Value("\${spring.application.name}")
    private val applicationName: String,
    private val networkConfig: NetworkConfig,
    private val nodeConfiguration: NodeConfiguration,
    private val ollamaFeignClient: OllamaClient,
    private val deepSeekFeignClient: DeepSeekClient,
    private val taskQueue: TaskQueue<AbstractTask>
) : IProbeControllerV1 {
    @GetMapping(NODE_INFO)
    override fun getNodeInfo(): Result<NodeProbeResult> {
        return Result.success(
            "",
            NodeProbeResult(
                applicationName,
                networkConfig.localIpAddress(),
                networkConfig.applicationPort,
                nodeConfiguration.isSsl,
                ollamaModels = (if (nodeConfiguration.ollama.isEnabled)
                    ollamaFeignClient.safeRequest { getOllamaModels().models }
                else
                    null) ?: emptyList(),
                deepseekModels = (if (nodeConfiguration.deepseek.isEnabled)
                    deepSeekFeignClient.safeRequest { getModels("Bearer " + nodeConfiguration.deepseek.apiKey).data }
                else
                    null) ?: emptyList()
            )
        )
    }

    @GetMapping(NODE_TASKS)
    override fun getNodeTasks(): Result<NodeTasksResult> {
        val chatTasks = taskQueue.glance()

        return Result.success(
            "",
            NodeTasksResult(
                runningTask = Global.Variables.currentRunningTask?.toNodeTaskVO(),
                pendingTasks = chatTasks.map { it.toNodeTaskVO() }
            )
        )
    }

    @GetMapping(NODE_AVAILABLE)
    override fun isNodeAvailable(): Result<Boolean> {
        return Result.success("", !Global.isTaskRunning())
    }

    private fun AbstractTask.toNodeTaskVO(): NodeTaskVO {
        return NodeTaskVO(taskType = this.taskType.name, taskClass = this::class.qualifiedName ?: "", data = this)
    }
}