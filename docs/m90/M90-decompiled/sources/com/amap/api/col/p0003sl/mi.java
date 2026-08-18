package com.amap.api.col.p0003sl;

import java.nio.ByteBuffer;

/* JADX INFO: compiled from: RobustFlatBufferBuilder.java */
/* JADX INFO: loaded from: classes2.dex */
public final class mi extends ob {
    mi(ByteBuffer byteBuffer) {
        super(byteBuffer);
    }

    @Override // com.amap.api.col.p0003sl.ob
    public final int a(CharSequence charSequence) {
        try {
            return super.a(charSequence);
        } catch (Throwable th) {
            nu.a(th);
            return super.a("");
        }
    }
}
