package com.yosep.order.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable

class OrderProductDiscountCoupon(
    @Id
    val orderCouponByUserId: String,
    val orderId: String,
    val productCount: Long,
    val userId: String,
    val discountAmount: Long,
    val discountPercent: Long,
    val productId: String,
    val totalPrice: Long,
    var calculatedPrice: Long,
    val dtype: String,
    var state: String,
):Persistable<String> {
    @Transient
    private var isNew = false

    @Transient
    override fun isNew(): Boolean {
        return isNew || orderCouponByUserId == null
    }

    fun setAsNew(): OrderProductDiscountCoupon? {
        isNew = true
        return this
    }

    override fun getId(): String? {
        return this.orderCouponByUserId
    }
}