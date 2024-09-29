package com.hr.webfluxdemo.api.transfer

import com.hr.webfluxdemo.api.transfer.dto.DepositWithdrawRequest
import com.hr.webfluxdemo.api.transfer.dto.DepositWithdrawResponse
import com.hr.webfluxdemo.api.transfer.dto.common.PaginationRequest
import com.hr.webfluxdemo.api.transfer.dto.common.PaginationResponse
import com.hr.webfluxdemo.api.transfer.dto.toSearchCondition
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class DepositWithdrawController(private val transferService: WebTransferService) {

    @Operation(
        summary = "입출금내역 - 페이지 조회",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = [Content(mediaType = "application/json")]
        ),
    )
    @PostMapping("/api/deposit-withdraw/page")
    suspend fun page(
        @RequestBody request: PaginationRequest<DepositWithdrawRequest>
    ): PaginationResponse<DepositWithdrawResponse> = withContext(Dispatchers.IO) {
        transferService.getPage(
            page = request.page,
            size = request.size,
            searchCondition = request.searchCondition.toSearchCondition()
        )
    }
}