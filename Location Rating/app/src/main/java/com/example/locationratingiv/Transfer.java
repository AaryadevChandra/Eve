package com.example.locationratingiv;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.SupportMapFragment;

public class Transfer extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        double totalSafetyPoints = getIntent().getDoubleExtra("totalSafetyPoints", 0);

        Intent i = new Intent(getBaseContext(), MainActivity.class);
        i.putExtra("totalSafetyPoints", totalSafetyPoints);
        startActivity(i);

    }
}
