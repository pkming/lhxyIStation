package com.lhxy.istationdevice.android11.domain.dispatch;

import com.lhxy.istationdevice.android11.core.AppLogCenter;
import com.lhxy.istationdevice.android11.core.Hexs;
import com.lhxy.istationdevice.android11.core.LogCategory;
import com.lhxy.istationdevice.android11.core.LogLevel;
import com.lhxy.istationdevice.android11.deviceapi.SerialPortAdapter;
import com.lhxy.istationdevice.android11.domain.config.ShellConfig;
import com.lhxy.istationdevice.android11.domain.module.state.DispatchState;
import com.lhxy.istationdevice.android11.domain.module.state.SignInState;
import com.lhxy.istationdevice.android11.domain.module.state.StationState;
import com.lhxy.istationdevice.android11.protocol.ProtocolPayloadExplainer;
import com.lhxy.istationdevice.android11.protocol.gps.GpsFixSnapshot;

import java.nio.charset.Charset;

/**
 * DVR 串口调度发送用例。
 * <p>
 * 先承接旧项目已经明确存在的几类串口上报帧，
 * 让 RS232-1 作为调度归属时不再只是页面状态切换。
 * 其中触摸协议当前按文档摘要实现第一版：55 AA + 05 + X/Y + 按下状态 + 校验。
 */
public final class DvrSerialDispatchUseCase {
    private static final Charset GB2312 = Charset.forName("GB2312");
    private final SerialPortAdapter serialPortAdapter;

    public DvrSerialDispatchUseCase(SerialPortAdapter serialPortAdapter) {
        this.serialPortAdapter = serialPortAdapter;
    }

    public boolean canUse(ShellConfig shellConfig) {
        return shellConfig != null
                && shellConfig.getBasicSetupConfig().getProtocolLinkageSettings().isSerialDispatchEnabled()
                && "DVR".equalsIgnoreCase(shellConfig.getBasicSetupConfig().getSerialSettings().getRs2321Protocol());
    }

    public void sendGpsReport(ShellConfig shellConfig, StationState stationState, GpsFixSnapshot snapshot, String traceId) {
        ShellConfig.SerialChannel channel = ensureReady(shellConfig, traceId);
        byte[] payload = buildGpsPayload(stationState, snapshot);
        send(channel, payload, "DVR_GPS_REPORT", traceId);
    }

    public void sendSiteInfo(ShellConfig shellConfig, StationState stationState, GpsFixSnapshot snapshot, String traceId) {
        ShellConfig.SerialChannel channel = ensureReady(shellConfig, traceId);
        byte[] payload = buildSiteInfoPayload(stationState, snapshot);
        send(channel, payload, "DVR_SITE_INFO", traceId);
    }

    public void sendDispatchReply(ShellConfig shellConfig, DispatchState dispatchState, int direction, int result, String traceId) {
        ShellConfig.SerialChannel channel = ensureReady(shellConfig, traceId);
        byte[] payload = buildDispatchReplyPayload(dispatchState, direction, result);
        send(channel, payload, "DVR_DISPATCH_REPLY", traceId);
    }

    public void sendStartBusReport(ShellConfig shellConfig, DispatchState dispatchState, String lineName, int direction, String traceId) {
        ShellConfig.SerialChannel channel = ensureReady(shellConfig, traceId);
        byte[] payload = buildStartBusPayload(dispatchState, safeLineName(lineName), direction);
        send(channel, payload, "DVR_START_BUS", traceId);
    }

    public void sendDriverAttendance(ShellConfig shellConfig, String lineName, SignInState signInState, String traceId) {
        ShellConfig.SerialChannel channel = ensureReady(shellConfig, traceId);
        byte[] payload = buildDriverAttendancePayload(safeLineName(lineName), signInState);
        send(channel, payload, "DVR_DRIVER_ATTENDANCE", traceId);
    }

    public void sendLowerReply(ShellConfig shellConfig, DispatchState dispatchState, int result, String traceId) {
        ShellConfig.SerialChannel channel = ensureReady(shellConfig, traceId);
        byte[] payload = buildLowerReplyPayload(dispatchState, result);
        send(channel, payload, "DVR_LOWER_REPLY", traceId);
    }

    public void sendKey(ShellConfig shellConfig, byte keyCode, String traceId) {
        ShellConfig.SerialChannel channel = ensureReady(shellConfig, traceId);
        byte[] payload = buildKeyPayload(keyCode);
        send(channel, payload, "DVR_KEY_EVENT", traceId);
    }

    public void sendTouchEvent(ShellConfig shellConfig, int x, int y, boolean pressed, String traceId) {
        ShellConfig.SerialChannel channel = ensureReady(shellConfig, traceId);
        byte[] payload = buildTouchPayload(x, y, pressed);
        send(channel, payload, "DVR_TOUCH_EVENT", traceId);
    }

    private ShellConfig.SerialChannel ensureReady(ShellConfig shellConfig, String traceId) {
        if (!canUse(shellConfig)) {
            throw new IllegalStateException("当前不是 DVR 串口调度模式");
        }
        ShellConfig.SerialChannel channel = shellConfig.requireSerialChannel("rs232_1");
        if (!serialPortAdapter.isOpen(channel.getPortName())) {
            serialPortAdapter.open(channel.toSerialPortConfig(), traceId + "-dvr-open");
        }
        return channel;
    }

    private void send(ShellConfig.SerialChannel channel, byte[] payload, String protocolName, String traceId) {
        AppLogCenter.log(
                LogCategory.PROTOCOL_TX,
                LogLevel.DEBUG,
                "DvrSerialDispatch",
                protocolName + " via " + channel.getKey() + " -> " + Hexs.toHex(payload)
                        + "\n- " + ProtocolPayloadExplainer.compactExplain(protocolName, payload),
                traceId
        );
        serialPortAdapter.send(channel.getPortName(), payload, traceId);
    }

    private byte[] buildGpsPayload(StationState stationState, GpsFixSnapshot snapshot) {
        byte[] payload = new byte[94];
        payload[0] = 0x55;
        payload[1] = 0x05;
        payload[2] = 0x58;
        payload[3] = 0x00;
        writeFixedText(payload, 4, 20, safeLineName(stationState.getLineName()));
        writeLittleEndianInt(payload, 24, (int) (System.currentTimeMillis() / 1000L));
        payload[28] = hemisphereFlag(snapshot == null ? "E" : snapshot.getLongitudeHemisphere(), "E");
        writeFixedText(payload, 29, 23, safeDecimal(snapshot == null ? null : snapshot.getLongitudeDecimal()));
        payload[52] = hemisphereFlag(snapshot == null ? "N" : snapshot.getLatitudeHemisphere(), "N");
        writeFixedText(payload, 53, 23, safeDecimal(snapshot == null ? null : snapshot.getLatitudeDecimal()));
        writeLittleEndianInt(payload, 76, toSpeedKmh(snapshot));
        writeLittleEndianInt(payload, 80, toAngle(snapshot));
        payload[84] = (byte) (resolveDirection(stationState) == 1 ? 0x00 : 0x01);
        payload[85] = (byte) stationNo(stationState);
        payload[86] = (byte) reportStatus(stationState);
        payload[87] = 0x01;
        payload[92] = checksum(payload, 92);
        payload[93] = (byte) 0xAA;
        return payload;
    }

    private byte[] buildSiteInfoPayload(StationState stationState, GpsFixSnapshot snapshot) {
        byte[] payload = new byte[70];
        payload[0] = 0x55;
        payload[1] = 0x06;
        payload[2] = 0x40;
        payload[3] = 0x00;
        writeFixedText(payload, 4, 20, safeLineName(stationState.getLineName()));
        payload[24] = (byte) (resolveDirection(stationState) == 1 ? 0x00 : 0x01);
        payload[28] = (byte) reportStatus(stationState);
        writeFixedText(payload, 32, 16, safeStationName(stationState.getCurrentStation()));
        payload[48] = 0x00;
        writeLittleEndianInt(payload, 52, (int) (System.currentTimeMillis() / 1000L));
        payload[56] = (byte) stationNo(stationState);
        writeLittleEndianInt(payload, 60, toSpeedKmh(snapshot));
        payload[68] = checksum(payload, 68);
        payload[69] = (byte) 0xAA;
        return payload;
    }

    private byte[] buildDispatchReplyPayload(DispatchState dispatchState, int direction, int result) {
        byte[] payload = new byte[38];
        payload[0] = 0x55;
        payload[1] = 0x10;
        payload[2] = 0x20;
        payload[3] = 0x00;
        writeLittleEndianInt(payload, 4, (int) dispatchState.getLastMsgSerialNo());
        writeLittleEndianInt(payload, 8, (int) dispatchState.getLineGuid());
        payload[12] = (byte) direction;
        payload[13] = (byte) dispatchState.getScheduleNoValue();
        payload[14] = (byte) dispatchState.getTimesNo();
        payload[15] = (byte) result;
        payload[36] = checksum(payload, 36);
        payload[37] = (byte) 0xAA;
        return payload;
    }

    private byte[] buildStartBusPayload(DispatchState dispatchState, String lineName, int direction) {
        byte[] payload = new byte[74];
        payload[0] = 0x55;
        payload[1] = 0x11;
        payload[2] = 0x44;
        payload[3] = 0x00;
        writeLittleEndianInt(payload, 4, (int) (System.currentTimeMillis() / 1000L));
        writeFixedText(payload, 8, 20, lineName);
        writeLittleEndianInt(payload, 28, (int) dispatchState.getLineGuid());
        payload[32] = (byte) direction;
        payload[33] = (byte) dispatchState.getScheduleNoValue();
        payload[34] = (byte) dispatchState.getTimesNo();
        payload[72] = checksum(payload, 72);
        payload[73] = (byte) 0xAA;
        return payload;
    }

    private byte[] buildDriverAttendancePayload(String lineName, SignInState signInState) {
        byte[] payload = new byte[42];
        payload[0] = 0x55;
        payload[1] = 0x0D;
        payload[2] = 0x24;
        payload[3] = 0x00;
        writeFixedText(payload, 4, 20, lineName);
        writeLittleEndianInt(payload, 24, (int) (System.currentTimeMillis() / 1000L));
        writeFixedText(payload, 28, 4, safeCardNo(signInState));
        writeLittleEndianInt(payload, 32, toDriverId(signInState));
        payload[36] = (byte) (signInState != null && signInState.isSignedIn() ? '1' : '0');
        payload[40] = checksum(payload, 40);
        payload[41] = (byte) 0xAA;
        return payload;
    }

    private byte[] buildLowerReplyPayload(DispatchState dispatchState, int result) {
        if (dispatchState == null || dispatchState.getPendingNoticeMsgSerialNo() <= 0) {
            throw new IllegalStateException("当前没有可回复的下发公告");
        }
        byte[] payload = new byte[14];
        payload[0] = 0x55;
        payload[1] = 0x13;
        payload[2] = 0x08;
        payload[3] = 0x00;
        writeLittleEndianInt(payload, 4, (int) dispatchState.getPendingNoticeMsgSerialNo());
        writeLittleEndianInt(payload, 8, result);
        payload[12] = checksum(payload, 12);
        payload[13] = (byte) 0xAA;
        return payload;
    }

    private byte[] buildKeyPayload(byte keyCode) {
        byte[] payload = new byte[10];
        payload[0] = 0x55;
        payload[1] = 0x14;
        payload[2] = 0x04;
        payload[3] = 0x00;
        payload[4] = keyCode;
        payload[8] = checksum(payload, 8);
        payload[9] = (byte) 0xAA;
        return payload;
    }

    private byte[] buildTouchPayload(int x, int y, boolean pressed) {
        // 文档摘要只给出 55 AA + 05 + X/Y + 按下/松开 + 校验，当前沿用小端坐标打包；
        // 真机阶段重点验证坐标范围与字节序是否完全匹配实际 DVR。
        byte[] payload = new byte[9];
        payload[0] = 0x55;
        payload[1] = (byte) 0xAA;
        payload[2] = 0x05;
        writeLittleEndianShort(payload, 3, clampCoordinate(x));
        writeLittleEndianShort(payload, 5, clampCoordinate(y));
        payload[7] = (byte) (pressed ? 0x01 : 0x00);
        payload[8] = checksum(payload, 8);
        return payload;
    }

    private void writeFixedText(byte[] payload, int start, int maxLength, String value) {
        byte[] raw = truncateGb2312(value, maxLength);
        for (int index = 0; index < raw.length; index++) {
            payload[start + index] = raw[index];
        }
        for (int index = raw.length; index < maxLength; index++) {
            payload[start + index] = 0x00;
        }
    }

    private byte[] truncateGb2312(String value, int maxLength) {
        String safeValue = value == null ? "-" : value.trim();
        if (safeValue.isEmpty()) {
            safeValue = "-";
        }
        byte[] raw = safeValue.getBytes(GB2312);
        if (raw.length <= maxLength) {
            return raw;
        }
        String truncated = safeValue;
        while (!truncated.isEmpty() && truncated.getBytes(GB2312).length > maxLength) {
            truncated = truncated.substring(0, truncated.length() - 1);
        }
        return truncated.getBytes(GB2312);
    }

    private void writeLittleEndianInt(byte[] payload, int offset, int value) {
        payload[offset] = (byte) (value & 0xFF);
        payload[offset + 1] = (byte) ((value >> 8) & 0xFF);
        payload[offset + 2] = (byte) ((value >> 16) & 0xFF);
        payload[offset + 3] = (byte) ((value >> 24) & 0xFF);
    }

    private void writeLittleEndianShort(byte[] payload, int offset, int value) {
        payload[offset] = (byte) (value & 0xFF);
        payload[offset + 1] = (byte) ((value >> 8) & 0xFF);
    }

    private byte checksum(byte[] payload, int endExclusive) {
        int sum = 0;
        for (int index = 0; index < endExclusive; index++) {
            sum += payload[index] & 0xFF;
        }
        return (byte) (sum & 0xFF);
    }

    private byte hemisphereFlag(String value, String positive) {
        return (byte) (positive.equalsIgnoreCase(emptyAsDash(value)) ? 0x31 : 0x30);
    }

    private int toSpeedKmh(GpsFixSnapshot snapshot) {
        if (snapshot == null || snapshot.getSpeedKnots() == null || snapshot.getSpeedKnots().trim().isEmpty()) {
            return 0;
        }
        try {
            return (int) Math.round(Double.parseDouble(snapshot.getSpeedKnots().trim()) * 1.852d);
        } catch (Exception ignore) {
            return 0;
        }
    }

    private int toAngle(GpsFixSnapshot snapshot) {
        if (snapshot == null || snapshot.getCourse() == null || snapshot.getCourse().trim().isEmpty()) {
            return 361;
        }
        try {
            return (int) Math.round(Double.parseDouble(snapshot.getCourse().trim()));
        } catch (Exception ignore) {
            return 361;
        }
    }

    private int resolveDirection(StationState stationState) {
        String directionText = stationState == null ? "" : emptyAsDash(stationState.getDirectionText());
        return directionText.contains("下") ? 2 : 1;
    }

    private int stationNo(StationState stationState) {
        String current = stationState == null ? "-" : emptyAsDash(stationState.getCurrentStation());
        return "-".equals(current) ? 0 : Math.max(1, stationState.getReportCount());
    }

    private int reportStatus(StationState stationState) {
        String phase = stationState == null ? "" : emptyAsDash(stationState.getReportPhase());
        return phase.contains("进站") || phase.contains("重复") || phase.contains("终点") ? 0 : 1;
    }

    private String safeLineName(String lineName) {
        return emptyAsDash(lineName);
    }

    private String safeStationName(String stationName) {
        return emptyAsDash(stationName);
    }

    private String safeDecimal(String value) {
        String safe = emptyAsDash(value);
        return "-".equals(safe) ? "0" : safe;
    }

    private int clampCoordinate(int value) {
        if (value < 0) {
            return 0;
        }
        return Math.min(value, 0xFFFF);
    }

    private String safeCardNo(SignInState signInState) {
        if (signInState == null) {
            return "0000";
        }
        String cardNo = emptyAsDash(signInState.getCardNo()).replace("-", "");
        if (cardNo.isEmpty()) {
            return "0000";
        }
        return cardNo.length() <= 4 ? cardNo : cardNo.substring(cardNo.length() - 4);
    }

    private int toDriverId(SignInState signInState) {
        if (signInState == null || signInState.getCardNo() == null) {
            return 0;
        }
        String digits = signInState.getCardNo().replaceAll("[^0-9]", "");
        if (digits.isEmpty()) {
            return 0;
        }
        if (digits.length() > 9) {
            digits = digits.substring(digits.length() - 9);
        }
        try {
            return Integer.parseInt(digits);
        } catch (Exception ignore) {
            return 0;
        }
    }

    private String emptyAsDash(String value) {
        return value == null || value.trim().isEmpty() ? "-" : value.trim();
    }
}