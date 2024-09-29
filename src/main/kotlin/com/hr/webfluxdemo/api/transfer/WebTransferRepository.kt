package com.hr.webfluxdemo.api.transfer

import com.hr.webfluxdemo.api.transfer.dto.TransferSearchCondition
import com.hr.webfluxdemo.api.transfer.dto.TransferWithUserName
import com.hr.webfluxdemo.domain.Transfer
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.data.r2dbc.convert.MappingR2dbcConverter
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository

@Repository
class WebTransferRepository(
    private val databaseClient: DatabaseClient,
    private val converter: MappingR2dbcConverter,
) {

    suspend fun findAllWithPage(offset: Int, limit: Int, condition: TransferSearchCondition): List<TransferWithUserName> {
        var sql: String =
            """
                SELECT t.*, u.nickname 
                FROM transfer t left outer join user u ON (t.user_id = u.id)
                WHERE
                (
                (t.updated_at BETWEEN :startDate AND :endDate)
                OR
                (t.updated_at IS NULL AND t.created_at BETWEEN :startDate AND :endDate)
                )
                ${addOptionalConditions(condition)}
            """.trimIndent()

        sql += "\n LIMIT :limit OFFSET :offset "

        println(sql)

        return databaseClient.sql(sql)
            .bind("startDate", condition.searchDate.first.atStartOfDay())
            .bind("endDate", condition.searchDate.second.atTime(23, 59, 59, 999))
            .bind("offset", offset)
            .bind("limit", limit)
            .map { row, metadata -> TransferWithUserName(
                transfer = converter.read(Transfer::class.java, row, metadata),
                userNickname = row.get("nickname", String::class.java)
            )
            }
            .all()
            .collectList()
            .awaitSingle()
    }

    fun count(condition: TransferSearchCondition): Int{
        val sql = """
            SELECT COUNT(*) 
            FROM transfer t left outer join user u ON (t.user_id = u.id)
            WHERE
            (
            (t.updated_at BETWEEN :startDate AND :endDate)
            OR
            (t.updated_at IS NULL AND t.created_at BETWEEN :startDate AND :endDate)
            )
            ${addOptionalConditions(condition)}
        """.trimIndent()

        return databaseClient.sql(sql)
            .bind("startDate", condition.searchDate.first.atStartOfDay())
            .bind("endDate", condition.searchDate.second.atTime(23, 59, 59, 999))
            .map { row -> row.get(0, Int::class.java)}
            .one()
            .block() ?: 0
    }

    fun addOptionalConditions(request: TransferSearchCondition?): String {
        if (null == request) {
            return ""
        }

        var where = ""

        request.accountNumber?.let {
            where += " AND t.account_number LIKE '%${it}%'"
        }

        request.memo?.let {
            where += " AND t.memo LIKE '%${it}%'"
        }

        request.userNickname?.let {
            where += " AND u.nickname LIKE '%${it}%'"
        }

        return where
    }


}