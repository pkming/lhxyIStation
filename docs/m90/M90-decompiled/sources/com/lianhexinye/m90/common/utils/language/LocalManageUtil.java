package com.lianhexinye.m90.common.utils.language;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.util.DisplayMetrics;
import android.util.Log;
import com.lianhexinye.m90.R;
import java.util.Locale;

/* JADX INFO: loaded from: classes2.dex */
public class LocalManageUtil {
    private static final String TAG = "LocalManageUtil";

    public static Locale getSystemLocale(Context context) {
        return SPUtil.getInstance(context).getSystemCurrentLocal();
    }

    public static String getSelectLanguage(Context context) {
        int selectLanguage = SPUtil.getInstance(context).getSelectLanguage();
        if (selectLanguage == 0) {
            return context.getString(R.string.language_auto);
        }
        if (selectLanguage == 1) {
            return context.getString(R.string.language_cn);
        }
        if (selectLanguage == 2) {
            return context.getString(R.string.language_traditional);
        }
        if (selectLanguage == 3) {
            return context.getString(R.string.language_en);
        }
        if (selectLanguage == 4) {
            return context.getString(R.string.language_ko);
        }
        return context.getString(R.string.language_es);
    }

    public static Locale getSetLanguageLocal(Context context) {
        int selectLanguage = SPUtil.getInstance(context).getSelectLanguage();
        if (selectLanguage == 0) {
            return getSystemLocale(context);
        }
        if (selectLanguage == 1) {
            return Locale.CHINA;
        }
        if (selectLanguage == 2) {
            return Locale.TAIWAN;
        }
        if (selectLanguage == 3) {
            return Locale.ENGLISH;
        }
        if (selectLanguage == 4) {
            return Locale.KOREA;
        }
        if (selectLanguage == 5) {
            return Locale.CANADA;
        }
        return getSystemLocale(context);
    }

    public static void saveSelectLanguage(Context context, String str) {
        SPUtil.getInstance(context).saveLanguage(str);
        setApplicationLanguage(context);
    }

    public static Context setLocal(Context context) {
        return updateResource(context, getSetLanguageLocal(context));
    }

    private static Context updateResource(Context context, Locale locale) {
        Locale.setDefault(locale);
        Resources resources = context.getResources();
        Configuration configuration = new Configuration(resources.getConfiguration());
        if (Build.VERSION.SDK_INT >= 17) {
            configuration.setLocale(locale);
            return context.createConfigurationContext(configuration);
        }
        configuration.locale = locale;
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
        return context;
    }

    public static void setApplicationLanguage(Context context) {
        Resources resources = context.getApplicationContext().getResources();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        Configuration configuration = resources.getConfiguration();
        Locale setLanguageLocal = getSetLanguageLocal(context);
        configuration.locale = setLanguageLocal;
        if (Build.VERSION.SDK_INT >= 24) {
            LocaleList localeList = new LocaleList(setLanguageLocal);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);
            context.getApplicationContext().createConfigurationContext(configuration);
            Locale.setDefault(setLanguageLocal);
        }
        resources.updateConfiguration(configuration, displayMetrics);
    }

    public static void saveSystemCurrentLanguage(Context context) {
        Locale locale;
        if (Build.VERSION.SDK_INT >= 24) {
            locale = LocaleList.getDefault().get(0);
        } else {
            locale = Locale.getDefault();
        }
        Log.d(TAG, locale.getLanguage());
        SPUtil.getInstance(context).setSystemCurrentLocal(locale);
    }

    public static void onConfigurationChanged(Context context) {
        saveSystemCurrentLanguage(context);
        setLocal(context);
        setApplicationLanguage(context);
    }
}
