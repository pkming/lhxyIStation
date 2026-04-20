package com.lhxy.istationdevice.android11.protocol.gps;

import java.util.ArrayList;
import java.util.List;

/**
 * GPS 串流解析器
 * <p>
 * 把串口原始字节拆成语句，再按 RMC / GGA 合成定位快照。
 */
public final class GpsStreamParser {
    private final GpsSentenceBuffer sentenceBuffer = new GpsSentenceBuffer();
    private final GpsNmeaParser nmeaParser = new GpsNmeaParser();
    private GpsFixSnapshot latestSnapshot;

    /**
     * 喂一段原始串口字节，返回这次更新出的定位快照。
     */
    public List<GpsFixSnapshot> accept(byte[] chunk) {
        List<GpsFixSnapshot> snapshots = new ArrayList<>();
        List<String> sentences = sentenceBuffer.accept(chunk);
        for (String sentence : sentences) {
            GpsFixSnapshot nextSnapshot = nmeaParser.parseSentence(sentence, latestSnapshot);
            if (nextSnapshot != null && nextSnapshot != latestSnapshot) {
                latestSnapshot = nextSnapshot;
                snapshots.add(nextSnapshot);
            }
        }
        return snapshots;
    }

    /**
     * 当前最新的一份定位快照。
     */
    public GpsFixSnapshot getLatestSnapshot() {
        return latestSnapshot;
    }

    /**
     * 清空解析状态。
     */
    public void reset() {
        sentenceBuffer.reset();
        latestSnapshot = null;
    }
}
