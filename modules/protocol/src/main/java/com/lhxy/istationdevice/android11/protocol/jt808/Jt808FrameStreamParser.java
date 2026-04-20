package com.lhxy.istationdevice.android11.protocol.jt808;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * JT808 / AL808 串流分帧器
 * <p>
 * 把 Socket 收到的零散字节拼成完整的 0x7E 包裹帧。
 */
public final class Jt808FrameStreamParser {
    private static final int MAX_PENDING_BYTES = 8192;

    private final ByteArrayOutputStream pendingFrame = new ByteArrayOutputStream();
    private boolean inFrame;

    /**
     * 喂入一段原始字节，并返回当前能切出来的完整帧。
     */
    public synchronized List<byte[]> accept(byte[] payload) {
        List<byte[]> frames = new ArrayList<>();
        if (payload == null || payload.length == 0) {
            return frames;
        }

        for (byte item : payload) {
            if (!inFrame) {
                if ((item & 0xFF) == 0x7E) {
                    pendingFrame.reset();
                    pendingFrame.write(item);
                    inFrame = true;
                }
                continue;
            }

            pendingFrame.write(item);
            if ((item & 0xFF) == 0x7E) {
                frames.add(pendingFrame.toByteArray());
                pendingFrame.reset();
                inFrame = false;
                continue;
            }

            if (pendingFrame.size() > MAX_PENDING_BYTES) {
                reset();
            }
        }

        return frames;
    }

    /**
     * 清空当前缓冲。
     */
    public synchronized void reset() {
        pendingFrame.reset();
        inFrame = false;
    }
}
