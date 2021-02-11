package com.example.locationratingiv;


import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class Fragment extends AppCompatActivity implements OnMapReadyCallback{

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

        double mapLatitude = getIntent().getDoubleExtra("lat", 0);
        double mapLongitude= getIntent().getDoubleExtra("lng", 0);

        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                .clickable(true)
                .add(
                        new LatLng(-35.016, 143.321),
                        new LatLng(-34.747, 145.592),
                        new LatLng(-34.364, 147.891),
                        new LatLng(-33.501, 150.217),
                        new LatLng(-32.306, 149.248),
                        new LatLng(-32.491, 147.309)));



//        googleMap.addMarker(new MarkerOptions().position(new LatLng(mapLatitude, mapLongitude)).title("Marker"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(-35.016, 143.321), 15)); 


    }
}
