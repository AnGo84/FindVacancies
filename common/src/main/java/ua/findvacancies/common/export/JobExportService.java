package ua.findvacancies.common.export;


import ua.findvacancies.common.model.ExportFormat;
import ua.findvacancies.common.model.JobVacancy;

import java.util.Collection;

/**
 * Interface for services that export a collection of JobVacancy objects
 * to various formats (e.g., JSON, XML, Excel).
 */
public interface JobExportService {

    /**
     * Exports a collection of JobVacancy objects to a specific format.
     *
     * @param vacancies The collection of JobVacancy objects to export.
     * @return A byte array representation of the exported data in the specific format.
     * For text-based formats (JSON, XML), this will be the UTF-8 encoded bytes of the string.
     * For binary formats (XLSX), this will be the raw binary content.
     */
    byte[] export(Collection<JobVacancy> vacancies);

    /**
     * Returns the {@link ExportFormat} associated with this export format.
     *
     * @return The ExportFormat enum.
     */
    ExportFormat getFormat();
}
