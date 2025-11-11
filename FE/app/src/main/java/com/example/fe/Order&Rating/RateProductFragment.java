package com.example.fe;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class RateProductFragment extends Fragment {

    private MaterialButton btnSendFeedback;
    private RatingBar ratingBar;
    private TextInputEditText etFeedback;
    private boolean isDarkButton = false; // Trạng thái nút

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rate_product, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> getParentFragmentManager().popBackStack());

        ratingBar = view.findViewById(R.id.rating_bar);
        etFeedback = view.findViewById(R.id.et_feedback);
        btnSendFeedback = view.findViewById(R.id.btn_send_feedback);

        btnSendFeedback.setOnClickListener(v -> {
            // Lấy dữ liệu
            float rating = ratingBar.getRating();
            String feedbackText = etFeedback.getText().toString();

            // TODO: Gửi dữ liệu này lên API backend của bạn

            // Hiển thị Dialog cảm ơn
            showThanksDialog();
        });

        // Listener để thay đổi màu nút
        // (Giống màn hình rate product-2)
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateButtonState();
            }
            @Override
            public void afterTextChanged(Editable s) {}
        };

        etFeedback.addTextChangedListener(textWatcher);
        ratingBar.setOnRatingBarChangeListener((rb, rating, fromUser) -> updateButtonState());

        return view;
    }

    private void updateButtonState() {
        // Nếu có rating hoặc có text thì đổi màu
        boolean shouldBeDark = ratingBar.getRating() > 0 || !etFeedback.getText().toString().isEmpty();

        if (shouldBeDark && !isDarkButton) {
            // Đổi sang màu tối
            btnSendFeedback.setBackgroundColor(Color.parseColor("#333333"));
            isDarkButton = true;
        } else if (!shouldBeDark && isDarkButton) {
            // Đổi về màu hồng
            btnSendFeedback.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.pink_500));
            isDarkButton = false;
        }
    }

    private void showThanksDialog() {
        if (getContext() == null) return;

        Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_feedback_thanks); // Dùng layout dialog
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        dialog.setCancelable(false);

        Button btnDone = dialog.findViewById(R.id.btn_done);
        btnDone.setOnClickListener(v -> {
            dialog.dismiss();
            // Quay về màn hình trước
            getParentFragmentManager().popBackStack();
        });

        dialog.show();
    }
}
