package android.media;

import android.media.MediaCodecInfo;

/* JADX INFO: loaded from: classes.dex */
public final class MediaCodecList {
    static final native int findCodecByName(String str);

    static final native MediaCodecInfo.CodecCapabilities getCodecCapabilities(int i, String str);

    public static final native int getCodecCount();

    static final native String getCodecName(int i);

    static final native String[] getSupportedTypes(int i);

    static final native boolean isEncoder(int i);

    private static final native void native_init();

    public static final MediaCodecInfo getCodecInfoAt(int i) {
        if (i < 0 || i > getCodecCount()) {
            throw new IllegalArgumentException();
        }
        return new MediaCodecInfo(i);
    }

    private MediaCodecList() {
    }

    static {
        System.loadLibrary("media_jni");
        native_init();
    }
}
