package com.yosep.order.data.dto

import com.yosep.order.data.vo.OrderTotalDiscountCouponDto
import javax.validation.constraints.Min
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.Size

data class OrderTotalDiscountCouponStepDto(
    val orderId: String,

    @field:Min(0)
    val totalPrice: Long,

    @field:NotEmpty
    @field:Size(min = 1)
    val orderTotalDiscountCouponDtos: List<OrderTotalDiscountCouponDto>,

    @field:Min(0)
    var calculatedPrice: Long,
    @field:NotEmpty
    var state: String = "READY"
)