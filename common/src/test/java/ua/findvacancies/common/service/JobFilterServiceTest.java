package ua.findvacancies.common.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ua.findvacancies.common.TestDataFactory;
import ua.findvacancies.common.model.JobSearchCriteria;
import ua.findvacancies.common.model.JobVacancy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("JobFilterService Unit Tests")
class JobFilterServiceTest {
    private JobFilterService jobFilterService;
    private List<JobVacancy> vacancies;

    @BeforeEach
    void setUp() {
        // Initialize the service and a list of sample vacancies before each test
        jobFilterService = new JobFilterService();
        vacancies = new ArrayList<>();
        vacancies.add(
                TestDataFactory.buildJovVacancy("Java Software Engineer",
                        LocalDate.now().minusDays(1),
                        "Description for Java role. Tech stack: Java, Spring Boot, Microservices.")
                /*new JobVacancy(
                ,
                "Company A",
                "Kyiv",
                "Description for Java role. Tech stack: Java, Spring Boot, Microservices.",
                "http://example.com/java-vacancy")*/
        );
        vacancies.add(
                TestDataFactory.buildJovVacancy("Senior Java Developer",
                        LocalDate.now().minusDays(3),
                        "Senior developer role. Tech stack: Java, Junit, Mockito, SQL.")
        );
        vacancies.add(TestDataFactory.buildJovVacancy("JavaScript Frontend Developer",
                        LocalDate.now().minusDays(2),
                        "Frontend role. Tech stack: JavaScript, React, Node.js.")
        );
        vacancies.add(TestDataFactory.buildJovVacancy("QA Engineer (Automation)",
                        LocalDate.now().minusDays(5),
                        "Testing role with a focus on automation.")
        );
        vacancies.add(TestDataFactory.buildJovVacancy("Golang Developer",
                        LocalDate.now().minusDays(1),
                        "Backend position. Golang, Docker, Kubernetes.")
        );
    }

    @Test
    @DisplayName("Should filter vacancies by keyword")
    void shouldFilterVacanciesByKeyword() {
        // Given a search criteria with a single keyword
        JobSearchCriteria criteria = JobSearchCriteria.builder()
                .keywords(Set.of("Java"))
                .build();

        // When the filter service is called
        List<JobVacancy> filteredList = jobFilterService.filter(vacancies, criteria);

        // Then only Java word related vacancies should be returned
        assertEquals(3, filteredList.size());
    }

    @Test
    @DisplayName("Should filter vacancies by multiple keywords")
    void shouldFilterVacanciesByMultipleKeywords() {
        // Given search criteria with multiple keywords
        JobSearchCriteria criteria = JobSearchCriteria.builder()
                .keywords(Set.of("Java", "Golang"))
                .build();

        // When the filter service is called
        List<JobVacancy> filteredList = jobFilterService.filter(vacancies, criteria);

        // Then all vacancies matching either keyword should be returned
        assertEquals(4, filteredList.size());
    }

    @Test
    @DisplayName("Should exclude vacancies by ignored keyword")
    void shouldExcludeVacanciesByIgnoredKeyword() {
        // Given search criteria with an ignored keyword
        JobSearchCriteria criteria = JobSearchCriteria.builder()
                .keywords(Set.of("developer"))
                .ignoredWords(Set.of("JavaScript"))
                .build();

        // When the filter service is called
        List<JobVacancy> filteredList = jobFilterService.filter(vacancies, criteria);

        // Then the JavaScript vacancy should be excluded, even though it contains "developer"
        assertEquals(2, filteredList.size());
        assertTrue(filteredList.stream().noneMatch(v -> v.getTitle().contains("JavaScript")));
    }

    @Test
    @DisplayName("Should return all vacancies if no keywords are specified")
    void shouldReturnAllVacanciesIfNoKeywords() {
        // Given an empty search criteria
        JobSearchCriteria criteria = JobSearchCriteria.builder().build();

        // When the filter service is called
        List<JobVacancy> filteredList = jobFilterService.filter(vacancies, criteria);

        // Then the original list should be returned without any filtering
        assertEquals(vacancies.size(), filteredList.size());
    }

    @Test
    @DisplayName("Should return empty list if input is null or empty")
    void shouldReturnEmptyListIfInputEmpty() {
        // Given a null list of vacancies
        JobSearchCriteria criteria = JobSearchCriteria.builder().build();
        List<JobVacancy> filteredList = jobFilterService.filter(null, criteria);
        assertTrue(filteredList.isEmpty());

        // Given an empty list of vacancies
        filteredList = jobFilterService.filter(new ArrayList<>(), criteria);
        assertTrue(filteredList.isEmpty());
    }

}
