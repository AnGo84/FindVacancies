package ua.findvacancies.mvc.model;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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