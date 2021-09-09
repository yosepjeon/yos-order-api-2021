package com.yosep.order.event.saga.process

import com.yosep.order.data.dto.ProductStepDtoForCreation

data class ProcessProductStepEvent(
    val eventId: String,
    val productStepDtoForCreation: ProductStepDtoForCreation
) {
}