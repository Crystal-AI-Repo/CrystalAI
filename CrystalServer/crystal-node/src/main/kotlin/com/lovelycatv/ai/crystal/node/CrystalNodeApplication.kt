package com.lovelycatv.ai.crystal.node

import com.lovelycatv.ai.crystal.common.client.safeRequest
import com.lovelycatv.ai.crystal.common.response.Result
import com.lovelycatv.ai.crystal.common.response.dispatcher.NodeRegisterResult
import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.common.util.toJSONString
import com.lovelycatv.ai.crystal.node.client.NodeDispatcherClient
import com.lovelycatv.ai.crystal.node.config.NetworkConfig
import com.lovelycatv.ai.crystal.node.config.NodeConfiguration
import com.lovelycatv.ai.crystal.node.task.NodeRegisterTask
import jakarta.annotation.Resource
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.ConfigurableApplicationContext
import kotlin.concurrent.thread

@SpringBootApplication
@EnableConfigurationProperties(NodeConfiguration::class)
@EnableFeignClients
class CrystalNodeApplication : CommandLineRunner {
    private val log = this.logger()

    @Resource
    private lateinit var networkConfig: NetworkConfig
    @Value("\${server.port}")
    private var serverPort: Int = 8080
    @Resource
    private lateinit var nodeDispatcherClient: NodeDispatcherClient

    override fun run(vararg args: String?) {
        // Register to dispatcher service
        NodeRegisterTask(networkConfig.localIpAddress(), serverPort, nodeDispatcherClient).start()
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(CrystalNodeApplication::class.java, *args)
}