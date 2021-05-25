package com.yosep.order.data.repository

import com.yosep.order.data.entity.Order
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface OrderRepository: R2dbcRepository<Order, String> {
}