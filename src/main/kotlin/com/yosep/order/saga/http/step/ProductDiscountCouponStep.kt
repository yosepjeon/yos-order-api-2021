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
    stepType: String = "PRODUCT",
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
            .uri("/test")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(orderProductDiscountCouponStepDto)
            .retrieve()
            .bodyToMono(OrderProductDiscountCouponStepDto::class.java)
    }

    override fun revert(): Mono<OrderProductDiscountCouponStepDto> {
        return Mono.empty()
    }
}