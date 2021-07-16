package com.yosep.order.common.config

import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.RedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableCaching
class RedisConfig {
    @Bean
    fun redisOperations(factory: ReactiveRedisConnectionFactory?): ReactiveRedisOperations<String, String>? {
        val serializer: RedisSerializer<String> = StringRedisSerializer()
        val serializationContext = RedisSerializationContext.newSerializationContext<String, String>()
            .key(serializer)
            .value(serializer)
            .hashKey(serializer)
            .hashValue(serializer)
            .build()
        return ReactiveRedisTemplate(factory!!, serializationContext)
    }
}