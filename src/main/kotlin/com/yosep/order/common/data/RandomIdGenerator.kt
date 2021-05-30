package com.yosep.order.common.data

import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Component
class RandomIdGenerator {
    fun generate(): String {
        val now = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val uuid = UUID.randomUUID().toString().replace("-","").substring(0,10)

        return uuid.toString()
    }
}