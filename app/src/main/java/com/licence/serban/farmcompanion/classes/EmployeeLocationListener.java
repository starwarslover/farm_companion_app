package com.licence.serban.farmcompanion.classes;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by Serban on 05.04.2017.
 */

public class EmployeeLocationListener implements LocationListener {
    private DatabaseReference mainReference;
    private String employeeID;
    private DatabaseReference userActiveTasksRef;
    private DatabaseReference activeTasksRef;


    public EmployeeLocationListener(DatabaseReference mainReference, String employeeID) {
        this.mainReference = mainReference;
        this.employeeID = employeeID;
        activeTasksRef = mainReference.child(Utilities.Constants.DB_ACTIVE_TASKS);

    }

    @Override
    public void onLocationChanged(Location location) {
        activeTasksRef.child(employeeID).child(Utilities.Constants.DB_LIVE_TRACKING).child("latitude").setValue(location.getLatitude());
        activeTasksRef.child(employeeID).child(Utilities.Constants.DB_LIVE_TRACKING).child("longitude").setValue(location.getLongitude());
        Log.e("Location: ", location.getLatitude() + " " + location.getLongitude());

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
