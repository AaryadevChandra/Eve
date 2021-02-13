package com.example.locsmstrial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import im.delight.android.location.SimpleLocation;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;

public class MainActivity extends AppCompatActivity {

    double lat, lng;

    //private SimpleLocation sl;
//double lat, longi;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        sl = new SimpleLocation(this);
//        if(!sl.hasLocationEnabled())
//            SimpleLocation.openSettings(this);
//        sl.beginUpdates();
//    }
//    public void onClick1(View v)
//    {
//        SimpleLocation.Point loc = sl.getPosition();
//        //sl.endUpdates();
//        lat = sl.getLatitude();
//        longi = sl.getLongitude();
//        ((TextView)findViewById(R.id.lati)).setText(String.valueOf(lat));
//        ((TextView)findViewById(R.id.longit)).setText(String.valueOf(longi));
//    }
//    public void onClick2(View v)
//    {
//        Intent i = new Intent(this, SMSActivity.class);
//        i.putExtra("Lat", lat);
//        i.putExtra("Longi", longi);
//        startActivity(i);
//
//    }
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        // make the device update its location
//        sl.beginUpdates();
//
//        // ...
//    }
//    @Override
//    protected void onPause() {
//        // stop location updates (saves battery)
//        sl.endUpdates();
//
//        // ...
//
//        super.onPause();
//    }

    FusedLocationProviderClient fusedLocationProviderClient;


    //location permission check; returns true or false
    public void sosSMS(android.view.View V)
    {
                    
    }

    public boolean checkLocationPermission()
    {

        boolean permissionsFlag = true;
        String[] permissionsArray = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

        for(String permissions : permissionsArray)
        {
            if(ContextCompat.checkSelfPermission(this, permissions) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, permissionsArray, 101);
                break;
            }
        }
        for(String permissions : permissionsArray)
        {
            if(ContextCompat.checkSelfPermission(this, permissions) != PackageManager.PERMISSION_GRANTED)
            {
                permissionsFlag = false;
            }
        }
        return permissionsFlag;
    }


    //checking whether location is enabled; returns true or false
    public boolean isLocationEnabled()
    {
        boolean GPSStatus;
        //getting the current location state
        LocationManager locationManager = (LocationManager)MainActivity.this.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        GPSStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return GPSStatus;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

    }

    //Requesting location updates
    @SuppressLint("MissingPermission")
    public void requestLocationUpdates()
    {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(5).setFastestInterval(0).setNumUpdates(1);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
    }

    //function callback for requestLocationUpdates() function
    private LocationCallback locationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location mLastLocation = locationResult.getLastLocation();
        }
    };

    public void ratingFunction(android.view.View v)
    {
        if(checkLocationPermission())
        {
            if(!isLocationEnabled())
            {
                Intent locationStartIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(locationStartIntent);

                Toast.makeText(this, "Please turn on location services", Toast.LENGTH_LONG).show();
            }
            if(isLocationEnabled())
            {
                fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Location> task)
                    {

                        Location location = task.getResult();

                        lat = location.getLatitude();
                        lng = location.getLongitude();

                        Intent intent = new Intent(MainActivity.this, SMSActivity.class);
                        intent.putExtra("lat", lat);
                        intent.putExtra("lng", lng);
                        startActivity(intent);

                        // do your stuff over here

                    }
                });
            }
        }
    }



}