package com.example.isalock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context context = this;
        Button bt = (Button)findViewById(R.id.bt);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(MainActivity.this.getFilesDir(), "first.txt");
                String flag = "1";
                if(!file.exists()){
                    try {
                        file.createNewFile();
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("first.txt", Context.MODE_PRIVATE));
                        outputStreamWriter.write(flag);
                        outputStreamWriter.flush();
                        outputStreamWriter.close();
                        Intent i = new Intent(context, FirstActivity.class);
                        startActivity(i);
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else if(file.exists()){
                    String op = "";
                    try {
                        InputStream inputStream = context.openFileInput("first.txt");
                        if(inputStream != null){
                            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                            String receiveString;
                            StringBuilder stringBuilder = new StringBuilder();
                            while ( (receiveString = bufferedReader.readLine()) != null ) {
                                stringBuilder.append(receiveString);
                            }
                            inputStream.close();
                            op = stringBuilder.toString();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(op.equals("1"))
                    {
                        Intent i = new Intent(context, BiometricActivity.class);
                        startActivity(i);
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public static String crypt(String input)
    {
        String op = "Not completed - enc";
        byte[] Bytes = {};
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(input.getBytes());
            op = bytesToHexString(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return op;
    }
    public static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (byte aByte : bytes) {
            String hex = Integer.toHexString(0xFF & aByte);
            if (hex.length() == 1) {
                sb.append('0');
            }
            sb.append(hex);
        }
        return sb.toString();
    }
}