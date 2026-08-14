package com.lhxy.istationdevice.android11.protocol.jt808;

/**
 * 0x8B05 车辆运营指令（平台下行）。
 * 
 * <p>对标现场版M90实现：平台下发车辆运营指令，终端需用0x0900透传消息应答。</p>
 */
public final class Jt808VehicleOperationCommand {
    public static final int MESSAGE_ID = 0x8B05;
    
    private final Jt808Variant variant;
    private final String terminalId;
    private final int requestSerialNumber;
    private final byte transmissionType;
    private final String carNumber;
    private final String departureTime;
    private final byte[] rawBody;

    public Jt808VehicleOperationCommand(
            Jt808Variant variant,
            String terminalId,
            int requestSerialNumber,
            byte[] body
    ) {
        this.variant = variant;
        this.terminalId = terminalId;
        this.requestSerialNumber = requestSerialNumber;
        this.rawBody = body;
        
        // 解析消息体
        if (body != null && body.length >= 17) {
            this.transmissionType = body[0];
            
            // 字节3-12: 车牌号（10字节）
            byte[] carBytes = new byte[10];
            System.arraycopy(body, 3, carBytes, 0, 10);
            this.carNumber = bytesToHexString(carBytes);
            
            // 字节13-18: 发车时间（6字节BCD码）
            byte[] timeBytes = new byte[6];
            System.arraycopy(body, 13, timeBytes, 0, 6);
            this.departureTime = bcdToString(timeBytes);
        } else {
            this.transmissionType = 0;
            this.carNumber = "";
            this.departureTime = "";
        }
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

    public byte getTransmissionType() {
        return transmissionType;
    }

    public String getCarNumber() {
        return carNumber;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public byte[] getRawBody() {
        return rawBody;
    }

    /**
     * 创建车辆运营应答（0x0900透传消息，22字节）
     */
    public Jt808PassthroughMessage createResponse(
            int responseSerialNumber,
            String actualCarNumber,
            String actualDepartureTime,
            int result
    ) {
        byte[] content = new byte[21];  // 22字节总长度，type字节由PassthroughMessage处理
        int offset = 0;
        
        // [0-1]: 保留字节（0x0000）
        content[offset++] = 0;
        content[offset++] = 0;
        
        // [2-3]: 流水号（对应下行消息的流水号）
        content[offset++] = (byte) (requestSerialNumber >> 8);
        content[offset++] = (byte) requestSerialNumber;
        
        // [4-13]: 车牌号（10字节）
        try {
            byte[] carBytes = hexStringToBytes(actualCarNumber);
            int copyLen = Math.min(carBytes.length, 10);
            System.arraycopy(carBytes, 0, content, offset, copyLen);
        } catch (Exception ignored) {
        }
        offset += 10;
        
        // [14-19]: 发车时间（6字节BCD码）
        byte[] timeBcd = stringToBcd(actualDepartureTime);
        System.arraycopy(timeBcd, 0, content, offset, 6);
        offset += 6;
        
        // [20]: 结果码（0=成功，1=失败，2=消息有误）
        content[offset] = (byte) result;
        
        return new Jt808PassthroughMessage(
                variant,
                terminalId,
                responseSerialNumber,
                transmissionType,  // 使用下行消息的transmissionType
                content
        );
    }

    private static String bytesToHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b & 0xFF));
        }
        return sb.toString();
    }

    private static String bcdToString(byte[] bcd) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bcd) {
            int high = (b >> 4) & 0x0F;
            int low = b & 0x0F;
            sb.append(high).append(low);
        }
        return sb.toString();
    }

    private static byte[] stringToBcd(String timeStr) {
        byte[] bcd = new byte[6];
        if (timeStr == null || timeStr.length() < 12) {
            return bcd;
        }
        try {
            for (int i = 0; i < 6; i++) {
                int high = Character.digit(timeStr.charAt(i * 2), 10);
                int low = Character.digit(timeStr.charAt(i * 2 + 1), 10);
                bcd[i] = (byte) ((high << 4) | low);
            }
        } catch (Exception ignored) {
        }
        return bcd;
    }

    private static byte[] hexStringToBytes(String hex) {
        if (hex == null || hex.isEmpty()) {
            return new byte[0];
        }
        hex = hex.replaceAll("[^0-9A-Fa-f]", "");
        if (hex.length() % 2 != 0) {
            hex = "0" + hex;
        }
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(i * 2, i * 2 + 2), 16);
        }
        return bytes;
    }
}
