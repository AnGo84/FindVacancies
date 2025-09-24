package ua.findvacancies.common.model.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.JobSite;

import java.util.Set;

@Data
@AllArgsConstructor
public class SearchCriteriaDto {

    public static final String VIEW_PARAM = "searchCriteria";

    @NotBlank(message = "{searchParams.SearchLine.Blank}")
    private String searchLine;

    //@NotBlank(message = "{searchParams.Days.Blank}")
    @Min(value = 0, message = "{searchParams.Days.MinValue}")
    private int days;

    @NotEmpty(message = "{searchParams.Sites.Blank}")
    private Set<JobSite> sites;

    private String ignoreWords;

    public SearchCriteriaDto() {
        this.days = JobSearchCriteria.DEFAULT_LAST_N_DAYS;
    }
}
