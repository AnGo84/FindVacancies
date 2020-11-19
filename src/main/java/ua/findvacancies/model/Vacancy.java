package ua.findvacancies.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;

@XmlType(propOrder = {"title", "url", "salary", "companyName", "city", "siteName", "date"})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vacancy {
    @XmlElement(name = "Title")
    private String title;
    @XmlElement(name = "Salary")
    private String salary;
    @XmlElement(name = "City")
    private String city;
    @XmlElement(name = "CompanyName")
    private String companyName;
    @XmlElement(name = "SiteName")
    private String siteName;
    @XmlElement(name = "URL")
    private String url;
    @XmlElement(name = "Date")
    private Date date;
    @XmlElement(name = "Hot")
    private boolean isHot;

}
