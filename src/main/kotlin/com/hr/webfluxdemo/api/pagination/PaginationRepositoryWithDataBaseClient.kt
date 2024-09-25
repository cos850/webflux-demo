package com.hr.webfluxdemo.api.pagination

import com.hr.webfluxdemo.domain.Product
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository

@Repository
class PaginationRepositoryWithDataBaseClient(
    private val dbClient: DatabaseClient
) {

    fun findAllWithPage(offset: Int, limit: Int): MutableList<Product> {

        return dbClient.sql("SELECT * FROM product LIMIT :limit OFFSET :offset")
            .bind("offset", offset)
            .bind("limit", limit)
            .map { row -> Product(
                    name = row.get("name", String::class.java)!!,
                    price = row.get("price", Int::class. java)!!,
                    id = row.get("id", Long::class.java),
                )
            }
            .all()
            .collectList()
            .block()!!
    }

    fun count(): Int{
        return dbClient.sql("SELECT COUNT(*) FROM product")
            .map { row -> row.get(0, Int::class.java)}
            .one()
            .block() ?: 0
    }

}