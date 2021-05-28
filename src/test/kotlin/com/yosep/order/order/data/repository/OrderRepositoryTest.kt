package com.yosep.order.order.data.repository

import com.yosep.order.common.reactive.Transaction
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
class OrderRepositoryTest @Autowired constructor(
    private val orderRepository: OrderRepository
) {
    val log = Slf4JLoggerFactory.getInstance(OrderRepositoryTest::class.java)
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
    @DisplayName("[OrderRepository] 주문 생성 성공 테스트")
    fun createOrderSuccessTest() {
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

        log.info("[OrderRepository] 주문 생성 성공 테스트")
//        Spring Test에서는 R2dbc관련 @Transactional을 아직 지원하지 않는다.
//        StepVerifier.create(orderRepository.save(order))
//            .expectSubscription()
//            .consumeNextWith {
//                Assertions.assertEquals("create-order-test", it.orderId)
//                log.info("$it")
//                log.info("END")
//            }
//            .verifyComplete()
//        orderRepository.save(order)
//            .map(Order::orderId)
//            .flatMap(orderRepository::findById)
//            .`as`(Transaction::withRollback)
//            .`as`(StepVerifier::create)
//            .assertNext { order ->
//                Assertions.assertEquals(true, order!!.orderId == "create-order-test")
//            }
//            .verifyComplete()

        orderRepository.save(order)
            .flatMap {
                orderRepository.findById(it.orderId)
            }
            .`as`(Transaction::withRollback)
            .`as`(StepVerifier::create)
            .assertNext { order ->
                Assertions.assertEquals(true, order!!.orderId == "create-order-test")
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("[OrderRepository] 주문 생성 실패 테스트")
    fun createOrderFailTest() {
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

        log.info("[OrderRepository] 주문 생성 실패 테스트")

        orderRepository.save(order)
            .flatMap {
                orderRepository.findById(it.orderId)
            }
            .`as`(Transaction::withRollback)
            .`as`(StepVerifier::create)
            .assertNext { order ->
                Assertions.assertEquals(false, order!!.orderId == "create-order-test1")
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("주문 조회 성공 테스트")
    fun readOrderSuccessTest() {
        log.info("[OrderRepository] 주문 조회 성공 테스트")

        StepVerifier.create(orderRepository.findById("test"))
            .expectSubscription()
            .consumeNextWith {
                Assertions.assertEquals("test", it.orderId)
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("주문 조회 실패 테스트")
    fun readOrderFailTest() {
        log.info("[OrderRepository] 주문 조회 실패 테스트")

        StepVerifier.create(orderRepository.findById("test"))
            .expectSubscription()
            .consumeNextWith {
                Assertions.assertNotEquals("test!", it.orderId)
            }
            .verifyComplete()
    }
}