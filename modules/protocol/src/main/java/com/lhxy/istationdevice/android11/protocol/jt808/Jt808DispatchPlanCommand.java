package com.lhxy.istationdevice.android11.protocol.jt808;

/** 平台调度计划下发（0x8B01）。 */
public final class Jt808DispatchPlanCommand {
    public static final int MESSAGE_ID = 0x8B01;

    private final Jt808Variant variant;
    private final String terminalId;
    private final int requestSerialNumber;
    private final int timesNo;
    private final String departureTime;
    private final int overtimeMinutes;
    private final int overtimeSpeakIntervalMinutes;
    private final int prepareSpeakIntervalMinutes;
    private final String scheduleText;

    public Jt808DispatchPlanCommand(
            Jt808Variant variant,
            String terminalId,
            int requestSerialNumber,
            int timesNo,
            String departureTime,
            int overtimeMinutes,
            int overtimeSpeakIntervalMinutes,
            int prepareSpeakIntervalMinutes,
            String scheduleText
    ) {
        this.variant = variant;
        this.terminalId = terminalId;
        this.requestSerialNumber = requestSerialNumber;
        this.timesNo = timesNo;
        this.departureTime = departureTime;
        this.overtimeMinutes = overtimeMinutes;
        this.overtimeSpeakIntervalMinutes = overtimeSpeakIntervalMinutes;
        this.prepareSpeakIntervalMinutes = prepareSpeakIntervalMinutes;
        this.scheduleText = scheduleText;
    }

    public Jt808Variant getVariant() {
        return variant;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public int getRequestSerialNumber() {
        return requestSerialNumber;
    }

    public int getTimesNo() {
        return timesNo;
    }

    /** HHmmss。 */
    public String getDepartureTime() {
        return departureTime;
    }

    public int getOvertimeMinutes() {
        return overtimeMinutes;
    }

    public int getOvertimeSpeakIntervalMinutes() {
        return overtimeSpeakIntervalMinutes;
    }

    public int getPrepareSpeakIntervalMinutes() {
        return prepareSpeakIntervalMinutes;
    }

    public String getScheduleText() {
        return scheduleText;
    }
}
