package com.andrewhollenbach.RITNextBus;

import android.graphics.Color;
import android.os.CountDownTimer;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class NextBusTimer extends CountDownTimer {
    int badColor;
    int goodColor;

    public Date nextStop;
    public TextView timeTextView;
    public LinearLayout container;
    public String location;

    public NextBusTimer(long millisInFuture, long countDownInterval, Date nextStop, TextView timeTextView, LinearLayout container, String location) {
        super(millisInFuture,countDownInterval);

        this.nextStop = nextStop;
        this.timeTextView = timeTextView;
        this.container = container;
        this.location = location;

        setTextTime();

        // initialize colors
        goodColor = Color.rgb(119,170,0);
        badColor  = Color.rgb(204,0,0);
    }

    @Override
    public void onTick(long millisUntilFinished) {
        String time = String.format("%d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished),
                TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished)));

        timeTextView.setText(time);
        if(millisUntilFinished < 180000) { // 3 minutes
            container.setBackgroundColor(badColor);
        }
    }

    @Override
    public void onFinish() {
        container.setBackgroundColor(goodColor);

        setTextTime();

        ArrayList<Date> rTimes = DataManager.getNext4("residentialRoutes");
        ArrayList<Date> aTimes = DataManager.getNext4("academicRoutes");
        RITNextBusActivity.ScheduleFragment.updateTable(rTimes, aTimes);

        Date residential = DataManager.getNextResidential();
        Date academic    = DataManager.getNextAcademic();
        RITNextBusActivity.NextBusFragment.startTimers(residential, academic);
    }

    public void setTextTime() {
        if(location.equals("residentialRoutes")) {
            TextView timeTip = (TextView) container.findViewById(R.id.mainTimeTipHome);
            timeTip.setText("(" + DataManager.printTimeFormat.format(nextStop) + ")");
        } else {
            TextView timeTip = (TextView) container.findViewById(R.id.mainTimeTipDest);
            timeTip.setText("(" + DataManager.printTimeFormat.format(nextStop) + ")");
        }
    }
}
