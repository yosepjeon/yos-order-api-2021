package com.yosep.order.data.repository

import com.yosep.order.data.entity.OrderLegacy
import com.yosep.order.data.entity.OrderProduct
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.r2dbc.repository.R2dbcRepository
import reactor.core.publisher.Flux

@Deprecated("더이상 사용하지 마시오.")
interface OrderLegacyRepository: R2dbcRepository<OrderLegacy, String> {
    @Query("SELECT * FROM yos_order_legacy WHERE user_id = :userId")
    fun findOrdersBySenderId(userId: String): Flux<OrderLegacy>

    @Query("SELECT * FROM yos_product_in_order WHERE order_id = :orderId")
    fun findProductsByOrderId(orderId: String): Flux<OrderProduct>
}