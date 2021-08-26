package com.yosep.order.order.saga.http

import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.data.dto.ProductStepDtoForCreation
import com.yosep.order.data.vo.OrderProductDtoForCreation
import com.yosep.order.saga.http.step.ProductStep
import io.netty.util.internal.logging.Slf4JLoggerFactory
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores::class)
class ProductStepTest @Autowired constructor(
    @Qualifier("product")
    private val productWebclient: WebClient
) {
    val log = Slf4JLoggerFactory.getInstance(ProductStepTest::class.java)

    @Test
    @DisplayName("[StepTest] Product Step 구현 테스트")
    fun product_step_구현_테스트() {
        log.info("[StepTest] Product Step 구현 테스트")

        val productCount = (Math.random() * 10).toInt() + 1
        val orderProducts = mutableListOf<OrderProductDtoForCreation>()

        for (i in 0 until productCount) {
            val productInfoForCreation = OrderProductDtoForCreation(
                i.toString(),
                (Math.random() * 10).toInt(),
                10000,
                "READY"
            )

            orderProducts.add(productInfoForCreation)
        }

        val orderDtoForCreation = OrderDtoForCreation(
            1000000,
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

        val productStepDtoForCreation= ProductStepDtoForCreation(
            orderDtoForCreation.totalPrice,
            orderDtoForCreation.orderProductDtos
        )

        val productStep = ProductStep(productWebclient, productStepDtoForCreation)
        productStep.process()
            .`as`(StepVerifier::create)
            .assertNext {
                log.info("before: $productStepDtoForCreation")
                log.info("after: $it")
                Assertions.assertEquals(false, it.equals(productStepDtoForCreation))
            }
            .verifyComplete()
    }
}