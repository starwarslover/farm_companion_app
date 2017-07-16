package com.licence.serban.farmcompanion.tasks.fragments;


import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.interfaces.OnDrawerMenuLock;
import com.licence.serban.farmcompanion.misc.StringDateFormatter;
import com.licence.serban.farmcompanion.misc.Utilities;
import com.licence.serban.farmcompanion.misc.models.Coordinates;
import com.licence.serban.farmcompanion.tasks.models.ActiveTaskWrapper;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * A simple {@link Fragment} subclass.
 */
public class TaskTrackingFragment extends Fragment {
  private MapView mapView;
  private GoogleMap myGoogleMap;

  private DatabaseReference mainReference;
  private DatabaseReference userActiveTasksReference;
  private DatabaseReference tasksReference;
  private String userID;
  private Circle selectedCircle;

  private boolean cameraPositioned = false;

  private HashMap<String, Circle> empsCircles;
  private HashMap<String, Long> startTimes;
  private String taskID;

  private String selectedEmployee;

  private TaskDetailsLayoutHolder holder;
  private OnDrawerMenuLock drawerMenuLock;
  private ChildEventListener empsListener;
  private Context context;
  private Timer timer;
  private long elapsedTime;

  public TaskTrackingFragment() {
    // Required empty public constructor
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    this.context = context;
    try {
      drawerMenuLock = (OnDrawerMenuLock) context;
    } catch (ClassCastException ex) {
      throw new ClassCastException(context.toString()
              + " must implement OnDrawerMenuLock");
    }
  }

  @Override
  public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    final View view = inflater.inflate(R.layout.fragment_task_tracking, container, false);

    drawerMenuLock.lockDrawer();
    empsCircles = new HashMap<>();
    startTimes = new HashMap<>();

    mapView = (MapView) view.findViewById(R.id.mapView);
    mapView.onCreate(savedInstanceState);
    holder = new TaskDetailsLayoutHolder(view);

    mapView.onResume();

    Bundle args = getArguments();
    if (args != null) {
      userID = args.getString(Utilities.Constants.USER_ID);
      taskID = args.getString(Utilities.Constants.TASK_ID_EXTRA);
    }

    mainReference = FirebaseDatabase.getInstance().getReference();
    tasksReference = mainReference.child(Utilities.Constants.DB_ACTIVE_TASKS).child(userID);
    try {
      MapsInitializer.initialize(getActivity().getApplicationContext());
    } catch (Exception e) {
      e.printStackTrace();
    }

    mapView.getMapAsync(new OnMapReadyCallback() {
      @Override
      public void onMapReady(GoogleMap googleMap) {
        myGoogleMap = googleMap;
        int permissionCheck = ContextCompat.checkSelfPermission(TaskTrackingFragment.this.getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionCheck == PermissionChecker.PERMISSION_GRANTED) {

          myGoogleMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
            @Override
            public void onCircleClick(Circle circle) {
              selectedCircle = circle;
              selectedEmployee = getHashKeyValue(circle);
              elapsedTime = startTimes.get(selectedEmployee);
              startTimer();
              holder.showDetails();
            }
          });

          myGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
              selectedCircle = null;
              selectedEmployee = null;
              elapsedTime = 0;
              timer.purge();
              timer.cancel();
              holder.hideDetails();
            }
          });

          renderTasks();
        }
      }
    });

    return view;
  }

  private void startTimer() {
    timer = new Timer();
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        mhandler.obtainMessage(1).sendToTarget();
      }
    }, 0, 1000);
  }

  @Nullable
  private String getHashKeyValue(Circle circle) {
    for (String key : empsCircles.keySet()) {
      if (empsCircles.get(key).equals(circle)) {
        return key;
      }
    }
    return null;
  }

  private void renderTasks() {

    this.empsListener = new ChildEventListener() {
      @Override
      public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          ActiveTaskWrapper taskWrapper = snapshot.getValue(ActiveTaskWrapper.class);
          Coordinates coordinates = taskWrapper.getLive_coordinates();
          if (coordinates != null && coordinates.toLatLng() != null) {
            LatLng latLng = coordinates.toLatLng();

            Circle circle = myGoogleMap.addCircle(new CircleOptions().center(latLng).radius(350).fillColor(Color.BLACK));
            circle.setClickable(true);
            empsCircles.put(dataSnapshot.getKey(), circle);
            startTimes.put(dataSnapshot.getKey(), taskWrapper.getStartDate());
            if (taskID != null && taskID.equals(dataSnapshot.getKey())) {
              CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
              myGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
              selectedCircle = circle;
              elapsedTime = taskWrapper.getStartDate();
              startTimer();
              selectedEmployee = getHashKeyValue(circle);
              holder.showDetails();
            }
            if (taskID == null && !cameraPositioned) {
              cameraPositioned = true;
              CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(9).build();
              myGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
          }

        }
      }

      @Override
      public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
          ActiveTaskWrapper taskWrapper = snapshot.getValue(ActiveTaskWrapper.class);
          Coordinates coordinates = taskWrapper.getLive_coordinates();
          if (coordinates != null && coordinates.toLatLng() != null) {
            LatLng latLng = coordinates.toLatLng();
            Circle circle = empsCircles.get(dataSnapshot.getKey());
            if (circle != null) {
              circle.setCenter(latLng);
            }
            if (selectedEmployee != null && selectedEmployee.equals(dataSnapshot.getKey())) {
              holder.setSpeed(taskWrapper.getSpeed());
              holder.setStartedAt(taskWrapper.getStartDate());
              holder.setEmpName(taskWrapper.getEmp_name());
              Integer activity = taskWrapper.getCurrentState();

              if (activity != null) {
                if (isMoving(activity)) {
                  holder.taskTrackingInfoStateTextView.setText(context.getResources().getString(R.string.on_move));
                }
                if (isStill(activity)) {
                  holder.taskTrackingInfoStateTextView.setText(context.getResources().getString(R.string.still));
                }
              }

              holder.taskTrackingInfoStopTimeTextView.setText(StringDateFormatter.millisToTime(taskWrapper.getTimeStopped()));
              holder.setDistance(taskWrapper.getTravelDistance());
              CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
              myGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

          }

        }
      }

      @Override
      public void onChildRemoved(DataSnapshot dataSnapshot) {
        Circle circle = empsCircles.get(dataSnapshot.getKey());
        if (circle != null) {
          circle.remove();
        }
        selectedCircle = null;
        selectedEmployee = null;
        holder.hideDetails();
        empsCircles.remove(dataSnapshot.getKey());
      }

      @Override
      public void onChildMoved(DataSnapshot dataSnapshot, String s) {

      }

      @Override
      public void onCancelled(DatabaseError databaseError) {

      }
    };
    tasksReference.addChildEventListener(this.empsListener);
  }

  @Override
  public void onResume() {
    super.onResume();
    mapView.onResume();
  }

  @Override
  public void onPause() {
    super.onPause();
    mapView.onPause();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mapView.onDestroy();
    tasksReference.removeEventListener(this.empsListener);
  }

  @Override
  public void onDetach() {
    super.onDetach();
    drawerMenuLock.unlockDrawerMenu();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }

  public Handler mhandler = new Handler() {
    @Override
    public void handleMessage(Message msg) {
      if (selectedEmployee != null) {
        long time = System.currentTimeMillis() - elapsedTime;
        holder.setElapsedTime(time);
      }
    }
  };

  private class TaskDetailsLayoutHolder {
    private TextView speedTextView;
    private LinearLayout parentLayout;
    private TextView startedAtTextView;
    private TextView totalWorkTimeTextView;
    private TextView empNameTextView;
    public TextView taskTrackingInfoDistanceTextView;
    public TextView taskTrackingInfoStateTextView;
    public TextView taskTrackingInfoStopTimeTextView;

    public TaskDetailsLayoutHolder(View view) {
      this.speedTextView = (TextView) view.findViewById(R.id.taskTrackingInfoSpeedTextView);
      this.parentLayout = (LinearLayout) view.findViewById(R.id.taskTrackingInfoLayout);
      this.startedAtTextView = (TextView) view.findViewById(R.id.taskTrackingEmpStartedAtTextView);
      this.totalWorkTimeTextView = (TextView) view.findViewById(R.id.taskTrackingElapsedTimeTextView);
      this.empNameTextView = (TextView) view.findViewById(R.id.taskTrackingEmpNameTextView);
      this.taskTrackingInfoDistanceTextView = (TextView) view.findViewById(R.id.taskTrackingInfoDistanceTextView);
      this.taskTrackingInfoStateTextView = (TextView) view.findViewById(R.id.taskTrackingInfoStateTextView);
      this.taskTrackingInfoStopTimeTextView = (TextView) view.findViewById(R.id.taskTrackingInfoStopTimeTextView);
    }

    public void setSpeed(Double speed) {
      if (speed == null)
        speed = 0d;
      double kmHSpeed = speed * 3.6;
      String speedStr = new DecimalFormat("#0.0").format(kmHSpeed) + " km/h";
      this.speedTextView.setText(speedStr);
    }

    public void showDetails() {
      this.parentLayout.setVisibility(View.VISIBLE);
    }

    public void hideDetails() {
      this.parentLayout.setVisibility(View.GONE);
    }

    public void setTotalWorkTime(long mills) {
      try {
        String time = String.format("%02d:%02d:%02d",
                TimeUnit.MILLISECONDS.toHours(mills),
                TimeUnit.MILLISECONDS.toMinutes(mills) -
                        TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(mills)),
                TimeUnit.MILLISECONDS.toSeconds(mills) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mills)));
        this.totalWorkTimeTextView.setText(time);

      } catch (Exception e) {

      }
    }


    public void setStartedAt(Long date) {
      try {
        String formatedDate = StringDateFormatter.millisToString(date, "dd.MM.YYYY HH:mm");
        startedAtTextView.setText(formatedDate);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    public void setEmpName(String name) {
      this.empNameTextView.setText(name);
    }

    public void setDistance(double distance) {
      String distanceString = new DecimalFormat("#0.0").format(distance) + " m";
      taskTrackingInfoDistanceTextView.setText(distanceString);
    }

    public void setElapsedTime(long elapsedTime) {
      String timeText = StringDateFormatter.millisToTime(elapsedTime);
      totalWorkTimeTextView.setText(timeText);
    }
  }

  public static boolean isStill(int detectedActivity) {
    return detectedActivity == DetectedActivity.STILL;
  }

  public static boolean isMoving(int detectedActivity) {
    return detectedActivity == DetectedActivity.IN_VEHICLE ||
            detectedActivity == DetectedActivity.WALKING ||
            detectedActivity == DetectedActivity.ON_FOOT ||
            detectedActivity == DetectedActivity.RUNNING;
  }
}
