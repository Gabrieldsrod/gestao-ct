package com.gabrieldsrod.gestao_ct.Utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    public static final DateTimeFormatter BR_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public static String formatar(LocalDate data) {
        return data.format(BR_FORMATTER);
    }
}
