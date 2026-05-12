package com.lhxy.istationdevice.android11.app;

import android.app.Application;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.core.TraceIds;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigRepository;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigLoader;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigValidator;
import com.lhxy.istationdevice.android11.domain.file.StationResourceArchiveUseCase;
import com.lhxy.istationdevice.android11.runtime.ShellRuntime;

import java.io.File;
import java.util.List;

/**
 * 应用入口
 */
public class ShellApplication extends Application {
    public static boolean isUserPassword = false;

    @Override
    public void onCreate() {
        super.onCreate();
        AppLogCenter.init(this);
        String traceId = TraceIds.next("app");
        try {
            File runtimeConfigFile = ShellConfigLoader.bootstrapRuntimeConfig(this);
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, "ShellApplication", "运行配置文件: " + runtimeConfigFile.getAbsolutePath(), traceId);
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, "ShellApplication", "初始化运行配置文件失败: " + e.getMessage(), traceId);
        }
        ensureBundledStationResources(traceId);
        ShellConfig shellConfig = ShellConfigRepository.get(this);
        AppLanguageManager.apply(shellConfig.getBasicSetupConfig().getLanguageSettings().getLanguageCode());
        ShellRuntime shellRuntime = ShellRuntime.get();
        shellRuntime.applyConfig(this, shellConfig);
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, "ShellApplication", "Android 11 新壳启动", traceId);
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, "ShellApplication", "加载运行配置: " + shellConfig.getDeviceProfile() + " / " + shellConfig.getConfigVersion(), traceId);
        List<String> issues = ShellConfigValidator.validate(shellConfig);
        if (issues.isEmpty()) {
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, "ShellApplication", "运行配置检查通过", traceId);
        } else {
            for (String issue : issues) {
                AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, "ShellApplication", "运行配置问题: " + issue, traceId);
            }
        }
        shellRuntime.getJt808SocketMonitor().syncDefaultChannels(shellRuntime.getSocketClientAdapter(), shellConfig, traceId + "-socket-monitor");
    }

    private void ensureBundledStationResources(String traceId) {
        StationResourceArchiveUseCase stationResourceArchiveUseCase = new StationResourceArchiveUseCase();
        File sourceRoot = stationResourceArchiveUseCase.resolveManagedSourceRoot(this);
        File lineInfoFile = new File(sourceRoot, "Bus/lineInfo.csv");
        if (lineInfoFile.exists() && lineInfoFile.isFile()) {
            return;
        }
        try {
            StationResourceArchiveUseCase.OperationResult result = stationResourceArchiveUseCase.importStationResources(this);
            if (result.isSuccess()) {
                AppLogCenter.log(
                        LogCategory.BIZ,
                        LogLevel.INFO,
                        "ShellApplication",
                        "已自动初始化报站资源: " + result.getSummary(),
                        traceId + "-station-resource-bootstrap"
                );
            } else {
                AppLogCenter.log(
                        LogCategory.ERROR,
                        LogLevel.WARN,
                        "ShellApplication",
                        "自动初始化报站资源失败: " + result.getSummary() + " / " + result.getDetail(),
                        traceId + "-station-resource-bootstrap"
                );
            }
        } catch (Exception e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.WARN,
                    "ShellApplication",
                    "自动初始化报站资源异常: " + e.getMessage(),
                    traceId + "-station-resource-bootstrap"
            );
        }
    }
}
