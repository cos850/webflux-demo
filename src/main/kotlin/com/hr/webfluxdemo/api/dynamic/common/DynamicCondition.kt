package com.hr.webfluxdemo.api.dynamic.common

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.RUNTIME)
annotation class DynamicCondition(
    /**
     * join하는 경우에는 해당 테이블 약어 추가 필요
     */
    val columnName: String,
    val operator: DynamicOperator
)

enum class DynamicOperator(val op: String) {
    EQUAL("="),
    LIKE("LIKE"),
    LIKE_END("LIKE"),
    LIKE_START("LIKE"),
    BETWEEN("BETWEEN"),
    IS_NULL("IS NULL"),
    IS_NOT_NULL("IS NOT NULL")
    ;

    fun isNullableType(): Boolean {
        return this == IS_NULL || this == IS_NOT_NULL
    }
}
