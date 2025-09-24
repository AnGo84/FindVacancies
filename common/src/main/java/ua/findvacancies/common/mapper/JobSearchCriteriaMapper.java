package ua.findvacancies.common.mapper;

import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.dto.SearchCriteriaDto;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

public class JobSearchCriteriaMapper {

    public JobSearchCriteria toEntity(SearchCriteriaDto dto) {
        Set<String> keywords = parseWords(dto.getSearchLine());
        Set<String> ignoredWords = parseWords(dto.getIgnoreWords());

        return JobSearchCriteria.builder()
                .keywords(keywords)
                .ignoredWords(ignoredWords)
                .lastNDays(dto.getDays())
                .maxPagesToScrape(JobSearchCriteria.DEFAULT_MAX_PAGES_TO_SCRAPE)
                .build();
    }

    private Set<String> parseWords(String input) {
        if (input == null || input.isBlank()) {
            return Collections.emptySet();
        }
        return Arrays.stream(input.split("[,\\s]+"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toUnmodifiableSet());
    }


}
