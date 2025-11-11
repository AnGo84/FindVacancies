package ua.findvacancies.common.export;

import lombok.extern.slf4j.Slf4j;
import ua.findvacancies.common.model.ExportFormat;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A registry service that collects and provides access to all
 * {@link JobExportService} implementations in the application context.
 * This allows for dynamic lookup of exporters by their file extension or MIME type.
 */
@Slf4j
public class JobExportServiceRegistry {

    private final Map<ExportFormat, JobExportService> exportersByFormat;

    /**
     * Constructor for JobExportServiceRegistry.
     * These are then collected into a map for easy lookup by ExportFormat.
     *
     * @param jobExportServices A list of discovered JobExportService implementations.
     */
    public JobExportServiceRegistry(final Set<JobExportService> jobExportServices) {
        this.exportersByFormat = jobExportServices.stream()
                .collect(Collectors.toMap(JobExportService::getFormat, Function.identity()));
        log.info("JobExportServiceRegistry initialized with {} exporters: {}",
                exportersByFormat.size(),
                exportersByFormat.keySet().stream()
                        .map(ExportFormat::getFileExtension)
                        .collect(Collectors.joining(", ")));
    }

    /**
     * Retrieves a {@link JobExportService} instance by its {@link ExportFormat}.
     *
     * @param format The {@link ExportFormat} enum of the desired exporter.
     * @return An {@link Optional} containing the {@link JobExportService} if found,
     * or an empty Optional if no exporter is registered for the given format.
     */
    public Optional<JobExportService> getExporter(final ExportFormat format) {
        return Optional.ofNullable(exportersByFormat.get(format));
    }

    /**
     * Returns a set of all registered {@link JobExportService} instances.
     *
     * @return A set of all JobExportService implementations.
     */
    public Set<JobExportService> getAllExporters() {
        return exportersByFormat.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns a set of all registered {@link ExportFormat}.
     *
     * @return A set of all ExportFormat with implementation.
     */
    public Set<ExportFormat> getAllExportFormats() {
        return exportersByFormat.keySet().stream().collect(Collectors.toUnmodifiableSet());
    }
}
