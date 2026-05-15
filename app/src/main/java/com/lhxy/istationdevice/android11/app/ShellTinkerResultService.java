package com.lhxy.istationdevice.android11.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LegacyHomeStatusRepository;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.domain.upgrade.TinkerHotUpdateStateStore;
import com.tencent.tinker.lib.service.DefaultTinkerResultService;
import com.tencent.tinker.lib.service.PatchResult;
import com.tencent.tinker.lib.util.TinkerServiceInternals;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public final class ShellTinkerResultService extends DefaultTinkerResultService {
    private static final String TAG = "ShellTinkerResult";
    private static final int RESTART_DELAY_MILLIS = 500;
    private static final int RESTART_REQUEST_CODE = 1024;

    @Override
    public void onPatchResult(PatchResult result) {
        Context context = getApplicationContext();
        AppLogCenter.init(context);
        String traceId = firstNonBlank(TinkerHotUpdateStateStore.getPendingTraceId(context), "tinker-result");
        if (result == null) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "结果服务收到空 PatchResult", traceId);
            return;
        }
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "结果服务已进入 isSuccess=" + result.isSuccess, traceId);
        TinkerServiceInternals.killTinkerPatchServiceProcess(context);
        if (!result.isSuccess) {
            TinkerHotUpdateStateStore.clearPending(context);
            String failureDetail = resolveFailureDetail(result);
            LegacyHomeStatusRepository.setInfoTips(context, "热更新补丁合成失败");
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "热更新补丁合成失败: " + failureDetail, traceId);
            return;
        }

        String patchVersion = firstNonBlank(
            TinkerHotUpdateStateStore.getPendingPatchVersion(context),
            result.patchVersion
        );
        String patchMd5 = firstNonBlank(
            TinkerHotUpdateStateStore.getPendingPatchMd5(context),
            computeMd5Safe(result.rawPatchFilePath)
        );
        TinkerHotUpdateStateStore.markApplied(context, patchVersion, patchMd5);
        AppLogCenter.log(
                LogCategory.BIZ,
                LogLevel.INFO,
                TAG,
                "热更新补丁合成成功 patchVersion=" + emptyAsDash(patchVersion) + " / md5=" + emptyAsDash(patchMd5),
            traceId
        );
        deleteRawPatchFile(resolveRawPatchFile(result));
        LegacyHomeStatusRepository.setInfoTips(context, "热更新补丁已生效，应用即将重启");
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "准备调度应用重启", traceId);
        scheduleAppRestart(context);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    @Override
    public boolean checkIfNeedKill(PatchResult result) {
        return result != null && result.isSuccess;
    }

    private void scheduleAppRestart(Context context) {
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        if (launchIntent == null) {
            return;
        }
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int flags = PendingIntent.FLAG_ONE_SHOT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, RESTART_REQUEST_CODE, launchIntent, flags);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) {
            return;
        }
        long triggerAtMillis = System.currentTimeMillis() + RESTART_DELAY_MILLIS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC, triggerAtMillis, pendingIntent);
        } else {
            alarmManager.setExact(AlarmManager.RTC, triggerAtMillis, pendingIntent);
        }
    }

    private File resolveRawPatchFile(PatchResult result) {
        if (result.rawPatchFilePath == null || result.rawPatchFilePath.trim().isEmpty()) {
            return null;
        }
        return new File(result.rawPatchFilePath.trim());
    }

    private String computeMd5Safe(String filePath) {
        if (filePath == null || filePath.trim().isEmpty()) {
            return "";
        }
        File file = new File(filePath.trim());
        if (!file.exists() || !file.isFile()) {
            return "";
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            try (InputStream inputStream = new FileInputStream(file)) {
                byte[] buffer = new byte[8192];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    digest.update(buffer, 0, length);
                }
            }
            byte[] bytes = digest.digest();
            StringBuilder builder = new StringBuilder(bytes.length * 2);
            for (byte value : bytes) {
                String hex = Integer.toHexString(value & 0xFF);
                if (hex.length() == 1) {
                    builder.append('0');
                }
                builder.append(hex);
            }
            return builder.toString();
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "计算补丁 MD5 失败: " + e.getMessage(), firstNonBlank(TinkerHotUpdateStateStore.getPendingTraceId(getApplicationContext()), "tinker-result"));
            return "";
        }
    }

    private String resolveFailureDetail(PatchResult result) {
        if (result.e != null && result.e.getMessage() != null && !result.e.getMessage().trim().isEmpty()) {
            return result.e.getMessage().trim();
        }
        return result.toString().replace('\n', ' ').trim();
    }

    private String emptyAsDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }

    private String firstNonBlank(String first, String second) {
        if (first != null && !first.trim().isEmpty()) {
            return first.trim();
        }
        if (second != null && !second.trim().isEmpty()) {
            return second.trim();
        }
        return "";
    }
}