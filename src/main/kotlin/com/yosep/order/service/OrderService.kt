package com.yosep.order.service

import com.yosep.order.common.data.RandomIdGenerator
import com.yosep.order.data.dto.CreatedOrderDto
import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.data.entity.Order
import com.yosep.order.data.entity.OrderProduct
import com.yosep.order.data.enum.OrderProductState
import com.yosep.order.data.repository.OrderProductRepository
import com.yosep.order.data.repository.OrderRepository
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
@Transactional(readOnly = true)
class OrderService @Autowired constructor(
    private val modelMapper: ModelMapper,
    private val randomIdGenerator: RandomIdGenerator,
    private val orderRepository: OrderRepository,
    private val orderProductRepository: OrderProductRepository
) {
    /*
    * 주문 생성
    * Logic:
    * 1. Random 주문 ID 생성
    * 2. 입력 받은 DTO와 주문 ID를 조합하여 주문 엔티티 생성
    * 3. 엔티티 저장
    * 3-1. 생성한 주문 ID가 이미 존재한다면 Integration Exception 발생. retry()를 통해 1번으로 회귀
    * 3-1. 생성한 주문 ID가 존재하지 않는다면 엔티티를 저장
    * 4. 주문 상품 저장
    * 4-1.
     */
    @Transactional(readOnly = false)
    fun createOrder(orderDtoForCreation: OrderDtoForCreation): Mono<CreatedOrderDto> {
        return randomIdGenerator.generate()
            .flatMap { orderId ->
                val order = Order(
                    orderId,
                    orderDtoForCreation.userId,
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
            .flatMap { order ->
                var i = 1
                val orderProducts = mutableListOf<OrderProduct>()

                orderDtoForCreation.orderProductDtos.forEach { orderProductDto ->
                    val orderProduct = OrderProduct(
                        order.orderId + i,
                        order.orderId,
                        orderProductDto.productId,
                        orderProductDto.count,
                        OrderProductState.READY.value
                    )

                    orderProduct.setAsNew()

                    orderProducts.add(orderProduct)
                    i++
                }

                orderProductRepository.saveAll(orderProducts.asIterable())
                    .collectList()
                    .flatMap { orderProducts ->
                        Mono.create<CreatedOrderDto> { sink ->
                            val createdOrderDto = CreatedOrderDto(
                                order,
                                orderProducts
                            )

                            sink.success(createdOrderDto)
                        }
                    }
            }
            .retry()

    }

    /*
    *
     */
    @Transactional(readOnly = false)
    fun doOrder(orderDtoForCreation: OrderDtoForCreation) {

//        createOrder(orderDtoForCreation)
//            .flatMap { createdOrderDto:CreatedOrderDto ->
//                Mono.empty<String>()
//            }
    }

    fun readOrderById(orderId: String): Mono<Order> {
        return orderRepository.findById(orderId)
    }

    /*
    * 읽기만 가능 한 유저 별 주문 조회
    * Logic:
    * 1. 유저 아이디를 기준으로 주문을 읽어온다.
    * 2. 읽어온 주문들을 정렬하여 Immutable
     */
    fun readOnlyOrderBySenderId(senderId: String): Mono<List<Order>> {
        return orderRepository
            .findOrdersBySenderId(senderId)
            .collectSortedList { o1, o2 -> o2.orderRegisterDate.compareTo(o1.orderRegisterDate) }
    }

    fun updateOrder() {

    }

    fun deleteOrderById(orderId: String) {
        orderRepository.deleteById(orderId)
            .flatMap {
                Mono.create<String> { monoSink ->
                    monoSink.success(orderId)
                }
            }
    }

    fun cancelAll(orderId: String) {

    }

    @Transactional(readOnly = false)
    fun deleteAllOrder() {
        orderProductRepository.deleteAll()
    }

    @Transactional(readOnly = false)
    fun cancelOrderProduct(orderProductIds: List<String>) {
//        orderProductIds.forEach { orderProductId ->
//
//        }
    }

    fun processOrder() {

    }

}