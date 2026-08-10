package com.lhxy.istationdevice.android11.protocol.jt808;

import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 解析旧 M90 自定义 0x8B01 调度计划消息。 */
public final class Jt808DispatchPlanCommandParser {
    private static final Charset GBK = Charset.forName("GBK");
    private static final Pattern SCHEDULE_PATTERN = Pattern.compile(
            "第\\s*(\\d+)\\s*趟.*?(\\d{1,2})\\s*点\\s*(\\d{1,2})\\s*分\\s*(\\d{1,2})\\s*秒"
    );

    private Jt808DispatchPlanCommandParser() {
    }

    public static Jt808DispatchPlanCommand parse(Jt808Frame frame) {
        if (frame == null) {
            throw new IllegalArgumentException("8B01 调度计划帧为空");
        }
        if (frame.getMessageId() != Jt808DispatchPlanCommand.MESSAGE_ID) {
            throw new IllegalArgumentException("不是 8B01 调度计划帧");
        }
        byte[] body = frame.getBody();
        if (body.length == 0) {
            throw new IllegalArgumentException("8B01 调度计划消息体为空");
        }
        String decoded = new String(body, GBK);
        int jsonStart = decoded.indexOf('{');
        int jsonEnd = findJsonEnd(decoded, jsonStart);
        if (jsonStart < 0 || jsonEnd < jsonStart) {
            throw new IllegalArgumentException("8B01 消息体缺少完整 JSON");
        }
        String json = decoded.substring(jsonStart, jsonEnd + 1);
        String scheduleText = requireJsonString(json, "sc");
        Matcher matcher = SCHEDULE_PATTERN.matcher(scheduleText);
        if (!matcher.find()) {
            throw new IllegalArgumentException("8B01 sc 字段缺少趟次或发车时间");
        }
        int timesNo = parseRange(matcher.group(1), "趟次", 0, 255);
        int hour = parseRange(matcher.group(2), "小时", 0, 23);
        int minute = parseRange(matcher.group(3), "分钟", 0, 59);
        int second = parseRange(matcher.group(4), "秒", 0, 59);
        return new Jt808DispatchPlanCommand(
                frame.getVariant(),
                frame.getTerminalId(),
                frame.getSerialNumber(),
                timesNo,
                String.format("%02d%02d%02d", hour, minute, second),
                optionalMinutes(json, "ot"),
                optionalMinutes(json, "ots"),
                optionalMinutes(json, "ts"),
                scheduleText
        );
    }

    static int findJsonEnd(String text, int start) {
        if (start < 0) {
            return -1;
        }
        int depth = 0;
        boolean inString = false;
        boolean escaped = false;
        for (int index = start; index < text.length(); index++) {
            char value = text.charAt(index);
            if (inString) {
                if (escaped) {
                    escaped = false;
                } else if (value == '\\') {
                    escaped = true;
                } else if (value == '"') {
                    inString = false;
                }
                continue;
            }
            if (value == '"') {
                inString = true;
            } else if (value == '{') {
                depth++;
            } else if (value == '}' && --depth == 0) {
                return index;
            }
        }
        return -1;
    }

    private static int optionalMinutes(String json, String key) {
        String value = findJsonValue(json, key);
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim())) {
            return -1;
        }
        return parseRange(value.trim(), key, 0, 24 * 60);
    }

    private static String requireJsonString(String json, String key) {
        String value = findJsonString(json, key);
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException("8B01 JSON 缺少 " + key + " 字段");
        }
        return value.trim();
    }

    static String findJsonString(String json, String key) {
        String marker = "\"" + key + "\"";
        int keyIndex = json.indexOf(marker);
        if (keyIndex < 0) {
            return null;
        }
        int colon = json.indexOf(':', keyIndex + marker.length());
        if (colon < 0) {
            return null;
        }
        int quote = colon + 1;
        while (quote < json.length() && Character.isWhitespace(json.charAt(quote))) {
            quote++;
        }
        if (quote >= json.length() || json.charAt(quote) != '"') {
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (int index = quote + 1; index < json.length(); index++) {
            char value = json.charAt(index);
            if (value == '"') {
                return result.toString();
            }
            if (value != '\\') {
                result.append(value);
                continue;
            }
            if (++index >= json.length()) {
                return null;
            }
            char escaped = json.charAt(index);
            if (escaped != 'u') {
                result.append(unescape(escaped));
                continue;
            }
            if (index + 4 >= json.length()) {
                return null;
            }
            try {
                result.append((char) Integer.parseInt(json.substring(index + 1, index + 5), 16));
            } catch (NumberFormatException e) {
                return null;
            }
            index += 4;
        }
        return null;
    }

    static String findJsonValue(String json, String key) {
        String stringValue = findJsonString(json, key);
        if (stringValue != null) {
            return stringValue;
        }
        String marker = "\"" + key + "\"";
        int keyIndex = json.indexOf(marker);
        if (keyIndex < 0) {
            return null;
        }
        int colon = json.indexOf(':', keyIndex + marker.length());
        if (colon < 0) {
            return null;
        }
        int start = colon + 1;
        while (start < json.length() && Character.isWhitespace(json.charAt(start))) {
            start++;
        }
        int end = start;
        while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') {
            end++;
        }
        return start < end ? json.substring(start, end).trim() : null;
    }

    private static char unescape(char value) {
        switch (value) {
            case 'n':
                return '\n';
            case 'r':
                return '\r';
            case 't':
                return '\t';
            case 'b':
                return '\b';
            case 'f':
                return '\f';
            default:
                return value;
        }
    }

    private static int parseRange(String value, String label, int minimum, int maximum) {
        try {
            int parsed = Integer.parseInt(value);
            if (parsed < minimum || parsed > maximum) {
                throw new IllegalArgumentException(label + "超出范围: " + value);
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(label + "不是数字: " + value);
        }
    }
}
