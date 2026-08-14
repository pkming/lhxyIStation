package com.lhxy.istationdevice.android11.protocol.jt808;

import java.util.HashMap;
import java.util.Map;

/**
 * 平台设置终端参数（0x8103）。
 * 
 * <p>消息体包含多个参数项，每个参数项格式：参数ID(DWORD) + 参数长度(BYTE) + 参数值(N字节)</p>
 * 
 * <p>对标现场版 M90 支持的参数ID：</p>
 * <ul>
 *   <li>0x0001: 心跳间隔（秒）</li>
 *   <li>0x0029: GPS定位汇报间隔（秒）</li>
 *   <li>0x0055: 超速持续时间（秒）</li>
 *   <li>0x0056: 超速预警差值（1/10 km/h）</li>
 *   <li>0x0057: 疲劳驾驶预警差值（秒）</li>
 *   <li>其他参数待扩展...</li>
 * </ul>
 */
public final class Jt808SetTerminalParametersCommand {
    public static final int MESSAGE_ID = 0x8103;
    
    // 常用参数ID定义（对标现场版）
    public static final int PARAM_HEARTBEAT_INTERVAL = 0x0001;          // 心跳间隔(秒)
    public static final int PARAM_TCP_RESPONSE_TIMEOUT = 0x0002;        // TCP消息应答超时时间(秒)
    public static final int PARAM_TCP_RETRANSMIT_COUNT = 0x0003;        // TCP消息重传次数
    public static final int PARAM_UDP_RESPONSE_TIMEOUT = 0x0004;        // UDP消息应答超时时间(秒)
    public static final int PARAM_UDP_RETRANSMIT_COUNT = 0x0005;        // UDP消息重传次数
    public static final int PARAM_SMS_RESPONSE_TIMEOUT = 0x0006;        // SMS消息应答超时时间(秒)
    public static final int PARAM_SMS_RETRANSMIT_COUNT = 0x0007;        // SMS消息重传次数
    
    public static final int PARAM_GPS_REPORT_STRATEGY = 0x0020;         // 定位汇报策略
    public static final int PARAM_GPS_REPORT_SCHEME = 0x0021;           // 定位汇报方案
    public static final int PARAM_GPS_UNLOGIN_REPORT_INTERVAL = 0x0027; // 未登录定位汇报间隔(秒)
    public static final int PARAM_GPS_SLEEP_REPORT_INTERVAL = 0x0028;   // 休眠定位汇报间隔(秒)
    public static final int PARAM_GPS_URGENT_REPORT_INTERVAL = 0x0029;  // 紧急报警定位汇报间隔(秒)
    public static final int PARAM_GPS_DEFAULT_REPORT_INTERVAL = 0x002C; // 缺省位置汇报间隔(秒)
    
    public static final int PARAM_OVERSPEED_DURATION = 0x0055;          // 超速持续时间(秒)
    public static final int PARAM_OVERSPEED_ALARM_SPEED_DIFF = 0x0056;  // 超速预警差值(1/10 km/h)
    public static final int PARAM_FATIGUE_DRIVING_TIME_THRESHOLD = 0x0057; // 疲劳驾驶预警差值(秒)
    
    public static final int PARAM_COLLISION_ALARM_PARAM = 0x005D;       // 碰撞报警参数
    public static final int PARAM_ROLLOVER_ALARM_PARAM = 0x005E;        // 侧翻报警参数
    
    public static final int PARAM_VEHICLE_MILEAGE = 0x0080;             // 车辆里程表读数(1/10 km)
    public static final int PARAM_PROVINCE_ID = 0x0081;                 // 车辆所在省域ID
    public static final int PARAM_CITY_ID = 0x0082;                     // 车辆所在市域ID
    public static final int PARAM_VEHICLE_PLATE_NO = 0x0083;            // 车牌号码
    public static final int PARAM_VEHICLE_PLATE_COLOR = 0x0084;         // 车牌颜色

    private final Jt808Variant variant;
    private final String terminalId;
    private final int requestSerialNumber;
    private final Map<Integer, byte[]> parameters;  // 参数ID -> 参数值(原始字节)

    public Jt808SetTerminalParametersCommand(
            Jt808Variant variant,
            String terminalId,
            int requestSerialNumber,
            Map<Integer, byte[]> parameters
    ) {
        this.variant = variant;
        this.terminalId = terminalId;
        this.requestSerialNumber = requestSerialNumber;
        this.parameters = parameters == null ? new HashMap<>() : new HashMap<>(parameters);
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

    public Map<Integer, byte[]> getParameters() {
        return new HashMap<>(parameters);
    }

    /**
     * 获取DWORD参数值（4字节）
     */
    public Integer getParameterAsDword(int parameterId) {
        byte[] value = parameters.get(parameterId);
        if (value == null || value.length != 4) {
            return null;
        }
        return ((value[0] & 0xFF) << 24)
                | ((value[1] & 0xFF) << 16)
                | ((value[2] & 0xFF) << 8)
                | (value[3] & 0xFF);
    }

    /**
     * 获取WORD参数值（2字节）
     */
    public Integer getParameterAsWord(int parameterId) {
        byte[] value = parameters.get(parameterId);
        if (value == null || value.length != 2) {
            return null;
        }
        return ((value[0] & 0xFF) << 8) | (value[1] & 0xFF);
    }

    /**
     * 获取BYTE参数值（1字节）
     */
    public Integer getParameterAsByte(int parameterId) {
        byte[] value = parameters.get(parameterId);
        if (value == null || value.length != 1) {
            return null;
        }
        return value[0] & 0xFF;
    }

    /**
     * 获取字符串参数值（GBK编码）
     */
    public String getParameterAsString(int parameterId) {
        byte[] value = parameters.get(parameterId);
        if (value == null || value.length == 0) {
            return null;
        }
        try {
            return new String(value, "GBK").trim();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 是否包含指定参数
     */
    public boolean hasParameter(int parameterId) {
        return parameters.containsKey(parameterId);
    }

    /**
     * 获取参数数量
     */
    public int getParameterCount() {
        return parameters.size();
    }
}
