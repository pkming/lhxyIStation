package com.lhxy.istationdevice.android11.protocol.jt808;

import java.time.LocalDateTime;

/**
 * JT808 / AL808 司机考勤参数
 */
public final class Jt808DriverAttendance {
    private final int lineNumber;
    private final String cardNumberHex;
    private final LocalDateTime terminalTime;
    private final int driverStatus;
    private final int type;

    public Jt808DriverAttendance(
            int lineNumber,
            String cardNumberHex,
            LocalDateTime terminalTime,
            int driverStatus,
            int type
    ) {
        this.lineNumber = lineNumber;
        this.cardNumberHex = cardNumberHex;
        this.terminalTime = terminalTime;
        this.driverStatus = driverStatus;
        this.type = type;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public String getCardNumberHex() {
        return cardNumberHex;
    }

    public LocalDateTime getTerminalTime() {
        return terminalTime;
    }

    public int getDriverStatus() {
        return driverStatus;
    }

    public int getType() {
        return type;
    }
}
