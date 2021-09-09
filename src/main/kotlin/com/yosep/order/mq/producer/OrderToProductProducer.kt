package com.yosep.order.mq.producer

import com.fasterxml.jackson.databind.ObjectMapper
import com.yosep.order.event.saga.revert.RevertProductStepEvent
import io.netty.util.internal.logging.Slf4JLoggerFactory
import org.apache.kafka.clients.producer.ProducerConfig
import org.apache.kafka.clients.producer.ProducerRecord
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kafka.sender.KafkaSender
import reactor.kafka.sender.SenderOptions
import reactor.kafka.sender.SenderRecord
import reactor.kafka.sender.SenderResult
import reactor.kotlin.core.publisher.toMono
import java.text.SimpleDateFormat
import java.util.*

@Component
class OrderToProductProducer @Autowired constructor(
    private val objectMapper: ObjectMapper
) {
    private val log = Slf4JLoggerFactory.getInstance(OrderToProductProducer::class.java)

    private val BOOTSTRAP_SERVERS = "localhost:9092"
    private var sender: KafkaSender<Int, String>? = null
    private var dateFormat: SimpleDateFormat? = null

    init {
        val props: MutableMap<String, Any> = HashMap()
        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = BOOTSTRAP_SERVERS
        props[ProducerConfig.CLIENT_ID_CONFIG] = "sample-producer"
        props[ProducerConfig.ACKS_CONFIG] = "all"
        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] =
            org.apache.kafka.common.serialization.IntegerSerializer::class.java
        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] =
            org.apache.kafka.common.serialization.StringSerializer::class.java
        val senderOptions = SenderOptions.create<Int, String>(props)
        sender = KafkaSender.create(senderOptions)
        dateFormat = SimpleDateFormat("HH:mm:ss:SSS z dd MMM yyyy")
    }

    @Throws(InterruptedException::class)
    fun publishRevertProductEvent(revertProductStepEvent: RevertProductStepEvent): Mono<Any> {
        val message = objectMapper.writeValueAsString(revertProductStepEvent)

        return sender!!.send(Flux.range(1, 1)
            .map { i: Int ->
                SenderRecord.create(
                    ProducerRecord("revert-product-step", message),
                    i
                )
            })
            .toMono()
            .doOnError { e: Throwable? ->
                log.error(
                    "Send failed",
                    e
                )
            }
            .flatMap { r: SenderResult<Int> ->
                val metadata = r.recordMetadata()
                System.out.printf(
                    "Message %d sent successfully, topic-partition=%s-%d offset=%d timestamp=%s\n",
                    r.correlationMetadata(),
                    metadata.topic(),
                    metadata.partition(),
                    metadata.offset(),
                    dateFormat!!.format(Date(metadata.timestamp()))
                )

                Mono.create<SenderResult<Int>> {
                    it.success(r)
                }
            }
//        return Mono.create<Any> {
//            sender!!.send(Flux.range(1, 1)
//                .map { i: Int ->
//                    SenderRecord.create(
//                        ProducerRecord("revert-product-step", message),
//                        i
//                    )
//                }).toMono()
//                .doOnError { e: Throwable? ->
//                    log.error(
//                        "Send failed",
//                        e
//                    )
//                }
//                .flatMap { r: SenderResult<Int> ->
//                    val metadata = r.recordMetadata()
//                    System.out.printf(
//                        "Message %d sent successfully, topic-partition=%s-%d offset=%d timestamp=%s\n",
//                        r.correlationMetadata(),
//                        metadata.topic(),
//                        metadata.partition(),
//                        metadata.offset(),
//                        dateFormat!!.format(Date(metadata.timestamp()))
//                    )
//
//                    Mono.create<SenderResult<Int>> {
//                        it.success(r)
//                    }
//                }
//        }
    }
}