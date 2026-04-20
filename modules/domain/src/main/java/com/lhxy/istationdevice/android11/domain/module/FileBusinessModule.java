package com.lhxy.istationdevice.android11.domain.module;

import android.content.Context;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigLoader;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigRepository;
import com.lhxy.istationdevice.android11.domain.debug.DebugBundleExporter;
import com.lhxy.istationdevice.android11.domain.file.StationResourceArchiveUseCase;
import com.lhxy.istationdevice.android11.domain.gps.GpsSerialMonitor;
import com.lhxy.istationdevice.android11.domain.socket.Jt808SocketMonitor;

import java.io.File;
import java.util.function.Supplier;

/**
 * 文件模块
 * <p>
 * 当前先承接运行配置、调试包导出目录和后续导入导出入口。
 */
public final class FileBusinessModule extends AbstractTerminalBusinessModule {
    private final Supplier<File> exportDirSupplier;
    private final GpsSerialMonitor gpsSerialMonitor;
    private final Jt808SocketMonitor jt808SocketMonitor;
    private final Supplier<String> foundationStatusSupplier;
    private final Supplier<String> moduleStatusSupplier;
    private final StationResourceArchiveUseCase stationResourceArchiveUseCase = new StationResourceArchiveUseCase();
    private String lastExportFilePath = "-";
    private String lastRuntimeConfigPath = "-";
    private String lastResourceImportPath = "-";
    private String lastResourceExportPath = "-";

    public FileBusinessModule(
            Supplier<File> exportDirSupplier,
            GpsSerialMonitor gpsSerialMonitor,
            Jt808SocketMonitor jt808SocketMonitor,
            Supplier<String> foundationStatusSupplier,
            Supplier<String> moduleStatusSupplier
    ) {
        this.exportDirSupplier = exportDirSupplier;
        this.gpsSerialMonitor = gpsSerialMonitor;
        this.jt808SocketMonitor = jt808SocketMonitor;
        this.foundationStatusSupplier = foundationStatusSupplier;
        this.moduleStatusSupplier = moduleStatusSupplier;
    }

    @Override
    public String getKey() {
        return "file";
    }

    @Override
    public String getTitle() {
        return "文件";
    }

    @Override
    public String describePurpose() {
        return "承接运行配置、调试包和后续导入导出链路。";
    }

    @Override
    public String describeStatus() {
        Context context = getContext();
        if (context == null) {
            return "当前还没有可用上下文";
        }
        File runtimeConfigFile = ShellConfigLoader.getRuntimeConfigFile(context);
        File exportDir = exportDirSupplier.get();
        lastRuntimeConfigPath = runtimeConfigFile.getAbsolutePath();
        ShellConfig.ResourceImportSettings resourceImportSettings = requireShellConfig().getBasicSetupConfig().getResourceImportSettings();
        ShellConfig.ProtocolLinkageSettings protocolLinkageSettings = requireShellConfig().getBasicSetupConfig().getProtocolLinkageSettings();
        return "runtimeConfig=" + runtimeConfigFile.getAbsolutePath() + (runtimeConfigFile.exists() ? " [存在]" : " [未生成]")
                + "\n- exportDir=" + exportDir.getAbsolutePath() + (exportDir.exists() ? " [存在]" : " [未创建]")
            + "\n- stationResource=" + yesNo(resourceImportSettings.isStationResourceImported())
            + " / line=" + resourceImportSettings.getLineName()
            + " / source=" + resourceImportSettings.getSource()
            + "\n- dispatchOwner=" + protocolLinkageSettings.getDispatchOwner()
            + "\n- importScan=" + stationResourceArchiveUseCase.describeImportLocations(context)
            + "\n- managedResourceDir=" + stationResourceArchiveUseCase.resolveManagedResourceRoot(context).getAbsolutePath()
            + "\n- lastImport=" + lastResourceImportPath
            + "\n- lastResourceExport=" + lastResourceExportPath
                + "\n- lastExport=" + lastExportFilePath
                + "\n- " + describeActionMemory();
    }

    @Override
    public ModuleRunResult runSample(String traceId) {
        return checkFileRuntime(traceId);
    }

    @Override
    public ModuleRunResult runAction(String actionKey, String traceId) {
        if ("export_bundle".equals(actionKey)) {
            return exportBundle(traceId);
        }
        if ("import_station_resources".equals(actionKey)) {
            return importStationResources(traceId);
        }
        if ("export_station_resources".equals(actionKey)) {
            return exportStationResources(traceId);
        }
        if ("reset_runtime_config".equals(actionKey)) {
            return resetRuntimeConfig(traceId);
        }
        return unsupportedAction(actionKey);
    }

    private ModuleRunResult checkFileRuntime(String traceId) {
        Context context = getContext();
        if (context == null) {
            return failureText("文件样例执行失败", "当前没有可用上下文");
        }

        try {
            File runtimeConfigFile = ShellConfigLoader.getRuntimeConfigFile(context);
            File exportDir = exportDirSupplier.get();
            if (!exportDir.exists() && !exportDir.mkdirs()) {
                throw new IllegalStateException("无法创建导出目录: " + exportDir.getAbsolutePath());
            }
            lastRuntimeConfigPath = runtimeConfigFile.getAbsolutePath();
            return success(
                    "文件目录检查已完成",
                    "runtime=" + runtimeConfigFile.getAbsolutePath() + "，exports=" + exportDir.getAbsolutePath()
            );
        } catch (Exception e) {
            return failure("文件样例执行失败", e);
        }
    }

    private ModuleRunResult exportBundle(String traceId) {
        Context context = getContext();
        if (context == null) {
            return failureText("导出调试包失败", "当前没有可用上下文");
        }
        try {
            ShellConfig shellConfig = requireShellConfig();
            File exportFile = DebugBundleExporter.export(
                    context,
                    shellConfig,
                    gpsSerialMonitor,
                    jt808SocketMonitor,
                    foundationStatusSupplier.get(),
                    moduleStatusSupplier.get()
            );
            lastExportFilePath = exportFile.getAbsolutePath();
            return success("已导出调试包", exportFile.getAbsolutePath());
        } catch (Exception e) {
            return failure("导出调试包失败", e);
        }
    }

    private ModuleRunResult importStationResources(String traceId) {
        Context context = getContext();
        if (context == null) {
            return failureText("导入报站资源失败", "当前没有可用上下文");
        }
        try {
            StationResourceArchiveUseCase.OperationResult result = stationResourceArchiveUseCase.importStationResources(context);
            if (!result.isSuccess()) {
                AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, "FileBusinessModule", result.getSummary() + ": " + result.getDetail(), traceId);
                return failureText(result.getSummary(), result.getDetail());
            }

            lastResourceImportPath = result.getArchiveFile() == null ? "-" : result.getArchiveFile().getAbsolutePath();
            ShellConfig updated = buildShellConfigWithImportedResources(requireShellConfig(), result);
            ShellConfigRepository.save(context, updated);
            updateContext(context, updated);
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, "FileBusinessModule", result.getSummary() + ": " + result.getDetail(), traceId);
            return success(result.getSummary(), result.getDetail());
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, "FileBusinessModule", "导入报站资源失败: " + emptyAsDash(e.getMessage()), traceId);
            return failure("导入报站资源失败", e);
        }
    }

    private ModuleRunResult exportStationResources(String traceId) {
        Context context = getContext();
        if (context == null) {
            return failureText("导出报站资源失败", "当前没有可用上下文");
        }
        try {
            StationResourceArchiveUseCase.OperationResult result = stationResourceArchiveUseCase.exportStationResources(context, exportDirSupplier.get());
            if (!result.isSuccess()) {
                AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, "FileBusinessModule", result.getSummary() + ": " + result.getDetail(), traceId);
                return failureText(result.getSummary(), result.getDetail());
            }
            lastResourceExportPath = result.getArchiveFile() == null ? "-" : result.getArchiveFile().getAbsolutePath();
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, "FileBusinessModule", result.getSummary() + ": " + result.getDetail(), traceId);
            return success(result.getSummary(), result.getDetail());
        } catch (Exception e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, "FileBusinessModule", "导出报站资源失败: " + emptyAsDash(e.getMessage()), traceId);
            return failure("导出报站资源失败", e);
        }
    }

    private ModuleRunResult resetRuntimeConfig(String traceId) {
        Context context = getContext();
        if (context == null) {
            return failureText("重置运行配置失败", "当前没有可用上下文");
        }
        try {
            File runtimeConfigFile = ShellConfigLoader.resetRuntimeConfig(context);
            lastRuntimeConfigPath = runtimeConfigFile.getAbsolutePath();
            return success("已重置运行配置", runtimeConfigFile.getAbsolutePath());
        } catch (Exception e) {
            return failure("重置运行配置失败", e);
        }
    }

    private ShellConfig buildShellConfigWithImportedResources(ShellConfig current, StationResourceArchiveUseCase.OperationResult result) {
        ShellConfig.BasicSetupConfig basicSetup = current.getBasicSetupConfig();
        return new ShellConfig(
                current.getDeviceProfile(),
                current.getConfigVersion(),
                "runtime:" + ShellConfigRepository.getRuntimeConfigFile(getContext()).getAbsolutePath(),
                current.getSerialChannels(),
                current.getSocketChannels(),
                current.getGpioConfig(),
                current.getCameraConfig(),
                current.getRfidConfig(),
                current.getSystemConfig(),
                current.getDebugReplay(),
                new ShellConfig.BasicSetupConfig(
                        basicSetup.getNewspaperSettings(),
                        basicSetup.getNetworkSettings(),
                        basicSetup.getSerialSettings(),
                        basicSetup.getTtsSettings(),
                        basicSetup.getLanguageSettings(),
                        basicSetup.getOtherSettings(),
                        basicSetup.getWirelessSettings(),
                        new ShellConfig.ResourceImportSettings(
                                true,
                                result.getArchiveFile() == null ? "-" : result.getArchiveFile().getAbsolutePath(),
                                result.getLineName(),
                                System.currentTimeMillis()
                        ),
                        basicSetup.getProtocolLinkageSettings()
                )
        );
    }
}
