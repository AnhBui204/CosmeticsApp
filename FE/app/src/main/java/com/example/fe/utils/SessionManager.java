package com.example.fe.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.fe.data.UserData;

public class SessionManager {

    private static final String PREF_NAME = "app_session";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_NAME = "full_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ROLE = "role";
    private static final String KEY_PHONE = "phone_number";
    private static final String KEY_LOGIN_PROVIDER = "login_provider";
    private SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // Lưu user + token
    public void saveUser(UserData user) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_TOKEN, user.getToken());
        editor.putString(KEY_USER_ID, user.getId());
        editor.putString(KEY_NAME, user.getFullName());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_PHONE, user.getPhoneNumber());
        editor.putString(KEY_ROLE, user.getRole());
        editor.putString(KEY_LOGIN_PROVIDER, user.getLoginProvider());
        editor.apply();
    }

    public String getName() {
        return prefs.getString(KEY_NAME, "");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    // Lấy token
    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    // Lấy thông tin user
    public UserData getUser() {
        UserData user = new UserData();
        user.setToken(prefs.getString(KEY_TOKEN, null));
        user.setId(prefs.getString(KEY_USER_ID, null));
        user.setFullName(prefs.getString(KEY_NAME, null));
        user.setEmail(prefs.getString(KEY_EMAIL, null));
        user.setPhoneNumber(prefs.getString(KEY_PHONE, null));
        user.setRole(prefs.getString(KEY_ROLE, null));
        user.setLoginProvider(prefs.getString(KEY_LOGIN_PROVIDER, "email"));
        return user;
    }

    // Xóa session
    public void clearSession() {
        prefs.edit().clear().apply();
    }
}
