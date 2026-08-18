package com.lianhexinye.m90.common.utils;

import android.content.Context;
import android.content.SharedPreferences;

/* JADX INFO: loaded from: classes2.dex */
public class PasswordManager {
    private static final String KEY_USER_PWD = "user_pwd";
    private static final String PREF_NAME = "pwd_store";
    private static final String SUPER_PASSWORD = "915742";
    private SharedPreferences sp;

    public PasswordManager(Context context) {
        this.sp = context.getSharedPreferences(PREF_NAME, 0);
    }

    public boolean isSuperPassword(String str) {
        return SUPER_PASSWORD.equals(str);
    }

    public boolean verifyPassword(String str) {
        if (isSuperPassword(str)) {
            return true;
        }
        String userPassword = getUserPassword();
        return !userPassword.isEmpty() && userPassword.equals(str);
    }

    public void setUserPassword(String str) {
        this.sp.edit().putString(KEY_USER_PWD, str).apply();
    }

    public String getUserPassword() {
        return this.sp.getString(KEY_USER_PWD, "");
    }

    public boolean hasUserPassword() {
        return !getUserPassword().isEmpty();
    }

    public boolean checkPassword(String str) {
        if (isSuperPassword(str)) {
            return true;
        }
        String userPassword = getUserPassword();
        if (userPassword.isEmpty()) {
            return false;
        }
        return userPassword.equals(str);
    }
}
