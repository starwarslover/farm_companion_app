package com.licence.serban.farmcompanion.emp_account.fragments;


import android.Manifest;
import android.content.Context;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.employees.models.Employee;
import com.licence.serban.farmcompanion.interfaces.OnFragmentStart;
import com.licence.serban.farmcompanion.misc.Utilities;
import com.licence.serban.farmcompanion.misc.models.Coordinates;
import com.licence.serban.farmcompanion.tasks.adapters.TasksDatabaseAdapter;
import com.licence.serban.farmcompanion.tasks.models.Task;

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
  private OnFragmentStart startFragment;
  private String employeeID;
  private long startTime;
  private Task currentTask;
  private Employee employee;

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

    employeeID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    stopTaskButton = (Button) view.findViewById(R.id.empTrackStopBroadcastButton);
    stopTaskButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startFragment.popBackStack();
      }
    });

    activeTasksReference = FirebaseDatabase.getInstance().getReference().child(Utilities.Constants.DB_ACTIVE_TASKS).child(employerID).child(taskID);
    currentTaskReference = FirebaseDatabase.getInstance().getReference().child(TasksDatabaseAdapter.DB_TASKS).child(employerID).child(taskID);
    getInformation();

    setUpLocationApi();
    setOrientation();
    return view;
  }

  private void getInformation() {
    currentTaskReference.addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        currentTask = dataSnapshot.getValue(Task.class);
        startTask();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });
    FirebaseDatabase.getInstance().getReference().child(Utilities.Constants.DB_EMPLOYEES).child(employerID).child(employeeID).addListenerForSingleValueEvent(new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        employee = dataSnapshot.getValue(Employee.class);
        activeTasksReference.child(employeeID).child("emp_name").setValue(employee.getName());

      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    });
  }


  private void setUpLocationApi() {
    googleApiClient = new GoogleApiClient.Builder(this.getActivity())
            .addApi(LocationServices.API)
            .addConnectionCallbacks(this)
            .build();

    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(5000);
    mLocationRequest.setSmallestDisplacement(10);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
  }

  private void stopBroadcasting() {
    activeTasksReference.child(employeeID).removeValue();
    currentTask.stopTask();
    googleApiClient.disconnect();
  }

  private void setOrientation() {
    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
  }

  private void startTask() {
    startTime = System.currentTimeMillis();
    activeTasksReference.child(employeeID).child(Utilities.Constants.DB_START_DATE).setValue(startTime);
    currentTask.startTask(true);
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    stopBroadcasting();
  }

  @Override
  public void onStart() {
    super.onStart();

    if (lastLocation == null) {
      googleApiClient.connect();
    }
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    try {
      startFragment = (OnFragmentStart) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString()
              + " must implement OnFragmentStart");
    }
  }

  @Override
  public void onStop() {
    super.onStop();
    googleApiClient.disconnect();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
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
    activeTasksReference.child(employeeID).child(Utilities.Constants.GPS_COORDINATES).setValue(coords);
    activeTasksReference.child(employeeID).child(Utilities.Constants.DB_SPEED).setValue(location.getSpeed());
    long elapsedTime = System.currentTimeMillis() - startTime;
    activeTasksReference.child(employeeID).child(Utilities.Constants.DB_ELAPSED_TIME).setValue(elapsedTime);
  }

}

