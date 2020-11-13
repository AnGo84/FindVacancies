package ua.findvacancies.mvc.utils;

import org.junit.jupiter.api.Test;
import ua.findvacancies.mvc.TestUtils;
import ua.findvacancies.mvc.model.SearchParam;
import ua.findvacancies.mvc.model.Vacancy;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class VacancyUtilsTest{

    @Test
    public void whenIsEmpty() {

        assertTrue(VacancyUtils.isEmpty(null));
        Vacancy vacancy = new Vacancy();

        assertTrue(VacancyUtils.isEmpty(vacancy));
        vacancy.setSiteName("siteName");
        assertTrue(VacancyUtils.isEmpty(vacancy));
        vacancy.setUrl("");
        assertTrue(VacancyUtils.isEmpty(vacancy));
        vacancy.setUrl("siteURL");
        assertFalse(VacancyUtils.isEmpty(vacancy));
    }
    @Test
    public void whenIsNotApplyToSearch() {

        assertFalse(VacancyUtils.isApplyToSearch(null, null));

        Vacancy vacancy = new Vacancy();

        assertFalse(VacancyUtils.isApplyToSearch(vacancy, new SearchParam()));
        SearchParam searchParam = TestUtils.getSearchParams();

        vacancy.setSiteName("siteName");
        assertFalse(VacancyUtils.isApplyToSearch(vacancy, searchParam));
        vacancy.setUrl("");
        assertFalse(VacancyUtils.isApplyToSearch(vacancy, searchParam));
        vacancy.setUrl("siteURL");
        assertFalse(VacancyUtils.isApplyToSearch(vacancy, searchParam));

        vacancy.setTitle("with exclude words");
        assertTrue(VacancyUtils.isApplyToSearch(vacancy, searchParam));

        vacancy.setTitle("Title does not include words for exclusion");
        assertFalse(VacancyUtils.isApplyToSearch(vacancy, searchParam));

        vacancy.setDate(AppDateUtils.addDaysToDate(new Date(), -2));
        assertTrue(VacancyUtils.isApplyToSearch(vacancy, searchParam));

        vacancy.setDate(null);
        assertFalse(VacancyUtils.isApplyToSearch(vacancy, searchParam));

        vacancy.setDate(AppDateUtils.addDaysToDate(new Date(), -10));
        assertFalse(VacancyUtils.isApplyToSearch(vacancy, searchParam));
        vacancy.setHot(true);
        assertTrue(VacancyUtils.isApplyToSearch(vacancy, searchParam));
    }
}