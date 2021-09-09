package com.yosep.order.data.dto

import com.yosep.order.data.entity.Order
import com.yosep.order.data.entity.OrderProduct

data class CreatedOrderDto(
    val order: Order,
    val orderProducts: List<OrderProduct> = emptyList()
)