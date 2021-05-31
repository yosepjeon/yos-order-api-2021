package com.yosep.order.common.data

import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class RandomIdGenerator {
    fun generate(): Mono<String> = Mono.create<String> {
        val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"))
        val uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 10)
        it.success(now + uuid)
    }
}