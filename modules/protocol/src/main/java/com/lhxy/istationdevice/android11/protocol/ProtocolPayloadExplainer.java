package com.lhxy.istationdevice.android11.protocol;

import com.lhxy.istationdevice.android11.protocol.jt808.Jt808FrameInspector;

/**
 * 协议负载解释器
 * <p>
 * 当前先补一层轻量解释，让日志不只剩原始 HEX。
 */
public final class ProtocolPayloadExplainer {
    private ProtocolPayloadExplainer() {
    }

    /**
     * 输出紧凑摘要，适合直接拼到日志里。
     */
    public static String compactExplain(String protocolName, byte[] payload) {
        int length = payload == null ? 0 : payload.length;
        StringBuilder builder = new StringBuilder("bytes=").append(length);
        String protocolSummary = describeProtocolName(protocolName);
        if (!protocolSummary.isEmpty()) {
            builder.append(" / ").append(protocolSummary);
        }
        String protocolDetail = compactProtocolDetail(protocolName, payload);
        if (!protocolDetail.isEmpty()) {
            builder.append(" / ").append(protocolDetail);
        }
        if (looksLikeJt808(protocolName, payload)) {
            builder.append(" / ").append(compactJt808Inspect(Jt808FrameInspector.inspect(payload)));
        }
        return builder.toString();
    }

    private static String compactProtocolDetail(String protocolName, byte[] payload) {
        if (protocolName == null || payload == null) {
            return "";
        }
        String name = protocolName.trim().toUpperCase();
        switch (name) {
            case "DVR_KEY_EVENT":
                return describeDvrKeyEvent(payload);
            case "DVR_TOUCH_EVENT":
                return describeDvrTouchEvent(payload);
            default:
                return "";
        }
    }

    private static String describeProtocolName(String protocolName) {
        if (protocolName == null) {
            return "";
        }
        String name = protocolName.trim().toUpperCase();
        if (name.isEmpty()) {
            return "";
        }
        switch (name) {
            case "TONGDA_LINE_NAME":
                return "通达/线路名";
            case "TONGDA_STATION":
                return "通达/报站";
            case "TONGDA_SITE_INFO":
                return "通达/站序信息";
            case "TONGDA_INTERNAL":
                return "通达/内屏";
            case "LHXY_LINE_NAME":
                return "LHXY/线路名";
            case "LHXY_STATION":
                return "LHXY/报站";
            case "LHXY_SITE_INFO":
                return "LHXY/站序信息";
            case "LHXY_SERVICE_TONE":
                return "LHXY/服务音";
            case "HENGWU_LINE_NAME":
                return "恒舞/线路名";
            case "HENGWU_STATION":
                return "恒舞/报站";
            case "DVR_GPS_REPORT":
                return "DVR/GPS上报";
            case "DVR_SITE_INFO":
                return "DVR/站点信息";
            case "DVR_DISPATCH_REPLY":
                return "DVR/调度回复";
            case "DVR_START_BUS":
                return "DVR/发车上报";
            case "DVR_DRIVER_ATTENDANCE":
                return "DVR/司机考勤";
            case "DVR_LOWER_REPLY":
                return "DVR/下发回复";
            case "DVR_KEY_EVENT":
                return "DVR/键位事件";
            case "DVR_TOUCH_EVENT":
                return "DVR/触摸事件";
            default:
                if (name.contains("REGISTER")) {
                    return "注册";
                }
                if (name.contains("AUTHORITY")) {
                    return "鉴权";
                }
                if (name.contains("HEARTBEAT")) {
                    return "心跳";
                }
                if (name.contains("POSITION")) {
                    return "位置汇报";
                }
                if (name.contains("REPORT_STATION")) {
                    return "报站信息";
                }
                if (name.contains("GENERAL_RESPONSE")) {
                    return "通用应答";
                }
                if (name.contains("DRIVER_ATTENDANCE")) {
                    return "司机考勤";
                }
                if (name.contains("TIMING_RESPONSE")) {
                    return "校时应答";
                }
                if (name.contains("TERMINAL_PROPERTIES")) {
                    return "终端属性";
                }
                if (name.contains("REQUEST_MESSAGE")) {
                    return "请求消息";
                }
                if (name.contains("UPGRADE_NOTIFICATION")) {
                    return "升级通知";
                }
                if (name.contains("VIOLATION")) {
                    return "违规信息";
                }
                if (name.contains("SWITCH_LINE_RESPONSE")) {
                    return "切换线路应答";
                }
                if (name.contains("LINE_SWITCH")) {
                    return "线路切换";
                }
                return "";
        }
    }

    private static boolean looksLikeJt808(String protocolName, byte[] payload) {
        if (payload == null || payload.length < 2) {
            return false;
        }
        boolean wrapped = (payload[0] & 0xFF) == 0x7E && (payload[payload.length - 1] & 0xFF) == 0x7E;
        if (wrapped) {
            return true;
        }
        String name = protocolName == null ? "" : protocolName.toUpperCase();
        return name.contains("JT808") || name.contains("AL808");
    }

    private static String compactJt808Inspect(String inspectText) {
        if (inspectText == null || inspectText.trim().isEmpty()) {
            return "jt808=empty";
        }
        StringBuilder builder = new StringBuilder();
        int count = 0;
        for (String line : inspectText.split("\\n")) {
            String text = line == null ? "" : line.trim();
            if (text.isEmpty() || "解析结果:".equals(text)) {
                continue;
            }
            if (text.startsWith("- 原始 HEX") || text.startsWith("- 去转义 HEX") || text.startsWith("- 消息体 HEX")) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(" | ");
            }
            builder.append(text.replace("- ", ""));
            count++;
            if (count >= 3) {
                break;
            }
        }
        return builder.length() == 0 ? "jt808=no-summary" : builder.toString();
    }

    private static String describeDvrKeyEvent(byte[] payload) {
        if (payload.length < 5) {
            return "dvr-key=invalid";
        }
        return "key=0x" + twoHex(payload[4] & 0xFF);
    }

    private static String describeDvrTouchEvent(byte[] payload) {
        if (payload.length < 9) {
            return "dvr-touch=invalid";
        }
        int x = readLittleEndianShort(payload, 3);
        int y = readLittleEndianShort(payload, 5);
        String phase = (payload[7] & 0xFF) == 0x00 ? "up" : "down/move";
        return "phase=" + phase + " / x=" + x + " / y=" + y + " / checksum=0x" + twoHex(payload[8] & 0xFF);
    }

    private static int readLittleEndianShort(byte[] payload, int offset) {
        if (payload == null || payload.length <= offset + 1) {
            return 0;
        }
        return (payload[offset] & 0xFF) | ((payload[offset + 1] & 0xFF) << 8);
    }

    private static String twoHex(int value) {
        String hex = Integer.toHexString(value & 0xFF).toUpperCase();
        return hex.length() >= 2 ? hex : "0" + hex;
    }
}