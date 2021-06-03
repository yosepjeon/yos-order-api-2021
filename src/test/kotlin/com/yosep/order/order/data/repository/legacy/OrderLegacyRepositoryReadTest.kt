package com.yosep.order.order.data.repository.legacy

import com.yosep.order.data.entity.OrderLegacy
import com.yosep.order.data.repository.OrderLegacyRepository
import io.netty.util.internal.logging.Slf4JLoggerFactory
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier
import java.time.LocalDateTime

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores::class)
class OrderLegacyRepositoryReadTest @Autowired constructor(
    private val orderLegacyRepository: OrderLegacyRepository,
) {
    val log = Slf4JLoggerFactory.getInstance(OrderLegacyRepositoryCreationTest::class.java)
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
        orderLegacyRepository
            .deleteAll()
            .block()

        log.info("===================================================== END =====================================================")
    }

    @Test
    @DisplayName("[OrderRepository] 주문 조회 성공 테스트")
    fun 주문_조회_성공_테스트() {
        log.info("[OrderRepository] 주문 조회 성공 테스트")

        StepVerifier.create(orderLegacyRepository.findById("test-0-0"))
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

        StepVerifier.create(orderLegacyRepository.findById("test-0").defaultIfEmpty(OrderLegacy()))
            .consumeNextWith { order ->
                log.info(order.toString())
                Assertions.assertEquals(true, order.orderId == "")
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("[OrderRepository] 읽기전용 유저 별 주문 조회 성공 테스트")
    fun 유저별_주문_조회_성공_테스트() {
        log.info("[OrderRepository] 읽기전용 유저 별 주문 조회 성공 테스트")

        StepVerifier.create(orderLegacyRepository.findOrdersBySenderId("sender1"))
            .thenConsumeWhile { order ->
                log.info(order.toString())
                order != null
            }
            .verifyComplete()
    }

    fun createOrders(senderNum: Int) {
        for(i in 0..20) {
            val order = OrderLegacy(
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

            orderLegacyRepository.save(order)
                .map {
                    orderId = it.orderId
                }
                .block()
        }
    }

}