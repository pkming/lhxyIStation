package android.text.style;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Parcel;
import android.text.Layout;
import android.text.ParcelableSpan;
import android.text.Spanned;

/* JADX INFO: loaded from: classes.dex */
public class BulletSpan implements LeadingMarginSpan, ParcelableSpan {
    private static final int BULLET_RADIUS = 3;
    public static final int STANDARD_GAP_WIDTH = 2;
    private static Path sBulletPath;
    private final int mColor;
    private final int mGapWidth;
    private final boolean mWantColor;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.text.ParcelableSpan
    public int getSpanTypeId() {
        return 8;
    }

    public BulletSpan() {
        this.mGapWidth = 2;
        this.mWantColor = false;
        this.mColor = 0;
    }

    public BulletSpan(int i) {
        this.mGapWidth = i;
        this.mWantColor = false;
        this.mColor = 0;
    }

    public BulletSpan(int i, int i2) {
        this.mGapWidth = i;
        this.mWantColor = true;
        this.mColor = i2;
    }

    public BulletSpan(Parcel parcel) {
        this.mGapWidth = parcel.readInt();
        this.mWantColor = parcel.readInt() != 0;
        this.mColor = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mGapWidth);
        parcel.writeInt(this.mWantColor ? 1 : 0);
        parcel.writeInt(this.mColor);
    }

    @Override // android.text.style.LeadingMarginSpan
    public int getLeadingMargin(boolean z) {
        return this.mGapWidth + 6;
    }

    @Override // android.text.style.LeadingMarginSpan
    public void drawLeadingMargin(Canvas canvas, Paint paint, int i, int i2, int i3, int i4, int i5, CharSequence charSequence, int i6, int i7, boolean z, Layout layout) {
        if (((Spanned) charSequence).getSpanStart(this) == i6) {
            Paint.Style style = paint.getStyle();
            int color = 0;
            if (this.mWantColor) {
                color = paint.getColor();
                paint.setColor(this.mColor);
            }
            paint.setStyle(Paint.Style.FILL);
            if (canvas.isHardwareAccelerated()) {
                if (sBulletPath == null) {
                    Path path = new Path();
                    sBulletPath = path;
                    path.addCircle(0.0f, 0.0f, 3.6000001f, Path.Direction.CW);
                }
                canvas.save();
                canvas.translate(i + (i2 * 3), (i3 + i5) / 2.0f);
                canvas.drawPath(sBulletPath, paint);
                canvas.restore();
            } else {
                canvas.drawCircle(i + (i2 * 3), (i3 + i5) / 2.0f, 3.0f, paint);
            }
            if (this.mWantColor) {
                paint.setColor(color);
            }
            paint.setStyle(style);
        }
    }
}
