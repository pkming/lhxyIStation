package com.lhxy.istationdevice.android11.domain.debug;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.config.ShellConfigLoader;

import java.io.File;
import java.util.Map;

/**
 * 终端自检用例
 * <p>
 * 先做最影响联调效率的几项：
 * 串口节点、Socket 配置、GPIO 路径、Camera 权限、RFID 桥接、SystemOps 命令模板。
 */
public final class TerminalSelfCheckUseCase {
    /**
     * 生成当前终端的自检摘要。
     */
    public String buildReport(Context context, ShellConfig shellConfig) {
        return buildReport(context, shellConfig, "", "");
    }

    /**
     * 生成当前终端的自检摘要，并附带运行时底座状态。
     */
    public String buildReport(Context context, ShellConfig shellConfig, String foundationStatus) {
        return buildReport(context, shellConfig, foundationStatus, "");
    }

    /**
     * 生成当前终端的自检摘要，并附带底座和模块状态。
     */
    public String buildReport(Context context, ShellConfig shellConfig, String foundationStatus, String moduleStatus) {
        StringBuilder builder = new StringBuilder("终端自检:");
        if (shellConfig == null) {
            return builder.append("\n- 当前没有可用配置").toString();
        }

        builder.append("\n- 设备配置: ")
                .append(shellConfig.getDeviceProfile())
                .append(" / ")
                .append(shellConfig.getConfigVersion());

        File runtimeConfigFile = ShellConfigLoader.getRuntimeConfigFile(context);
        builder.append("\n- 运行配置文件: ")
                .append(runtimeConfigFile.getAbsolutePath())
                .append(runtimeConfigFile.exists() ? " [已存在]" : " [未生成]");

        builder.append("\n\n串口检查:");
        for (Map.Entry<String, ShellConfig.SerialChannel> entry : shellConfig.getSerialChannels().entrySet()) {
            ShellConfig.SerialChannel channel = entry.getValue();
            String portPath = normalizePortPath(channel.getPortName());
            if (channel.getMode().toConfigValue().equalsIgnoreCase("stub")) {
                builder.append("\n- ").append(entry.getKey())
                        .append(" -> ").append(portPath)
                        .append(" [stub，跳过节点检查]");
                continue;
            }

            File portFile = new File(portPath);
            builder.append("\n- ").append(entry.getKey())
                    .append(" -> ").append(portPath)
                    .append(portFile.exists() ? " [存在]" : " [不存在]")
                    .append(portFile.exists() ? " [R=" + portFile.canRead() + ", W=" + portFile.canWrite() + "]" : "");
        }

        builder.append("\n\nSocket 检查:");
        for (Map.Entry<String, ShellConfig.SocketChannel> entry : shellConfig.getSocketChannels().entrySet()) {
            ShellConfig.SocketChannel channel = entry.getValue();
            builder.append("\n- ").append(entry.getKey())
                    .append(" -> ")
                    .append(channel.getHost()).append(":").append(channel.getPort())
                    .append(" [").append(channel.getMode().toConfigValue()).append("]");
            if ("real".equalsIgnoreCase(channel.getMode().toConfigValue())) {
                if (channel.getHost() == null || channel.getHost().trim().isEmpty()) {
                    builder.append(" [host 缺失]");
                }
                if (channel.getPort() <= 0) {
                    builder.append(" [port 非法]");
                }
            }
        }

        builder.append("\n\nGPIO 检查:");
        builder.append("\n- GPIO mode -> ").append(shellConfig.getGpioConfig().getMode().toConfigValue());
        for (Map.Entry<String, ShellConfig.GpioPin> entry : shellConfig.getGpioConfig().getPins().entrySet()) {
            ShellConfig.GpioPin pin = entry.getValue();
            builder.append("\n- ").append(entry.getKey())
                    .append(" -> ").append(pin.getPinId());
            if (shellConfig.getGpioConfig().getMode() == com.lhxy.istationdevice.android11.deviceapi.DeviceMode.STUB) {
                builder.append(" [stub，默认值=").append(pin.getDefaultValue()).append("]");
                continue;
            }
            if (pin.getValuePath() == null || pin.getValuePath().trim().isEmpty()) {
                builder.append(" [valuePath 缺失]");
                continue;
            }
            File valueFile = new File(pin.getValuePath().trim());
            builder.append(" -> ").append(pin.getValuePath())
                    .append(valueFile.exists() ? " [存在]" : " [不存在]")
                    .append(valueFile.exists() ? " [R=" + valueFile.canRead() + ", W=" + valueFile.canWrite() + "]" : "");
        }

        builder.append("\n\nCamera 检查:");
        builder.append("\n- Camera mode -> ").append(shellConfig.getCameraConfig().getMode().toConfigValue());
        builder.append("\n- CAMERA 权限 -> ")
                .append(context == null ? "无法检查" : (context.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ? "已授权" : "未授权"));
        for (Map.Entry<String, ShellConfig.CameraChannel> entry : shellConfig.getCameraConfig().getChannels().entrySet()) {
            builder.append("\n- ").append(entry.getKey())
                    .append(" -> cameraId=").append(entry.getValue().getCameraId())
                    .append(entry.getValue().getNote().isEmpty() ? "" : " / " + entry.getValue().getNote());
        }

        builder.append("\n\nRFID 检查:");
        builder.append("\n- RFID mode -> ").append(shellConfig.getRfidConfig().getMode().toConfigValue());
        builder.append("\n- mockCardNo -> ").append(emptyAsDash(shellConfig.getRfidConfig().getMockCardNo()));
        builder.append("\n- inputFilePath -> ").append(emptyAsDash(shellConfig.getRfidConfig().getInputFilePath()));
        if (!shellConfig.getRfidConfig().getInputFilePath().trim().isEmpty()) {
            File inputFile = new File(shellConfig.getRfidConfig().getInputFilePath().trim());
            builder.append(inputFile.exists() ? " [存在]" : " [不存在]");
        }
        builder.append("\n- readCommand -> ").append(emptyAsDash(shellConfig.getRfidConfig().getReadCommand()));

        builder.append("\n\nSystemOps 检查:");
        builder.append("\n- mode -> ").append(shellConfig.getSystemConfig().getMode().toConfigValue());
        builder.append("\n- silentInstall -> ").append(shellConfig.getSystemConfig().isSupportSilentInstall());
        builder.append("\n- reboot -> ").append(shellConfig.getSystemConfig().isAllowReboot());
        builder.append("\n- setTime -> ").append(shellConfig.getSystemConfig().isAllowSetTime());
        builder.append("\n- rebootCommand -> ").append(emptyAsDash(shellConfig.getSystemConfig().getRebootCommand()));
        builder.append("\n- setTimeCommand -> ").append(emptyAsDash(shellConfig.getSystemConfig().getSetTimeCommand()));

        builder.append("\n\n基础设置检查:");
        builder.append("\n- newspaper.innerVolume -> ").append(shellConfig.getBasicSetupConfig().getNewspaperSettings().getInnerVolume());
        builder.append("\n- newspaper.outerVolume -> ").append(shellConfig.getBasicSetupConfig().getNewspaperSettings().getOuterVolume());
        builder.append("\n- newspaper.lineProperty -> ").append(shellConfig.getBasicSetupConfig().getNewspaperSettings().getLineProperty());
        builder.append("\n- tts.enabled -> ").append(shellConfig.getBasicSetupConfig().getTtsSettings().isEnabled());
        builder.append("\n- language -> ").append(shellConfig.getBasicSetupConfig().getLanguageSettings().getLanguageCode());
        builder.append("\n- shoutingVolume -> ").append(shellConfig.getBasicSetupConfig().getOtherSettings().getShoutingVolume());
        builder.append("\n- dispatchVolume -> ").append(shellConfig.getBasicSetupConfig().getOtherSettings().getDispatchVolume());

        if (foundationStatus != null && !foundationStatus.trim().isEmpty()) {
            builder.append("\n\n运行时底座状态:")
                    .append("\n").append(foundationStatus.trim());
        }

        if (moduleStatus != null && !moduleStatus.trim().isEmpty()) {
            builder.append("\n\n业务模块状态:")
                    .append("\n").append(moduleStatus.trim());
        }

        return builder.toString();
    }

    private String normalizePortPath(String portName) {
        if (portName == null || portName.trim().isEmpty()) {
            return "/dev/unknown";
        }
        String trimmed = portName.trim();
        return trimmed.startsWith("/") ? trimmed : "/dev/" + trimmed;
    }

    private String emptyAsDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }
}
