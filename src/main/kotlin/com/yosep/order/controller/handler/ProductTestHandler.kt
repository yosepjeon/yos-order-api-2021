package com.yosep.order.controller.handler

import com.yosep.order.service.ProductTestService
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.body
import reactor.core.publisher.Mono

@Component
class ProductTestHandler constructor(
    private val productTestService: ProductTestService
) {
    fun connectionTest(req: ServerRequest): Mono<ServerResponse> {
        return ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(Mono.just("연결 성공"))
    }

    fun restDocTest(req: ServerRequest): Mono<ServerResponse> {
        return ok()
            .contentType(MediaType.APPLICATION_JSON)
            .body(productTestService.findProductById("test"))
    }
}