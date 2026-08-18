package android.graphics;

/* JADX INFO: loaded from: classes.dex */
public class SumPathEffect extends PathEffect {
    private static native int nativeCreate(int i, int i2);

    public SumPathEffect(PathEffect pathEffect, PathEffect pathEffect2) {
        this.native_instance = nativeCreate(pathEffect.native_instance, pathEffect2.native_instance);
    }
}
