package com.lhxy.istationdevice.android11.protocol.jt808;

/**
 * JT808 / AL808 通用应答参数
 */
public final class Jt808GeneralResponse {
    private final int responseSerialNumber;
    private final int responseMessageId;
    private final int result;

    public Jt808GeneralResponse(int responseSerialNumber, int responseMessageId, int result) {
        this.responseSerialNumber = responseSerialNumber;
        this.responseMessageId = responseMessageId;
        this.result = result;
    }

    public int getResponseSerialNumber() {
        return responseSerialNumber;
    }

    public int getResponseMessageId() {
        return responseMessageId;
    }

    public int getResult() {
        return result;
    }
}
