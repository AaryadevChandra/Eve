package com.example.locationratingiv;


import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.SerializablePermission;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Fragment extends AppCompatActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_main);


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        //allowing networking functions on main thread (to be changed later)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);

        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.map_fragment, supportMapFragment).commit();

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


    public StringBuilder addParameterPlaces(String query, String value)
    {
        StringBuilder sb = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        sb.append("&").append(query).append("=").append(value);

        return sb;
    }
    FusedLocationProviderClient fusedLocationProviderClient;

    //location permission check; returns true or false
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
        LocationManager locationManager = (LocationManager)Fragment.this.getSystemService(Context.LOCATION_SERVICE);
        assert locationManager != null;
        GPSStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        return GPSStatus;
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {

        EditText debug = findViewById(R.id.debugMapsView);



        String startLocationTextQuery = getIntent().getStringExtra("start");
        String destinationTextQuery = getIntent().getStringExtra("end");

//        building the request URL for Directions API
        StringBuilder sbDirectionsRequestURL = new StringBuilder("https://maps.googleapis.com/maps/api/directions/json?");
        String DirectionsRequest_url_formatted = String.format(Locale.US, "origin=%s&destination=%s&key=%s", startLocationTextQuery, destinationTextQuery, getString(R.string.API_KEY));
        sbDirectionsRequestURL.append(DirectionsRequest_url_formatted);
        String DistanceRequestURL = sbDirectionsRequestURL.toString();

        OkHttpClient client = new OkHttpClient();
        Request requestDirections = new Request.Builder().url(DistanceRequestURL).get().build();
        try {
            Response responseDirections = client.newCall(requestDirections).execute();
            JSONObject jsonObjectDirections = new JSONObject(responseDirections.body().string());

            int stepsLength = jsonObjectDirections.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps").length();


            List<LatLng> latLngs;
            double midlat, midlng;

            for(int z=0;z<stepsLength;z++)
            {
                //displays the polyline on the map from start point to destination
                latLngs = PolyUtil.decode(jsonObjectDirections.getJSONArray("routes").getJSONObject(0).getJSONArray("legs")
                        .getJSONObject(0).getJSONArray("steps").getJSONObject(z).getJSONObject("polyline").getString("points"));
                googleMap.addPolyline(new PolylineOptions().addAll(latLngs).width(5).color(Color.BLUE));

                midlat = jsonObjectDirections.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps").getJSONObject(z).getJSONObject("start_location").getDouble("lat");
                midlng = jsonObjectDirections.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps").getJSONObject(z).getJSONObject("start_location").getDouble("lng");

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
                        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                            @Override
                            public void onComplete(@NonNull Task<Location> task) {

                                Location location = task.getResult();

                                //initial search attributes and search utility variables
                                int radius = 2000;
                                String[] types = {"police", "fire_station", "restaurant"};
                                int[] safetyPoints = {5, 4, 3};
                                double totalSafetyPoints = 0;
                                int placeTypeChangeFlag = 0;

                                if(location != null)
                                {
                                    while(placeTypeChangeFlag != types.length)
                                    {
                                        try
                                        {
                                            //Places API URL building
                                            StringBuilder sbPlacesRequestURL =  addParameterPlaces("location", Double.toString(location.getLatitude()) + "," + Double.toString(location.getLongitude()));
                                            String PlacesRequest_url_formatted = String.format(Locale.US, "&radius=%d&type=%s&key=%s", radius, types[placeTypeChangeFlag], getString(R.string.API_KEY));
                                            sbPlacesRequestURL.append(PlacesRequest_url_formatted);
                                            String PlacesRequestURL = sbPlacesRequestURL.toString();

                                            //Places API call
                                            OkHttpClient client = new OkHttpClient();
                                            Request requestPlaces = new Request.Builder().url(PlacesRequestURL).get().build();

                                            Response responsePlaces = client.newCall(requestPlaces).execute();

                                            JSONObject jsonObjectPlaces = new JSONObject(responsePlaces.body().string());
                                            int no_of_results = jsonObjectPlaces.getJSONArray("results").length();


                                            double placeLat;
                                            double placeLng;


                                            //storing the results in an array and getting the Lat Lng of the Places API response json
                                            for(int i=0;i<no_of_results;i++) {
                                                //getting the lat lng for each place
                                                placeLat = jsonObjectPlaces.getJSONArray("results").getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                                                placeLng = jsonObjectPlaces.getJSONArray("results").getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                                                //creating the url for DistanceMatrix API
                                                StringBuilder sbDistanceMatrixRequestURL = new StringBuilder("https://maps.googleapis.com/maps/api/distancematrix/json?");

                                                //building the url based on whether search inputs are selected or user's current location
                                                String DistanceMatrixRequest_url_formatted = null;

                                                DistanceMatrixRequest_url_formatted = String.format(Locale.US, "&origins=%f,%f&destinations=%f,%f&key=%s", midlat, midlng, placeLat, placeLng, getString(R.string.API_KEY));

                                                sbDistanceMatrixRequestURL.append(DistanceMatrixRequest_url_formatted);
                                                String DistanceMatrixRequestURL = sbDistanceMatrixRequestURL.toString();

                                                Request requestDistanceMatrix = new Request.Builder().url(DistanceMatrixRequestURL).get().build();

                                                try {
                                                    //performing the DistanceMatrix API call here to get the distance bw current location and response places
                                                    Response responseDistanceMatrix = client.newCall(requestDistanceMatrix).execute();
                                                    JSONObject jsonObjectDistanceMatrix = new JSONObject(responseDistanceMatrix.body().string());
                                                    int distance = jsonObjectDistanceMatrix.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").getInt("value");

                                                    if (distance < 500) {
                                                        totalSafetyPoints += safetyPoints[placeTypeChangeFlag] * 2.50;
                                                    } else if (distance >= 500 && distance < 1000) {
                                                        totalSafetyPoints += safetyPoints[placeTypeChangeFlag] * 2.25;
                                                    } else if (distance >= 1000 && distance < 1500) {
                                                        totalSafetyPoints += safetyPoints[placeTypeChangeFlag] * 2.00;
                                                    } else if (distance >= 1500 && distance < 2000) {
                                                        totalSafetyPoints += safetyPoints[placeTypeChangeFlag] * 1.75;
                                                    } else if (distance >= 2000 && distance < 2500) {
                                                        totalSafetyPoints += safetyPoints[placeTypeChangeFlag] * 1.50;
                                                    } else if (distance >= 2500 && distance < 3000) {
                                                        totalSafetyPoints += safetyPoints[placeTypeChangeFlag] * 1.25;
                                                    } else {
                                                        totalSafetyPoints += safetyPoints[placeTypeChangeFlag];
                                                    }
                                                }
                                                catch (IOException | JSONException e)
                                                {
                                                    e.printStackTrace();
                                                }
                                            }
                                            if(placeTypeChangeFlag == types.length - 1)
                                            {
                                                debug.setText(Double.toString(totalSafetyPoints));
                                            }
                                            placeTypeChangeFlag++;
                                        }
                                        catch (IOException | JSONException e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                                else if(location == null)
                                {
                                    requestLocationUpdates();
                                    startActivity(new Intent(getApplicationContext(), Fragment.class));
                                }
                            }
                        });
                    }
                }

            }
        }
        catch (JSONException | IOException  e)
        {
            e.printStackTrace();
        }
    }
}
