package ua.findvacancies.comparators;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ua.findvacancies.TestUtils;
import ua.findvacancies.model.Vacancy;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComparatorVacanciesByDateTest {

    private ComparatorVacanciesByDate comparatorVacanciesByDate;

    @BeforeEach
    public void setUp() {
        comparatorVacanciesByDate = new ComparatorVacanciesByDate();
    }


    @Test
    public void whenCompareReturnResult() {
        Vacancy vacancy1 = new Vacancy();
        Vacancy vacancy2 = new Vacancy();
        assertEquals(-1, comparatorVacanciesByDate.compare(vacancy1, vacancy1));
        assertEquals(-1, comparatorVacanciesByDate.compare(vacancy2, vacancy2));

        vacancy1.setDate(new Date());
        vacancy2.setDate(new Date());
        assertEquals(0, comparatorVacanciesByDate.compare(vacancy1, vacancy1));
        assertEquals(0, comparatorVacanciesByDate.compare(vacancy2, vacancy2));

        vacancy1.setDate(TestUtils.createDate(2020, 3, 20));

        vacancy2.setDate(TestUtils.createDate(2019, 3, 20));
        assertEquals(-1, comparatorVacanciesByDate.compare(vacancy1, vacancy2));

        vacancy2.setDate(TestUtils.createDate(2021, 3, 20));
        assertEquals(1, comparatorVacanciesByDate.compare(vacancy1, vacancy2));
    }

}