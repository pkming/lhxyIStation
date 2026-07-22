package android.media.videoeditor;

/* JADX INFO: loaded from: classes.dex */
public class EffectColor extends Effect {
    public static final int GRAY = 8355711;
    public static final int GREEN = 65280;
    public static final int PINK = 16737996;
    public static final int TYPE_COLOR = 1;
    public static final int TYPE_FIFTIES = 5;
    public static final int TYPE_GRADIENT = 2;
    public static final int TYPE_NEGATIVE = 4;
    public static final int TYPE_SEPIA = 3;
    private final int mColor;
    private final int mType;

    private EffectColor() {
        this(null, null, 0L, 0L, 0, 0);
    }

    public EffectColor(MediaItem mediaItem, String str, long j, long j2, int i, int i2) {
        super(mediaItem, str, j, j2);
        if (i == 1 || i == 2) {
            if (i2 == 65280 || i2 == 8355711 || i2 == 16737996) {
                this.mColor = i2;
            } else {
                throw new IllegalArgumentException("Invalid Color: " + i2);
            }
        } else if (i == 3 || i == 4 || i == 5) {
            this.mColor = -1;
        } else {
            throw new IllegalArgumentException("Invalid type: " + i);
        }
        this.mType = i;
    }

    public int getType() {
        return this.mType;
    }

    public int getColor() {
        return this.mColor;
    }
}
