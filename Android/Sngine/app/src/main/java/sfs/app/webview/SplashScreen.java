package sfs.app.webview;

import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity implements TextureView.SurfaceTextureListener {
    private TextureView textureView;
    private MediaPlayer mediaPlayer;
    private static final String TAG = "SplashScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age_verify);

        textureView = findViewById(R.id.textureView);
        Button btnYes = findViewById(R.id.btnYes);
        Button btnNo = findViewById(R.id.btnNo);

        // Set up TextureView listener
        textureView.setSurfaceTextureListener(this);

        // Handle button clicks
        btnYes.setOnClickListener(v -> {
            releaseMediaPlayer();
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        btnNo.setOnClickListener(v -> finishAffinity());
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        setupVideo(new Surface(surfaceTexture));
    }

    private void setupVideo(Surface surface) {
        try {
            int rawId = getResources().getIdentifier("tom_and_jerry", "raw", getPackageName());
            
            if (rawId != 0) {
                mediaPlayer = new MediaPlayer();
                Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + rawId);
                
                mediaPlayer.setDataSource(this, videoUri);
                mediaPlayer.setSurface(surface);
                mediaPlayer.setLooping(true);
                
                mediaPlayer.setOnPreparedListener(mp -> {
                    Log.d(TAG, "Video prepared successfully");
                    
                    // Apply center crop transformation (CSS cover equivalent)
                    applyCenterCropTransform(mp.getVideoWidth(), mp.getVideoHeight());
                    
                    mp.start();
                });
                
                mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                    Log.e(TAG, "Video playback error - what: " + what + ", extra: " + extra);
                    Toast.makeText(SplashScreen.this, "Video playback error", Toast.LENGTH_SHORT).show();
                    return true;
                });
                
                mediaPlayer.prepareAsync();
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception setting up video: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void applyCenterCropTransform(int videoWidth, int videoHeight) {
        if (videoWidth == 0 || videoHeight == 0) return;
        
        float viewWidth = textureView.getWidth();
        float viewHeight = textureView.getHeight();
        
        float scaleX = 1.0f;
        float scaleY = 1.0f;
        
        if (videoWidth * viewHeight > viewWidth * videoHeight) {
            // Video is wider than view - scale based on height
            scaleX = viewHeight / videoHeight;
            scaleY = scaleX;
        } else {
            // Video is taller than view - scale based on width
            scaleX = viewWidth / videoWidth;
            scaleY = scaleX;
        }
        
        // Calculate translation to center the scaled video
        float scaledWidth = videoWidth * scaleX;
        float scaledHeight = videoHeight * scaleY;
        float translateX = (viewWidth - scaledWidth) / 2;
        float translateY = (viewHeight - scaledHeight) / 2;
        
        // Apply transformation matrix
        Matrix matrix = new Matrix();
        matrix.setScale(scaleX, scaleY);
        matrix.postTranslate(translateX, translateY);
        
        textureView.setTransform(matrix);
    }

    // Other TextureView listener methods
    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            applyCenterCropTransform(mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());
        }
    }
    
    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return true;
    }
    
    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {}

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayer();
    }
}
