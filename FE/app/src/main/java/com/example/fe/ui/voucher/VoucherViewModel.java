package com.example.fe.ui.voucher;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.fe.models.Voucher;
import com.example.fe.repository.VoucherRepository;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VoucherViewModel extends AndroidViewModel {
    private final VoucherRepository repository;
    public final MutableLiveData<List<Voucher>> vouchers = new MutableLiveData<>();
    public final MutableLiveData<String> error = new MutableLiveData<>();

    public VoucherViewModel(@NonNull Application application) {
        super(application);
        repository = new VoucherRepository(application.getApplicationContext());
    }

    public void loadVouchers() {
        repository.getVouchers(1, 100, null, new VoucherRepository.VouchersCallback() {
            @Override
            public void onSuccess(List<Voucher> data) {
                vouchers.postValue(data);
            }

            @Override
            public void onError(Throwable t) {
                error.postValue(t == null ? "Unknown error" : t.getMessage());
            }
        });
    }

    public void createVoucher(Voucher v, Callback<Voucher> cb) {
        repository.createVoucher(v, cb);
    }

    public void updateVoucher(String id, Voucher v, Callback<Voucher> cb) {
        repository.updateVoucher(id, v, cb);
    }

    public void deleteVoucher(String id, Callback<ResponseBody> cb) {
        repository.deleteVoucher(id, new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                cb.onResponse(call, response);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                cb.onFailure(call, t);
            }
        });
    }
}
