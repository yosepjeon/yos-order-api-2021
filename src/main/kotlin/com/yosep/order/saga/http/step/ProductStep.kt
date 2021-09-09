package com.yosep.order.saga.http.step

import com.fasterxml.jackson.annotation.JsonIgnore
import com.yosep.order.data.dto.ProductStepDtoForCreation
import com.yosep.order.event.saga.revert.RevertProductStepEvent
import com.yosep.order.mq.producer.OrderToProductProducer

import reactor.core.publisher.Mono

import org.springframework.web.reactive.function.client.WebClient

import com.yosep.order.saga.http.WorkflowStep
import com.yosep.order.saga.http.annotation.SagaStep
import org.springframework.http.MediaType
import reactor.kotlin.core.publisher.toMono


class ProductStep(
    @JsonIgnore
    private val webClient: WebClient? = null,
    @JsonIgnore
    private val orderToProductProducer: OrderToProductProducer? = null,
    var productStepDtoForCreation: ProductStepDtoForCreation,
    stepType: String = "PRODUCT",
    state: String = "READY"
) : WorkflowStep<ProductStepDtoForCreation>(
    stepType,
    state
) {
    @SagaStep
    override fun process(): Mono<ProductStepDtoForCreation> {
        this.state = "PENDING"

        return webClient!!
            .post()
            .uri("/order-saga")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(productStepDtoForCreation)
            .retrieve()
            .bodyToMono(ProductStepDtoForCreation::class.java)
//            .retryWhen()
            .doOnNext { productStepDtoForCreation ->
                this.productStepDtoForCreation = productStepDtoForCreation

                if (this.productStepDtoForCreation.state == "COMP") {
                    this.state = "PRODUCT_STEP_COMP"
                } else {
                    this.state = "PRODUCT_STEP_FAIL"
                    throw RuntimeException(this.state)
                }

                println("[ProductStep]")
                println(productStepDtoForCreation)
            }
    }

    override fun revert(orderId: String): Mono<Any> {
        println("call product step revert()")

        val revertProductStepEvent = RevertProductStepEvent(
            orderId,
            productStepDtoForCreation.orderProductDtos
        )

        return orderToProductProducer!!.publishRevertProductEvent(revertProductStepEvent).toMono()
//        return Mono.create { monoSink ->
//
//
//            monoSink.success()
//        }

//        return produ
    }

    private fun checkProductPrices() {

    }
}