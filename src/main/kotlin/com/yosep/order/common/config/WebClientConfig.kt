package com.yosep.order.common.config

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.http.codec.ClientCodecConfigurer
import reactor.netty.http.client.HttpClient
import reactor.core.publisher.Mono
import org.slf4j.Logger
import org.springframework.http.codec.HttpMessageWriter

import org.springframework.http.codec.LoggingCodecSupport
import org.springframework.web.reactive.function.client.*
import reactor.netty.Connection
import reactor.netty.tcp.TcpClient
import java.util.function.Consumer
import java.util.function.Function


@Configuration
class WebClientConfig constructor(
    val log: Logger = LoggerFactory.getLogger(WebClientConfig::class.java)
) {

    @Bean
    fun webClient(): WebClient {
        val exchangeStrategies = ExchangeStrategies.builder()
            .codecs { configurer: ClientCodecConfigurer ->
                configurer.defaultCodecs().maxInMemorySize(1024 * 1024 * 50)
            }
            .build()
        exchangeStrategies
            .messageWriters().stream()
            .filter { obj: HttpMessageWriter<*>? ->
                LoggingCodecSupport::class.java.isInstance(
                    obj
                )
            }
            .forEach { writer: HttpMessageWriter<*> ->
                (writer as LoggingCodecSupport).isEnableLoggingRequestDetails = true
            }
        return WebClient.builder()
            .clientConnector(
                ReactorClientHttpConnector(
                    HttpClient
                        .create()
                        .tcpConfiguration(
                            Function { client: TcpClient ->
                                client.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 120000)
                                    .doOnConnected { conn: Connection ->
                                        conn.addHandlerLast(ReadTimeoutHandler(180))
                                            .addHandlerLast(WriteTimeoutHandler(180))
                                    }
                            }
                        )
                )
            )
            .exchangeStrategies(exchangeStrategies)
            .filter(ExchangeFilterFunction.ofRequestProcessor { clientRequest: ClientRequest ->
                log.debug("Request: {} {}", clientRequest.method(), clientRequest.url())
                clientRequest.headers()
                    .forEach { name: String?, values: List<String?> ->
                        values.forEach(
                            Consumer { value: String? ->
                                log.debug(
                                    "{} : {}",
                                    name,
                                    value
                                )
                            })
                    }
                Mono.just(clientRequest)
            })
            .filter(ExchangeFilterFunction.ofResponseProcessor { clientResponse: ClientResponse ->
                clientResponse.headers().asHttpHeaders()
                    .forEach { name: String?, values: List<String?> ->
                        values.forEach(
                            Consumer { value: String? ->
                                log.debug(
                                    "{} : {}",
                                    name,
                                    value
                                )
                            })
                    }
                Mono.just(clientResponse)
            })
//            .defaultHeader(
//                "user-agent",
//                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Safari/537.3"
//            )
            .build()
    }
}