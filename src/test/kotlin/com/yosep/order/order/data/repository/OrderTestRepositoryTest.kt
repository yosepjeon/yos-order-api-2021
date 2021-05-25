package com.yosep.order.order.data.repository

import com.yosep.order.data.repository.OrderTestRepository
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier

@SpringBootTest
class OrderTestRepositoryTest @Autowired constructor(
    private val orderTestRepository: OrderTestRepository
) {
    @Test
    fun readOrderTest() {
        StepVerifier.create(orderTestRepository.findById("test"))
            .expectSubscription()
            .consumeNextWith {
                Assertions.assertEquals("test", it.id)
            }
            .verifyComplete()
    }
}