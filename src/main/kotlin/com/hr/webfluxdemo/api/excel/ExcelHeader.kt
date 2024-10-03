package com.hr.webfluxdemo.api.excel

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExcelHeader(
    val headerName: String,
    val maxDataLength: Int = 10
)
