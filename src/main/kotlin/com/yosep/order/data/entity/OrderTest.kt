package com.yosep.order.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Transient
import org.springframework.data.domain.Persistable
import org.springframework.data.relational.core.mapping.Table
import javax.jdo.annotations.Column

@Table("yos_order_test")
data class OrderTest(
    @Id
    @field:Column(length = 300)
    var orderId: String,
    var name: String
): Persistable<String> {

    @Transient
    private var isNew = false

    @Transient
    override fun isNew(): Boolean {
        return isNew || orderId == null
    }

    fun setAsNew(): OrderTest? {
        isNew = true
        return this
    }

    override fun getId(): String? {
        return this.orderId
    }
}