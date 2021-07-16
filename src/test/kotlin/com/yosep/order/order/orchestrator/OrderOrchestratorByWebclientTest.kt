package com.yosep.order.order.orchestrator

import com.fasterxml.jackson.databind.ObjectMapper
import com.yosep.order.common.exception.DuplicateKeyException
import com.yosep.order.common.exception.TestException
import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.data.vo.OrderProductDtoForCreation
import com.yosep.order.orchestrator.OrderOrchestratorByWebclient
import com.yosep.order.saga.http.Workflow
import com.yosep.order.saga.http.flow.OrderWorkflow
import io.netty.util.internal.logging.Slf4JLoggerFactory
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.test.context.ActiveProfiles
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import reactor.util.retry.Retry
import java.lang.RuntimeException

@ActiveProfiles("test")
@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores::class)
class OrderOrchestratorByWebclientTest @Autowired constructor(
    val orderOrchestratorByWebclient: OrderOrchestratorByWebclient,
    val redisTemplate: ReactiveRedisTemplate<String, String>,
    val objectMapper: ObjectMapper
) {
    val log = Slf4JLoggerFactory.getInstance(OrderOrchestratorByWebclientTest::class.java)

    @Test
    @DisplayName("[OrderOrchestratorByWebclientTest] workflow 아이디 중복시 retry 테스트")
    fun workflow_아이디_중복시_retry_테스트() {
        log.info("[OrderOrchestratorByWebclientTest] workflow 아이디 중복시 retry 테스트")
        redisTemplate.opsForValue().set("a1", "a")
            .block()
        redisTemplate.opsForValue().set("a2", "a")
            .block()
        redisTemplate.opsForValue().set("a3", "a")
            .block()

        var num = 0
        var id: String = "a"
        Mono.just("a")
            .flatMap { orderEventId ->
                num++
                id = orderEventId + num
                log.info("id= ${id}")
                redisTemplate.hasKey(orderEventId + num)
            }
            .flatMap { hasKey ->
                if (hasKey) {
                    log.info("중복 발생")
                    throw DuplicateKeyException()
//                    throw TestException()
                }

                redisTemplate.opsForValue().set(id, "a")
            }
            .doOnError {
                log.info("[ERROR]")
                log.info(it.toString())
            }
            .retryWhen(Retry.max(5)
                .filter { error ->
                    error is DuplicateKeyException
                }
            )
            .doOnError {
                log.info("[ERROR 초과]")
                log.info(it.toString())
            }
            .`as`(StepVerifier::create)
            .assertNext { isSuccess ->
                Assertions.assertEquals(true, isSuccess)
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("[OrderOrchestratorByWebclientTest] workflow_생성_및_저장_테스트")
    fun workflow_생성_및_저장_테스트() {
        log.info("[OrderOrchestratorByWebclientTest] workflow_생성_및_저장_테스트")

        val productCount = (Math.random() * 10).toInt() + 1
        val orderProducts = mutableListOf<OrderProductDtoForCreation>()

        for (i in 0 until productCount) {
            val productInfoForCreation = OrderProductDtoForCreation(
                i.toString(),
                (Math.random() * 10).toInt(),
                "READY"
            )

            orderProducts.add(productInfoForCreation)
        }

        val orderDtoForCreation = OrderDtoForCreation(
            1000000,
            orderProducts,
            "sender1",
            "요깨비",
            "이재훈",
            "123123123",
            "asdf",
            "asdf",
            "asdf",
            "asdf",
            "asdf",
            "READY",
        )

//        orderOrchestratorByWebclient.order(orderDtoForCreation)
//            .flatMap(redisTemplate.opsForValue()::get)
//            .`as`(StepVerifier::create)
//            .assertNext { paredOrderWorkFlow ->
//                log.info(paredOrderWorkFlow)
//                val orderWorkflow = objectMapper.readValue<Workflow<*, *>>(paredOrderWorkFlow, Workflow::class.java)
//
//                log.info(orderWorkflow.getSteps().toString())
//            }
//            .verifyComplete()

        orderOrchestratorByWebclient.order(orderDtoForCreation)
            .`as`(StepVerifier::create)
            .assertNext { createdOrderDto ->
                log.info(createdOrderDto.toString())
            }
            .verifyComplete()

    }
}