package com.licence.serban.farmcompanion.fragments.equipment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.licence.serban.farmcompanion.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class EquipmentDetailsFragment extends Fragment {


    public EquipmentDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_equipment_details, container, false);
    }

}
