package ua.findvacancies.export;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import ua.findvacancies.model.Vacancy;
import ua.findvacancies.model.VacancyWrapper;


import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Slf4j
public class XMLDocument {

    private static final String DEFAULT_XML_FILE_NAME = "Vacancies.xml";

    private final JAXBContext jaxbContext;
    private final Marshaller marshaller;

    public XMLDocument() throws JAXBException {
        jaxbContext = JAXBContext.newInstance(VacancyWrapper.class);
        marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    }

    public void build(HttpServletResponse response, List<Vacancy> vacancyList) throws JAXBException, IOException {
        response.setContentType("application/octet-stream");
        response.setHeader("Content-disposition", "attachment; filename=" + DEFAULT_XML_FILE_NAME);

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

    }

    private void writeVacanciesXMLFile(File file, List<Vacancy> vacancyList) throws JAXBException {
        if (file != null && !CollectionUtils.isEmpty(vacancyList)) {
            marshaller.marshal(new VacancyWrapper(vacancyList), file);
        }
    }

}
