package ua.findvacancies.utils;

import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AppDateUtils {

    public static final String DATE_FORMAT_DD_MM_YYYY = "dd.MM.yyyy";
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY);

    public static Date addDaysToDate(Date date, int days) {
        if (date == null) {
            throw new NullPointerException("Wrong parameter Date!");
        }
        Calendar c = getCalendar(date);
        c.add(Calendar.DATE, days);
        return c.getTime();
    }

    public static int compareDatesByDayAndMonth(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new NullPointerException("Parameter is NULL");
        }
        SimpleDateFormat fmt = new SimpleDateFormat("MMdd");
        return fmt.format(date1).compareTo(fmt.format(date2));
    }

    public static int compareDatesByDayMonthYear(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            throw new NullPointerException("Parameter is NULL");
        }
        return simpleDateFormat.format(date1).compareTo(simpleDateFormat.format(date2));
    }

    public static int getYearFromDate(Date date) {
        if (date == null) {
            throw new NullPointerException("Parameter is NULL");
        }
        Calendar calendar = getCalendar(date);
        return calendar.get(Calendar.YEAR);
    }

    public static Date changeDateYear(Date date, int year) {
        if (date == null) {
            throw new NullPointerException("Parameter is NULL");
        }
        Calendar calendar = getCalendar(date);
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

    private static Calendar getCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear(Calendar.HOUR_OF_DAY);
        calendar.clear(Calendar.AM_PM);
        calendar.clear(Calendar.MINUTE);
        calendar.clear(Calendar.SECOND);
        calendar.clear(Calendar.MILLISECOND);
        calendar.setTime(date);
        return calendar;
    }

    public static String formatToString(Date date) {
        if (ObjectUtils.isEmpty(date)) {
            return "";
        }
        try {
            return simpleDateFormat.format(date);
        } catch (Exception e) {
            return "";
        }
    }

}
