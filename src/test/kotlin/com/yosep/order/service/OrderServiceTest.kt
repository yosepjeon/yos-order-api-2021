package com.yosep.order.service

import com.yosep.order.common.reactive.Transaction
import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.data.entity.Order
import com.yosep.order.data.repository.OrderRepository
import io.netty.util.internal.logging.Slf4JLoggerFactory
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier
import java.time.LocalDateTime

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores::class)
class OrderServiceTest @Autowired constructor(
    val orderRepository: OrderRepository,
    val orderService: OrderService
) {
    val log = Slf4JLoggerFactory.getInstance(OrderServiceTest::class.java)

    var orderId = ""

    @BeforeEach
    fun createOrder() {
        val order = Order(
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

        orderRepository.save(order)
            .map {
                orderId = it.orderId
            }
            .block()

        log.info("===================================================== START =====================================================")
    }

    @AfterEach
    fun deleteOrder() {
        orderRepository
            .deleteById(orderId)
            .block()

        log.info("===================================================== END =====================================================")
    }

    @Test
    @DisplayName("[OrderService]주문 생성 테스트")
    fun 주문_생성_성공_테스트() {
        log.info("[OrderService]주문 생성 성공 테스트")

        val orderDtoForCreation = OrderDtoForCreation(
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

        orderService.createOrder(orderDtoForCreation)
            .`as`(Transaction::withRollback)
            .`as`(StepVerifier::create)
            .assertNext { order ->
                log.info(order.toString())
                Assertions.assertEquals(true, order != null)
            }
            .verifyComplete()

    }
}