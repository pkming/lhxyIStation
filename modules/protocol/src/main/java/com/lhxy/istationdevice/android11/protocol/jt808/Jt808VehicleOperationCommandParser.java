package com.lhxy.istationdevice.android11.protocol.jt808;

/**
 * 解析 0x8B05 车辆运营指令。
 */
public final class Jt808VehicleOperationCommandParser {

    private Jt808VehicleOperationCommandParser() {
    }

    /**
     * 从 JT808 帧解析车辆运营指令
     */
    public static Jt808VehicleOperationCommand parse(Jt808Frame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("frame is null");
        }
        if (frame.getMessageId() != Jt808VehicleOperationCommand.MESSAGE_ID) {
            throw new IllegalArgumentException(
                    "Invalid message id: expected 0x8B05, got 0x" 
                    + Integer.toHexString(frame.getMessageId())
            );
        }

        return new Jt808VehicleOperationCommand(
                frame.getVariant(),
                frame.getTerminalId(),
                frame.getSerialNumber(),
                frame.getBody()
        );
    }
}
