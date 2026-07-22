package org.apache.poi.hssf.record;

/* JADX INFO: loaded from: classes3.dex */
public interface CustomField extends Cloneable {
    int fillField(byte[] bArr, short s, int i);

    int getSize();

    int serializeField(int i, byte[] bArr);

    void toString(StringBuffer stringBuffer);
}
