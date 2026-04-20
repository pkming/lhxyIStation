package com.lhxy.istationdevice.android11.domain.config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 终端配置校验器
 * <p>
 * 用来在启动期和调试期尽早发现配置缺口，避免真机联调时才发现口位或 host 没配。
 */
public final class ShellConfigValidator {
    private ShellConfigValidator() {
    }

    /**
     * 校验当前配置并返回问题列表。
     */
    public static List<String> validate(ShellConfig shellConfig) {
        List<String> issues = new ArrayList<>();
        if (shellConfig == null) {
            issues.add("配置对象为空");
            return issues;
        }

        if (shellConfig.getDeviceProfile() == null || shellConfig.getDeviceProfile().trim().isEmpty()) {
            issues.add("deviceProfile 为空");
        }
        if (shellConfig.getConfigVersion() == null || shellConfig.getConfigVersion().trim().isEmpty()) {
            issues.add("configVersion 为空");
        }

        for (Map.Entry<String, ShellConfig.SerialChannel> entry : shellConfig.getSerialChannels().entrySet()) {
            ShellConfig.SerialChannel channel = entry.getValue();
            if (channel.getPortName() == null || channel.getPortName().trim().isEmpty()) {
                issues.add("串口 " + entry.getKey() + " 没有配置 portName");
            }
            if (channel.getBaudRate() <= 0) {
                issues.add("串口 " + entry.getKey() + " 的 baudRate 非法");
            }
            if (channel.getMode() == null) {
                issues.add("串口 " + entry.getKey() + " 没有配置 mode");
            }
        }

        for (Map.Entry<String, ShellConfig.SocketChannel> entry : shellConfig.getSocketChannels().entrySet()) {
            ShellConfig.SocketChannel channel = entry.getValue();
            if (channel.getChannelName() == null || channel.getChannelName().trim().isEmpty()) {
                issues.add("Socket " + entry.getKey() + " 没有配置 channelName");
            }
            if (channel.getMode() == com.lhxy.istationdevice.android11.deviceapi.SocketMode.REAL) {
                if (channel.getHost() == null || channel.getHost().trim().isEmpty() || channel.getHost().contains("pending")) {
                    issues.add("Socket " + entry.getKey() + " 没有配置可用 host");
                }
                if (channel.getPort() <= 0) {
                    issues.add("Socket " + entry.getKey() + " 的 port 非法");
                }
            }
        }

        ShellConfig.GpioConfig gpioConfig = shellConfig.getGpioConfig();
        if (gpioConfig.getPins().isEmpty()) {
            issues.add("gpio.pins 为空");
        }
        for (Map.Entry<String, ShellConfig.GpioPin> entry : gpioConfig.getPins().entrySet()) {
            ShellConfig.GpioPin pin = entry.getValue();
            if (pin.getPinId() == null || pin.getPinId().trim().isEmpty()) {
                issues.add("GPIO " + entry.getKey() + " 没有配置 pinId");
            }
            if (gpioConfig.getMode() == com.lhxy.istationdevice.android11.deviceapi.DeviceMode.REAL
                    && (pin.getValuePath() == null || pin.getValuePath().trim().isEmpty())) {
                issues.add("GPIO " + entry.getKey() + " 在 real 模式下没有配置 valuePath");
            }
        }

        ShellConfig.CameraConfig cameraConfig = shellConfig.getCameraConfig();
        if (cameraConfig.getChannels().isEmpty()) {
            issues.add("camera.channels 为空");
        }
        for (Map.Entry<String, ShellConfig.CameraChannel> entry : cameraConfig.getChannels().entrySet()) {
            ShellConfig.CameraChannel channel = entry.getValue();
            if (channel.getCameraId() == null || channel.getCameraId().trim().isEmpty()) {
                issues.add("Camera " + entry.getKey() + " 没有配置 cameraId");
            }
        }

        ShellConfig.RfidConfig rfidConfig = shellConfig.getRfidConfig();
        if (rfidConfig.getMode() == com.lhxy.istationdevice.android11.deviceapi.DeviceMode.REAL
                && isEmpty(rfidConfig.getInputFilePath())
                && isEmpty(rfidConfig.getReadCommand())
                && isEmpty(rfidConfig.getMockCardNo())) {
            issues.add("RFID 在 real 模式下没有可用的文件、命令或兜底卡号");
        }

        ShellConfig.SystemConfig systemConfig = shellConfig.getSystemConfig();
        if (systemConfig.getMode() == com.lhxy.istationdevice.android11.deviceapi.DeviceMode.REAL) {
            if (systemConfig.isSupportSilentInstall() && isEmpty(systemConfig.getSilentInstallCommand())) {
                issues.add("SystemOps 开了静默安装但没有配置 silentInstallCommand");
            }
            if (systemConfig.isAllowReboot() && isEmpty(systemConfig.getRebootCommand())) {
                issues.add("SystemOps 开了重启但没有配置 rebootCommand");
            }
            if (systemConfig.isAllowSetTime() && isEmpty(systemConfig.getSetTimeCommand())) {
                issues.add("SystemOps 开了校时但没有配置 setTimeCommand");
            }
        }

        ShellConfig.DebugReplay debugReplay = shellConfig.getDebugReplay();
        if (debugReplay == null) {
            issues.add("debugReplay 没有配置");
            return issues;
        }

        if (!shellConfig.getSerialChannels().containsKey(debugReplay.getDisplaySerialKey())) {
            issues.add("debugReplay.displaySerialKey 对不上当前串口配置");
        }
        if (!shellConfig.getSerialChannels().containsKey(debugReplay.getGpsSerialKey())) {
            issues.add("debugReplay.gpsSerialKey 对不上当前串口配置");
        }
        if (!shellConfig.getSocketChannels().containsKey(debugReplay.getJt808SocketKey())) {
            issues.add("debugReplay.jt808SocketKey 对不上当前 Socket 配置");
        }
        if (!shellConfig.getSocketChannels().containsKey(debugReplay.getAl808SocketKey())) {
            issues.add("debugReplay.al808SocketKey 对不上当前 Socket 配置");
        }
        if (!shellConfig.getGpioConfig().getPins().containsKey(debugReplay.getGpioPinKey())) {
            issues.add("debugReplay.gpioPinKey 对不上当前 GPIO 配置");
        }
        if (!shellConfig.getCameraConfig().getChannels().containsKey(debugReplay.getCameraChannelKey())) {
            issues.add("debugReplay.cameraChannelKey 对不上当前 Camera 配置");
        }

        ShellConfig.BasicSetupConfig basicSetupConfig = shellConfig.getBasicSetupConfig();
        if (basicSetupConfig.getNewspaperSettings().getInnerVolume() < 0 || basicSetupConfig.getNewspaperSettings().getInnerVolume() > 15) {
            issues.add("basicSetup.newspaper.innerVolume 超出范围");
        }
        if (basicSetupConfig.getNewspaperSettings().getOuterVolume() < 0 || basicSetupConfig.getNewspaperSettings().getOuterVolume() > 15) {
            issues.add("basicSetup.newspaper.outerVolume 超出范围");
        }
        if (basicSetupConfig.getTtsSettings().getInnerVolume() < 0 || basicSetupConfig.getTtsSettings().getInnerVolume() > 15) {
            issues.add("basicSetup.tts.innerVolume 超出范围");
        }
        if (basicSetupConfig.getTtsSettings().getOuterVolume() < 0 || basicSetupConfig.getTtsSettings().getOuterVolume() > 15) {
            issues.add("basicSetup.tts.outerVolume 超出范围");
        }
        if (basicSetupConfig.getOtherSettings().getDispatchVolume() < 0 || basicSetupConfig.getOtherSettings().getDispatchVolume() > 15) {
            issues.add("basicSetup.other.dispatchVolume 超出范围");
        }
        if (basicSetupConfig.getOtherSettings().getShoutingVolume() < 0 || basicSetupConfig.getOtherSettings().getShoutingVolume() > 100) {
            issues.add("basicSetup.other.shoutingVolume 超出范围");
        }
        if (basicSetupConfig.getProtocolLinkageSettings().isNetworkDispatchEnabled()
                && !"无".equals(basicSetupConfig.getSerialSettings().getRs2321Protocol())) {
            issues.add("basicSetup.protocolLinkage=network 时，serialSettings.rs2321Protocol 应为无");
        }
        if (basicSetupConfig.getProtocolLinkageSettings().isSerialDispatchEnabled()
                && "无".equals(basicSetupConfig.getSerialSettings().getRs2321Protocol())) {
            issues.add("basicSetup.protocolLinkage=serial_rs232_1 时，serialSettings.rs2321Protocol 不能为无");
        }

        return issues;
    }

    /**
     * 输出更适合页面显示的校验摘要。
     */
    public static String describe(ShellConfig shellConfig) {
        List<String> issues = validate(shellConfig);
        if (issues.isEmpty()) {
            return "配置检查：通过";
        }

        StringBuilder builder = new StringBuilder("配置检查：发现 ").append(issues.size()).append(" 个问题");
        for (String issue : issues) {
            builder.append("\n- ").append(issue);
        }
        return builder.toString();
    }

    private static boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }
}
