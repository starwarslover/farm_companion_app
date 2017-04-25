package com.licence.serban.farmcompanion.fragments.equipment;


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
import com.licence.serban.farmcompanion.classes.Equipment;
import com.licence.serban.farmcompanion.classes.Utilities;
import com.licence.serban.farmcompanion.classes.adapters.EquipmentDatabaseAdapter;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.interfaces.OnFragmentStart;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddEquipmentFragment extends Fragment {


    private OnFragmentStart fragmentStartCallback;
    private OnAppTitleChange titleUpdateCallback;
    private Spinner typeSpinner;
    private TextInputEditText manufacturerInputText;
    private TextInputEditText modelInputText;
    private TextInputEditText serialNrInputText;
    private Button addEquipmentButton;
    private String userID;
    private EquipmentDatabaseAdapter databaseAdapter;

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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        titleUpdateCallback.updateTitle(getResources().getString(R.string.add_equipment));

        View view = inflater.inflate(R.layout.fragment_add_equipment, container, false);

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
    }

    private Equipment getInputInfo() {
        String type = typeSpinner.getSelectedItem().toString();
        String manufacturer = manufacturerInputText.getText().toString();
        String model = modelInputText.getText().toString();
        String serialNumber = serialNrInputText.getText().toString();

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
        return equipment;
    }

}
