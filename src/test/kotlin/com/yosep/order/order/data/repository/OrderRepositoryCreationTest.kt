package com.yosep.order.order.data.repository

import com.yosep.order.common.OrderGeneratorForTest
import com.yosep.order.common.data.RandomIdGenerator
import com.yosep.order.common.reactive.Transaction
import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.data.entity.Order
import com.yosep.order.data.entity.OrderProduct
import com.yosep.order.data.enum.OrderProductState
import com.yosep.order.data.repository.OrderProductRepository
import com.yosep.order.data.repository.OrderRepository
import com.yosep.order.data.vo.OrderProductDtoForCreation
import io.netty.util.internal.logging.Slf4JLoggerFactory
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import java.time.LocalDateTime

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores::class)
class OrderRepositoryCreationTest @Autowired constructor(
    private val orderRepository: OrderRepository,
    private val orderProductRepository: OrderProductRepository,
    private val randomIdGenerator: RandomIdGenerator,
    private val orderGeneratorForTest: OrderGeneratorForTest
) {
    val log = Slf4JLoggerFactory.getInstance(OrderRepositoryCreationTest::class.java)
    var orderId = ""

    @BeforeEach
    fun createOrder() {

        log.info("===================================================== START =====================================================")
    }

    @AfterEach
    fun deleteOrder() {
        orderRepository
            .deleteAll()
            .block()

        orderProductRepository
            .deleteAll()
            .block()

        log.info("===================================================== END =====================================================")
    }

//    @Test
//    fun a() {
//        orderRepository.deleteAll().block();
//    }

    @Test
    @DisplayName("[OrderRepository] 주문 생성 성공 테스트")
    fun 주문_생성_성공_테스트(){
        log.info("[OrderRepository] 주문 생성 성공 테스트")
        val orderProducts = mutableListOf<OrderProductDtoForCreation>()

        val orderDtoForCreation = OrderDtoForCreation(
            10000000,
            orderProducts,
            emptyList(),
            emptyList(),
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

        randomIdGenerator.generate()
            .flatMap { orderId ->
                val order = Order(
                    orderId,
                    orderDtoForCreation.userId,
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

                Mono.create<Order> { sink ->
                    sink.success(order)
                }
            }
            .flatMap(orderRepository::save)
            .`as`(Transaction::withRollback)
            .`as`(StepVerifier::create)
            .assertNext { order ->
                log.info("[연산 완료]")
                log.info(order.toString())
                Assertions.assertEquals(true, order!!.userId == orderDtoForCreation.userId)
            }
            .verifyComplete()
    }

//    @Test
//    @DisplayName("[OrderRepository] 주문상품 생성 성공 테스트")
//    fun 주문상품_생성_성공_테스트() {
//        log.info("[OrderRepository] 주문상품 생성 성공 테스트")
//
//        val orderDtoForCreation = OrderDtoForCreation(
//            "create-order-test",
//            listOf(),
//            "sender1",
//            "요깨비",
//            "이재훈",
//            "123123123",
//            "asdf",
//            "asdf",
//            "asdf",
//            "asdf",
//            "asdf",
//            "READY",
//        )
//
//        randomIdGenerator.generate()
//            .flatMap { orderId ->
//                val order = Order(
//                    orderId,
//                    orderDtoForCreation.userId,
//                    orderDtoForCreation.senderName,
//                    orderDtoForCreation.receiverName,
//                    orderDtoForCreation.phone,
//                    orderDtoForCreation.postCode,
//                    orderDtoForCreation.roadAddr,
//                    orderDtoForCreation.jibunAddr,
//                    orderDtoForCreation.extraAddr,
//                    orderDtoForCreation.detailAddr,
//                    orderDtoForCreation.orderState,
//                )
//                order.setAsNew()
//
//                Mono.create<Order> { sink ->
//                    sink.success(order)
//                }
//            }
//            .flatMap(orderRepository::save)
//            .flatMap { order ->
//                val orderProducts = mutableListOf<OrderProduct>()
//                val productCount = (Math.random() * 10).toInt() + 1
////                val orderId = "order-product-test"
//                for (i in 0 until productCount) {
//                    val orderProduct = OrderProduct(
//                        order.orderId + i,
//                        order.orderId,
//                        "product",
//                        productCount,
//                        OrderProductState.READY.value
//                    )
//                    orderProduct.setAsNew()
//
//                    orderProducts.add(orderProduct)
//                }
//
//                orderProductRepository.saveAll(orderProducts.asIterable())
//                    .collectList()
//            }
//            .`as`(Transaction::withRollback)
//            .`as`(StepVerifier::create)
//            .assertNext { orderProducts ->
//                log.info(orderProducts.toString())
//                Assertions.assertEquals(true, orderProducts.size > 0)
//            }
//            .verifyComplete()
//    }
}