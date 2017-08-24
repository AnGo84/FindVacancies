package ua.findvacancies.mvc.vo;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

/**
 * Created by AnGo on 24.07.2017.
 */
public class SearchParams {
//    @NotBlank(message = "Enter words for search!")
    @NotBlank(message = "{searchParams.SearchLine.Blank}")
    private String searchLine;

//    @NotBlank(message="Enter days amount for search!")
    @NotBlank(message="{searchParams.Days.Blank}")
    //@DecimalMin(value = "0", message = "The day value must be an integer and not an empty!")
//    @Min(value = 0, message = "The day value must be an integer!")
    @Min(value = 0, message = "{searchParams.Days.MinValue}")
    private String days;

    public SearchParams() {
    }

    public SearchParams(String searchLine, String days) {
        this.searchLine = searchLine;
        this.days = days;
    }

    public String getSearchLine() {
        return searchLine;
    }

    public void setSearchLine(String searchLine) {
        this.searchLine = searchLine;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SearchParams{");
        sb.append("searchLine='").append(searchLine).append('\'');
        sb.append(", days=").append(days);
        sb.append('}');
        return sb.toString();
    }
}
