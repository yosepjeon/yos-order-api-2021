package com.yosep.order.data.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import javax.jdo.annotations.Column

@Table("yos_order_event")
class OrderEvent(
    @Id
    @field:Column(length = 300)
    val eventId: String,

    var state: String = "PENDING",
) {
}