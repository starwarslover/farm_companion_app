package com.licence.serban.farmcompanion.emp_account.fragments;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.misc.Utilities;
import com.licence.serban.farmcompanion.tasks.adapters.TasksDatabaseAdapter;
import com.licence.serban.farmcompanion.tasks.models.Task;
import com.licence.serban.farmcompanion.interfaces.OnFragmentStart;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmpTaskDetailsFragment extends Fragment {


    private String employerID;
    private String taskID;
    private DatabaseReference taskReference;
    private Task currentTask;
    private Button startTaskButton;
    private OnFragmentStart startFragmentCallback;

    public EmpTaskDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_emp_task_details, container, false);

        Bundle args = getArguments();
        if (args != null) {
            employerID = args.getString(Utilities.Constants.DB_EMPLOYER_ID);
            taskID = args.getString(Utilities.Constants.TASK_ID_EXTRA);
        }

        setViews(view);

        setDatabaseListener();


        return view;
    }

    private void setDatabaseListener() {
        taskReference = FirebaseDatabase.getInstance().getReference().child(TasksDatabaseAdapter.DB_TASKS).child(employerID).child(taskID);
        taskReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                currentTask = dataSnapshot.getValue(Task.class);
                if (currentTask != null) {
                    setViewsContent();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setViews(View view) {
        startTaskButton = (Button) view.findViewById(R.id.empTaskDetailsStartTaskButton);
        startTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isGpsProviderEnabled()) {
                    startTrackingFragment();
                } else {
                    requestGpsEnable();
                }
            }
        });
    }

    private void requestGpsEnable() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());
        builder.setTitle(R.string.gps_not_found_title);  // GPS not found
        builder.setMessage(R.string.gps_not_found_message); // Want to enable?
        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton(R.string.no, null);
        builder.create().show();
    }

    private boolean isGpsProviderEnabled() {
        LocationManager locationManager = (LocationManager) this.getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void startTrackingFragment() {
        EmpTaskTrackingFragment trackingFragment = new EmpTaskTrackingFragment();
        Bundle args = new Bundle();
        args.putString(Utilities.Constants.DB_EMPLOYER_ID, employerID);
        args.putString(Utilities.Constants.TASK_ID_EXTRA, taskID);
        trackingFragment.setArguments(args);
        startFragmentCallback.startFragment(trackingFragment, true);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            startFragmentCallback = (OnFragmentStart) context;
        } catch (Exception ex) {

        }
    }

    private void setViewsContent() {

    }

}
