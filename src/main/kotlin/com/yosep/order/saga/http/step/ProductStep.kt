package com.yosep.order.saga.http.step

import com.fasterxml.jackson.annotation.JsonIgnore
import com.yosep.order.data.dto.ProductStepDtoForCreation

import reactor.core.publisher.Mono

import org.springframework.web.reactive.function.client.WebClient

import com.yosep.order.saga.http.WorkflowStep
import com.yosep.order.saga.http.annotation.SagaStep
import org.springframework.http.MediaType


class ProductStep(
    @JsonIgnore
    private val webClient: WebClient? = null,
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
                    this.state = "COMP"
                } else {
                    this.state = "FAIL"
                }

                println("[ProductStep]")
                println(productStepDtoForCreation)
            }
    }

    override fun revert(): Mono<ProductStepDtoForCreation> {

        return Mono.create { monoSink ->

            monoSink.success()
        }
    }

    private fun checkProductPrices() {

    }
}