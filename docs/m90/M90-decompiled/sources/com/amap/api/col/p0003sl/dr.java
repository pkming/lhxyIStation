package com.amap.api.col.p0003sl;

import android.content.Context;
import android.content.res.AssetManager;
import java.io.File;

/* JADX INFO: compiled from: ResourcesUtil.java */
/* JADX INFO: loaded from: classes2.dex */
public final class dr {
    private static boolean a = new File("/system/framework/amap.jar").exists();

    public static AssetManager a(Context context) {
        if (context == null) {
            return null;
        }
        AssetManager assets = context.getAssets();
        if (a) {
            try {
                assets.getClass().getDeclaredMethod("addAssetPath", String.class).invoke(assets, "/system/framework/amap.jar");
            } catch (Throwable th) {
                jw.c(th, "ResourcesUtil", "getSelfAssets");
            }
        }
        return assets;
    }

    public static int a(Context context, float f) {
        return (int) ((f * context.getResources().getDisplayMetrics().density) + 0.5f);
    }
}
