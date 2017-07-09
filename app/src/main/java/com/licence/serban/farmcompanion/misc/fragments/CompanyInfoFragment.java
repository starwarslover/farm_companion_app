package com.licence.serban.farmcompanion.misc.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;
import com.licence.serban.farmcompanion.misc.Utilities;
import com.licence.serban.farmcompanion.misc.models.Company;
import com.licence.serban.farmcompanion.misc.models.Coordinates;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompanyInfoFragment extends Fragment {
  private EditText companyNameEditText;
  private EditText adminAreaEditText;
  private EditText zipCodeEditText;
  private EditText countryEditText;
  private OnAppTitleChange updateTitleCallback;
  private Button saveChangesButton;
  private GoogleApiClient googleApiClient;

  private String userID;
  private boolean editMode = false;

  private DatabaseReference databaseReference;
  private Button placePickerButton;
  private final int REQUEST_PLACE = 20;
  private EditText subAdminAreaEditText;
  private EditText localityEditText;
  private EditText subLocalityEditText;
  private Coordinates coordinates;
  private EditText companyInfoAddressEditText;
  public static final String DB_COMPANIES = "companies";
  private ValueEventListener compListener;

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
  public void onDetach() {
    super.onDetach();
    databaseReference.child(DB_COMPANIES).child(userID).removeEventListener(compListener);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           final Bundle savedInstanceState) {
    updateTitleCallback.updateTitle(getResources().getString(R.string.nav_company_info));

    databaseReference = FirebaseDatabase.getInstance().getReference();

    View view = inflater.inflate(R.layout.fragment_company_info, container, false);
    userID = getArguments().getString(Utilities.Constants.USER_ID);

    companyNameEditText = (EditText) view.findViewById(R.id.companyInfoCompanyNameEditText);
    adminAreaEditText = (EditText) view.findViewById(R.id.companyInfoAdminAreaEditText);
    subAdminAreaEditText = (EditText) view.findViewById(R.id.companyInfoSubAdminArea);
    localityEditText = (EditText) view.findViewById(R.id.companyInfoLocalityEditText);
    subLocalityEditText = (EditText) view.findViewById(R.id.companyInfoSubLocalityEditText);
    zipCodeEditText = (EditText) view.findViewById(R.id.companyInfoZipCodeEditText);
    countryEditText = (EditText) view.findViewById(R.id.companyInfoCountryEditText);
    saveChangesButton = (Button) view.findViewById(R.id.companyInfoSaveButton);
    placePickerButton = (Button) view.findViewById(R.id.companyInfoOpenPickPlaceDialogButton);
    companyInfoAddressEditText = (EditText) view.findViewById(R.id.companyInfoAddressEditText);


    compListener = new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        Company company = dataSnapshot.getValue(Company.class);
        if (company != null) {
          companyNameEditText.setText(company.getName());
          adminAreaEditText.setText(company.getAdminArea());
          subAdminAreaEditText.setText(company.getSubAdminArea());
          localityEditText.setText(company.getLocality());
          subLocalityEditText.setText(company.getSublocality());
          zipCodeEditText.setText(company.getZipCode());
          countryEditText.setText(company.getCountry());
          companyInfoAddressEditText.setText(company.getFullAddress());
        }
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    };
    databaseReference.child(DB_COMPANIES).child(userID).addValueEventListener(compListener);

    placePickerButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
          startActivityForResult(builder.build(CompanyInfoFragment.this.getActivity()), REQUEST_PLACE);
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
          e.printStackTrace();
        }
      }
    });

    saveChangesButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (!editMode) {
          editMode = true;
          switchEditMode();
          saveChangesButton.setText(getResources().getString(R.string.save_changes));
        } else {
          String companyName = companyNameEditText.getText().toString().trim();
          String country = countryEditText.getText().toString().trim();
          String adminArea = adminAreaEditText.getText().toString().trim();
          String subAdminArea = subAdminAreaEditText.getText().toString().trim();
          String locality = localityEditText.getText().toString();
          String subLocality = subLocalityEditText.getText().toString();
          String zipCode = zipCodeEditText.getText().toString().trim();
          String fullAddress = companyInfoAddressEditText.getText().toString().trim();

          Company company = new Company();
          company.setName(companyName);
          company.setCountry(country);
          company.setAdminArea(adminArea);
          company.setSubAdminArea(subAdminArea);
          company.setLocality(locality);
          company.setSublocality(subLocality);
          company.setCoordinates(coordinates);
          company.setZipCode(zipCode);
          company.setFullAddress(fullAddress);

          DatabaseReference companiesRef = databaseReference.child(DB_COMPANIES).child(userID);
          companiesRef
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

        }
      }
    });

    return view;
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == REQUEST_PLACE && resultCode == Activity.RESULT_OK) {
      Place place = PlacePicker.getPlace(data, CompanyInfoFragment.this.getActivity());
      Geocoder geocoder = new Geocoder(CompanyInfoFragment.this.getActivity(), Locale.getDefault());
      LatLng latLng = place.getLatLng();
      if (latLng != null) {
        try {
          List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 2);
          if (addresses.size() != 0) {
            Address selectedAddress = addresses.get(0);
            coordinates = new Coordinates(latLng.latitude, latLng.longitude);
            String country = selectedAddress.getCountryName() != null ? selectedAddress.getCountryName() : "N/A";
            String locality = selectedAddress.getLocality() != null ? selectedAddress.getLocality() : "N/A";
            String zipCode = selectedAddress.getPostalCode() != null ? selectedAddress.getPostalCode() : "N/A";
            String admin = selectedAddress.getAdminArea() != null ? selectedAddress.getAdminArea() : "N/A";
            String sublocality = selectedAddress.getSubLocality() != null ? selectedAddress.getSubLocality() : "N/A";
            String subadmin = selectedAddress.getSubAdminArea() != null ? selectedAddress.getSubAdminArea() : "N/A";
            String fullAddress = selectedAddress.getAddressLine(0) != null ? selectedAddress.getAddressLine(0) : "N/A";
            countryEditText.setText(country);
            adminAreaEditText.setText(admin);
            subAdminAreaEditText.setText(subadmin);
            localityEditText.setText(locality);
            subLocalityEditText.setText(sublocality);
            zipCodeEditText.setText(zipCode);
            companyInfoAddressEditText.setText(fullAddress);
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }

  private void switchEditMode() {
    companyNameEditText.setEnabled(editMode);
    int visibility = View.VISIBLE;
    if (!editMode)
      visibility = View.GONE;
    placePickerButton.setVisibility(visibility);
  }

}
