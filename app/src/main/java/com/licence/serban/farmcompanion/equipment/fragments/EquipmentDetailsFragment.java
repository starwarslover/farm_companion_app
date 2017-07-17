package com.licence.serban.farmcompanion.equipment.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.equipment.adapters.EquipmentDatabaseAdapter;
import com.licence.serban.farmcompanion.equipment.models.Equipment;
import com.licence.serban.farmcompanion.interfaces.OnDrawerMenuLock;
import com.licence.serban.farmcompanion.interfaces.OnFragmentStart;
import com.licence.serban.farmcompanion.misc.StringDateFormatter;
import com.licence.serban.farmcompanion.misc.Utilities;

/**
 * A simple {@link Fragment} subclass.
 */
public class EquipmentDetailsFragment extends Fragment {


  private OnDrawerMenuLock drawerMenuLock;
  private String userID;
  private String equipID;
  private DatabaseReference mainReference;
  private DatabaseReference equipReference;
  private TextView equipDetailsTypeTextView;
  private TextView equipDetailsManufTextView;
  private TextView equipDetailsModelTextView;
  private TextView equipDetailsSerialNrTextView;
  private TextView equipDetailsPlateNrTextView;
  private TextView equipDetailsManufYearTextView;
  private TextView equipDetailsPurchaseDateTextView;
  private TextView equipDetailsPurchasedPriceTextView;
  private TextView equipDetailsOwnershipTextView;
  private TextView equipDetailsEngineTypeTextView;
  private TextView equipDetailsEngineCapacityTextView;
  private TextView equipDetailsTransmTypeTextView;
  private ImageButton equipDetailsDeleteButton;
  private OnFragmentStart fragmentStart;
  private TextView equipDetailsNameTextView;
  private ValueEventListener equipListener;

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
    try {
      fragmentStart = (OnFragmentStart) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString()
              + " must implement OnFragmentStart");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    drawerMenuLock.unlockDrawerMenu();
    equipReference.removeEventListener(equipListener);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    View view = inflater.inflate(R.layout.fragment_equipment_details, container, false);
    drawerMenuLock.lockDrawer();

    Bundle args = getArguments();
    if (args != null) {
      userID = args.getString(Utilities.Constants.USER_ID);
      equipID = args.getString(Utilities.Constants.EQUIP_ID_EXTRA);
    }

    setViews(view);
    setDbRefs();
    fillViews();
    return view;
  }

  private void fillViews() {
    equipListener = new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        Equipment equipment = dataSnapshot.getValue(Equipment.class);
        if (equipment != null) {
          setFieldValues(equipment);
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    };

    this.equipReference.addValueEventListener(equipListener);

  }

  private void setFieldValues(Equipment equipment) {
    equipDetailsTypeTextView.setText(equipment.getType());
    String name = equipment.getManufacturer() + " " + equipment.getModel();
    equipDetailsNameTextView.setText(name);
    equipDetailsManufTextView.setText(equipment.getManufacturer());
    equipDetailsModelTextView.setText(equipment.getModel());
    equipDetailsSerialNrTextView.setText(!equipment.getSerialNumber().isEmpty() ? equipment.getSerialNumber() : "N/A");
    equipDetailsPlateNrTextView.setText(!equipment.getPlateNumber().isEmpty() ? equipment.getPlateNumber() : "N/A");
    equipDetailsManufYearTextView.setText(String.valueOf(equipment.getManufacturingYear()));
    String purchaseDate = "N/A";
    if (equipment.getPurchaseDate() != 0)
      purchaseDate = StringDateFormatter.millisToString(equipment.getPurchaseDate(), "dd.MM.yyyy");
    equipDetailsPurchaseDateTextView.setText(purchaseDate);
    equipDetailsPurchasedPriceTextView.setText(equipment.getPurchasePrice() == 0 ? String.valueOf(equipment.getPurchasePrice()) : "N/A");
    equipDetailsOwnershipTextView.setText(equipment.getOwnership());
    equipDetailsEngineTypeTextView.setText(!equipment.getEngineType().isEmpty() ? equipment.getEngineType() : "N/A");
    String capacity = equipment.getEngineCapacity() != 0 ? String.valueOf(equipment.getEngineCapacity()) + " " +
            getResources().getString(R.string.horse_power) : "N/A";
    equipDetailsEngineCapacityTextView.setText(capacity);
    equipDetailsTransmTypeTextView.setText(!equipment.getTransmissionType().isEmpty() ? equipment.getTransmissionType() : "N/A");
  }

  private void setDbRefs() {
    mainReference = FirebaseDatabase.getInstance().getReference();
    equipReference = mainReference.child(EquipmentDatabaseAdapter.DB_EQUIPMENTS).child(userID).child(equipID);
  }

  private void setViews(View view) {
    equipDetailsTypeTextView = (TextView) view.findViewById(R.id.equipDetailsTypeTextView);
    equipDetailsNameTextView = (TextView) view.findViewById(R.id.equipDetailsNameTextView);
    equipDetailsManufTextView = (TextView) view.findViewById(R.id.equipDetailsManufTextView);
    equipDetailsModelTextView = (TextView) view.findViewById(R.id.equipDetailsModelTextView);
    equipDetailsSerialNrTextView = (TextView) view.findViewById(R.id.equipDetailsSerialNrTextView);
    equipDetailsPlateNrTextView = (TextView) view.findViewById(R.id.equipDetailsPlateNrTextView);
    equipDetailsManufYearTextView = (TextView) view.findViewById(R.id.equipDetailsManufYearTextView);
    equipDetailsPurchaseDateTextView = (TextView) view.findViewById(R.id.equipDetailsPurchaseDateTextView);
    equipDetailsPurchasedPriceTextView = (TextView) view.findViewById(R.id.equipDetailsPurchasedPriceTextView);
    equipDetailsOwnershipTextView = (TextView) view.findViewById(R.id.equipDetailsOwnershipTextView);
    equipDetailsEngineTypeTextView = (TextView) view.findViewById(R.id.equipDetailsEngineTypeTextView);
    equipDetailsEngineCapacityTextView = (TextView) view.findViewById(R.id.equipDetailsEngineCapacityTextView);
    equipDetailsTransmTypeTextView = (TextView) view.findViewById(R.id.equipDetailsTransmTypeTextView);
    equipDetailsDeleteButton = (ImageButton) view.findViewById(R.id.equipDetailsDeleteButton);

    equipDetailsDeleteButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        new AlertDialog.Builder(EquipmentDetailsFragment.this.getActivity(), R.style.AlertDialogCustom)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(getResources().getString(R.string.confirm_delete))
                .setMessage(getResources().getString(R.string.confirm_delete_message))
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    equipReference.removeValue()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                              @Override
                              public void onComplete(@NonNull Task<Void> task) {
                                fragmentStart.popBackStack();
                              }
                            });
                  }

                })
                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                  @Override
                  public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                  }
                })
                .show();
      }
    });
  }

}
