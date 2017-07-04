package com.licence.serban.farmcompanion.dashboard.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    private OnAppTitleChange updateTitleCallback;

    public DashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            updateTitleCallback = (OnAppTitleChange) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString()
                    + " must implement OnHeadlineSelectedListener");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        updateTitleCallback.updateTitle(getResources().getString(R.string.nav_dash));

        try {
            Date date = Calendar.getInstance().getTime();
            Log.e("Date", date.toString());
            String dateStr = SimpleDateFormat.getDateTimeInstance().format(date);
            Log.e("DateStr", dateStr);
            String date2Str = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss zz", Locale.ENGLISH).format(date);
            Log.e("Date2Str",date2Str);
            Date date1 = SimpleDateFormat.getDateTimeInstance().parse(dateStr);
            Log.w("Date1", date1.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

}
