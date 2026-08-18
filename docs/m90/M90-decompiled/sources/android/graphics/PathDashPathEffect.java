package android.graphics;

/* JADX INFO: loaded from: classes.dex */
public class PathDashPathEffect extends PathEffect {
    private static native int nativeCreate(int i, float f, float f2, int i2);

    public enum Style {
        TRANSLATE(0),
        ROTATE(1),
        MORPH(2);

        int native_style;

        Style(int i) {
            this.native_style = i;
        }
    }

    public PathDashPathEffect(Path path, float f, float f2, Style style) {
        this.native_instance = nativeCreate(path.ni(), f, f2, style.native_style);
    }
}
