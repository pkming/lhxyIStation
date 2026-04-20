package com.lhxy.istationdevice.android11.protocol.legacy;

import java.util.List;

public interface DisplayProtocolGenerator {
    byte[] createNewspaperStation(BusLineSnapshot snapshot, DisplayLanguage language);

    byte[] createLineName(BusLineSnapshot snapshot, DisplayLanguage language);

    byte[] createLineState(BusLineSnapshot snapshot);

    ProtocolBatchResult createSiteInfo(List<BusLineSnapshot> snapshots);

    byte[] createInternalScreen(BusLineSnapshot snapshot, DisplayLanguage language);

    byte[] createServiceTone(byte value);
}

