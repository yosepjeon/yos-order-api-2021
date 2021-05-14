package com.yosep.order.data.repository

import com.yosep.order.data.entity.OrderTest
import org.springframework.data.r2dbc.repository.R2dbcRepository

interface OrderTestRepository: R2dbcRepository<OrderTest, String> {
}