package com.example.yektv;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.view.*;
import android.widget.*;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ChannelAdapter extends RecyclerView.Adapter<ChannelAdapter.ViewHolder> {
    private List<Channel> channelList;
    private OnChannelClickListener listener;

    public interface OnChannelClickListener {
        void onChannelClick(Channel channel);
    }

    public ChannelAdapter(List<Channel> channelList, OnChannelClickListener listener) {
        this.channelList = channelList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context ctx = parent.getContext();
        boolean isTv = ctx.getPackageManager().hasSystemFeature("android.software.leanback");

        LinearLayout card = new LinearLayout(ctx);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(16, 0, 16, 0);

        GradientDrawable bg = new GradientDrawable(
                GradientDrawable.Orientation.LEFT_RIGHT,
                new int[] {Color.parseColor("#2C3E50"), Color.parseColor("#34495E")} // koyu antrasit degrade
        );
        bg.setCornerRadius(32f);
        card.setBackground(bg);
        card.setElevation(8f);

        TextView tv = new TextView(ctx);
        tv.setTextSize(18f);
        tv.setTextColor(Color.parseColor("#BDC3C7")); // açık gri yazı
        tv.setTypeface(Typeface.DEFAULT_BOLD);
        tv.setPadding(48, 40, 48, 40);
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

        // Focus durumu için yumuşak turkuaz vurgusu
        card.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                bg.setColors(new int[] {Color.parseColor("#1ABC9C"), Color.parseColor("#16A085")});
                card.setElevation(20f);
                tv.setTextColor(Color.WHITE);
            } else {
                bg.setColors(new int[] {Color.parseColor("#2C3E50"), Color.parseColor("#34495E")});
                card.setElevation(8f);
                tv.setTextColor(Color.parseColor("#BDC3C7"));
            }
        });

        return new ViewHolder(card, tv);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Channel channel = channelList.get(position);
        holder.textView.setText(channel.name);

        holder.itemView.setOnClickListener(v -> listener.onChannelClick(channel));
    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ViewHolder(View itemView, TextView textView) {
            super(itemView);
            this.textView = textView;
        }
    }
}
