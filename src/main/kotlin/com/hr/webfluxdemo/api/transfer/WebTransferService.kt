package com.hr.webfluxdemo.api.transfer

import com.hr.webfluxdemo.api.transfer.dto.DepositWithdrawResponse
import com.hr.webfluxdemo.api.transfer.dto.TransferSearchCondition
import com.hr.webfluxdemo.api.transfer.dto.common.PaginationResponse
import org.springframework.stereotype.Service

@Service
class WebTransferService(private val transferRepository: WebTransferRepository) {

    suspend fun getPage(page: Int, size: Int, searchCondition: TransferSearchCondition) : PaginationResponse<DepositWithdrawResponse> {
        val offset = (page-1) * size

        val contents = transferRepository.findPageWithUserName(
            offset = offset,
            limit = size,
            condition = searchCondition,
        )

        val count = transferRepository.count(searchCondition)

        return PaginationResponse<DepositWithdrawResponse> (
            currentPage = page,
            totalElements = count.toLong(),
            totalPages = if(count > 0) (count / size + if(count % size > 0) 1 else 0) else 0,
            contents = contents.map { entity -> DepositWithdrawResponse(
                id = entity.transfer.id,
                amount = entity.transfer.amount,
                accountNumber = entity.transfer.accountNumber,
                memo = entity.transfer.memo,
                lastDateTime = (entity.transfer.updatedAt ?: entity.transfer.createdAt).toLocalDateTime()
            )},
        )
    }
}