package com.amap.api.col.p0003sl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/* JADX INFO: compiled from: ByteJoinDataStrategy.java */
/* JADX INFO: loaded from: classes2.dex */
public final class ln extends lt {
    ByteArrayOutputStream a;

    public ln() {
        this.a = new ByteArrayOutputStream();
    }

    public ln(lt ltVar) {
        super(ltVar);
        this.a = new ByteArrayOutputStream();
    }

    @Override // com.amap.api.col.p0003sl.lt
    protected final byte[] a(byte[] bArr) {
        byte[] byteArray = this.a.toByteArray();
        try {
            this.a.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.a = new ByteArrayOutputStream();
        return byteArray;
    }

    @Override // com.amap.api.col.p0003sl.lt
    public final void b(byte[] bArr) {
        try {
            this.a.write(bArr);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }
}
