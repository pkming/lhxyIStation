package com.lhxy.istationdevice.android11.protocol.legacy;

import com.lhxy.istationdevice.android11.protocol.ProtocolEnvelope;

import java.util.ArrayList;
import java.util.List;

public final class LegacyProtocolDemo {
    private LegacyProtocolDemo() {
    }

    /**
     * 生成报站 / 屏显样例。
     */
    public static List<ProtocolEnvelope> generateStationDemoEnvelopes() {
        return generateDemoEnvelopes();
    }

    /**
     * 生成完整屏显样例。
     */
    public static List<ProtocolEnvelope> generateDemoEnvelopes() {
        BusLineSnapshot snapshot = new BusLineSnapshot(
                3,
                1,
                1,
                16,
                "人民广场",
                "101路",
                "火车站",
                "科技园"
        );

        List<BusLineSnapshot> sites = new ArrayList<>();
        sites.add(new BusLineSnapshot(1, 1, 1, 16, "火车站", "101路", "火车站", "科技园"));
        sites.add(new BusLineSnapshot(2, 1, 1, 16, "市政府", "101路", "火车站", "科技园"));
        sites.add(snapshot);

        List<ProtocolEnvelope> envelopes = new ArrayList<>();

        TongDaDisplayProtocol tongDa = new TongDaDisplayProtocol();
        envelopes.add(new ProtocolEnvelope("TONGDA_LINE_NAME", "RS485", tongDa.createLineName(snapshot, DisplayLanguage.SIMPLIFIED_CHINESE)));
        envelopes.add(new ProtocolEnvelope("TONGDA_STATION", "RS485", tongDa.createNewspaperStation(snapshot, DisplayLanguage.SIMPLIFIED_CHINESE)));
        envelopes.add(new ProtocolEnvelope("TONGDA_SITE_INFO", "RS485", tongDa.createSiteInfo(sites).getPayload()));
        envelopes.add(new ProtocolEnvelope("TONGDA_INTERNAL", "RS485", tongDa.createInternalScreen(snapshot, DisplayLanguage.SIMPLIFIED_CHINESE)));

        LhxyDisplayProtocol lhxy = new LhxyDisplayProtocol();
        envelopes.add(new ProtocolEnvelope("LHXY_LINE_NAME", "RS485", lhxy.createLineName(snapshot, DisplayLanguage.SIMPLIFIED_CHINESE)));
        envelopes.add(new ProtocolEnvelope("LHXY_STATION", "RS485", lhxy.createNewspaperStation(snapshot, DisplayLanguage.SIMPLIFIED_CHINESE)));
        envelopes.add(new ProtocolEnvelope("LHXY_SITE_INFO", "RS485", lhxy.createSiteInfo(sites).getPayload()));
        envelopes.add(new ProtocolEnvelope("LHXY_SERVICE_TONE", "RS485", lhxy.createServiceTone((byte) 0x02)));

        HengWuDisplayProtocol hengWu = new HengWuDisplayProtocol();
        envelopes.add(new ProtocolEnvelope("HENGWU_LINE_NAME", "RS485", hengWu.createLineName(snapshot, DisplayLanguage.SIMPLIFIED_CHINESE)));
        envelopes.add(new ProtocolEnvelope("HENGWU_STATION", "RS485", hengWu.createNewspaperStation(snapshot, DisplayLanguage.ENGLISH)));

        return envelopes;
    }
}
