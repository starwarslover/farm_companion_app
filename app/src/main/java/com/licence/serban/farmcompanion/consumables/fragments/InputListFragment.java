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
  public void onDetach() {
    super.onDetach();
    ConsumableDatabaseAdapter.getInstance(userID).removeListeners();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_input_list, container, false);
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
        case SEMINTE: {
          seedBehaviour();
          break;
        }
        case IERBICIDE: {
          chemicalBehaviour();
          break;
        }
        case COMBUSTIBIL: {
          fuelBehaviour();
          break;
        }
        case FERTILIZATORI: {
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
