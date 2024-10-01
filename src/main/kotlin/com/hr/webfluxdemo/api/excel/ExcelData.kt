package com.hr.webfluxdemo.api.excel

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class ExcelHeader(
    val headerName: String
)
