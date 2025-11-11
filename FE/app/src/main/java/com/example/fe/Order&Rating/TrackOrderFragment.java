package com.example.fe;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fe.models.TrackStep; // <-- IMPORT FILE MỚI

import java.util.ArrayList;
import java.util.List;

public class TrackOrderFragment extends Fragment {

    private RecyclerView recyclerView;
    private TrackOrderAdapter adapter;
    private List<TrackStep> stepList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_order, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        recyclerView = view.findViewById(R.id.recycler_view_track);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadTrackingData(); // Tải dữ liệu giả

        adapter = new TrackOrderAdapter(stepList);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void loadTrackingData() {
        stepList = new ArrayList<>();
        // Bây giờ chúng ta sử dụng class TrackStep đã được import
        stepList.add(new TrackStep("Parcel is successfully delivered", "15 May 10:20", true));
        stepList.add(new TrackStep("Parcel is out for delivery", "14 May 08:00", true));
        stepList.add(new TrackStep("Parcel is received at delivery Branch", "13 May 17:25", true));
        stepList.add(new TrackStep("Parcel is in transit", "13 May 07:00", true));
        stepList.add(new TrackStep("Sender has shipped your parcel", "12 May 14:25", true));
        stepList.add(new TrackStep("Sender is preparing to ship your order", "12 May 10:01", true));
    }
}
