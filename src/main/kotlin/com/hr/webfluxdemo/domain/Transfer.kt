package com.hr.webfluxdemo.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.ZonedDateTime

@Table("transfer")
data class Transfer(
    @Id
    val id: Long,

    // 사용자명 (user.id)
    val userId: String,

    // 고객 계좌 ID (pocket.id)
    val pocketId: Long,

    // 입출금 금액
    val amount: Long,

    // 은행코드
    val bankCode: BankCode,

    // 계좌번호
    val accountNumber: String,

    // 출금 계좌 메모
    val memo: String,

    // 받는 통장 메모
    val receiverMemo: String,

    // 최초 거래 시간
    val createdAt: ZonedDateTime,

    // 마지막 거래 시간
    val updatedAt: ZonedDateTime?,
)