package com.lovelycatv.ai.crystal.dispatcher.cron

import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.dispatcher.manager.AbstractNodeManager
import kotlinx.coroutines.*
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.concurrent.Executors


/**
 * @author lovelycat
 * @since 2025-02-15 18:54
 * @version 1.0
 */
@Component
@EnableScheduling
class NodeStatusCheckCronJob(
    private val nodeManager: AbstractNodeManager
) {
    private val log = this.logger()

    private val nodeAlivenessCheckJob = Job()
    private val nodeAlivenessChecker = CoroutineScope(Dispatchers.IO + nodeAlivenessCheckJob)
    private val nodeInfoUpdateJob = Job()
    private val nodeInfoUpdater = CoroutineScope(Dispatchers.IO + nodeInfoUpdateJob)

    @Scheduled(cron = "\${crystal.node.check-alive-cron}")
    fun checkNodeAlive() {
        nodeManager.allRegisteredNodes.forEach {
            nodeAlivenessChecker.launch {
                if (!nodeManager.checkAndUpdateNodeStatus(it.nodeId)) {
                    log.warn(
                        "Node [{} / {}] is down, lastAliveTime: [{}] ({} seconds ago)",
                        it.nodeName,
                        it.requestUrl,
                        it.lastAliveTimestamp,
                        (System.currentTimeMillis() - it.lastAliveTimestamp) / 1000
                    )
                }
            }
        }
    }

    @Scheduled(cron = "\${crystal.node.update-node-cron}")
    fun updateNodeInfo() {
        nodeManager.allRegisteredNodes.filter { it.isAlive }.forEach {
            nodeInfoUpdater.launch {
                if (!nodeManager.updateNodeInfo(it.nodeId)) {
                    log.warn(
                        "Could not update information of node: [{} / {}], lastUpdateTime: [{}] ({} seconds ago)",
                        it.nodeName,
                        it.requestUrl,
                        it.lastUpdateTimestamp,
                        (System.currentTimeMillis() - it.lastUpdateTimestamp) / 1000
                    )
                }
            }
        }
    }
}