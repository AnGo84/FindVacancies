package ua.findvacancies.mvc.comparators;

import org.junit.Before;
import org.junit.Test;
import ua.findvacancies.mvc.TestUtils;
import ua.findvacancies.mvc.viewdata.Vacancy;

import static org.junit.Assert.assertEquals;

public class ComparatorVacanciesByDateTest {

    private ComparatorVacanciesByDate comparatorVacanciesByDate;

    @Before
    public void setUp() {
        comparatorVacanciesByDate = new ComparatorVacanciesByDate();
    }


    @Test
    public void whenCompareReturnResult() {
        Vacancy vacancy1 = new Vacancy();
        Vacancy vacancy2 = new Vacancy();
        assertEquals(0, comparatorVacanciesByDate.compare(vacancy1, vacancy1));
        assertEquals(0, comparatorVacanciesByDate.compare(vacancy2, vacancy2));

        vacancy1.setDate(TestUtils.createDate(2020, 3, 20));

        vacancy2.setDate(TestUtils.createDate(2019, 3, 20));
        assertEquals(-1, comparatorVacanciesByDate.compare(vacancy1, vacancy2));

        vacancy2.setDate(TestUtils.createDate(2021, 3, 20));
        assertEquals(1, comparatorVacanciesByDate.compare(vacancy1, vacancy2));
    }

    @Test(expected = NullPointerException.class)
    public void whenCompareNullParams_thenThrowNPE() {
        comparatorVacanciesByDate.compare(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void whenCompareNullFirstParam_thenThrowNPE() {
        comparatorVacanciesByDate.compare(null, new Vacancy());
    }

    @Test(expected = NullPointerException.class)
    public void whenCompareNullSecondParam_thenThrowNPE() {
        comparatorVacanciesByDate.compare(new Vacancy(), null);
    }
}