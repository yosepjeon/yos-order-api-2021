package com.yosep.order.data.repository

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier

@SpringBootTest
class OrderEntityTestRepositoryTest @Autowired constructor(
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