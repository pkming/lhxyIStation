package android.graphics;

import android.graphics.PorterDuff;

/* JADX INFO: loaded from: classes.dex */
public class PorterDuffColorFilter extends ColorFilter {
    private static native int nCreatePorterDuffFilter(int i, int i2, int i3);

    private static native int native_CreatePorterDuffFilter(int i, int i2);

    public PorterDuffColorFilter(int i, PorterDuff.Mode mode) {
        this.native_instance = native_CreatePorterDuffFilter(i, mode.nativeInt);
        this.nativeColorFilter = nCreatePorterDuffFilter(this.native_instance, i, mode.nativeInt);
    }
}
