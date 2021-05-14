package com.yosep.order.controller.router

import com.yosep.order.controller.handler.ProductTestHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RequestPredicates.path
import org.springframework.web.reactive.function.server.RouterFunctions.nest
import org.springframework.web.reactive.function.server.router

@Configuration
class ProductRouter constructor(
    private val productTestHandler: ProductTestHandler
) {
    @Bean
    fun testRouteFunction() = nest(
        path("/product/test"),
        router {
            listOf(
                GET("/connection-test",productTestHandler::connectionTest),
                GET("/rest-doc-test", productTestHandler::restDocTest)
            )
        }
    )
}
