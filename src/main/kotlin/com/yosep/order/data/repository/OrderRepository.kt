package com.yosep.order.data.repository

import com.yosep.order.data.entity.Order
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux

interface OrderRepository: R2dbcRepository<Order, String> {
    @Query("SELECT * FROM yos_order WHERE sender_id = :senderId")
    fun findOrderBySenderId(senderId: String): Flux<Order>
}