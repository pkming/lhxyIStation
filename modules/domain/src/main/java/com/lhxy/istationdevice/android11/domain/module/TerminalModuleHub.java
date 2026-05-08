package com.lhxy.istationdevice.android11.domain.module;

import android.content.Context;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.CameraAdapter;
import com.lhxy.istationdevice.android11.deviceapi.GpioAdapter;
import com.lhxy.istationdevice.android11.deviceapi.RfidAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SocketClientAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SystemOps;
import com.lhxy.istationdevice.android11.domain.DeviceFoundationUseCase;
import com.lhxy.istationdevice.android11.domain.ProtocolReplayUseCase;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.dispatch.DvrSerialDispatchUseCase;
import com.lhxy.istationdevice.android11.domain.dvr.DvrSerialMonitor;
import com.lhxy.istationdevice.android11.domain.gps.GpsSerialMonitor;
import com.lhxy.istationdevice.android11.domain.socket.Jt808SocketMonitor;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 业务模块统一编排中心
 * <p>
 * 新壳后面的业务都从这里挂：
 * 调度、GPS、报站、签到、摄像头/DVR、升级、文件。
 */
public final class TerminalModuleHub {
    private static final String TAG = "TerminalModuleHub";
    private final Map<String, TerminalBusinessModule> modules = new LinkedHashMap<>();
    private final DvrSerialMonitor dvrSerialMonitor;

    public TerminalModuleHub(
            SerialPortAdapter serialPortAdapter,
            SocketClientAdapter socketClientAdapter,
            GpioAdapter gpioAdapter,
            CameraAdapter cameraAdapter,
            RfidAdapter rfidAdapter,
            SystemOps systemOps,
            GpsSerialMonitor gpsSerialMonitor,
            Jt808SocketMonitor jt808SocketMonitor,
            Supplier<File> exportDirSupplier,
            Supplier<String> foundationStatusSupplier,
            Supplier<String> moduleStatusSupplier
    ) {
        ProtocolReplayUseCase protocolReplayUseCase = new ProtocolReplayUseCase();
        DeviceFoundationUseCase deviceFoundationUseCase = new DeviceFoundationUseCase();
        DvrSerialDispatchUseCase dvrSerialDispatchUseCase = new DvrSerialDispatchUseCase(serialPortAdapter);
        DispatchBusinessModule dispatchModule =
            new DispatchBusinessModule(protocolReplayUseCase, socketClientAdapter, gpioAdapter, jt808SocketMonitor, dvrSerialDispatchUseCase);
        GpsBusinessModule gpsModule =
            new GpsBusinessModule(serialPortAdapter, gpsSerialMonitor, systemOps);
        StationBusinessModule stationModule =
                new StationBusinessModule(protocolReplayUseCase, serialPortAdapter, gpioAdapter, gpsSerialMonitor, dispatchModule, dvrSerialDispatchUseCase);
        SignInBusinessModule signInModule =
                new SignInBusinessModule(protocolReplayUseCase, socketClientAdapter, rfidAdapter, dvrSerialDispatchUseCase);
        dispatchModule.attachStateProviders(signInModule::getSignInState, stationModule::getStationState);
        dvrSerialMonitor = new DvrSerialMonitor(
            dispatchModule.getDispatchState(),
            signInModule.getSignInState(),
            dispatchModule::onDispatchRequestReceived,
            dispatchModule::onDispatchNoticeReceived
        );
        CameraDvrBusinessModule cameraModule = new CameraDvrBusinessModule(
                deviceFoundationUseCase,
                cameraAdapter,
                gpioAdapter,
                serialPortAdapter,
                dvrSerialDispatchUseCase,
                dvrSerialMonitor
        );

        register(dispatchModule);
        register(gpsModule);
        register(stationModule);
        register(signInModule);
        register(cameraModule);
        register(new UpgradeBusinessModule(protocolReplayUseCase, socketClientAdapter, systemOps, jt808SocketMonitor));
        register(new FileBusinessModule(
                exportDirSupplier,
                gpsSerialMonitor,
                jt808SocketMonitor,
                foundationStatusSupplier,
                moduleStatusSupplier
        ));
    }

    public DvrSerialMonitor getDvrSerialMonitor() {
        return dvrSerialMonitor;
    }

    private void register(TerminalBusinessModule module) {
        modules.put(module.getKey(), module);
    }

    /**
     * 同步上下文和配置到全部业务模块。
     */
    public void updateContext(Context context, ShellConfig shellConfig) {
        AppLogCenter.log(
                LogCategory.BIZ,
                LogLevel.DEBUG,
                TAG,
                "开始刷新业务模块上下文 count=" + modules.size(),
                "module-hub-context"
        );
        for (TerminalBusinessModule module : modules.values()) {
            module.updateContext(context, shellConfig);
        }
        AppLogCenter.log(
                LogCategory.BIZ,
                LogLevel.DEBUG,
                TAG,
                "业务模块上下文刷新完成 count=" + modules.size(),
                "module-hub-context"
        );
    }

    /**
     * 返回全部模块列表。
     */
    public List<TerminalBusinessModule> getModules() {
        return new ArrayList<>(modules.values());
    }

    /**
     * 按 key 查模块。
     */
    public TerminalBusinessModule findModule(String moduleKey) {
        return modules.get(moduleKey);
    }

    /**
     * 执行单个模块样例。
     */
    public ModuleRunResult runModule(String moduleKey, String traceId) {
        TerminalBusinessModule module = modules.get(moduleKey);
        if (module == null) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.WARN, TAG, "模块样例执行失败，未找到模块 module=" + safeText(moduleKey), traceId);
            return ModuleRunResult.failure(moduleKey, moduleKey, "模块样例执行失败", "没有找到模块");
        }
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "开始执行模块样例 module=" + module.getKey(), traceId);
        try {
            ModuleRunResult result = module.runSample(traceId);
            logResult("模块样例执行完成", module.getKey(), result.isSuccess(), result.describeInline(), traceId);
            return result;
        } catch (RuntimeException e) {
            AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "模块样例执行异常 module=" + module.getKey() + " / error=" + safeErrorMessage(e), traceId);
            throw e;
        }
    }

    /**
     * 执行全部模块样例。
     */
    public List<ModuleRunResult> runAll(String traceIdPrefix) {
        List<ModuleRunResult> results = new ArrayList<>();
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "开始批量执行模块样例 count=" + modules.size(), traceIdPrefix + "-all");
        for (TerminalBusinessModule module : modules.values()) {
            String traceId = traceIdPrefix + "-" + module.getKey();
            AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "批量执行模块样例 module=" + module.getKey(), traceId);
            try {
                ModuleRunResult result = module.runSample(traceId);
                results.add(result);
                logResult("批量模块样例完成", module.getKey(), result.isSuccess(), result.describeInline(), traceId);
            } catch (RuntimeException e) {
                AppLogCenter.log(LogCategory.ERROR, LogLevel.ERROR, TAG, "批量模块样例异常 module=" + module.getKey() + " / error=" + safeErrorMessage(e), traceId);
                throw e;
            }
        }
        AppLogCenter.log(LogCategory.BIZ, LogLevel.INFO, TAG, "批量执行模块样例结束 successCount=" + countSuccess(results) + " / total=" + results.size(), traceIdPrefix + "-all");
        return results;
    }

    /**
     * 执行单个模块的扩展动作。
     */
    public ModuleRunResult runAction(String moduleKey, String actionKey, String traceId) {
        TerminalBusinessModule module = modules.get(moduleKey);
        if (module == null) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.WARN,
                    TAG,
                    "模块动作执行失败，未找到模块 module=" + safeText(moduleKey) + " / action=" + safeText(actionKey),
                    traceId
            );
            return ModuleRunResult.failure(moduleKey, moduleKey, "模块动作执行失败", "没有找到模块");
        }
        AppLogCenter.log(
                LogCategory.BIZ,
                LogLevel.INFO,
                TAG,
                "开始执行模块动作 module=" + module.getKey() + " / action=" + safeText(actionKey),
                traceId
        );
        try {
            ModuleRunResult result = module.runAction(actionKey, traceId);
            logResult("模块动作执行完成", module.getKey(), result.isSuccess(), result.describeInline(), traceId);
            return result;
        } catch (RuntimeException e) {
            AppLogCenter.log(
                    LogCategory.ERROR,
                    LogLevel.ERROR,
                    TAG,
                    "模块动作执行异常 module=" + module.getKey() + " / action=" + safeText(actionKey) + " / error=" + safeErrorMessage(e),
                    traceId
            );
            throw e;
        }
    }

    /**
     * 输出业务模块状态摘要。
     */
    public String describeStatus() {
        StringBuilder builder = new StringBuilder("业务模块状态:");
        for (TerminalBusinessModule module : modules.values()) {
            builder.append("\n- ")
                    .append(module.getTitle())
                    .append(" (")
                    .append(module.getKey())
                    .append(") -> ")
                    .append(compact(module.describeStatus()));
        }
        return builder.toString();
    }

    /**
     * 输出业务模块详细状态。
     */
    public String describeDetails() {
        StringBuilder builder = new StringBuilder("业务模块详情:");
        for (TerminalBusinessModule module : modules.values()) {
            builder.append("\n\n[")
                    .append(module.getTitle())
                    .append(" / ")
                    .append(module.getKey())
                    .append("]");
            String purpose = module.describePurpose();
            if (purpose != null && !purpose.trim().isEmpty()) {
                builder.append("\n- 作用: ").append(purpose.trim());
            }
            builder.append("\n").append(module.describeStatus());
        }
        return builder.toString();
    }

    /**
     * 把一组执行结果转成文本。
     */
    public String describeResults(List<ModuleRunResult> results) {
        StringBuilder builder = new StringBuilder("模块执行结果:");
        if (results == null || results.isEmpty()) {
            return builder.append("\n- 当前没有执行结果").toString();
        }
        for (ModuleRunResult result : results) {
            builder.append("\n- ").append(result.describeInline());
        }
        return builder.toString();
    }

    private String compact(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "-";
        }
        String[] lines = text.trim().split("\\n");
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (String line : lines) {
            String cleaned = line == null ? "" : line.trim();
            if (cleaned.isEmpty()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(" | ");
            }
            builder.append(cleaned.replace("- ", ""));
            count++;
            if (count >= 3) {
                break;
            }
        }
        return builder.toString();
    }

    private void logResult(String prefix, String moduleKey, boolean success, String message, String traceId) {
        AppLogCenter.log(
                success ? LogCategory.BIZ : LogCategory.ERROR,
                success ? LogLevel.INFO : LogLevel.WARN,
                TAG,
                prefix + " module=" + moduleKey + " / " + safeText(message),
                traceId
        );
    }

    private int countSuccess(List<ModuleRunResult> results) {
        int count = 0;
        for (ModuleRunResult result : results) {
            if (result != null && result.isSuccess()) {
                count++;
            }
        }
        return count;
    }

    private String safeErrorMessage(RuntimeException exception) {
        if (exception == null || exception.getMessage() == null || exception.getMessage().trim().isEmpty()) {
            return exception == null ? "未知错误" : exception.getClass().getSimpleName();
        }
        return exception.getMessage().trim();
    }

    private String safeText(String text) {
        return text == null || text.trim().isEmpty() ? "-" : text.trim();
    }
}
