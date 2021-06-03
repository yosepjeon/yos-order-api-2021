package com.yosep.order.service

import com.yosep.order.common.data.RandomIdGenerator
import com.yosep.order.data.dto.OrderDtoForCreationLegacy
import com.yosep.order.data.entity.OrderLegacy
import com.yosep.order.data.repository.OrderLegacyRepository
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Deprecated("더이상 사용하지 마시오.")
@Service
class OrderLegacyService @Autowired constructor(
    private val modelMapper: ModelMapper,
    private val randomIdGenerator: RandomIdGenerator,
    private val orderLegacyRepository: OrderLegacyRepository
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
    fun createOrder(orderDtoForCreationLegacy: OrderDtoForCreationLegacy): Mono<OrderLegacy> {
        return randomIdGenerator.generate()
            .flatMap {
                val order = OrderLegacy(
                    it,
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
                )
                order.setAsNew()

                Mono.create<OrderLegacy> { sink ->
                    sink.success(order)
                }
            }
            .flatMap(orderLegacyRepository::save)
            .retry()

    }

    fun readOrderById(orderId: String): Mono<OrderLegacy> {
        return orderLegacyRepository.findById(orderId)
    }

    /*
    * 읽기만 가능 한 유저 별 주문 조회
    * Logic:
    * 1. 유저 아이디를 기준으로 주문을 읽어온다.
    * 2. 읽어온 주문들을 정렬하여 Immutable
     */
    fun readOnlyOrderBySenderId(senderId: String): Mono<List<OrderLegacy>> {
        return orderLegacyRepository
            .findOrdersBySenderId(senderId)
            .collectSortedList { o1, o2 -> o2.orderRegisterDate.compareTo(o1.orderRegisterDate) }
    }

    fun updateOrder() {

    }

    fun deleteOrder() {

    }

    fun processOrder() {

    }

}