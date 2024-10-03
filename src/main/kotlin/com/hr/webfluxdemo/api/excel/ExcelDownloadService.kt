package com.hr.webfluxdemo.api.excel

import org.apache.poi.ss.usermodel.*
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import java.lang.reflect.Field
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

abstract class ExcelDownloadService<Q, D : Any> {

    val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")

    suspend fun generateExcel(
            headerInfo: Class<D>,
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
    protected fun createWorkbook(memoryRowSize: Int = 10) = SXSSFWorkbook(memoryRowSize)

    /**
     * @ExcelHeader를 기준으로 엑셀의 헤더 작성
     *
     * @param clazz: 작성할 DTO의 Java Class
     */
    protected fun Sheet.writeHeader(clazz: Class<D>) {
        val row = createRow(0)
        val headerStyle = workbook.createHeaderStyle()

        val fieldList = clazz.declaredFields
        var cellOffset = 0

        // ExcelHeader의 headerName을 기준으로 헤더를 작성함
        for(field in fieldList) {
            if(isSkipField(field)) {
                continue
            }

            val excelHeader = field.getAnnotation(ExcelHeader::class.java)
            val cell = row.createCell(cellOffset)
            cell.setCellValue(excelHeader.headerName)
            cell.cellStyle = headerStyle

            // SXSSFWorkbook은 메모리 효율성을 위해 autoColumnSize 를 사용할 수 없음
            // cell 넓이: 글자수 * 256 (10pt 1글자 크기: 256)
            val cellWidth = (excelHeader.maxDataLength + 2) * 256
            setColumnWidth(cellOffset, cellWidth)

            cellOffset++
        }
    }


    /**
     * 엑셀의 실제 데이터 작성
     *
     * @param startRowOffset: 시작 행 offset
     * @param dataList: 작성할 데이터
     */
    protected fun Sheet.writeData(startRowOffset:Int, dataList: List<D>){
        val cellStyle = workbook.createDataStyle()

        for((rowIdx, data) in dataList.withIndex()) {
            val row = createRow(startRowOffset + rowIdx)
            row.writeData(data)
        }
    }

    protected fun Row.writeData(data: D) {
        var cellOffset = 0
        for(field in data::class.java.declaredFields) {
            if(isSkipField(field)) {
                continue
            }

            // java에서 private 가 되기 때문에 접근 가능하도록 변경해줘야 함
            field.isAccessible = true

            val cell = createCell(cellOffset++)
            val value = field.get(data)

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

    /**
     * static method를 만드는 경우 Companion 필드가 생성되므로 제외
     *
     * @param field
     */
    private fun isSkipField(field: Field) = field.name == "Companion"

    protected fun LocalDateTime.toCellFormat(): String {
        return format(DATE_TIME_FORMATTER)
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