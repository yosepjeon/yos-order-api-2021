package com.yosep.order.common

import com.yosep.order.data.entity.Order
import io.netty.util.internal.logging.Slf4JLoggerFactory
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.DisplayNameGeneration
import org.junit.jupiter.api.DisplayNameGenerator
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores::class)
class WebfluxTest {
    val log = Slf4JLoggerFactory.getInstance(WebfluxTest::class.java)

    @Test
    @DisplayName("[Webflux Test] Mono 병렬 실행 테스트")
    fun Mono_병렬_실행_테스트() {
        log.info("[Webflux Test] Mono 병렬 실행 테스트")

        val m1 = Mono.create<String> { monoSink ->
            monoSink.success("Hello World")
        }

        val m2 = Mono.create<Long> { monoSink ->
            monoSink.success(100L)
        }

        val m3 = Mono.create<Double> { monoSink ->
            monoSink.success(3.0)
        }

        val m4 = Mono.create<Order> { monoSink ->
            monoSink.success(Order("test1"))
        }

        val monoZip = Mono.zip(m1,m2,m3,m4)

        StepVerifier.create(monoZip)
            .assertNext {
                log.info("order: ${it.t4}")
                log.info(it.toString())
            }
            .verifyComplete()
    }
}