package ua.findvacancies.common.export.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import ua.findvacancies.common.export.JobExportService;
import ua.findvacancies.common.model.ExportFormat;
import ua.findvacancies.common.model.JobVacancy;
import ua.findvacancies.common.model.JobVacancyListWrapper;
import ua.findvacancies.common.util.JacksonUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

/**
 * Service for exporting a collection of JobVacancy objects to XML format.
 * Uses Jackson's XmlMapper for XML serialization.
 */
@Slf4j
public class XmlExportService implements JobExportService {

    public static final String EMPTY_VACANCIES_LIST = "<JobVacancies/>";

    /**
     * Converts a collection of JobVacancy objects into an XML string, then returns its UTF-8 bytes.
     *
     * @param vacancies The collection of JobVacancy objects to export.
     * @return A byte array representing the XML data, or an empty byte array if an error occurs or collection is empty.
     */
    @Override
    public byte[] export(Collection<JobVacancy> vacancies) {
        return getXMLAsString(vacancies).getBytes(StandardCharsets.UTF_8);
    }

    private String getXMLAsString(Collection<JobVacancy> vacancies) {
        if (CollectionUtils.isEmpty(vacancies)) {
            log.warn("Attempted to export an empty or null collection of vacancies to XML.");
            return EMPTY_VACANCIES_LIST;
        }
        try {
            JobVacancyListWrapper wrapper = new JobVacancyListWrapper(vacancies);
            String xmlString = JacksonUtils.XML_MAPPER.writeValueAsString(wrapper);
            log.info("Successfully exported {} vacancies to XML.", vacancies.size());
            return xmlString;
        } catch (Exception e) {
            log.error("Error exporting vacancies to XML: {}", e.getMessage(), e);
            return EMPTY_VACANCIES_LIST;
        }
    }

    @Override
    public ExportFormat getFormat() { // Implement the new method
        return ExportFormat.XML;
    }
}
