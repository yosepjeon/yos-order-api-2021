package com.yosep.order.saga.http.step

import com.fasterxml.jackson.annotation.JsonIgnore
import com.yosep.order.data.vo.OrderCompanyDiscountCouponDto
import org.springframework.web.reactive.function.client.WebClient

class CompanyDiscountCouponStep(
    @JsonIgnore
    private val webClient: WebClient? = null,
    val orderProductDiscountCouponDtos: List<OrderCompanyDiscountCouponDto>,
    stepType: String = "PRODUCT",
    state: String = "READY"
) {
}