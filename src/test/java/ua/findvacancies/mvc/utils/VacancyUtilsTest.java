package ua.findvacancies.mvc.utils;

import org.junit.Test;
import ua.findvacancies.mvc.TestUtils;
import ua.findvacancies.mvc.model.SearchParam;
import ua.findvacancies.mvc.model.Vacancy;

import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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

        assertTrue(VacancyUtils.isNotApplyToSearch(null, null));

        Vacancy vacancy = new Vacancy();

        assertTrue(VacancyUtils.isNotApplyToSearch(vacancy, new SearchParam()));
        SearchParam searchParam = TestUtils.getSearchParams();

        vacancy.setSiteName("siteName");
        assertTrue(VacancyUtils.isNotApplyToSearch(vacancy, searchParam));
        vacancy.setUrl("");
        assertTrue(VacancyUtils.isNotApplyToSearch(vacancy, searchParam));
        vacancy.setUrl("siteURL");
        assertFalse(VacancyUtils.isNotApplyToSearch(vacancy, searchParam));

        vacancy.setTitle("with exclude words");
        assertTrue(VacancyUtils.isNotApplyToSearch(vacancy, searchParam));

        vacancy.setTitle("Title does not include words for exclusion");
        assertFalse(VacancyUtils.isNotApplyToSearch(vacancy, searchParam));

        vacancy.setDate(AppDateUtils.addDaysToDate(new Date(), -2));
        assertFalse(VacancyUtils.isNotApplyToSearch(vacancy, searchParam));

        vacancy.setDate(null);
        assertTrue(VacancyUtils.isNotApplyToSearch(vacancy, searchParam));

        vacancy.setDate(AppDateUtils.addDaysToDate(new Date(), -10));
        assertTrue(VacancyUtils.isNotApplyToSearch(vacancy, searchParam));
        vacancy.setHot(true);
        assertFalse(VacancyUtils.isNotApplyToSearch(vacancy, searchParam));


    }
}