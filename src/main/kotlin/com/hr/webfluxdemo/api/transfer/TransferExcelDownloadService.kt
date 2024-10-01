package com.hr.webfluxdemo.api.transfer

import com.hr.webfluxdemo.api.excel.ExcelDownloadService
import com.hr.webfluxdemo.api.excel.TransferExcelDto
import com.hr.webfluxdemo.api.transfer.dto.TransferSearchCondition
import org.springframework.stereotype.Service

@Service
class TransferExcelDownloadService(
    private val repository: WebTransferRepository
) : ExcelDownloadService<TransferSearchCondition, TransferExcelDto>() {

    override fun getSheetName(): String = "입출금내역"

    override suspend fun getTotalData(searchCondition: TransferSearchCondition): Int {
        return repository.count(searchCondition)
    }

    override suspend fun getDataList(
            offset: Int,
            size: Int,
            searchCondition: TransferSearchCondition
    ): List<TransferExcelDto> {
        return repository
            .findPageWithUserName(offset, size, searchCondition)
            .map { TransferExcelDto.of(it) }
    }

}