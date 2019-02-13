package com.tripnetra.tripnetra.utils;

import android.app.Activity;
import android.app.TimePickerDialog;

import java.util.Calendar;
import java.util.Locale;

public class Time_Picker_Dialog {

    private Activity activity;

    public Time_Picker_Dialog(Activity act){ activity=act; }

    public void TimeDialog(final callback_interface intrfce){

        final Calendar cal = Calendar.getInstance();

        new TimePickerDialog(activity, (view, hourOfDay, minute) -> {
            String format;
            if (hourOfDay == 0) {
                hourOfDay += 12;
                format = "AM";
            } else if (hourOfDay == 12) {
                format = "PM";
            } else if (hourOfDay > 12) {
                hourOfDay -= 12;
                format = "PM";
            } else {
                format = "AM";
            }
            String hhh = String.format(Locale.getDefault(),"%02d", hourOfDay);
            String mmm = String.format(Locale.getDefault(),"%02d", minute);

            intrfce.getResponse(hhh+":"+mmm+" "+format);

        }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), false).show();

    }

    public interface callback_interface {
        void getResponse(String date);
    }
}
