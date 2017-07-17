package com.licence.serban.farmcompanion.consumables.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.consumables.adapters.ConsumableDatabaseAdapter;
import com.licence.serban.farmcompanion.consumables.models.Consumable;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.interfaces.OnDatePickerSelectedListener;
import com.licence.serban.farmcompanion.interfaces.OnDrawerMenuLock;
import com.licence.serban.farmcompanion.interfaces.OnElementAdded;
import com.licence.serban.farmcompanion.interfaces.OnFragmentStart;
import com.licence.serban.farmcompanion.misc.ConsumableEnum;
import com.licence.serban.farmcompanion.misc.StringDateFormatter;
import com.licence.serban.farmcompanion.misc.Utilities;

import java.text.ParseException;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConsumableAddFragment extends Fragment implements OnDatePickerSelectedListener {

  private OnAppTitleChange updateTitleCallback;
  private TextInputEditText notesEditText;
  private TextInputEditText sellerEditText;
  private TextInputEditText nameEditText;
  private Button addButton;
  private Spinner typeSpinner;
  private TextInputEditText amountEditText;
  private TextInputEditText priceEditText;
  private TextInputEditText dateEditText;
  private Button dateButton;
  private String userID;
  private OnFragmentStart fragmentStart;
  private OnElementAdded employeeAddedListener;
  private OnDrawerMenuLock drawerLockListener;
  private TextView consUMTextView;


  public ConsumableAddFragment() {
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

    try {
      fragmentStart = (OnFragmentStart) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString()
              + " must implement OnFragmentStart");
    }

    try {
      employeeAddedListener = (OnElementAdded) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString()
              + " must implement OnElementAdded");
    }
    try {
      drawerLockListener = (OnDrawerMenuLock) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString()
              + " must implement OnDrawerMenuLock");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    drawerLockListener.unlockDrawerMenu();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    this.setRetainInstance(true);

    drawerLockListener.lockDrawer();
    View view = inflater.inflate(R.layout.fragment_consumable_add, container, false);

    this.updateTitleCallback.updateTitle(getResources().getString(R.string.add_cons));
    Bundle args = getArguments();
    if (args != null) {
      userID = args.getString(Utilities.Constants.USER_ID);
    }

    setViews(view);

    return view;
  }

  private void setViews(View view) {
    addButton = (Button) view.findViewById(R.id.consAddButton);
    typeSpinner = (Spinner) view.findViewById(R.id.consAddTypeSpinner);
    nameEditText = (TextInputEditText) view.findViewById(R.id.consAddNameEditText);
    amountEditText = (TextInputEditText) view.findViewById(R.id.consAddAmountEditText);
    priceEditText = (TextInputEditText) view.findViewById(R.id.consAddPriceEditText);
    dateEditText = (TextInputEditText) view.findViewById(R.id.consAddPurchaseDateEditText);
    dateButton = (Button) view.findViewById(R.id.consAddPurchaseDatePickButton);
    sellerEditText = (TextInputEditText) view.findViewById(R.id.consAddPurchasedFrom);
    notesEditText = (TextInputEditText) view.findViewById(R.id.consAddNotesEditText);
    consUMTextView = (TextView) view.findViewById(R.id.consUMTextView);
    setListeners();
  }

  private void setListeners() {
    addButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        createConsumable();
      }
    });

    typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
      @Override
      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position == 0 || position == 2)
          consUMTextView.setText(R.string.kg);
        else
          consUMTextView.setText(R.string.liters);
      }

      @Override
      public void onNothingSelected(AdapterView<?> parent) {

      }
    });

    dateButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        String date = dateEditText.getText().toString().trim();
        long time = -1;
        if (!date.isEmpty()) {
          try {
            Date tempDate = StringDateFormatter.stringToDate(date);
            time = tempDate.getTime();
          } catch (ParseException e) {
            e.printStackTrace();
          }
        }
        employeeAddedListener.openDatePicker(time);
      }
    });
  }

  private void createConsumable() {
    Consumable consToAdd = getConsumableFromFields();
    if (consToAdd == null)
      return;

    String id = ConsumableDatabaseAdapter.getInstance(userID).insertConsumable(consToAdd);
    fragmentStart.popBackStack();
  }

  private Consumable getConsumableFromFields() {
    Consumable cons = null;

    int idx = typeSpinner.getSelectedItemPosition();
    ConsumableEnum type = ConsumableEnum.get(idx);
    String name = nameEditText.getText().toString();
    if (name.isEmpty()) {
      nameEditText.setError(getResources().getString(R.string.no_name_error));
      return null;
    }
    double amount = 0;
    String amountStr = amountEditText.getText().toString();
    if (amountStr.isEmpty()) {
      amountEditText.setError(getResources().getString(R.string.required_error));
      return null;
    }

    try {
      amount = Double.parseDouble(amountStr);
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      amountEditText.setError(getResources().getString(R.string.invalid_numeric_val_err));
      return null;
    }

    double price = 0;
    if (!priceEditText.getText().toString().isEmpty())
      try {
        price = Double.parseDouble(priceEditText.getText().toString());
      } catch (Exception ex) {
        System.out.println(ex.getMessage());
        priceEditText.setError(getResources().getString(R.string.invalid_numeric_val_err));
        return null;
      }
    if (price < 0) {
      priceEditText.setError(getResources().getString(R.string.negative_number_err));
      return null;
    }

    if (amount < 0) {
      amountEditText.setError(getResources().getString(R.string.negative_number_err));
      return null;
    }
    String purchaseDateString = dateEditText.getText().toString();
    Date purchaseDate = null;
    if (!purchaseDateString.isEmpty()) {
      try {
        purchaseDate = StringDateFormatter.stringToDate(purchaseDateString);
      } catch (ParseException e) {
        e.printStackTrace();
      }
    }

    String purchasedFrom = sellerEditText.getText().toString();
    String notes = notesEditText.getText().toString();

    cons = new Consumable();
    cons.setType(type);
    cons.setName(name);
    cons.setAmount(amount);
    cons.setPurchasePrice(price);
    if (purchaseDate != null)
      cons.setPurchaseDate(purchaseDate.getTime());
    cons.setPurchasedFrom(purchasedFrom);
    cons.setNotes(notes);

    return cons;
  }


  @Override
  public void setDate(String date) {
    dateEditText.setText(date);
  }
}
