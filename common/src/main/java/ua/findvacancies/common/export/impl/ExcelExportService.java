package ua.findvacancies.common.export.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ua.findvacancies.common.export.JobExportService;
import ua.findvacancies.common.model.ExportFormat;
import ua.findvacancies.common.model.JobVacancy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for exporting a list of JobVacancy objects to Excel (XLSX) format.
 * Uses Apache POI for Excel generation. The output is a raw byte array
 * of the binary XLSX file.
 */
@Slf4j
public class ExcelExportService implements JobExportService {

    private static CellStyle getHeaderRowStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        return headerStyle;
    }

    /**
     * Exports a collection of JobVacancy objects to an Excel (XLSX) file,
     * returned as a raw byte array.
     *
     * @param vacancies The collection of JobVacancy objects to export.
     * @return A byte array of the XLSX file, or an empty byte array if an error occurs or collection is empty.
     */
    @Override
    public byte[] export(Collection<JobVacancy> vacancies) {
        if (CollectionUtils.isEmpty(vacancies)) {
            log.warn("Attempted to export an empty or null collection of vacancies to Excel.");
            return new byte[0];
        }

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            final var sheet = workbook.createSheet("Job Vacancies");

            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = getHeaderRowStyle(workbook);
            List<String> fieldNames = createHeaderRow(headerRow, headerStyle);

            // Populate data rows
            int rowNum = 1;
            for (JobVacancy vacancy : vacancies) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < fieldNames.size(); i++) {
                    String fieldName = fieldNames.get(i);
                    Cell cell = row.createCell(i);
                    setCellValue(vacancy, fieldName, cell);
                }
            }

            // Auto-size columns for better readability
            for (int i = 0; i < fieldNames.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);
            log.info("Successfully exported {} vacancies to XLSX.", vacancies.size());
            return outputStream.toByteArray();

        } catch (IOException e) {
            log.error("Error exporting vacancies to XLSX: {}", e.getMessage(), e);
            return new byte[0];
        }
    }

    private void setCellValue(JobVacancy vacancy, String fieldName, Cell cell) {
        try {
            Field field = JobVacancy.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(vacancy);

            if (value instanceof LocalDate localDate) {
                cell.setCellValue(localDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
            } else if (value instanceof List) {
                cell.setCellValue(((List<?>) value).stream()
                        .map(Object::toString)
                        .collect(Collectors.joining(", ")));
            } else if (value != null) {
                cell.setCellValue(value.toString());
            } else {
                cell.setCellValue("");
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("Error accessing field '{}' for JobVacancy: {}", fieldName, e.getMessage());
            cell.setCellValue("ERROR");
        }
    }

    // Create header row dynamically based on JobVacancy fields
    private List<String> createHeaderRow(Row headerRow, CellStyle headerStyle) {
        Field[] fields = JobVacancy.class.getDeclaredFields();
        List<String> fieldNames = new ArrayList<>();

        for (Field field : fields) {
            if (!field.isSynthetic()) {
                String fieldName = field.getName();
                fieldNames.add(fieldName);
                Cell cell = headerRow.createCell(fieldNames.indexOf(fieldName));
                cell.setCellValue(capitalizeFirstLetter(fieldName));
                cell.setCellStyle(headerStyle);
            }
        }
        return fieldNames;
    }

    @Override
    public ExportFormat getFormat() { // Implement the new method
        return ExportFormat.XLSX;
    }

    /**
     * Helper method to capitalize the first letter of a string.
     */
    private String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}
