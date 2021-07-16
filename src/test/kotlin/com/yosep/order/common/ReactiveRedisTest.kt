package com.yosep.order.common

import com.fasterxml.jackson.databind.ObjectMapper
import com.yosep.order.common.data.RandomIdGenerator
import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.data.vo.OrderProductDtoForCreation
import com.yosep.order.saga.http.step.OrderStep
import com.yosep.order.service.OrderService
import io.netty.util.internal.logging.Slf4JLoggerFactory
import org.junit.jupiter.api.*
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.Range
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.test.context.ActiveProfiles
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.test.StepVerifier
import java.sql.Timestamp
import java.time.LocalDateTime

@ActiveProfiles("test")
@SpringBootTest
@DisplayNameGeneration(ReplaceUnderscores::class)
class ReactiveRedisTest @Autowired constructor(
    val objectMapper: ObjectMapper,
    val orderService: OrderService,
    val randomIdGenerator: RandomIdGenerator,
    val redisTemplate: ReactiveRedisTemplate<String, String>
) {
    val log = Slf4JLoggerFactory.getInstance(ReactiveRedisTest::class.java)

    @BeforeEach
    fun drawLineByTestBefore() {
        log.info("===================================================== START =====================================================")
    }

    @AfterEach
    fun drawLineByTestAfter() {
        log.info("===================================================== END =====================================================")
    }

    @Test
    @DisplayName("[Reactive Redis] 정수 값 넣고빼기 성공 테스트")
    fun 정수_값_넣고빼기_성공_테스트() {
        log.info("[Reactive Redis] 정수 값 넣기 성공 테스트")
        val valueOps = redisTemplate!!.opsForValue()
        StepVerifier.create(
            valueOps.set("a", "1")
                .flatMap { result: Boolean? ->
                    valueOps.get("a")
                })
            .assertNext { result: String ->
                log.info("result: $result")
                Assertions.assertEquals("1", result)
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("[Reactive Redis] 정수 값 변경 성공 테스트")
    fun 정수_값_변경_성공_테스트() {
        log.info("[Reactive Redis] 정수 값 변경 성공 테스트")
        val valueOps = redisTemplate!!.opsForValue()
        StepVerifier.create(
            valueOps.set("a", "1")
                .flatMap { result: Boolean? ->
                    valueOps.increment("a", 3)
                })
            .assertNext { result ->
                log.info("result: $result")
                Assertions.assertEquals(4, result)
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("[Reactive Redis] List 조회 테스트")
    fun List_조회_테스트() {
        log.info("[Reactive Redis] List 조회 테스트")
        val listOps = redisTemplate!!.opsForList()
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

        val orderStep = OrderStep(orderService, randomIdGenerator, "READY")
        orderStep.process(orderDtoForCreation)

    }

    @Test
    @DisplayName("[Reactive Redis] Sorted Set 조회 테스트")
    fun SortedSet_조회_테스트() {
        log.info("[Reactive Redis] Sorted Set 조회 테스트")
        val zSetOps = redisTemplate!!.opsForZSet()

        for (i in 0..5) {
            val t1 = Timestamp.valueOf(LocalDateTime.now())
                .toString()
                .replace("[- :]".toRegex(), "")
                .toDouble()

            zSetOps.add("test-event1", "event$i", t1).block()
        }

        StepVerifier.create(zSetOps.range("test-event1", Range.closed(0, 10)).collectList())
            .assertNext {
                log.info("size: ${it.size}")
            }
            .verifyComplete()


    }
}