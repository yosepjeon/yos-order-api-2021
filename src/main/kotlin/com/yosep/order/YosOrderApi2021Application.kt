package com.yosep.order

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy

@EnableAspectJAutoProxy
@SpringBootApplication
class YosOrderApi2021Application

fun main(args: Array<String>) {
	runApplication<YosOrderApi2021Application>(*args)
}
