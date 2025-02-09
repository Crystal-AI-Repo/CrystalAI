package com.lovelycatv.ai.crystal.dispatcher

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class CrystalDispatcherApplication

fun main(args: Array<String>) {
    SpringApplication.run(CrystalDispatcherApplication::class.java, *args)
}