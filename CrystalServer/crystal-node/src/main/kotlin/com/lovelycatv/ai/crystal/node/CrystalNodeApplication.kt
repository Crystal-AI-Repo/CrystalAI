package com.lovelycatv.ai.crystal.node

import com.lovelycatv.ai.crystal.node.client.NodeDispatcherClient
import com.lovelycatv.ai.crystal.node.config.NetworkConfig
import com.lovelycatv.ai.crystal.node.config.NodeConfiguration
import jakarta.annotation.Resource
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.ConfigurableApplicationContext

@SpringBootApplication
@EnableConfigurationProperties(NodeConfiguration::class)
@EnableFeignClients
class CrystalNodeApplication : CommandLineRunner {
    @Resource
    private lateinit var networkConfig: NetworkConfig
    @Value("\${server.port}")
    private var serverPort: Int = 8080
    @Resource
    private lateinit var nodeDispatcherClient: NodeDispatcherClient
    @Resource
    private lateinit var applicationContext: ConfigurableApplicationContext

    override fun run(vararg args: String?) {
        // Register to dispatcher service
        val result = nodeDispatcherClient.registerNode(networkConfig.localIpAddress(), serverPort, false)
        if (!result.isSuccessful()) {
            SpringApplication.exit(applicationContext)
        }
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(CrystalNodeApplication::class.java, *args)
}