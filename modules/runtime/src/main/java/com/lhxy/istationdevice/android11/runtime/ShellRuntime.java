package com.lhxy.istationdevice.android11.runtime;

import android.content.Context;

import com.lhxy.istationdevice.android11.deviceapi.CameraAdapter;
import com.lhxy.istationdevice.android11.deviceapi.GpioAdapter;
import com.lhxy.istationdevice.android11.deviceapi.RfidAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SocketClientAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SystemOps;
import com.lhxy.istationdevice.android11.devicem90.M90ManagedCameraAdapter;
import com.lhxy.istationdevice.android11.devicem90.M90ManagedGpioAdapter;
import com.lhxy.istationdevice.android11.devicem90.M90ManagedRfidAdapter;
import com.lhxy.istationdevice.android11.devicem90.M90ManagedSerialPortAdapter;
import com.lhxy.istationdevice.android11.devicem90.M90ManagedSocketClientAdapter;
import com.lhxy.istationdevice.android11.devicem90.M90ManagedSystemOps;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.gps.GpsSerialMonitor;
import com.lhxy.istationdevice.android11.domain.module.TerminalModuleHub;
import com.lhxy.istationdevice.android11.domain.socket.Jt808SocketMonitor;

import java.io.File;

/**
 * 新壳共享运行时
 * <p>
 * 首页、调试页、后面接入的业务页都统一从这里拿设备实例和运行状态，
 * 避免每个页面自己 new 一套适配器，导致串口、Socket、GPS 监听互相看不到。
 */
public final class ShellRuntime {
    private static final ShellRuntime INSTANCE = new ShellRuntime();

    private final M90ManagedSerialPortAdapter serialPortAdapter = new M90ManagedSerialPortAdapter();
    private final M90ManagedSocketClientAdapter socketClientAdapter = new M90ManagedSocketClientAdapter();
    private final M90ManagedGpioAdapter gpioAdapter = new M90ManagedGpioAdapter();
    private final M90ManagedCameraAdapter cameraAdapter = new M90ManagedCameraAdapter();
    private final M90ManagedRfidAdapter rfidAdapter = new M90ManagedRfidAdapter();
    private final M90ManagedSystemOps systemOps = new M90ManagedSystemOps();
    private final GpsSerialMonitor gpsSerialMonitor = new GpsSerialMonitor();
    private final Jt808SocketMonitor jt808SocketMonitor = new Jt808SocketMonitor();
    private final TerminalModuleHub moduleHub;
    private volatile ShellConfig activeConfig;
    private volatile Context appContext;

    private ShellRuntime() {
        moduleHub = new TerminalModuleHub(
                serialPortAdapter,
                socketClientAdapter,
                gpioAdapter,
                cameraAdapter,
                rfidAdapter,
                systemOps,
                gpsSerialMonitor,
                jt808SocketMonitor,
                this::resolveExportDir,
                this::describeFoundationStatus,
                this::describeModuleStatus
        );
    }

    /**
     * 返回全局唯一运行时实例。
     */
    public static ShellRuntime get() {
        return INSTANCE;
    }

    /**
     * 返回共享串口适配器。
     */
    public SerialPortAdapter getSerialPortAdapter() {
        return serialPortAdapter;
    }

    /**
     * 返回共享 Socket 适配器。
     */
    public SocketClientAdapter getSocketClientAdapter() {
        return socketClientAdapter;
    }

    /**
     * 返回共享 GPIO 适配器。
     */
    public GpioAdapter getGpioAdapter() {
        return gpioAdapter;
    }

    /**
     * 返回共享 Camera 适配器。
     */
    public CameraAdapter getCameraAdapter() {
        return cameraAdapter;
    }

    /**
     * 返回共享 RFID 适配器。
     */
    public RfidAdapter getRfidAdapter() {
        return rfidAdapter;
    }

    /**
     * 返回共享 GPS 串口监视器。
     */
    public GpsSerialMonitor getGpsSerialMonitor() {
        return gpsSerialMonitor;
    }

    /**
     * 返回共享 Socket 协议监视器。
     */
    public Jt808SocketMonitor getJt808SocketMonitor() {
        return jt808SocketMonitor;
    }

    /**
     * 返回系统能力封装。
     */
    public SystemOps getSystemOps() {
        return systemOps;
    }

    /**
     * 返回统一业务模块编排中心。
     */
    public TerminalModuleHub getModuleHub() {
        return moduleHub;
    }

    /**
     * 返回当前生效配置。
     */
    public ShellConfig getActiveConfig() {
        return activeConfig;
    }

    /**
     * 把当前配置同步到所有底座适配器。
     */
    public synchronized void applyConfig(Context context, ShellConfig shellConfig) {
        appContext = context == null ? null : context.getApplicationContext();
        activeConfig = shellConfig;
        if (shellConfig == null) {
            return;
        }
        gpioAdapter.updateConfig(shellConfig.getGpioConfig());
        cameraAdapter.updateConfig(appContext, shellConfig.getCameraConfig());
        rfidAdapter.updateConfig(shellConfig.getRfidConfig());
        systemOps.updateConfig(shellConfig.getSystemConfig());
        moduleHub.updateContext(appContext, shellConfig);
    }

    /**
     * 输出底座当前状态。
     */
    public String describeFoundationStatus() {
        StringBuilder builder = new StringBuilder("底座状态:");
        builder.append("\n- ").append(gpioAdapter.describeStatus());
        builder.append("\n- ").append(cameraAdapter.describeStatus());
        builder.append("\n- ").append(rfidAdapter.describeStatus());
        builder.append("\n- ").append(systemOps.describeStatus());
        return builder.toString();
    }

    /**
     * 输出当前业务模块摘要。
     */
    public String describeModuleStatus() {
        return moduleHub.describeStatus();
    }

    private File resolveExportDir() {
        Context currentContext = appContext;
        if (currentContext == null) {
            return new File("/data/local/tmp/istation-shell-exports");
        }
        File exportDir = currentContext.getExternalFilesDir("exports");
        return exportDir == null ? new File(currentContext.getFilesDir(), "exports") : exportDir;
    }
}
