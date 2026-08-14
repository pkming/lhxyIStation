package com.lhxy.istationdevice.android11.protocol.jt808;

/**
 * 0x0900 数据上行透传。
 * 
 * <p>消息体格式：透传类型(BYTE) + 透传内容(N字节)</p>
 * 
 * <p>对标现场版M90透传类型：</p>
 * <ul>
 *   <li>type=0x01: 司机登录/注销（87字节）</li>
 *   <li>type=0x06: 进站报告（104字节）</li>
 *   <li>type=0x07: 出站报告（112字节）</li>
 *   <li>type=0x09: 司机登录注销（现场版实际使用，87字节）</li>
 * </ul>
 */
public final class Jt808PassthroughMessage {
    public static final int MESSAGE_ID = 0x0900;
    
    // 透传类型定义（对标现场版）
    public static final byte TYPE_DRIVER_LOGIN = 0x01;      // 司机登录/注销（通用）
    public static final byte TYPE_ENTER_STATION = 0x06;     // 进站报告
    public static final byte TYPE_LEAVE_STATION = 0x07;     // 出站报告
    public static final byte TYPE_DRIVER_SIGNIN = 0x09;     // 司机登录注销（M90实际使用）
    
    private final Jt808Variant variant;
    private final String terminalId;
    private final int serialNumber;
    private final byte passthroughType;
    private final byte[] content;

    public Jt808PassthroughMessage(
            Jt808Variant variant,
            String terminalId,
            int serialNumber,
            byte passthroughType,
            byte[] content
    ) {
        this.variant = variant;
        this.terminalId = terminalId;
        this.serialNumber = serialNumber;
        this.passthroughType = passthroughType;
        this.content = content == null ? new byte[0] : content;
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

    public byte getPassthroughType() {
        return passthroughType;
    }

    public byte[] getContent() {
        return content;
    }

    public int getContentLength() {
        return content.length;
    }
    
    public boolean isDriverLoginLogout() {
        return passthroughType == TYPE_DRIVER_LOGIN || passthroughType == TYPE_DRIVER_SIGNIN;
    }
    
    public boolean isEnterStation() {
        return passthroughType == TYPE_ENTER_STATION;
    }
    
    public boolean isLeaveStation() {
        return passthroughType == TYPE_LEAVE_STATION;
    }

    /**
     * 创建进站报告透传消息（对标M90: type=6, 104字节）
     */
    public static Jt808PassthroughMessage createEnterStationReport(
            Jt808Variant variant,
            String terminalId,
            int serialNumber,
            int vehicleStatus,
            String stationCode,
            int plannedTrips,
            int busNo,
            int direction,
            String lineName,
            String enterTime,
            int reportType,
            int angle,
            int longitude,
            int latitude
    ) {
        byte[] content = new byte[103];  // type字节不包含在content中
        int offset = 0;
        
        // [0-3]: 车辆状态（4字节）
        content[offset++] = (byte) (vehicleStatus >> 24);
        content[offset++] = (byte) (vehicleStatus >> 16);
        content[offset++] = (byte) (vehicleStatus >> 8);
        content[offset++] = (byte) vehicleStatus;
        
        // [4-39]: 站点编码（36字节GBK）
        try {
            byte[] stationBytes = stationCode.getBytes("GBK");
            int copyLen = Math.min(stationBytes.length, 36);
            System.arraycopy(stationBytes, 0, content, offset, copyLen);
        } catch (Exception ignored) {
        }
        offset += 36;
        
        // [40]: 计划趟次
        content[offset++] = (byte) plannedTrips;
        
        // [41]: 车次号
        content[offset++] = (byte) busNo;
        
        // [42]: 行驶方向
        content[offset++] = (byte) direction;
        
        // [43-78]: 线路名称（36字节GBK）
        try {
            byte[] lineBytes = lineName.getBytes("GBK");
            int copyLen = Math.min(lineBytes.length, 36);
            System.arraycopy(lineBytes, 0, content, offset, copyLen);
        } catch (Exception ignored) {
        }
        offset += 36;
        
        // [79-84]: 进站时间（BCD码6字节）
        byte[] timeBcd = stringToBcd(enterTime);
        System.arraycopy(timeBcd, 0, content, offset, 6);
        offset += 6;
        
        // [85-90]: 出站时间（空）
        offset += 6;
        
        // [91-92]: 上报类型（2字节）
        content[offset++] = (byte) (reportType >> 8);
        content[offset++] = (byte) reportType;
        
        // [93-94]: 角度（2字节）
        content[offset++] = (byte) (angle >> 8);
        content[offset++] = (byte) angle;
        
        // [95-102]: 经纬度（8字节）
        content[offset++] = (byte) (longitude >> 24);
        content[offset++] = (byte) (longitude >> 16);
        content[offset++] = (byte) (longitude >> 8);
        content[offset++] = (byte) longitude;
        content[offset++] = (byte) (latitude >> 24);
        content[offset++] = (byte) (latitude >> 16);
        content[offset++] = (byte) (latitude >> 8);
        content[offset++] = (byte) latitude;
        
        return new Jt808PassthroughMessage(
                variant,
                terminalId,
                serialNumber,
                TYPE_ENTER_STATION,
                content
        );
    }

    /**
     * 创建出站报告透传消息（对标M90: type=7, 112字节）
     */
    public static Jt808PassthroughMessage createLeaveStationReport(
            Jt808Variant variant,
            String terminalId,
            int serialNumber,
            int vehicleStatus,
            String stationCode,
            int plannedTrips,
            int busNo,
            int direction,
            String lineName,
            String enterTime,
            String leaveTime,
            int reportType,
            int angle,
            int longitude,
            int latitude
    ) {
        byte[] content = new byte[111];  // type字节不包含在content中
        int offset = 0;
        
        // [0-3]: 车辆状态（4字节）
        content[offset++] = (byte) (vehicleStatus >> 24);
        content[offset++] = (byte) (vehicleStatus >> 16);
        content[offset++] = (byte) (vehicleStatus >> 8);
        content[offset++] = (byte) vehicleStatus;
        
        // [4-39]: 站点编码（36字节GBK）
        try {
            byte[] stationBytes = stationCode.getBytes("GBK");
            int copyLen = Math.min(stationBytes.length, 36);
            System.arraycopy(stationBytes, 0, content, offset, copyLen);
        } catch (Exception ignored) {
        }
        offset += 36;
        
        // [40]: 计划趟次
        content[offset++] = (byte) plannedTrips;
        
        // [41]: 车次号
        content[offset++] = (byte) busNo;
        
        // [42]: 行驶方向
        content[offset++] = (byte) direction;
        
        // [43-78]: 线路名称（36字节GBK）
        try {
            byte[] lineBytes = lineName.getBytes("GBK");
            int copyLen = Math.min(lineBytes.length, 36);
            System.arraycopy(lineBytes, 0, content, offset, copyLen);
        } catch (Exception ignored) {
        }
        offset += 36;
        
        // [79-84]: 进站时间（BCD码6字节）
        byte[] enterBcd = stringToBcd(enterTime);
        System.arraycopy(enterBcd, 0, content, offset, 6);
        offset += 6;
        
        // [85-90]: 出站时间（BCD码6字节）
        byte[] leaveBcd = stringToBcd(leaveTime);
        System.arraycopy(leaveBcd, 0, content, offset, 6);
        offset += 6;
        
        // [91-92]: 上报类型（2字节）
        content[offset++] = (byte) (reportType >> 8);
        content[offset++] = (byte) reportType;
        
        // [93-94]: 角度（2字节）
        content[offset++] = (byte) (angle >> 8);
        content[offset++] = (byte) angle;
        
        // [95-96]: 空字节
        offset += 2;
        
        // [97-100]: 空字节
        offset += 4;
        
        // [101-110]: 经纬度（8字节+2字节预留）
        content[offset++] = (byte) (longitude >> 24);
        content[offset++] = (byte) (longitude >> 16);
        content[offset++] = (byte) (longitude >> 8);
        content[offset++] = (byte) longitude;
        content[offset++] = (byte) (latitude >> 24);
        content[offset++] = (byte) (latitude >> 16);
        content[offset++] = (byte) (latitude >> 8);
        content[offset++] = (byte) latitude;
        
        return new Jt808PassthroughMessage(
                variant,
                terminalId,
                serialNumber,
                TYPE_LEAVE_STATION,
                content
        );
    }

    /**
     * 创建司机登录注销透传消息（对标M90: type=0x09, 87字节）
     */
    public static Jt808PassthroughMessage createDriverLoginLogout(
            Jt808Variant variant,
            String terminalId,
            int serialNumber,
            String deviceVersion,
            String cardNo,
            String devicePassword,
            int longitude,
            int latitude,
            String terminalTime,
            int driverStatus,
            int deviceSignMode,
            String simCardIccid
    ) {
        byte[] content = new byte[86];  // type字节不包含在content中
        int offset = 0;
        
        // [0-3]: 设备版本号（4字节GBK）
        try {
            byte[] versionBytes = deviceVersion.getBytes("GBK");
            int copyLen = Math.min(versionBytes.length, 4);
            System.arraycopy(versionBytes, 0, content, offset, copyLen);
        } catch (Exception ignored) {
        }
        offset += 4;
        
        // [4-39]: 司机IC卡号（36字节HEX）
        try {
            byte[] cardBytes = hexStringToBytes(cardNo);
            int copyLen = Math.min(cardBytes.length, 36);
            System.arraycopy(cardBytes, 0, content, offset, copyLen);
        } catch (Exception ignored) {
        }
        offset += 36;
        
        // [40-49]: 设备密码（10字节GBK）
        try {
            byte[] passwordBytes = devicePassword.getBytes("GBK");
            int copyLen = Math.min(passwordBytes.length, 10);
            System.arraycopy(passwordBytes, 0, content, offset, copyLen);
        } catch (Exception ignored) {
        }
        offset += 10;
        
        // [50-53]: 经度（4字节）
        content[offset++] = (byte) (longitude >> 24);
        content[offset++] = (byte) (longitude >> 16);
        content[offset++] = (byte) (longitude >> 8);
        content[offset++] = (byte) longitude;
        
        // [54-57]: 纬度（4字节）
        content[offset++] = (byte) (latitude >> 24);
        content[offset++] = (byte) (latitude >> 16);
        content[offset++] = (byte) (latitude >> 8);
        content[offset++] = (byte) latitude;
        
        // [58-63]: 终端时间（BCD码6字节）
        byte[] timeBcd = stringToBcd(terminalTime);
        System.arraycopy(timeBcd, 0, content, offset, 6);
        offset += 6;
        
        // [64]: 司机状态（0=签到，1=签退）
        content[offset++] = (byte) driverStatus;
        
        // [65]: 设备签到方式（1=刷卡）
        content[offset++] = (byte) deviceSignMode;
        
        // [66-85]: SIM卡ICCID（20字节GBK）
        try {
            byte[] iccidBytes = simCardIccid.getBytes("GBK");
            int copyLen = Math.min(iccidBytes.length, 20);
            System.arraycopy(iccidBytes, 0, content, offset, copyLen);
        } catch (Exception ignored) {
        }
        
        return new Jt808PassthroughMessage(
                variant,
                terminalId,
                serialNumber,
                TYPE_DRIVER_SIGNIN,
                content
        );
    }

    /**
     * 字符串转BCD码（YYMMDDHHmmss → 6字节BCD）
     */
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

    /**
     * HEX字符串转字节数组
     */
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
