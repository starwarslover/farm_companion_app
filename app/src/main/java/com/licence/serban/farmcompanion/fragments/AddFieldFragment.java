package com.licence.serban.farmcompanion.fragments;


import android.content.Context;
import android.os.Bundle;
import android.renderscript.Double2;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.classes.Utilities;
import com.licence.serban.farmcompanion.classes.models.CompanyField;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.interfaces.OnDrawerMenuLock;
import com.licence.serban.farmcompanion.interfaces.OnElementAdded;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddFieldFragment extends Fragment {

    private String userId = null;
    private String fieldID = null;
    private boolean editMode = false;
    private CompanyField fieldToEdit = null;

    private OnAppTitleChange updateTitleCallback;
    private OnDrawerMenuLock lockDrawerMenuCallback;
    private OnElementAdded fieldAddedListener;

    private Spinner ownershipSpinner;
    private TextInputEditText notesEditText;
    private TextInputEditText areaEditText;
    private TextInputEditText locationEditText;
    private TextInputEditText nameEditText;
    private Button addFieldButton;

    private DatabaseReference firebaseDatabase;
    private DatabaseReference userReference;
    private DatabaseReference fieldsReference;

    public AddFieldFragment() {
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
            lockDrawerMenuCallback = (OnDrawerMenuLock) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString()
                    + " must implement OnDrawerMenuLock");
        }
        try {
            fieldAddedListener = (OnElementAdded) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString()
                    + " must implement OnElementAdded");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_field, container, false);

        lockDrawerMenuCallback.lockDrawer();

        findViewsById(view);

        Bundle arguments = getArguments();

        if (arguments != null) {
            userId = arguments.getString(Utilities.Constants.USER_ID);
            fieldID = arguments.getString(Utilities.Constants.FIELD_ID);
        }

        firebaseDatabase = FirebaseDatabase.getInstance().getReference();
        userReference = firebaseDatabase.child(Utilities.Constants.DB_USERS).child(userId);
        fieldsReference = firebaseDatabase.child(Utilities.Constants.DB_FIELDS);

        if (fieldID != null) {
            doEditField(view);
        } else {
            doAddField(view);
        }


        return view;
    }

    private void doAddField(View view) {
        updateTitleCallback.updateTitle(getResources().getString(R.string.add_field));

        addFieldButton.setText(getResources().getString(R.string.add_field));
        addFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fieldName = nameEditText.getText().toString().trim();
                String fieldLocation = locationEditText.getText().toString().trim();
                String fieldAreaString = areaEditText.getText().toString().trim();
                String fieldOwnership = ownershipSpinner.getSelectedItem().toString().trim();
                String fieldNotes = notesEditText.getText().toString().trim();

                if (!validateInputData(fieldName, fieldAreaString)) {
                    return;
                }

                CompanyField companyField = new CompanyField();
                companyField.setName(fieldName);
                companyField.setLocation(fieldLocation);
                companyField.setArea(Double.parseDouble(fieldAreaString));
                companyField.setOwnership(fieldOwnership);
                companyField.setCropStatus(getResources().getString(R.string.unseeded_string));
                companyField.setNotes(fieldNotes);


                String id = fieldsReference.push().getKey();
                companyField.setId(id);
                fieldsReference.child(id).setValue(companyField);
                userReference.child(Utilities.Constants.DB_FIELDS).child(id).setValue(true);

                fieldAddedListener.endFragment();
            }
        });
    }


    private void doEditField(View view) {

        addFieldButton.setText(getResources().getString(R.string.edit_button));
        setViewsState(editMode);

        fieldsReference.child(fieldID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                fieldToEdit = dataSnapshot.getValue(CompanyField.class);
                nameEditText.setText(fieldToEdit.getName());
                locationEditText.setText(fieldToEdit.getLocation());
                areaEditText.setText(String.valueOf(fieldToEdit.getArea()));
                ownershipSpinner.setSelection(((ArrayAdapter<String>) ownershipSpinner.getAdapter()).getPosition(fieldToEdit.getOwnership()));
                notesEditText.setText(fieldToEdit.getNotes());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        addFieldButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editMode) {
                    String fieldName = nameEditText.getText().toString().trim();
                    String fieldLocation = locationEditText.getText().toString().trim();
                    String fieldAreaString = areaEditText.getText().toString().trim();
                    String fieldOwnership = ownershipSpinner.getSelectedItem().toString().trim();
                    String fieldNotes = notesEditText.getText().toString().trim();

                    if (!validateInputData(fieldName, fieldAreaString)) {
                        return;
                    }

                    fieldToEdit.setName(fieldName);
                    fieldToEdit.setLocation(fieldLocation);
                    fieldToEdit.setArea(Double.parseDouble(fieldAreaString));
                    fieldToEdit.setOwnership(fieldOwnership);
                    fieldToEdit.setCropStatus(getResources().getString(R.string.unseeded_string));
                    fieldToEdit.setNotes(fieldNotes);

                    fieldsReference.child(fieldToEdit.getId()).setValue(fieldToEdit);

                    editMode = false;
                    addFieldButton.setText(getResources().getString(R.string.edit_button));
                    setViewsState(editMode);
                    fieldAddedListener.endFragment();
                } else {
                    editMode = true;
                    addFieldButton.setText(getResources().getString(R.string.save_changes));
                    setViewsState(editMode);

                }

            }
        });


    }

    private void setViewsState(boolean state) {
        nameEditText.setEnabled(state);
        locationEditText.setEnabled(state);
        areaEditText.setEnabled(state);
        ownershipSpinner.setEnabled(state);
        notesEditText.setEnabled(state);


    }

    private boolean validateInputData(String fieldName, String fieldAreaString) {

        if (fieldName.isEmpty()) {
            nameEditText.setError(getResources().getString(R.string.no_field_name));
            return false;
        }
        if (fieldAreaString.isEmpty()) {
            areaEditText.setError(getResources().getString(R.string.no_field_area_error));
            return false;
        } else {
            if (Double.parseDouble(fieldAreaString) <= 0) {
                areaEditText.setError(getResources().getString(R.string.invalid_area_error));
                return false;
            }
        }

        return true;
    }

    void findViewsById(View view) {
        nameEditText = (TextInputEditText) view.findViewById(R.id.addFieldNameEditText);
        locationEditText = (TextInputEditText) view.findViewById(R.id.addFieldLocationEditText);
        areaEditText = (TextInputEditText) view.findViewById(R.id.addFieldAreaEditText);
        notesEditText = (TextInputEditText) view.findViewById(R.id.addFieldNotesEditText);
        ownershipSpinner = (Spinner) view.findViewById(R.id.addFieldOwnershipSpinner);
        addFieldButton = (Button) view.findViewById(R.id.addFieldAddButton);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        lockDrawerMenuCallback.unlockDrawerMenu();
    }
}
