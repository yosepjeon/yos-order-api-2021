package com.yosep.order.saga.http.step

import com.fasterxml.jackson.annotation.JsonIgnore
import com.yosep.order.data.dto.OrderTotalDiscountCouponStepDto
import com.yosep.order.event.saga.revert.RevertTotalDiscountCouponStepEvent
import com.yosep.order.mq.producer.OrderToCouponProducer
import com.yosep.order.mq.producer.OrderToTotalCouponProducer
import com.yosep.order.saga.http.WorkflowStep
import com.yosep.order.saga.http.annotation.SagaStep
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

class TotalDiscountCouponStep(
    @JsonIgnore
    private val webClient: WebClient? = null,
    @JsonIgnore
    @Autowired
    private var orderToCouponProducer: OrderToTotalCouponProducer? = null,
    var orderTotalDiscountCouponStepDto: OrderTotalDiscountCouponStepDto,
    stepType: String = "TOTAL-DISCOUNT-COUPON",
    state: String = "READY"
) : WorkflowStep<OrderTotalDiscountCouponStepDto>(
    stepType, state
) {
    @SagaStep
    override fun process(): Mono<OrderTotalDiscountCouponStepDto> {
        this.state = "TD_COUPON_STEP_PENDING"
        this.orderTotalDiscountCouponStepDto.state = "PENDING"


        return webClient!!
            .post()
            .uri("/total/order-saga-total-coupon")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(orderTotalDiscountCouponStepDto)
            .retrieve()
            .bodyToMono(OrderTotalDiscountCouponStepDto::class.java)
            .flatMap { orderTotalDiscountCouponStepDto ->
                this.orderTotalDiscountCouponStepDto = orderTotalDiscountCouponStepDto

                if (this.orderTotalDiscountCouponStepDto.state == "COMP") {
                    this.state = "TD_COUPON_STEP_COMP"
                } else {
                    this.state = "TD_COUPON_STEP_FAIL"
                    throw RuntimeException(this.state)
                }

//                println("[Total Discount Coupon Step]")
//                println(orderTotalDiscountCouponStepDto)

                Mono.create<OrderTotalDiscountCouponStepDto> {
                    it.success(orderTotalDiscountCouponStepDto)
                }
            }
            .doOnError {
                throw RuntimeException("${it.message}")
            }
    }

    override fun revert(orderId: String): Mono<Any> {
        println("call total discount counpon step revert()")
        val revertTotalDiscountCouponStepEvent = RevertTotalDiscountCouponStepEvent(
            orderId,
            orderTotalDiscountCouponStepDto.orderTotalDiscountCouponDtos
        )

        return orderToCouponProducer!!.publishRevertSagaStepEvent(revertTotalDiscountCouponStepEvent)
    }

    private fun checkProductPrices() {

    }
}