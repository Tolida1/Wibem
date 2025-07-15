package com.example.yektv;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;

import androidx.recyclerview.widget.*;

import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.ads.UnityAds.UnityAdsError;
import com.unity3d.ads.UnityAds.FinishState;

import java.util.*;

public class M3U2Activity extends Activity implements IUnityAdsListener {
    RecyclerView recyclerView;
    List<String> categoryList = new ArrayList<>();
    Map<String, List<Channel>> categoryChannelMap = new HashMap<>();
    List<Channel> currentChannelList = new ArrayList<>();
    boolean showingCategories = true;
    String m3uUrl = "https://faydalisite.com/getir_m3u2.php";

    CategoryAdapter categoryAdapter;
    ChannelAdapter channelAdapter;

    private static final String UNITY_GAME_ID = "5497809";
    private static final String INTERSTITIAL_PLACEMENT = "Interstitial_Android";
    private int clickCount = 0;
    private final int showAdEvery = 3;
    private String lastSelectedChannelUrl = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Unity Ads başlat
        UnityAds.initialize(this, UNITY_GAME_ID, this, false);

        setContentView(R.layout.activity_main);  // Aynı layout kullanılabilir

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return true;
            }
            @Override
            public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
                LinearSmoothScroller smoothScroller = new LinearSmoothScroller(recyclerView.getContext()) {
                    private static final float SPEED = 250f;
                    @Override
                    protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
                        return SPEED / displayMetrics.densityDpi;
                    }
                };
                smoothScroller.setTargetPosition(position);
                startSmoothScroll(smoothScroller);
            }
        };
        recyclerView.setLayoutManager(layoutManager);

        categoryAdapter = new CategoryAdapter(categoryList, category -> showChannelsForCategory(category));
        recyclerView.setAdapter(categoryAdapter);

        channelAdapter = new ChannelAdapter(currentChannelList, channel -> {
            lastSelectedChannelUrl = channel.url;
            onChannelSelected(channel);
        });

        new Thread(() -> {
            StringBuilder result = new StringBuilder();
            try {
                java.net.URL url = new java.net.URL(m3uUrl);
                java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                java.io.BufferedReader in = new java.io.BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null)
                    result.append(inputLine).append("\n");
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            List<Channel> allChannels = M3UParser.parse(result.toString());
            for (Channel c : allChannels) {
                String group = (c.group == null || c.group.isEmpty()) ? "Tümü" : c.group;
                if (!categoryChannelMap.containsKey(group))
                    categoryChannelMap.put(group, new ArrayList<>());
                categoryChannelMap.get(group).add(c);
            }
            categoryList.clear();
            categoryList.addAll(categoryChannelMap.keySet());

            runOnUiThread(() -> categoryAdapter.notifyDataSetChanged());
        }).start();
    }

    private void showChannelsForCategory(String category) {
        showingCategories = false;
        currentChannelList.clear();
        currentChannelList.addAll(categoryChannelMap.get(category));
        recyclerView.setAdapter(channelAdapter);
        channelAdapter.notifyDataSetChanged();
    }

    private void onChannelSelected(Channel channel) {
        clickCount++;
        if (clickCount % showAdEvery == 0 && UnityAds.isReady(INTERSTITIAL_PLACEMENT)) {
            UnityAds.show(this, INTERSTITIAL_PLACEMENT);
        } else {
            openPlayer(channel.url);
        }
    }

    private void openPlayer(String url) {
        Intent intent = new Intent(M3U2Activity.this, PlayerActivity.class);
        intent.putExtra("url", url);
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
                    openPlayer(lastSelectedChannelUrl);
                    lastSelectedChannelUrl = null;
                }
            }
        }
    }

    @Override
    public void onUnityAdsError(UnityAdsError error, String message) {}

    @Override
    public void onBackPressed() {
        if (!showingCategories) {
            showingCategories = true;
            recyclerView.setAdapter(categoryAdapter);
            categoryAdapter.notifyDataSetChanged();
        } else {
            Intent intent = new Intent(this, ListSelectionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        }
    }
}
