package com.yosep.order.event.saga.revert

import com.yosep.order.data.dto.OrderTotalDiscountCouponStepDto
import com.yosep.order.data.vo.OrderTotalDiscountCouponDto

data class RevertTotalDiscountCouponStepEvent(
    val eventId: String,
    val orderTotalDiscountCouponDtos: List<OrderTotalDiscountCouponDto>,
) {

}