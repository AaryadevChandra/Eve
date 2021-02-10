package com.example.isalock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PasswordActivity extends AppCompatActivity {
    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password);
        Intent i = new Intent(this, appFirstActivity.class);
        Context context = this;
        final String[] pass = {""};
        et = (EditText)findViewById(R.id.et);
        TextView tv1 = (TextView)findViewById(R.id.tv1);
        Button bt = (Button)findViewById(R.id.bt3);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputStream inputStream = context.openFileInput("secure.txt");
                    if(inputStream != null){
                        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                        String receiveString;
                        StringBuilder stringBuilder = new StringBuilder();
                        while ( (receiveString = bufferedReader.readLine()) != null ) {
                            stringBuilder.append(receiveString);
                        }
                        inputStream.close();
                        pass[0] = stringBuilder.toString();
                    }
                    String argument = et.getText().toString();
                    argument = MainActivity.crypt(argument);
                    if(pass[0].equals(argument)){
                        startActivity(i);
                    }
                    else{
                        tv1.setText("Incorrect password. Please enter the correct password");
                        et.requestFocus();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}