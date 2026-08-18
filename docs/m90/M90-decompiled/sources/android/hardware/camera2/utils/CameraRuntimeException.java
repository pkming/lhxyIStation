package android.hardware.camera2.utils;

import android.hardware.camera2.CameraAccessException;

/* JADX INFO: loaded from: classes.dex */
public class CameraRuntimeException extends RuntimeException {
    private Throwable mCause;
    private String mMessage;
    private final int mReason;

    public final int getReason() {
        return this.mReason;
    }

    public CameraRuntimeException(int i) {
        this.mReason = i;
    }

    public CameraRuntimeException(int i, String str) {
        super(str);
        this.mReason = i;
        this.mMessage = str;
    }

    public CameraRuntimeException(int i, String str, Throwable th) {
        super(str, th);
        this.mReason = i;
        this.mMessage = str;
        this.mCause = th;
    }

    public CameraRuntimeException(int i, Throwable th) {
        super(th);
        this.mReason = i;
        this.mCause = th;
    }

    public CameraAccessException asChecked() {
        CameraAccessException cameraAccessException;
        String str = this.mMessage;
        if (str != null && this.mCause != null) {
            cameraAccessException = new CameraAccessException(this.mReason, this.mMessage, this.mCause);
        } else if (str != null) {
            cameraAccessException = new CameraAccessException(this.mReason, this.mMessage);
        } else if (this.mCause != null) {
            cameraAccessException = new CameraAccessException(this.mReason, this.mCause);
        } else {
            cameraAccessException = new CameraAccessException(this.mReason);
        }
        cameraAccessException.setStackTrace(getStackTrace());
        return cameraAccessException;
    }
}
