package com.lianhexinye.m90.common.utils.language;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class SPUtil {
    private static volatile SPUtil instance;
    private final SharedPreferences mSharedPreferences;
    private final String SP_NAME = "language_setting";
    private final String TAG_LANGUAGE = "LanguageSettings";
    private final String TAG_SYSTEM_LANGUAGE = "system_language";
    private Locale systemCurrentLocal = Locale.ENGLISH;

    public SPUtil(Context context) {
        this.mSharedPreferences = context.getSharedPreferences("language_setting", 0);
    }

    public void saveLanguage(String str) {
        SharedPreferences.Editor editorEdit = this.mSharedPreferences.edit();
        editorEdit.putString("LanguageSettings", str);
        editorEdit.commit();
    }

    public int getSelectLanguage() {
        return Integer.valueOf(this.mSharedPreferences.getString("LanguageSettings", "0")).intValue();
    }

    public Locale getSystemCurrentLocal() {
        return this.systemCurrentLocal;
    }

    public void setSystemCurrentLocal(Locale locale) {
        this.systemCurrentLocal = locale;
    }

    public static SPUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (SPUtil.class) {
                if (instance == null) {
                    instance = new SPUtil(context);
                }
            }
        }
        return instance;
    }
}
