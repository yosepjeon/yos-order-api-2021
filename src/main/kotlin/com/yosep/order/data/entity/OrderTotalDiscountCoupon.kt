package com.yosep.order.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable

class OrderTotalDiscountCoupon(
    @Id
    val orderCouponByUserId: String,
    val userId: String,
    val discountAmount: Long,
    val discountPercent: Long,
    val dtype: String,
    var state: String = "READY"
) : Persistable<String> {
    @Transient
    private var isNew = false

    @Transient
    override fun isNew(): Boolean {
        return isNew || orderCouponByUserId == null
    }

    fun setAsNew(): OrderTotalDiscountCoupon? {
        isNew = true
        return this
    }

    override fun getId(): String? {
        return this.orderCouponByUserId
    }
}