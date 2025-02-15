package com.lovelycatv.ai.crystal.node

import com.lovelycatv.ai.crystal.common.util.logger
import com.lovelycatv.ai.crystal.node.client.NodeDispatcherClient
import com.lovelycatv.ai.crystal.node.config.NetworkConfig
import com.lovelycatv.ai.crystal.node.config.NodeConfiguration
import com.lovelycatv.ai.crystal.node.cron.NodeRegisterCronJob
import jakarta.annotation.Resource
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableConfigurationProperties(NodeConfiguration::class)
@EnableFeignClients
class CrystalNodeApplication

fun main(args: Array<String>) {
    SpringApplication.run(CrystalNodeApplication::class.java, *args)
}