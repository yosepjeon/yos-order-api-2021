package com.yosep.order.order.data.repository

import com.yosep.order.common.data.RandomIdGenerator
import com.yosep.order.common.reactive.Transaction
import com.yosep.order.data.dto.OrderDtoForCreation
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
class OrderRepositoryTest @Autowired constructor(
    private val orderRepository: OrderRepository,
    private val randomIdGenerator: RandomIdGenerator
) {
    val log = Slf4JLoggerFactory.getInstance(OrderRepositoryTest::class.java)
    var orderId = ""

    @BeforeEach
    fun createOrder() {
        val order = Order(
            "test0",
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
//        1. Spring Test에서는 R2dbc관련 @Transactional을 아직 지원하지 않는다.
//        StepVerifier.create(orderRepository.save(order))
//            .expectSubscription()
//            .consumeNextWith {
//                Assertions.assertEquals("create-order-test", it.orderId)
//                log.info("$it")
//                log.info("END")
//            }
//            .verifyComplete()

//        2. map은 동기 방식으로 성능에 문제를 초래 flatMap으로 바꿀 예정
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
    @DisplayName("[OrderRepository] 주문 생성 시 임의로 생성된 주문 아이디가 중복될 경우 해결 테스트")
    fun 주문_생성시_임의로_생성된_주문_아이디가_중복될_경우_해결_테스트() {
        log.info("[OrderRepository] 주문 생성 시 임의로 생성된 주문 아이디가 중복될 경우 해결 테스트")

        val orderDtoForCreation = OrderDtoForCreation(
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
        )

        var num = 0
        randomIdGenerator.generate()
            .flatMap {
                log.info(it)
                val order = Order(
                    "test$num",
                    orderDtoForCreation.productId,
                    orderDtoForCreation.senderId,
                    orderDtoForCreation.senderName,
                    orderDtoForCreation.receiverName,
                    orderDtoForCreation.phone,
                    orderDtoForCreation.postCode,
                    orderDtoForCreation.roadAddr,
                    orderDtoForCreation.jibunAddr,
                    orderDtoForCreation.extraAddr,
                    orderDtoForCreation.detailAddr,
                    orderDtoForCreation.orderState,
                )
                order.setAsNew()
                num++

                Mono.create<Order> { sink ->
                    sink.success(order)
                }
            }
            .flatMap(orderRepository::save)
            .retry()
            .`as`(Transaction::withRollback)
            .subscribe()
    }

    @Test
    @DisplayName("[OrderRepository] 주문 조회 성공 테스트")
    fun 주문_조회_성공_테스트() {
        log.info("[OrderRepository] 주문 조회 성공 테스트")

        StepVerifier.create(orderRepository.findById("test0"))
            .expectSubscription()
            .consumeNextWith {
                Assertions.assertEquals("test0", it.orderId)
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("[OrderRepository] 주문 조회 실패 테스트")
    fun 주문_조회_실패_테스트() {
        log.info("[OrderRepository] 주문 조회 실패 테스트")

        StepVerifier.create(orderRepository.findById("test0"))
            .expectSubscription()
            .consumeNextWith {
                Assertions.assertNotEquals("test!", it.orderId)
            }
            .verifyComplete()
    }
}