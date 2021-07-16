package com.yosep.order.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.yosep.order.common.data.RandomIdGenerator
import com.yosep.order.common.mapper.OrderMapper
import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.orchestrator.OrderOrchestratorByWebclient
import org.modelmapper.ModelMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderSagaService @Autowired constructor(
    private val objectMapper: ObjectMapper,
    private val randomIdGenerator: RandomIdGenerator,
    private val orderOrchestratorByWebclient: OrderOrchestratorByWebclient
) {
    @Transactional(readOnly = false)
    fun processOrder(orderDtoForCreation: OrderDtoForCreation) {

    }
}