package com.andrewhollenbach.ritnextbus;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.provider.ContactsContract;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;


public class RITNextBusActivity extends Activity implements ActionBar.TabListener {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ritnext_bus);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Can't just actionBar.hide(), because that hides the tabs as well
        actionBar.setDisplayShowHomeEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);

        // set up data
        int resourceId = getResources().getIdentifier("com.andrewhollenbach.ritnextbus:raw/data.json",null,null);
        JSONObject data = loadJsonFromFile(R.raw.data);
        DataManager.setData(data);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.ritnext_bus, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch(position) {
                case 0:
                    return NextBusFragment.newInstance();
                case 1:
                    return ScheduleFragment.newInstance();
                case 2:
                    return PreferencesFragment.newInstance();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // JSON data stuff
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public JSONObject loadJsonFromFile(int file) {
        String jsonString = "";
        try {
            InputStream is = getResources().openRawResource(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            jsonString = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        JSONObject json = null;
        try {
            json = new JSONObject(jsonString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Fragment Classes
    ///////////////////////////////////////////////////////////////////////////////////////////////


    /**
     * The main Next Bus view. Displays the next time at home and academic.
     */
    public static class NextBusFragment extends Fragment {

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static NextBusFragment newInstance() {
            return new NextBusFragment();
        }

        public NextBusFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_nextbus, container, false);

            SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm");

            Date residential = DataManager.getNextResidential();
            TextView tTipHome = (TextView)rootView.findViewById(R.id.mainTimeTipHome);
            tTipHome.setText("(" + timeFormat.format(residential) + ")");

            Date academic    = DataManager.getNextAcademic();
            TextView tTipAcad = (TextView)rootView.findViewById(R.id.mainTimeTipDest);
            tTipAcad.setText("(" + timeFormat.format(academic) + ")");

            return rootView;
        }
    }

    /**
     * Displays a table of route times
     */
    public static class ScheduleFragment extends Fragment {

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static ScheduleFragment newInstance() {
            return new ScheduleFragment();
        }

        public ScheduleFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_schedule, container, false);

            return rootView;
        }
    }

    /**
     * The view which displays options to select route and learn about the app.
     */
    public static class PreferencesFragment extends Fragment {

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PreferencesFragment newInstance() {
            return new PreferencesFragment();
        }

        public PreferencesFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_preferences, container, false);

            return rootView;
        }
    }

}
