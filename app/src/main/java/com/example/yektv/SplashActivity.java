package com.example.yektv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAds.FinishState;
import com.unity3d.ads.UnityAds.UnityAdsError;

public class SplashActivity extends Activity implements IUnityAdsListener {

    private static final int SPLASH_DURATION = 3500; // 3.5 saniye
    private static final String UNITY_GAME_ID = "5497809";
    private static final String INTERSTITIAL_PLACEMENT = "Interstitial_Android";

    private boolean adFinished = false;
    private boolean splashDelayPassed = false;

    private static final String TAG = "SplashActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Analiz için ziyaretçi bildirimi
        trackVisit();

        TextView footer = findViewById(R.id.textFooter);
        Animation blink = new AlphaAnimation(0.3f, 1f);
        blink.setDuration(1000);
        blink.setRepeatMode(Animation.REVERSE);
        blink.setRepeatCount(Animation.INFINITE);
        footer.startAnimation(blink);

        UnityAds.initialize(this, UNITY_GAME_ID, this, false);
        Log.d(TAG, "Unity Ads initialization started");

        new Handler().postDelayed(() -> {
            splashDelayPassed = true;
            Log.d(TAG, "Splash delay passed");
            if (UnityAds.isReady(INTERSTITIAL_PLACEMENT)) {
                Log.d(TAG, "Unity Ads ready, showing ad");
                UnityAds.show(SplashActivity.this, INTERSTITIAL_PLACEMENT);
            } else {
                Log.d(TAG, "Unity Ads not ready, proceeding");
                adFinished = true;
                proceedIfReady();
            }
        }, SPLASH_DURATION);
    }

    private void trackVisit() {
        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL("https://faydalisite.com/track_visit.php");
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.getInputStream().close();  // Sadece isteği tetikle
                Log.d(TAG, "Visit tracked successfully");
            } catch (Exception e) {
                Log.e(TAG, "Visit tracking failed: " + e.getMessage());
            }
        }).start();
    }

    private void proceedIfReady() {
        if (adFinished && splashDelayPassed) {
            Log.d(TAG, "Proceeding to ListSelectionActivity");
            openListSelection();
        }
    }

    private void openListSelection() {
        Intent intent = new Intent(SplashActivity.this, ListSelectionActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onUnityAdsReady(String placementId) {
        Log.d(TAG, "onUnityAdsReady: " + placementId);
    }

    @Override
    public void onUnityAdsStart(String placementId) {
        Log.d(TAG, "onUnityAdsStart: " + placementId);
    }

    @Override
    public void onUnityAdsFinish(String placementId, FinishState result) {
        Log.d(TAG, "onUnityAdsFinish: " + placementId + " result: " + result);
        if (placementId.equals(INTERSTITIAL_PLACEMENT)) {
            adFinished = true;
            proceedIfReady();
        }
    }

    @Override
    public void onUnityAdsError(UnityAdsError error, String message) {
        Log.e(TAG, "onUnityAdsError: " + message);
        adFinished = true;
        proceedIfReady();
    }
}
