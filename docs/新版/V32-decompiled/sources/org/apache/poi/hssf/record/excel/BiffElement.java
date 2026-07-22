package org.apache.poi.hssf.record.excel;

import java.io.IOException;

/* JADX INFO: loaded from: classes3.dex */
public interface BiffElement {
    int getBinaryLength();

    int getType();

    void setLength(int i);

    void setType(int i);

    byte[] toBinary() throws IOException;
}
