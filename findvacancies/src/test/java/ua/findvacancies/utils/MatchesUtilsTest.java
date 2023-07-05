package ua.findvacancies.utils;

import org.junit.jupiter.api.Test;
import ua.findvacancies.model.Vacancy;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MatchesUtilsTest {

    @Test
    public void matchesTerm_String_String() {
        assertFalse(MatchesUtils.matchesTerm(null, null));
        assertFalse(MatchesUtils.matchesTerm(null, "search_term"));
        assertFalse(MatchesUtils.matchesTerm("value", null));

        assertFalse(MatchesUtils.matchesTerm("value", "search_term"));
        assertTrue(MatchesUtils.matchesTerm("value", "value"));

    }

    @Test
    public void matches_Vacancy_String() {

        assertFalse(MatchesUtils.matches(null, null));
        assertFalse(MatchesUtils.matches(null, "search_term"));
        assertFalse(MatchesUtils.matches(new Vacancy(), null));

        assertFalse(MatchesUtils.matches(new Vacancy(), "search_term"));

        Vacancy vacancy = new Vacancy();
        vacancy.setTitle("title");
        assertTrue(MatchesUtils.matches(vacancy, "t"));
        assertTrue(MatchesUtils.matches(vacancy, "TI"));
        assertTrue(MatchesUtils.matches(vacancy, "title"));

        vacancy.setSalary("Salary");
        assertTrue(MatchesUtils.matches(vacancy, "s"));
        assertTrue(MatchesUtils.matches(vacancy, "sA"));
        assertTrue(MatchesUtils.matches(vacancy, "salary"));

        vacancy.setCity("City");
        assertTrue(MatchesUtils.matches(vacancy, "c"));
        assertTrue(MatchesUtils.matches(vacancy, "CI"));
        assertTrue(MatchesUtils.matches(vacancy, "city"));

        vacancy.setCompanyName("CompanyName");
        assertTrue(MatchesUtils.matches(vacancy, "c"));
        assertTrue(MatchesUtils.matches(vacancy, "COMP"));
        assertTrue(MatchesUtils.matches(vacancy, "companyName"));

        vacancy.setSiteName("SiteName");
        assertTrue(MatchesUtils.matches(vacancy, "s"));
        assertTrue(MatchesUtils.matches(vacancy, "SITE"));
        assertTrue(MatchesUtils.matches(vacancy, "siteName"));

        vacancy.setUrl("URL");
        assertTrue(MatchesUtils.matches(vacancy, "u"));
        assertTrue(MatchesUtils.matches(vacancy, "Ur"));
        assertTrue(MatchesUtils.matches(vacancy, "url"));

        Date dateNow = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateNow);

        vacancy.setDate(dateNow);

        String yearText = String.valueOf(calendar.get(Calendar.YEAR));

        assertTrue(MatchesUtils.matches(vacancy, yearText));

    }

}
