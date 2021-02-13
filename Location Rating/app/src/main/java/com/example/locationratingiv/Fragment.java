package com.example.locationratingiv;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.PolyUtil;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.SerializablePermission;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Fragment extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.maps_main);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_fragment);

//        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(this);

        getSupportFragmentManager().beginTransaction().replace(R.id.map_fragment, supportMapFragment).commit();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        double totalSafetyPoints = 0;
        double ratingForRoute = 0;

        EditText debugMapsView = findViewById(R.id.debugMapsView);
        debugMapsView.append("again");

//        double mapLatitude = getIntent().getDoubleExtra("lat", 0);
//        double mapLongitude= getIntent().getDoubleExtra("lng", 0);

        String startLocationTextQuery = getIntent().getStringExtra("startLocation");
        String destinationTextQuery = getIntent().getStringExtra("destination");


        googleMap.setOnMapClickListener(this);




//        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
//                .clickable(true)
//                .add(
//                        new LatLng(-35.016, 143.321),
//                        new LatLng(-34.747, 145.592),
//                        new LatLng(-34.364, 147.891),
//                        new LatLng(-33.501, 150.217),
//                        new LatLng(-32.306, 149.248),
//                        new LatLng(-32.491, 147.309)));

//        googleMap.addMarker(new MarkerOptions().position(new LatLng(mapLatitude, mapLongitude)).title("Marker"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-35.016, 143.321), 15));

        //building the request URL for Directions API
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

            StringBuilder polylinePoints = new StringBuilder();

            List<LatLng> latLngs;
            double midlat, midlng, AvgSafetyPointsAddition = 0;

            for(int i=0;i<stepsLength;i++)
            {
                //displays the polyline on the map from start point to destination
                latLngs = PolyUtil.decode(jsonObjectDirections.getJSONArray("routes").getJSONObject(0).getJSONArray("legs")
                        .getJSONObject(0).getJSONArray("steps").getJSONObject(i).getJSONObject("polyline").getString("points"));
                googleMap.addPolyline(new PolylineOptions().addAll(latLngs).width(5).color(Color.BLUE));

                midlat = jsonObjectDirections.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps").getJSONObject(i).getJSONObject("start_location").getDouble("lat");
                midlng = jsonObjectDirections.getJSONArray("routes").getJSONObject(0).getJSONArray("legs").getJSONObject(0).getJSONArray("steps").getJSONObject(i).getJSONObject("start_location").getDouble("lng");

//                mainActivityObject.ratingFunction(null, 1, midlat, midlng);
//                totalSafetyPoints = getIntent().getDoubleExtra("totalSafetyPoints", -1);
//                AvgSafetyPointsAddition = AvgSafetyPointsAddition + totalSafetyPoints;
            }

//            ratingForRoute = AvgSafetyPointsAddition/stepsLength;
//
//            debugMapsView.setText(Double.toString(ratingForRoute));

        } catch (JSONException | IOException  e) {
            e.printStackTrace();
        }
    }

    //function that listens for map clicks
    @Override
    public void onMapClick(LatLng latLng) {

        //does stuff

    }

    public void searchButtonClick(android.view.View v) throws IOException, JSONException {

        // reading the text values of start point and destination and storing then in variables
//        EditText destinationTextView = findViewById(R.id.destinationTextView);
//        EditText startLocationTextView = findViewById(R.id.startLocationTextView);

//        String startLocationTextQuery = startLocationTextView.getText().toString();
//        String destinationTextQuery = destinationTextView.getText().toString();




    }
}
