package com.yosep.order.common.config

import org.modelmapper.ModelMapper
import org.modelmapper.convention.MatchingStrategies
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ModelMapperConfig {

    @Bean(name = ["basicModelMapper"])
    fun basicModelMapper(): ModelMapper {
        val orderMapper = ModelMapper()
        orderMapper.configuration.matchingStrategy = MatchingStrategies.STRICT

        return orderMapper
    }
}