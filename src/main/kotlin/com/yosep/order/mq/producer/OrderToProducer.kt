package com.yosep.order.mq.producer

import reactor.core.publisher.Mono

interface OrderToProducer {
    @Throws(InterruptedException::class)
    fun publishRevertSagaStepEvent(event: Any): Mono<Any>
}