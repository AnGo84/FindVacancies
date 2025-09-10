package ua.findvacancies.common.model;

import java.util.Optional;

/**
 * Enum representing the supported export formats, along with their
 * file extensions and MIME types.
 */
public enum ExportFormat {
    JSON("json", "application/json"),
    XML("xml", "application/xml"),
    XLSX("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

    private final String fileExtension;
    private final String mimeType;

    ExportFormat(String fileExtension, String mimeType) {
        this.fileExtension = fileExtension;
        this.mimeType = mimeType;
    }

    public String getFileExtension() {
        return fileExtension;
    }

    public String getMimeType() {
        return mimeType;
    }

    /**
     * Finds an ExportFormat enum by its file extension (case-insensitive).
     * @param fileExtension The file extension string (e.g., "json", "XLSX").
     * @return An Optional containing the matching ExportFormat, or empty if not found.
     */
    public static Optional<ExportFormat> fromFileExtension(String fileExtension) {
        if (fileExtension == null || fileExtension.isEmpty()) {
            return Optional.empty();
        }
        for (ExportFormat format : ExportFormat.values()) {
            if (format.fileExtension.equalsIgnoreCase(fileExtension)) {
                return Optional.of(format);
            }
        }
        return Optional.empty();
    }

    /**
     * Finds an ExportFormat enum by its MIME type (case-insensitive).
     * @param mimeType The MIME type string (e.g., "application/json").
     * @return An Optional containing the matching ExportFormat, or empty if not found.
     */
    public static Optional<ExportFormat> fromMimeType(String mimeType) {
        if (mimeType == null || mimeType.isEmpty()) {
            return Optional.empty();
        }
        for (ExportFormat format : ExportFormat.values()) {
            if (format.mimeType.equalsIgnoreCase(mimeType)) {
                return Optional.of(format);
            }
        }
        return Optional.empty();
    }

}
