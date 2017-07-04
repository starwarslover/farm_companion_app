package com.licence.serban.farmcompanion.consumables.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.consumables.adapters.ConsumableDatabaseAdapter;
import com.licence.serban.farmcompanion.consumables.models.Consumable;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.interfaces.OnFragmentStart;
import com.licence.serban.farmcompanion.misc.ConsumableEnum;
import com.licence.serban.farmcompanion.misc.Utilities;

/**
 * A simple {@link Fragment} subclass.
 */
public class ConsumableAddFragment extends Fragment {

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
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {

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

    setListeners();
  }

  private void setListeners() {
    addButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        createConsumable();
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
    double amount;
    try {
      amount = Double.parseDouble(amountEditText.getText().toString());
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      amountEditText.setError(getResources().getString(R.string.invalid_numeric_val_err));
      return null;
    }

    double price;
    try {
      price = Double.parseDouble(priceEditText.getText().toString());
    } catch (Exception ex) {
      System.out.println(ex.getMessage());
      priceEditText.setError(getResources().getString(R.string.invalid_numeric_val_err));
      return null;
    }

    cons = new Consumable();
    cons.setType(type);
    cons.setName(name);
    cons.setAmount(amount);
    cons.setPurchasePrice(price);

    return cons;
  }


}
