package ua.findvacancies.common.model;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Unit tests for the ExportFormat enum.
 * Verifies correct file extensions, MIME types, and lookup methods.
 */
class ExportFormatTest {

    @Test
    void testGetFileExtension() {
        assertEquals("json", ExportFormat.JSON.getFileExtension());
        assertEquals("xml", ExportFormat.XML.getFileExtension());
        assertEquals("xlsx", ExportFormat.XLSX.getFileExtension());
    }

    @Test
    void testGetMimeType() {
        assertEquals("application/json", ExportFormat.JSON.getMimeType());
        assertEquals("application/xml", ExportFormat.XML.getMimeType());
        assertEquals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                ExportFormat.XLSX.getMimeType());
    }

    @Test
    void testFromFileExtension_ValidNames() {
        assertEquals(Optional.of(ExportFormat.JSON), ExportFormat.fromFileExtension("json"));
        assertEquals(Optional.of(ExportFormat.XML), ExportFormat.fromFileExtension("xml"));
        assertEquals(Optional.of(ExportFormat.XLSX), ExportFormat.fromFileExtension("xlsx"));
        // Test case-insensitivity
        assertEquals(Optional.of(ExportFormat.JSON), ExportFormat.fromFileExtension("JSON"));
        assertEquals(Optional.of(ExportFormat.XLSX), ExportFormat.fromFileExtension("XLSX"));
    }

    @Test
    void testFromFileExtension_InvalidName() {
        assertFalse(ExportFormat.fromFileExtension("csv").isPresent());
        assertFalse(ExportFormat.fromFileExtension(null).isPresent());
        assertFalse(ExportFormat.fromFileExtension("").isPresent());
    }

    @Test
    void testFromMimeType_ValidTypes() {
        assertEquals(Optional.of(ExportFormat.JSON), ExportFormat.fromMimeType("application/json"));
        assertEquals(Optional.of(ExportFormat.XML), ExportFormat.fromMimeType("application/xml"));
        assertEquals(Optional.of(ExportFormat.XLSX),
                ExportFormat.fromMimeType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        // Test case-insensitivity
        assertEquals(Optional.of(ExportFormat.JSON), ExportFormat.fromMimeType("APPLICATION/JSON"));
    }

    @Test
    void testFromMimeType_InvalidTypes() {
        assertFalse(ExportFormat.fromMimeType("text/plain").isPresent());
        assertFalse(ExportFormat.fromMimeType(null).isPresent());
        assertFalse(ExportFormat.fromMimeType("").isPresent());
    }
}
