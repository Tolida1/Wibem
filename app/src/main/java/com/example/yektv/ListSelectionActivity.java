package com.example.yektv;

import android.app.Activity;
import android.content.Intent;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class ListSelectionActivity extends Activity {

    private final int cornerRadius = 48;
    private final int colorStart = Color.parseColor("#2c3e50");
    private final int colorEnd = Color.parseColor("#34495e");
    private final int focusColorStart = Color.parseColor("#16a085");
    private final int focusColorEnd = Color.parseColor("#1abc9c");
    private final int textColorNormal = Color.parseColor("#ecf0f1");
    private final int textColorFocus = Color.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout rootLayout = new FrameLayout(this);
        rootLayout.setBackgroundColor(Color.parseColor("#191919"));

        LinearLayout buttonLayout = new LinearLayout(this);
        buttonLayout.setOrientation(LinearLayout.VERTICAL);
        buttonLayout.setPadding(60, 200, 60, 200);
        buttonLayout.setGravity(Gravity.CENTER);

        FrameLayout.LayoutParams buttonLayoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT);
        buttonLayoutParams.gravity = Gravity.CENTER;

        LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        buttonParams.setMargins(0, 24, 0, 24);

        Button btnM3U = createButton("M3U Listesi");
        Button btnManual = createButton("Manuel Liste");
        Button btnM3U2 = createButton("M3U İzle 2");

        View.OnFocusChangeListener focusChangeListener = (v, hasFocus) -> {
            Button btn = (Button) v;
            if (hasFocus) {
                btn.setBackground(createGradientDrawable(focusColorStart, focusColorEnd, cornerRadius));
                btn.setTextColor(textColorFocus);
                btn.setElevation(24f);
            } else {
                btn.setBackground(createGradientDrawable(colorStart, colorEnd, cornerRadius));
                btn.setTextColor(textColorNormal);
                btn.setElevation(12f);
            }
        };

        btnM3U.setOnFocusChangeListener(focusChangeListener);
        btnManual.setOnFocusChangeListener(focusChangeListener);
        btnM3U2.setOnFocusChangeListener(focusChangeListener);

        buttonLayout.addView(btnM3U, buttonParams);
        buttonLayout.addView(btnManual, buttonParams);
        buttonLayout.addView(btnM3U2, buttonParams);

        rootLayout.addView(buttonLayout, buttonLayoutParams);

        ImageView telegramIcon = new ImageView(this);
        telegramIcon.setImageResource(R.drawable.ic_telegram);
        int iconSize = dpToPx(48);
        FrameLayout.LayoutParams iconParams = new FrameLayout.LayoutParams(iconSize, iconSize);
        iconParams.gravity = Gravity.TOP | Gravity.END;
        iconParams.setMargins(0, dpToPx(16), dpToPx(16), 0);
        telegramIcon.setLayoutParams(iconParams);

        telegramIcon.setOnClickListener(v -> {
            String telegramUrl = "https://t.me/taraftarex";
            Intent telegramIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(telegramUrl));
            startActivity(telegramIntent);
        });

        rootLayout.addView(telegramIcon);

        setContentView(rootLayout);

        btnM3U.setOnClickListener(v -> {
            startActivity(new Intent(ListSelectionActivity.this, MainActivity.class));
            finish();
        });

        btnManual.setOnClickListener(v -> {
            startActivity(new Intent(ListSelectionActivity.this, ManualListActivity.class));
            finish();
        });

        btnM3U2.setOnClickListener(v -> {
            startActivity(new Intent(ListSelectionActivity.this, M3U2Activity.class));
            finish();
        });
    }

    private Button createButton(String text) {
        Button btn = new Button(this);
        btn.setText(text);
        btn.setAllCaps(false);
        btn.setTextSize(22f);
        btn.setTypeface(Typeface.DEFAULT_BOLD);
        btn.setTextColor(textColorNormal);
        btn.setGravity(Gravity.CENTER);
        btn.setPadding(0, 50, 0, 50);
        btn.setBackground(createGradientDrawable(colorStart, colorEnd, cornerRadius));
        btn.setElevation(12f);
        return btn;
    }

    private GradientDrawable createGradientDrawable(int startColor, int endColor, int radius) {
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{startColor, endColor});
        gd.setCornerRadius(radius);
        return gd;
    }

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
