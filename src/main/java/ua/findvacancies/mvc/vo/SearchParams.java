package ua.findvacancies.mvc.vo;

import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import ua.findvacancies.mvc.model.Provider;
import ua.findvacancies.mvc.model.Site;

import javax.validation.constraints.Min;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by AnGo on 24.07.2017.
 */
public class SearchParams {
    //    @NotBlank(message = "Enter words for search!")
    @NotBlank(message = "{searchParams.SearchLine.Blank}")
    private java.lang.String searchLine;

    //    @NotBlank(message="Enter days amount for search!")
    @NotBlank(message = "{searchParams.Days.Blank}")
    //@DecimalMin(value = "0", message = "The day value must be an integer and not an empty!")
//    @Min(value = 0, message = "The day value must be an integer!")
    @Min(value = 0, message = "{searchParams.Days.MinValue}")
    private java.lang.String days;

    //    @NotBlank(message = "{searchParams.Sites.Blank}")
    @NotEmpty(message = "{searchParams.Sites.Blank}")
    private Set<String> sites = new HashSet<>();

    public SearchParams() {
    }

    public SearchParams(java.lang.String searchLine, java.lang.String days) {
        this.searchLine = searchLine;
        this.days = days;
    }

    public SearchParams(String searchLine, String days, Set<String> sites) {
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


    public Set<Site> getEnumSites() {
        if (this.sites == null || this.sites.isEmpty()) {
            return null;
        }
        Set<Site> sites = new HashSet<>();
        for (String site : this.sites) {
            sites.add(Site.valueOf(site.toUpperCase()));
        }
        return sites;
    }

    public Set<Provider> getProviderSet() {
        Set<Site> siteSet = this.getEnumSites();
        if (this.sites == null || this.sites.isEmpty()|| siteSet==null || siteSet.isEmpty()) {
            return null;
        }
        Set<Provider> providerSet = new HashSet<>();

        for (Site site : siteSet) {
            providerSet.add(new Provider(site.getStrategy()));
        }
        Provider[] tempProviders = providerSet.toArray(new Provider[providerSet.size()]);
        return providerSet;
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
