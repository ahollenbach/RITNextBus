package com.andrewhollenbach.ritnextbus;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.InputStream;

public class StopSpinnerSelectedListener implements AdapterView.OnItemSelectedListener {

    private View rootView;
    private Activity activity;

    public StopSpinnerSelectedListener(View view, Activity activity) {
        super();

        this.rootView = view;
        this.activity = activity;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        SharedPreferences preferences = activity.getPreferences(Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("route", parent.getItemAtPosition(pos).toString());

        editor.commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
