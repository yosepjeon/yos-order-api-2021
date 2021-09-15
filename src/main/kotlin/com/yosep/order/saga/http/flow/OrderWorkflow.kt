package com.yosep.order.saga.http.flow

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.yosep.order.common.data.RandomIdGenerator
import com.yosep.order.common.exception.NotExistWorkflowException
import com.yosep.order.data.dto.*
import com.yosep.order.mq.producer.OrderToCouponProducer
import com.yosep.order.mq.producer.OrderToProductCouponProducer
import com.yosep.order.mq.producer.OrderToProductProducer
import com.yosep.order.mq.producer.OrderToTotalCouponProducer
import com.yosep.order.saga.http.Workflow
import com.yosep.order.saga.http.WorkflowStep
import com.yosep.order.saga.http.annotation.SagaStep
import com.yosep.order.saga.http.step.OrderStep
import com.yosep.order.saga.http.step.ProductDiscountCouponStep
import com.yosep.order.saga.http.step.ProductStep
import com.yosep.order.saga.http.step.TotalDiscountCouponStep
import com.yosep.order.service.OrderService
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
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
    private var orderToProductProducer: OrderToProductProducer? = null,
    @JsonIgnore
    private var orderToProductCouponProducer: OrderToProductCouponProducer? = null,
    @JsonIgnore
    private var orderToTotalCouponProducer: OrderToTotalCouponProducer? = null,
    @JsonIgnore
    private var orderToCouponProducer: OrderToCouponProducer? = null,
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
    fun processFlow(): Mono<Boolean> {
        state = "PENDING"
        orderDtoForCreation.orderId = id

        val orderStep = OrderStep(orderService, randomIdGenerator, orderDtoForCreation)
        val productStep = ProductStep(
            productWebclient,
            orderToProductProducer,
            ProductStepDtoForCreation(id, orderDtoForCreation.totalPrice, orderDtoForCreation.orderProductDtos)
        )
        val productDiscountCouponStep = ProductDiscountCouponStep(
            couponWebclient,
            orderToProductCouponProducer,
            OrderProductDiscountCouponStepDto(
                id,
                orderDtoForCreation.orderProductDiscountCouponDtos
            )
        )

        var totalDiscountCouponStep: TotalDiscountCouponStep? = null
        var createdOrderDto: CreatedOrderDto? = null

        return productStep.process()
            .doOnNext {
                update(productStep as WorkflowStep<Any>)
                    .doOnNext { isSuccessUpdate ->
                        if (!isSuccessUpdate) {
                            throw NotExistWorkflowException()
                        }
                    }
            }
            // !!!!
            .flatMap {
                productDiscountCouponStep.process()
            }
            .doOnNext {
                update(productDiscountCouponStep as WorkflowStep<Any>)
                    .doOnNext { isSuccessUpdate ->
                        if (!isSuccessUpdate) {
                            throw NotExistWorkflowException()
                        }
                    }
            }
            .flatMap {
                var totalPrice = 0L
//                throw StepFailException("product step fail exception")
                productDiscountCouponStep.orderProductDiscountCouponStepDto.orderProductDiscountCouponDtos.forEach {
                    totalPrice += it.calculatedPrice
                }

                productStep.productStepDtoForCreation.orderProductDtos.forEach {
                    totalPrice += (it.count * it.price)
                }

                totalDiscountCouponStep = TotalDiscountCouponStep(
                    couponWebclient,
                    orderToTotalCouponProducer,
                    OrderTotalDiscountCouponStepDto(
                        id,
                        totalPrice,
                        orderDtoForCreation.orderTotalDiscountCouponDtos,
                        0L,
                        "READY"
                    )
                )

                // TODO: null 없애기(!!)
                totalDiscountCouponStep!!.process()
            }
            .doOnNext {
                update(totalDiscountCouponStep as WorkflowStep<Any>)
                    .doOnNext { isSuccessUpdate ->
                        if (!isSuccessUpdate) {
                            throw NotExistWorkflowException()
                        }
                    }
            }
            .flatMap {
                this.state = "COMP"
//                throw NotExistWorkflowException()
                Mono.create<Boolean> { monoSink ->
                    monoSink.success(true)
                }
            }
            .doOnNext {
                println("[Complete]")
                println(objectMapper!!.writeValueAsString(this))
            }
            .onErrorResume {
                println(it)

                if (it is RuntimeException) {
                    revertFlow()
                        .subscribe()
                }

                Mono.create<Boolean> { monoSink ->
//                    monoSink.success(createdOrderDto)
                    monoSink.success(false)
                }
            }
    }

    fun processStep() {

    }

    fun revertFlow(): Mono<Unit> {
        println("call revertFlow()")

        val monos = mutableListOf<Mono<Any>>()

        steps.forEach { step ->
            monos.add(step.revert(id).subscribeOn(Schedulers.parallel()))
        }

        return monos.zip {}
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