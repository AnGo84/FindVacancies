package ua.findvacancies.export;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.util.CollectionUtils;
import ua.findvacancies.model.Vacancy;
import ua.findvacancies.utils.AppDateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class ExportToExcel {

    public static File createExcelFile(Workbook workbook, String fileName) throws IOException {
        if (workbook!= null) {
            File outputFile = File.createTempFile(fileName, ".xlsx");
            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                workbook.write(fos);
            }
            return outputFile;
        }
        return null;
    }

    public static void createSheet(Workbook workbook, List<Vacancy> vacancyList){
        Sheet excelSheet = workbook.createSheet("Vacancies");
        setExcelHeader(excelSheet, getCellStyle(workbook));
        if (!CollectionUtils.isEmpty(vacancyList)){
            int rowCount = 1;
            for (Vacancy vacancy : vacancyList) {
                fillRowWithData(vacancy, excelSheet.createRow(rowCount++));
            }
        }
    }

    private static void setExcelHeader(org.apache.poi.ss.usermodel.Sheet excelSheet, CellStyle styleHeader) {
        //set Excel Header names
        Row header = excelSheet.createRow(0);
        header.createCell(0).setCellValue("Title");
        header.getCell(0).setCellStyle(styleHeader);
        header.createCell(1).setCellValue("URL");
        header.getCell(1).setCellStyle(styleHeader);
        header.createCell(2).setCellValue("Salary");
        header.getCell(2).setCellStyle(styleHeader);
        header.createCell(3).setCellValue("City");
        header.getCell(3).setCellStyle(styleHeader);
        header.createCell(4).setCellValue("Company Name");
        header.getCell(4).setCellStyle(styleHeader);
        header.createCell(5).setCellValue("Site Name");
        header.getCell(5).setCellStyle(styleHeader);
        header.createCell(6).setCellValue("Date");
        header.getCell(6).setCellStyle(styleHeader);
    }

    private static CellStyle getCellStyle(Workbook workbook) {
        CellStyle styleHeader = workbook.createCellStyle();
        styleHeader.setFont(getFont(workbook));
        return styleHeader;
    }

    private static Font getFont(Workbook workbook) {
        Font font = workbook.createFont();
        font.setFontName("Arial");
        font.setBold(true);
        return font;
    }

    private static void fillRowWithData(Vacancy vacancy, Row row) {
        row.createCell(0).setCellValue(vacancy.getTitle());
        row.createCell(1).setCellValue(vacancy.getUrl());
        row.createCell(2).setCellValue(vacancy.getSalary());
        row.createCell(3).setCellValue(vacancy.getCity());
        row.createCell(4).setCellValue(vacancy.getCompanyName());
        row.createCell(5).setCellValue(vacancy.getSiteName());
        row.createCell(6).setCellValue(AppDateUtils.formatToString(vacancy.getDate()));
    }

}
