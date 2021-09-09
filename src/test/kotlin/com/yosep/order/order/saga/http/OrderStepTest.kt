package com.yosep.order.order.saga.http

import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.data.vo.OrderProductDtoForCreation
import com.yosep.order.service.OrderService
import io.netty.util.internal.logging.Slf4JLoggerFactory
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.DisplayNameGeneration
import org.junit.jupiter.api.DisplayNameGenerator
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.ReactiveRedisTemplate

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores::class)
class OrderStepTest @Autowired constructor(
    private val orderService: OrderService,
    private val redisTemplate: ReactiveRedisTemplate<String, String>
) {
    val log = Slf4JLoggerFactory.getInstance(OrderStepTest::class.java)!!

    @Test
    @DisplayName("[WorkflowStep] OrderStep 테스트")
    fun OrderStep_테스트() {
        log.info("[WorkflowStep] OrderStep 테스트")
        val productCount = (Math.random() * 10).toInt() + 1
        val orderProducts = mutableListOf<OrderProductDtoForCreation>()

        for (i in 0 until productCount) {
            val productInfoForCreation = OrderProductDtoForCreation(
                i.toString(),
                (Math.random() * 10).toInt(),
                10000,
                "READY",
            )

            orderProducts.add(productInfoForCreation)
        }

        val orderDtoForCreation = OrderDtoForCreation(
            "",
            10000000,
            orderProducts,
            emptyList(),
            emptyList(),
            "sender1",
            "요깨비",
            "이재훈",
            "123123123",
            "asdf",
            "asdf",
            "asdf",
            "asdf",
            "asdf",
            "READY",
        )

//        val orderStep =
    }
}