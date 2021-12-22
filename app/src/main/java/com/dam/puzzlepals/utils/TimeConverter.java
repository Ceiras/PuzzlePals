package com.dam.puzzlepals.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeConverter {

    public static String convertDateToCompleteFormatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        return dateFormat.format(date);
    }

    public static String convertTimeMillisToReadableString(long score) {
        int milliseconds = (int) (score % 1000);
        int seconds = (int) (score / 1000);
        int minutes = 0;
        int hours = 0;
        if (seconds >= 60) {
            minutes = seconds / 60;
            seconds = seconds % 60;
        }
        if (minutes >= 60) {
            hours = minutes / 60;
            minutes = minutes % 60;
        }

        if (hours > 0) {
            return String.format(Locale.getDefault(), "%dh %dm %ds %dms", hours, minutes, seconds, milliseconds);
        } else if (minutes > 0) {
            return String.format(Locale.getDefault(), "%dm %ds %dms", minutes, seconds, milliseconds);
        } else if (seconds > 0) {
            return String.format(Locale.getDefault(), "%ds %dms", seconds, milliseconds);
        } else {
            return String.format(Locale.getDefault(), "%dms", milliseconds);
        }
    }

}
