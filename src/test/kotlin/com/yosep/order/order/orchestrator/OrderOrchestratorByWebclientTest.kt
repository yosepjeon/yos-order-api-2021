package com.yosep.order.order.orchestrator

import com.fasterxml.jackson.databind.ObjectMapper
import com.yosep.order.common.exception.DuplicateKeyException
import com.yosep.order.common.exception.TestException
import com.yosep.order.common.reactive.Transaction
import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.data.vo.OrderProductDiscountCouponDto
import com.yosep.order.data.vo.OrderProductDtoForCreation
import com.yosep.order.data.vo.OrderTotalDiscountCouponDto
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
    fun workflow_생성_및_저장_테스트1() {
        log.info("[OrderOrchestratorByWebclientTest] workflow_생성_및_저장_테스트")

        val productCount = (Math.random() * 5).toInt() + 1

        val orderProducts = mutableListOf<OrderProductDtoForCreation>()
        for (i in 0 until productCount) {
            val productInfoForCreation = OrderProductDtoForCreation(
                "test-product-category1-$i",
                i+1,
                (((i + 1) * 111000).toLong()),
                "READY"
            )

            orderProducts.add(productInfoForCreation)
        }

        val orderProductDiscountCouponDtos = mutableListOf<OrderProductDiscountCouponDto>()
        for (i in 1..2) {
            val orderProductDiscountCouponDto = OrderProductDiscountCouponDto(
                "PRODUCT",
                "own-product-amount-coupon-test$i",
                1L * i,
                "user-admin-for-test",
                10000,
                0,
                "test-product-category1-$i",
                i * 111000L,
                0,
                "READY"
            )

            orderProductDiscountCouponDtos.add(orderProductDiscountCouponDto)
        }

        val orderTotalDiscountCouponDtos = mutableListOf<OrderTotalDiscountCouponDto>()
        for(i in 1..2) {
            val orderTotalDiscountCouponDto = OrderTotalDiscountCouponDto(
                "TOTAL",
                "own-total-amount-coupon-test$i",
                "user-admin-for-test",
                10000,
                0,
                "READY"
            )

            orderTotalDiscountCouponDtos.add(orderTotalDiscountCouponDto)
        }

        val orderDtoForCreation = OrderDtoForCreation(
            1000000,
            orderProducts,
            orderProductDiscountCouponDtos,
            orderTotalDiscountCouponDtos,
            "user-admin-for-test",
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

        orderOrchestratorByWebclient.order(orderDtoForCreation)
            .`as`(Transaction::withRollback)
            .`as`(StepVerifier::create)
            .assertNext { createdOrderDto ->
                log.info("최종 결과")
                log.info(createdOrderDto.toString())
            }
            .verifyComplete()

    }

    @Test
    @DisplayName("[OrderOrchestratorByWebclientTest] workflow_생성_및_저장_테스트")
    fun workflow_생성_및_저장_테스트2() {
        log.info("[OrderOrchestratorByWebclientTest] workflow_생성_및_저장_테스트")

        val productCount = (Math.random() * 5).toInt() + 1

        val orderProducts = mutableListOf<OrderProductDtoForCreation>()
        for (i in 0 until productCount) {
            val productInfoForCreation = OrderProductDtoForCreation(
                "test-product-category1-$i",
                i+1,
                (((i + 1) * 111000).toLong()),
                "READY"
            )

            orderProducts.add(productInfoForCreation)
        }

        val orderProductDiscountCouponDtos = mutableListOf<OrderProductDiscountCouponDto>()
        for (i in 1..2) {
            val orderProductDiscountCouponDto = OrderProductDiscountCouponDto(
                "PRODUCT",
                "own-product-amount-coupon-test$i",
                1L * i,
                "user-admin-for-test",
                10000,
                0,
                "test-product-category1-$i",
                i * 111000L,
                0,
                "READY"
            )

            orderProductDiscountCouponDtos.add(orderProductDiscountCouponDto)
        }

        val orderTotalDiscountCouponDtos = mutableListOf<OrderTotalDiscountCouponDto>()
        for(i in 1..1) {
            val orderTotalDiscountCouponDto = OrderTotalDiscountCouponDto(
                "TOTAL",
                "own-total-percent-coupon-test$i",
                "user-admin-for-test",
                0,
                i * 10L,
                "READY"
            )

            orderTotalDiscountCouponDtos.add(orderTotalDiscountCouponDto)
        }

        val orderDtoForCreation = OrderDtoForCreation(
            1000000,
            orderProducts,
            orderProductDiscountCouponDtos,
            orderTotalDiscountCouponDtos,
            "user-admin-for-test",
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

        orderOrchestratorByWebclient.order(orderDtoForCreation)
            .`as`(Transaction::withRollback)
            .`as`(StepVerifier::create)
            .assertNext { createdOrderDto ->
                log.info("최종 결과")
                log.info(createdOrderDto.toString())
            }
            .verifyComplete()

    }

    @Test
    @DisplayName("[OrderOrchestratorByWebclientTest] workflow_생성_및_저장_테스트")
    fun workflow_생성_및_저장_테스트3() {
        log.info("[OrderOrchestratorByWebclientTest] workflow_생성_및_저장_테스트")

        val productCount = (Math.random() * 5).toInt() + 1

        val orderProducts = mutableListOf<OrderProductDtoForCreation>()
        for (i in 0 until productCount) {
            val productInfoForCreation = OrderProductDtoForCreation(
                "test-product-category1-$i",
                i+1,
                (((i + 1) * 111000).toLong()),
                "READY"
            )

            orderProducts.add(productInfoForCreation)
        }

        val orderProductDiscountCouponDtos = mutableListOf<OrderProductDiscountCouponDto>()
        for (i in 1..2) {
            val orderProductDiscountCouponDto = OrderProductDiscountCouponDto(
                "PRODUCT",
                "own-product-amount-coupon-test$i",
                1L * i,
                "user-admin-for-test",
                10000,
                0,
                "test-product-category1-$i",
                i * 111000L,
                0,
                "READY"
            )

            orderProductDiscountCouponDtos.add(orderProductDiscountCouponDto)
        }

        val orderTotalDiscountCouponDtos = mutableListOf<OrderTotalDiscountCouponDto>()
        for(i in 1..2) {
            val orderTotalDiscountCouponDto = OrderTotalDiscountCouponDto(
                "TOTAL",
                "own-total-amount-coupon-test$i",
                "user-admin-for-test",
                10000,
                0,
                "READY"
            )

            orderTotalDiscountCouponDtos.add(orderTotalDiscountCouponDto)
        }

        val orderDtoForCreation = OrderDtoForCreation(
            1000000,
            orderProducts,
            orderProductDiscountCouponDtos,
            orderTotalDiscountCouponDtos,
            "user-admin-for-test",
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

        orderOrchestratorByWebclient.order(orderDtoForCreation)
            .`as`(Transaction::withRollback)
            .`as`(StepVerifier::create)
            .assertNext { createdOrderDto ->
                log.info("최종 결과")
                log.info(createdOrderDto.toString())
            }
            .verifyComplete()

    }
}