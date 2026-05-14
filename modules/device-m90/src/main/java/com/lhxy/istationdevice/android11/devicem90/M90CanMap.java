package com.lhxy.istationdevice.android11.devicem90;

/**
 * M90 CAN 口位说明
 */
public final class M90CanMap {
    public static final String CAN0 = "can0";
    public static final String CAN1 = "can1";

    private M90CanMap() {
    }

    public static String describe() {
        return "M90 CAN 预设：" + CAN0 + " / " + CAN1;
    }
}