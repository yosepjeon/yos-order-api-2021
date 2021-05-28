package com.yosep.order.common.config

import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement
import io.r2dbc.spi.ConnectionFactories
import org.mariadb.r2dbc.MariadbConnectionConfiguration
import org.mariadb.r2dbc.MariadbConnectionFactory


@Configuration
@EnableTransactionManagement
class R2dbcConfig: AbstractR2dbcConfiguration() {
    @Value("\${spring.r2dbc.url}")
    val url = ""
    @Value("\${spring.r2dbc.host}")
    val host = ""
    @Value("\${spring.r2dbc.port}")
    val port = 0
    @Value("\${spring.r2dbc.database}")
    val database = ""
    @Value("\${spring.r2dbc.username}")
    val userName = ""
    @Value("\${spring.r2dbc.password}")
    val password = ""

    @Bean
    override fun connectionFactory(): ConnectionFactory {
        return MariadbConnectionFactory(MariadbConnectionConfiguration.builder()
            .host(host)
            .port(port)
            .database(database)
            .username(userName)
            .password(password)
            .build())
    }

    @Bean
    fun reactiveTransactionManager(connectionFactory: ConnectionFactory?): ReactiveTransactionManager {
        return R2dbcTransactionManager(connectionFactory!!)
    }
}