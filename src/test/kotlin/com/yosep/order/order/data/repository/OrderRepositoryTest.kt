package com.yosep.order.order.data.repository

import com.yosep.order.common.reactive.Transaction
import com.yosep.order.data.entity.Order
import com.yosep.order.data.repository.OrderRepository
import io.netty.util.internal.logging.Slf4JLoggerFactory
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.Rollback
import org.springframework.test.context.TestExecutionListeners
import org.springframework.transaction.annotation.Transactional
import reactor.test.StepVerifier
import java.time.LocalDateTime

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores::class)
class OrderRepositoryTest @Autowired constructor(
    private val orderRepository: OrderRepository
) {
    val log = Slf4JLoggerFactory.getInstance(OrderRepositoryTest::class.java)

    @Test
    fun readOrderTest() {
//        StepVerifier.create(orderRepository.findById("test"))
//            .expectSubscription()
//            .consumeNextWith {
//                Assertions.assertEquals("test", it.productId)
//            }
//            .verifyComplete()
    }

    @Test
    @DisplayName("[OrderRepository] 주문 생성 테스트")
    fun createOrderTest() {
        val order = Order(
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
            LocalDateTime.now(),
            null,
            null
        )

        order.setAsNew()

        log.info("[OrderRepository] 주문 생성 테스트")
//        StepVerifier.create(orderRepository.save(order))
//            .expectSubscription()
//            .consumeNextWith {
//                Assertions.assertEquals("create-order-test", it.orderId)
//                log.info("$it")
//                log.info("END")
//            }
//            .verifyComplete()

        orderRepository.save(order)
            .map(Order::orderId)
            .flatMap(orderRepository::findById)
            .`as`(Transaction::withRollback)
            .`as`(StepVerifier::create)
            .assertNext { order ->
                Assertions.assertEquals(true, order!!.orderId == "create-order-test")
            }
            .verifyComplete()
    }
}