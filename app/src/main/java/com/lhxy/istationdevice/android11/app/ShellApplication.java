package com.lhxy.istationdevice.android11.app;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.tencent.tinker.lib.listener.DefaultPatchListener;
import com.tencent.tinker.lib.patch.UpgradePatch;
import com.tencent.tinker.lib.reporter.DefaultLoadReporter;
import com.tencent.tinker.lib.reporter.DefaultPatchReporter;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.entry.DefaultApplicationLike;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 应用入口
 */
public class ShellApplication extends DefaultApplicationLike {
    private static final String TAG = "ShellApplication";
    public static boolean isUserPassword = false;

    public ShellApplication(
            Application application,
            int tinkerFlags,
            boolean tinkerLoadVerifyFlag,
            long applicationStartElapsedTime,
            long applicationStartMillisTime,
            Intent tinkerResultIntent
    ) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

    @Override
    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        installTinker();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Class<?> startupClass = Class.forName("com.lhxy.istationdevice.android11.app.ShellAppStartup");
            Method initializeMethod = startupClass.getMethod("initialize", Application.class);
            initializeMethod.invoke(null, getApplication());
        } catch (Exception e) {
            throw buildIllegalState("应用启动初始化失败", e);
        }
    }

    private void installTinker() {
        try {
            Application application = getApplication();
            TinkerInstaller.install(
                this,
                new DefaultLoadReporter(application),
                new DefaultPatchReporter(application),
                new DefaultPatchListener(application),
                ShellTinkerResultService.class,
                new UpgradePatch()
            );
            Log.i(TAG, "Tinker 安装完成");
        } catch (Exception e) {
            throw buildIllegalState("Tinker 安装失败", e);
        }
    }

    private IllegalStateException buildIllegalState(String message, Exception exception) {
        Throwable cause = exception;
        if (exception instanceof InvocationTargetException && ((InvocationTargetException) exception).getTargetException() != null) {
            cause = ((InvocationTargetException) exception).getTargetException();
        }
        Log.e(TAG, message, cause);
        return new IllegalStateException(message, cause);
    }
}
