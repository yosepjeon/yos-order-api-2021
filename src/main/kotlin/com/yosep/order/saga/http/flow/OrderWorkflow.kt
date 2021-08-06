package com.yosep.order.saga.http.flow

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.yosep.order.common.data.RandomIdGenerator
import com.yosep.order.common.exception.NotExistWorkflowException
import com.yosep.order.common.exception.StepFailException
import com.yosep.order.data.dto.CreatedOrderDto
import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.data.dto.ProductStepDtoForCreation
import com.yosep.order.saga.http.Workflow
import com.yosep.order.saga.http.WorkflowStep
import com.yosep.order.saga.http.step.OrderStep
import com.yosep.order.saga.http.step.ProductDiscountCouponStep
import com.yosep.order.saga.http.step.ProductStep
import com.yosep.order.saga.http.step.TotalDiscountCouponStep
import com.yosep.order.service.OrderService
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.onErrorReturn
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.zip
import java.lang.RuntimeException
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
//    var createdOrderDto: CreatedOrderDto? = null,
    id: String = "",
    steps: MutableList<WorkflowStep<Any>> = mutableListOf(),
    type: String = "ORDER",
    state: String = "READY",
    createdDate: LocalDateTime = LocalDateTime.now()
) : Workflow<OrderDtoForCreation, CreatedOrderDto>(id, steps, type, state, createdDate) {
    fun processFlow(): Mono<CreatedOrderDto> {
        val orderStep = OrderStep(orderService, randomIdGenerator, orderDtoForCreation)
        val productStep = ProductStep(
            productWebclient,
            ProductStepDtoForCreation(orderDtoForCreation.totalPrice, orderDtoForCreation.orderProductDtos)
        )
        val productDiscountCouponStep =
            ProductDiscountCouponStep(couponWebclient, orderDtoForCreation.orderProductDiscountCouponDtos)
        val totalDiscountCouponStep =
            TotalDiscountCouponStep(couponWebclient, orderDtoForCreation.orderTotalDiscountCouponDtos)
        var createdOrderDto: CreatedOrderDto? = null

        return orderStep.process()
            .doOnNext {
                createdOrderDto = it
//                this.steps.add(orderStep as WorkflowStep<Any>)
                update(orderStep as WorkflowStep<Any>)
                    .doOnNext { isSuccessUpdate ->
                        if (!isSuccessUpdate) {
                            throw NotExistWorkflowException()
                        }
                    }
            }
            .flatMap {
                productStep.process()
            }
            .doOnNext {
//                this.steps.add(productStep as WorkflowStep<Any>)
                println("Product Step Result: ${it.state}")

                if (it.state == "FAIL") {
                    throw StepFailException("product step fail exception")
                }
                update(productStep as WorkflowStep<Any>)
                    .doOnNext { isSuccessUpdate ->
                        if (!isSuccessUpdate) {
                            throw NotExistWorkflowException()
                        }
                    }
            }
            .flatMap {
                Mono.create<CreatedOrderDto> { monoSink ->
                    monoSink.success(createdOrderDto)
                }
            }
            .doOnNext {
                println("[parsed this]")
                println(objectMapper!!.writeValueAsString(this))
            }
            .onErrorResume {
                if(it is RuntimeException) {
                    revertFlow()
                }

                Mono.create<CreatedOrderDto> { monoSink ->
                    monoSink.success(createdOrderDto)
                }
            }
//            .doOnError {
//                if(it is RuntimeException) {
//                    revertFlow()
//                }
//            }
    }

    fun revertFlow(): Mono<Unit> {
        Mono.zip(Mono.just(1), Mono.just(2))
        val monos = mutableListOf<Mono<Any>>()

        steps.forEach { step ->
            println(step)
            monos.add(step.revert().subscribeOn(Schedulers.parallel()))
        }

        return monos.zip {
            it.forEach {
                println("element: $it")
            }
        }
    }

    private fun update(step: WorkflowStep<Any>): Mono<Boolean> {
        this.steps.add(step)
        val parsedOrderWorkflow = objectMapper!!.writeValueAsString(this)
        return redisTemplate!!.opsForValue().setIfPresent(id, parsedOrderWorkflow)
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