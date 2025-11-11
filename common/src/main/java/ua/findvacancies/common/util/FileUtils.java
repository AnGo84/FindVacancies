package ua.findvacancies.common.util;

import org.apache.commons.lang3.StringUtils;
import ua.findvacancies.common.model.ExportFormat;

public final class FileUtils {

    public static final String DEFAULT_FILE_NAME = "job_vacancies";

    private FileUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static String getFileName(final ExportFormat format) {
        if (format == null) {
            throw new IllegalArgumentException("Export format must not be null");
        }

        return DEFAULT_FILE_NAME + "." + format.getFileExtension();
    }

    public static String getAttachedFileHeaderValue(final String filename) {
        if (StringUtils.isBlank(filename)) {
            throw new IllegalArgumentException("File name must not be blank");
        }
        return "attachment; filename=\"" + filename + "\"";
    }

    public static String getAttachedFileHeaderValue(final ExportFormat format) {
        return getAttachedFileHeaderValue(getFileName(format));
    }

}
