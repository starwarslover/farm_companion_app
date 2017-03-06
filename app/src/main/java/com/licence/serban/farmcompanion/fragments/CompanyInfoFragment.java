package com.licence.serban.farmcompanion.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.classes.Company;
import com.licence.serban.farmcompanion.classes.Utilities;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompanyInfoFragment extends Fragment {
    private EditText companyNameEditText;
    private EditText cityEditText;
    private EditText zipCodeEditText;
    private EditText countryEditText;
    private OnAppTitleChange updateTitleCallback;
    private Button saveChangesButton;

    private String userID;
    private boolean editMode = false;

    private DatabaseReference databaseReference;

    public CompanyInfoFragment() {
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        updateTitleCallback.updateTitle(getResources().getString(R.string.nav_company_info));

        databaseReference = FirebaseDatabase.getInstance().getReference();

        View view = inflater.inflate(R.layout.fragment_company_info, container, false);
        userID = getArguments().getString(Utilities.Constants.USER_ID);

        companyNameEditText = (EditText) view.findViewById(R.id.companyInfoCompanyNameEditText);
        cityEditText = (EditText) view.findViewById(R.id.companyInfoCityEditText);
        zipCodeEditText = (EditText) view.findViewById(R.id.companyInfoZipCodeEditText);
        countryEditText = (EditText) view.findViewById(R.id.companyInfoCountryEditText);
        saveChangesButton = (Button) view.findViewById(R.id.companyInfoSaveButton);

        databaseReference.child(Utilities.Constants.DB_USERS).child(userID).child(Utilities.Constants.DB_COMPANY).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Company company = dataSnapshot.getValue(Company.class);
                if (company != null) {
                    companyNameEditText.setText(company.getName());
                    cityEditText.setText(company.getCity());
                    zipCodeEditText.setText(company.getZipCode());
                    countryEditText.setText(company.getCountry());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editMode) {
                    String companyName = companyNameEditText.getText().toString().trim();
                    String country = countryEditText.getText().toString().trim();
                    String city = cityEditText.getText().toString().trim();
                    String zipCode = zipCodeEditText.getText().toString().trim();

                    Company company = new Company();
                    company.setName(companyName);
                    company.setCity(city);
                    company.setCountry(country);
                    company.setZipCode(zipCode);

                    databaseReference.child(Utilities.Constants.DB_USERS).child(userID).child(Utilities.Constants.DB_COMPANY)
                            .setValue(company)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(CompanyInfoFragment.this.getActivity(), getResources().getString(R.string.save_success), Toast.LENGTH_SHORT).show();
                                }
                            });
                    editMode = false;
                    switchEditMode();
                    saveChangesButton.setText(getResources().getString(R.string.edit_button));

                } else {
                    editMode = true;
                    switchEditMode();
                    saveChangesButton.setText(getResources().getString(R.string.save_changes));
                }
            }
        });

        return view;
    }

    private void switchEditMode() {
        companyNameEditText.setEnabled(editMode);
        cityEditText.setEnabled(editMode);
        zipCodeEditText.setEnabled(editMode);
        countryEditText.setEnabled(editMode);
    }
}
