package com.example.ant.randomrei;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sp = getSharedPreferences("MyPref", MODE_PRIVATE);
        int monthToFind = getIntent().getIntExtra("MONTHTOFIND", 1);
        int dayToFind   = getIntent().getIntExtra("DAYTOFIND",1);

//        Log.i("mylog_hist0", Integer.toString(monthToFind) + "  " + Integer.toString(dayToFind));

        ArrayList<String> pictures = new ArrayList<>();
        String history = sp.getString("HISTORY", "[]");

//        try {
        try {
            JSONArray historyArray = new JSONArray(history);
            boolean found = false;

            for (int i = 0; i < historyArray.length(); i++) {
                JSONObject day = historyArray.getJSONObject(i);

//                    Log.i("mylog_hist2", Integer.toString(day.getInt("day"))
//                    + "---" + Integer.toString(day.getInt("month")));
//                    Log.i("mylog_hist2", day.getString("pictures"));

                if (day.getInt("day") == dayToFind &&
                        day.getInt("month") == monthToFind) {
                    found = true;
//                        Log.i("mylog_hist3", "Day found.");

                    for (int j = 0; j < day.getJSONArray("pictures").length(); j++) {
                        pictures.add(day.getJSONArray("pictures").getString(j));
//                            Log.i("mylog_hist4", day.getJSONArray("pictures").getString(j));
                    }
                }
            }
//                Log.i("mylog_hist1", TextUtils.join(", ", pictures));
            if (found) {
                ListView history_list = (ListView) findViewById(R.id.list);
                DataAdapter adapter = new DataAdapter(this, pictures.toArray(new String[0]));
                history_list.setAdapter(adapter);
            } else {
                ListView history_list = (ListView) findViewById(R.id.list);
                TextView tv = (TextView) findViewById(R.id.myempty);
                tv.setText(R.string.nohistory);
                history_list.setEmptyView(tv);
                DataAdapter adapter = new DataAdapter(this, new String[0]);
                history_list.setAdapter(adapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        } catch (NullPointerException g) {
//            g.getLocalizedMessage();
//
//            ListView history_list = (ListView) findViewById(R.id.list);
//            TextView tv = (TextView) findViewById(R.id.myempty);
//            tv.setText(R.string.nohistory);
//            history_list.setEmptyView(tv);
//            DataAdapter adapter = new DataAdapter(this, new String[0]);
//            history_list.setAdapter(adapter);
//        }
    }
}
