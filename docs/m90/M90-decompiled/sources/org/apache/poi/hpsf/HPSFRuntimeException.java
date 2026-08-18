package org.apache.poi.hpsf;

/* JADX INFO: loaded from: classes3.dex */
public class HPSFRuntimeException extends RuntimeException {
    private Throwable reason;

    public HPSFRuntimeException() {
    }

    public HPSFRuntimeException(String str) {
        super(str);
    }

    public HPSFRuntimeException(Throwable th) {
        this.reason = th;
    }

    public HPSFRuntimeException(String str, Throwable th) {
        super(str);
        this.reason = th;
    }

    public Throwable getReason() {
        return this.reason;
    }
}
