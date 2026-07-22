package android.graphics;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class Picture {
    private static final int WORKING_STREAM_STORAGE = 16384;
    public final boolean createdFromStream;
    private final int mNativePicture;
    private Canvas mRecordingCanvas;

    private static native int nativeBeginRecording(int i, int i2, int i3);

    private static native int nativeConstructor(int i);

    private static native int nativeCreateFromStream(InputStream inputStream, byte[] bArr);

    private static native void nativeDestructor(int i);

    private static native void nativeDraw(int i, int i2);

    private static native void nativeEndRecording(int i);

    private static native boolean nativeWriteToStream(int i, OutputStream outputStream, byte[] bArr);

    public native int getHeight();

    public native int getWidth();

    public Picture() {
        this(nativeConstructor(0), false);
    }

    public Picture(Picture picture) {
        this(nativeConstructor(picture != null ? picture.mNativePicture : 0), false);
    }

    public Canvas beginRecording(int i, int i2) {
        RecordingCanvas recordingCanvas = new RecordingCanvas(this, nativeBeginRecording(this.mNativePicture, i, i2));
        this.mRecordingCanvas = recordingCanvas;
        return recordingCanvas;
    }

    public void endRecording() {
        if (this.mRecordingCanvas != null) {
            this.mRecordingCanvas = null;
            nativeEndRecording(this.mNativePicture);
        }
    }

    public void draw(Canvas canvas) {
        if (this.mRecordingCanvas != null) {
            endRecording();
        }
        nativeDraw(canvas.mNativeCanvas, this.mNativePicture);
    }

    @Deprecated
    public static Picture createFromStream(InputStream inputStream) {
        return new Picture(nativeCreateFromStream(inputStream, new byte[16384]), true);
    }

    @Deprecated
    public void writeToStream(OutputStream outputStream) {
        Objects.requireNonNull(outputStream);
        if (!nativeWriteToStream(this.mNativePicture, outputStream, new byte[16384])) {
            throw new RuntimeException();
        }
    }

    protected void finalize() throws Throwable {
        try {
            nativeDestructor(this.mNativePicture);
        } finally {
            super.finalize();
        }
    }

    final int ni() {
        return this.mNativePicture;
    }

    private Picture(int i, boolean z) {
        if (i == 0) {
            throw new RuntimeException();
        }
        this.mNativePicture = i;
        this.createdFromStream = z;
    }

    private static class RecordingCanvas extends Canvas {
        private final Picture mPicture;

        public RecordingCanvas(Picture picture, int i) {
            super(i);
            this.mPicture = picture;
        }

        @Override // android.graphics.Canvas
        public void setBitmap(Bitmap bitmap) {
            throw new RuntimeException("Cannot call setBitmap on a picture canvas");
        }

        @Override // android.graphics.Canvas
        public void drawPicture(Picture picture) {
            if (this.mPicture == picture) {
                throw new RuntimeException("Cannot draw a picture into its recording canvas");
            }
            super.drawPicture(picture);
        }
    }
}
