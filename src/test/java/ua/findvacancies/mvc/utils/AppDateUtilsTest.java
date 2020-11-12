package ua.findvacancies.mvc.utils;

import org.junit.Test;
import ua.findvacancies.mvc.TestUtils;

import java.util.Date;

import static org.junit.Assert.*;

public class AppDateUtilsTest {

    @Test
    public void whenAddDaysToDate_thenOk() {
        Date now = new Date();
        assertEquals(now, AppDateUtils.addDaysToDate(now, 0));
    }

    @Test(expected = NullPointerException.class)
    public void whenAddDaysToDateWithNullDateParam_thenThrowNPE() {
        AppDateUtils.addDaysToDate(null, 0);
    }

    @Test(expected = NullPointerException.class)
    public void whenChangeDateYear_thenThrowNPE() {
        AppDateUtils.changeDateYear(null, 0);
    }

    @Test
    public void whenChangeDateYear_thenReturnResult() {
        assertNotNull(AppDateUtils.changeDateYear(new Date(), 2020));

        Date expectedDate = TestUtils.createDate(2020, 3, 20);
        Date testDate = TestUtils.createDate(123, 3, 20);
        assertEquals(expectedDate, AppDateUtils.changeDateYear(testDate, 2020));
        assertNotEquals(expectedDate, AppDateUtils.changeDateYear(testDate, 2019));
    }

    @Test(expected = NullPointerException.class)
    public void whenGetYearFromDate_thenThrowNPE() {
        AppDateUtils.getYearFromDate(null);
    }

    @Test
    public void whenGetYearFromDate_thenReturnResult() {
        assertNotNull(AppDateUtils.getYearFromDate(new Date()));

        Date testDate = TestUtils.createDate(2020, 3, 20);
        assertEquals(2020, AppDateUtils.getYearFromDate(testDate));
    }

    @Test(expected = NullPointerException.class)
    public void whenCompareDatesByDayAndMonthAndNullParams_thenThrowNPE() {
        AppDateUtils.compareDatesByDayAndMonth(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void whenCompareDatesByDayAndMonthAndNullFirstParam_thenThrowNPE() {
        AppDateUtils.compareDatesByDayAndMonth(null, new Date());
    }

    @Test(expected = NullPointerException.class)
    public void whenCompareDatesByDayAndMonthAndNullSecondParam_thenThrowNPE() {
        AppDateUtils.compareDatesByDayAndMonth(new Date(), null);
    }

    @Test
    public void whenCompareDatesByDayAndMonth_thenReturnResult() {
        Date testDate1 = TestUtils.createDate(2020, 3, 20);
        Date testDate2 = TestUtils.createDate(2019, 3, 20);
        assertEquals(0, AppDateUtils.compareDatesByDayAndMonth(testDate1, testDate2));

        testDate2 = TestUtils.createDate(2019, 4, 20);
        assertEquals(-1, AppDateUtils.compareDatesByDayAndMonth(testDate1, testDate2));
        testDate2 = TestUtils.createDate(2019, 3, 21);
        assertEquals(-1, AppDateUtils.compareDatesByDayAndMonth(testDate1, testDate2));

        testDate2 = TestUtils.createDate(2019, 2, 20);
        assertEquals(1, AppDateUtils.compareDatesByDayAndMonth(testDate1, testDate2));
        testDate2 = TestUtils.createDate(2019, 3, 10);
        assertEquals(1, AppDateUtils.compareDatesByDayAndMonth(testDate1, testDate2));

    }

    @Test(expected = NullPointerException.class)
    public void whenCompareDatesByDayMonthYearAndNullParams_thenThrowNPE() {
        AppDateUtils.compareDatesByDayMonthYear(null, null);
    }

    @Test(expected = NullPointerException.class)
    public void whenCompareDatesByDayMonthYearAndNullFirstParam_thenThrowNPE() {
        AppDateUtils.compareDatesByDayMonthYear(null, new Date());
    }

    @Test(expected = NullPointerException.class)
    public void whenCompareDatesByDayMonthYearAndNullSecondParam_thenThrowNPE() {
        AppDateUtils.compareDatesByDayMonthYear(new Date(), null);
    }

    @Test
    public void whenCompareDatesByDayMonthYear_thenReturnResult() {
        Date testDate1 = TestUtils.createDate(2020, 3, 20);
        Date testDate2 = TestUtils.createDate(2020, 3, 20);
        assertEquals(0, AppDateUtils.compareDatesByDayMonthYear(testDate1, testDate2));

        testDate2 = TestUtils.createDate(2020, 2, 20);
        assertEquals(1, AppDateUtils.compareDatesByDayMonthYear(testDate1, testDate2));

        testDate2 = TestUtils.createDate(2019, 3, 20);
        assertEquals(1, AppDateUtils.compareDatesByDayMonthYear(testDate1, testDate2));

        testDate2 = TestUtils.createDate(2021, 3, 20);
        assertEquals(-1, AppDateUtils.compareDatesByDayMonthYear(testDate1, testDate2));

        testDate2 = TestUtils.createDate(2020, 3, 21);
        assertEquals(-1, AppDateUtils.compareDatesByDayMonthYear(testDate1, testDate2));

    }

}