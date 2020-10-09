package ua.findvacancies.mvc.viewdata;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by AnGo on 25.07.2017.
 */
@XmlRootElement(name = "VACANCIES")
public class VacancyWrapper {
    private List<Vacancy> vacancyList;

    @XmlElement(name = "vacancy")
    public List<Vacancy> getVacancyList() {
        return vacancyList;
    }

    public void setVacancyList(List<Vacancy> vacancyList) {
        this.vacancyList = vacancyList;
    }
}
