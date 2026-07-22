package android.hardware.display.outputstate;

/* JADX INFO: loaded from: classes.dex */
public interface DispOutputState {
    public static final int DISPLAY_EXTERNAL = 2;
    public static final int DISPLAY_PRIMARY = 1;

    void devicePlugChanged(int i, int i2, boolean z);
}
