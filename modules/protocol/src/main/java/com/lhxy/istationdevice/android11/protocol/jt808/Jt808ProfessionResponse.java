package com.lhxy.istationdevice.android11.protocol.jt808;

/** 平台对终端职业请求的应答（0x8B09）。 */
public final class Jt808ProfessionResponse {
    public static final int MESSAGE_ID = 0x8B09;

    private final int requestSerialNumber;
    private final int result;

    public Jt808ProfessionResponse(int requestSerialNumber, int result) {
        this.requestSerialNumber = requestSerialNumber;
        this.result = result;
    }

    public int getRequestSerialNumber() {
        return requestSerialNumber;
    }

    public int getResult() {
        return result;
    }

    public boolean isAccepted() {
        return result == 1;
    }
}
