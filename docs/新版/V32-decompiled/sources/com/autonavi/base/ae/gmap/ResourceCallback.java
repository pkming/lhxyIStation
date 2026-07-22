package com.autonavi.base.ae.gmap;

/* JADX INFO: loaded from: classes2.dex */
public class ResourceCallback {
    private long instance;

    private static native void nativeCallCancel();

    private static native void nativeCallFailed(long j, String str);

    private static native void nativeCallSuccess(long j, AMapAppResourceItem aMapAppResourceItem);

    public ResourceCallback() {
        this.instance = 0L;
    }

    public ResourceCallback(long j) {
        this.instance = 0L;
        this.instance = j;
    }

    public long getInstance() {
        return this.instance;
    }

    public void setInstance(long j) {
        this.instance = j;
    }

    public void callSuccess(AMapAppResourceItem aMapAppResourceItem) {
        nativeCallSuccess(this.instance, aMapAppResourceItem);
    }

    public void callFailed(String str) {
        nativeCallFailed(this.instance, str);
    }

    public void callCancel() {
        nativeCallCancel();
    }
}
