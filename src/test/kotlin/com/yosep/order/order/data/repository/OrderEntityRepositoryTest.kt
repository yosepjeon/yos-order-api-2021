package com.yosep.order.order.data.repository

import com.yosep.order.data.repository.OrderRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier

@SpringBootTest
class OrderEntityRepositoryTest @Autowired constructor(
    private val orderRepository: OrderRepository
) {
    @Test
    fun readOrder() {
        StepVerifier.create(orderRepository.findById("test"))
            .expectSubscription()
            .consumeNextWith {
                Assertions.assertEquals("test", it.productId)
            }
            .verifyComplete()
    }
}