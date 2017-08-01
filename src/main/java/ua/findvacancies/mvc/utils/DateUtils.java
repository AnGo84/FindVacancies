package ua.findvacancies.mvc.utils;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by AnGo on 27.06.2017.
 */
public class DateUtils {
    public static Date addDaysToDate(Date date, int days) {
        if (date == null) {
            throw new NullPointerException("Wrong parameter Date!");
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return c.getTime();
    }
}
