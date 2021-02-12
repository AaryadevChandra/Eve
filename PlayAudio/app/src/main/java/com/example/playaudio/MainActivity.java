package com.example.playaudio;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button btnPlay, btnPause, btnStop;

    MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnPlay = (Button)findViewById(R.id.btn_activity_main_play);
        btnPause = (Button)findViewById(R.id.btn_activity_main_pause);
        btnStop = (Button)findViewById(R.id.btn_activity_main_stop);

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer==null){
                    mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.sample);
                }
                mediaPlayer.start();
            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer!=null){
                    mediaPlayer.pause();
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer!=null){
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            }
        });
    }
}