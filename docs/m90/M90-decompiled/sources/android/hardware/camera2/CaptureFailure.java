package android.hardware.camera2;

/* JADX INFO: loaded from: classes.dex */
public class CaptureFailure {
    public static final int REASON_ERROR = 0;
    public static final int REASON_FLUSHED = 1;
    private final boolean mDropped;
    private final int mFrameNumber;
    private final int mReason;
    private final CaptureRequest mRequest;
    private final int mSequenceId;

    public CaptureFailure(CaptureRequest captureRequest, int i, boolean z, int i2, int i3) {
        this.mRequest = captureRequest;
        this.mReason = i;
        this.mDropped = z;
        this.mSequenceId = i2;
        this.mFrameNumber = i3;
    }

    public CaptureRequest getRequest() {
        return this.mRequest;
    }

    public int getFrameNumber() {
        return this.mFrameNumber;
    }

    public int getReason() {
        return this.mReason;
    }

    public boolean wasImageCaptured() {
        return !this.mDropped;
    }

    public int getSequenceId() {
        return this.mSequenceId;
    }
}
