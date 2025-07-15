package com.example.yektv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.*;
import java.util.*;

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAds.UnityAdsError;
import com.unity3d.ads.UnityAds.FinishState;

public class ManualListActivity extends Activity implements IUnityAdsListener {
    RecyclerView recyclerView;
    List<Channel> manualChannels = new ArrayList<>();
    ChannelAdapter channelAdapter;

    String manualApiUrl = "https://faydalisite.com/api/manual_channels.php";

    private static final String UNITY_GAME_ID = "5497809";
    private static final String INTERSTITIAL_PLACEMENT = "Interstitial_Android";
    private int clickCount = 0;
    private final int showAdEvery = 3;
    private String lastSelectedChannelUrl = null;
    private String lastReferer = "";
    private String lastOrigin = "";
    private String lastUserAgent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Unity Ads başlat
        UnityAds.initialize(this, UNITY_GAME_ID, this, false);

        setContentView(R.layout.activity_manual_list);

        recyclerView = findViewById(R.id.manualRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        channelAdapter = new ChannelAdapter(manualChannels, channel -> {
            lastSelectedChannelUrl = channel.url;
            lastReferer = channel.referer;
            lastOrigin = channel.origin;
            lastUserAgent = channel.userAgent;
            onChannelSelected(channel);
        });

        recyclerView.setAdapter(channelAdapter);

        fetchManualChannels();
    }

    private void fetchManualChannels() {
        new Thread(() -> {
            try {
                java.net.URL url = new java.net.URL(manualApiUrl);
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    result.append(line);
                }
                in.close();

                org.json.JSONArray array = new org.json.JSONArray(result.toString());
                manualChannels.clear();

                for (int i = 0; i < array.length(); i++) {
                    org.json.JSONObject obj = array.getJSONObject(i);
                    Channel ch = new Channel();
                    ch.name = obj.getString("name");
                    ch.url = obj.getString("url");
                    ch.referer = obj.optString("referer", "");
                    ch.origin = obj.optString("origin", "");
                    ch.userAgent = obj.optString("userAgent", "");
                    manualChannels.add(ch);
                }

                runOnUiThread(() -> channelAdapter.notifyDataSetChanged());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void onChannelSelected(Channel channel) {
        clickCount++;
        if (clickCount % showAdEvery == 0 && UnityAds.isReady(INTERSTITIAL_PLACEMENT)) {
            UnityAds.show(this, INTERSTITIAL_PLACEMENT);
        } else {
            openPlayer(channel.url, channel.referer, channel.origin, channel.userAgent);
        }
    }

    private void openPlayer(String url, String referer, String origin, String userAgent) {
        Intent intent = new Intent(ManualListActivity.this, PlayerWithHeadersActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("referer", referer);
        intent.putExtra("origin", origin);
        intent.putExtra("userAgent", userAgent);
        startActivity(intent);
    }

    // Unity Ads callbacks
    @Override
    public void onUnityAdsReady(String placementId) {}

    @Override
    public void onUnityAdsStart(String placementId) {}

    @Override
    public void onUnityAdsFinish(String placementId, FinishState result) {
        if (placementId.equals(INTERSTITIAL_PLACEMENT)) {
            if (result == FinishState.COMPLETED || result == FinishState.SKIPPED) {
                if (lastSelectedChannelUrl != null) {
                    openPlayer(lastSelectedChannelUrl, lastReferer, lastOrigin, lastUserAgent);
                    lastSelectedChannelUrl = null;
                }
            }
        }
    }

    @Override
    public void onUnityAdsError(UnityAdsError error, String message) {}

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, ListSelectionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
