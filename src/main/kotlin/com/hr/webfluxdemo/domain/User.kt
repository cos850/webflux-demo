package com.hr.webfluxdemo.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.time.ZonedDateTime

@Table
class User (
    @Id
    val id: Long,

    // 로그인할 사용자 ID
    val loginId: String,

    val nickname: String,

    val email: String,

    val phoneNumber: String,

    // 최초 생성 시각
    val createdAt: ZonedDateTime,

    // 마지막 수정 시각
    val updatedAt: ZonedDateTime,
)