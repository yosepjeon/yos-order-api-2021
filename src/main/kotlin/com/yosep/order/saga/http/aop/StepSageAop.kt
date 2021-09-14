package com.yosep.order.saga.http.aop

import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.AfterThrowing
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Pointcut
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Aspect
@Component
class StepSageAop @Autowired constructor(

){
//    @Pointcut("@annotation(com.yosep.order.saga.http.annotation.SagaStep)")
//    fun ProcessSagaStepAop() {
//    }

    @After("@annotation(com.yosep.order.saga.http.annotation.SagaStep)")
    @Throws(Throwable::class)
    fun processSagaStepAop(joinPoint: JoinPoint) {


        // @LogExecutionTime 애노테이션이 붙어있는 타겟 메소드를 실행
        println("AOP")
        joinPoint.target
//        println("after proceed ${joinPoint.target}")
//        return proceed // 결과 리턴
    }
}