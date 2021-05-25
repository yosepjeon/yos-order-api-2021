package com.yosep.order.common.config

import io.r2dbc.spi.ConnectionFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.r2dbc.connection.R2dbcTransactionManager
import org.springframework.transaction.ReactiveTransactionManager
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableTransactionManagement
class R2dbcConfig @Autowired constructor(
    val connectionFactory: ConnectionFactory
) {

    @Bean
    fun transactionManager(): ReactiveTransactionManager {
        return R2dbcTransactionManager(connectionFactory);
    }
}