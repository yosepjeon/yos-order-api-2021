package com.yosep.order.saga.http.step

import com.fasterxml.jackson.annotation.JsonIgnore
import com.yosep.order.data.dto.CreatedOrderDto
import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.data.vo.OrderProductDiscountCouponDto
import com.yosep.order.saga.http.WorkflowStep
import com.yosep.order.saga.http.annotation.SagaStep
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

class ProductDiscountCouponStep(
    @JsonIgnore
    private val webClient: WebClient? = null,
    val orderProductDiscountCouponDto: List<OrderProductDiscountCouponDto>,
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