package ua.findvacancies.common.export;


import ua.findvacancies.common.model.ExportFormat;
import ua.findvacancies.common.model.JobVacancy;

import java.util.List;

/**
 * Interface for services that export a list of JobVacancy objects
 * to various formats (e.g., JSON, XML, Excel).
 */
public interface JobExportService {

    /**
     * Exports a list of JobVacancy objects to a specific format.
     *
     * @param vacancies The list of JobVacancy objects to export.
     * @return A byte array representation of the exported data in the specific format.
     * For text-based formats (JSON, XML), this will be the UTF-8 encoded bytes of the string.
     * For binary formats (XLSX), this will be the raw binary content.
     */
    byte[] export(List<JobVacancy> vacancies);

    /**
     * Returns the {@link ExportFormat} associated with this export format.
     * @return The ExportFormat enum.
     */
    ExportFormat getFormat();
}
