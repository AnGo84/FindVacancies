package ua.findvacancies.mvc.export;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ua.findvacancies.mvc.viewdata.Vacancy;
import ua.findvacancies.mvc.viewdata.VacancyWrapper;

import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.AbstractSet;
import java.util.List;

public class XMLDocument {
    private static final Logger rootLogger = LogManager.getRootLogger();

    private static final String DEFAULT_XML_FILE_NAME = "Vacancies.xml";

    public void buildXMLDocument(HttpServletResponse response, List<Vacancy> vacancyList){
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment; filename=" + DEFAULT_XML_FILE_NAME);
        try {
            File file = new File(DEFAULT_XML_FILE_NAME);
            writeVacanciesXMLFile(file, vacancyList);
            try (OutputStream out = response.getOutputStream();
                 FileInputStream in = new FileInputStream(file)) {
                byte[] buffer = new byte[4096];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }
        } catch (IOException | JAXBException e) {
            rootLogger.error("Export to XML error: " + e.getMessage());
        }
    }
    private void writeVacanciesXMLFile(File file, List<Vacancy> vacancyList) throws JAXBException {
        if (file != null || vacancyList != null || !vacancyList.isEmpty()) {

            JAXBContext context = JAXBContext.newInstance(VacancyWrapper.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            VacancyWrapper wrapper = new VacancyWrapper();
            wrapper.setVacancyList(vacancyList);
            // Маршаллируем и сохраняем XML в файл.
            m.marshal(wrapper, file);
        }
    }
}
