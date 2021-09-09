package com.yosep.order.common

import com.yosep.order.common.data.RandomIdGenerator
import com.yosep.order.data.dto.OrderDtoForCreation
import com.yosep.order.data.vo.OrderProductDtoForCreation
import com.yosep.order.service.OrderService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class OrderGeneratorForTest @Autowired constructor(
    private val randomIdGenerator: RandomIdGenerator,
    private val orderService: OrderService
) {
    fun create() {
        val orderCount = 10

        for (i in 0 until orderCount) {
            val productCount = (Math.random() * 10).toInt()
            val productInfoForCreations = mutableListOf<OrderProductDtoForCreation>()

            for (i in 0 until productCount) {
                val productInfoForCreation = OrderProductDtoForCreation(
                    i.toString(),
                    (Math.random() * 10).toInt(),
                    10000,
                    "READY"
                )

                productInfoForCreations.add(productInfoForCreation)
            }

            val orderDtoForCreation = OrderDtoForCreation(
                "",
                100000000,
                productInfoForCreations,
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

//            orderService.createOrder()

        }
    }
}