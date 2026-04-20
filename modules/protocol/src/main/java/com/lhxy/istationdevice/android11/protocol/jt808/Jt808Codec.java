package com.lhxy.istationdevice.android11.protocol.jt808;

import java.io.ByteArrayOutputStream;

public final class Jt808Codec {
    public int nextSerialNumber() {
        return Jt808CodecSupport.nextSerialNumber();
    }

    public byte[] encode(Jt808Frame frame) {
        byte[] body = frame.getBody();
        ByteArrayOutputStream message = new ByteArrayOutputStream(12 + body.length + 1);
        write(message, Jt808CodecSupport.toWord(frame.getMessageId()));
        write(message, Jt808CodecSupport.toWord(body.length));
        write(message, Jt808CodecSupport.terminalIdBytes(frame.getTerminalId()));
        write(message, Jt808CodecSupport.toWord(frame.getSerialNumber()));
        write(message, body);

        byte[] payload = message.toByteArray();
        byte checksum = 0x00;
        for (byte item : payload) {
            checksum ^= item;
        }

        ByteArrayOutputStream withChecksum = new ByteArrayOutputStream(payload.length + 1);
        write(withChecksum, payload);
        withChecksum.write(checksum);

        byte[] escaped = Jt808CodecSupport.escape(withChecksum.toByteArray());
        ByteArrayOutputStream framed = new ByteArrayOutputStream(escaped.length + 2);
        framed.write(0x7E);
        write(framed, escaped);
        framed.write(0x7E);
        return framed.toByteArray();
    }

    private static void write(ByteArrayOutputStream output, byte[] bytes) {
        output.write(bytes, 0, bytes.length);
    }
}
