package com.yosep.order.event.saga.revert

import com.yosep.order.data.vo.OrderProductDtoForCreation

data class RevertProductStepEvent(
    val eventId: String,
    val orderProductDtos: List<OrderProductDtoForCreation> = emptyList(),
) {

}