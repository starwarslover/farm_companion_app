package com.licence.serban.farmcompanion.fragments.tasks;


import android.Manifest;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.licence.serban.farmcompanion.R;
import com.licence.serban.farmcompanion.classes.Utilities;
import com.licence.serban.farmcompanion.classes.adapters.TasksDatabaseAdapter;
import com.licence.serban.farmcompanion.classes.models.Coordinates;

import java.util.HashMap;

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

    private HashMap<String, Circle> empsCircles;
    private String taskID;

    private HashMap<String, Marker> mapMarkers;

    public TaskTrackingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_task_tracking, container, false);

        mapMarkers = new HashMap<>();
        empsCircles = new HashMap<>();

        mapView = (MapView) view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

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
                    googleMap.setMyLocationEnabled(true);
//                    LatLng sydney = new LatLng(-34, 151);
//                    myGoogleMap.addMarker(new MarkerOptions().position(sydney).title("Marker Title").snippet("Marker Description"));
//
//                    // For zooming automatically to the location of the marker
//                    CameraPosition cameraPosition = new CameraPosition.Builder().target(sydney).zoom(12).build();
//                    googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    renderTasks();
                }
            }
        });

        return view;
    }

    private void renderTasks() {

        tasksReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Coordinates coordinates = dataSnapshot.child(Utilities.Constants.GPS_COORDINATES).getValue(Coordinates.class);
                LatLng latLng = coordinates.toLatLng();
                Marker marker = myGoogleMap.addMarker(new MarkerOptions().position(latLng));
                mapMarkers.put(dataSnapshot.getKey(), marker);

                Circle circle = myGoogleMap.addCircle(new CircleOptions().center(latLng).radius(100));
                empsCircles.put(dataSnapshot.getKey(), circle);
                if (taskID != null && taskID.equals(dataSnapshot.getKey())) {
                    CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).build();
                    myGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                }
//                myGoogleMap.addMarker(new MarkerOptions().position(latLng).title(dataSnapshot.getKey()));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Coordinates coordinates = dataSnapshot.child(Utilities.Constants.GPS_COORDINATES).getValue(Coordinates.class);
                LatLng latLng = coordinates.toLatLng();
                Marker marker = mapMarkers.get(dataSnapshot.getKey());
                marker.setPosition(latLng);
                Circle circle = empsCircles.get(dataSnapshot.getKey());
                circle.setCenter(latLng);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Marker marker = mapMarkers.get(dataSnapshot.getKey());
                marker.remove();
                mapMarkers.remove(dataSnapshot.getKey());
                Circle circle = empsCircles.get(dataSnapshot.getKey());
                circle.remove();
                mapMarkers.remove(dataSnapshot.getKey());
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
}
