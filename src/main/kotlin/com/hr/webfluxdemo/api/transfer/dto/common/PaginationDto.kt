package com.hr.webfluxdemo.api.transfer.dto.common

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "페이지 요청 데이터")
open class PaginationRequest<T> (
    @field:Schema(description = "페이지번호 (1부터 시작, default: 1)", required = false)
    val page: Int = 1,

    @field:Schema(description = "목록 사이즈 (default: 10)", required = false)
    val size: Int = 10,

    @field:Schema(description = "검색 조건 클래스", required = false)
    val searchCondition: T,
)

data class PaginationResponse<T> (
    val totalElements: Long = 0,
    val totalPages: Int = 0,
    val currentPage: Int = 0,
    val contents: List<T>,
)