package com.licence.serban.farmcompanion.equipment.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.interfaces.OnDrawerMenuLock;

/**
 * A simple {@link Fragment} subclass.
 */
public class EquipmentDetailsFragment extends Fragment {


  private OnDrawerMenuLock drawerMenuLock;

  public EquipmentDetailsFragment() {
    // Required empty public constructor
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      drawerMenuLock = (OnDrawerMenuLock) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString()
              + " must implement OnDrawerMenuLock");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    drawerMenuLock.unlockDrawerMenu();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    drawerMenuLock.lockDrawer();
    return inflater.inflate(R.layout.fragment_equipment_details, container, false);
  }

}
