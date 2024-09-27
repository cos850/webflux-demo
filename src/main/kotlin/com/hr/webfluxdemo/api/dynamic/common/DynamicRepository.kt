package com.hr.webfluxdemo.api.dynamic.common

import com.hr.webfluxdemo.api.dynamic.common.DynamicOperator.*
import org.springframework.stereotype.Repository
import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties

@Repository
interface DynamicRepository {

    fun makeWhere(condition: Any): String?
}