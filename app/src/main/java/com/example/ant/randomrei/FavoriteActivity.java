package com.example.ant.randomrei;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
//import android.text.TextUtils;
//import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

public class FavoriteActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            final SharedPreferences sp = getSharedPreferences("MyPref", MODE_PRIVATE);
            final JSONArray favorites_json = new JSONArray(sp.getString("FAVORITES", "[]"));

            final String[] favorites = new String[favorites_json.length()];
            for (int i = 0; i < favorites_json.length(); i++) {
                favorites[i] = favorites_json.getString(i);
            }

//            Log.i("mylog_favs", TextUtils.join(", ", favorites));

            final ListView favorite_list = (ListView) findViewById(R.id.list);
            TextView tv = (TextView) findViewById(R.id.myempty);
            tv.setText(R.string.nofavorites);
            favorite_list.setEmptyView(tv);
            DataAdapter adapter = new DataAdapter(this, favorites);
            favorite_list.setAdapter(adapter);

            favorite_list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                               int pos, long id) {
                    try {
                        JSONArray favorites_json_new = new JSONArray();
                        for (int i = 0; i < favorites_json.length(); i++) {
                            if (!favorites_json.get(i).equals(favorites[pos])) {
                                favorites_json_new.put(favorites_json.get(i));
                            }
                        }

                        sp.edit().putString("FAVORITES", favorites_json_new.toString()).apply();

                        Toast.makeText(FavoriteActivity.this, favorites[pos]
                                .concat(" was removed from favorites."), Toast.LENGTH_LONG).show();
                        recreate();
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
