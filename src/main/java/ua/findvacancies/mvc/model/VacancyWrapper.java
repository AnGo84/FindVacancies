package ua.findvacancies.mvc.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

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
