package com.yosep.order.common

import com.yosep.order.data.entity.Order
import io.netty.util.internal.logging.Slf4JLoggerFactory
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.DisplayNameGeneration
import org.junit.jupiter.api.DisplayNameGenerator
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.zip
import reactor.test.StepVerifier

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores::class)
class WebfluxTest {
    val log = Slf4JLoggerFactory.getInstance(WebfluxTest::class.java)

    @Test
    @DisplayName("[Webflux&Reactor Test] Mono 병렬 실행 테스트")
    fun Mono_병렬_실행_테스트() {
        log.info("[Webflux&Reactor Test] Mono 병렬 실행 테스트")

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

    @Test
    @DisplayName("[Webflux&Reactor Test]Mono를 담은 list를 zip하여 병렬처리 테스트")
    fun Mono를_담은_list를_zip하여_병렬처리_테스트() {
        log.info("[Webflux&Reactor Test]Mono를 담은 list를 zip하여 병렬처리 테스트")

        val monos = mutableListOf<Mono<Any>>()

        val list1 = mutableListOf<String>()
        for(i in 0 until 10) {
            list1.add("call order step revert")
        }

        val m1 = Mono.create<Any> { monoSink ->
            list1.forEach {
                log.info(it)
            }

            monoSink.success()
        }.subscribeOn(Schedulers.parallel())

        val list2 = mutableListOf<String>()
        for(i in 0 until 10) {
            list2.add("call product step revert")
        }

        val m2 = Mono.create<Any> { monoSink ->
            list2.forEach {
                log.info(it)
            }

            monoSink.success()
        }.subscribeOn(Schedulers.parallel())

        monos.add(m1)
        monos.add(m2)

        val monosZip = monos.zip {  }
        log.info("병렬실행 확인")
        StepVerifier.create(monosZip)
            .verifyComplete()
    }
}