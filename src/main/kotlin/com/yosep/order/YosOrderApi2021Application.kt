package com.yosep.order

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient
import org.springframework.context.annotation.EnableAspectJAutoProxy

@EnableAspectJAutoProxy
@SpringBootApplication
@EnableDiscoveryClient
class YosOrderApi2021Application

fun main(args: Array<String>) {
	runApplication<YosOrderApi2021Application>(*args)
}
