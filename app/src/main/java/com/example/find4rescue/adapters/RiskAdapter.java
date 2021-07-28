package com.example.find4rescue.adapters;

import android.content.Context;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.find4rescue.R;
import com.example.find4rescue.databinding.ItemRiskBinding;
import com.example.find4rescue.fragments.SearchDetailFragment;
import com.example.find4rescue.models.Risk;
import com.parse.ParseFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RiskAdapter extends RecyclerView.Adapter<RiskAdapter.ViewHolder> {

    public interface OnRiskClickListener {
        void onRiskClick(int position);
    }

    Context context;
    List<Risk> risks;
    OnRiskClickListener listener;

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;

    public RiskAdapter(Context context, List<Risk> risks, OnRiskClickListener listener) {
        this.context = context;
        this.risks = risks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("RiskAdapter", "OnCreateViewHolder");
        View view = LayoutInflater.from(context).inflate(R.layout.item_risk, parent, false);
        return new ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RiskAdapter.ViewHolder holder, int position) {
        Log.d("RiskAdapter", "OnBindViewHolder: " + position);
        Risk risk = risks.get(position);
        holder.bind(risk);
    }

    @Override
    public int getItemCount() {
        return risks.size();
    }

    // Clean all elements of the recycler
    public void clear() {
        risks.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Risk> list) {
        risks.addAll(list);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvDescription;
        TextView tvType;
        TextView tvUsername;
        TextView tvAddress;
        ImageView ivDisasterImage;
        TextView tvTimestamp;
        OnRiskClickListener onRiskClickListener;
        LinearLayout row_linearlayout;

        public ViewHolder(@NonNull View itemView, OnRiskClickListener listener) {
            super(itemView);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvType = itemView.findViewById(R.id.tvType);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            ivDisasterImage = itemView.findViewById(R.id.ivDisasterImage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            onRiskClickListener = listener;
            itemView.setOnClickListener(this);
        }

        public void bind(Risk risk) {
            tvDescription.setText("Description: " + risk.getDescription());
            tvType.setText("Type: " + risk.getType());
            tvUsername.setText("Rescuer: " + risk.getRescuer().getUsername());
            tvAddress.setText("Address: " + risk.getAddress());

            ParseFile image = risk.getImage();
            if (image != null) {
                Glide.with(context)
                        .load(image.getUrl())
                        .into(ivDisasterImage);
            }

            Date date = risk.getCreatedAt();

            Log.d("RiskAdapter", "date: " + getRelativeTimeAgo(date.toString()));
            tvTimestamp.setText(getRelativeTimeAgo(date.toString()));

            SearchDetailFragment fragment = new SearchDetailFragment();
            Bundle bundle = new Bundle();
            bundle.putParcelable("risk", risk);
            fragment.setArguments(bundle);
        }

        public String getRelativeTimeAgo(String rawJsonDate) {
            String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
            SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
            sf.setLenient(true);

            try {
                long time = sf.parse(rawJsonDate).getTime();
                long now = System.currentTimeMillis();

                final long diff = now - time;
                if (diff < MINUTE_MILLIS) {
                    return "just now";
                } else if (diff < 50 * MINUTE_MILLIS) {
                    return diff / MINUTE_MILLIS + "m";
                } else if (diff < 24 * HOUR_MILLIS) {
                    return diff / HOUR_MILLIS + "h";
                } else {
                    return diff / DAY_MILLIS + "d";
                }
            } catch (ParseException e) {
                Log.i("RiskAdapter", "getRelativeTimeAgo failed");
                e.printStackTrace();
            }

            return "";
        }

        @Override
        public void onClick(View v) {
            onRiskClickListener.onRiskClick(getAdapterPosition());
        }
    }
}
