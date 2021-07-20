package com.yosep.order.saga.http.flow

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.yosep.order.common.data.RandomIdGenerator
import com.yosep.order.common.exception.DuplicateKeyException
import com.yosep.order.common.exception.NotExistWorkflowException
import com.yosep.order.data.dto.CreatedOrderDto
import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.saga.http.Workflow
import com.yosep.order.saga.http.WorkflowStep
import com.yosep.order.saga.http.step.OrderStep
import com.yosep.order.saga.http.step.ProductStep
import com.yosep.order.service.OrderService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.LocalDateTime

class OrderWorkflow(
    @JsonIgnore
    private val paymentWebclient: WebClient,
    @JsonIgnore
    private val productWebclient: WebClient,
    @JsonIgnore
    private val couponWebclient: WebClient,
    @JsonIgnore
    private var redisTemplate: ReactiveRedisTemplate<String, String>? = null,
    @JsonIgnore
    private var orderService: OrderService? = null,
    @JsonIgnore
    private var randomIdGenerator: RandomIdGenerator? = null,
    @JsonIgnore
    private var objectMapper: ObjectMapper? = null,
    val orderDtoForCreation: OrderDtoForCreation,
    id: String = "",
    steps: MutableList<WorkflowStep<OrderDtoForCreation, CreatedOrderDto>> = mutableListOf(),
    type: String = "ORDER",
    state: String = "READY",
    createdDate: LocalDateTime = LocalDateTime.now()
) : Workflow<OrderDtoForCreation, CreatedOrderDto>(id, steps, type, state, createdDate) {
    fun processFlow(): Mono<CreatedOrderDto> {
        val orderStep = OrderStep(orderService, randomIdGenerator)
        val productStep = ProductStep(paymentWebclient, orderDtoForCreation.orderProductDtos)
        var createdOrderDto: CreatedOrderDto

        return orderStep.process(orderDtoForCreation)
            // update
            .flatMap { createdOrderDto ->
                this.steps.add(orderStep)
                update()
                    .flatMap { isSuccessUpdate ->
                        if (!isSuccessUpdate) {
                            throw NotExistWorkflowException()
                        }

                        Mono.create<CreatedOrderDto> { monoSink ->
                            monoSink.success(createdOrderDto)
                        }
                    }
            }
            .flatMap { createdOrderDto ->
                Mono.create<CreatedOrderDto> { monoSink ->
                    println("parsedFlow= ${objectMapper!!.writeValueAsString(this)}")
//                    println("selected= ${redisTemplate!!.opsForValue().get(id)}")
                    monoSink.success(createdOrderDto)
                }
            }
    }

    fun revertFlow() {

    }

    private fun update(): Mono<Boolean> {
        val parsedOrderWorkflow = objectMapper!!.writeValueAsString(this)
        return redisTemplate!!.opsForValue().setIfPresent(id, parsedOrderWorkflow)
    }
}

//class OrderWorkflow<T,R>(
//    @JsonIgnore
//    var redisTemplate: ReactiveRedisTemplate<String, String>? = null,
//    @JsonIgnore
//    var orderService: OrderService? = null,
//    @JsonIgnore
//    var randomIdGenerator: RandomIdGenerator? = null,
//    id: String = "",
////    steps: MutableList<WorkflowStep<OrderDtoForCreation, CreatedOrderDto>> = mutableListOf(),
//    steps: MutableList<WorkflowStep<T,R>> = mutableListOf(),
//    type: String = "ORDER",
//    state: String = "READY",
//    createdDate: LocalDateTime = LocalDateTime.now()
//) : Workflow<T,R>(id, steps, type, state, createdDate) {
//    fun processFlow(orderDtoForCreation: OrderDtoForCreation): Mono<CreatedOrderDto> {
//        val orderStep = OrderStep(orderService, randomIdGenerator)
//        val productStep = ProductStep()
//
//        return orderStep.process(orderDtoForCreation)
////            .flatMap { createdOrderDto ->
////                this.steps.add(orderStep )
////                Mono.empty<CreatedOrderDto>()
////            }
//    }
//
//    fun revertFlow() {
//
//    }
//}