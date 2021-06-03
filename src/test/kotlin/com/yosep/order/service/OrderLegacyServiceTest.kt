package com.yosep.order.service

import com.yosep.order.common.reactive.Transaction
import com.yosep.order.data.dto.OrderDtoForCreationLegacy
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
class OrderLegacyServiceTest @Autowired constructor(
    val orderLegacyRepository: OrderLegacyRepository,
    val orderLegacyService: OrderLegacyService
) {
    val log = Slf4JLoggerFactory.getInstance(OrderLegacyServiceTest::class.java)

    var orderId = ""

    @BeforeEach
    fun createOrder() {
        val order = OrderLegacy(
            "test",
            "product-01",
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
    @DisplayName("[OrderService]주문 생성 테스트")
    fun 주문_생성_성공_테스트() {
        log.info("[OrderService]주문 생성 성공 테스트")

        val orderDtoForCreation = OrderDtoForCreationLegacy(
            "create-order-test",
            "product-01",
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

        orderLegacyService.createOrder(orderDtoForCreation)
            .`as`(Transaction::withRollback)
            .`as`(StepVerifier::create)
            .assertNext { order ->
                log.info(order.toString())
                Assertions.assertEquals(true, order != null)
            }
            .verifyComplete()

    }

    @Test
    @DisplayName("읽기전용 사용자별 주문 조회_테스트")
    fun 읽기전용_사용자별_주문_조회_테스트() {
        log.info("읽기전용 사용자별 주문 조회_테스트")

        StepVerifier
            .create(orderLegacyService.readOnlyOrderBySenderId("sender1"))
            .expectSubscription()
            .consumeNextWith { orderList ->
                log.info(orderList.toString())
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