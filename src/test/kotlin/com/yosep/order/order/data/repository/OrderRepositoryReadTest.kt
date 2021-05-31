package com.yosep.order.order.data.repository

import com.yosep.order.data.entity.Order
import com.yosep.order.data.repository.OrderRepository
import io.netty.util.internal.logging.Slf4JLoggerFactory
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDateTime

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores::class)
class OrderRepositoryReadTest @Autowired constructor(
    private val orderRepository: OrderRepository,
) {
    val log = Slf4JLoggerFactory.getInstance(OrderRepositoryCreateTest::class.java)
    var orderId = ""

    @BeforeEach
    fun createOrder() {

        for(i in 0 until 5) {
            createOrders(i)
        }

        log.info("===================================================== START =====================================================")
    }

    @AfterEach
    fun deleteOrder() {
        orderRepository
            .deleteAll()
            .block()

        log.info("===================================================== END =====================================================")
    }

    @Test
    @DisplayName("[OrderRepository] 주문 조회 성공 테스트")
    fun 주문_조회_성공_테스트() {
        log.info("[OrderRepository] 주문 조회 성공 테스트")

        StepVerifier.create(orderRepository.findById("test-0-0"))
            .expectSubscription()
            .consumeNextWith { order ->
                log.info(order.toString())
                Assertions.assertEquals(true, order != null)
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("[OrderRepository] 주문 조회 실패 테스트")
    fun 주문_조회_실패_테스트() {
        log.info("[OrderRepository] 주문 조회 실패 테스트")

        StepVerifier.create(orderRepository.findById("test-0").defaultIfEmpty(Order()))
            .consumeNextWith { order ->
                log.info(order.toString())
                Assertions.assertEquals(true, order.orderId == "")
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("[OrderRepository] 유저 별 주문 조회 성공 테스트")
    fun 유저별_주문_조회_성공_테스트() {
        log.info("[OrderRepository] 유저 별 주문 조회 성공 테스트")

        StepVerifier.create(orderRepository.findOrderBySenderId("sender1"))
            .thenConsumeWhile { order ->
                log.info(order.toString())
                order != null
            }
            .verifyComplete()
    }

    fun createOrders(senderNum: Int) {
        for(i in 0..20) {
            val order = Order(
                "test-$senderNum-$i",
                "product-01",
                "sender$senderNum",
                "요깨비",
                "이재훈",
                "123123123",
                "asdf",
                "asdf",
                "asdf",
                "asdf",
                "asdf",
                "READY",
                LocalDateTime.now(),
                null,
                null
            )

            order.setAsNew()

            orderRepository.save(order)
                .map {
                    orderId = it.orderId
                }
                .block()
        }
    }

}