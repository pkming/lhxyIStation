package com.lhxy.istationdevice.android11.deviceapi;

/**
 * GPIO 抽象接口
 * <p>
 * 页面和业务层只认逻辑 pin key，不直接碰 sysfs 路径或厂商 JNI。
 */
public interface GpioAdapter {
    /**
     * 写一个 GPIO 值。
     */
    void write(String pinId, int value, String traceId);

    /**
     * 读取一个 GPIO 值。
     */
    int read(String pinId, String traceId);
}
