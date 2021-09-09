package com.yosep.order.order.mq

import com.fasterxml.jackson.databind.ObjectMapper
import com.yosep.order.event.saga.revert.RevertProductDiscountCouponStepEvent
import com.yosep.order.event.saga.revert.RevertProductStepEvent
import com.yosep.order.event.saga.revert.RevertTotalDiscountCouponStepEvent
import com.yosep.order.mq.producer.OrderToCouponProducer
import com.yosep.order.mq.producer.OrderToProductProducer
import io.netty.util.internal.logging.Slf4JLoggerFactory
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.DisplayNameGeneration
import org.junit.jupiter.api.DisplayNameGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import reactor.test.StepVerifier

@ActiveProfiles("test")
@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores::class)
class ProducerTest @Autowired constructor(
    val orderToProductProducer: OrderToProductProducer,
    val orderToCouponProducer: OrderToCouponProducer,

    val objectMapper: ObjectMapper
) {
    val log = Slf4JLoggerFactory.getInstance(ProducerTest::class.java)

    @Test
    @DisplayName("[OrderToProductProducerTest] OrderToProductProducer 상품 revert test")
    fun orderToProductProducer_상품_revert_test() {
        log.info("[OrderToProductProducerTest] OrderToProductProducer 상품 revert test")

        val revertProductStepEvent = RevertProductStepEvent(
            "orderId",
            emptyList()
        )
        orderToProductProducer.publishRevertProductEvent(revertProductStepEvent)
            .`as`(StepVerifier::create)
            .assertNext {
                log.info("$it")
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("[OrderToProductDiscountCouponProducerTest] OrderToProductDiscountCouponProducer revert test")
    fun orderToProductDiscountCouponProducer_revert_test() {
        log.info("[OrderToProductDiscountCouponProducerTest] OrderToProductDiscountCouponProducer revert test")

        val revertProductDiscountCouponStepEvent = RevertProductDiscountCouponStepEvent(
            "orderId",
            emptyList()
        )
        orderToCouponProducer.publishRevertProductDiscountCouponEvent(revertProductDiscountCouponStepEvent)
            .`as`(StepVerifier::create)
            .assertNext {
                log.info("$it")
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("[OrderToTotalDiscountCouponProducerTest] OrderToTotalDiscountCouponProducer revert test")
    fun orderToTotalDiscountCouponProducer_revert_test() {
        log.info("[OrderToTotalDiscountCouponProducerTest] OrderToTotalDiscountCouponProducer revert test")
        val revertTotalDiscountCouponStepEvent = RevertTotalDiscountCouponStepEvent(
            "orderId",
            emptyList()
        )

        orderToCouponProducer.publishRevertTotalDiscountCouponEvent(revertTotalDiscountCouponStepEvent)
            .`as`(StepVerifier::create)
            .assertNext {
                log.info("$it")
            }
            .verifyComplete()
    }
}