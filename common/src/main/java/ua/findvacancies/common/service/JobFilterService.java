package ua.findvacancies.common.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.JobVacancy;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Service responsible for applying filtering logic to a list of JobVacancy objects
 * based on provided JobSearchCriteria. This logic is common across all scrapers.
 */
@Slf4j
public class JobFilterService {

    /**
     * Filters a list of JobVacancy objects based on the provided JobSearchCriteria.
     * This method is useful for post-processing a list of already scraped vacancies.
     *
     * @param rawVacancies The list of raw (unfiltered) job vacancies.
     * @param criteria     The search criteria to apply.
     * @return A filtered list of JobVacancy objects.
     */
    public List<JobVacancy> filter(final List<JobVacancy> rawVacancies, final JobSearchCriteria criteria) {
        if (CollectionUtils.isEmpty(rawVacancies)) {
            log.info("The list of job vacancies is empty.");
            return Collections.emptyList();
        }

        if (criteria == null || criteria.isEmpty()) {
            log.info("No search criteria provided or criteria is empty. Returning all {} raw vacancies.",
                    rawVacancies.size());
            return rawVacancies;
        }

        log.info("Applying filters to {} raw vacancies.", rawVacancies.size());

        return rawVacancies.stream()
                .filter(vacancy -> matches(vacancy, criteria))
                .toList();
    }

    /**
     * Checks if a single JobVacancy matches the given JobSearchCriteria.
     * This method is intended for use during the scraping process to filter
     * vacancies before adding them to a list, saving memory.
     *
     * @param vacancy  The JobVacancy object to check.
     * @param criteria The search criteria to apply.
     * @return true if the vacancy matches all criteria, false otherwise.
     */
    public boolean matches(final JobVacancy vacancy, final JobSearchCriteria criteria) {
        if (criteria == null || criteria.isEmpty()) {
            return true;
        }

        // Combine title and description for keyword/ignored word checks
        // Ensure description is not null, even if it's a placeholder like "N/A"
        String titleAndDescription = (vacancy.getTitle() + " "
                + Optional.ofNullable(vacancy.getDescription()).orElse(""))
                .toLowerCase();

        // 1. Keyword filter
        if (criteria.hasKeywords()) {
            boolean matchesKeyword = criteria.getKeywords().stream()
                    .anyMatch(keyword -> titleAndDescription.contains(keyword.toLowerCase()));
            if (!matchesKeyword) {
                log.debug("Vacancy '{}' filtered out by keywords.", vacancy.getTitle());
                return false;
            }
        }

        // 2. Ignored word filter
        if (criteria.hasIgnoredWords()) {
            boolean containsIgnoredWord = criteria.getIgnoredWords().stream()
                    .anyMatch(ignoredWord -> titleAndDescription.contains(ignoredWord.toLowerCase()));
            if (containsIgnoredWord) {
                log.debug("Vacancy '{}' filtered out by ignored words.", vacancy.getTitle());
                return false;
            }
        }

        // 3. Last N Days filter
        if (criteria.getLastNDays() != null) {
            if (vacancy.getDatePosted() == null) {
                // If the date is unparseable or empty, include it if a date filter is present.
                // This ensures jobs with missing date info aren't silently dropped,
                // as we can't confirm they are outside the N days.
                log.debug("Vacancy '{}' included despite null date due to lastNDays filter presence.",
                        vacancy.getTitle());
                return true;
            }
            LocalDate cutoffDate = LocalDate.now().minusDays(criteria.getLastNDays());
            if (vacancy.getDatePosted().isBefore(cutoffDate)) {
                log.debug("Vacancy '{}' filtered out by date (posted before {} days ago).", vacancy.getTitle(),
                        criteria.getLastNDays());
                return false;
            }
        }
        return true;
    }

}
