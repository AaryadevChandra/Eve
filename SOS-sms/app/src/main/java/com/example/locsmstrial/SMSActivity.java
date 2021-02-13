package com.example.locsmstrial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.EditText;

public class SMSActivity extends AppCompatActivity {
double latitude, longitude;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms);
        Intent i = getIntent();
        latitude = i.getDoubleExtra("lat",0.0);
        longitude = i.getDoubleExtra("lng",0.0);
    }
    public void onClick(View v)
    {
        String phone = ((EditText)findViewById(R.id.etp)).getText().toString();
        String message = "Please help me! My location is " + LinkCreator(latitude, longitude, 15.0);
        if(checkSMSPermission())
        {
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phone, null, message, null, null);
        }
    }
    public String LinkCreator(double latitudeinp, double longitudeinp, double zoominp)
    {
        return "https://www.google.com/maps/place/" + String.valueOf(latitudeinp) + "+" + String.valueOf(longitudeinp) + "/@" + String.valueOf(latitudeinp) + "," + String.valueOf(longitudeinp) + "," + String.valueOf(zoominp) + "z";
    }
    public boolean checkSMSPermission()
    {   //function to check whether permission for sending SMSs has been granted
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, 2);//in-built function to request for SMS permission

        //after this step, the permission should be granted by the user
        int check = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS); // checking whether the permission
        return (check == PackageManager.PERMISSION_GRANTED);                                   // for sending SMSs has been granted and returning the 'true' or 'false' value
    }


}