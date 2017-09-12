package ua.findvacancies.mvc.export;

import org.apache.poi.ss.usermodel.*;
import org.springframework.web.servlet.view.document.AbstractXlsView;
import ua.findvacancies.mvc.vo.Vacancy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;


public class ExcelDocument extends AbstractXlsView {

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) throws Exception {
        //New Excel sheet
        Sheet excelSheet = workbook.createSheet("Vacancies");
        //Excel file name change
        response.setHeader("Content-Disposition", "attachment; filename=excelDocument.xls");

        Font font = workbook.createFont();
        font.setFontName("Arial");
        //font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        //font.setColor(HSSFColor.BLACK.index);

        //Create Style for header
        CellStyle styleHeader = workbook.createCellStyle();
        //styleHeader.setFillForegroundColor(HSSFColor.WHITE.index);
        //styleHeader.setFillPattern(CellStyle.SOLID_FOREGROUND);
        styleHeader.setFont(font);


        //Set excel header
        setExcelHeader(excelSheet, styleHeader);

        //Get data from model
        final Object modelObject = model.get("modelObject");
        if (modelObject != null) {
            List<Vacancy> vacancyList = (List<Vacancy>) modelObject;
            if (vacancyList != null && !vacancyList.isEmpty()) {
                int rowCount = 1;
                for (Vacancy vacancy : vacancyList) {
                    Row row = excelSheet.createRow(rowCount++);
                    row.createCell(0).setCellValue(vacancy.getTitle());
                    row.createCell(1).setCellValue(vacancy.getUrl());
                    row.createCell(2).setCellValue(vacancy.getSalary());
                    row.createCell(3).setCellValue(vacancy.getCity());
                    row.createCell(4).setCellValue(vacancy.getCompanyName());
                    row.createCell(5).setCellValue(vacancy.getSiteName());
                    row.createCell(6).setCellValue(vacancy.getDate());
                }
            }
        }
    }

    public void setExcelHeader(Sheet excelSheet, CellStyle styleHeader) {
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
        header.createCell(5).setCellValue("String Name");
        header.getCell(5).setCellStyle(styleHeader);
        header.createCell(6).setCellValue("Date");
        header.getCell(6).setCellStyle(styleHeader);
    }

}
