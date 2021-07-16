package com.yosep.order.saga.http.step

import com.fasterxml.jackson.annotation.JsonIgnore
import com.yosep.order.common.data.RandomIdGenerator
import com.yosep.order.data.dto.CreatedOrderDto
import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.saga.http.WorkflowStep
import com.yosep.order.service.OrderService
import reactor.core.publisher.Mono

class OrderStep(
    @JsonIgnore
    private var orderService: OrderService? = null,
    @JsonIgnore
    private var randomIdGenerator: RandomIdGenerator? = null,
    stepType: String = "ORDER",
    state: String = "READY"
) : WorkflowStep<OrderDtoForCreation, CreatedOrderDto>(
    stepType,
    state
) {
    private lateinit var createdOrderDto: CreatedOrderDto

    override fun process(orderDtoForCreation: OrderDtoForCreation): Mono<CreatedOrderDto> {
        this.state = "PENDING"

        return orderService!!.createOrder(orderDtoForCreation)
            .flatMap { createdOrderDto ->
                this.createdOrderDto = createdOrderDto

                this.state = "COMP"
                Mono.create<CreatedOrderDto> { monoSink ->
                    monoSink.success(this.createdOrderDto)
                }
            }
    }

    override fun revert(orderDtoForCreation: OrderDtoForCreation): Mono<CreatedOrderDto> {

        return Mono.empty()
    }

}