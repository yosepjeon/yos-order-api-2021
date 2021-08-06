package com.yosep.order.saga.http.step

import com.fasterxml.jackson.annotation.JsonIgnore
import com.yosep.order.data.dto.CreatedOrderDto
import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.data.vo.OrderProductDtoForCreation
import com.yosep.order.data.vo.OrderTotalDiscountCouponDto
import com.yosep.order.saga.http.WorkflowStep
import com.yosep.order.saga.http.annotation.SagaStep
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

class TotalDiscountCouponStep(
    @JsonIgnore
    private val webClient: WebClient? = null,
//    private val orderProductDtos: List<OrderProductDtoForCreation>,
    val orderTotalDiscountCouponDtos: List<OrderTotalDiscountCouponDto>,
//    private val couponStepDtoForCreation: CouponStepDtoForCreation,
    stepType: String = "COUPON",
    state: String = "READY"
) : WorkflowStep<CreatedOrderDto>(
    stepType,state
) {
    @SagaStep
    override fun process(): Mono<CreatedOrderDto> {
        this.state = "PENDING"

        webClient!!
            .post()
            .uri("/test")
            .body(BodyInserters.fromValue(""))
            .retrieve()
            .bodyToMono(String::class.java)


        return Mono.empty()
    }

    override fun revert(): Mono<CreatedOrderDto> {
        return Mono.empty()
    }

    private fun checkProductPrices() {

    }
}