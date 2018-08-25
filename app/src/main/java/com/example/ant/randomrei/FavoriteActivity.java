package com.example.ant.randomrei;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class FavoriteActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sp = getSharedPreferences("MyPref", MODE_PRIVATE);

        String favorite = sp.getString("FAVORITES", null);
        String[] favorites;
        if (favorite != null) {
            favorites = favorite.split(" ");
        } else {
            favorites = new String[0];
        }
        Log.i("mylog_favs", "hu" + TextUtils.join(", ", favorites));

        ListView favorite_list = (ListView) findViewById(R.id.list);
        TextView tv = (TextView) findViewById(R.id.myempty);
        tv.setText("No favorites");
        favorite_list.setEmptyView(tv);
        DataAdapter adapter = new DataAdapter(this, favorites);
        favorite_list.setAdapter(adapter);

    }
}
