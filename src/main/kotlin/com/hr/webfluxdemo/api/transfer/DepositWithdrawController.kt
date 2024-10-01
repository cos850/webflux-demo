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
import org.springframework.core.io.buffer.DefaultDataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.io.ByteArrayOutputStream

@RestController
class DepositWithdrawController(
    private val transferService: WebTransferService,
    private val transferExcelDownloadService: TransferExcelDownloadService
) {

    @Operation(
        summary = "입출금내역 - 페이지 조회",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = [Content(mediaType = "application/json")]
        ),
    )
    @PostMapping("/api/deposit-withdraw")
    suspend fun page(
        @RequestBody request: PaginationRequest<DepositWithdrawRequest>
    ): PaginationResponse<DepositWithdrawResponse> = withContext(Dispatchers.IO) {
        transferService.getPage(
            page = request.page,
            size = request.size,
            searchCondition = request.searchCondition.toSearchCondition()
        )
    }

    @Operation(
        summary = "입출금내역 - 엑셀 다운로드",
        requestBody = io.swagger.v3.oas.annotations.parameters.RequestBody(
            required = true,
            content = [Content(mediaType = "application/json")]
        ),
    )
    @PostMapping("/api/deposit-withdraw/download")
    suspend fun downloadExcel(
        @RequestBody request: DepositWithdrawRequest,
        httpResponse: ServerHttpResponse
    ): Mono<Void> {
        val workbook = transferExcelDownloadService.generateExcel(request)

//        val workbook = transferExcelDownloadService.generateExcel(request)
        httpResponse.headers[HttpHeaders.CONTENT_DISPOSITION] = listOf("attachment; filename=\"sample.xlsx\"")
        httpResponse.headers.contentType = MediaType.APPLICATION_OCTET_STREAM

        // OutputStream에 데이터를 기록
        val outputStream = ByteArrayOutputStream()
        workbook.write(outputStream)
        workbook.dispose()

        // ByteArray로 변환
        val byteArray = outputStream.toByteArray()

        // DataBuffer로 변환하여 응답으로 스트리밍
        val buffer = httpResponse.bufferFactory().wrap(byteArray)

        // 스트리밍 방식으로 클라이언트에 데이터 전송
        return httpResponse.writeWith(Mono.just(buffer))

//        val fileName = "sample"
//
//        // HTTP 응답 헤더 설정
//        httpResponse.headers[HttpHeaders.CONTENT_DISPOSITION] = listOf("attachment; filename=\"${fileName}.xlsx\"")
//        httpResponse.headers.contentType = MediaType.APPLICATION_OCTET_STREAM
//
////        val outputStream = httpResponse.bufferFactory().allocateBuffer(1024)
////        workbook.write(outputStream.asOutputStream())
//
//        val outputStream = httpResponse.bufferFactory().allocateBuffer(1024)
//        workbook.write(outputStream.asOutputStream())
//
//
////        workbook.dispose()
//        Mono.just(outputStream)
//            .doOnNext { buffer ->
//                println("Writing buffer of size: ${buffer.readableByteCount()}")
//            }
    }
}