package android.speech.srec;

import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
public final class MicrophoneInputStream extends InputStream {
    private static final String TAG = "MicrophoneInputStream";
    private int mAudioRecord;
    private byte[] mOneByte = new byte[1];

    private static native void AudioRecordDelete(int i) throws IOException;

    private static native int AudioRecordNew(int i, int i2);

    private static native int AudioRecordRead(int i, byte[] bArr, int i2, int i3) throws IOException;

    private static native int AudioRecordStart(int i);

    private static native void AudioRecordStop(int i) throws IOException;

    static {
        System.loadLibrary("srec_jni");
    }

    public MicrophoneInputStream(int i, int i2) throws IOException {
        this.mAudioRecord = 0;
        int iAudioRecordNew = AudioRecordNew(i, i2);
        this.mAudioRecord = iAudioRecordNew;
        if (iAudioRecordNew == 0) {
            throw new IOException("AudioRecord constructor failed - busy?");
        }
        int iAudioRecordStart = AudioRecordStart(iAudioRecordNew);
        if (iAudioRecordStart == 0) {
            return;
        }
        close();
        throw new IOException("AudioRecord start failed: " + iAudioRecordStart);
    }

    @Override // java.io.InputStream
    public int read() throws IOException {
        int i = this.mAudioRecord;
        if (i == 0) {
            throw new IllegalStateException("not open");
        }
        if (AudioRecordRead(i, this.mOneByte, 0, 1) == 1) {
            return this.mOneByte[0] & 255;
        }
        return -1;
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr) throws IOException {
        int i = this.mAudioRecord;
        if (i == 0) {
            throw new IllegalStateException("not open");
        }
        return AudioRecordRead(i, bArr, 0, bArr.length);
    }

    @Override // java.io.InputStream
    public int read(byte[] bArr, int i, int i2) throws IOException {
        int i3 = this.mAudioRecord;
        if (i3 == 0) {
            throw new IllegalStateException("not open");
        }
        return AudioRecordRead(i3, bArr, i, i2);
    }

    @Override // java.io.InputStream, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        int i = this.mAudioRecord;
        if (i != 0) {
            try {
                AudioRecordStop(i);
                try {
                    AudioRecordDelete(this.mAudioRecord);
                } finally {
                }
            } catch (Throwable th) {
                try {
                    AudioRecordDelete(this.mAudioRecord);
                    throw th;
                } finally {
                }
            }
        }
    }

    protected void finalize() throws Throwable {
        if (this.mAudioRecord == 0) {
            return;
        }
        close();
        throw new IOException("someone forgot to close MicrophoneInputStream");
    }
}
