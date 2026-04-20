package com.lhxy.istationdevice.android11.deviceapi;

/**
 * 通用设备模式
 * <p>
 * GPIO、Camera、RFID、SystemOps 都先按这套 stub / real 管，
 * 这样配置和运行时可以用同一套开关，不用每块都单独定义一份 mode。
 */
public enum DeviceMode {
    STUB("stub"),
    REAL("real");

    private final String configValue;

    DeviceMode(String configValue) {
        this.configValue = configValue;
    }

    /**
     * 返回更适合写回配置文件的值。
     */
    public String toConfigValue() {
        return configValue;
    }

    /**
     * 从配置值解析设备模式。
     */
    public static DeviceMode fromConfig(String rawValue) {
        if (rawValue == null) {
            return STUB;
        }
        for (DeviceMode value : values()) {
            if (value.configValue.equalsIgnoreCase(rawValue.trim())) {
                return value;
            }
        }
        return STUB;
    }
}
