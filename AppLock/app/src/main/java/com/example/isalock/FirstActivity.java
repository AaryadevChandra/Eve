package com.example.isalock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;


public class FirstActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        Context context = this;
        File file = new File(context.getFilesDir(), "first.txt");
        EditText et1;
        EditText et2;
        et2 = (EditText)findViewById(R.id.etrecheck);
        et1 = (EditText)findViewById(R.id.etfirst);
        Button bt2 = (Button)findViewById(R.id.bt2);
        bt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = "";
                String passwordcheck = "";
                password = et1.getText().toString();
                passwordcheck = et2.getText().toString();
                if(password.length() >= 8){
                    if(password.equals(passwordcheck)) {
                        String protectedpass = MainActivity.crypt(password);
                        try {
                            file.createNewFile();
                            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("secure.txt", Context.MODE_PRIVATE));
                            outputStreamWriter.write(protectedpass);
                            outputStreamWriter.flush();
                            outputStreamWriter.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(FirstActivity.this, "Thank you, password saved!", Toast.LENGTH_LONG).show();
                        Intent i = new Intent(context, appFirstActivity.class);
                        startActivity(i);
                    }
                    else {
                        Toast.makeText(context, "Passwords do not match. Please check again", Toast.LENGTH_SHORT).show();
                        et1.requestFocus();
                    }
                }
                else
                {
                    Toast.makeText(context, "Please enter a password with 8 or more characters", Toast.LENGTH_SHORT).show();
                    et1.requestFocus();
                }
            }
        });

    }
}
