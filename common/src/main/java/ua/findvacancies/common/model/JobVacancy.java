package ua.findvacancies.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a single job vacancy extracted from a job board.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JacksonXmlRootElement(localName = "JobVacancy")
//@Schema(description = "Represents a job vacancy with detailed information.")
public class JobVacancy {

    //@Schema(description = "Job title (e.g., 'Java Developer')", example = "Java Developer")
    private String title;

    //@Schema(description = "Date the vacancy was posted or last updated", example = "2025-08-01")
    private LocalDate datePosted;

    //@Schema(description = "Job board source", example = "DJINNI_CO")
    private JobSite jobSite;

    //@Schema(description = "Direct URL to the job vacancy", example = "https://djinni.co/jobs/12345-java-dev")
    private String url;

    //@Schema(description = "Name of the hiring company", example = "TechCorp")
    private String companyName;

    //@Schema(description = "Location of the job", example = "Kyiv")
    private String city;

    //@Schema(description = "Salary range (e.g., '$2000 - $3000' or 'Negotiable')", example = "$2000 - $3000")
    private String salary;

    //@Schema(description = "Indicates if the vacancy is marked as 'hot'", example = "true")
    @JsonProperty("isHot")
    private boolean isHot;

    //@Schema(description = "Full job description text")
    private String description;

    @JacksonXmlElementWrapper(localName = "skills")
    @JacksonXmlProperty(localName = "skill")
    //@Schema(description = "List of key skills/technologies required", example = "[\"Java\", \"Spring Boot\"]")
    private List<String> skills;
}
