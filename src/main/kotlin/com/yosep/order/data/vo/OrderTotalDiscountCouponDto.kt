package com.yosep.order.data.vo

data class OrderTotalDiscountCouponDto(
    val couponByUserId: String,
    val userId: String,
    val discountAmount: Long,
    val discountPercent: Long,
    var state: String = "READY"
)