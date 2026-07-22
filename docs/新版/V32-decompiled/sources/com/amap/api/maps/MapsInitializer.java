package com.amap.api.maps;

import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import com.amap.api.col.p0003sl.ab;
import com.amap.api.col.p0003sl.dx;
import com.amap.api.col.p0003sl.ih;
import com.amap.api.col.p0003sl.ik;
import com.amap.api.col.p0003sl.ip;
import com.amap.api.col.p0003sl.ku;

/* JADX INFO: loaded from: classes2.dex */
public final class MapsInitializer {
    public static final int HTTP = 1;
    public static final int HTTPS = 2;
    public static String TERRAIN_LOCAL_DEM_SOURCE_PATH = null;
    private static boolean closeTileOverlay = false;
    private static ExceptionLogger exceptionLogger = null;
    private static boolean isContourLineEnable = false;
    private static boolean isLoadWorldGridMap = false;
    private static boolean isNeedDownloadCoordinateConvertLibrary = true;
    private static boolean isNetWorkEnable = true;
    private static boolean isPolyline2Enable = true;
    private static boolean isSupportRecycleView = true;
    private static boolean isTerrainEnable = false;
    private static boolean isTextureDestroyedRender = false;
    private static boolean isTextureSizeChangedInvoked = false;
    private static boolean isTextureViewDestorySync = true;
    private static boolean isloadWorldVectorMap = true;
    private static int mProtocol = 1;
    private static String mWorldVectorOfflineMapStyleAssetsPath = "";
    private static String mWorldVectorOfflineMapStyleFilePath = "";
    public static String sdcardDir = "";

    public static void disableCachedMapDataUpdate(boolean z) {
    }

    public static boolean getPolyline2Enable() {
        return true;
    }

    public static String getVersion() {
        return "10.0.600";
    }

    public static boolean isDisableCachedMapDataUpdate() {
        return false;
    }

    public static void setBuildingHeight(int i) {
    }

    public static void setPolyline2Enable(boolean z) {
    }

    public static void initialize(Context context) throws RemoteException {
        if (context != null) {
            ab.a = context.getApplicationContext();
        } else {
            Log.w("MapsInitializer", "the context is null");
        }
    }

    public static void setNetWorkEnable(boolean z) {
        isNetWorkEnable = z;
    }

    public static boolean getNetWorkEnable() {
        return isNetWorkEnable;
    }

    public static void setApiKey(String str) {
        if (str == null || str.trim().length() <= 0) {
            return;
        }
        ih.a(ab.a, str);
    }

    public static boolean isLoadWorldGridMap() {
        return isLoadWorldGridMap;
    }

    public static void loadWorldVectorMap(boolean z) {
        isloadWorldVectorMap = z;
    }

    public static boolean isLoadWorldVectorMap() {
        return isloadWorldVectorMap;
    }

    public static void setDownloadCoordinateConvertLibrary(boolean z) {
        isNeedDownloadCoordinateConvertLibrary = z;
    }

    public static boolean isDownloadCoordinateConvertLibrary() {
        return isNeedDownloadCoordinateConvertLibrary;
    }

    public static void setHost(String str) {
        if (TextUtils.isEmpty(str)) {
            ku.a = -1;
            ku.b = "";
        } else {
            ku.a = 1;
            ku.b = str;
        }
    }

    public static void setProtocol(int i) {
        mProtocol = i;
    }

    public static int getProtocol() {
        return mProtocol;
    }

    public static void setTextureViewDestorySync(boolean z) {
        isTextureViewDestorySync = z;
    }

    public static boolean getTextureViewDestorySync() {
        return isTextureViewDestorySync;
    }

    public static void setWorldVectorOfflineMapStyleFilePath(String str) {
        mWorldVectorOfflineMapStyleFilePath = str;
    }

    public static String getWorldVectorOfflineMapStyleFilePath() {
        return mWorldVectorOfflineMapStyleFilePath;
    }

    public static void setWorldVectorOfflineMapStyleAssetsPath(String str) {
        mWorldVectorOfflineMapStyleAssetsPath = str;
    }

    public static String getWorldVectorOfflineMapStyleAssetsPath() {
        return mWorldVectorOfflineMapStyleAssetsPath;
    }

    public static void setTextureDestroyedRender(boolean z) {
        isTextureDestroyedRender = z;
    }

    public static boolean getTextureDestroyRender() {
        return isTextureDestroyedRender;
    }

    public static void setTextureSizeChangedInvoked(boolean z) {
        isTextureSizeChangedInvoked = z;
    }

    public static boolean getTextureSizeChangedInvoked() {
        return isTextureSizeChangedInvoked;
    }

    public static void setExceptionLogger(ExceptionLogger exceptionLogger2) {
        exceptionLogger = exceptionLogger2;
    }

    public static ExceptionLogger getExceptionLogger() {
        return exceptionLogger;
    }

    public static boolean isSupportRecycleView() {
        return isSupportRecycleView;
    }

    public static void setSupportRecycleView(boolean z) {
        isSupportRecycleView = z;
    }

    public static String getDeviceId(Context context) {
        return ik.q(context);
    }

    public static boolean isTerrainEnable() {
        return isTerrainEnable;
    }

    public static boolean isContourLineEnable() {
        return isContourLineEnable;
    }

    public static void setTerrainEnable(boolean z) {
        isTerrainEnable = z;
    }

    public static void setContourLIneEnable(boolean z) {
        isContourLineEnable = z;
    }

    public static synchronized void updatePrivacyShow(Context context, boolean z, boolean z2) {
        ip.a(context, z, z2, dx.a());
    }

    public static synchronized void updatePrivacyAgree(Context context, boolean z) {
        ip.a(context, z, dx.a());
    }
}
