package com.yosep.order.service

import com.yosep.order.data.entity.OrderTest
import com.yosep.order.data.repository.OrderTestRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class OrderTestService @Autowired constructor(
    private val orderTestRepository: OrderTestRepository,
) {
    fun findProductById(productId: String):Mono<OrderTest> {
        return orderTestRepository.findById(productId)
    }

//    fun findAllDsl(productName: String):Flux<ProductTest> {
//        return produc
//    }
}