package com.yosep.order.service

import com.yosep.order.common.data.RandomIdGenerator
import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.data.entity.Order
import com.yosep.order.data.repository.OrderRepository
import javassist.bytecode.DuplicateMemberException
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OrderService @Autowired constructor(
    private val modelMapper: ModelMapper,
    private val randomIdGenerator: RandomIdGenerator,
    private val orderRepository: OrderRepository
) {

    /*
    * 주문 생성
    * Logic:
    * 1. Random 주문 ID 생성
    * 2. 입력 받은 DTO와 주문 ID를 조합하여 주문 엔티티 생성
    * 3. 엔티티 저장
    * 3-1. 생성한 주문 ID가 이미 존재한다면 Integration Exception 발생. retry()를 통해 1번으로 회귀
    * 3-1. 생성한 주문 ID가 존재하지 않는다면 엔티티를 저장 후 결과 반환
     */
    fun createOrder(orderDtoForCreation: OrderDtoForCreation): Mono<Order> {
        return randomIdGenerator.generate()
            .flatMap {
                val order = Order(
                    it,
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
                )
                order.setAsNew()

                Mono.create<Order> { sink ->
                    sink.success(order)
                }
            }
            .flatMap(orderRepository::save)
            .retry()

    }

    fun readOrderById(orderId: String): Mono<Order> {
        return orderRepository.findById(orderId)
    }

    fun readOrderBySenderId(senderId: String) {

    }

    fun updateOrder() {

    }

    fun deleteOrder() {

    }

    fun processOrder() {

    }

}