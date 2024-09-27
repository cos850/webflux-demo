package com.hr.webfluxdemo.api.dynamic.common

import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties

class DynamicRepositoryImpl : DynamicRepository {

    override fun makeWhere(condition: Any): String? {
        val whereClauses = mutableListOf<String>()

        for(prop in condition::class.memberProperties) {
            val dynamicCondition = prop.annotations
                .filterIsInstance<DynamicCondition>()
                .firstOrNull()

            dynamicCondition ?. let {
                val columnName = it.columnName
                val operator = it.operator
                val value = prop.call(condition)?.toString()

                // 조건절의 데이터가 null이 아니거나, 값이 null 이여야 하는 조건의 경우 생성
                if(value != null || operator.isNullableType()) {
                    val prefix = " ${columnName} ${operator} "
                    whereClauses.add(prefix + makeClause(operator, value))
                }
            }
        }

        return if(whereClauses.isEmpty()) {
            null
        } else {
            "WHERE ${whereClauses.joinToString(" AND ")}"
        }
    }

    private fun makeClause(operator: DynamicOperator, value: Any?, ): String {
        return when(operator) {
            DynamicOperator.EQUAL -> "'${value}' "
            DynamicOperator.LIKE -> " '%${value}%'"
            DynamicOperator.LIKE_START -> " '%${value}'"
            DynamicOperator.LIKE_END -> "'${value}%'"
            DynamicOperator.IS_NULL, DynamicOperator.IS_NOT_NULL -> ""
            DynamicOperator.BETWEEN -> {
                return if(value is Pair<*, *>) {
                    "${value.first} AND ${value.second}"
                } else {
                    throw IllegalArgumentException("동적 쿼리 생성 중 오류. BETWEEN 연산자 사용 시 반드시 pair 타입이어야 합니다.")
                }
            }
        }
    }

    private fun KType.isPairType(): Boolean {
        return this.classifier == Pair::class
    }
}