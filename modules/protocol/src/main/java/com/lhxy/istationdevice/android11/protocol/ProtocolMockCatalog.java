package com.lhxy.istationdevice.android11.protocol;

import com.lhxy.istationdevice.android11.core.Hexs;
import com.lhxy.istationdevice.android11.protocol.jt808.Jt808ProtocolDemo;
import com.lhxy.istationdevice.android11.protocol.legacy.LegacyProtocolDemo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 协议 mock 样例目录
 * <p>
 * 把当前仓库里已经固化的协议样例统一整理成一份可读目录，
 * 方便工作日离线联调、导出调试包和后面继续补新协议文档映射。
 */
public final class ProtocolMockCatalog {
    private ProtocolMockCatalog() {
    }

    /**
     * 返回当前内置的全部 mock 样例。
     */
    public static List<MockSample> listAll() {
        List<MockSample> samples = new ArrayList<>();
        add(samples, "legacy-display", "旧屏显完整样例", "legacy/display", LegacyProtocolDemo.generateDemoEnvelopes());
        add(samples, "legacy-station", "旧屏显报站样例", "legacy/station", LegacyProtocolDemo.generateStationDemoEnvelopes());
        add(samples, "jt808-all", "JT808/AL808 完整样例", "jt808/full", Jt808ProtocolDemo.generateDemoEnvelopes());
        add(samples, "jt808-dispatch", "调度主链样例", "jt808/dispatch", Jt808ProtocolDemo.generateDispatchDemoEnvelopes());
        add(samples, "jt808-signin", "签到样例", "jt808/signin", Jt808ProtocolDemo.generateSignInDemoEnvelopes());
        add(samples, "jt808-upgrade", "升级样例", "jt808/upgrade", Jt808ProtocolDemo.generateUpgradeDemoEnvelopes());
        return Collections.unmodifiableList(samples);
    }

    /**
     * 输出适合调试包和文档留档的文本目录。
     */
    public static String describeCatalog() {
        List<MockSample> samples = listAll();
        StringBuilder builder = new StringBuilder("协议 mock 目录:");
        builder.append("\n- 样例组数量: ").append(samples.size());
        int envelopeCount = 0;
        for (MockSample sample : samples) {
            envelopeCount += sample.getEnvelopes().size();
        }
        builder.append("\n- 总报文数: ").append(envelopeCount);
        for (MockSample sample : samples) {
            builder.append("\n\n[").append(sample.getKey()).append("] ")
                    .append(sample.getTitle())
                    .append(" / ")
                    .append(sample.getCategory())
                    .append(" / ")
                    .append(sample.getEnvelopes().size())
                    .append(" 条");
            int index = 1;
            for (ProtocolEnvelope envelope : sample.getEnvelopes()) {
                builder.append("\n")
                        .append(index++)
                        .append(". ")
                        .append(envelope.getProtocolName())
                        .append(" -> ")
                        .append(envelope.getChannelName())
                        .append(" / ")
                        .append(Hexs.toHex(envelope.getPayload()));
            }
        }
        return builder.toString();
    }

    /**
     * 输出适合页面摘要展示的紧凑信息。
     */
    public static String describeCompact() {
        List<MockSample> samples = listAll();
        StringBuilder builder = new StringBuilder("协议 mock 摘要:");
        builder.append("\n- 样例组: ").append(samples.size());
        int envelopeCount = 0;
        for (MockSample sample : samples) {
            envelopeCount += sample.getEnvelopes().size();
        }
        builder.append("\n- 报文总数: ").append(envelopeCount);
        for (MockSample sample : samples) {
            builder.append("\n- ")
                    .append(sample.getKey())
                    .append(" -> ")
                    .append(sample.getEnvelopes().size())
                    .append(" 条");
        }
        return builder.toString();
    }

    private static void add(
            List<MockSample> target,
            String key,
            String title,
            String category,
            List<ProtocolEnvelope> envelopes
    ) {
        target.add(new MockSample(key, title, category, envelopes == null ? Collections.emptyList() : envelopes));
    }

    /**
     * 一组 mock 样例。
     */
    public static final class MockSample {
        private final String key;
        private final String title;
        private final String category;
        private final List<ProtocolEnvelope> envelopes;

        public MockSample(String key, String title, String category, List<ProtocolEnvelope> envelopes) {
            this.key = key == null ? "" : key;
            this.title = title == null ? "" : title;
            this.category = category == null ? "" : category;
            this.envelopes = Collections.unmodifiableList(new ArrayList<>(envelopes == null ? Collections.emptyList() : envelopes));
        }

        public String getKey() {
            return key;
        }

        public String getTitle() {
            return title;
        }

        public String getCategory() {
            return category;
        }

        public List<ProtocolEnvelope> getEnvelopes() {
            return envelopes;
        }
    }
}