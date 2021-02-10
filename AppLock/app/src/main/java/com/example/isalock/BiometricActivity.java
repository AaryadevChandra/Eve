package com.example.isalock;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static androidx.biometric.BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE;
import static androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED;
import static androidx.biometric.BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE;
import static androidx.biometric.BiometricManager.BIOMETRIC_SUCCESS;

public class BiometricActivity extends AppCompatActivity {
    androidx.biometric.BiometricPrompt biometricPrompt;
    androidx.biometric.BiometricPrompt.PromptInfo promptInfo;
    TextView tv; int c = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_biometric);
        tv = (TextView)findViewById(R.id.tv);
        Context context = this;
        Button bt7 = (Button)findViewById(R.id.bt7);
        CancellationSignal cancellationSignal = new CancellationSignal();
        Intent i1 = new Intent(this, appFirstActivity.class);
        Intent i2 = new Intent(this, PasswordActivity.class);
        bt7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(i2);
            }
        });
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG| BiometricManager.Authenticators.BIOMETRIC_WEAK)){
            case BIOMETRIC_SUCCESS:{
                tv.setText("Welcome to the Biometric Test");
                Executor ex = Executors.newSingleThreadExecutor();
                biometricPrompt = new BiometricPrompt(this, ex, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Authentication Error, please try again", Toast.LENGTH_SHORT).show();
                                //cancellationSignal.cancel();
                            }
                        });
                        startActivity(i2);
                    }
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(context, "Authentication Successful", Toast.LENGTH_SHORT).show();
                            }
                        });
                        cancellationSignal.cancel();
                        startActivity(i1);
                    }
                    @Override
                    public void onAuthenticationFailed() {
                        runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                               Toast.makeText(context, "Authentication failed. Please enter your passcode", Toast.LENGTH_SHORT).show();
                        }
                        });
                        biometricPrompt.cancelAuthentication();
                        startActivity(i2);
                    }
                });
                promptInfo = new androidx.biometric.BiometricPrompt.PromptInfo.Builder()
                        .setTitle("ISA App Lock")
                        .setSubtitle("Please place your finger on the sensor")
                        .setNegativeButtonText("Cancel")
                        .build();
                biometricPrompt.authenticate(promptInfo);
            }break;
            case BIOMETRIC_ERROR_HW_UNAVAILABLE:{
                tv.setText("Biometric Hardware unavailable");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Authentication hardware unavailable", Toast.LENGTH_SHORT).show();
                    }
                });
                startActivity(i2);
            }break;
            case BIOMETRIC_ERROR_NO_HARDWARE:{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "Authentication hardware not present", Toast.LENGTH_SHORT).show();
                    }
                });
                tv.setText("We have detected that you do not have a Biometric Hardware Setup. Please use your password");
                startActivity(i2);
            }break;
            case BIOMETRIC_ERROR_NONE_ENROLLED:{
                tv.setText("Please enroll a biometric feature to use the same");
                Intent intent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                startActivityForResult(intent, 100);


            }break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100)
        {
            Intent i1 = new Intent(this, appFirstActivity.class);
            startActivity(i1);
        }
    }
}