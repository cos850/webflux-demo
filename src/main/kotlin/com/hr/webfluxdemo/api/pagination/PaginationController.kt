package com.hr.webfluxdemo.api.pagination

import com.hr.webfluxdemo.domain.Product
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class PaginationController(
    private val paginationService: PaginationService,
) {

    @GetMapping("/page")
    fun getProductPage(
        @RequestParam("page", defaultValue = "0") page: Int,
        @RequestParam("size", defaultValue = "10") size: Int,
    ): ResponsePagination {
        return ResponsePagination(
            content = paginationService.page(page, size)
        )
    }

}

data class ResponsePagination(
    val content: List<Product>,
    val totalPages: Int = 0,
    val totalElements: Int = 0,
    val currentPage: Int = 0,
)