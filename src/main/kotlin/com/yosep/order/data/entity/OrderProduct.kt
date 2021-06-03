package com.yosep.order.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table

@Table("yos_product_in_order")
data class OrderProduct (
    @Id
    val orderProductId: String,
    val orderId: String,
    val productId: String,
    val count: Int,
    val state: String
): Persistable<String> {

    @Transient
    private var isNew = false

    @Transient
    override fun isNew(): Boolean {
        return isNew || orderProductId == null
    }

    fun setAsNew(): OrderProduct? {
        isNew = true
        return this
    }

    override fun getId(): String? {
        return this.orderProductId
    }
}