package com.hr.webfluxdemo.api.excel

import com.hr.webfluxdemo.api.transfer.dto.TransferWithUserName
import com.hr.webfluxdemo.domain.BankCode
import java.time.LocalDateTime

data class TransferExcelDto(
    @ExcelHeader("거래 시각")
    val lastDateTime: LocalDateTime,

    @ExcelHeader("업체명")
    val partnerName: String?,

    @ExcelHeader("거래 금액")
    val amount: Long,

    @ExcelHeader("수취인명")
    val receiverName: String,

    @ExcelHeader("은행코드")
    val bankCode: BankCode,

    @ExcelHeader("계좌번호")
    val accountNumber: String
) {
    companion object {
        fun of(source: TransferWithUserName): TransferExcelDto = with(source.transfer) {
            TransferExcelDto(
                lastDateTime = (updatedAt ?: createdAt).toLocalDateTime(),
                partnerName = source.userNickname,
                amount = amount,
                receiverName = memo,
                bankCode = bankCode,
                accountNumber = accountNumber
            )
        }
    }
}