package android.media.videoeditor;

import android.graphics.Rect;

/* JADX INFO: loaded from: classes.dex */
public class EffectKenBurns extends Effect {
    private Rect mEndRect;
    private Rect mStartRect;

    private EffectKenBurns() {
        this(null, null, null, null, 0L, 0L);
    }

    public EffectKenBurns(MediaItem mediaItem, String str, Rect rect, Rect rect2, long j, long j2) {
        super(mediaItem, str, j, j2);
        if (rect.width() <= 0 || rect.height() <= 0) {
            throw new IllegalArgumentException("Invalid Start rectangle");
        }
        if (rect2.width() <= 0 || rect2.height() <= 0) {
            throw new IllegalArgumentException("Invalid End rectangle");
        }
        this.mStartRect = rect;
        this.mEndRect = rect2;
    }

    public Rect getStartRect() {
        return this.mStartRect;
    }

    public Rect getEndRect() {
        return this.mEndRect;
    }

    void getKenBurnsSettings(Rect rect, Rect rect2) {
        rect.left = getStartRect().left;
        rect.top = getStartRect().top;
        rect.right = getStartRect().right;
        rect.bottom = getStartRect().bottom;
        rect2.left = getEndRect().left;
        rect2.top = getEndRect().top;
        rect2.right = getEndRect().right;
        rect2.bottom = getEndRect().bottom;
    }
}
