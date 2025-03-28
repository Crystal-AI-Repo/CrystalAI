package com.lovelycatv.ai.crystal.node.aop

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect

@Aspect
class TaskQueueAspect {
    @After("execution(* com.lovelycatv.ai.crystal.node.queue.TaskQueue+.submitTask(..))")
    fun afterSubmitTask(joinPoint: JoinPoint) {

    }
}