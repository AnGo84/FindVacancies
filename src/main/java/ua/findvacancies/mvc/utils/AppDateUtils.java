package ua.findvacancies.mvc.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AppDateUtils {
    public static Date addDaysToDate(Date date, int days) {
        if (date == null) {
            throw new NullPointerException("Wrong parameter Date!");
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return c.getTime();
    }

    public static int compareDatesByDayAndMonth(Date date1, Date date2){
        if (date1 == null|| date2 ==null){
            throw new NullPointerException("Parameter is NULL");
        }
        SimpleDateFormat fmt = new SimpleDateFormat("MMdd");
        return fmt.format(date1).compareTo(fmt.format(date2));
    }

    public static int compareDatesByDayMonthYear(Date date1, Date date2){
        if (date1 == null|| date2 ==null){
            throw new NullPointerException("Parameter is NULL");
        }
        SimpleDateFormat fmt = new SimpleDateFormat("MMddyyyy");
        return fmt.format(date1).compareTo(fmt.format(date2));
    }

    public static int getYearFromDate(Date date){
        if (date == null){
            throw new NullPointerException("Parameter is NULL");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static Date changeDateYear(Date date, int year){
        if (date == null){
            throw new NullPointerException("Parameter is NULL");
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.YEAR, year);
        return calendar.getTime();
    }

}
