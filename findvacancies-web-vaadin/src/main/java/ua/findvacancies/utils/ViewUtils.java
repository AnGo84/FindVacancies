package ua.findvacancies.utils;

import org.springframework.util.ObjectUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

public class ViewUtils {

    public static String getCopyrightInfo() {
        return "Developed by AnGo 2017-" + LocalDate.now().getYear();
    }
    public static String getCopyrightCurrentYear() {
        return String.valueOf(LocalDate.now().getYear());
    }

    public static String getDateAsString(Date date, Locale locale){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy", locale);
        if (ObjectUtils.isEmpty(date)) {
            return "";
        }
        try {
            return simpleDateFormat.format(date);
        } catch (Exception e) {
            return "";
        }
    }
    public static LocalDate getDateAsLocalDate(Date dateToConvert){
        if (ObjectUtils.isEmpty(dateToConvert)) {
            return LocalDate.now();
        }
        LocalDate localDate = dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return localDate;
    }
}
