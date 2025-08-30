package sfs.app.webview;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class SplashScreen extends AppCompatActivity {
    private WebView videoWebView;
    private static final String TAG = "SplashScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Make activity full screen (like TikTok)
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            View.SYSTEM_UI_FLAG_FULLSCREEN
        );
        
        setContentView(R.layout.activity_age_verify);

        videoWebView = findViewById(R.id.videoWebView);
        Button btnYes = findViewById(R.id.btnYes);
        Button btnNo = findViewById(R.id.btnNo);

        // Set up WebView for video playback WITH SOUND
        setupVideoWebView();

        // Handle button clicks
        btnYes.setOnClickListener(v -> {
            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        btnNo.setOnClickListener(v -> finishAffinity());
    }

    private void setupVideoWebView() {
        try {
            // Configure WebView
            videoWebView.getSettings().setJavaScriptEnabled(true);
            videoWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
            videoWebView.getSettings().setDomStorageEnabled(true);
            videoWebView.setWebChromeClient(new WebChromeClient());
            videoWebView.setWebViewClient(new WebViewClient());
            
            // Load HTML with video that uses CSS object-fit: cover - REMOVED muted attribute
            String htmlContent = "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no'>" +
                "<style>" +
                "body, html { margin: 0; padding: 0; width: 100%; height: 100%; overflow: hidden; background: #000; }" +
                "video { width: 100%; height: 100%; object-fit: cover; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<video autoplay loop playsinline>" + // REMOVED 'muted' attribute for sound
                "<source src='file:///android_res/raw/tom_and_jerry.mp4' type='video/mp4'>" +
                "</video>" +
                "</body>" +
                "</html>";

            videoWebView.loadDataWithBaseURL(
                "file:///android_res/raw/",
                htmlContent,
                "text/html",
                "UTF-8",
                null
            );

            Log.d(TAG, "WebView video setup successfully with sound");

        } catch (Exception e) {
            Log.e(TAG, "Exception setting up WebView video: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoWebView != null) {
            videoWebView.onPause();
            // Pause video by executing JavaScript
            videoWebView.loadUrl("javascript:document.querySelector('video').pause();");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoWebView != null) {
            videoWebView.onResume();
            // Resume video by executing JavaScript
            videoWebView.loadUrl("javascript:document.querySelector('video').play();");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (videoWebView != null) {
            videoWebView.destroy();
        }
    }
}
