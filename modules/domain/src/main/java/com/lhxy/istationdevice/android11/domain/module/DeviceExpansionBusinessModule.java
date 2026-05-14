package com.lhxy.istationdevice.android11.domain.module;

import com.lhxy.istationdevice.android11.domain.config.ShellConfig;

import java.util.Map;

/**
 * AL-M90 扩展硬件接入模块。
 */
public final class DeviceExpansionBusinessModule extends AbstractTerminalBusinessModule {
    @Override
    public String getKey() {
        return "device_expansion";
    }

    @Override
    public String getTitle() {
        return "扩展硬件";
    }

    @Override
    public String describePurpose() {
        return "承接 LocationManager、CAN、ttyS0 键盘、RFID I2C 和补齐 GPIO 的配置化接入状态。";
    }

    @Override
    public String describeStatus() {
        try {
            return buildStatus(requireShellConfig()) + "\n- " + describeActionMemory();
        } catch (Exception e) {
            return "扩展硬件配置未就绪: " + emptyAsDash(e.getMessage());
        }
    }

    @Override
    public ModuleRunResult runSample(String traceId) {
        try {
            return success("已刷新扩展硬件接入状态", buildStatus(requireShellConfig()));
        } catch (Exception e) {
            return failure("扩展硬件状态刷新失败", e);
        }
    }

    private String buildStatus(ShellConfig shellConfig) {
        StringBuilder builder = new StringBuilder();
        ShellConfig.LocationConfig location = shellConfig.getLocationConfig();
        builder.append("LocationManager=").append(location.getMode().toConfigValue())
                .append(" / enabled=").append(yesNo(location.isEnabled()))
                .append(" / provider=").append(location.getProvider());

        ShellConfig.RfidConfig rfid = shellConfig.getRfidConfig();
        builder.append("\n- RFID I2C=").append(emptyAsDash(rfid.getI2cDevicePath()))
                .append("@").append(emptyAsDash(rfid.getI2cAddress()));

        ShellConfig.KeyboardConfig keyboard = shellConfig.getKeyboardConfig();
        builder.append("\n- Keyboard=").append(keyboard.getMode().toConfigValue())
                .append(" / serial=").append(emptyAsDash(keyboard.getSerialKey()))
                .append(" / protocol=").append(emptyAsDash(keyboard.getProtocol()));

        builder.append("\n- CAN=").append(shellConfig.getCanConfig().getMode().toConfigValue());
        for (Map.Entry<String, ShellConfig.CanChannel> entry : shellConfig.getCanConfig().getChannels().entrySet()) {
            ShellConfig.CanChannel channel = entry.getValue();
            builder.append("\n  ").append(entry.getKey())
                    .append(" -> ").append(emptyAsDash(channel.getInterfaceName()))
                    .append(" / device=").append(emptyAsDash(channel.getDevicePath()))
                    .append(" / read=").append(emptyAsDash(channel.getReadCommand()))
                    .append(" / write=").append(emptyAsDash(channel.getWriteCommand()));
        }

        builder.append("\n- GPIO 扩展:");
        appendGpio(builder, shellConfig, "io3");
        appendGpio(builder, shellConfig, "io4");
        appendGpio(builder, shellConfig, "shouting_inner");
        appendGpio(builder, shellConfig, "shouting_outer");
        appendGpio(builder, shellConfig, "headphone_detect_power");
        appendGpio(builder, shellConfig, "headphone_detect");
        return builder.toString();
    }

    private void appendGpio(StringBuilder builder, ShellConfig shellConfig, String key) {
        ShellConfig.GpioPin pin = shellConfig.getGpioConfig().getPins().get(key);
        builder.append("\n  ").append(key).append(" -> ");
        if (pin == null) {
            builder.append("未配置");
            return;
        }
        builder.append(emptyAsDash(pin.getPinId()))
                .append(" / valuePath=").append(emptyAsDash(pin.getValuePath()))
                .append(" / default=").append(pin.getDefaultValue());
    }
}