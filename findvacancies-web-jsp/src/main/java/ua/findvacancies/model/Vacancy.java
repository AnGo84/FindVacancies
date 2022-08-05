package ua.findvacancies.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;

@XmlType(propOrder = {"title", "url", "salary", "companyName", "city", "siteName", "date"})
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vacancy {
    @XmlAttribute(name = "Title")
    private String title;
    @XmlAttribute(name = "Salary")
    private String salary;
    @XmlAttribute(name = "City")
    private String city;
    @XmlAttribute(name = "CompanyName")
    private String companyName;
    @XmlAttribute(name = "SiteName")
    private String siteName;
    @XmlAttribute(name = "URL")
    private String url;
    @XmlAttribute(name = "Date")
    private Date date;
    @XmlAttribute(name = "Hot")
    private boolean isHot;

}
