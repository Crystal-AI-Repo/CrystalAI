package com.lovelycatv.ai.crystal.node

import com.lovelycatv.ai.crystal.node.config.NodeConfiguration
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