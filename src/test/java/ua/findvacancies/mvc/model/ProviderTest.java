package ua.findvacancies.mvc.model;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ProviderTest {

    @Test
    public void whenContain() {
        assertFalse(Provider.contains(null));
        assertFalse(Provider.contains(""));
        assertFalse(Provider.contains("wrong"));
        assertTrue(Provider.contains("workUA"));
        assertTrue(Provider.contains("DOU"));
    }
}