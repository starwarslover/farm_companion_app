package com.licence.serban.farmcompanion.equipment.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.equipment.adapters.EquipmentDatabaseAdapter;
import com.licence.serban.farmcompanion.equipment.models.Equipment;
import com.licence.serban.farmcompanion.equipment.models.EquipmentState;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.interfaces.OnDatePickerSelectedListener;
import com.licence.serban.farmcompanion.interfaces.OnDrawerMenuLock;
import com.licence.serban.farmcompanion.interfaces.OnElementAdded;
import com.licence.serban.farmcompanion.interfaces.OnFragmentStart;
import com.licence.serban.farmcompanion.misc.StringDateFormatter;
import com.licence.serban.farmcompanion.misc.Utilities;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import static com.licence.serban.farmcompanion.R.id.addEquipTransmissionTypeEditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddEquipmentFragment extends Fragment implements OnDatePickerSelectedListener {


  private OnFragmentStart fragmentStartCallback;
  private OnAppTitleChange titleUpdateCallback;
  private Spinner typeSpinner;
  private TextInputEditText manufacturerInputText;
  private TextInputEditText modelInputText;
  private TextInputEditText serialNrInputText;
  private Button addEquipmentButton;
  private String userID;
  private EquipmentDatabaseAdapter databaseAdapter;
  private TextInputEditText plateNumberEditText;
  private OnDrawerMenuLock drawerMenuLock;
  private SeekBar manufacturingYearSeekBar;
  private TextView manufYearSeekBarLabel;
  private TextInputEditText purchaseDateEditText;
  private Button datePickButton;
  private Spinner ownershipSpinner;
  private TextInputEditText purchasePriceEditText;
  private TextInputEditText engineTypeEditText;
  private TextInputEditText engineCapacityEditText;
  private TextInputEditText transmissionTypeEditText;
  private int year;
  private OnElementAdded onDateSelected;

  public AddEquipmentFragment() {
    // Required empty public constructor
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      fragmentStartCallback = (OnFragmentStart) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString()
              + " must implement OnFragmentStart");
    }
    try {
      titleUpdateCallback = (OnAppTitleChange) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString()
              + " must implement OnAppTitleChange");
    }
    try {
      drawerMenuLock = (OnDrawerMenuLock) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString()
              + " must implement OnDrawerMenuLock");
    }
    try {
      onDateSelected = (OnElementAdded) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString()
              + " must implement OnElementAdded");
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
    titleUpdateCallback.updateTitle(getResources().getString(R.string.add_equipment));

    this.year = Calendar.getInstance().get(Calendar.YEAR);
    View view = inflater.inflate(R.layout.fragment_add_equipment, container, false);
    drawerMenuLock.lockDrawer();
    Bundle args = getArguments();
    if (args != null) {
      userID = args.getString(Utilities.Constants.USER_ID);
    }

    setViews(view);

    databaseAdapter = EquipmentDatabaseAdapter.getInstance(userID);

    return view;
  }

  private void setViews(View view) {
    typeSpinner = (Spinner) view.findViewById(R.id.equipmentTypeSpinner);
    manufacturerInputText = (TextInputEditText) view.findViewById(R.id.addEquipManufacturerEditText);
    modelInputText = (TextInputEditText) view.findViewById(R.id.addEquipModelEditText);
    serialNrInputText = (TextInputEditText) view.findViewById(R.id.addEquipSerialNrEditText);
    addEquipmentButton = (Button) view.findViewById(R.id.addEquipmentAddButton);
    plateNumberEditText = (TextInputEditText) view.findViewById(R.id.addEquipPlateNrEditText);
    manufacturingYearSeekBar = (SeekBar) view.findViewById(R.id.addEquipManufacturingYearSeekBar);
    manufYearSeekBarLabel = (TextView) view.findViewById(R.id.manufacturingYearSeekBarLabel);
    purchaseDateEditText = (TextInputEditText) view.findViewById(R.id.addEquipPurchaseDateEditText);
    datePickButton = (Button) view.findViewById(R.id.addEquipDatePickButton);
    ownershipSpinner = (Spinner) view.findViewById(R.id.addEquipOwnershipSpinner);
    purchasePriceEditText = (TextInputEditText) view.findViewById(R.id.addEquipPurchasePriceEditText);
    engineTypeEditText = (TextInputEditText) view.findViewById(R.id.addEquipEngineTypeEditText);
    engineCapacityEditText = (TextInputEditText) view.findViewById(R.id.addEquipEngineCapacityEditText);
    transmissionTypeEditText = (TextInputEditText) view.findViewById(addEquipTransmissionTypeEditText);

    setListeners();
  }

  private void setListeners() {
    addEquipmentButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Equipment equipment = getInputInfo();
        if (equipment == null) {
          return;
        }
        databaseAdapter.insertEquipment(equipment);
        fragmentStartCallback.popBackStack();
      }
    });

    datePickButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String date = purchaseDateEditText.getText().toString().trim();
        long time = -1;
        if (!date.isEmpty()) {
          try {
            Date tempDate = StringDateFormatter.stringToDate(date);
            time = tempDate.getTime();
          } catch (ParseException e) {
            e.printStackTrace();
          }
        }
        onDateSelected.openDatePicker(time);
      }
    });

    if (year != 0)
      manufacturingYearSeekBar.setMax(year - 1990);

    manufacturingYearSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
      @Override
      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        manufYearSeekBarLabel.setText(String.valueOf(1990 + progress));
      }

      @Override
      public void onStartTrackingTouch(SeekBar seekBar) {

      }

      @Override
      public void onStopTrackingTouch(SeekBar seekBar) {

      }
    });
  }

  private Equipment getInputInfo() {
    String type = typeSpinner.getSelectedItem().toString();
    String manufacturer = manufacturerInputText.getText().toString();
    String model = modelInputText.getText().toString();
    String serialNumber = serialNrInputText.getText().toString();
    String plateNr = plateNumberEditText.getText().toString();
    String manufYearStr = manufYearSeekBarLabel.getText().toString();
    int manufYear = 0;
    try {
      manufYear = Integer.parseInt(manufYearStr);
    } catch (Exception ex) {

    }
    String purchaseDateText = purchaseDateEditText.getText().toString();
    long purhcaseDate = 0;
    if (!purchaseDateText.isEmpty()) {
      try {
        purhcaseDate = StringDateFormatter.stringToDate(purchaseDateText).getTime();
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }
    String ownership = ownershipSpinner.getSelectedItem().toString();
    String purchasePriceStr = purchasePriceEditText.getText().toString();
    double purchasePrice = 0;
    try {
      purchasePrice = Double.parseDouble(purchasePriceStr);
    } catch (Exception ex) {

    }
    String engineType = engineTypeEditText.getText().toString();
    String engineCapacityStr = engineCapacityEditText.getText().toString();
    int engineCapacity = 0;
    try {
      engineCapacity = Integer.parseInt(engineCapacityStr);
    } catch (Exception ex) {

    }
    String transmissionType = transmissionTypeEditText.getText().toString();

    if (manufacturer.isEmpty()) {
      manufacturerInputText.setError(getResources().getString(R.string.required_field));
      return null;
    }
    if (model.isEmpty()) {
      modelInputText.setError(getResources().getString(R.string.required_field));
      return null;
    }

    Equipment equipment = new Equipment();
    equipment.setType(type);
    equipment.setManufacturer(manufacturer);
    equipment.setModel(model);
    equipment.setSerialNumber(serialNumber);
    equipment.setState(EquipmentState.DISPONIBIL);
    equipment.setPlateNumber(plateNr);
    equipment.setManufacturingYear(manufYear);
    equipment.setPurchaseDate(purhcaseDate);
    equipment.setOwnership(ownership);
    equipment.setPurchasePrice(purchasePrice);
    equipment.setEngineType(engineType);
    equipment.setEngineCapacity(engineCapacity);
    equipment.setTransmissionType(transmissionType);
    return equipment;
  }

  public void setDate(String date) {
    purchaseDateEditText.setText(date);
  }

}
