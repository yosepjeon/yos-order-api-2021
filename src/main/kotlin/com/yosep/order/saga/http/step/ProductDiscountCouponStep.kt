package com.yosep.order.saga.http.step

import com.fasterxml.jackson.annotation.JsonIgnore
import com.yosep.order.data.dto.OrderProductDiscountCouponStepDto
import com.yosep.order.data.dto.ProductStepDtoForCreation
import com.yosep.order.data.vo.OrderProductDiscountCouponDto
import com.yosep.order.data.vo.OrderTotalDiscountCouponDto
import com.yosep.order.saga.http.WorkflowStep
import com.yosep.order.saga.http.annotation.SagaStep
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

class ProductDiscountCouponStep(
    @JsonIgnore
    private val webClient: WebClient? = null,
    var orderProductDiscountCouponStepDto: OrderProductDiscountCouponStepDto,
    stepType: String = "PRODUCT-DISCOUNT-COUPON",
    state: String = "READY"
) : WorkflowStep<OrderProductDiscountCouponStepDto>(
    stepType,
    state
) {
    @SagaStep
    override fun process(): Mono<OrderProductDiscountCouponStepDto> {
        this.state = "PENDING"
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
                    this.state = "COMP"
                } else {
                    this.state = "FAIL"
                }

                println("[Product Discount Coupon Step]")
                println(orderProductDiscountCouponStepDto)

                Mono.create<OrderProductDiscountCouponStepDto> {
                    it.success(orderProductDiscountCouponStepDto)
                }
            }
    }

    override fun revert(): Mono<OrderProductDiscountCouponStepDto> {
        return Mono.empty()
    }
}