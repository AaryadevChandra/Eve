package com.example.locationratingiv;

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
import android.os.StrictMode;
import android.provider.Settings;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    Executor ex1, ex2;

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


        //allowing networking functions on main thread (to be changed later)
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


    }

    @Override
    protected void onStart() {
        super.onStart();
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

    public void buttonClick(android.view.View v)
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


                                    EditText textView = findViewById(R.id.textView);

                                    //storing the results in an array and getting the Lat Lng of the Places API response json
                                    for(int i=0;i<no_of_results;i++)
                                    {
                                        //getting the lat lng for each place
                                        Double placeLat = jsonObjectPlaces.getJSONArray("results").getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                                        Double placeLng = jsonObjectPlaces.getJSONArray("results").getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng");

                                        //creating the url for DistanceMatrix API
                                        StringBuilder sbDistanceMatrixRequestURL = new StringBuilder("https://maps.googleapis.com/maps/api/distancematrix/json?");
                                        String DistanceMatrixRequest_url_formatted = String.format(Locale.US, "&origins=%f,%f&destinations=%f,%f&key=%s", location.getLatitude(), location.getLongitude(), placeLat, placeLng, getString(R.string.API_KEY));
                                        sbDistanceMatrixRequestURL.append(DistanceMatrixRequest_url_formatted);
                                        String DistanceMatrixRequestURL = sbDistanceMatrixRequestURL.toString();

                                        Request requestDistanceMatrix = new Request.Builder().url(DistanceMatrixRequestURL).get().build();

                                        try
                                        {
                                            //performing the DistanceMatrix API call here to get the distance bw current location and response places
                                            Response responseDistanceMatrix = client.newCall(requestDistanceMatrix).execute();
                                            JSONObject jsonObjectDistanceMatrix = new JSONObject(responseDistanceMatrix.body().string());
                                            int distance = jsonObjectDistanceMatrix.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0).getJSONObject("distance").getInt("value");

                                            if(distance < 500)
                                            {
                                                totalSafetyPoints += safetyPoints[placeTypeChangeFlag] * 2.50;
                                            }
                                            else if(distance >= 500 && distance < 1000)
                                            {
                                                totalSafetyPoints += safetyPoints[placeTypeChangeFlag] * 2.25;
                                            }
                                            else if(distance >= 1000 && distance < 1500)
                                            {
                                                totalSafetyPoints += safetyPoints[placeTypeChangeFlag] * 2.00;
                                            }
                                            else if(distance >= 1500 && distance < 2000)
                                            {
                                                totalSafetyPoints += safetyPoints[placeTypeChangeFlag] * 1.75;
                                            }
                                            else if(distance >= 2000 && distance < 2500)
                                            {
                                                totalSafetyPoints += safetyPoints[placeTypeChangeFlag] * 1.50;
                                            }
                                            else if(distance >= 2500 && distance < 3000)
                                            {
                                                totalSafetyPoints += safetyPoints[placeTypeChangeFlag] * 1.25;
                                            }
                                            else
                                            {
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
                                        EditText pointsTextView = findViewById(R.id.pointsTextView);
                                        pointsTextView.setText(Double.toString(totalSafetyPoints));

                                        Intent intent = new Intent(getBaseContext(), Fragment.class);
                                        intent.putExtra("lat", location.getLatitude());
                                        intent.putExtra("lng", location.getLongitude());
                                        startActivity(intent);
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
                            buttonClick(v);
                        }
                    }
                });
            }
        }
    }
    public void mapsButton(android.view.View v)
    {
        Intent switchActivitiesIntent = new Intent(this, Fragment.class);
        startActivity(switchActivitiesIntent);
    }
}