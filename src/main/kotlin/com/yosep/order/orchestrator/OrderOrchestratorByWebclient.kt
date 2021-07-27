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
    @Qualifier("product")
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
        lateinit var orderWorkflow: OrderWorkflow
//        lateinit var orderWorkflow: Workflow<OrderDtoForCreation, CreatedOrderDto>

        return createOrderWorkFlow(orderDtoForCreation)
            .flatMap { createdOrderWorkFlow ->
                orderWorkflow = createdOrderWorkFlow as OrderWorkflow
                orderWorkflow.processFlow()
            }
//            .flatMap(orderWorkflow.processFlow(orderDtoForCreation))
    }

    private fun createOrderWorkFlow(orderDtoForCreation: OrderDtoForCreation): Mono<OrderWorkflow> {
        lateinit var orderEventId: String
//        lateinit var orderWorkflow: Workflow<OrderDtoForCreation, CreatedOrderDto>
        lateinit var orderWorkflow: OrderWorkflow

        return randomIdGenerator.generate()
            .flatMap { createdOrderEventId ->
                orderEventId = createdOrderEventId
//                redisTemplate.hasKey(createdOrderEventId)
                orderWorkflow = OrderWorkflow(
                    paymentWebclient = paymentWebclient,
                    productWebclient = productWebclient,
                    couponWebclient = couponWebclient,
                    redisTemplate = redisTemplate,
                    orderService = orderService,
                    randomIdGenerator = randomIdGenerator,
                    objectMapper= objectMapper,
                    orderDtoForCreation = orderDtoForCreation,
                    id = orderEventId
                )

                val parsedOrderWorkFlow = objectMapper.writeValueAsString(orderWorkflow)
                println(parsedOrderWorkFlow)
                val workflow = objectMapper.readValue(parsedOrderWorkFlow, Workflow::class.java)
                println("[paresedOrderWorkFlow]")
                println("id: ${workflow.id}")
                println("type: ${workflow.type}")

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