package com.lhxy.istationdevice.android11.domain;

import com.lhxy.istationdevice.android11.deviceapi.CameraAdapter;
import com.lhxy.istationdevice.android11.deviceapi.GpioAdapter;
import com.lhxy.istationdevice.android11.deviceapi.RfidAdapter;
import com.lhxy.istationdevice.android11.deviceapi.SystemOps;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;

/**
 * 底座设备调试用例
 * <p>
 * GPIO、Camera、RFID、SystemOps 先统一收在这里，
 * 调试页只负责收集输入和刷新状态，不直接写设备调用细节。
 */
public final class DeviceFoundationUseCase {
    /**
     * 读取一个 GPIO 当前值。
     */
    public int readGpio(GpioAdapter gpioAdapter, ShellConfig shellConfig, String gpioKey, String traceId) {
        requireGpio(shellConfig, gpioKey);
        return gpioAdapter.read(gpioKey, traceId);
    }

    /**
     * 写一个 GPIO 值。
     */
    public void writeGpio(GpioAdapter gpioAdapter, ShellConfig shellConfig, String gpioKey, int value, String traceId) {
        requireGpio(shellConfig, gpioKey);
        gpioAdapter.write(gpioKey, value, traceId);
    }

    /**
     * 打开一个 Camera 逻辑通道。
     */
    public void openCamera(CameraAdapter cameraAdapter, ShellConfig shellConfig, String cameraChannelKey, String traceId) {
        requireCamera(shellConfig, cameraChannelKey);
        cameraAdapter.open(cameraChannelKey, traceId);
    }

    /**
     * 关闭一个 Camera 逻辑通道。
     */
    public void closeCamera(CameraAdapter cameraAdapter, ShellConfig shellConfig, String cameraChannelKey, String traceId) {
        requireCamera(shellConfig, cameraChannelKey);
        cameraAdapter.close(cameraChannelKey, traceId);
    }

    /**
     * 读取一次 RFID。
     */
    public String readRfid(RfidAdapter rfidAdapter, String traceId) {
        return rfidAdapter.readCard(traceId);
    }

    /**
     * 把系统时间同步到当前时间。
     */
    public long syncSystemTimeNow(SystemOps systemOps, String traceId) {
        long now = System.currentTimeMillis();
        systemOps.setSystemTime(now, traceId);
        return now;
    }

    /**
     * 请求系统重启。
     */
    public void requestReboot(SystemOps systemOps, String reason, String traceId) {
        systemOps.reboot(reason, traceId);
    }

    private void requireGpio(ShellConfig shellConfig, String gpioKey) {
        if (shellConfig == null) {
            throw new IllegalStateException("当前没有可用配置");
        }
        shellConfig.getGpioConfig().requirePin(gpioKey);
    }

    private void requireCamera(ShellConfig shellConfig, String cameraChannelKey) {
        if (shellConfig == null) {
            throw new IllegalStateException("当前没有可用配置");
        }
        shellConfig.getCameraConfig().requireChannel(cameraChannelKey);
    }
}
