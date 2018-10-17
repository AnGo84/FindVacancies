package ua.findvacancies.mvc.vo;


import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.Date;

/**
 * Created by AnGo on 04.05.2016.
 */
@XmlType(propOrder = {"title", "url", "salary", "companyName", "city", "siteName", "date"})
public class Vacancy {
    private String title;
    private String salary;
    private String city;
    private String companyName;
    private String siteName;
    private String url;
    private Date date;


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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vacancy vacancy = (Vacancy) o;

        if (title != null ? !title.equals(vacancy.title) : vacancy.title != null) return false;
        if (salary != null ? !salary.equals(vacancy.salary) : vacancy.salary != null) return false;
        if (city != null ? !city.equals(vacancy.city) : vacancy.city != null) return false;
        if (companyName != null ? !companyName.equals(vacancy.companyName) : vacancy.companyName != null) return false;
        if (siteName != null ? !siteName.equals(vacancy.siteName) : vacancy.siteName != null) return false;
        if (date != null ? !date.equals(vacancy.date) : vacancy.date != null) return false;

        return url != null ? url.equals(vacancy.url) : vacancy.url == null;

    }

    @Override
    public int hashCode() {
        int result = title != null ? title.hashCode() : 0;
        result = 31 * result + (salary != null ? salary.hashCode() : 0);
        result = 31 * result + (city != null ? city.hashCode() : 0);
        result = 31 * result + (companyName != null ? companyName.hashCode() : 0);
        result = 31 * result + (siteName != null ? siteName.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Vacancy{");
        sb.append("title='").append(title).append('\'');
        sb.append(", salary='").append(salary).append('\'');
        sb.append(", city='").append(city).append('\'');
        sb.append(", companyName='").append(companyName).append('\'');
        sb.append(", siteName='").append(siteName).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append(", date='").append(date).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
