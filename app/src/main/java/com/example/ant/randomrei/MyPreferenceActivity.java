package com.example.ant.randomrei;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

public class MyPreferenceActivity extends PreferenceActivity {
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public class MyPreferenceFragment extends PreferenceFragment
    {
        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            sp = getSharedPreferences("MyPref", MODE_PRIVATE);
            final TimePreference pref = (TimePreference) findPreference ("timepicker");
            pref.setSummary(Integer.toString(sp.getInt("UPDATEHOUR", 19)) + ":"
                    + Integer.toString(sp.getInt("UPDATEMINUTE", 0)));
        }

        @Override
        public void onResume()
        {
            final TimePreference pref = (TimePreference) findPreference ("timepicker");

            pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    pref.setSummary(Integer.toString(sp.getInt("UPDATEHOUR", 19)) + ":"
                            + Integer.toString(sp.getInt("UPDATEMINUTE", 0)));
                    return true;
                }
            });
            super.onResume();
        }
    }
}