package ua.findvacancies.common.model;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JacksonXmlRootElement(localName = "JobVacancies")
//@Schema(description = "Wrapper for a list of job vacancies serialized to XML")
public class JobVacancyListWrapper {

    /**
     * A list of job vacancies to be serialized.
     * <p>
     * {@code @JacksonXmlElementWrapper(useWrapping = false)} disables the default wrapping element around the list.
     * {@code @JacksonXmlProperty(localName = "JobVacancy")} sets each item’s XML tag name to {@code <JobVacancy>}.
     * <p>
     * Example XML output:
     * <pre>{@code
     * <JobVacancies>
     *     <JobVacancy>...</JobVacancy>
     *     <JobVacancy>...</JobVacancy>
     * </JobVacancies>
     * }</pre>
     */
    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "JobVacancy")
    private List<JobVacancy> jobVacancies;
}
