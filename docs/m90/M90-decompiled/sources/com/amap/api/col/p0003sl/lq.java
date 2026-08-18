package com.amap.api.col.p0003sl;

/* JADX INFO: compiled from: LogJsonDataStrategy.java */
/* JADX INFO: loaded from: classes2.dex */
public final class lq extends lt {
    private StringBuilder a;
    private boolean b;

    public lq() {
        this.a = new StringBuilder();
        this.b = true;
    }

    public lq(lt ltVar) {
        super(ltVar);
        this.a = new StringBuilder();
        this.b = true;
    }

    @Override // com.amap.api.col.p0003sl.lt
    protected final byte[] a(byte[] bArr) {
        byte[] bArrA = it.a(this.a.toString());
        this.d = bArrA;
        this.b = true;
        StringBuilder sb = this.a;
        sb.delete(0, sb.length());
        return bArrA;
    }

    @Override // com.amap.api.col.p0003sl.lt
    public final void b(byte[] bArr) {
        String strA = it.a(bArr);
        if (this.b) {
            this.b = false;
        } else {
            this.a.append(",");
        }
        this.a.append("{\"log\":\"").append(strA).append("\"}");
    }
}
