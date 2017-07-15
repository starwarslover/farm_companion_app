package com.licence.serban.farmcompanion.emp_account.fragments;


import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.employees.models.Employee;
import com.licence.serban.farmcompanion.interfaces.OnFragmentStart;
import com.licence.serban.farmcompanion.misc.ActivityRecognizedService;
import com.licence.serban.farmcompanion.misc.StringDateFormatter;
import com.licence.serban.farmcompanion.misc.Utilities;
import com.licence.serban.farmcompanion.misc.models.Coordinates;
import com.licence.serban.farmcompanion.tasks.adapters.TasksDatabaseAdapter;
import com.licence.serban.farmcompanion.tasks.models.Task;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * A simple {@link Fragment} subclass.
 */
public class EmpTaskTrackingFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks, LocationListener, ResultCallback<Status> {


  private static final int REQ_PERMISSION_LOCATION = 25;
  public static final String BROADCAST_ACTION = "BROADCAST_ACTION";
  private static final String TOTAL_STOP_TIME = "timeStopped";
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
  private ValueEventListener taskListener;
  private DatabaseReference empReference;
  private ValueEventListener empListener;
  private TextView taskTrackTaskName;
  private TextView startedAtTextView;
  private TextView travelSpeedTextView;
  private TextView totalTimeTextView;
  private TextView totalDistanceTextView;
  private Timer timer;
  private ArrayList<LatLng> coordsSet;
  private Context context;
  private long time = 0;
  private PendingIntent pendingIntent;
  private TextView statusTextView;
  private ActivityDetectionBroadcastReceiver detectionBroadcastReceiver;
  private DetectedActivity lastRecordedActivity;
  private long movingStartTime;
  private long stillStartTime;
  private boolean hasStopped;
  private long totalStopTime = 0;

  public EmpTaskTrackingFragment() {
    // Required empty public constructor
  }


  @Override
  public void onStart() {
    super.onStart();
    if (lastLocation == null) {
      googleApiClient.connect();
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    LocalBroadcastManager.getInstance(context).registerReceiver(detectionBroadcastReceiver, new IntentFilter(BROADCAST_ACTION));
  }

  @Override
  public void onPause() {
    super.onPause();
    LocalBroadcastManager.getInstance(context).unregisterReceiver(detectionBroadcastReceiver);
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
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
//    googleApiClient.disconnect();
  }

  @Override
  public void onDestroyView() {
    super.onDestroyView();
//    getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    stopBroadcasting();
    this.currentTaskReference.removeEventListener(taskListener);
    this.empReference.removeEventListener(empListener);
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
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

    detectionBroadcastReceiver = new ActivityDetectionBroadcastReceiver();

    coordsSet = new ArrayList<>();

    employeeID = FirebaseAuth.getInstance().getCurrentUser().getUid();

    setViews(view);
    setDbRefs();
    getInformation();

    setUpLocationApi();
//    setOrientation();
    return view;
  }

  private void setDbRefs() {
    activeTasksReference = FirebaseDatabase.getInstance().getReference().child(Utilities.Constants.DB_ACTIVE_TASKS).child(employerID).child(taskID);
    currentTaskReference = FirebaseDatabase.getInstance().getReference().child(TasksDatabaseAdapter.DB_TASKS).child(employerID).child(taskID);
    empReference = FirebaseDatabase.getInstance().getReference().child(Utilities.Constants.DB_EMPLOYEES).child(employerID).child(employeeID);
  }

  private void setViews(View view) {
    taskTrackTaskName = (TextView) view.findViewById(R.id.taskTrackTaskName);
    startedAtTextView = (TextView) view.findViewById(R.id.startedAtTextView);
    travelSpeedTextView = (TextView) view.findViewById(R.id.travelSpeedTextView);
    totalTimeTextView = (TextView) view.findViewById(R.id.totalTimeTextView);
    totalDistanceTextView = (TextView) view.findViewById(R.id.totalDistanceTextView);
    stopTaskButton = (Button) view.findViewById(R.id.empTrackStopBroadcastButton);
    stopTaskButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        startFragment.popBackStack();
      }
    });
    statusTextView = (TextView) view.findViewById(R.id.statusTextView);

  }

  private void getInformation() {
    taskListener = new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        currentTask = dataSnapshot.getValue(Task.class);
        if (currentTask != null)
          startTask();
      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    };
    currentTaskReference.addListenerForSingleValueEvent(taskListener);
    empListener = new ValueEventListener() {
      @Override
      public void onDataChange(DataSnapshot dataSnapshot) {
        employee = dataSnapshot.getValue(Employee.class);
        activeTasksReference.child(employeeID).child("emp_name").setValue(employee.getName());

      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    };
    empReference.addListenerForSingleValueEvent(empListener);
  }

  private void setUpLocationApi() {
    googleApiClient = new GoogleApiClient.Builder(context)
            .addApi(LocationServices.API)
            .addApi(ActivityRecognition.API)
            .addConnectionCallbacks(this)
            .build();

    mLocationRequest = new LocationRequest();
    mLocationRequest.setInterval(5000);
    mLocationRequest.setSmallestDisplacement(10);
    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
  }

  public Handler mhandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      updateUi();
      updateTaskTime();
    }
  };

  private void updateTaskTime() {
    if (hasStopped) {
      this.totalStopTime += 1000;
      activeTasksReference.child(employeeID).child(TOTAL_STOP_TIME).setValue(this.totalStopTime);
    }
  }

  private void stopBroadcasting() {
    activeTasksReference.child(employeeID).removeValue();
    if (currentTask != null) {
      long totalTime = System.currentTimeMillis() - this.startTime;
      currentTask.setTimeStopped(currentTask.getTimeStopped() + this.totalStopTime);
      currentTask.setTotalTime(currentTask.getTotalTime() + totalTime);
      currentTask.stopTask();
    }

    ActivityRecognition.ActivityRecognitionApi.removeActivityUpdates(googleApiClient, pendingIntent);

    googleApiClient.disconnect();
    timer.purge();
    timer.cancel();
  }

  private void startTask() {
    startTime = System.currentTimeMillis();
    activeTasksReference.child(employeeID).child(Utilities.Constants.DB_START_DATE).setValue(startTime);
    if (currentTask != null) {
      currentTask.startTask(true);
    }
    this.startedAtTextView.setText(StringDateFormatter.milisToString(startTime, "dd.MM.yyyy HH:mm"));
    time = 0;
    timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        mhandler.obtainMessage(1).sendToTarget();
      }
    }, 0, 1000);
  }

  @Override
  public void onConnected(@Nullable Bundle bundle) {
    if (ActivityCompat.checkSelfPermission(context,
            Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

      ActivityCompat.requestPermissions(this.getActivity(),
              new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
              REQ_PERMISSION_LOCATION);


    } else {
      //noinspection MissingPermission
      LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, EmpTaskTrackingFragment.this);

      Intent intent = new Intent(context, ActivityRecognizedService.class);
      pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
      ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(this.googleApiClient, 2000, pendingIntent).setResultCallback(this);
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

    this.lastLocation = location;
    this.coordsSet.add(new LatLng(location.getLatitude(), location.getLongitude()));
  }

  private void updateUi() {
    if (this.lastLocation != null) {
      double speedVal = this.lastLocation.getSpeed();
      double kmHSpeed = speedVal * 3.6;
      String speed = new DecimalFormat("#0.0").format(kmHSpeed) + " km/h";
      travelSpeedTextView.setText(speed);
    }

    long elapsedTime = System.currentTimeMillis() - startTime;
    int hrs = (int) (MILLISECONDS.toHours(elapsedTime) % 24);
    int min = (int) (MILLISECONDS.toMinutes(elapsedTime) % 60);
    int sec = (int) (MILLISECONDS.toSeconds(elapsedTime) % 60);

    totalTimeTextView.setText(String.format("%02d:%02d:%02d", hrs, min, sec));

    double distance = SphericalUtil.computeLength(this.coordsSet);
    String distanceString = new DecimalFormat("#0.0").format(distance) + " m";
    totalDistanceTextView.setText(distanceString);
  }

  @Override
  public void onResult(@NonNull Status status) {

  }

  private class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
      ActivityRecognizedService.DetectedActivityWrapper detected = (ActivityRecognizedService.DetectedActivityWrapper) intent.getSerializableExtra(ActivityRecognizedService.ACTIVITY_RESULT);
      if (detected != null && detected.detectedActivity != null)
        handleDetectedActivity(detected.detectedActivity);
    }
  }

  private void handleDetectedActivity(DetectedActivity detectedActivity) {
    if (detectedActivity.getConfidence() > 75 && (isMoving(detectedActivity) || isStill
            (detectedActivity))) {
      if (isMoving(detectedActivity)) {
        statusTextView.setText(context.getResources().getString(R.string.on_move));
        onDetectedActivityChanged(detectedActivity);
      }
      if (isStill(detectedActivity)) {
        statusTextView.setText(context.getResources().getString(R.string.still));
        onDetectedActivityChanged(detectedActivity);
      }

    }
  }

  private void onDetectedActivityChanged(DetectedActivity detectedActivity) {
    if (this.lastRecordedActivity != null) {
      if (isMoving(lastRecordedActivity) && isStill(detectedActivity)) {
        this.stillStartTime = System.currentTimeMillis();
        this.hasStopped = true;
      }

      if (isStill(lastRecordedActivity) && isMoving(detectedActivity)) {
        this.hasStopped = false;
      }
    } else {
      if (isStill(detectedActivity)) {
        this.stillStartTime = System.currentTimeMillis();
        this.hasStopped = true;
      }

      if (isMoving(detectedActivity)) {
        this.hasStopped = false;
      }
    }
  }

  private boolean isStill(DetectedActivity detectedActivity) {
    return detectedActivity.getType() == DetectedActivity.STILL;
  }

  private boolean isMoving(DetectedActivity detectedActivity) {
    return detectedActivity.getType() == DetectedActivity.IN_VEHICLE ||
            detectedActivity.getType() == DetectedActivity.WALKING ||
            detectedActivity.getType() == DetectedActivity.ON_FOOT ||
            detectedActivity.getType() == DetectedActivity.RUNNING;
  }


}
