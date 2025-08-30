package sfs.app.webview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
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

        // Wait for layout to be measured, then apply correct scaling
        videoView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                videoView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                setupVideo();
            }
        });

        // Handle button clicks
        btnYes.setOnClickListener(v -> {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        btnNo.setOnClickListener(v -> finishAffinity());
    }

    private void setupVideo() {
        try {
            int rawId = getResources().getIdentifier("tom_and_jerry", "raw", getPackageName());
            
            if (rawId != 0) {
                Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + rawId);
                videoView.setVideoURI(videoUri);
                
                videoView.setOnPreparedListener(mp -> {
                    Log.d(TAG, "Video prepared successfully");
                    mp.setLooping(true);
                    
                    // Get video dimensions
                    int videoWidth = mp.getVideoWidth();
                    int videoHeight = mp.getVideoHeight();
                    
                    // Get screen dimensions
                    int screenWidth = getResources().getDisplayMetrics().widthPixels;
                    int screenHeight = getResources().getDisplayMetrics().heightPixels;
                    
                    // Calculate proper scale to cover screen (CSS cover equivalent)
                    float scale = calculateCoverScale(videoWidth, videoHeight, screenWidth, screenHeight);
                    
                    // Apply the scale
                    videoView.setScaleX(scale);
                    videoView.setScaleY(scale);
                    
                    // Center the video
                    videoView.setPivotX(0);
                    videoView.setPivotY(0);
                    videoView.setTranslationX((screenWidth - (videoWidth * scale)) / 2);
                    videoView.setTranslationY((screenHeight - (videoHeight * scale)) / 2);
                    
                    videoView.start();
                });
                
                videoView.setOnErrorListener((mp, what, extra) -> {
                    Log.e(TAG, "Video playback error");
                    Toast.makeText(SplashScreen.this, "Video playback error", Toast.LENGTH_SHORT).show();
                    return true;
                });
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception setting up video: " + e.getMessage());
        }
    }

    private float calculateCoverScale(int videoWidth, int videoHeight, int screenWidth, int screenHeight) {
        if (videoWidth == 0 || videoHeight == 0) return 1.0f;
        
        float widthRatio = (float) screenWidth / videoWidth;
        float heightRatio = (float) screenHeight / videoHeight;
        
        // Use the larger ratio to ensure video covers entire screen
        return Math.max(widthRatio, heightRatio);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoView != null) {
            videoView.stopPlayback();
        }
    }
}
