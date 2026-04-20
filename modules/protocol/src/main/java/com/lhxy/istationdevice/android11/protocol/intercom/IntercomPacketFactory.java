package com.lhxy.istationdevice.android11.protocol.intercom;

import com.lhxy.istationdevice.android11.core.Hexs;

import java.io.ByteArrayOutputStream;
import java.time.Instant;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 对讲包工厂
 * <p>
 * 旧项目这块不是标准 JT808 帧，单独保留成一组协议工具。
 */
public final class IntercomPacketFactory {
    private static final byte[] HEADER = new byte[]{0x30, 0x31, 0x63, 0x64, (byte) 0x81, (byte) 0x86};
    private static final AtomicInteger NEXT_SERIAL = new AtomicInteger(1);

    /**
     * 按旧协议生成一包对讲数据。
     */
    public byte[] encode(String terminalId, int channelNumber, byte[] content) {
        int serialNumber = NEXT_SERIAL.getAndUpdate(current -> current >= 0xFFFF ? 1 : current + 1);
        long now = Instant.now().toEpochMilli();
        return encode(new IntercomPacket(serialNumber, terminalId, channelNumber, now, content));
    }

    /**
     * 按给定模型生成对讲数据。
     */
    public byte[] encode(IntercomPacket packet) {
        byte[] payload = packet.getContent();
        ByteArrayOutputStream output = new ByteArrayOutputStream(26 + payload.length);
        write(output, HEADER);
        write(output, toWord(packet.getSerialNumber()));
        write(output, terminalIdBytes(packet.getTerminalId()));
        output.write(packet.getChannelNumber() & 0xFF);
        output.write(0x30);
        write(output, toLongBytes(packet.getTimestampMillis()));
        write(output, toWord(payload.length));
        write(output, payload);
        return output.toByteArray();
    }

    private static byte[] toWord(int value) {
        return new byte[]{
                (byte) ((value >>> 8) & 0xFF),
                (byte) (value & 0xFF)
        };
    }

    private static byte[] terminalIdBytes(String terminalId) {
        byte[] raw = bcdFromDigits(terminalId);
        byte[] result = new byte[6];
        int copyLength = Math.min(result.length, raw.length);
        System.arraycopy(raw, 0, result, 0, copyLength);
        return result;
    }

    private static byte[] toLongBytes(long value) {
        return new byte[]{
                (byte) ((value >>> 56) & 0xFF),
                (byte) ((value >>> 48) & 0xFF),
                (byte) ((value >>> 40) & 0xFF),
                (byte) ((value >>> 32) & 0xFF),
                (byte) ((value >>> 24) & 0xFF),
                (byte) ((value >>> 16) & 0xFF),
                (byte) ((value >>> 8) & 0xFF),
                (byte) (value & 0xFF)
        };
    }

    private static byte[] bcdFromDigits(String value) {
        String digits = value == null ? "" : value.replaceAll("\\D", "");
        if (digits.isEmpty()) {
            return new byte[0];
        }
        if (digits.length() % 2 != 0) {
            digits = "0" + digits;
        }
        return Hexs.fromHex(digits);
    }

    private static void write(ByteArrayOutputStream output, byte[] bytes) {
        output.write(bytes, 0, bytes.length);
    }
}
