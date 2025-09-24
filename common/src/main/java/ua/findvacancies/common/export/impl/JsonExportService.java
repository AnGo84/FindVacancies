package ua.findvacancies.common.export.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import ua.findvacancies.common.export.JobExportService;
import ua.findvacancies.common.model.ExportFormat;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.util.JacksonUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collection;

/**
 * Service for exporting a collection of JobVacancy objects to JSON format.
 * Uses Jackson for JSON serialization.
 */
@Slf4j
public class JsonExportService implements JobExportService {

    public static final String EMPTY_ARRAY_JSON = "[]";

    /**
     * Converts a collection of JobVacancy objects into a JSON string, then returns its UTF-8 bytes.
     *
     * @param vacancies The collection of JobVacancy objects to export.
     * @return A byte array representing the JSON data, or an empty byte array if an error occurs or collection is
     * empty.
     */
    @Override
    public byte[] export(Collection<JobVacancy> vacancies) {
        return getJsonAsString(vacancies).getBytes(StandardCharsets.UTF_8);
    }

    private String getJsonAsString(Collection<JobVacancy> vacancies) {
        if (CollectionUtils.isEmpty(vacancies)) {
            log.warn("Attempted to export an empty or null collection of vacancies to JSON.");
            return EMPTY_ARRAY_JSON;
        }
        try {
            String jsonString = JacksonUtils.JSON_MAPPER.writeValueAsString(vacancies);
            log.info("Successfully exported {} vacancies to JSON.", vacancies.size());
            return jsonString;
        } catch (Exception e) {
            log.error("Error exporting vacancies to JSON: {}", e.getMessage(), e);
            return EMPTY_ARRAY_JSON;
        }
    }

    @Override
    public ExportFormat getFormat() { // Implement the new method
        return ExportFormat.JSON;
    }
}
