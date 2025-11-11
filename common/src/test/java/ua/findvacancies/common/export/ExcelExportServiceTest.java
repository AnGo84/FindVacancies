package ua.findvacancies.common.export;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;
import ua.findvacancies.common.TestDataFactory;
import ua.findvacancies.common.export.impl.ExcelExportService;
import ua.findvacancies.common.model.ExportFormat;
import ua.findvacancies.common.model.JobVacancy;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExcelExportServiceTest {

    private final ExcelExportService excelExportService = new ExcelExportService();

    @Test
    void testExportEmptyList() {
        // Given an empty search criteria
        List<JobVacancy> emptyList = new ArrayList<>();

        // When
        byte[] result = excelExportService.export(emptyList);

        // Then
        assertNotNull(result, "Exporting an empty list should return a non-null byte array.");
        assertEquals(0, result.length, "Empty list should result in an empty byte array.");
    }

    @Test
    void testExportSingleVacancy() throws IOException {
        // Given a list with a single vacancy
        JobVacancy jobVacancy = TestDataFactory.buildJovVacancy(
                "Java Developer",
                LocalDate.of(2025, 7, 15));
        List<JobVacancy> vacancies = List.of(jobVacancy);

        // When
        byte[] excelBytes = excelExportService.export(vacancies);

        // Then
        assertNotNull(excelBytes, "Exported Excel bytes should not be null.");
        assertTrue(excelBytes.length > 0, "Exported Excel bytes should not be empty.");

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            Sheet sheet = workbook.getSheetAt(0);
            assertNotNull(sheet, "Sheet should exist.");
            assertEquals("Job Vacancies", sheet.getSheetName(), "Sheet name should be 'Job Vacancies'.");

            // Verify header row (assuming order from reflection)
            // This test is brittle if field order changes. For robust tests, check cell values by header name.
            Row headerRow = sheet.getRow(0);
            assertNotNull(headerRow, "Header row should exist.");
            assertEquals("Title", headerRow.getCell(0).getStringCellValue());
            assertEquals("DatePosted", headerRow.getCell(1).getStringCellValue());
            assertEquals("JobSite", headerRow.getCell(2).getStringCellValue());
            assertEquals("Url", headerRow.getCell(3).getStringCellValue());
            assertEquals("CompanyName", headerRow.getCell(4).getStringCellValue());
            assertEquals("City", headerRow.getCell(5).getStringCellValue());
            assertEquals("Salary", headerRow.getCell(6).getStringCellValue());
            assertEquals("IsHot", headerRow.getCell(7).getStringCellValue());
            assertEquals("Description", headerRow.getCell(8).getStringCellValue());
            assertEquals("Skills", headerRow.getCell(9).getStringCellValue());

            // Verify data row
            Row dataRow = sheet.getRow(1);
            assertNotNull(dataRow, "Data row should exist.");
            assertEquals(jobVacancy.getTitle(), dataRow.getCell(0).getStringCellValue());
            assertEquals("2025-07-15", dataRow.getCell(1).getStringCellValue()); // LocalDate formatted as string
            assertEquals(jobVacancy.getJobSite().name(), dataRow.getCell(2).getStringCellValue());
            assertEquals(jobVacancy.getUrl(), dataRow.getCell(3).getStringCellValue());
            assertEquals(jobVacancy.getCompanyName(), dataRow.getCell(4).getStringCellValue());
            assertEquals(jobVacancy.getCity(), dataRow.getCell(5).getStringCellValue());
            assertEquals(jobVacancy.getSalary(), dataRow.getCell(6).getStringCellValue());
            assertEquals(String.valueOf(jobVacancy.isHot()), dataRow.getCell(7).getStringCellValue());
            assertEquals(jobVacancy.getDescription(), dataRow.getCell(8).getStringCellValue());
            assertFalse(dataRow.getCell(9).getStringCellValue().isEmpty());
        }
    }

    @Test
    void testExportMultipleVacancies() throws IOException {
        // Given
        JobVacancy vacancy1 = TestDataFactory.buildJovVacancy("Dev 1");
        JobVacancy vacancy2 = TestDataFactory.buildJovVacancy("Dev 2");
        List<JobVacancy> vacancies = List.of(vacancy1, vacancy2);

        // When
        byte[] excelBytes = excelExportService.export(vacancies);

        // Then
        assertNotNull(excelBytes);

        try (Workbook workbook = new XSSFWorkbook(new ByteArrayInputStream(excelBytes))) {
            org.apache.poi.ss.usermodel.Sheet sheet = workbook.getSheetAt(0);
            assertEquals(3, sheet.getPhysicalNumberOfRows(), "Should have 1 header row + 2 data rows.");
            assertEquals("Dev 1", sheet.getRow(1).getCell(0).getStringCellValue());
            assertEquals("Dev 2", sheet.getRow(2).getCell(0).getStringCellValue());
        }
    }

    @Test
    void testGetFormat() {
        assertEquals(ExportFormat.XLSX, excelExportService.getFormat());
    }

}
