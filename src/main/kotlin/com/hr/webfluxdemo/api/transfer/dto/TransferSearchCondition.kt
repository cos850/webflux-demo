package com.hr.webfluxdemo.api.transfer.dto

import com.hr.webfluxdemo.domain.TransferStatus
import java.time.LocalDate

data class TransferSearchCondition(
    val searchDate: Pair<LocalDate, LocalDate>,

    val userNickname: String? = null,

    val accountNumber: String? = null,

    val memo: String? = null,

    val accountSign: String? = null,

    val status: TransferStatus? = null,
)