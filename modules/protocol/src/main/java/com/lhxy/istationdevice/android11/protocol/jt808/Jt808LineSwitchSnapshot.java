package com.lhxy.istationdevice.android11.protocol.jt808;

import java.time.LocalDateTime;

/**
 * JT808 / AL808 线路切换参数
 */
public final class Jt808LineSwitchSnapshot {
    private final int type;
    private final int firstLineNumber;
    private final int firstDirection;
    private final int firstBusNo;
    private final int lineNumber;
    private final int direction;
    private final int busNo;
    private final LocalDateTime terminalTime;
    private final int reserved;

    public Jt808LineSwitchSnapshot(
            int type,
            int firstLineNumber,
            int firstDirection,
            int firstBusNo,
            int lineNumber,
            int direction,
            int busNo,
            LocalDateTime terminalTime,
            int reserved
    ) {
        this.type = type;
        this.firstLineNumber = firstLineNumber;
        this.firstDirection = firstDirection;
        this.firstBusNo = firstBusNo;
        this.lineNumber = lineNumber;
        this.direction = direction;
        this.busNo = busNo;
        this.terminalTime = terminalTime;
        this.reserved = reserved;
    }

    public int getType() {
        return type;
    }

    public int getFirstLineNumber() {
        return firstLineNumber;
    }

    public int getFirstDirection() {
        return firstDirection;
    }

    public int getFirstBusNo() {
        return firstBusNo;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public int getDirection() {
        return direction;
    }

    public int getBusNo() {
        return busNo;
    }

    public LocalDateTime getTerminalTime() {
        return terminalTime;
    }

    public int getReserved() {
        return reserved;
    }
}
