package com.yosep.order.saga.http.step

import com.fasterxml.jackson.annotation.JsonIgnore
import com.yosep.order.data.dto.CreatedOrderDto
import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.data.dto.OrderProductDiscountCouponStepDto
import com.yosep.order.data.dto.OrderTotalDiscountCouponStepDto
import com.yosep.order.data.vo.OrderProductDtoForCreation
import com.yosep.order.data.vo.OrderTotalDiscountCouponDto
import com.yosep.order.saga.http.WorkflowStep
import com.yosep.order.saga.http.annotation.SagaStep
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

class TotalDiscountCouponStep(
    @JsonIgnore
    private val webClient: WebClient? = null,
    var orderTotalDiscountCouponStepDto: OrderTotalDiscountCouponStepDto,
    stepType: String = "TOTAL-DISCOUNT-COUPON",
    state: String = "READY"
) : WorkflowStep<OrderTotalDiscountCouponStepDto>(
    stepType, state
) {
    @SagaStep
    override fun process(): Mono<OrderTotalDiscountCouponStepDto> {
        this.state = "PENDING"
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
                    this.state = "COMP"
                } else {
                    this.state = "FAIL"
                }

                println("[Total Discount Coupon Step]")
                println(orderTotalDiscountCouponStepDto)

                Mono.create<OrderTotalDiscountCouponStepDto> {
                    it.success(orderTotalDiscountCouponStepDto)
                }
            }
    }

    override fun revert(): Mono<OrderTotalDiscountCouponStepDto> {
        return Mono.empty()
    }

    private fun checkProductPrices() {

    }
}