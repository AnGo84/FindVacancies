package ua.findvacancies.model.viewdata;


import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.HashSet;
import java.util.Set;

public class ViewSearchParams {
    @NotBlank(message = "{viewSearchParams.SearchLine.Blank}")
    private String searchLine;

    @NotBlank(message = "{viewSearchParams.Days.Blank}")
    @Min(value = 0, message = "{viewSearchParams.Days.MinValue}")
    private String days;

    @NotEmpty(message = "{viewSearchParams.Sites.Blank}")
    private Set<String> sites = new HashSet<>();

    public ViewSearchParams() {
    }

    public ViewSearchParams(String searchLine, String days) {
        this.searchLine = searchLine;
        this.days = days;
    }

    public ViewSearchParams(String searchLine, String days, Set<String> sites) {
        this.searchLine = searchLine;
        this.days = days;
        this.sites = sites;
    }

    public java.lang.String getSearchLine() {
        return searchLine;
    }

    public void setSearchLine(java.lang.String searchLine) {
        this.searchLine = searchLine;
    }

    public java.lang.String getDays() {
        return days;
    }

    public void setDays(java.lang.String days) {
        this.days = days;
    }

    public Set<String> getSites() {
        return sites;
    }

    public void setSites(Set<String> sites) {
        this.sites = sites;
    }

    @Override
    public java.lang.String toString() {
        final StringBuilder sb = new StringBuilder("SearchParams{");
        sb.append("searchLine='").append(searchLine).append('\'');
        sb.append(", days='").append(days).append('\'');
        sb.append(", sites=").append(sites);
        sb.append('}');
        return sb.toString();
    }
}
