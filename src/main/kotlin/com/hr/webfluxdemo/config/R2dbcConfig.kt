package com.hr.webfluxdemo.config

import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions
import org.springframework.data.r2dbc.dialect.DialectResolver
import org.springframework.data.r2dbc.dialect.R2dbcDialect

@Configuration
class R2dbcConfig(private val connectionFactory: ConnectionFactory) {

    @Bean
    fun r2dbcDialect(): R2dbcDialect {
        return DialectResolver.getDialect(connectionFactory)
    }

    @Bean
    fun r2dbcCustomConversions(dialect: R2dbcDialect): R2dbcCustomConversions {
        val converters = listOf(LocalDateTimeToZonedDateTimeConverter())
        return R2dbcCustomConversions.of(dialect, converters)
    }
}
