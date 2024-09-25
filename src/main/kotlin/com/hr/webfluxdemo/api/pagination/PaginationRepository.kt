package com.hr.webfluxdemo.api.pagination

import com.hr.webfluxdemo.domain.Product
import org.springframework.data.r2dbc.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import reactor.core.publisher.Flux


interface PaginationRepository: ReactiveCrudRepository<Product, Long> {

    @Query("SELECT * FROM product LIMIT :limit OFFSET :offset")
    fun findAllWithPagination(limit: Int, offset: Int) : Flux<Product>
}