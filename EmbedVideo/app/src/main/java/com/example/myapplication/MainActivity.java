package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

public class MainActivity extends YouTubeBaseActivity {
    private Button button;
    private YouTubePlayerView youTubePlayerView;
    YouTubePlayer.OnInitializedListener onInitializedListener;
    Button youtube;

    public static final String API_KEY="AIzaSyBJ7F58ojhT3rzIuKOVY7qoMKMn_Ujn1gs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        youTubePlayerView=(YouTubePlayerView)findViewById(R.id.youtubePlayer_view);
        button=(Button)findViewById(R.id.button);

        youtube = findViewById(R.id.yt_button);
        youtube.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                gotoUrl("https://www.youtube.com/watch?v=T7aNSRoDCmg&ab_channel=BRIGHTSIDE");
            }
        });

        onInitializedListener = new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                youTubePlayer.loadVideo("T7aNSRoDCmg");
                //remove the below line if you want to play or pause
                youTubePlayer.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

            }
        };
    button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            youTubePlayerView.initialize("AIzaSyBJ7F58ojhT3rzIuKOVY7qoMKMn_Ujn1gs",onInitializedListener);
        }
    });
}
    private void gotoUrl(String s){
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW, uri));
    }
}