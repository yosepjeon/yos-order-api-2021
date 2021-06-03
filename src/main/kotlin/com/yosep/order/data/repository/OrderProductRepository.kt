package com.yosep.order.data.repository

import com.yosep.order.data.entity.OrderProduct
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface OrderProductRepository: R2dbcRepository<OrderProduct, String> {
}