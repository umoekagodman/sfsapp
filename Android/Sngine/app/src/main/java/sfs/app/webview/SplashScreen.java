package sfs.app.webview; // Your package name

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Set the age verification layout
        setContentView(getResources().getIdentifier("activity_age_verify", "layout", getPackageName()));

        // Find views using resource identifiers
        videoView = findViewById(getResources().getIdentifier("videoView", "id", getPackageName()));
        Button btnYes = findViewById(getResources().getIdentifier("btnYes", "id", getPackageName()));
        Button btnNo = findViewById(getResources().getIdentifier("btnNo", "id", getPackageName()));

        // Set up video background
        try {
            int videoResourceId = getResources().getIdentifier("tom_and_jerry", "raw", getPackageName());
            if (videoResourceId != 0) {
                Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + videoResourceId);
                videoView.setVideoURI(videoUri);
                videoView.setOnPreparedListener(mp -> {
                    mp.setLooping(true);
                    videoView.start();
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Handle Yes button click - proceed to main app
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashScreen.this, getMainActivityClass());
                startActivity(intent);
                finish();
            }
        });

        // Handle No button click - close the app
        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });
    }

    // Helper method to get the main activity class
    private Class<?> getMainActivityClass() {
        try {
            return Class.forName("sfs.app.webview.MainActivity");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoView != null) {
            videoView.start();
        }
    }
}
