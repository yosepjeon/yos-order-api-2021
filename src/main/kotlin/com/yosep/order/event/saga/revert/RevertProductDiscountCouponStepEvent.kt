package com.yosep.order.event.saga.revert

import com.yosep.order.data.dto.OrderProductDiscountCouponStepDto
import com.yosep.order.data.vo.OrderProductDiscountCouponDto

data class RevertProductDiscountCouponStepEvent(
    val eventId: String,
    val orderProductDiscountCouponDtos: List<OrderProductDiscountCouponDto>
) {
}