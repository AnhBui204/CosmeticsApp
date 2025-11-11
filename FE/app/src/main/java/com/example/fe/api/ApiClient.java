package com.example.fe.api;

import android.content.Context;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static Retrofit retrofitPublic; // Cho login/signup/google login
    private static Retrofit retrofitAuth;   // cho request có token
    private static final String BASE_URL = "http://10.0.2.2:5000";

    // Retrofit cho login/signup
    public static Retrofit getPublicClient() {
        if (retrofitPublic == null) {
            retrofitPublic = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitPublic;
    }

    // Retrofit cho các request cần token
    public static Retrofit getAuthClient(Context context) {
        if (retrofitAuth == null) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(new APIInterceptor(context))
                    .build();

            retrofitAuth = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitAuth;
    }
}

