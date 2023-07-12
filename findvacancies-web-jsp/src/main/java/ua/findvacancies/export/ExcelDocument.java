package ua.findvacancies.export;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsView;
import ua.findvacancies.model.Vacancy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExcelDocument extends AbstractXlsView {
    public static final String OBJECT_NAME = "vacancyList";

    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest request, HttpServletResponse response) {
        log.info("Export to Excel");
        response.setHeader("Content-Disposition", "attachment; filename=excelVacancies.xls");

        List<Vacancy> vacancyList = new ArrayList<>();
        final Object modelObject = model.get(OBJECT_NAME);
        if (modelObject != null) {
            vacancyList = (List<Vacancy>) modelObject;

        }
        ExportToExcel.createSheet(workbook, vacancyList);
        log.info("Exported to Excel {} objects", vacancyList.size());
    }

}
