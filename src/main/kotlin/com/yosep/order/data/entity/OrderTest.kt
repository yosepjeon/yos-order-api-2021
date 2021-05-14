package com.yosep.order.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("yos_order_test")
data class OrderTest(
    @Id
    val id: String,
    val name: String
)