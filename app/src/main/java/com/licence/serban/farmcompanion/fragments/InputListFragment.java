package com.licence.serban.farmcompanion.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.classes.InputTypeEnum;
import com.licence.serban.farmcompanion.classes.Utilities;


/**
 * A simple {@link Fragment} subclass.
 */
public class InputListFragment extends Fragment {
    private InputTypeEnum inputType;
    private TextView titleTextView;


    public InputListFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_input_list, container, false);
        titleTextView = (TextView) view.findViewById(R.id.inputListTitle);

        inputType = (InputTypeEnum) getArguments().getSerializable(Utilities.Constants.BUNDLE_INPUT_TYPE);
        if (inputType != null) {
            switch (inputType) {
                case SEED: {
                    seedBehaviour();
                    break;
                }
                case CHEMICAL: {
                    chemicalBehaviour();
                    break;
                }
                case FUEL: {
                    fuelBehaviour();
                    break;
                }
                case FERTILISER: {
                    fertiliserBehaviour();
                    break;
                }
            }
        }
        return view;
    }

    private void fertiliserBehaviour() {
        titleTextView.setText("Fertiliser");
    }

    private void fuelBehaviour() {
        titleTextView.setText("Fuel");
    }

    private void chemicalBehaviour() {
        titleTextView.setText("Chemicals");
    }

    private void seedBehaviour() {
        titleTextView.setText("Seeds");
    }


}
