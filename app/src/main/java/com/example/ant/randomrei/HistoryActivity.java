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
        int monthToFind = sp.getInt("MONTHTOFIND", 1);
        int dayToFind   = sp.getInt("DAYTOFIND", 1);

        ArrayList<String> pictures = new ArrayList<>();
        String history = sp.getString("HISTORY", null);

//        if (history == null) {
//            Toast.makeText(HistoryActivity.this, "No history at all.",
//                    Toast.LENGTH_LONG).show();
//            finish();
//        }

        try {
            try {
                JSONArray historyArray = new JSONArray(history);

                for (int i = 0; i < historyArray.length(); i++) {
                    JSONObject day = historyArray.getJSONObject(i);

                    Log.i("mylog_hist", day.getString("pictures"));

                    if (day.getInt("day") == dayToFind &&
                            day.getInt("month") == monthToFind) {

                        Log.i("mylog_hist", "Day found.");

                        for (int j = 0; j < day.getJSONArray("pictures").length(); j++) {
                            pictures.add(day.getJSONArray("pictures").getString(j));
                            Log.i("mylog_hist", day.getJSONArray("pictures").getString(j));
                        }
                    }
                }
                Log.i("mylog_hist", "hi" + TextUtils.join(", ", pictures));

                ListView history_list = (ListView) findViewById(R.id.list);
                history_list.setEmptyView(findViewById(R.id.myempty));
                DataAdapter adapter = new DataAdapter(this, pictures.toArray(new String[0]));
                history_list.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (NullPointerException g) {
            g.getLocalizedMessage();

            ListView history_list = (ListView) findViewById(R.id.list);
            TextView tv = (TextView) findViewById(R.id.myempty);
            tv.setText("No history for this day");
            history_list.setEmptyView(tv);
            DataAdapter adapter = new DataAdapter(this, new String[0]);
            history_list.setAdapter(adapter);
        }
    }
}
