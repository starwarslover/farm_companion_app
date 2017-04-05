package com.licence.serban.farmcompanion.fragments;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.classes.EmployeeLocationListener;
import com.licence.serban.farmcompanion.classes.Utilities;
import com.licence.serban.farmcompanion.interfaces.OnAppTitleChange;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmployeeWorkFragment extends Fragment {


    private OnAppTitleChange updateTitleCallback;
    private Button startBroadcastButton;
    private DatabaseReference databaseReference;
    private String userID;

    public EmployeeWorkFragment() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_employee_work, container, false);

        updateTitleCallback.updateTitle(getResources().getString(R.string.my_tasks));

        databaseReference = FirebaseDatabase.getInstance().getReference();
        Bundle args = getArguments();
        if (args != null) {
            userID = args.getString(Utilities.Constants.USER_ID);
        }

        startBroadcastButton = (Button) view.findViewById(R.id.startBroadCastingButton);
        startBroadcastButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocationManager locationManager = (LocationManager) EmployeeWorkFragment.this.getActivity().getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeWorkFragment.this.getActivity());
                    builder.setTitle(R.string.gps_not_found_title);  // GPS not found
                    builder.setMessage(R.string.gps_not_found_message); // Want to enable?
                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EmployeeWorkFragment.this.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });
                    builder.setNegativeButton(R.string.no, null);
                    builder.create().show();
                    return;
                }

                int permissionCheck = ContextCompat.checkSelfPermission(EmployeeWorkFragment.this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
                    Toast.makeText(EmployeeWorkFragment.this.getActivity(), "Here!", Toast.LENGTH_SHORT).show();
                    EmployeeLocationListener locationListener = new EmployeeLocationListener(databaseReference, userID);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, locationListener);
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 10, locationListener);
                }
            }
        });
        return view;
    }

}
