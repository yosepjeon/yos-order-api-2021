package com.yosep.order.saga.http.step

import com.fasterxml.jackson.annotation.JsonIgnore
import com.yosep.order.data.dto.OrderProductDiscountCouponStepDto
import com.yosep.order.event.saga.revert.RevertProductDiscountCouponStepEvent
import com.yosep.order.mq.producer.OrderToCouponProducer
import com.yosep.order.mq.producer.OrderToProductCouponProducer
import com.yosep.order.saga.http.WorkflowStep
import com.yosep.order.saga.http.annotation.SagaStep
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

class ProductDiscountCouponStep(
    @JsonIgnore
    private val webClient: WebClient? = null,
    @JsonIgnore
    @Autowired
    private var orderToCouponProducer: OrderToProductCouponProducer? = null,
    var orderProductDiscountCouponStepDto: OrderProductDiscountCouponStepDto,
    stepType: String = "PRODUCT-DISCOUNT-COUPON",
    state: String = "READY"
) : WorkflowStep<OrderProductDiscountCouponStepDto>(
    stepType,
    state
) {
    @SagaStep
    override fun process(): Mono<OrderProductDiscountCouponStepDto> {
        this.state = "PD_COUPON_STEP_PENDING"
        this.orderProductDiscountCouponStepDto.state = "PENDING"

        return webClient!!
            .post()
            .uri("/product/order-saga-product-coupon")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(orderProductDiscountCouponStepDto)
            .retrieve()
            .bodyToMono(OrderProductDiscountCouponStepDto::class.java)
            .flatMap { orderProductDiscountCouponStepDto ->
                this.orderProductDiscountCouponStepDto = orderProductDiscountCouponStepDto

                if (this.orderProductDiscountCouponStepDto.state == "COMP") {
                    this.state = "PD_COUPON_STEP_COMP"
                } else {
                    this.state = "PD_COUPON_STEP_FAIL"
                    throw RuntimeException(this.state)
                }

//                println("[Product Discount Coupon Step]")
//                println(orderProductDiscountCouponStepDto)

                Mono.create<OrderProductDiscountCouponStepDto> {
                    it.success(orderProductDiscountCouponStepDto)
                }
            }
            .doOnError {
                throw RuntimeException("${it.message}")
            }
    }

    override fun revert(orderId: String): Mono<Any> {
        println("call product discount coupon step revert()")
        val revertProductDiscountCouponStepEvent = RevertProductDiscountCouponStepEvent(
            orderId,
            orderProductDiscountCouponStepDto.orderProductDiscountCouponDtos
        )

        return orderToCouponProducer!!.publishRevertSagaStepEvent(revertProductDiscountCouponStepEvent)
    }
}