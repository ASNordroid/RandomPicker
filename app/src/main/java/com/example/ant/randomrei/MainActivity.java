package com.example.ant.randomrei;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.SharedPreferences;
import android.Manifest;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.IllegalFormatConversionException;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ListView list;
    Parcelable state;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        sp = getSharedPreferences("MyPref", MODE_PRIVATE);
    }

    @Override
    protected void onStart() {
        Log.i("mylog_main_onStart", Integer.toString(sp.getInt("UPDATEHOUR", 19))
        + " " + Integer.toString(sp.getInt("UPDATEMINUTE", 0)));
        Log.i("mylog_main_onStart", sp.getString("FOLDERPATH", "None FOLDERPATH"));

        long updateTimestamp = sp.getLong("UPDATETIMESTAMP",0);
        Log.i("mylog_main_onStart", String.valueOf(updateTimestamp));

        if (sp.getString("FOLDERPATH", null) != null) {
            if ((System.currentTimeMillis() - updateTimestamp) >= 24 * 60 * 60 * 1000) {
                updateContentInView();
            } else {
                try {
                    JSONArray curPictures = new JSONArray(sp.getString("CURPICTURES", null));
                    String[] picsToShow = new String[curPictures.length()];
                    for (int i = 0; i < curPictures.length(); i++) {
                        try {
                            picsToShow[i] = (String) curPictures.get(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    showList(picsToShow);
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                    updateContentInView();
                }
            }
        } else {
            ListView tList = (ListView) findViewById(R.id.list);
            TextView tv = (TextView) findViewById(R.id.myempty);
            tv.setText(R.string.nofolder);
            tList.setEmptyView(tv);
            DataAdapter adapter = new DataAdapter(this, new String[0]);
            tList.setAdapter(adapter);
        }

        super.onStart();
    }

    @Override
    protected void onPause() {
        state = list.onSaveInstanceState();
        super.onPause();
    }

    @Override
    protected void onResume() {
        if(state != null) {
            list.onRestoreInstanceState(state);
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.action_favorites:
                startActivity(new Intent(MainActivity.this,
                        FavoriteActivity.class));
                return true;
            case R.id.action_history:
                // workaround for crash on some SAMSUNG devices
                Context context = new ContextWrapper(this) {
                    private Resources wrappedResources;

                    @Override
                    public Resources getResources() {
                        Resources r = super.getResources();
                        if(wrappedResources == null) {
                            wrappedResources = new Resources(r.getAssets(), r.getDisplayMetrics(),
                                    r.getConfiguration()) {
                                @NonNull @Override
                                public String getString(int id, Object... formatArgs) throws NotFoundException {
                                    try {
                                        return super.getString(id, formatArgs);
                                    } catch (IllegalFormatConversionException ifce) {
                                        //Log.e("DatePickerDialogFix", "IllegalFormatConversionException Fixed!", ifce);
                                        String template = super.getString(id);
                                        template = template.replaceAll("%" + ifce.getConversion(), "%s");
                                        return String.format(getConfiguration().locale, template, formatArgs);
                                    }
                                }
                            };
                        }
                        return wrappedResources;
                    }
                };
                //----------------------------------------------------------------------------------

                Calendar today = Calendar.getInstance();
                DatePickerDialog dpd = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        Intent historyIntent = new Intent(MainActivity.this,
                                HistoryActivity.class);
                        historyIntent.putExtra("MONTHTOFIND", month + 1);
                        historyIntent.putExtra("DAYTOFIND", day);
                        startActivity(historyIntent);
                    }
                }, today.get(Calendar.YEAR),
                   today.get(Calendar.MONTH),
                   today.get(Calendar.DAY_OF_MONTH));

                dpd.show();
                return true;
            case R.id.action_folderpicker:
                if (Build.VERSION.SDK_INT >= 23 && !storagePermissionAvailable()) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            111);
                }

                Intent folderIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                folderIntent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(Intent.createChooser(folderIntent,
                        "Choose directory"), 9999);
                return true;
            case R.id.action_loadnew:
                updateContentInView();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this,
                        MyPreferenceActivity.class));
                return true;
            case R.id.action_about:
                Toast.makeText(MainActivity.this,
                        R.string.about, Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //change to exit on double press back ???
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Exit")
                .setMessage("Are you sure?")
                .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                        Intent intent = new Intent(Intent.ACTION_MAIN);
//                        intent.addCategory(Intent.CATEGORY_HOME);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                        startActivity(intent);
                        finish();
//                        System.exit(0); // delete ??
                    }
                })
                .setNegativeButton("no", null)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 9999 && resultCode == MainActivity.RESULT_OK) {
            try {
                // can I make it intent instead of sharedpref ???
                sp.edit().putString("FOLDERPATH",
                        '/' + Objects.requireNonNull(data.getData()).getPath().split(":")[1]).apply();
//                Log.i("mylog_main", sp.getString("FOLDERPATH", "Nonono"));
            } catch (NullPointerException e) {
                e.getLocalizedMessage();
            }

            setNewRandomListFromFolder();
            updateContentInView();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case 111:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Yay!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Permission not granted", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    boolean storagePermissionAvailable() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void saveTodaySet(String[] pictures) {
        Calendar today = Calendar.getInstance();
        JSONObject todayJO = new JSONObject();

        try {
            todayJO.put("day", today.get(Calendar.DAY_OF_MONTH));
            todayJO.put("month", today.get(Calendar.MONTH) + 1);
            todayJO.put("pictures", new JSONArray(pictures));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray todayJA;
        String oldPicturesString = sp.getString("HISTORY", "[]"); // null ???
        try {
            todayJA = new JSONArray(oldPicturesString);
        } catch (JSONException | NullPointerException e) { //better catch
            e.printStackTrace();
            todayJA = new JSONArray();
        }
        todayJA.put(todayJO);
        Log.i("mylog_main", todayJA.toString());

        sp.edit().putString("HISTORY", todayJA.toString()).apply();
    }

    private void updateContentInView() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.set(Calendar.HOUR, sp.getInt("UPDATEHOUR",19));
        cal.set(Calendar.MINUTE, sp.getInt("UPDATEMINUTE",0));
        cal.set(Calendar.SECOND, 0);
        sp.edit().putLong("UPDATETIMESTAMP", cal.getTimeInMillis()).apply();

        Integer numberOfPics = Integer.parseInt(PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString("numberOfPics", "10"));
        Log.i("mylog_main", numberOfPics.toString());
        String[] picsToShow = getNewPics(numberOfPics);

        try {
            JSONArray curPictures = new JSONArray(picsToShow);
            sp.edit().putString("CURPICTURES", curPictures.toString()).apply();
            saveTodaySet(picsToShow);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showList(picsToShow);
    }

    private void showList(final String[] picsToShow) {
        list = (ListView) findViewById(R.id.list);
        list.setEmptyView(findViewById(R.id.myempty));
        DataAdapter adapter = new DataAdapter(this, picsToShow);
        list.setAdapter(adapter);

        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                                           int pos, long id) {
                try {
                    JSONArray favorites = new JSONArray(sp.getString("FAVORITES", "[]"));
                    favorites.put(picsToShow[pos]);
                    sp.edit().putString("FAVORITES", favorites.toString()).apply();

                    Toast.makeText(MainActivity.this, picsToShow[pos]
                            .concat(" was added to favorites."), Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                return true;
            }
        });
    }

    private String[] getNewPics(int numberOfPics) {
        try {
            List<String> allImagesList = new ArrayList<>(sp.getStringSet("ALLIMAGESSET", new HashSet<String>()));
            long seed = System.nanoTime();
            Collections.shuffle(allImagesList, new Random(seed));

            Log.i("mylog_main", Integer.toString(allImagesList.size()));

            if (allImagesList.size() < numberOfPics) {
                setNewRandomListFromFolder();
                allImagesList = new ArrayList<>(sp.getStringSet("ALLIMAGESSET", new HashSet<String>()));
            }

            String[] imageIds = new String[numberOfPics];
            try {
                for (int i = 0; i < numberOfPics; ++i) {
                    imageIds[i] = (allImagesList.remove(allImagesList.size() - 1));
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                e.getLocalizedMessage();
            }

            sp.edit().putStringSet("ALLIMAGESSET", new HashSet<>(allImagesList)).apply();

            return imageIds;
        } catch (NullPointerException g) {
            g.printStackTrace();
            return new String[0];
        }
    }

    private void setNewRandomListFromFolder() {
        File folderPath = new File(Environment.getExternalStorageDirectory(),
                sp.getString("FOLDERPATH", ""));

        Log.i("mylog_main", folderPath.toString());

        HashSet<String> pictures = new HashSet<>();

        try {
            for (File f : folderPath.listFiles())
                if (f.isFile() && (f.getPath().endsWith(".jpeg")
                        || f.getPath().endsWith(".png")
                        || f.getPath().endsWith(".jpg")))
                    pictures.add(f.getAbsolutePath());

            sp.edit().putStringSet("ALLIMAGESSET", pictures).apply();
        } catch (NullPointerException e) {
            Log.i("mylog_main", e.getLocalizedMessage());
        }
    }
}