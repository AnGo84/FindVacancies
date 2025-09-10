package ua.findvacancies.common.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * A builder class to contain the job search criteria for querying job vacancies.
 * It's designed to be a value object, providing a clean way to pass multiple search parameters.
 *
 * @EqualsAndHashCode is used to ensure this class can be a key in a Map for caching.
 */
@Value
@Builder
@EqualsAndHashCode
//@Schema(description = "Criteria object used to filter job vacancies during search and scraping")
public class JobSearchCriteria {
    public static final int DEFAULT_MAX_PAGES_TO_SCRAPE = 5;
    public static final int DEFAULT_LAST_N_DAYS = 5;

    //@Schema(description = "Keywords to search for (e.g., 'java developer')")
    @Builder.Default
    Set<String> keywords =  new HashSet<>();

    //@Schema(description = "Words to ignore in the search results (e.g., 'junior', 'trainee')")
    @Builder.Default
    Set<String> ignoredWords = new HashSet<>();

    // @Schema(description = "Filter for vacancies posted in the last N days. Null means no date filter.")
    @Builder.Default
    Integer lastNDays = DEFAULT_LAST_N_DAYS;

    /*@Schema(description = "Maximum number of pages to scrape for a given search. Null means default: " +
            DEFAULT_MAX_PAGES_TO_SCRAPE)*/
    @Builder.Default
    Integer maxPagesToScrape = DEFAULT_MAX_PAGES_TO_SCRAPE;

    /**
     * Checks if the criteria is empty (no filters or pagination controls applied).
     *
     * @return true if no keywords, ignored words, date filter, or pagination controls are specified, false otherwise.
     */
    @JsonIgnore
    public boolean isEmpty() {
        return (keywords == null || keywords.isEmpty()) &&
                (ignoredWords == null || ignoredWords.isEmpty()) &&
                (lastNDays == null) &&
                (maxPagesToScrape == null);
    }

    /**
     * Checks if the criteria has keywords.
     *
     * @return true if there are keywords, false otherwise.
     */
    @JsonIgnore
    public boolean hasKeywords() {
        return keywords != null && !keywords.isEmpty();
    }

    /**
     * Checks if the criteria has ignoredWords.
     *
     * @return true if there are ignoredWords, false otherwise.
     */
    @JsonIgnore
    public boolean hasIgnoredWords() {
        return ignoredWords != null && !ignoredWords.isEmpty();
    }

    /**
     * Returns the maximum number of pages to scrape.
     * <p>
     * If {@code maxPagesToScrape} is {@code null}, this method returns the default value
     * defined in {@link JobSearchCriteria#DEFAULT_MAX_PAGES_TO_SCRAPE}.
     *
     * @return the configured maximum number of pages to scrape, or the default value if not set
     */
    @JsonIgnore
    public int getMaxPagesToScrapeOrDefault() {
        return Optional.ofNullable(maxPagesToScrape).orElse(JobSearchCriteria.DEFAULT_MAX_PAGES_TO_SCRAPE);
    }

}
