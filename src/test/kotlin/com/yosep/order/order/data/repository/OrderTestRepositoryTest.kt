package com.yosep.order.order.data.repository

import com.yosep.order.data.entity.OrderTest
import com.yosep.order.data.repository.OrderTestRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import reactor.test.StepVerifier

@SpringBootTest
class OrderTestRepositoryTest @Autowired constructor(
    private val orderTestRepository: OrderTestRepository
) {
    @BeforeEach
    fun createOrderTestEntity() {
        val orderTest = OrderTest("test","테스트")
        orderTest.setAsNew()

        orderTestRepository.save(orderTest)
            .subscribe()
    }

    @AfterEach
    fun deleteOrderTestEntity() {
        val orderId = "test"
        orderTestRepository.deleteById(orderId)
            .subscribe()
    }

    @Test
    fun readOrderTest() {

//        StepVerifier.create(orderTestRepository.findById("test"))
//            .expectSubscription()
//            .consumeNextWith {
//                println("### $it")
//                Assertions.assertEquals("test", it.id)
//            }
//            .verifyComplete()
    }
}