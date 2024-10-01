package com.hr.webfluxdemo.api.excel

import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties

abstract class ExcelDownloadService<Q, D : Any> {

    suspend fun generateExcel(
            headerInfo: KClass<D>,
            searchCondition: Q
    ): SXSSFWorkbook {
        val memoryRowSize = 1000
        val workbook = createWorkbook(memoryRowSize)
        val sheet = workbook.createSheet(getSheetName())

        // header 작성
        sheet.writeHeader(headerInfo)

        val totalCount = getTotalData(searchCondition)

        if(totalCount == 0) return workbook

        var offset = 0
        while (offset < totalCount) {
            val dataList = getDataList(offset, memoryRowSize, searchCondition)

            sheet.writeData(offset+1, dataList)
            offset += memoryRowSize
        }

        return workbook
    }

    protected abstract fun getSheetName(): String
    protected abstract suspend fun getTotalData(searchCondition: Q): Int
    protected abstract suspend fun getDataList(offset: Int, size: Int, searchCondition: Q): List<D>

    /**
     * SXSSFWorkbook 반환
     * : XSSFWorkbook 에서 성능이 강화된 것으로, 전달한 개수만큼 메모리에 유지하고 나머지는 임시파일에 저장한다.
     *
     * - rowAccessWindowSize: 기본 값 1000
     */
    protected fun createWorkbook(memoryRowSize: Int = 1000) = SXSSFWorkbook(memoryRowSize)

    protected fun Sheet.writeHeader(clazz: KClass<D>) {
        val row = createRow(0)
        val headerStyle = workbook.createHeaderStyle()
        val propertyList = clazz.memberProperties

        // ExcelHeader의 headerName을 기준으로 헤더를 작성함
        propertyList.forEachIndexed { i, property ->
            val excelHeader = property.findAnnotation<ExcelHeader>()
            if(null != excelHeader) {
                val cell = row.createCell(i)
                cell.setCellValue(excelHeader.headerName)
                cell.cellStyle = headerStyle
            }
        }
    }

    protected fun Sheet.writeData(startRowOffset:Int, dataList: List<D>){
        val cellStyle = workbook.createDataStyle()

        for((rowIdx, data) in dataList.withIndex()) {
            val row = createRow(startRowOffset + rowIdx)
            row.writeData(data)
        }
    }

    protected fun Row.writeData(data: D) {
        for((cellIdx, property) in data::class.memberProperties.withIndex()) {
            val cell = createCell(cellIdx)
            val value = property.call(data)

            value?.let {
                when(value) {
                    is String -> cell.setCellValue(value)
                    is Boolean -> cell.setCellValue(value)
                    is LocalDateTime -> cell.setCellValue(value.toCellFormat())
                    is LocalDate -> cell.setCellValue(value)
                    else -> cell.setCellValue(value.toString())
                }
            }
        }
    }

    protected fun LocalDateTime.toCellFormat(): String {
        return "${toLocalDate()} ${toLocalTime()}"
    }


    /**
     * 이 아래로는 Style 관련 함수
     */

    protected fun Workbook.createHeaderStyle(): CellStyle {
        return createCellStyle().apply {
            fillForegroundColor = IndexedColors.GREY_80_PERCENT.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER

            borderBottom = BorderStyle.THICK
            setFont(createHeaderFont())
        }
    }

    protected fun Workbook.createHeaderFont(): Font {
        return createFont().apply {
            fontHeightInPoints = 14
            bold = true
            color = IndexedColors.WHITE.index
        }
    }

    protected fun Workbook.createDataStyle(): CellStyle {
        return createCellStyle().apply {
            alignment = HorizontalAlignment.LEFT
            setFont(createDataFont())
        }
    }

    protected fun Workbook.createDataFont(): Font {
        return createFont().apply {
            fontHeightInPoints = 12
            color = IndexedColors.BLACK.index
        }
    }
}