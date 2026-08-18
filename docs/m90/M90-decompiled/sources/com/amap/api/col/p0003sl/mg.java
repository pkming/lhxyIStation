package com.amap.api.col.p0003sl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* JADX INFO: compiled from: AbstractBuilder.java */
/* JADX INFO: loaded from: classes2.dex */
public abstract class mg {
    mi a;
    private ByteBuffer b;

    mg(int i) {
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(i);
        this.b = byteBufferAllocate;
        byteBufferAllocate.order(ByteOrder.LITTLE_ENDIAN);
        this.a = new mi(this.b);
    }

    public final mg a() {
        this.a.a(this.b);
        return this;
    }
}
