//package com.yosep.order.mq.producer
//
//import java.util.concurrent.TimeUnit
//
//import java.util.concurrent.CountDownLatch
//
//import org.apache.kafka.clients.producer.RecordMetadata
//
//import org.apache.kafka.clients.producer.ProducerRecord
//
//import reactor.kafka.sender.SenderRecord
//
//import reactor.core.publisher.Flux
//
//import java.text.SimpleDateFormat
//
//import reactor.kafka.sender.KafkaSender
//
//import reactor.kafka.sender.SenderOptions
//
//import org.apache.kafka.clients.producer.ProducerConfig
//import org.apache.kafka.common.serialization.IntegerSerializer
//import org.apache.kafka.common.serialization.StringSerializer
//import org.slf4j.Logger
//import org.slf4j.LoggerFactory
//import reactor.kafka.sender.SenderResult
//import java.lang.Exception
//import java.util.*
//
//
//class SampleProducer(bootstrapServers: String) {
//    private val sender: KafkaSender<Int, String>
//    private val dateFormat: SimpleDateFormat
//    @Throws(InterruptedException::class)
//    fun sendMessages(topic: String?, count: Int, latch: CountDownLatch) {
//        sender.send(Flux.range(1, count)
//            .map { i: Int ->
//                SenderRecord.create(
//                    ProducerRecord(topic, i, "Message_$i"),
//                    i
//                )
//            })
//            .doOnError { e: Throwable? ->
//                log.error(
//                    "Send failed",
//                    e
//                )
//            }
//            .subscribe { r: SenderResult<Int> ->
//                val metadata = r.recordMetadata()
//                System.out.printf(
//                    "Message %d sent successfully, topic-partition=%s-%d offset=%d timestamp=%s\n",
//                    r.correlationMetadata(),
//                    metadata.topic(),
//                    metadata.partition(),
//                    metadata.offset(),
//                    dateFormat.format(Date(metadata.timestamp()))
//                )
//                latch.countDown()
//            }
//    }
//
//    fun close() {
//        sender.close()
//    }
//
//    companion object {
//        private val log: Logger = LoggerFactory.getLogger(SampleProducer::class.java.name)
//        private const val BOOTSTRAP_SERVERS = "localhost:9092"
//        private const val TOPIC = "demo-topic"
//        @Throws(Exception::class)
//        @JvmStatic
//        fun main(args: Array<String>) {
//            val count = 20
//            val latch = CountDownLatch(count)
//            val producer = SampleProducer(BOOTSTRAP_SERVERS)
//            producer.sendMessages(TOPIC, count, latch)
//            latch.await(10, TimeUnit.SECONDS)
//            producer.close()
//        }
//    }
//
//    init {
//        val props: MutableMap<String, Any> = HashMap()
//        props[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = bootstrapServers
//        props[ProducerConfig.CLIENT_ID_CONFIG] = "sample-producer"
//        props[ProducerConfig.ACKS_CONFIG] = "all"
//        props[ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG] = IntegerSerializer::class.java
//        props[ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG] = StringSerializer::class.java
//        val senderOptions = SenderOptions.create<Int, String>(props)
//        sender = KafkaSender.create(senderOptions)
//        dateFormat = SimpleDateFormat("HH:mm:ss:SSS z dd MMM yyyy")
//    }
//}