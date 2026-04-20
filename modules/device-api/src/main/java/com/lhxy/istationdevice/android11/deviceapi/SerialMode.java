package com.lhxy.istationdevice.android11.deviceapi;

/**
 * 串口模式
 * <p>
 * 先和 Socket 一样分成 stub / real，
 * 这样页面和业务层都只认配置，不直接认具体适配器实现。
 */
public enum SerialMode {
    STUB("stub"),
    REAL("real");

    private final String configValue;

    SerialMode(String configValue) {
        this.configValue = configValue;
    }

    /**
     * 配置文件里的值。
     */
    public String toConfigValue() {
        return configValue;
    }

    /**
     * 从配置值解析模式。
     */
    public static SerialMode fromConfig(String rawValue) {
        if (rawValue == null) {
            return STUB;
        }
        for (SerialMode value : values()) {
            if (value.configValue.equalsIgnoreCase(rawValue.trim())) {
                return value;
            }
        }
        return STUB;
    }
}
