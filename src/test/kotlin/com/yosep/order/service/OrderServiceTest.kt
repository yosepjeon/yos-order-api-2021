package com.yosep.order.service

import com.yosep.order.common.OrderGeneratorForTest
import com.yosep.order.common.reactive.Transaction
import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.data.vo.OrderProductDtoForCreation
import io.netty.util.internal.logging.Slf4JLoggerFactory
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores::class)
class OrderServiceTest @Autowired constructor(
    val orderGeneratorForTest: OrderGeneratorForTest,
    val orderService: OrderService
) {
    val log = Slf4JLoggerFactory.getInstance(OrderLegacyServiceTest::class.java)

    @BeforeEach
    fun printStart() {
        log.info("===================================================== START =====================================================")
    }

    @AfterEach
    fun printEnd() {
        log.info("===================================================== END =====================================================")
    }

    @Test
    @DisplayName("[OrderService]주문 생성 테스트")
    fun 주문_생성_테스트() {
        log.info("[OrderService]주문 생성 테스트")
        val productCount = (Math.random() * 10).toInt() + 1
        val orderProducts = mutableListOf<OrderProductDtoForCreation>()

        for (i in 0 until productCount) {
            val productInfoForCreation = OrderProductDtoForCreation(
                i.toString(),
                (Math.random() * 10).toInt()
            )

            orderProducts.add(productInfoForCreation)
        }

        val orderDtoForCreation = OrderDtoForCreation(
            "create-order-test",
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

        orderService.createOrder(orderDtoForCreation)
            .`as`(Transaction::withRollback)
            .`as`(StepVerifier::create)
            .assertNext { createdOrderDto ->
                log.info("[연산 완료]")
                log.info(createdOrderDto.toString())
                Assertions.assertEquals(true, createdOrderDto!!.orderProducts.isNotEmpty())
                Assertions.assertEquals(true, createdOrderDto!!.orderProducts.size == orderProducts.size)
            }
            .verifyComplete()
    }
}