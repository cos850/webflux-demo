package com.hr.webfluxdemo.config

import org.springframework.core.convert.converter.Converter
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.*

class LocalDateTimeToZonedDateTimeConverter: Converter<LocalDateTime, ZonedDateTime> {

    override fun convert(source: LocalDateTime): ZonedDateTime? {
        return ZonedDateTime.of(source, TimeZone.getTimeZone("Asia/Seoul").toZoneId())
    }
}