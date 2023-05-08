package com.juno.pacsserver.common.util;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;

import static com.juno.pacsserver.common.contants.Constants.Date.*;

@UtilityClass
public class DateUtil {

    public static String getStringDatetime() {
        return LocalDateTime.now().format(FORMATTER_YYYYMMDD_HHMMSS_SSSSSS);
    }

    public static String getStringDate() {
        return LocalDateTime.now().format(FORMATTER_YYYYMMDD);
    }

    public static String getStringTime() {
        return LocalDateTime.now().format(FORMATTER_HH);
    }
}
