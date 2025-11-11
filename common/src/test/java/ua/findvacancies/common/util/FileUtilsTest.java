package ua.findvacancies.common.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import ua.findvacancies.common.model.ExportFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileUtilsTest {

    @Test
    @DisplayName("Should throw exception when ExportFormat is null")
    void testGetFileNameWithNullFormat() {
        // Given | When
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> FileUtils.getFileName(null));
        // Then
        assertEquals("Export format must not be null", ex.getMessage());
    }

    @Test
    @DisplayName("Should return correct filename for valid ExportFormat")
    void testGetFileNameValidFormat() {
        // Given
        var format = ExportFormat.JSON;
        // When
        String result = FileUtils.getFileName(format);
        // Then
        assertEquals("job_vacancies.json", result);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "   ", "\t", "\n"})
    @DisplayName("Should throw exception when filename is blank or null")
    void testGetAttachedFileHeaderValueWithBlankOrNull(String input) {
        // Given | When
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> FileUtils.getAttachedFileHeaderValue(input));
        // Then
        assertEquals("File name must not be blank", ex.getMessage());
    }

    @Test
    @DisplayName("Should return correct header for valid filename")
    void testGetAttachedFileHeaderValueValidFilename() {
        // Given
        var validFilename = "report.pdf";
        // When
        var result = FileUtils.getAttachedFileHeaderValue(validFilename);
        // Then
        assertEquals("attachment; filename=\"report.pdf\"", result);
    }

    @Test
    @DisplayName("Should return correct header for valid ExportFormat")
    void testGetAttachedFileHeaderValueWithFormat() {
        // Given
        var format = ExportFormat.XLSX;
        // When
        var result = FileUtils.getAttachedFileHeaderValue(format);
        // Then
        assertEquals("attachment; filename=\"job_vacancies.xlsx\"", result);
    }

}
