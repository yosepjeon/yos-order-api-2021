package com.yosep.order.order.saga.http

import com.yosep.order.data.dto.OrderProductDiscountCouponStepDto
import com.yosep.order.data.vo.OrderProductDiscountCouponDto
import com.yosep.order.mq.producer.OrderToCouponProducer
import com.yosep.order.mq.producer.OrderToProductCouponProducer
import com.yosep.order.mq.producer.OrderToTotalCouponProducer
import com.yosep.order.saga.http.step.ProductDiscountCouponStep
import io.netty.util.internal.logging.Slf4JLoggerFactory
import org.junit.jupiter.api.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores::class)
class ProductDiscountCouponStepTest @Autowired constructor(
    @Qualifier("coupon")
    private val couponWebclient: WebClient,
    private val orderToCouponProducer: OrderToCouponProducer,
    private val orderToProductCouponProducer: OrderToProductCouponProducer,
) {
    val log = Slf4JLoggerFactory.getInstance(ProductDiscountCouponStepTest::class.java)

    @Test
    @DisplayName("[Product Discount Coupon Step Test] Coupon Step 구현 성공 테스트")
    fun product_discount_coupon_step_구현_성공_테스트() {
        log.info("[Product Discount Coupon Step Test] Coupon Step 구현 성공 테스트")
        val orderProductDiscountCouponDtos = mutableListOf<OrderProductDiscountCouponDto>()

        // 금액 할인 쿠폰 사용
        for (i in 1..2) {
            val orderProductDiscountCouponDto = OrderProductDiscountCouponDto(
                "PRODUCT",
                "own-product-amount-coupon-test$i",
                1L * i,
                "user-admin-for-test",
                10000,
                0,
                "test-product-category1-$i",
                i * 111000L,
                0,
                "READY"
            )

            orderProductDiscountCouponDtos.add(orderProductDiscountCouponDto)
        }


        val orderProductDiscountCouponStepDto = OrderProductDiscountCouponStepDto(
            "",
            orderProductDiscountCouponDtos,
            "READY"
        )

        val productDiscountCouponStep = ProductDiscountCouponStep(
            couponWebclient,
            orderToProductCouponProducer,
            orderProductDiscountCouponStepDto,
            "PRODUCT"
        )

        productDiscountCouponStep.process()
            .`as`(StepVerifier::create)
            .assertNext {
                log.info("before: $orderProductDiscountCouponStepDto")
                log.info("after: $it")
            }
            .verifyComplete()
    }
}