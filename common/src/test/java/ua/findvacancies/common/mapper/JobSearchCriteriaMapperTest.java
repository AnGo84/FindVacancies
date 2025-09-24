package ua.findvacancies.common.mapper;

import org.junit.jupiter.api.Test;
import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.model.dto.SearchCriteriaDto;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class JobSearchCriteriaMapperTest {

    private final Set<JobSite> jobSites = new HashSet<>();
    private final JobSearchCriteriaMapper mapper = new JobSearchCriteriaMapper();

    @Test
    void testValidInput() {
        // Given
        SearchCriteriaDto dto = new SearchCriteriaDto("java, spring boot", 7, jobSites, "the, a");

        // When
        JobSearchCriteria result = mapper.toEntity(dto);

        // Then
        assertEquals(Set.of("java", "spring", "boot"), result.getKeywords());
        assertEquals(Set.of("the", "a"), result.getIgnoredWords());
        assertEquals(7, result.getLastNDays());
        assertEquals(JobSearchCriteria.DEFAULT_MAX_PAGES_TO_SCRAPE, result.getMaxPagesToScrape());
    }

    @Test
    void testEmptyStrings() {
        // Given
        SearchCriteriaDto dto = new SearchCriteriaDto("", 0, jobSites, "");

        // When
        JobSearchCriteria result = mapper.toEntity(dto);

        // Then
        assertTrue(result.getKeywords().isEmpty());
        assertTrue(result.getIgnoredWords().isEmpty());
        assertEquals(0, result.getLastNDays());
    }

    @Test
    void testNullStrings() {
        // Given
        SearchCriteriaDto dto = new SearchCriteriaDto(null, 5, jobSites, null);

        // When
        JobSearchCriteria result = mapper.toEntity(dto);

        // Then
        assertTrue(result.getKeywords().isEmpty());
        assertTrue(result.getIgnoredWords().isEmpty());
        assertEquals(5, result.getLastNDays());
    }

    @Test
    void testExtraSpacesAndCommas() {
        // Given
        SearchCriteriaDto dto = new SearchCriteriaDto("  java , , spring  ", 3, jobSites, " , the , , ");

        // When
        JobSearchCriteria result = mapper.toEntity(dto);

        // Then
        assertEquals(Set.of("java", "spring"), result.getKeywords());
        assertEquals(Set.of("the"), result.getIgnoredWords());
    }

    @Test
    void testDuplicateWords() {
        // Given
        SearchCriteriaDto dto = new SearchCriteriaDto("java java spring", 1, jobSites, "the the");

        // When
        JobSearchCriteria result = mapper.toEntity(dto);

        // Then
        assertEquals(Set.of("java", "spring"), result.getKeywords());
        assertEquals(Set.of("the"), result.getIgnoredWords());
    }

}
