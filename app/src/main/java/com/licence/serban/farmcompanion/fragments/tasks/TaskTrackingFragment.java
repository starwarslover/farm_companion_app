package com.licence.serban.farmcompanion.fragments.tasks;


import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.classes.StringDateFormatter;
import com.licence.serban.farmcompanion.classes.Utilities;
import com.licence.serban.farmcompanion.classes.models.Coordinates;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
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
    private String taskID;

    private HashMap<String, Marker> mapMarkers;
    private String selectedEmployee;

    private TaskDetailsLayoutHolder holder;

    public TaskTrackingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_task_tracking, container, false);

        mapMarkers = new HashMap<>();
        empsCircles = new HashMap<>();

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
//                    LatLng sydney = new LatLng(-34, 151);
//                    myGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));
//
//                    // For zooming automatically to the location of the marker
//                    CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
//                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    myGoogleMap.setOnCircleClickListener(new GoogleMap.OnCircleClickListener() {
                        @Override
                        public void onCircleClick(Circle circle) {
                            selectedCircle = circle;
                            selectedEmployee = getHashKeyValue(circle);
                            holder.showDetails();
                        }
                    });

                    myGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                        @Override
                        public void onMapClick(LatLng latLng) {
                            selectedCircle = null;
                            selectedEmployee = null;
                            holder.hideDetails();
                        }
                    });

                    renderTasks();
                }
            }
        });

        return view;
    }

    private String getHashKeyValue(Circle circle) {
        for (String key : empsCircles.keySet()) {
            if (empsCircles.get(key).equals(circle)) {
                return key;
            }
        }
        return null;
    }

    private void renderTasks() {

        tasksReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Coordinates coordinates = snapshot.child(Utilities.Constants.GPS_COORDINATES).getValue(Coordinates.class);
                    if (coordinates != null && coordinates.toLatLng() != null) {
                        LatLng latLng = coordinates.toLatLng();
//                Marker marker = myGoogleMap.addMarker(new MarkerOptions().position(latLng));
//                mapMarkers.put(dataSnapshot.getKey(), marker);

                        Circle circle = myGoogleMap.addCircle(new CircleOptions().center(latLng).radius(150).fillColor(Color.BLACK));
                        circle.setClickable(true);
                        empsCircles.put(dataSnapshot.getKey(), circle);
                        if (taskID != null && taskID.equals(dataSnapshot.getKey())) {
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
                            myGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                            selectedCircle = circle;
                            selectedEmployee = getHashKeyValue(circle);
                            holder.showDetails();
                        }

                        if (taskID == null && !cameraPositioned) {
                            cameraPositioned = true;
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(9).build();
                            myGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }
//                myGoogleMap.addMarker(new MarkerOptions().position(latLng).title(dataSnapshot.getKey()));

                    }

                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Coordinates coordinates = snapshot.child(Utilities.Constants.GPS_COORDINATES).getValue(Coordinates.class);
                    if (coordinates != null && coordinates.toLatLng() != null) {
                        LatLng latLng = coordinates.toLatLng();
//                Marker marker = mapMarkers.get(dataSnapshot.getKey());
//                marker.setPosition(latLng);
                        Circle circle = empsCircles.get(dataSnapshot.getKey());
                        if (circle != null) {
                            circle.setCenter(latLng);
                        }
                        if (selectedEmployee != null && selectedEmployee.equals(dataSnapshot.getKey())) {
                            holder.setSpeed(dataSnapshot.child(Utilities.Constants.DB_SPEED).getValue(String.class));
                            try {
                                String date = snapshot.child(Utilities.Constants.DB_START_DATE).getValue(String.class);
                                holder.setStartedAt(date);
                            } catch (Exception ex) {

                            }
                            long time = snapshot.child(Utilities.Constants.DB_ELAPSED_TIME).getValue(Long.class);
                            holder.setTotalWorkTime(time);
                            holder.setEmpName(snapshot.child("emp_name").getValue(String.class));
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
                            myGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }

                    }

                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
//                Marker marker = mapMarkers.get(dataSnapshot.getKey());
//                marker.remove();
//                mapMarkers.remove(dataSnapshot.getKey());
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
        });
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
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private class TaskDetailsLayoutHolder {
        private TextView speedTextView;
        private LinearLayout parentLayout;
        private TextView startedAtTextView;
        private TextView totalWorkTimeTextView;
        private TextView empNameTextView;

        public TaskDetailsLayoutHolder(View view) {
            this.speedTextView = (TextView) view.findViewById(R.id.taskTrackingInfoSpeedTextView);
            this.parentLayout = (LinearLayout) view.findViewById(R.id.taskTrackingInfoLayout);
            this.startedAtTextView = (TextView) view.findViewById(R.id.taskTrackingEmpStartedAtTextView);
            this.totalWorkTimeTextView = (TextView) view.findViewById(R.id.taskTrackingElapsedTimeTextView);
            this.empNameTextView = (TextView) view.findViewById(R.id.taskTrackingEmpNameTextView);
        }

        public void setSpeed(String speed) {
            this.speedTextView.setText(speed);
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


        public void setStartedAt(String date) {
            try {
                Date dateToFormat = new SimpleDateFormat("dd MM yyyy HH:mm:ss zz", Locale.ENGLISH).parse(date);
                String formatedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(dateToFormat);
                String formatedHour = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH).format(dateToFormat);
                String formatedText = formatedDate + "\n" + formatedHour;
                startedAtTextView.setText(formatedText);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void setEmpName(String name) {
            this.empNameTextView.setText(name);
        }
    }
}
