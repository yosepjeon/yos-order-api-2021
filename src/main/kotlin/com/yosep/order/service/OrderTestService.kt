package com.yosep.order.service

import com.yosep.order.data.dto.CreatedOrderDto
import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.data.entity.Order
import com.yosep.order.data.entity.OrderTest
import com.yosep.order.data.repository.OrderRepository
import com.yosep.order.data.repository.OrderTestRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class OrderTestService @Autowired constructor(
    private val orderTestRepository: OrderTestRepository,
    private val orderRepository: OrderRepository
) {
    fun findProductById(productId: String):Mono<OrderTest> {
        return orderTestRepository.findById(productId)
    }

    fun createOrder(orderDtoForCreation: OrderDtoForCreation): Mono<CreatedOrderDto> {
        val order = Order(
            orderDtoForCreation.orderId,
            orderDtoForCreation.productId,
            orderDtoForCreation.senderId,
            orderDtoForCreation.senderName,
            orderDtoForCreation.receiverName,
            orderDtoForCreation.phone,
            orderDtoForCreation.postCode,
            orderDtoForCreation.roadAddr,
            orderDtoForCreation.jibunAddr,
            orderDtoForCreation.extraAddr,
            orderDtoForCreation.detailAddr,
            orderDtoForCreation.orderState,
            LocalDateTime.now(),
            null,
            null
        )

        return Mono.empty()
    }

//    fun findAllDsl(productName: String):Flux<ProductTest> {
//        return produc
//    }
}