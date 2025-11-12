package com.example.fe.repository;

import android.content.Context;

// ApiClient với getAuthClient(Context) nằm trong package com.example.fe.api
import com.example.fe.api.ApiClient;
import com.example.fe.network.ApiService;
import com.example.fe.models.Voucher;

import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoucherRepository {
    private final ApiService api;

    public VoucherRepository(Context context) {
        api = ApiClient.getAuthClient(context).create(ApiService.class);
    }

    public interface VouchersCallback {
        void onSuccess(List<Voucher> data);
        void onError(Throwable t);
    }

    public void getVouchers(Integer page, Integer limit, Boolean active, VouchersCallback cb) {
        api.getVouchers(page, limit, active).enqueue(new Callback<com.example.fe.models.VouchersResponse>() {
            @Override
            public void onResponse(Call<com.example.fe.models.VouchersResponse> call, Response<com.example.fe.models.VouchersResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    cb.onSuccess(response.body().getData());
                } else {
                    int code = response != null ? response.code() : -1;
                    cb.onError(new Throwable("Failed to load vouchers: " + code));
                }
            }

            @Override
            public void onFailure(Call<com.example.fe.models.VouchersResponse> call, Throwable t) {
                cb.onError(t);
            }
        });
    }

    public void createVoucher(Voucher v, Callback<Voucher> cb) {
        api.createVoucher(v).enqueue(cb);
    }

    public void updateVoucher(String id, Voucher v, Callback<Voucher> cb) {
        api.updateVoucher(id, v).enqueue(cb);
    }

    public void deleteVoucher(String id, Callback<okhttp3.ResponseBody> cb) {
        api.deleteVoucher(id).enqueue(cb);
    }
}
