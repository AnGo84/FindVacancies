package ua.findvacancies.common.model;

import org.apache.commons.collections4.CollectionUtils;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Enum representing the names of the job sites supported by the parser.
 * Provides a type-safe way to refer to different job boards.
 */
public enum JobSite {
    DJINNI_CO("djinni.co", "https://djinni.co"),
    WORK_UA("work.ua", "https://www.work.ua"),
    DOU_UA("jobs.dou.ua", "https://www.jobs.dou.ua"),
    GRC_UA("grc.ua", "https://www.grc.ua")
    ;

    private final String siteName;
    private final String url;

    JobSite(String siteName, String url) {
        this.siteName = siteName;
        this.url = url;
    }

    /**
     * Returns the common URL-friendly name for the site.
     *
     * @return The site's URL name.
     */
    public String getSiteName() {
        return siteName;
    }

    /**
     * Returns the URL for the site.
     *
     * @return The site's URL.
     */
    public String getUrl() {
        return url;
    }

    /**
     * Finds a SiteName enum by its URL-friendly name.
     *
     * @param siteName The URL-friendly name of the site (e.g., "djinni.co").
     * @return An Optional containing the matching SiteName, or empty if not found.
     */
    public static Optional<JobSite> fromUrlName(String siteName) {
        for (JobSite site : JobSite.values()) {
            if (site.siteName.equalsIgnoreCase(siteName)) {
                return java.util.Optional.of(site);
            }
        }
        return java.util.Optional.empty();
    }

    /**
     * Converts a set of {@link JobSite} objects into a single comma-separated string of site names.
     * <p>
     * If the input set is {@code null} or empty, this method returns an empty string.
     * Otherwise, it extracts the site name from each {@link JobSite} using {@link JobSite#getSiteName()}
     * and joins them into a readable inline format.
     * </p>
     *
     * @param sites the set of {@link JobSite} instances to extract names from; may be {@code null} or empty
     * @return a comma-separated string of site names, or an empty string if the input is empty
     */
    public static String joinSiteNames(Set<JobSite> sites) {
        if (CollectionUtils.isEmpty(sites)) {
            return "";
        }
        return sites.stream()
                .map(JobSite::getSiteName)
                .sorted()
                .collect(Collectors.joining(", "));
    }
}
