package com.yosep.order.controller.router

import com.yosep.order.controller.handler.OrderTestHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.RequestPredicates.path
import org.springframework.web.reactive.function.server.RouterFunctions.nest
import org.springframework.web.reactive.function.server.router

@Configuration
class OrderRouter constructor(
    private val orderTestHandler: OrderTestHandler
) {
    @Bean
    fun testRouteFunction() = nest(
        path("/product/test"),
        router {
            listOf(
                GET("/connection-test",orderTestHandler::connectionTest),
                GET("/rest-doc-test", orderTestHandler::restDocTest)
            )
        }
    )
}
