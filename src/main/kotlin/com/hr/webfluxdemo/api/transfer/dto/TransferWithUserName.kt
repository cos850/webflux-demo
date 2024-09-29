package com.hr.webfluxdemo.api.transfer.dto

import com.hr.webfluxdemo.domain.Transfer

data class TransferWithUserName(
    val transfer: Transfer,
    val userNickname: String?,
)