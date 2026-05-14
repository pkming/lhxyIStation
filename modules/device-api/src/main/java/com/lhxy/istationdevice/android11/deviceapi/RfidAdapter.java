package com.lhxy.istationdevice.android11.deviceapi;

/**
 * RFID 抽象接口
 * <p>
 * 目前先统一成“是否可用 + 读一次卡号”，
 * 真机后面如果要加持续监听，再在这层扩。
 */
public interface RfidAdapter {
    /**
     * 当前 RFID 能力是否可用。
     */
    boolean isAvailable();

    /**
     * 读取一次卡号。
     */
    String readCard(String traceId);

    /**
     * 等待卡片离开感应区。
     * <p>
     * 默认实现直接返回，方便 stub/只读一次卡号的桥接先复用；
     * 真机适配器可以按设备能力覆写成真实等待。
     */
    default boolean waitCardRemoved(String traceId, long timeoutMs, long pollIntervalMs) {
        return true;
    }
}
