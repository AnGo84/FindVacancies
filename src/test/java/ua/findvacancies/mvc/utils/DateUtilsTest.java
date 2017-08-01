package ua.findvacancies.mvc.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.util.Date;

import static org.junit.Assert.assertEquals;

/**
 * Created by AnGo on 28.07.2017.
 */
public class DateUtilsTest {

    @Rule
    public TestName name = new TestName();

    @Before
    public void setUp() throws Exception {
        System.out.println("Test start " + name.getMethodName());
    }

    @After
    public void tearDown() throws Exception {
        System.out.println("Test end " + name.getMethodName());
    }

    @Test
    public void addDaysToDate() throws Exception {
        Date now = new Date();
        assertEquals(now, DateUtils.addDaysToDate(now, 0));

    }

    @Test(expected = NullPointerException.class)
    public void addDaysToDateNullDateParam() throws Exception {
        DateUtils.addDaysToDate(null, 0);

    }


}