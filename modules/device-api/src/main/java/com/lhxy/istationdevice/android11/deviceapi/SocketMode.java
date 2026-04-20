package com.lhxy.istationdevice.android11.deviceapi;

/**
 * Socket 发送模式
 */
public enum SocketMode {
    STUB,
    REAL;

    /**
     * 从配置文本解析发送模式。
     */
    public static SocketMode fromConfig(String source) {
        if (source == null) {
            return STUB;
        }
        return "real".equalsIgnoreCase(source.trim()) ? REAL : STUB;
    }

    /**
     * 返回更适合写进配置文件的值。
     */
    public String toConfigValue() {
        return name().toLowerCase();
    }
}
