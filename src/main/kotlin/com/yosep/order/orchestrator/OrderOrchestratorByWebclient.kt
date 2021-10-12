package com.yosep.order.orchestrator

import com.fasterxml.jackson.databind.ObjectMapper
import com.yosep.order.common.data.RandomIdGenerator
import com.yosep.order.common.exception.DuplicateKeyException
import com.yosep.order.data.dto.CreatedOrderDto
import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.data.entity.Order
import com.yosep.order.mq.producer.OrderToProductCouponProducer
import com.yosep.order.mq.producer.OrderToProductProducer
import com.yosep.order.mq.producer.OrderToTotalCouponProducer
import com.yosep.order.saga.http.annotation.SagaStep
import com.yosep.order.saga.http.flow.OrderWorkflow
import com.yosep.order.service.OrderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.util.retry.Retry

@Component
class OrderOrchestratorByWebclient @Autowired constructor(
    @Qualifier("payment-command")
    private val paymentWebclient: WebClient,
    @Qualifier("product")
    private val productWebclient: WebClient,
    @Qualifier("coupon")
    private val couponWebclient: WebClient,
    private val orderToProductProducer: OrderToProductProducer,
    private val orderToProductCouponProducer: OrderToProductCouponProducer,
    private val orderToTotalCouponProducer: OrderToTotalCouponProducer,
    private val orderService: OrderService,
    private val objectMapper: ObjectMapper,
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
    private val randomIdGenerator: RandomIdGenerator
) {
    @SagaStep
    fun order(orderDtoForCreation: OrderDtoForCreation): Mono<CreatedOrderDto> {

        lateinit var orderWorkflow: OrderWorkflow

        return createOrderWorkFlow(orderDtoForCreation)
            .flatMap { createdOrderWorkFlow ->
                orderWorkflow = createdOrderWorkFlow as OrderWorkflow

                orderWorkflow.processFlow()
                    .flatMap { result ->

                        Mono.create<CreatedOrderDto> {
                            if(result) {
                                it.success(CreatedOrderDto(Order()))
                            }else {
                                it.success(CreatedOrderDto(Order()))
                            }
                        }
                    }
            }
    }

    fun revertOrder(orderId: String) {
        TODO("모듈이 갑자기 다운되었을 경우 중단된 flow revert 구현")
        TODO("Redis의 데이터가 소실되었을 경우 중단된 flow revert 구현")
    }

//    @SagaStep
    private fun createOrderWorkFlow(orderDtoForCreation: OrderDtoForCreation): Mono<OrderWorkflow>
    {
        lateinit var orderEventId: String
        lateinit var orderWorkflow: OrderWorkflow

        return orderService!!.createOrder(orderDtoForCreation)
            .flatMap { createdOrderDto ->
                orderEventId = createdOrderDto.order.orderId
                orderWorkflow = OrderWorkflow(
                    paymentWebclient = paymentWebclient,
                    productWebclient = productWebclient,
                    couponWebclient = couponWebclient,
                    orderToProductProducer = orderToProductProducer,
                    orderToProductCouponProducer = orderToProductCouponProducer,
                    orderToTotalCouponProducer = orderToTotalCouponProducer,
                    redisTemplate = redisTemplate,
                    orderService = orderService,
                    randomIdGenerator = randomIdGenerator,
                    objectMapper = objectMapper,
                    orderDtoForCreation = orderDtoForCreation,
                    id = orderEventId
                )

                val parsedOrderWorkFlow = objectMapper.writeValueAsString(orderWorkflow)

                redisTemplate.opsForValue().setIfAbsent(orderEventId, parsedOrderWorkFlow)
            }
            .flatMap { result ->
                if (!result) {
                    throw DuplicateKeyException()
                } else {

                    Mono.create<OrderWorkflow> { monoSink ->
                        monoSink.success(orderWorkflow)
                    }
                }
                Mono.create<OrderWorkflow> { monoSink ->
                    monoSink.success(orderWorkflow)
                }
            }
            .retryWhen(Retry.max(5)
                .filter { error ->
                    error is DuplicateKeyException
                })
    }

    private fun doOnErrors(throwable: Throwable) {

    }
}