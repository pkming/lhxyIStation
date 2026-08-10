package com.lhxy.istationdevice.android11.protocol.jt808;

/** 平台调度控制（0x8B02）。 */
public final class Jt808DispatchControlCommand {
    public static final int MESSAGE_ID = 0x8B02;
    public static final int TYPE_SWITCH_UP = 0x01;
    public static final int TYPE_SWITCH_DOWN = 0x02;
    public static final int TYPE_SWITCH_RESERVED = 0x03;
    public static final int TYPE_CANCEL_PLAN = 0x8C;
    public static final int TYPE_UPDATE_TRIPS = 0x8D;

    private final Jt808Variant variant;
    private final String terminalId;
    private final int requestSerialNumber;
    private final long lineNumber;
    private final int type;
    private final String nextTrip;
    private final String thisTrip;
    private final String tomorrow;

    public Jt808DispatchControlCommand(
            Jt808Variant variant,
            String terminalId,
            int requestSerialNumber,
            long lineNumber,
            int type,
            String nextTrip,
            String thisTrip,
            String tomorrow
    ) {
        this.variant = variant;
        this.terminalId = terminalId;
        this.requestSerialNumber = requestSerialNumber;
        this.lineNumber = lineNumber;
        this.type = type;
        this.nextTrip = normalize(nextTrip);
        this.thisTrip = normalize(thisTrip);
        this.tomorrow = normalize(tomorrow);
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

    public long getLineNumber() {
        return lineNumber;
    }

    public int getType() {
        return type;
    }

    public String getNextTrip() {
        return nextTrip;
    }

    public String getThisTrip() {
        return thisTrip;
    }

    public String getTomorrow() {
        return tomorrow;
    }

    private static String normalize(String value) {
        if (value == null || value.trim().isEmpty() || "null".equalsIgnoreCase(value.trim())) {
            return "-";
        }
        return value.trim();
    }
}
