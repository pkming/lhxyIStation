package com.lhxy.istationdevice.android11.domain.module;

import android.content.Context;

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
 * 调度、报站、签到、摄像头/DVR、升级、文件。
 */
public final class TerminalModuleHub {
    private final Map<String, TerminalBusinessModule> modules = new LinkedHashMap<>();

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

        register(new DispatchBusinessModule(protocolReplayUseCase, socketClientAdapter, jt808SocketMonitor, dvrSerialDispatchUseCase));
        register(new StationBusinessModule(protocolReplayUseCase, serialPortAdapter, gpsSerialMonitor, dvrSerialDispatchUseCase));
        register(new SignInBusinessModule(protocolReplayUseCase, socketClientAdapter, rfidAdapter, dvrSerialDispatchUseCase));
        register(new CameraDvrBusinessModule(deviceFoundationUseCase, cameraAdapter, gpioAdapter, dvrSerialDispatchUseCase));
        register(new UpgradeBusinessModule(protocolReplayUseCase, socketClientAdapter, systemOps));
        register(new FileBusinessModule(
                exportDirSupplier,
                gpsSerialMonitor,
                jt808SocketMonitor,
                foundationStatusSupplier,
                moduleStatusSupplier
        ));
    }

    private void register(TerminalBusinessModule module) {
        modules.put(module.getKey(), module);
    }

    /**
     * 同步上下文和配置到全部业务模块。
     */
    public void updateContext(Context context, ShellConfig shellConfig) {
        for (TerminalBusinessModule module : modules.values()) {
            module.updateContext(context, shellConfig);
        }
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
            return ModuleRunResult.failure(moduleKey, moduleKey, "模块样例执行失败", "没有找到模块");
        }
        return module.runSample(traceId);
    }

    /**
     * 执行全部模块样例。
     */
    public List<ModuleRunResult> runAll(String traceIdPrefix) {
        List<ModuleRunResult> results = new ArrayList<>();
        for (TerminalBusinessModule module : modules.values()) {
            results.add(module.runSample(traceIdPrefix + "-" + module.getKey()));
        }
        return results;
    }

    /**
     * 执行单个模块的扩展动作。
     */
    public ModuleRunResult runAction(String moduleKey, String actionKey, String traceId) {
        TerminalBusinessModule module = modules.get(moduleKey);
        if (module == null) {
            return ModuleRunResult.failure(moduleKey, moduleKey, "模块动作执行失败", "没有找到模块");
        }
        return module.runAction(actionKey, traceId);
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
}
