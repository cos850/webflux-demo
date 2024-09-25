package com.hr.webfluxdemo.api.pagination

import com.hr.webfluxdemo.domain.Product
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class PaginationServiceTest {

    private val paginationService: PaginationService
    private val paginationRepository: PaginationRepository

    // 필드 주입으로 변경
    @Autowired
    constructor(paginationService: PaginationService, paginationRepository: PaginationRepository) {
        this.paginationService = paginationService
        this.paginationRepository = paginationRepository
    }

    @BeforeEach
    fun clean() {
        paginationRepository.deleteAll()
    }

    @Test
    @DisplayName("Webflux + R2DBC 페이지네이션 테스트")
    fun pagination(){
        //given
        val dummyData = mutableListOf<Product>()
        for (i: Int in 1..20) {
            val product = Product("제품-${i}", 1000)
            dummyData.add(product)
        }
        paginationService.saveAll(dummyData)

        val page1 = 0
        val size1 = 10

        val page2 = 1
        val size2 = 10

        // when
        val result1 = paginationService.page(page1, size1)
        val result2 = paginationService.page(page2, size2)

        println("result1(${result1.size}) = ${result1}")
        println("result2(${result2.size}) = ${result2}")

        // then

    }


    @Test
    @DisplayName("Webflux + R2DBC 페이지네이션 dbClient를 통한 직접 구현")
    fun pagination2(){
        //given
        val dummyData = mutableListOf<Product>()
        for (i: Int in 1..20) {
            val product = Product("제품-${i}", 1000)
            dummyData.add(product)
        }
        paginationService.saveAll(dummyData)

        val page1 = 0
        val size1 = 10

        val page2 = 1
        val size2 = 10

        val result1 = paginationService.pageWithDbClientRepo(page1, size1)
        val result2 = paginationService.pageWithDbClientRepo(page2, size2)

        println("result1(${result1.size}) = ${result1}")
        println("result2(${result2.size}) = ${result2}")

    }
}