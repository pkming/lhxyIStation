package com.lhxy.istationdevice.android11.protocol.jt808;

import java.time.LocalDateTime;

/**
 * 终端校时应答参数
 */
public final class Jt808TimingResponse {
    private final LocalDateTime terminalTime;

    public Jt808TimingResponse(LocalDateTime terminalTime) {
        this.terminalTime = terminalTime;
    }

    public LocalDateTime getTerminalTime() {
        return terminalTime;
    }
}
