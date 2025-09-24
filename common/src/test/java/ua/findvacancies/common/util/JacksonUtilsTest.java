package ua.findvacancies.common.util;

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JacksonUtilsTest {

    @Test
    void testJsonSerializationExcludesNullsAndFormatsDate() throws Exception {
        // Given
        Sample sample = new Sample("Test", LocalDateTime.of(2025, 9, 17, 10, 42), null);

        // When
        String json = JacksonUtils.JSON_MAPPER.writeValueAsString(sample);
        JsonNode node = JacksonUtils.JSON_MAPPER.readTree(json);

        // Then
        assertEquals("Test", node.get("name").asText());
        assertEquals("2025-09-17T10:42:00", node.get("timestamp").asText());
        assertNull(node.get("optional"));
    }

    @Test
    void testXmlSerializationIncludesIndentation() throws Exception {
        // Given
        Sample sample = new Sample("Test", LocalDateTime.of(2025, 9, 17, 10, 42), null);

        // When
        String xml = JacksonUtils.XML_MAPPER.writeValueAsString(sample);

        // Then
        assertTrue(xml.contains("<name>Test</name>"));
        assertTrue(xml.contains("<timestamp>2025-09-17T10:42:00</timestamp>"));
        assertFalse(xml.contains("<optional>"));
        assertTrue(xml.contains("\n"));
    }

    static class Sample {
        public String name;
        public LocalDateTime timestamp;
        public String optional;

        public Sample(String name, LocalDateTime timestamp, String optional) {
            this.name = name;
            this.timestamp = timestamp;
            this.optional = optional;
        }
    }

}