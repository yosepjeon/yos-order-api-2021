package com.yosep.order.saga.http.step

import com.fasterxml.jackson.annotation.JsonIgnore
import com.yosep.order.common.data.RandomIdGenerator
import com.yosep.order.data.dto.CreatedOrderDto
import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.saga.http.WorkflowStep
import com.yosep.order.saga.http.annotation.SagaStep
import com.yosep.order.service.OrderService
import reactor.core.publisher.Mono

class OrderStep(
    @JsonIgnore
    private var orderService: OrderService? = null,
    @JsonIgnore
    private var randomIdGenerator: RandomIdGenerator? = null,
    val orderDtoForCreation: OrderDtoForCreation,
    var orderId: String = "",
    stepType: String = "ORDER",
    state: String = "READY"
) : WorkflowStep<CreatedOrderDto>(
    stepType,
    state
) {
    private lateinit var createdOrderDto: CreatedOrderDto

    @SagaStep
    override fun process(): Mono<CreatedOrderDto> {
        this.state = "PENDING"

        return orderService!!.createOrder(orderDtoForCreation)
            .flatMap { createdOrderDto ->
                this.createdOrderDto = createdOrderDto

                orderId = this.createdOrderDto.order.orderId

                this.state = "COMP"
                Mono.create<CreatedOrderDto> { monoSink ->
                    monoSink.success(this.createdOrderDto)
                }
            }
    }

    override fun revert(): Mono<CreatedOrderDto> {
        return Mono.create { monoSink ->
            monoSink.success()
        }
    }

}