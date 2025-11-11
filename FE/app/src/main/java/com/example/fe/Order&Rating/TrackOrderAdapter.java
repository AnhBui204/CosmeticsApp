package com.example.fe;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe.models.TrackStep; // <-- IMPORT FILE MỚI

import java.util.List;

public class TrackOrderAdapter extends RecyclerView.Adapter<TrackOrderAdapter.TrackViewHolder> {

    private List<TrackStep> stepList;
    private Context context;

    public TrackOrderAdapter(List<TrackStep> stepList) {
        this.stepList = stepList;
    }

    @NonNull
    @Override
    public TrackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_track_step, parent, false);
        return new TrackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrackViewHolder holder, int position) {
        TrackStep step = stepList.get(position);

        // Sử dụng getters từ class TrackStep
        holder.tvStatus.setText(step.getStatus());
        holder.tvDateTime.setText(step.getDateTime());

        // Logic cho đường kẻ và icon
        if (position == 0) {
            holder.lineTop.setVisibility(View.INVISIBLE);
        } else {
            holder.lineTop.setVisibility(View.VISIBLE);
        }

        if (position == stepList.size() - 1) {
            holder.lineBottom.setVisibility(View.INVISIBLE);
        } else {
            holder.lineBottom.setVisibility(View.VISIBLE);
        }

        // Đặt icon
        if (step.isCompleted()) {
            holder.ivDot.setImageResource(R.drawable.ic_check_circle_filled);
        } else {
            holder.ivDot.setImageResource(R.drawable.ic_dot_pending);
        }
    }

    @Override
    public int getItemCount() {
        return stepList.size();
    }

    class TrackViewHolder extends RecyclerView.ViewHolder {
        View lineTop, lineBottom;
        ImageView ivDot;
        TextView tvStatus, tvDateTime;

        public TrackViewHolder(@NonNull View itemView) {
            super(itemView);
            lineTop = itemView.findViewById(R.id.line_top);
            lineBottom = itemView.findViewById(R.id.line_bottom);
            ivDot = itemView.findViewById(R.id.iv_dot);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvDateTime = itemView.findViewById(R.id.tv_date_time);
        }
    }
}
