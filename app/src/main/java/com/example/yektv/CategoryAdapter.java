package com.example.yektv;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {
    private List<String> categoryList;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(String category);
    }

    public CategoryAdapter(List<String> categoryList, OnCategoryClickListener listener) {
        this.categoryList = categoryList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
        boolean isTv = ctx.getPackageManager().hasSystemFeature("android.software.leanback");

        LinearLayout card = new LinearLayout(ctx);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(20, 0, 20, 0);

        // Koyu gri degrade arkaplan
        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[]{Color.parseColor("#2E2E2E"), Color.parseColor("#1C1C1C")}
        );
        bg.setCornerRadius(40f);
        card.setBackground(bg);
        card.setElevation(6f);

        TextView tv = new TextView(ctx);
        tv.setTextSize(18f);
        tv.setTextColor(Color.parseColor("#E6E1D3")); // Krem-bej açık ton
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setPadding(48, 48, 48, 48);
        tv.setGravity(Gravity.CENTER_VERTICAL);

        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        tv.setLayoutParams(tvParams);

        card.addView(tv);

        RecyclerView.LayoutParams cardParams = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        cardParams.setMargins(48, 24, 48, 24);
        card.setLayoutParams(cardParams);

        card.setClickable(true);
        if (isTv) {
            card.setFocusable(true);
            card.setFocusableInTouchMode(true);
        } else {
            card.setFocusable(false);
        }

        // Focus efektleri (sıcak vurgulu koyu tonlar)
        card.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                bg.setColors(new int[]{Color.parseColor("#605B56"), Color.parseColor("#3A352F")}); // koyu kahverengi-griler
                card.setElevation(16f);
                tv.setTextColor(Color.parseColor("#FFF9E3")); // açık krem vurgusu
            } else {
                bg.setColors(new int[]{Color.parseColor("#2E2E2E"), Color.parseColor("#1C1C1C")});
                card.setElevation(6f);
                tv.setTextColor(Color.parseColor("#E6E1D3"));
            }
        });

        return new ViewHolder(card, tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String category = categoryList.get(position);
        holder.textView.setText(category);

        holder.itemView.setOnClickListener(v -> listener.onCategoryClick(category));
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ViewHolder(View itemView, TextView textView) {
            super(itemView);
            this.textView = textView;
        }
    }
}
