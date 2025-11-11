package ua.findvacancies.common.export;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;
import ua.findvacancies.common.TestDataFactory;
import ua.findvacancies.common.export.impl.JsonExportService;
import ua.findvacancies.common.model.ExportFormat;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.util.JacksonUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonExportServiceTest {

    private final JsonExportService jsonExportService = new JsonExportService();

    @Test
    void testExportEmptyList() {
        // Given an empty search criteria
        List<JobVacancy> emptyList = new ArrayList<>();

        // When
        byte[] resultBytes = jsonExportService.export(emptyList);

        // Then
        assertNotNull(resultBytes, "Exporting an empty list should return a non-null byte array.");
        String resultString = new String(resultBytes, StandardCharsets.UTF_8);
        assertEquals("[]", resultString.trim(), "Empty list should result in an empty JSON array bytes.");
    }

    @Test
    void testExportSingleVacancy() throws Exception {
        // Given a list with a single vacancy
        JobVacancy jobVacancy = TestDataFactory.buildJovVacancy(
                "Java Developer",
                LocalDate.of(2025, 8, 25));
        List<JobVacancy> vacancies = List.of(jobVacancy);

        // When
        byte[] jsonOutputBytes = jsonExportService.export(vacancies);

        // Then
        assertNotNull(jsonOutputBytes, "JSON output bytes should not be null.");
        assertTrue(jsonOutputBytes.length > 0, "JSON output bytes should not be empty.");

        String jsonOutput = new String(jsonOutputBytes, StandardCharsets.UTF_8);
        // Parse JSON and verify content
        JsonNode rootNode = JacksonUtils.JSON_MAPPER.readTree(jsonOutput);
        assertTrue(rootNode.isArray(), "Root node should be an array.");
        assertEquals(1, rootNode.size(), "Array should contain one vacancy.");

        JsonNode firstVacancy = rootNode.get(0);
        assertEquals(jobVacancy.getTitle(), firstVacancy.get("title").asText());
        assertEquals(jobVacancy.getSalary(), firstVacancy.get("salary").asText());
        assertEquals(jobVacancy.getCity(), firstVacancy.get("city").asText());
        assertEquals(jobVacancy.getCompanyName(), firstVacancy.get("companyName").asText());
        assertEquals(jobVacancy.getJobSite().name(), firstVacancy.get("jobSite").asText());
        assertEquals(jobVacancy.getUrl(), firstVacancy.get("url").asText());
        assertEquals("2025-08-25", firstVacancy.get("datePosted").asText());
        assertEquals(jobVacancy.isHot(), firstVacancy.get("isHot").asBoolean());
        assertEquals(jobVacancy.getDescription(), firstVacancy.get("description").asText());
        assertTrue(firstVacancy.get("skills").isArray());
        assertEquals(3, firstVacancy.get("skills").size());
    }

    @Test
    void testExportMultipleVacancies() throws Exception {
        // Given
        JobVacancy vacancy1 = TestDataFactory.buildJovVacancy("Dev 1");
        JobVacancy vacancy2 = TestDataFactory.buildJovVacancy("Dev 2");
        List<JobVacancy> vacancies = List.of(vacancy1, vacancy2);

        // When
        byte[] jsonOutputBytes = jsonExportService.export(vacancies);

        // Then
        assertNotNull(jsonOutputBytes);

        JsonNode rootNode = JacksonUtils.JSON_MAPPER.readTree(jsonOutputBytes);
        assertTrue(rootNode.isArray());
        assertEquals(2, rootNode.size());
        assertEquals("Dev 1", rootNode.get(0).get("title").asText());
        assertEquals("Dev 2", rootNode.get(1).get("title").asText());
    }

    @Test
    void testGetFormat() {
        assertEquals(ExportFormat.JSON, jsonExportService.getFormat());
    }

}
