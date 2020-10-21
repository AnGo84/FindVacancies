package ua.findvacancies.mvc.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;
import java.util.Objects;

@XmlType(propOrder = {"title", "url", "salary", "companyName", "city", "siteName", "date"})
public class Vacancy {
    private String title;
    private String salary;
    private String city;
    private String companyName;
    private String siteName;
    private String url;
    private Date date;
    private boolean isHot;

    public Vacancy() {
        this.title = "";
        this.salary = "";
        this.city = "";
        this.companyName = "";
        this.siteName = "";
        this.url = "";
        this.date = new Date();
    }

    @XmlElement(name = "Title")
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @XmlElement(name = "Salary")
    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    @XmlElement(name = "City")
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @XmlElement(name = "CompanyName")
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    @XmlElement(name = "SiteName")
    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    @XmlElement(name = "URL")
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @XmlElement(name = "Date")
    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @XmlElement(name = "Hot")
    public boolean isHot() {
        return isHot;
    }

    public void setHot(boolean hot) {
        isHot = hot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vacancy vacancy = (Vacancy) o;
        return isHot == vacancy.isHot &&
                Objects.equals(title, vacancy.title) &&
                Objects.equals(salary, vacancy.salary) &&
                Objects.equals(city, vacancy.city) &&
                Objects.equals(companyName, vacancy.companyName) &&
                Objects.equals(siteName, vacancy.siteName) &&
                Objects.equals(url, vacancy.url) &&
                Objects.equals(date, vacancy.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, salary, city, companyName, siteName, url, date, isHot);
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Vacancy{");
        sb.append("title='").append(title).append('\'');
        sb.append(", salary='").append(salary).append('\'');
        sb.append(", city='").append(city).append('\'');
        sb.append(", companyName='").append(companyName).append('\'');
        sb.append(", siteName='").append(siteName).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", date=").append(date);
        sb.append(", isHot=").append(isHot);
        sb.append('}');
        return sb.toString();
    }
}
