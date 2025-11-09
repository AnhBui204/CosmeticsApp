package com.example.fe.ui.seller;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PlaceholderSellerFragment extends Fragment {
    private static final String ARG_TITLE = "arg_title";
    private String title;

    public PlaceholderSellerFragment() {
    }

    public PlaceholderSellerFragment(String title) {
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        setArguments(args);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        TextView tv = new TextView(requireContext());
        tv.setText(title != null ? title : "Placeholder");
        tv.setPadding(24,24,24,24);
        return tv;
    }
}

