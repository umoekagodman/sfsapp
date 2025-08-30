package sfs.app.webview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.VideoView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    private VideoView videoView;
    private static final String TAG = "SplashScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age_verify);

        videoView = findViewById(R.id.videoView);
        Button btnYes = findViewById(R.id.btnYes);
        Button btnNo = findViewById(R.id.btnNo);

        // Set up video background with detailed error handling
        setupVideo();

        // Handle Yes button click - proceed to main app
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
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

    private void setupVideo() {
        try {
            Log.d(TAG, "Attempting to load video...");
            
            // Method 1: Try using resource ID directly
            String packageName = getPackageName();
            int rawId = getResources().getIdentifier("tom_and_jerry", "raw", packageName);
            
            Log.d(TAG, "Package name: " + packageName);
            Log.d(TAG, "Raw resource ID: " + rawId);
            
            if (rawId != 0) {
                Uri videoUri = Uri.parse("android.resource://" + packageName + "/" + rawId);
                Log.d(TAG, "Video URI: " + videoUri.toString());
                
                videoView.setVideoURI(videoUri);
                
                videoView.setOnPreparedListener(mp -> {
                    Log.d(TAG, "Video prepared successfully");
                    mp.setLooping(true);
                    videoView.start();
                    Log.d(TAG, "Video playback started");
                });
                
                videoView.setOnErrorListener((mp, what, extra) -> {
                    Log.e(TAG, "Video playback error - what: " + what + ", extra: " + extra);
                    Toast.makeText(SplashScreen.this, "Video format not supported", Toast.LENGTH_SHORT).show();
                    return true; // We handled the error
                });
                
                videoView.setOnCompletionListener(mp -> {
                    Log.d(TAG, "Video completed, restarting...");
                    videoView.start(); // Restart video when it completes
                });
            } else {
                Log.e(TAG, "Raw resource not found");
                Toast.makeText(this, "Video file not found", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception setting up video: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause called");
        if (videoView != null && videoView.isPlaying()) {
            videoView.pause();
            Log.d(TAG, "Video paused");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume called");
        if (videoView != null && !videoView.isPlaying()) {
            videoView.start();
            Log.d(TAG, "Video resumed");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy called");
        if (videoView != null) {
            videoView.stopPlayback();
            Log.d(TAG, "Video playback stopped");
        }
    }
}
