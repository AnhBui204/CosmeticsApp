package com.example.fe;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class IntroAdapter extends RecyclerView.Adapter<IntroAdapter.IntroViewHolder> {
    private int[] images;

    public IntroAdapter(int[] images) {
        this.images = images;
    }

    @NonNull
    @Override
    public IntroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_intro_image, parent, false);
        return new IntroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IntroViewHolder holder, int position) {
        holder.imageIntro.setImageResource(images[position]);
    }

    @Override
    public int getItemCount() {
        return images.length;
    }

    public static class IntroViewHolder extends RecyclerView.ViewHolder {
        ImageView imageIntro;
        public IntroViewHolder(@NonNull View itemView) {
            super(itemView);
            imageIntro = itemView.findViewById(R.id.imageIntro);
        }
    }
}
