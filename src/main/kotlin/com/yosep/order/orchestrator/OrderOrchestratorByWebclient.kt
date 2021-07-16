package com.yosep.order.orchestrator

import com.fasterxml.jackson.databind.ObjectMapper
import com.yosep.order.common.data.RandomIdGenerator
import com.yosep.order.common.exception.DuplicateKeyException
import com.yosep.order.data.dto.CreatedOrderDto
import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.saga.http.Workflow
import com.yosep.order.saga.http.flow.OrderWorkflow
import com.yosep.order.service.OrderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.util.retry.Retry
import java.time.LocalDateTime

@Component
class OrderOrchestratorByWebclient @Autowired constructor(
    @Qualifier("payment-command")
    private val paymentWebclient: WebClient,
    @Qualifier("product-command")
    private val productWebclient: WebClient,
    @Qualifier("coupon-command")
    private val couponWebclient: WebClient,
    private val orderService: OrderService,
    private val objectMapper: ObjectMapper,
    private val redisTemplate: ReactiveRedisTemplate<String, String>,
    private val randomIdGenerator: RandomIdGenerator
) {
    fun order(orderDtoForCreation: OrderDtoForCreation): Mono<CreatedOrderDto> {

        lateinit var orderEventId: String
        lateinit var orderWorkflow: OrderWorkflow<Any, OrderDtoForCreation>

        return createOrderWorkFlow()
            .flatMap { createdOrderWorkFlow ->
                orderWorkflow = createdOrderWorkFlow as OrderWorkflow<Any, OrderDtoForCreation>

                orderWorkflow.processFlow(orderDtoForCreation)
            }
//            .flatMap(orderWorkflow.processFlow(orderDtoForCreation))
    }

    private fun createOrderWorkFlow(): Mono<OrderWorkflow<*, *>> {
        lateinit var orderEventId: String
        lateinit var orderWorkflow: Workflow<Any, OrderDtoForCreation>

        return randomIdGenerator.generate()
            .flatMap { createdOrderEventId ->
                orderEventId = createdOrderEventId
//                redisTemplate.hasKey(createdOrderEventId)
                orderWorkflow = OrderWorkflow<Any, OrderDtoForCreation>(
                    redisTemplate = redisTemplate,
                    orderService = orderService,
                    randomIdGenerator = randomIdGenerator,
                    id = orderEventId
                )

                val paredOrderWorkFlow = objectMapper.writeValueAsString(orderWorkflow)
                redisTemplate.opsForValue().set(orderEventId, paredOrderWorkFlow)
            }
            .flatMap { result ->
                if (!result) {
                    throw DuplicateKeyException()
                } else {
                    Mono.create<OrderWorkflow<*, *>> { monoSink ->
                        monoSink.success(orderWorkflow as OrderWorkflow<Any, OrderDtoForCreation>)
                    }
                }
            }
            .retryWhen(Retry.max(5)
                .filter { error ->
                    error is DuplicateKeyException
                })

//        return randomIdGenerator.generate()
//            .flatMap { createdOrderEventId ->
//                orderEventId = createdOrderEventId
//                redisTemplate.hasKey(createdOrderEventId)
//            }
//            .flatMap { hasKey ->
//                if (hasKey) {
//                    throw DuplicateKeyException()
//                } else {
//                    orderWorkflow = OrderWorkflow<Any, OrderDtoForCreation>(
//                        redisTemplate = redisTemplate,
//                        orderService = orderService,
//                        randomIdGenerator = randomIdGenerator,
//                        id = orderEventId
//                    )
//
//                    val paredOrderWorkFlow = objectMapper.writeValueAsString(orderWorkflow)
//                    redisTemplate.opsForValue().set(orderEventId, paredOrderWorkFlow)
//                        .flatMap {
//                            println("$$$ $it")
//                            redisTemplate.opsForValue().get(orderEventId)
//                        }
//                        .flatMap {
//                            println("### $it")
//
//                            Mono.create<OrderWorkflow<*, *>> { monoSink ->
////                                val selectedOrderWorkflow = objectMapper.readValue<OrderWorkflow<*, *>>(
////                                    it,
////                                    OrderWorkflow::class.java
////                                )
////
////                                selectedOrderWorkflow.orderService = orderService
////                                selectedOrderWorkflow.redisTemplate = redisTemplate
////                                selectedOrderWorkflow.randomIdGenerator = randomIdGenerator
////
////                                monoSink.success(selectedOrderWorkflow)
//                                monoSink.success(orderWorkflow as OrderWorkflow<Any, OrderDtoForCreation>)
//                            }
//                        }
//                }
//            }
//            .retryWhen(Retry.max(5)
//                .filter { error ->
//                    error is DuplicateKeyException
//                })
    }

    private fun doOnErrors(throwable: Throwable) {

    }
}