package com.yosep.order.saga.http.step

import com.fasterxml.jackson.annotation.JsonIgnore
import com.yosep.order.data.dto.CreatedOrderDto
import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.data.dto.ProductStepDtoForCreation
import com.yosep.order.data.vo.OrderProductDtoForCreation
import org.springframework.web.reactive.function.BodyInserters

import reactor.core.publisher.Mono

import com.yosep.order.saga.http.WorkflowStepStatus

import org.springframework.web.reactive.function.client.WebClient

import com.yosep.order.saga.http.WorkflowStep
import com.yosep.order.saga.http.annotation.SagaStep
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserter
import reactor.core.publisher.Flux
import reactor.kotlin.core.publisher.toFlux


class ProductStep(
    @JsonIgnore
    private val webClient: WebClient? = null,
//    private val orderProductsDtoForCreation: List<OrderProductDtoForCreation> = emptyList(),
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
        this.productStepDtoForCreation.state = "PENDING"
//        WebClientResponseException.GatewayTimeout

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

                if(this.productStepDtoForCreation.state == "FAIL") {
                    this.state = "FAIL"
                }else {
                    this.state = "COMP"
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

//internal class InventoryStep(private val webClient: WebClient, requestDTO: InventoryRequestDTO) : WorkflowStep {
//    private val requestDTO: InventoryRequestDTO
//    override var status = WorkflowStepStatus.PENDING
//        private set
//
//    override fun process(): Mono<Boolean?> {
//        return webClient
//            .post()
//            .uri("/inventory/deduct")
//            .body(BodyInserters.fromValue<Any>(requestDTO))
//            .retrieve()
//            .bodyToMono(InventoryResponseDTO::class.java)
//            .map { r -> r.getStatus().equals(InventoryStatus.AVAILABLE) }
//            .doOnNext { b -> status = if (b) WorkflowStepStatus.COMPLETE else WorkflowStepStatus.FAILED }
//    }
//
//    override fun revert(): Mono<Boolean?> {
//        return webClient
//            .post()
//            .uri("/inventory/add")
//            .body(BodyInserters.fromValue<Any>(requestDTO))
//            .retrieve()
//            .bodyToMono(Void::class.java)
//            .map { r: Void? -> true }
//            .onErrorReturn(false)
//    }
//
//    init {
//        this.requestDTO = requestDTO
//    }
//}