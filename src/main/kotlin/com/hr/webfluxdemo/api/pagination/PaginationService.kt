package com.hr.webfluxdemo.api.pagination

import com.hr.webfluxdemo.domain.Product
import org.springframework.stereotype.Service

@Service
class PaginationService(
    private val paginationRepository: PaginationRepository,
    private val paginationRepositoryWithDataBaseClient: PaginationRepositoryWithDataBaseClient
) {

    fun page(page: Int, size: Int): List<Product> {
        val offset = page * size

        return paginationRepository.findAllWithPagination(size, offset)
            .collectList()
            .block() ?: emptyList()
    }

    fun saveAll(list: List<Product>): List<Product> {
        return paginationRepository.saveAll(list)
            .collectList()
            .block() ?: emptyList()
    }

    fun pageWithDbClientRepo(page: Int, size: Int): Pagination<Product> {
        val offset = page * size

        val productContents: MutableList<Product>  = paginationRepositoryWithDataBaseClient.findAllWithPage(offset, size)
        val count: Int = paginationRepositoryWithDataBaseClient.count()

        return Pagination(
            page,
            size,
            contents = productContents,
            totalElement = count.toLong(),
            totalPage = if(count > 0) (count / size + if(count % size > 0) 1 else 0) else 0
        )
    }

}

data class Pagination<T>(
    val page: Int,
    val size: Int,
    val contents: List<T>,
    val totalElement: Long,
    val totalPage: Int,
)