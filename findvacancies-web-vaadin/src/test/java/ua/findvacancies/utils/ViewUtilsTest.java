package ua.findvacancies.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ViewUtilsTest {
    @Test
    public void whenGetCopyrightInfo_thenOk() {
        int currentYear = LocalDate.now().getYear();
        String copyrightInfo = ViewUtils.getCopyrightInfo();

        assertEquals(copyrightInfo, "Developed by AnGo 2017-" + currentYear);
    }
}