package ua.findvacancies.webflux.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotEmpty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.service.JobService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/jobs")
@Tag(name = "Job Vacancies Parser WebFlux API", description = "WebFlux API for searching job vacancies.")@Validated
@Slf4j
public class JobScraperWebfluxController {

    private final JobService jobService;

    public JobScraperWebfluxController(JobService jobService) {
        this.jobService = jobService;
    }

    /**
     * Endpoint to search for job vacancies based on specific criteria.
     * This endpoint returns a stream of vacancies as they become available.
     *
     * @param keywords         A comma-separated string of keywords (e.g., "java,spring").
     * @param ignoredWords     A comma-separated string of words to ignore (e.g., "junior,trainee").
     * @param lastNDays        Filter for vacancies posted in the last N days.
     * @param maxPagesToScrape Maximum number of pages to scrape per site.
     * @param sites            A comma-separated string of site names to scrape (e.g., "djinni.co,work.ua").
     *                         If empty, all available sites will be scraped.
     * @return A Flux of JobVacancy objects.
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Search for Job Vacancies Asynchronously",
            description = "Searches for job vacancies across specified sites and returns them as a stream of data.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved job vacancies.",
                            content = @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = JobVacancy.class)))),
                    @ApiResponse(responseCode = "400", description = "Invalid request parameters.")
            })
    @Parameters(value = {
            @Parameter(
                    name = "keywords",
                    required = true,
                    description = "Comma-separated keywords to search for (e.g., 'java,spring').",
                    example = "java,spring",
                    schema = @Schema(type = "array", implementation = String.class)
            ),
            @Parameter(
                    name = "ignoredWords",
                    description = "Comma-separated words to ignore (e.g., 'junior,trainee').",
                    example = "junior,trainee",
                    schema = @Schema(type = "array", implementation = String.class)
            ),
            @Parameter(
                    name = "lastNDays",
                    description = "Filter for vacancies posted in the last N days.",
                    example = "5",
                    schema = @Schema(type = "integer", defaultValue = "5")
            ),
            @Parameter(
                    name = "maxPagesToScrape",
                    description = "Maximum number of pages to scrape per site.",
                    example = "10",
                    schema = @Schema(type = "integer")
            ),
            @Parameter(
                    name = "sites",
                    description = "The set of Sites to scrape",
                    array = @ArraySchema(schema = @Schema(implementation = JobSite.class))
            )
    })
    public Flux<JobVacancy> searchJobs(
            @RequestParam @NotEmpty Set<String> keywords,
            @RequestParam(required = false) Set<String> ignoredWords,
            @RequestParam(required = false) final Integer lastNDays,
            @RequestParam(required = false) Integer maxPagesToScrape,
            @RequestParam(required = false) Set<JobSite> sites) {

        final var criteria = JobSearchCriteria.builder()
                .keywords(keywords)
                .ignoredWords(ignoredWords)
                .lastNDays(lastNDays)
                .maxPagesToScrape(maxPagesToScrape)
                .build();
        log.info("Received request to scrape jobs with criteria: {} and target sites: {}", criteria, sites);

        List<JobVacancy> vacancies = jobService.getJobs(criteria, sites);
        return Flux.fromIterable(vacancies);
    }

    /**
     * Endpoint to list all available job sites that can be scraped.
     *
     * @return A Mono of a Set of available job site names.
     */
    @GetMapping("/sites")
    @Operation(summary = "Get Available Job Sites",
            description = "Retrieves a list of all job sites that can be scraped.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved available sites.",
                            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(type = "array", implementation = JobSite.class)))
            })
    public Mono<Set<JobSite>> getAvailableSites() {
        return Mono.just(jobService.getAvailableJobSites());
    }

}
