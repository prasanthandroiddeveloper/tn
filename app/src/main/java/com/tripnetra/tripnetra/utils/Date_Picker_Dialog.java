package com.tripnetra.tripnetra.utils;

import android.app.Activity;
import android.app.DatePickerDialog;

import java.util.Calendar;

public class Date_Picker_Dialog {

    private Activity activity;
    private long mindate,maxdate;

    public Date_Picker_Dialog(Activity act, long min, long max){
        activity=act;
        mindate = min;
        maxdate = max;
    }

    public void DateDialog(final callback_interface intrfce){
        final Calendar cal = Calendar.getInstance();

        DatePickerDialog pickerDialog = new DatePickerDialog(activity, (view, year, month, day) -> {
            Calendar calndr = Calendar.getInstance();
            calndr.set(year,month,day);

            intrfce.getResponse(Utils.DatetoStr(calndr.getTime(),0));

        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

        pickerDialog.show();
        pickerDialog.getDatePicker().setMinDate(mindate);
        pickerDialog.getDatePicker().setMaxDate(maxdate);

    }

    public interface callback_interface {
        void getResponse(String date);
    }

}