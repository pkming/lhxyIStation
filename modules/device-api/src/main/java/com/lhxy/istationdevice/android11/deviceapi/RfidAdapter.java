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
}
