package ua.findvacancies.common.export;

import org.junit.jupiter.api.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;
import ua.findvacancies.common.TestDataFactory;
import ua.findvacancies.common.export.impl.XmlExportService;
import ua.findvacancies.common.model.ExportFormat;
import ua.findvacancies.common.model.JobSite;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.model.JobVacancyListWrapper;
import ua.findvacancies.common.util.MapHelper;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XmlExportServiceTest {

    private final XmlExportService xmlExportService = new XmlExportService();


    @Test
    void testExportEmptyList() {
        // Given an empty search criteria
        List<JobVacancy> emptyList = new ArrayList<>();

        // When
        byte[] resultBytes = xmlExportService.export(emptyList);

        // Then
        assertNotNull(resultBytes, "Exporting an empty list should return a non-null byte array.");
        String resultString = new String(resultBytes, StandardCharsets.UTF_8);
        // Expecting <JobVacancies/> for an empty list as defined in XmlExportService
        String expectedXmlForEmptyList = "<JobVacancies/>";
        Diff diff = DiffBuilder.compare(expectedXmlForEmptyList)
                .withTest(resultString)
                .ignoreWhitespace()
                .build();
        assertFalse(diff.hasDifferences(), "Empty list should result in an empty XML root element.");
    }

    @Test
    void testExportSingleVacancy() throws Exception {
        // Given a list with a single vacancy
        JobVacancy jobVacancy = TestDataFactory.buildJovVacancy(
                "Java Developer",
                LocalDate.of(2025, 6, 10));
        List<JobVacancy> vacancies = List.of(jobVacancy);

        // When
        byte[] xmlOutputBytes = xmlExportService.export(vacancies);

        // Then
        assertNotNull(xmlOutputBytes, "XML output bytes should not be null.");
        assertTrue(xmlOutputBytes.length > 0, "XML output bytes should not be empty.");

        String xmlOutput = new String(xmlOutputBytes, StandardCharsets.UTF_8);

        // Dynamically generate the expected XML using the same XmlMapper configuration
        // This ensures the expected XML structure and field order match the actual serialization
        JobVacancyListWrapper expectedWrapper = new JobVacancyListWrapper(vacancies);
        String expectedXml = MapHelper.XML_MAPPER.writeValueAsString(expectedWrapper);

        Diff diff = DiffBuilder.compare(expectedXml)
                .withTest(xmlOutput)
                .ignoreWhitespace()
                .build();
        assertFalse(diff.hasDifferences(), "XML output should match expected structure for single vacancy.");
    }

    @Test
    void testExportMultipleVacancies() {
        // Given
        JobVacancy vacancy1 = TestDataFactory.buildJovVacancy("Dev 1");
        JobVacancy vacancy2 = TestDataFactory.buildJovVacancy("Dev 2");
        List<JobVacancy> vacancies = List.of(vacancy1, vacancy2);

        // When
        byte[] xmlOutputBytes = xmlExportService.export(vacancies);

        // Then
        assertNotNull(xmlOutputBytes);

        String xmlOutput = new String(xmlOutputBytes, StandardCharsets.UTF_8);
        System.out.println(xmlOutput);
        // Verify that it contains both titles and the correct wrapper/item tags
        assertTrue(xmlOutput.contains("<JobVacancies>"));
        assertTrue(xmlOutput.contains("<JobVacancy>"));
        assertTrue(xmlOutput.contains("<title>Dev 1</title>"));
        assertTrue(xmlOutput.contains("<title>Dev 2</title>"));
        assertTrue(xmlOutput.contains("</JobVacancy>"));
        assertTrue(xmlOutput.contains("</JobVacancies>"));
    }

    @Test
    void testGetFormat() {
        assertEquals(ExportFormat.XML, xmlExportService.getFormat());
    }

}