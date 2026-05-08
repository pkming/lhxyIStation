package com.lhxy.istationdevice.android11.app.auth;

import android.content.Context;
import android.content.SharedPreferences;

final class LegacyPasswordManager {
    private static final String PREF_NAME = "pwd_store";
    private static final String KEY_USER_PASSWORD = "user_pwd";
    private static final String SUPER_PASSWORD = "915742";

    private final SharedPreferences sharedPreferences;

    LegacyPasswordManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    boolean isSuperPassword(String password) {
        return SUPER_PASSWORD.equals(password);
    }

    boolean verifyPassword(String password) {
        if (isSuperPassword(password)) {
            return true;
        }
        String userPassword = getUserPassword();
        return !userPassword.isEmpty() && userPassword.equals(password);
    }

    boolean checkPassword(String password) {
        return verifyPassword(password);
    }

    void setUserPassword(String password) {
        sharedPreferences.edit().putString(KEY_USER_PASSWORD, password == null ? "" : password.trim()).apply();
    }

    String getUserPassword() {
        return sharedPreferences.getString(KEY_USER_PASSWORD, "");
    }

    boolean hasUserPassword() {
        return !getUserPassword().isEmpty();
    }
}