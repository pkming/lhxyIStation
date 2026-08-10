package com.lhxy.istationdevice.android11.protocol.jt808;

/**
 * 平台下发文本消息（0x8300）。
 *
 * <p>旧 M90 使用消息体首字节控制展示和播报：bit2=展示，bit3=播报。</p>
 */
public final class Jt808TextMessageCommand {
    public static final int MESSAGE_ID = 0x8300;
    public static final int FLAG_DISPLAY = 0x04;
    public static final int FLAG_SPEAK = 0x08;

    private final Jt808Variant variant;
    private final String terminalId;
    private final int requestSerialNumber;
    private final int flag;
    private final String content;

    public Jt808TextMessageCommand(
            Jt808Variant variant,
            String terminalId,
            int requestSerialNumber,
            int flag,
            String content
    ) {
        this.variant = variant;
        this.terminalId = terminalId;
        this.requestSerialNumber = requestSerialNumber;
        this.flag = flag;
        this.content = content == null ? "" : content.trim();
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

    public int getFlag() {
        return flag;
    }

    public String getContent() {
        return content;
    }

    public boolean shouldDisplay() {
        return (flag & FLAG_DISPLAY) != 0;
    }

    public boolean shouldSpeak() {
        return (flag & FLAG_SPEAK) != 0;
    }

    public boolean hasSupportedAction() {
        return shouldDisplay() || shouldSpeak();
    }
}
