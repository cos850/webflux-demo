package com.hr.webfluxdemo.api.transfer

import com.hr.webfluxdemo.api.excel.ExcelDownloadService
import com.hr.webfluxdemo.api.excel.TransferExcelDto
import com.hr.webfluxdemo.api.transfer.dto.DepositWithdrawRequest
import com.hr.webfluxdemo.api.transfer.dto.toSearchCondition
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.springframework.stereotype.Service

@Service
class TransferExcelDownloadService(
    private val repository: WebTransferRepository
) : ExcelDownloadService<DepositWithdrawRequest, TransferExcelDto>() {

    override suspend fun generateExcel(request: DepositWithdrawRequest): SXSSFWorkbook {
        val memoryRowSize = 1000
        val workbook = createWorkbook(memoryRowSize)
        val sheet = workbook.createSheet("입출금내역")

        // header 작성
        sheet.writeHeader(TransferExcelDto::class)

        val searchCondition = request.toSearchCondition()
        val totalCount = repository.count(searchCondition)

        if(totalCount == 0) return workbook

        var offset = 0
        while (offset < totalCount) {
            val transferList = repository
                .findPageWithUserName(offset, memoryRowSize, searchCondition)
                .map { TransferExcelDto.of(it) }

            sheet.writeData(offset+1, transferList)
            offset += memoryRowSize
        }

        return workbook
    }

}