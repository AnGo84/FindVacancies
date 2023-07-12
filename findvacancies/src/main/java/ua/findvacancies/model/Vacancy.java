package ua.findvacancies.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@XmlType(propOrder = {"title", "url", "salary", "companyName", "city", "siteName", "date", "isHot"})
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
