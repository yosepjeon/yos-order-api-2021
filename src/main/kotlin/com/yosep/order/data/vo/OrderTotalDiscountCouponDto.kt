package com.yosep.order.data.vo

data class OrderTotalDiscountCouponDto(
    val dtype: String,
    val couponByUserId: String,
    val userId: String,
    val discountAmount: Long,
    val discountPercent: Long,
    var state: String = "READY"
)