package com.licence.serban.farmcompanion.fragments.emp_account;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
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
import com.licence.serban.farmcompanion.interfaces.OnCreateNewTaskListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmployeeTasksFragment extends Fragment {


    private OnAppTitleChange updateTitleCallback;
    private Button startBroadcastButton;
    private DatabaseReference databaseReference;
    private String userID;
    private String employerID;
    private DatabaseReference mainReference;
    private DatabaseReference userActivitiesReference;
    private DatabaseReference fullActivitiesReference;
    private Button newTaskButton;
    private OnCreateNewTaskListener newTaskCallback;


    public EmployeeTasksFragment() {
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
        try {
            newTaskCallback = (OnCreateNewTaskListener) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString()
                    + " must implement OnNewTaskListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_employee_tasks, container, false);

        newTaskButton = (Button) view.findViewById(R.id.empTaskNewTaskButton);

        updateTitleCallback.updateTitle(getResources().getString(R.string.my_tasks));

        newTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newTaskCallback.openNewTaskUI();
            }
        });
//
//        databaseReference = FirebaseDatabase.getInstance().getReference();
//        Bundle args = getArguments();
//        if (args != null) {
//            userID = args.getString(Utilities.Constants.USER_ID);
//            employerID = args.getString(Utilities.Constants.DB_EMPLOYER_ID);
//        }
//        mainReference = FirebaseDatabase.getInstance().getReference();
//        userActivitiesReference = mainReference.child(Utilities.Constants.DB_USERS).child(employerID).child(Utilities.Constants.DB_ACTIVITIES);
//        fullActivitiesReference = mainReference.child(Utilities.Constants.DB_ACTIVITIES);
//        startBroadcastButton = (Button) view.findViewById(R.id.startBroadCastingButton);
//        startBroadcastButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                LocationManager locationManager = (LocationManager) EmployeeTasksFragment.this.getActivity().getSystemService(Context.LOCATION_SERVICE);
//                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(EmployeeTasksFragment.this.getActivity());
//                    builder.setTitle(R.string.gps_not_found_title);  // GPS not found
//                    builder.setMessage(R.string.gps_not_found_message); // Want to enable?
//                    builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                            EmployeeTasksFragment.this.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                        }
//                    });
//                    builder.setNegativeButton(R.string.no, null);
//                    builder.create().show();
//                    return;
//                }
//
//                int permissionCheck = ContextCompat.checkSelfPermission(EmployeeTasksFragment.this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
//                if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {
//                    Toast.makeText(EmployeeTasksFragment.this.getActivity(), "Here!", Toast.LENGTH_SHORT).show();
//                    EmployeeLocationListener locationListener = new EmployeeLocationListener(databaseReference, userID);
//                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10, 10, locationListener);
//                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10, 10, locationListener);
//                    databaseReference.child(Utilities.Constants.DB_USERS).child(employerID).child(Utilities.Constants.DB_ACTIVE_TASKS).child(userID).setValue(true);
////                    userActivitiesReference.child()
//                }
//            }
//        });

        return view;
    }

}
