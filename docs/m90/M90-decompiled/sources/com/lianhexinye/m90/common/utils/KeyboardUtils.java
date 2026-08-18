package com.lianhexinye.m90.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;

/* JADX INFO: loaded from: classes2.dex */
public class KeyboardUtils {
    private static final String TAG = "KeyboardUtils";

    public static void closeKeyboard(Activity activity) {
        if (activity.getWindow().getAttributes().softInputMode == 2 || activity.getCurrentFocus() == null) {
            return;
        }
        ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 2);
    }

    public static boolean checkDeviceHasNavigationBar(Context context) {
        Resources resources = context.getResources();
        int identifier = resources.getIdentifier("config_showNavigationBar", "bool", "android");
        boolean z = false;
        boolean z2 = identifier > 0 ? resources.getBoolean(identifier) : false;
        try {
            Class<?> cls = Class.forName("android.os.SystemProperties");
            String str = (String) cls.getMethod("get", String.class).invoke(cls, "qemu.hw.mainkeys");
            if (!"1".equals(str)) {
                z = "0".equals(str) ? true : z2;
            }
            return z;
        } catch (Exception e) {
            Log.w(TAG, e);
            return z2;
        }
    }

    public static int getNavigationBarHeight(Context context) {
        Resources resources = context.getResources();
        int identifier = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (identifier <= 0 || !checkDeviceHasNavigationBar(context)) {
            return 0;
        }
        return resources.getDimensionPixelSize(identifier);
    }
}
