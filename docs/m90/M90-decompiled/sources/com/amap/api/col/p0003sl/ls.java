package com.amap.api.col.p0003sl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/* JADX INFO: compiled from: StatisticsPubDataStrategy.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ls extends lt {
    public ls() {
    }

    public ls(lt ltVar) {
        super(ltVar);
    }

    @Override // com.amap.api.col.p0003sl.lt
    protected final byte[] a(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(new SimpleDateFormat("yyyyMMdd HHmmss").format(new Date()));
        stringBuffer.append(" ");
        stringBuffer.append(UUID.randomUUID().toString());
        stringBuffer.append(" ");
        if (stringBuffer.length() != 53) {
            return new byte[0];
        }
        byte[] bArrA = it.a(stringBuffer.toString());
        byte[] bArr2 = new byte[bArrA.length + bArr.length];
        System.arraycopy(bArrA, 0, bArr2, 0, bArrA.length);
        System.arraycopy(bArr, 0, bArr2, bArrA.length, bArr.length);
        return bArr2;
    }
}
