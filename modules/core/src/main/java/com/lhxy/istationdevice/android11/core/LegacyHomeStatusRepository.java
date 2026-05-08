package com.lhxy.istationdevice.android11.core;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

/**
 * 首页右侧司机/提示/喊话区共享状态。
 * <p>
 * 这层下沉到 core 后，app 页面和 domain 业务模块都能直接写同一份状态，
 * 后续补 FTP / APK 后台链时不用再让页面层代发提示。
 */
public final class LegacyHomeStatusRepository {
    private static final String PREFS_NAME = "legacy_home_status";
    private static final String KEY_INFO_TIPS = "info_tips";
    private static final String KEY_INFO_OPERATION = "info_operation";
    private static final String KEY_INFO_OPERATION_MSG = "info_operation_msg";
    private static final String KEY_SHOUTING = "shouting";

    private LegacyHomeStatusRepository() {
    }

    @NonNull
    public static Snapshot getState(@NonNull Context context) {
        SharedPreferences prefs = prefs(context);
        return new Snapshot(
                prefs.getString(KEY_INFO_TIPS, ""),
                prefs.getInt(KEY_INFO_OPERATION, InfoOperation.NONE),
                prefs.getInt(KEY_INFO_OPERATION_MSG, 0),
                prefs.getString(KEY_SHOUTING, "")
        );
    }

    public static void setInfoTips(@NonNull Context context, @NonNull String text) {
        prefs(context).edit()
                .putString(KEY_INFO_TIPS, safe(text))
                .putInt(KEY_INFO_OPERATION, InfoOperation.NONE)
                .putInt(KEY_INFO_OPERATION_MSG, 0)
                .apply();
    }

    public static void setInfoOperation(@NonNull Context context, int operation) {
        setInfoOperation(context, operation, 0);
    }

    public static void setInfoOperation(@NonNull Context context, int operation, int operationMsg) {
        prefs(context).edit()
                .remove(KEY_INFO_TIPS)
                .putInt(KEY_INFO_OPERATION, operation)
                .putInt(KEY_INFO_OPERATION_MSG, operationMsg)
                .apply();
    }

    public static void clearInfoTips(@NonNull Context context) {
        prefs(context).edit()
                .remove(KEY_INFO_TIPS)
                .putInt(KEY_INFO_OPERATION, InfoOperation.NONE)
                .putInt(KEY_INFO_OPERATION_MSG, 0)
                .apply();
    }

    public static void setShouting(@NonNull Context context, @NonNull String text) {
        prefs(context).edit().putString(KEY_SHOUTING, safe(text)).apply();
    }

    public static void clearShouting(@NonNull Context context) {
        prefs(context).edit().remove(KEY_SHOUTING).apply();
    }

    @NonNull
    public static SharedPreferences.OnSharedPreferenceChangeListener registerListener(
            @NonNull Context context,
            @NonNull Listener listener
    ) {
        SharedPreferences.OnSharedPreferenceChangeListener prefsListener = (sharedPreferences, key) -> {
            if (KEY_INFO_TIPS.equals(key)
                    || KEY_INFO_OPERATION.equals(key)
                    || KEY_INFO_OPERATION_MSG.equals(key)
                    || KEY_SHOUTING.equals(key)) {
                listener.onHomeStatusChanged();
            }
        };
        prefs(context).registerOnSharedPreferenceChangeListener(prefsListener);
        return prefsListener;
    }

    public static void unregisterListener(
            @NonNull Context context,
            SharedPreferences.OnSharedPreferenceChangeListener listener
    ) {
        if (listener == null) {
            return;
        }
        prefs(context).unregisterOnSharedPreferenceChangeListener(listener);
    }

    private static SharedPreferences prefs(Context context) {
        return context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    private static String safe(String value) {
        return value == null ? "" : value.trim();
    }

    public static final class Snapshot {
        private final String manualInfoTips;
        private final int infoOperation;
        private final int infoOperationMessage;
        private final String shouting;

        Snapshot(String manualInfoTips, int infoOperation, int infoOperationMessage, String shouting) {
            this.manualInfoTips = manualInfoTips == null ? "" : manualInfoTips.trim();
            this.infoOperation = infoOperation;
            this.infoOperationMessage = infoOperationMessage;
            this.shouting = shouting == null ? "" : shouting.trim();
        }

        public String getInfoTips() {
            if (!manualInfoTips.isEmpty()) {
                return manualInfoTips;
            }
            return formatInfoOperation(infoOperation, infoOperationMessage);
        }

        public int getInfoOperation() {
            return infoOperation;
        }

        public int getInfoOperationMessage() {
            return infoOperationMessage;
        }

        public String getShouting() {
            return shouting;
        }
    }

    public interface Listener {
        void onHomeStatusChanged();
    }

    public static final class InfoOperation {
        public static final int NONE = 0;
        public static final int RESOURCE_PARSING = 2;
        public static final int NO_DOWNLOAD_TASK = 9;
        public static final int APK_UPGRADING = 4;
        public static final int RESOURCE_DOWNLOAD_FAILED = 7;
        public static final int RESOURCE_DOWNLOADING = 8;
        public static final int DOWNLOAD_APK_PROGRESS = 12;
        public static final int DOWNLOAD_SOURCEFILE_PROGRESS = 13;
        public static final int UPLOAD_LOG_PROGRESS = 14;

        private InfoOperation() {
        }
    }

    private static String formatInfoOperation(int operation, int operationMsg) {
        switch (operation) {
            case InfoOperation.RESOURCE_PARSING:
            case InfoOperation.NO_DOWNLOAD_TASK:
                return "";
            case InfoOperation.APK_UPGRADING:
                return "APK is upgrading...";
            case InfoOperation.RESOURCE_DOWNLOAD_FAILED:
                return "网络更新的资源解析失败，请检查更新资源";
            case InfoOperation.RESOURCE_DOWNLOADING:
                return "正在下载更新资源...";
            case InfoOperation.DOWNLOAD_APK_PROGRESS:
                return "Download M90 APK,process：" + clampPercent(operationMsg) + "%";
            case InfoOperation.DOWNLOAD_SOURCEFILE_PROGRESS:
                return "Download SourceFile,process：" + clampPercent(operationMsg) + "%";
            case InfoOperation.UPLOAD_LOG_PROGRESS:
                return "UPLOAD LOG procerr：" + clampPercent(operationMsg) + "%";
            default:
                return "";
        }
    }

    private static int clampPercent(int value) {
        if (value < 0) {
            return 0;
        }
        return Math.min(value, 100);
    }
}