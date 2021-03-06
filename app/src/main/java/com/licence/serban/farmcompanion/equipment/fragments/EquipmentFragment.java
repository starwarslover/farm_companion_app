package com.licence.serban.farmcompanion.equipment.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.equipment.adapters.EquipmentDatabaseAdapter;
import com.licence.serban.farmcompanion.equipment.adapters.EquipmentListAdapter;
import com.licence.serban.farmcompanion.equipment.models.Equipment;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.interfaces.OnFragmentStart;
import com.licence.serban.farmcompanion.misc.Utilities;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class EquipmentFragment extends Fragment {


  private OnAppTitleChange updateTitleCallback;
  private Button newEquipmentButton;
  private ListView equipmentListView;
  private EquipmentListAdapter equipmentAdapter;
  private String userID;
  private EquipmentDatabaseAdapter equipmentDatabaseAdapter;
  private OnFragmentStart startFragmentCallback;

  public EquipmentFragment() {
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

    try {
      startFragmentCallback = (OnFragmentStart) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString()
              + " must implement OnFragmentStart");
    }

  }

  @Override
  public void onDetach() {
    super.onDetach();
    equipmentDatabaseAdapter.removeListener();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

    updateTitleCallback.updateTitle(getResources().getString(R.string.nav_equipment));

    View view = inflater.inflate(R.layout.fragment_equipment, container, false);

    Bundle args = getArguments();
    if (args != null) {
      userID = args.getString(Utilities.Constants.USER_ID);
    }

    setViews(view);

    equipmentDatabaseAdapter = EquipmentDatabaseAdapter.getInstance(userID);
    equipmentDatabaseAdapter.setListener(equipmentAdapter);
    return view;
  }

  private void setViews(View view) {
    newEquipmentButton = (Button) view.findViewById(R.id.equipAddEquipmentButton);
    equipmentListView = (ListView) view.findViewById(R.id.equipmentListView);
    equipmentAdapter = new EquipmentListAdapter(this.getActivity(), R.layout.equipment_row, new ArrayList<Equipment>());
    equipmentListView.setAdapter(equipmentAdapter);

    newEquipmentButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startAddEquipmentFragment();
      }
    });

    equipmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startEquipmentDetails(position);
      }
    });

  }

  private void startEquipmentDetails(int position) {
    Fragment fragment = new EquipmentDetailsFragment();
    Equipment equip = equipmentAdapter.getItem(position);
    Bundle args = new Bundle();
    args.putString(Utilities.Constants.EQUIP_ID_EXTRA, equip != null ? equip.getId() : null);
    args.putString(Utilities.Constants.USER_ID, userID);
    fragment.setArguments(args);

    startFragmentCallback.startFragment(fragment, true);
  }

  private void startAddEquipmentFragment() {
    Fragment fragment = new AddEquipmentFragment();
    Bundle args = new Bundle();
    args.putString(Utilities.Constants.USER_ID, userID);
    fragment.setArguments(args);

    startFragmentCallback.startFragment(fragment, true, Utilities.Constants.DATE_FRAGMENT);
  }

}
