package com.example.ant.randomrei;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TimePreference extends DialogPreference {//implements Preference.OnPreferenceChangeListener {
    private TimePicker picker;
    private SharedPreferences sp;

    public TimePreference(Context ctxt, AttributeSet attrs) {
        super(ctxt, attrs);
        sp = ctxt.getSharedPreferences("MyPref", Activity.MODE_PRIVATE);
        try {
            //Preference preference = getPreferenceManager().findPreference("timepicker");
            Calendar tmp = Calendar.getInstance();
            tmp.set(Calendar.HOUR, sp.getInt("UPDATEHOUR", 19));
            tmp.set(Calendar.MINUTE, sp.getInt("UPDATEMINUTE", 0));
//            this.setOnPreferenceChangeListener(this);
            //preference.setSummary(new SimpleDateFormat("HH:mm").format(tmp.getTime()));
        } catch (NullPointerException e) {
            //Log.i("mytest")
            e.getLocalizedMessage();
        }
    }

    @Override
    protected View onCreateDialogView() {
        picker = new TimePicker(getContext());
        return (picker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        picker.setIs24HourView(true);
        // Delete: ???
        Calendar calendar = new GregorianCalendar();
        picker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
        picker.setCurrentMinute(calendar.get(Calendar.MINUTE));
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            sp.edit().putInt("UPDATEHOUR", picker.getCurrentHour()).apply();
            sp.edit().putInt("UPDATEMINUTE", picker.getCurrentMinute()).apply();
            callChangeListener(picker);
        }
    }

//    protected void updateSummary(final long time) {
//        final DateFormat dateFormat = android.text.format.DateFormat.getTimeFormat(getContext());
//        final Date date = new Date(time);
//        setSummary(dateFormat.format(date.getTime()));
//    }

//    @Override
//    public boolean onPreferenceChange(final Preference preference, final Object newValue) {
//        Preference pref = findPreference("timepicker");
//        ((TimePreference) preference).updateSummary((Long) newValue);
//        return true;
//    }
}
