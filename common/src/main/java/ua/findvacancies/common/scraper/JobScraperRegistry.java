package ua.findvacancies.common.scraper;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import ua.findvacancies.common.model.JobSite;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A registry service that collects and provides access to all
 * {@link JobScraper} implementations in the application context.
 * This allows for dynamic lookup of scrapers by their {@link JobSite}.
 */
@Slf4j
public class JobScraperRegistry {

    private final Map<JobSite, JobScraper> scrapers;

    /**
     * Constructor for JobScraperRegistry.
     * Spring automatically injects all beans that implement the JobScraper interface.
     * These are then collected into a map for easy lookup by SiteName.
     *
     * @param jobScrapers A set of all discovered JobScraper implementations.
     */
    public JobScraperRegistry(final Set<JobScraper> jobScrapers) {
        this.scrapers = jobScrapers.stream().collect(Collectors.toMap(JobScraper::getJobSite, Function.identity()));
        log.info("JobScraperRegistry initialized with {} scrapers: {}",
                scrapers.size(), JobSite.joinSiteNames(scrapers.keySet()));
    }

    /**
     * Retrieves a {@link JobScraper} instance by its {@link JobSite}.
     *
     * @param jobSite The {@link JobSite} enum of the desired scraper.
     * @return An {@link Optional} containing the {@link JobScraper} if found,
     * or an empty Optional if no scraper is registered for the given jobSite.
     */
    public Optional<JobScraper> getScraper(final JobSite jobSite) {
        return Optional.ofNullable(scrapers.get(jobSite));
    }

    /**
     * Retrieves a set of {@link JobScraper} instances corresponding to the specified set of {@link JobSite}.
     * If the provided {@code targetSites} set is {@code null} or empty, this method returns all available scrapers.
     * <p>
     * For each {@link JobSite} in the set, this method attempts to retrieve a corresponding {@link JobScraper}
     * using {@link #getScraper(JobSite)}. Only present scrapers are included in the final set.
     *
     * @param targetSites a set of {@link JobSite}s for which scrapers are requested; may be {@code null} or empty
     * @return a set of {@link JobScraper} instances matching the given sites, or all scrapers if none are specified
     */
    public Set<JobScraper> getScrapers(final Set<JobSite> targetSites) {
        if (CollectionUtils.isEmpty(targetSites)) {
            return scrapers.values().stream().collect(Collectors.toUnmodifiableSet());
        }
        return targetSites.stream()
                .map(this::getScraper)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns a list of all registered {@link JobScraper} instances.
     *
     * @return A list of all JobScraper implementations.
     */
    public Set<JobScraper> getAllScrapers() {
        return new HashSet<>(scrapers.values());
    }

    /**
     * Returns a set of all {@link JobSite}s for which scrapers are registered.
     *
     * @return A set of available site names.
     */
    public Set<JobSite> getAvailableJobSites() {
        return scrapers.keySet();
    }

}
