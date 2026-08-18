package android.media.videoeditor;

/* JADX INFO: loaded from: classes.dex */
public class VideoEditorProfile {
    public int maxInputVideoFrameHeight;
    public int maxInputVideoFrameWidth;
    public int maxOutputVideoFrameHeight;
    public int maxOutputVideoFrameWidth;

    private static final native int native_get_videoeditor_export_level(int i);

    private static final native int native_get_videoeditor_export_profile(int i);

    private static final native VideoEditorProfile native_get_videoeditor_profile();

    private static final native void native_init();

    static {
        System.loadLibrary("media_jni");
        native_init();
    }

    public static VideoEditorProfile get() {
        return native_get_videoeditor_profile();
    }

    public static int getExportProfile(int i) {
        if (i == 1 || i == 2 || i == 3) {
            return native_get_videoeditor_export_profile(i);
        }
        throw new IllegalArgumentException("Unsupported video codec" + i);
    }

    public static int getExportLevel(int i) {
        if (i == 1 || i == 2 || i == 3) {
            return native_get_videoeditor_export_level(i);
        }
        throw new IllegalArgumentException("Unsupported video codec" + i);
    }

    private VideoEditorProfile(int i, int i2, int i3, int i4) {
        this.maxInputVideoFrameWidth = i;
        this.maxInputVideoFrameHeight = i2;
        this.maxOutputVideoFrameWidth = i3;
        this.maxOutputVideoFrameHeight = i4;
    }
}
