package com.yosep.order.order.saga.http

import com.yosep.order.data.dto.OrderTotalDiscountCouponStepDto
import com.yosep.order.data.vo.OrderTotalDiscountCouponDto
import com.yosep.order.mq.producer.OrderToCouponProducer
import com.yosep.order.saga.http.step.TotalDiscountCouponStep
import io.netty.util.internal.logging.Slf4JLoggerFactory
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.DisplayNameGeneration
import org.junit.jupiter.api.DisplayNameGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.web.reactive.function.client.WebClient
import reactor.test.StepVerifier

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores::class)
class TotalDiscountCouponStepTest @Autowired constructor(
    @Qualifier("coupon")
    private val couponWebclient: WebClient,
    private val orderToCouponProducer: OrderToCouponProducer
) {
    val log = Slf4JLoggerFactory.getInstance(TotalDiscountCouponStepTest::class.java)

    @Test
    @DisplayName("[Total Discount Coupon Step Test] Coupon Step 구현 성공 테스트")
    fun total_discount_coupon_step_구현_성공_테스트() {
        log.info("[Total Discount Coupon Step Test] Coupon Step 구현 성공 테스트")

        val orderTotalDiscountCouponDtos = mutableListOf<OrderTotalDiscountCouponDto>()
        for(i in 1..2) {
            val orderTotalDiscountCouponDto = OrderTotalDiscountCouponDto(
                "TOTAL",
                "own-total-amount-coupon-test$i",
                "user-admin-for-test",
                10000,
                0,
                "READY"
            )

            orderTotalDiscountCouponDtos.add(orderTotalDiscountCouponDto)
        }

        val orderTotalDiscountCouponStepDto = OrderTotalDiscountCouponStepDto(
            "",
            100000,
            orderTotalDiscountCouponDtos,
            0,
            "READY"
        )

        val totalDiscountCouponStep = TotalDiscountCouponStep(
            couponWebclient,
            orderToCouponProducer,
            orderTotalDiscountCouponStepDto,
            "TOTAL-DISCOUNT-COUPON",
            "READY"
        )

        totalDiscountCouponStep.process()
            .`as`(StepVerifier::create)
            .assertNext {
                log.info("before: $orderTotalDiscountCouponStepDto")
                log.info("after: $it")
            }
            .verifyComplete()
    }
}