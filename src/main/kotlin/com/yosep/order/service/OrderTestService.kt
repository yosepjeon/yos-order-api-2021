package com.yosep.order.service

import com.yosep.order.data.dto.CreatedOrderDto
import com.yosep.order.data.dto.OrderDtoForCreationLegacy
import com.yosep.order.data.entity.OrderLegacy
import com.yosep.order.data.entity.OrderTest
import com.yosep.order.data.repository.OrderLegacyRepository
import com.yosep.order.data.repository.OrderTestRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Service
class OrderTestService @Autowired constructor(
    private val orderTestRepository: OrderTestRepository,
    private val orderLegacyRepository: OrderLegacyRepository
) {
    fun findProductById(productId: String):Mono<OrderTest> {
        return orderTestRepository.findById(productId)
    }

    fun createOrder(orderDtoForCreationLegacy: OrderDtoForCreationLegacy): Mono<CreatedOrderDto> {
        val order = OrderLegacy(
            orderDtoForCreationLegacy.orderId,
            orderDtoForCreationLegacy.productId,
            orderDtoForCreationLegacy.userId,
            orderDtoForCreationLegacy.senderName,
            orderDtoForCreationLegacy.receiverName,
            orderDtoForCreationLegacy.phone,
            orderDtoForCreationLegacy.postCode,
            orderDtoForCreationLegacy.roadAddr,
            orderDtoForCreationLegacy.jibunAddr,
            orderDtoForCreationLegacy.extraAddr,
            orderDtoForCreationLegacy.detailAddr,
            orderDtoForCreationLegacy.orderState,
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