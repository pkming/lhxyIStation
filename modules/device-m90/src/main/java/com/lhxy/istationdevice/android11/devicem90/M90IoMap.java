package com.lhxy.istationdevice.android11.devicem90;

/**
 * M90 关键 IO 说明
 */
public final class M90IoMap {
    public static final String IO1 = "GPIO1_D0";
    public static final String IO2 = "GPIO1_D1";
    public static final String IO3 = "GPIO1_D2";
    public static final String IO4 = "GPIO1_D3";
    public static final String IO5 = "GPIO1_D4";
    public static final String INNER_SPEAKER = "GPIO0_D6";
    public static final String INNER_AUDIO = "GPIO1_B1";
    public static final String OUTER_AUDIO = "GPIO1_B2";

    private M90IoMap() {
    }

    /**
     * 输出 M90 关键 IO 摘要。
     */
    public static String describe() {
        return "M90 关键 IO：IO1~IO5=" + IO1 + "~" + IO5
                + "，内喇叭=" + INNER_SPEAKER
                + "，内音=" + INNER_AUDIO
                + "，外音=" + OUTER_AUDIO;
    }
}
