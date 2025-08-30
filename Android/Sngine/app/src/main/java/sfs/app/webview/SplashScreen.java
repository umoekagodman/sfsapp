package sfs.app.webview;

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
        setContentView(R.layout.activity_age_verify); // Use direct reference

        videoView = findViewById(R.id.videoView);
        Button btnYes = findViewById(R.id.btnYes);
        Button btnNo = findViewById(R.id.btnNo);

        // Set up video background - use direct resource reference
        try {
            // Make sure you have tom_and_jerry.mp4 in res/raw folder
            Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/raw/tom_and_jerry");
            videoView.setVideoURI(videoUri);
            videoView.setOnPreparedListener(mp -> {
                mp.setLooping(true);
                videoView.start();
            });
            videoView.setOnErrorListener((mp, what, extra) -> {
                // If video fails to load, just continue without it
                return true; // Return true indicates we handled the error
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Handle Yes button click - proceed to main app
        btnYes.setOnClickListener(v -> {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        // Handle No button click - close the app
        btnNo.setOnClickListener(v -> finishAffinity());
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
        if (videoView != null && !videoView.isPlaying()) {
            videoView.start();
        }
    }
}
