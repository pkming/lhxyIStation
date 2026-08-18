package android.filterfw.core;

/* JADX INFO: loaded from: classes.dex */
public class NativeBuffer {
    private Frame mAttachedFrame;
    private long mDataPointer;
    private boolean mOwnsData;
    private int mRefCount;
    private int mSize;

    private native boolean allocate(int i);

    private native boolean deallocate(boolean z);

    private native boolean nativeCopyTo(NativeBuffer nativeBuffer);

    public int getElementSize() {
        return 1;
    }

    public NativeBuffer() {
        this.mDataPointer = 0L;
        this.mSize = 0;
        this.mOwnsData = false;
        this.mRefCount = 1;
    }

    public NativeBuffer(int i) {
        this.mDataPointer = 0L;
        this.mSize = 0;
        this.mOwnsData = false;
        this.mRefCount = 1;
        allocate(i * getElementSize());
        this.mOwnsData = true;
    }

    public NativeBuffer mutableCopy() {
        try {
            NativeBuffer nativeBuffer = (NativeBuffer) getClass().newInstance();
            if (this.mSize <= 0 || nativeCopyTo(nativeBuffer)) {
                return nativeBuffer;
            }
            throw new RuntimeException("Failed to copy NativeBuffer to mutable instance!");
        } catch (Exception unused) {
            throw new RuntimeException("Unable to allocate a copy of " + getClass() + "! Make sure the class has a default constructor!");
        }
    }

    public int size() {
        return this.mSize;
    }

    public int count() {
        if (this.mDataPointer != 0) {
            return this.mSize / getElementSize();
        }
        return 0;
    }

    public NativeBuffer retain() {
        Frame frame = this.mAttachedFrame;
        if (frame != null) {
            frame.retain();
        } else if (this.mOwnsData) {
            this.mRefCount++;
        }
        return this;
    }

    /* JADX WARN: Removed duplicated region for block: B:7:0x000d  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public android.filterfw.core.NativeBuffer release() {
        /*
            r3 = this;
            android.filterfw.core.Frame r0 = r3.mAttachedFrame
            r1 = 1
            r2 = 0
            if (r0 == 0) goto L10
            android.filterfw.core.Frame r0 = r0.release()
            if (r0 != 0) goto Ld
            goto Le
        Ld:
            r1 = r2
        Le:
            r2 = r1
            goto L1c
        L10:
            boolean r0 = r3.mOwnsData
            if (r0 == 0) goto L1c
            int r0 = r3.mRefCount
            int r0 = r0 - r1
            r3.mRefCount = r0
            if (r0 != 0) goto Ld
            goto Le
        L1c:
            if (r2 == 0) goto L25
            boolean r0 = r3.mOwnsData
            r3.deallocate(r0)
            r0 = 0
            return r0
        L25:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: android.filterfw.core.NativeBuffer.release():android.filterfw.core.NativeBuffer");
    }

    public boolean isReadOnly() {
        Frame frame = this.mAttachedFrame;
        if (frame != null) {
            return frame.isReadOnly();
        }
        return false;
    }

    static {
        System.loadLibrary("filterfw");
    }

    void attachToFrame(Frame frame) {
        this.mAttachedFrame = frame;
    }

    protected void assertReadable() {
        Frame frame;
        if (this.mDataPointer == 0 || this.mSize == 0 || !((frame = this.mAttachedFrame) == null || frame.hasNativeAllocation())) {
            throw new NullPointerException("Attempting to read from null data frame!");
        }
    }

    protected void assertWritable() {
        if (isReadOnly()) {
            throw new RuntimeException("Attempting to modify read-only native (structured) data!");
        }
    }
}
