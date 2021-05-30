package com.yosep.order.service

import com.yosep.order.common.data.RandomIdGenerator
import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.data.entity.Order
import com.yosep.order.data.repository.OrderRepository
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class OrderService @Autowired constructor(
    val modelMapper: ModelMapper,
    val randomIdGenerator: RandomIdGenerator,
    val orderRepository: OrderRepository
) {

    /*
    * 주문 생성
    * Logic:
    * 1. Random 주문 Id 생성
    * 1-1. 카테고리가 존재한다면 2번으로 진행.
    * 1-2. 카테고리가 존재하지 않는다면 Exception 던지기.
    * 2.
     */
    fun createOrder(orderDtoForCreation: OrderDtoForCreation): Mono<Order> {
        val orderId = randomIdGenerator.generate()

        return orderRepository.findById(orderId)
            .flatMap { order: Order ->
                Mono.create<Order> {
                    it.success()
                }
            }
            .doOnNext {

            }

//        orderRepository.findById(orderId)
//            .doOnNext {
//
//            }
//
//        val order = modelMapper.map(orderDtoForCreation, Order::class.java)
    }

    fun readOrderById() {

    }

    fun updateOrder() {

    }

    fun deleteOrder() {

    }

    fun processOrder() {

    }

}