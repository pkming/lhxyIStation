package com.lhxy.istationdevice.android11.protocol.jt808;

/**
 * 0x0900 职业请求上行消息（type=0x81）。
 * 
 * <p>对标现场版M90实现：司机在调度界面发起职业请求，平台用0x8B09应答。</p>
 * 
 * <p>支持的请求类型：</p>
 * <ul>
 *   <li>1: 请求排班</li>
 *   <li>2: 请求交接班</li>
 *   <li>3: 请求加油</li>
 *   <li>4: 请求充气</li>
 *   <li>5: 请求充电</li>
 *   <li>6: 退出营运</li>
 *   <li>7: 手动发车</li>
 *   <li>8: 手动收车</li>
 *   <li>9: 请求包车</li>
 *   <li>10: 请求维修</li>
 *   <li>11: 其他请求</li>
 *   <li>13: 对讲请求</li>
 * </ul>
 */
public final class Jt808ProfessionRequestMessage {
    public static final int MESSAGE_ID = 0x0900;
    public static final byte TRANSMISSION_TYPE = (byte) 0x81;
    
    // 职业请求类型定义
    public static final int TYPE_REQUEST_SCHEDULE = 1;       // 请求排班
    public static final int TYPE_REQUEST_HANDOVER = 2;       // 请求交接班
    public static final int TYPE_REQUEST_OIL = 3;            // 请求加油
    public static final int TYPE_REQUEST_AERATE = 4;         // 请求充气
    public static final int TYPE_REQUEST_CHARGE = 5;         // 请求充电
    public static final int TYPE_EXIT_OPERATION = 6;         // 退出营运
    public static final int TYPE_MANUAL_START = 7;           // 手动发车
    public static final int TYPE_MANUAL_END = 8;             // 手动收车
    public static final int TYPE_REQUEST_CHARTER = 9;        // 请求包车
    public static final int TYPE_REQUEST_REPAIR = 10;        // 请求维修
    public static final int TYPE_OTHER_REQUESTS = 11;        // 其他请求
    public static final int TYPE_INTERCOM = 13;              // 对讲请求
    
    private final Jt808Variant variant;
    private final String terminalId;
    private final int serialNumber;
    private final String lineNumber;
    private final String cardNo;
    private final int professionRequestType;
    private final String terminalTime;
    private final int longitude;
    private final int latitude;

    public Jt808ProfessionRequestMessage(
            Jt808Variant variant,
            String terminalId,
            int serialNumber,
            String lineNumber,
            String cardNo,
            int professionRequestType,
            String terminalTime,
            int longitude,
            int latitude
    ) {
        this.variant = variant;
        this.terminalId = terminalId;
        this.serialNumber = serialNumber;
        this.lineNumber = lineNumber;
        this.cardNo = cardNo;
        this.professionRequestType = professionRequestType;
        this.terminalTime = terminalTime;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Jt808Variant getVariant() {
        return variant;
    }

    public String getTerminalId() {
        return terminalId;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public String getCardNo() {
        return cardNo;
    }

    public int getProfessionRequestType() {
        return professionRequestType;
    }

    public String getTerminalTime() {
        return terminalTime;
    }

    public int getLongitude() {
        return longitude;
    }

    public int getLatitude() {
        return latitude;
    }

    /**
     * 创建职业请求透传消息（对标M90: type=0x81, 88字节）
     */
    public static Jt808PassthroughMessage createProfessionRequest(
            Jt808Variant variant,
            String terminalId,
            int serialNumber,
            String lineNumber,
            String cardNo,
            int professionRequestType,
            String terminalTime,
            int longitude,
            int latitude
    ) {
        byte[] content = new byte[87];  // type字节由PassthroughMessage处理
        int offset = 0;
        
        // [0-35]: 线路号（36字节GBK）
        try {
            byte[] lineBytes = lineNumber.getBytes("GBK");
            int copyLen = Math.min(lineBytes.length, 36);
            System.arraycopy(lineBytes, 0, content, offset, copyLen);
        } catch (Exception ignored) {
        }
        offset += 36;
        
        // [36-71]: 司机卡号（36字节HEX）
        try {
            byte[] cardBytes = hexStringToBytes(cardNo);
            int copyLen = Math.min(cardBytes.length, 36);
            System.arraycopy(cardBytes, 0, content, offset, copyLen);
        } catch (Exception ignored) {
        }
        offset += 36;
        
        // [72]: 职业请求类型（1-13）
        content[offset++] = (byte) professionRequestType;
        
        // [73-78]: 终端时间（BCD码6字节）
        byte[] timeBcd = stringToBcd(terminalTime);
        System.arraycopy(timeBcd, 0, content, offset, 6);
        offset += 6;
        
        // [79-82]: 经度（4字节）
        content[offset++] = (byte) (longitude >> 24);
        content[offset++] = (byte) (longitude >> 16);
        content[offset++] = (byte) (longitude >> 8);
        content[offset++] = (byte) longitude;
        
        // [83-86]: 纬度（4字节）
        content[offset++] = (byte) (latitude >> 24);
        content[offset++] = (byte) (latitude >> 16);
        content[offset++] = (byte) (latitude >> 8);
        content[offset++] = (byte) latitude;
        
        return new Jt808PassthroughMessage(
                variant,
                terminalId,
                serialNumber,
                TRANSMISSION_TYPE,
                content
        );
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
