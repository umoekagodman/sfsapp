package sfs.app.webview;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.PlayerView;

public class SplashScreen extends AppCompatActivity {
    private PlayerView playerView;
    private ExoPlayer player;
    private static final String TAG = "SplashScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_age_verify);

        playerView = findViewById(R.id.playerView);
        Button btnYes = findViewById(R.id.btnYes);
        Button btnNo = findViewById(R.id.btnNo);

        // Set up ExoPlayer
        setupPlayer();

        // Handle button clicks
        btnYes.setOnClickListener(v -> {
            releasePlayer();
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        btnNo.setOnClickListener(v -> finishAffinity());
    }

    private void setupPlayer() {
        try {
            // Create ExoPlayer instance
            player = new ExoPlayer.Builder(this).build();
            playerView.setPlayer(player);
            
            // Set looping
            player.setRepeatMode(Player.REPEAT_MODE_ALL);
            
            // Get video URI
            int rawId = getResources().getIdentifier("tom_and_jerry", "raw", getPackageName());
            if (rawId != 0) {
                Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + rawId);
                
                // Create media item and set it to player
                MediaItem mediaItem = MediaItem.fromUri(videoUri);
                player.setMediaItem(mediaItem);
                
                // Prepare and play
                player.prepare();
                player.play();
                
                Log.d(TAG, "ExoPlayer setup successfully");
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception setting up ExoPlayer: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void releasePlayer() {
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null) {
            player.play();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }
}
