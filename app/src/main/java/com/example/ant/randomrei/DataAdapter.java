package com.example.ant.randomrei;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

//import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.File;

public class DataAdapter extends ArrayAdapter<String> {
    private Context context;
    private LayoutInflater inflater;
    private String[] mData;

    DataAdapter(Context context, String[] mData) {
        super(context, R.layout.image, mData);

        this.context = context;
        this.mData = mData;

        inflater = LayoutInflater.from(context);
    }

    @Override @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.image, parent, false);
        }

//        Glide
//                .with(context)
//                .load(mData[position])
//                .crossFade()
//                .into((ImageView) convertView);

        Picasso
                .get()
                .setLoggingEnabled(true);

        Picasso
                .get()
                .load(new File(mData[position]))
                .fit()
                .centerInside()
                .into((ImageView) convertView);

        return convertView;
    }
}
