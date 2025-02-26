package com.lovelycatv.ai.crystal.node.aop

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.stereotype.Component

@Aspect
@Component
class OllamaTaskQueueAspect {

    @Before("execution(* OllamaTaskQueue.put(..))")
    fun beforePut(joinPoint: JoinPoint) {
        println("Before calling put() method in OllamaTaskQueue")
    }

    @After("execution(* OllamaTaskQueue.put(..))")
    fun afterPut(joinPoint: JoinPoint) {
        println("After calling put() method in OllamaTaskQueue")
    }
}