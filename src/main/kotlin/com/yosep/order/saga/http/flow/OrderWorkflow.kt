package com.yosep.order.saga.http.flow

import com.fasterxml.jackson.annotation.JsonIgnore
import com.yosep.order.common.data.RandomIdGenerator
import com.yosep.order.data.dto.CreatedOrderDto
import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.saga.http.Workflow
import com.yosep.order.saga.http.WorkflowStep
import com.yosep.order.saga.http.step.OrderStep
import com.yosep.order.saga.http.step.ProductStep
import com.yosep.order.service.OrderService
import org.springframework.data.redis.core.ReactiveRedisTemplate
import reactor.core.publisher.Mono
import java.time.LocalDateTime

class OrderWorkflow(
    @JsonIgnore
    var redisTemplate: ReactiveRedisTemplate<String, String>? = null,
    @JsonIgnore
    var orderService: OrderService? = null,
    @JsonIgnore
    var randomIdGenerator: RandomIdGenerator? = null,
    id: String = "",
    steps: MutableList<WorkflowStep<OrderDtoForCreation, CreatedOrderDto>> = mutableListOf(),
    type: String = "ORDER",
    state: String = "READY",
    createdDate: LocalDateTime = LocalDateTime.now()
) : Workflow<OrderDtoForCreation, CreatedOrderDto>(id, steps, type, state, createdDate) {
    fun processFlow(orderDtoForCreation: OrderDtoForCreation): Mono<CreatedOrderDto> {
        val orderStep = OrderStep(orderService, randomIdGenerator)
        val productStep = ProductStep()

        return orderStep.process(orderDtoForCreation)
            .flatMap { createdOrderDto ->
                this.steps.add(orderStep )
                Mono.empty<CreatedOrderDto>()
            }
    }

    fun revertFlow() {

    }
}