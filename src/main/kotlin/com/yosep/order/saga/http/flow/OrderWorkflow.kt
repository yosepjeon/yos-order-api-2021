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
import java.time.LocalDateTime
import kotlin.RuntimeException

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

    @JsonIgnore
    var finalPrice: Long = 0

    @JsonIgnore
    lateinit var orderStep: OrderStep

    @JsonIgnore
    lateinit var productStep: ProductStep

    @JsonIgnore
    lateinit var productDiscountCouponStep: ProductDiscountCouponStep

    @JsonIgnore
    lateinit var totalDiscountCouponStep: TotalDiscountCouponStep


    fun processFlow(): Mono<Boolean> {
        state = "PENDING"
        orderDtoForCreation.orderId = id

        orderStep = OrderStep(orderService, randomIdGenerator, orderDtoForCreation)
        productStep = ProductStep(
            productWebclient,
            orderToProductProducer,
            ProductStepDtoForCreation(id, orderDtoForCreation.orderProductDtos)
        )
        productDiscountCouponStep = ProductDiscountCouponStep(
            couponWebclient,
            orderToProductCouponProducer,
            OrderProductDiscountCouponStepDto(
                id,
                orderDtoForCreation.orderProductDiscountCouponDtos
            )
        )
        totalDiscountCouponStep = TotalDiscountCouponStep(
            couponWebclient,
            orderToTotalCouponProducer,
            OrderTotalDiscountCouponStepDto(
                id,
                finalPrice,
                orderDtoForCreation.orderTotalDiscountCouponDtos,
                0L,
                "READY"
            )
        )
        printBeforeLogic()

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
                finalPrice = 0L
//                throw StepFailException("product step fail exception")
                val map = mutableMapOf<String, Int>()

                productDiscountCouponStep.orderProductDiscountCouponStepDto.orderProductDiscountCouponDtos.forEach {
                    finalPrice += it.calculatedPrice
                    map[it.productId] = 1
                }

                productStep.productStepDtoForCreation.orderProductDtos.forEach {
                    if (map.getOrDefault(it.productId, -1) == -1) {
                        finalPrice += (it.count * it.price)
                    }
                }

                totalDiscountCouponStep = TotalDiscountCouponStep(
                    couponWebclient,
                    orderToTotalCouponProducer,
                    OrderTotalDiscountCouponStepDto(
                        id,
                        finalPrice,
                        orderDtoForCreation.orderTotalDiscountCouponDtos,
                        0L,
                        "READY"
                    )
                )

                // TODO: null 없애기(!!)
                totalDiscountCouponStep!!.process()
            }
            .doOnNext {
                finalPrice = it.calculatedPrice
                update(totalDiscountCouponStep as WorkflowStep<Any>)
                    .doOnNext { isSuccessUpdate ->
                        if (!isSuccessUpdate) {
                            throw NotExistWorkflowException()
                        }
                    }
            }
            .flatMap {
                this.state = "COMP"
                orderDtoForCreation.orderState = "COMP"
//                throw NotExistWorkflowException()
                printResult()

                Mono.create<Boolean> { monoSink ->
                    if (finalPrice == orderDtoForCreation.totalPrice) {
                        monoSink.success(true)
                    } else {
                        monoSink.success(false)
                    }
                }
            }
            .doOnNext {
                println("[Complete Result]")
                println(objectMapper!!.writeValueAsString(this))
            }
            .onErrorResume {
                val exception = it as RuntimeException

                Mono.create<Boolean> { monoSink ->
                    orderDtoForCreation.orderState = if(it.message == null) "Unknown_Exception" else it.message.toString()
                    printResult()

                    if (it is RuntimeException) {
                        revertFlow()
                            .subscribe()
                    }

                    monoSink.success(false)
                }
            }
    }

    //  포트폴리오용 실제 로직에 사용 X
    fun printBeforeLogic() {
        println("[수행 전]")
        printContent()
    }

    //  포트폴리오용 실제 로직에 사용 X
    fun printResult() {
        println("[최종 결과]")
        printContent()
    }

    //  포트폴리오용 실제 로직에 사용 X
    fun printContent() {
        println("orderId: ${orderDtoForCreation.orderId}")
        println("order state: ${orderDtoForCreation.orderState}")
        println()

        println("상품 스텝 처리 결과")
        productStep.productStepDtoForCreation.orderProductDtos.forEach { orderProductDto ->
            println("productId: ${orderProductDto.productId} 상품 금액: ${orderProductDto.price} 요청 수량: ${orderProductDto.count} state: ${orderProductDto.state}")
        }
        println()

        println("상품 할인 쿠폰 스텝 처리 결과")
        productDiscountCouponStep.orderProductDiscountCouponStepDto.orderProductDiscountCouponDtos.forEach { orderProductDiscountCouponDto ->
            println("couponByUserId: ${orderProductDiscountCouponDto.couponByUserId} 할인 금액: ${orderProductDiscountCouponDto.discountAmount} 할인 비율: ${orderProductDiscountCouponDto.discountPercent} state: ${orderProductDiscountCouponDto.state}")
        }
        println()

        println("전체 할인 쿠폰 스텝 처리 결과")
        totalDiscountCouponStep.orderTotalDiscountCouponStepDto.orderTotalDiscountCouponDtos.forEach { orderTotalDiscountCouponDto ->
            println("couponByUserId: ${orderTotalDiscountCouponDto.couponByUserId} 할인 금액: ${orderTotalDiscountCouponDto.discountAmount} 할인 비율: ${orderTotalDiscountCouponDto.discountPercent} state: ${orderTotalDiscountCouponDto.state}")
        }
        println()
        orderDtoForCreation.totalPrice
        println("요청 가격: ${orderDtoForCreation.totalPrice}")
        println("최종 가격: $finalPrice")
    }

    fun processStep(orderDtoForCreation: OrderDtoForCreation) {

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