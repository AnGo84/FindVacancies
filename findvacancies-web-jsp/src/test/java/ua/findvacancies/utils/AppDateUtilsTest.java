package ua.findvacancies.utils;

import org.junit.jupiter.api.Test;
import ua.findvacancies.TestUtils;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class AppDateUtilsTest {

    @Test
    public void whenAddDaysToDate_thenOk() {
        Date now = new Date();
        assertEquals(now, AppDateUtils.addDaysToDate(now, 0));
    }

    @Test
    public void whenAddDaysToDateWithNullDateParam_thenThrowNPE() {
        assertThrows(NullPointerException.class,
                ()->{AppDateUtils.addDaysToDate(null, 0);});
    }

    @Test
    public void whenChangeDateYear_thenThrowNPE() {
        assertThrows(NullPointerException.class,
                ()->{AppDateUtils.changeDateYear(null, 0);});
    }

    @Test
    public void whenChangeDateYear_thenReturnResult() {
        assertNotNull(AppDateUtils.changeDateYear(new Date(), 2020));

        Date expectedDate = TestUtils.createDate(2020, 3, 20);
        Date testDate = TestUtils.createDate(123, 3, 20);
        assertEquals(expectedDate, AppDateUtils.changeDateYear(testDate, 2020));
        assertNotEquals(expectedDate, AppDateUtils.changeDateYear(testDate, 2019));
    }

    @Test
    public void whenGetYearFromDate_thenThrowNPE() {
        assertThrows(NullPointerException.class,
                ()->{AppDateUtils.getYearFromDate(null);});
    }

    @Test
    public void whenGetYearFromDate_thenReturnResult() {
        assertNotNull(AppDateUtils.getYearFromDate(new Date()));

        Date testDate = TestUtils.createDate(2020, 3, 20);
        assertEquals(2020, AppDateUtils.getYearFromDate(testDate));
    }

    @Test
    public void whenCompareDatesByDayAndMonthAndNullParams_thenThrowNPE() {
        assertThrows(NullPointerException.class,
                ()->{AppDateUtils.compareDatesByDayAndMonth(null, null);});
    }

    @Test
    public void whenCompareDatesByDayAndMonthAndNullFirstParam_thenThrowNPE() {
        assertThrows(NullPointerException.class,
                ()->{AppDateUtils.compareDatesByDayAndMonth(null, new Date());});
    }

    @Test
    public void whenCompareDatesByDayAndMonthAndNullSecondParam_thenThrowNPE() {
        assertThrows(NullPointerException.class,
                ()->{AppDateUtils.compareDatesByDayAndMonth(new Date(), null);});
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

    @Test
    public void whenCompareDatesByDayMonthYearAndNullParams_thenThrowNPE() {
        assertThrows(NullPointerException.class,
                ()->{AppDateUtils.compareDatesByDayMonthYear(null, null);});
    }

    @Test
    public void whenCompareDatesByDayMonthYearAndNullFirstParam_thenThrowNPE() {
        assertThrows(NullPointerException.class,
                ()->{AppDateUtils.compareDatesByDayMonthYear(null, new Date());});
    }

    @Test
    public void whenCompareDatesByDayMonthYearAndNullSecondParam_thenThrowNPE() {
        assertThrows(NullPointerException.class,
                ()->{AppDateUtils.compareDatesByDayMonthYear(new Date(), null);});
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