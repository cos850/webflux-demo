package com.hr.webfluxdemo.api.dynamic.common

import kotlin.reflect.KType
import kotlin.reflect.full.memberProperties


fun generateWhere(condition: Any, vararg customClauses: String): String? {
    val whereClauses = mutableListOf<String>()

    for(prop in condition::class.memberProperties) {
        val dynamicCondition = prop.annotations
            .filterIsInstance<DynamicCondition>()
            .firstOrNull()

        dynamicCondition ?. let {
            val columnName = it.columnName
            val operator = it.operator
            val value = prop.call(condition)

            // 조건절의 데이터가 null이 아니거나, 값이 null 이여야 하는 조건의 경우 생성
            if(value != null || operator.isNullableType()) {
                val prefix = " ${columnName} ${operator} "
                whereClauses.add(prefix + makeClause(operator, value))
            }
        }
    }

    // 사용자 정의 조건 추가
    customClauses.forEach { customClause ->
        if (customClause.isNotEmpty()) {
            whereClauses.add(customClause)
        }
    }

    return if(whereClauses.isEmpty()) {
        null
    } else {
        "WHERE ${whereClauses.joinToString(" AND ")}"
    }
}

private fun makeClause(operator: DynamicOperator, value: Any?): String {
    return when(operator) {
        DynamicOperator.EQUAL -> "'${value}' "
        DynamicOperator.LIKE -> " '%${value}%'"
        DynamicOperator.LIKE_START -> " '%${value}'"
        DynamicOperator.LIKE_END -> "'${value}%'"
        DynamicOperator.IS_NULL, DynamicOperator.IS_NOT_NULL -> ""
        DynamicOperator.BETWEEN -> {
            print("value = ${value}, type = ${value!!::class.simpleName}")

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
