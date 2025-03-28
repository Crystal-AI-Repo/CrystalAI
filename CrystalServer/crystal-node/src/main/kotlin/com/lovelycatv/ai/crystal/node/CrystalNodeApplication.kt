package com.lovelycatv.ai.crystal.node

import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.node.config.NodeConfiguration
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableConfigurationProperties(NodeConfiguration::class)
@EnableFeignClients
class CrystalNodeApplication(
    private val nodeConfiguration: NodeConfiguration
) : CommandLineRunner {
    private val logger = logger()

    /**
     * Callback used to run the bean.
     * @param args incoming main method arguments
     * @throws Exception on error
     */
    override fun run(vararg args: String?) {
        if (nodeConfiguration._mode == NodeRunningMode.STANDALONE) {
            logger.info("Crystal Node is running in standalone mode.")
        } else if (nodeConfiguration._mode == NodeRunningMode.STANDALONE) {
            logger.info("Crystal Node is running in cluster mode.")
        }
    }

}

fun main(args: Array<String>) {
    SpringApplication.run(CrystalNodeApplication::class.java, *args)
}