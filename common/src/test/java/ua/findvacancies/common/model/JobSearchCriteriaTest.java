package ua.findvacancies.common.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JobSearchCriteria Model Tests")
class JobSearchCriteriaTest {

    @Test
    @DisplayName("Should correctly set and retrieve data using the builder")
    void shouldSetAndRetrieveData() {
        // Given
        final var lastNDays = 15;
        JobSearchCriteria criteria = JobSearchCriteria.builder()
                .keywords(Set.of("Java", "Spring"))
                .lastNDays(lastNDays)
                .ignoredWords(Set.of("Junior", "trainee"))
                .build();

        // When|Then
        assertEquals(Set.of("Java", "Spring"), criteria.getKeywords());
        assertEquals(lastNDays, criteria.getLastNDays());
        assertEquals(Set.of("Junior", "trainee"), criteria.getIgnoredWords());
    }

    @Test
    @DisplayName("Equals and HashCode should be consistent for equal objects")
    void equalsAndHashCodeShouldBeConsistent() {
        // Given
        JobSearchCriteria criteria1 = JobSearchCriteria.builder()
                .keywords(Set.of("Java"))
                .lastNDays(3)
                .build();

        JobSearchCriteria criteria2 = JobSearchCriteria.builder()
                .keywords(Set.of("Java"))
                .lastNDays(3)
                .build();

        // When|Then
        // Test equality
        assertEquals(criteria1, criteria2);
        assertEquals(criteria2, criteria1);

        // Test hash code consistency
        assertEquals(criteria1.hashCode(), criteria2.hashCode());
    }

    @Test
    @DisplayName("Equals and HashCode should be different for unequal objects")
    void equalsAndHashCodeShouldBeDifferent() {
        // Given
        JobSearchCriteria criteria1 = JobSearchCriteria.builder().keywords(Set.of("Java")).build();
        JobSearchCriteria criteria2 = JobSearchCriteria.builder().keywords(Set.of("Python")).build();

        // When|Then
        // Test inequality
        assertNotEquals(criteria1, criteria2);
        assertNotEquals(criteria2, criteria1);
    }

    @Test
    @DisplayName("Should correctly set default values")
    void shouldGenerateCorrectToString() {
        // When
        JobSearchCriteria criteria = JobSearchCriteria.builder()
                .build();

        // Then
        assertTrue(criteria.getKeywords().isEmpty());
        assertTrue(criteria.getIgnoredWords().isEmpty());
        assertEquals(JobSearchCriteria.DEFAULT_LAST_N_DAYS, criteria.getLastNDays());
        assertEquals(JobSearchCriteria.DEFAULT_MAX_PAGES_TO_SCRAPE, criteria.getMaxPagesToScrapeOrDefault());
    }

    @Test
    @DisplayName("isEmpty() should return false for an empty criteria object")
    void isEmptyShouldReturnTrueForEmptyCriteria() {
        JobSearchCriteria criteria = JobSearchCriteria.builder().build();
        assertFalse(criteria.isEmpty());
    }

    @Test
    @DisplayName("isEmpty() should return false for a criteria object with keywords")
    void isEmptyShouldReturnFalseForCriteriaWithKeywords() {
        JobSearchCriteria criteria = JobSearchCriteria.builder().keywords(Set.of("Java")).build();
        assertFalse(criteria.isEmpty());
    }

    @Test
    @DisplayName("hasKeywords() should return true when keywords are present")
    void hasKeywordsShouldReturnTrue() {
        JobSearchCriteria criteria = JobSearchCriteria.builder().keywords(Set.of("Java")).build();
        assertTrue(criteria.hasKeywords());
    }

    @Test
    @DisplayName("hasKeywords() should return false when keywords are not present")
    void hasKeywordsShouldReturnFalse() {
        JobSearchCriteria criteria = JobSearchCriteria.builder().build();
        assertFalse(criteria.hasKeywords());
    }

    @Test
    @DisplayName("hasIgnoredWords() should return true when ignored words are present")
    void hasIgnoredWordsShouldReturnTrue() {
        JobSearchCriteria criteria = JobSearchCriteria.builder().ignoredWords(Set.of("Junior")).build();
        assertTrue(criteria.hasIgnoredWords());
    }

    @Test
    @DisplayName("hasIgnoredWords() should return false when ignored words are not present")
    void hasIgnoredWordsShouldReturnFalse() {
        JobSearchCriteria criteria = JobSearchCriteria.builder().build();
        assertFalse(criteria.hasIgnoredWords());
    }
}