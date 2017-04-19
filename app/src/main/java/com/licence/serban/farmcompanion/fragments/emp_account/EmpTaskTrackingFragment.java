package com.licence.serban.farmcompanion.fragments.emp_account;


import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.classes.Utilities;
import com.licence.serban.farmcompanion.classes.adapters.TasksDatabaseAdapter;
import com.licence.serban.farmcompanion.classes.models.Coordinates;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmpTaskTrackingFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, LocationListener {


    private static final int REQ_PERMISSION_LOCATION = 25;
    private String taskID;
    private DatabaseReference activeTasksReference;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private LocationRequest mLocationRequest;
    private DatabaseReference currentTaskReference;
    private Button stopTaskButton;
    private String employerID;
    private DatabaseReference userActiveTasksReference;

    public EmpTaskTrackingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_emp_task_tracking, container, false);

        Bundle args = getArguments();
        if (args != null) {
            taskID = args.getString(Utilities.Constants.TASK_ID_EXTRA);
            employerID = args.getString(Utilities.Constants.DB_EMPLOYER_ID);
        }


        stopTaskButton = (Button) view.findViewById(R.id.empTrackStopBroadcastButton);
        stopTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopBroadcasting();
            }
        });

        googleApiClient = new GoogleApiClient.Builder(this.getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .build();

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setSmallestDisplacement(10);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        activeTasksReference = FirebaseDatabase.getInstance().getReference().child(Utilities.Constants.DB_ACTIVE_TASKS);
        currentTaskReference = FirebaseDatabase.getInstance().getReference().child(TasksDatabaseAdapter.DB_TASKS).child(taskID);
        userActiveTasksReference = FirebaseDatabase.getInstance().getReference().child(Utilities.Constants.DB_USERS).child(employerID).child(Utilities.Constants.DB_ACTIVE_TASKS);

        startTask();
        setOrientation();

        return view;
    }

    private void stopBroadcasting() {
        currentTaskReference.child("stopDate").setValue(new SimpleDateFormat("dd MM yyyy HH:mm:ss zz", Locale.ENGLISH).format(Calendar.getInstance()
                .getTime()));
        currentTaskReference.child("canTrack").setValue(false);
        activeTasksReference.child(taskID).removeValue();
        userActiveTasksReference.child(taskID).removeValue();
        googleApiClient.disconnect();
    }

    private void setOrientation() {
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void startTask() {
        currentTaskReference.child("startDate").setValue(new SimpleDateFormat("dd MM yyyy HH:mm:ss zz", Locale.ENGLISH).format(Calendar.getInstance()
                .getTime()));
        currentTaskReference.child("canTrack").setValue(true);
        userActiveTasksReference.child(taskID).setValue(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (lastLocation == null) {
            googleApiClient.connect();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this.getActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this.getActivity(),
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    REQ_PERMISSION_LOCATION);

        } else {
            //noinspection MissingPermission
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, EmpTaskTrackingFragment.this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        broadcastTaskDetails(location);
    }

    private void broadcastTaskDetails(Location location) {
        Coordinates coords = new Coordinates(location);
        activeTasksReference.child(taskID).child(Utilities.Constants.GPS_COORDINATES).setValue(coords);
    }

}

