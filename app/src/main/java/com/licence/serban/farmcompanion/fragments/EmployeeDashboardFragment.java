package com.licence.serban.farmcompanion.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmployeeDashboardFragment extends Fragment {


    private OnAppTitleChange updateTitleCallback;

    public EmployeeDashboardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            updateTitleCallback = (OnAppTitleChange) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString()
                    + " must implement OnAppTitleChange");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_employee_dashboard, container, false);

        updateTitleCallback.updateTitle(getResources().getString(R.string.nav_dash));

        return view;
    }

}
