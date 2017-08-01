package ua.findvacancies.mvc.export;

import ua.findvacancies.mvc.vo.Vacancy;
import ua.findvacancies.mvc.vo.VacancyWrapper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.List;

/**
 * Created by AnGo on 25.07.2017.
 */
public class XMLDocument {
    public static void writeVacanciesXMLFile(File file, List<Vacancy> vacancyList) throws JAXBException {
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
