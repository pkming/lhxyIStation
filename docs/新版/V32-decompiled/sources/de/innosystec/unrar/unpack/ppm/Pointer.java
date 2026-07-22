package de.innosystec.unrar.unpack.ppm;

/* JADX INFO: loaded from: classes2.dex */
public abstract class Pointer {
    static final /* synthetic */ boolean $assertionsDisabled = false;
    protected byte[] mem;
    protected int pos;

    public Pointer(byte[] bArr) {
        this.mem = bArr;
    }

    public int getAddress() {
        return this.pos;
    }

    public void setAddress(int i) {
        this.pos = i;
    }
}
