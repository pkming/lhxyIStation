package com.lhxy.istationdevice.android11.protocol.jt808;

/**
 * 平台下发文本消息（0x8300）。
 *
 * <p>消息体首字节 flag 控制位定义：</p>
 * <ul>
 *   <li>bit0 (0x01): 紧急标志 - 紧急消息优先处理</li>
 *   <li>bit1 (0x02): 保留</li>
 *   <li>bit2 (0x04): 终端显示 - 在首页展示文本</li>
 *   <li>bit3 (0x08): TTS播报 - 语音播报文本内容</li>
 *   <li>bit4 (0x10): 司机端 - 文本发给司机（小喇叭播放）</li>
 *   <li>bit5 (0x20): 乘客端 - 文本发给乘客（内音播放+RS485转发）</li>
 *   <li>bit6 (0x40): RS485转发 - 通过RS485发送给第三方设备</li>
 *   <li>bit7 (0x80): 保留</li>
 * </ul>
 *
 * <p>对标现场版 M90：bit2=展示，bit3=播报。新增 bit0/bit4/bit5/bit6 扩展功能。</p>
 */
public final class Jt808TextMessageCommand {
    public static final int MESSAGE_ID = 0x8300;
    
    // 现场版已有标志位
    public static final int FLAG_EMERGENCY = 0x01;  // bit0: 紧急标志
    public static final int FLAG_DISPLAY = 0x04;    // bit2: 终端显示
    public static final int FLAG_SPEAK = 0x08;      // bit3: TTS播报
    
    // 新增扩展标志位（支持司机/乘客区分）
    public static final int FLAG_TO_DRIVER = 0x10;    // bit4: 给司机端
    public static final int FLAG_TO_PASSENGER = 0x20; // bit5: 给乘客端
    public static final int FLAG_SEND_RS485 = 0x40;   // bit6: RS485转发

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

    /**
     * 是否紧急消息（bit0）
     */
    public boolean isEmergency() {
        return (flag & FLAG_EMERGENCY) != 0;
    }

    /**
     * 是否终端显示（bit2）
     */
    public boolean shouldDisplay() {
        return (flag & FLAG_DISPLAY) != 0;
    }

    /**
     * 是否TTS播报（bit3）
     */
    public boolean shouldSpeak() {
        return (flag & FLAG_SPEAK) != 0;
    }

    /**
     * 是否发给司机端（bit4）
     * <p>司机端：小喇叭播放，不通过RS485转发</p>
     */
    public boolean isToDriver() {
        return (flag & FLAG_TO_DRIVER) != 0;
    }

    /**
     * 是否发给乘客端（bit5）
     * <p>乘客端：内音播放，需通过RS485转发给第三方设备</p>
     */
    public boolean isToPassenger() {
        return (flag & FLAG_TO_PASSENGER) != 0;
    }

    /**
     * 是否需要RS485转发（bit6）
     * <p>通过RS485接口发送给第三方设备（LED屏、LCD屏等）</p>
     */
    public boolean shouldSendRs485() {
        return (flag & FLAG_SEND_RS485) != 0;
    }

    /**
     * 是否有支持的动作（展示或播报）
     */
    public boolean hasSupportedAction() {
        return shouldDisplay() || shouldSpeak();
    }

    /**
     * 获取目标端类型描述
     */
    public String getTargetEndpoint() {
        if (isToDriver() && isToPassenger()) {
            return "司机+乘客";
        } else if (isToDriver()) {
            return "司机";
        } else if (isToPassenger()) {
            return "乘客";
        } else {
            return "全部";
        }
    }
}
