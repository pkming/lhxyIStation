package android.widget;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/* JADX INFO: loaded from: classes.dex */
public class ScrollBarDrawable extends Drawable {
    private boolean mAlwaysDrawHorizontalTrack;
    private boolean mAlwaysDrawVerticalTrack;
    private boolean mChanged;
    private int mExtent;
    private Drawable mHorizontalThumb;
    private Drawable mHorizontalTrack;
    private int mOffset;
    private int mRange;
    private boolean mRangeChanged;
    private final Rect mTempBounds = new Rect();
    private boolean mVertical;
    private Drawable mVerticalThumb;
    private Drawable mVerticalTrack;

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    public void setAlwaysDrawHorizontalTrack(boolean z) {
        this.mAlwaysDrawHorizontalTrack = z;
    }

    public void setAlwaysDrawVerticalTrack(boolean z) {
        this.mAlwaysDrawVerticalTrack = z;
    }

    public boolean getAlwaysDrawVerticalTrack() {
        return this.mAlwaysDrawVerticalTrack;
    }

    public boolean getAlwaysDrawHorizontalTrack() {
        return this.mAlwaysDrawHorizontalTrack;
    }

    public void setParameters(int i, int i2, int i3, boolean z) {
        if (this.mVertical != z) {
            this.mChanged = true;
        }
        if (this.mRange != i || this.mOffset != i2 || this.mExtent != i3) {
            this.mRangeChanged = true;
        }
        this.mRange = i;
        this.mOffset = i2;
        this.mExtent = i3;
        this.mVertical = z;
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        boolean z;
        boolean z2 = this.mVertical;
        int i = this.mExtent;
        int i2 = this.mRange;
        boolean z3 = true;
        if (i <= 0 || i2 <= i) {
            z3 = z2 ? this.mAlwaysDrawVerticalTrack : this.mAlwaysDrawHorizontalTrack;
            z = false;
        } else {
            z = true;
        }
        Rect bounds = getBounds();
        if (canvas.quickReject(bounds.left, bounds.top, bounds.right, bounds.bottom, Canvas.EdgeType.AA)) {
            return;
        }
        if (z3) {
            drawTrack(canvas, bounds, z2);
        }
        if (z) {
            int iHeight = z2 ? bounds.height() : bounds.width();
            int iWidth = z2 ? bounds.width() : bounds.height();
            int iRound = Math.round((iHeight * i) / i2);
            int iRound2 = Math.round(((iHeight - iRound) * this.mOffset) / (i2 - i));
            int i3 = iWidth * 2;
            if (iRound < i3) {
                iRound = i3;
            }
            drawThumb(canvas, bounds, iRound2 + iRound > iHeight ? iHeight - iRound : iRound2, iRound, z2);
        }
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        this.mChanged = true;
    }

    protected void drawTrack(Canvas canvas, Rect rect, boolean z) {
        Drawable drawable;
        if (z) {
            drawable = this.mVerticalTrack;
        } else {
            drawable = this.mHorizontalTrack;
        }
        if (drawable != null) {
            if (this.mChanged) {
                drawable.setBounds(rect);
            }
            drawable.draw(canvas);
        }
    }

    protected void drawThumb(Canvas canvas, Rect rect, int i, int i2, boolean z) {
        Rect rect2 = this.mTempBounds;
        boolean z2 = this.mRangeChanged || this.mChanged;
        if (z2) {
            if (z) {
                rect2.set(rect.left, rect.top + i, rect.right, rect.top + i + i2);
            } else {
                rect2.set(rect.left + i, rect.top, rect.left + i + i2, rect.bottom);
            }
        }
        if (z) {
            Drawable drawable = this.mVerticalThumb;
            if (z2) {
                drawable.setBounds(rect2);
            }
            drawable.draw(canvas);
            return;
        }
        Drawable drawable2 = this.mHorizontalThumb;
        if (z2) {
            drawable2.setBounds(rect2);
        }
        drawable2.draw(canvas);
    }

    public void setVerticalThumbDrawable(Drawable drawable) {
        if (drawable != null) {
            this.mVerticalThumb = drawable;
        }
    }

    public void setVerticalTrackDrawable(Drawable drawable) {
        this.mVerticalTrack = drawable;
    }

    public void setHorizontalThumbDrawable(Drawable drawable) {
        if (drawable != null) {
            this.mHorizontalThumb = drawable;
        }
    }

    public void setHorizontalTrackDrawable(Drawable drawable) {
        this.mHorizontalTrack = drawable;
    }

    public int getSize(boolean z) {
        if (z) {
            Drawable drawable = this.mVerticalTrack;
            if (drawable == null) {
                drawable = this.mVerticalThumb;
            }
            return drawable.getIntrinsicWidth();
        }
        Drawable drawable2 = this.mHorizontalTrack;
        if (drawable2 == null) {
            drawable2 = this.mHorizontalThumb;
        }
        return drawable2.getIntrinsicHeight();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        Drawable drawable = this.mVerticalTrack;
        if (drawable != null) {
            drawable.setAlpha(i);
        }
        this.mVerticalThumb.setAlpha(i);
        Drawable drawable2 = this.mHorizontalTrack;
        if (drawable2 != null) {
            drawable2.setAlpha(i);
        }
        this.mHorizontalThumb.setAlpha(i);
    }

    @Override // android.graphics.drawable.Drawable
    public int getAlpha() {
        return this.mVerticalThumb.getAlpha();
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        Drawable drawable = this.mVerticalTrack;
        if (drawable != null) {
            drawable.setColorFilter(colorFilter);
        }
        this.mVerticalThumb.setColorFilter(colorFilter);
        Drawable drawable2 = this.mHorizontalTrack;
        if (drawable2 != null) {
            drawable2.setColorFilter(colorFilter);
        }
        this.mHorizontalThumb.setColorFilter(colorFilter);
    }

    public String toString() {
        return "ScrollBarDrawable: range=" + this.mRange + " offset=" + this.mOffset + " extent=" + this.mExtent + (this.mVertical ? " V" : " H");
    }
}
