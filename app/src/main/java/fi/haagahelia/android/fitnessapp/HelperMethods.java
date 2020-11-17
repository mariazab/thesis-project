package fi.haagahelia.android.fitnessapp;

import java.util.Calendar;

//Class for methods used globally
public class HelperMethods {

    //Method for formatting the date in the format XX.XX.XXXX
    public static String getDateInStringFormat(Calendar c) {
        String date = "";
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = c.get(Calendar.DAY_OF_MONTH);

        if (day < 10 && month < 10) {
            date += "0" + day + ".0" + month;
        } else if (day < 10){
            date += "0" + day + "." + month;
        } else if (month < 10) {
            date += day + ".0" + month;
        } else {
            date += day + "." + month;
        }

        date += "." + year;
        return date;
    }

    //Method for showing the duration properly
    public static String showDurationAsString(int hours, int minutes, int seconds) {
        String duration = "";
        if (hours != 0) {
            duration += hours + ":";
        }

        if (minutes == 0) {
            duration += "00";
        } else if (minutes < 10) {
            duration += "0" + minutes;
        } else {
            duration += + minutes;
        }

        if (seconds == 0) {
            duration += ":00";
        } else if (seconds < 10) {
            duration += ":0" + seconds;
        } else {
            duration += ":" + seconds;
        }
        return duration;
    }

}