package ua.findvacancies.utils;

import java.time.LocalDate;

public class ViewUtils {

    public static String getCopyrightInfo() {
        return "Developed by AnGo 2017-" + LocalDate.now().getYear();
    }

}
