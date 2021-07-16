package com.yosep.order.data.event

import com.yosep.order.data.entity.OrderProduct

data class OrderEventToUpdateProduct constructor(
    val orderEventId: String,
    val orderProducts: List<OrderProduct>,
    val state: String
) {

}