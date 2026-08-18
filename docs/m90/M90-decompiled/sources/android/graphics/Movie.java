package android.graphics;

import android.content.res.AssetManager;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/* JADX INFO: loaded from: classes.dex */
public class Movie {
    private final int mNativeMovie;

    public static native Movie decodeByteArray(byte[] bArr, int i, int i2);

    private static native Movie nativeDecodeAsset(int i);

    private static native Movie nativeDecodeStream(InputStream inputStream);

    private static native void nativeDestructor(int i);

    public native void draw(Canvas canvas, float f, float f2, Paint paint);

    public native int duration();

    public native int height();

    public native boolean isOpaque();

    public native boolean setTime(int i);

    public native int width();

    private Movie(int i) {
        if (i == 0) {
            throw new RuntimeException("native movie creation failed");
        }
        this.mNativeMovie = i;
    }

    public void draw(Canvas canvas, float f, float f2) {
        draw(canvas, f, f2, null);
    }

    public static Movie decodeStream(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        if (inputStream instanceof AssetManager.AssetInputStream) {
            return nativeDecodeAsset(((AssetManager.AssetInputStream) inputStream).getAssetInt());
        }
        return nativeDecodeStream(inputStream);
    }

    public static Movie decodeFile(String str) {
        try {
            return decodeTempStream(new FileInputStream(str));
        } catch (FileNotFoundException unused) {
            return null;
        }
    }

    protected void finalize() throws Throwable {
        try {
            nativeDestructor(this.mNativeMovie);
        } finally {
            super.finalize();
        }
    }

    private static Movie decodeTempStream(InputStream inputStream) {
        try {
            Movie movieDecodeStream = decodeStream(inputStream);
            try {
                inputStream.close();
                return movieDecodeStream;
            } catch (IOException unused) {
                return movieDecodeStream;
            }
        } catch (IOException unused2) {
            return null;
        }
    }
}
