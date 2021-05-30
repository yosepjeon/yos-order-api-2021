package com.yosep.order.service

import org.junit.jupiter.api.DisplayNameGeneration
import org.junit.jupiter.api.DisplayNameGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores::class)
class OrderServiceTest @Autowired constructor(
    orderService: OrderService
) {
}