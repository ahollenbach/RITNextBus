package com.andrewhollenbach.RITNextBus;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Date;

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

        DataManager.curRouteName = parent.getItemAtPosition(pos).toString();

        // update NextBus
        Date residential = DataManager.getNextResidential();
        Date academic    = DataManager.getNextAcademic();
        RITNextBusActivity.NextBusFragment.startTimers(residential, academic);

        // update schedule
        ArrayList<Date> rTimes = DataManager.getNext4("residentialRoutes");
        ArrayList<Date> aTimes = DataManager.getNext4("academicRoutes");
        RITNextBusActivity.ScheduleFragment.updateTable(rTimes, aTimes);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
