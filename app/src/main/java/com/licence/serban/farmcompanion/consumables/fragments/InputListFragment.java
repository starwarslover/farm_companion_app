package com.licence.serban.farmcompanion.consumables.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.consumables.adapters.ConsumableDatabaseAdapter;
import com.licence.serban.farmcompanion.consumables.adapters.ConsumableListAdapter;
import com.licence.serban.farmcompanion.consumables.models.Consumable;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.misc.ConsumableEnum;
import com.licence.serban.farmcompanion.misc.Utilities;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class InputListFragment extends Fragment {
  private ConsumableEnum inputType;
  private TextView titleTextView;
  private OnAppTitleChange updateTitleCallback;
  private String userID;
  private ListView consListView;
  private ConsumableListAdapter consAdapter;


  public InputListFragment() {
    // Required empty public constructor
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_input_list, container, false);
//        titleTextView = (TextView) view.findViewById(R.id.inputListTitle);
    consListView = (ListView) view.findViewById(R.id.consListView);
    consAdapter = new ConsumableListAdapter(getActivity(), R.layout.consumable_row, new ArrayList<Consumable>());
    consListView.setAdapter(consAdapter);

    Bundle args = getArguments();
    if (args != null) {
      userID = args.getString(Utilities.Constants.USER_ID);
    }

    inputType = (ConsumableEnum) getArguments().getSerializable(Utilities.Constants.BUNDLE_INPUT_TYPE);

    ConsumableDatabaseAdapter.getInstance(userID).setListener(consAdapter, inputType);
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

  }

  private void fuelBehaviour() {

  }

  private void chemicalBehaviour() {

  }

  private void seedBehaviour() {

  }


}
