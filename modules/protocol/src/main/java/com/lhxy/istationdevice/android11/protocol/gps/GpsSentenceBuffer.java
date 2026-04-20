package com.lhxy.istationdevice.android11.protocol.gps;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * GPS 串流分帧器
 * <p>
 * 复用旧项目的做法：遇到 '$' 开始，遇到 '\n' 结束。
 */
public final class GpsSentenceBuffer {
    private static final int MAX_SENTENCE_LENGTH = 512;

    private final StringBuilder builder = new StringBuilder(MAX_SENTENCE_LENGTH);
    private boolean frameStarted;

    /**
     * 喂一段串口字节，返回本次解析出的完整语句。
     */
    public List<String> accept(byte[] chunk) {
        List<String> sentences = new ArrayList<>();
        if (chunk == null || chunk.length == 0) {
            return sentences;
        }
        for (byte item : chunk) {
            char current = new String(new byte[]{item}, StandardCharsets.US_ASCII).charAt(0);
            if (current == '$') {
                frameStarted = true;
                builder.setLength(0);
                builder.append(current);
                continue;
            }
            if (!frameStarted) {
                continue;
            }
            if (current == '\n') {
                builder.append(current);
                sentences.add(builder.toString().trim());
                builder.setLength(0);
                frameStarted = false;
                continue;
            }
            if (builder.length() < MAX_SENTENCE_LENGTH) {
                builder.append(current);
            }
        }
        return sentences;
    }

    /**
     * 清空当前缓冲。
     */
    public void reset() {
        builder.setLength(0);
        frameStarted = false;
    }
}
