package com.hr.webfluxdemo.api.transfer.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * 입출금내역 요청 dto
 */
@Schema(description = "입출금 내역 페이지 요청")
data class DepositWithdrawRequest (

    @field:Schema(description = "검색 일자 (first ~ second)", required = true)
    val searchDate: Pair<LocalDate, LocalDate>,

    @field:Schema(description = "사용자명", example = "홍길동", required = false)
    val userNickname: String? = null,

    @field:Schema(description = "계좌번호", example = "123-456-789", required = false)
    val accountNumber: String? = null,

    // 선택: 출금 계좌 메모
    @field:Schema(description = "출금 계좌 메모", example = "Deposit for rent", required = false)
    val memo: String? = null,
)

/**
 * 입출금내역 응답 dto
 */
data class DepositWithdrawResponse (
    val id: Long,

    // 입출금 금액
    val amount: Long,

    // 계좌번호
    val accountNumber: String,

    // 출금 계좌 메모
    val memo: String,

    // 최종 거래 시간 (updatedAt이 null이면 createdAt 사용)
    val lastDateTime: LocalDateTime,
)

fun DepositWithdrawRequest.toSearchCondition(): TransferSearchCondition {
    return TransferSearchCondition(
        searchDate = searchDate,
        userNickname = userNickname,
        accountNumber = accountNumber,
        memo = memo
    )
}