package com.gabrieldsrod.gestao_ct.Utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static final DateTimeFormatter BR_FORMATTER_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static final DateTimeFormatter BR_FORMATTER_DATETIME = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public static String format(LocalDate data) {
        return data.format(BR_FORMATTER_DATE);
    }
}
