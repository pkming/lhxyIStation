package android.graphics.drawable;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Insets;
import android.graphics.NinePatch;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.Region;
import android.graphics.Xfermode;
import android.os.BatteryManager;
import android.os.Trace;
import android.provider.CalendarContract;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.StateSet;
import android.util.TypedValue;
import android.util.Xml;
import com.android.internal.R;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Arrays;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public abstract class Drawable {
    private static final Rect ZERO_BOUNDS_RECT = new Rect();
    private int mLayoutDirection;
    private int[] mStateSet = StateSet.WILD_CARD;
    private int mLevel = 0;
    private int mChangingConfigurations = 0;
    private Rect mBounds = ZERO_BOUNDS_RECT;
    private WeakReference<Callback> mCallback = null;
    private boolean mVisible = true;

    public interface Callback {
        void invalidateDrawable(Drawable drawable);

        void scheduleDrawable(Drawable drawable, Runnable runnable, long j);

        void unscheduleDrawable(Drawable drawable, Runnable runnable);
    }

    public static int resolveOpacity(int i, int i2) {
        if (i == i2) {
            return i;
        }
        if (i == 0 || i2 == 0) {
            return 0;
        }
        int i3 = -3;
        if (i != -3 && i2 != -3) {
            i3 = -2;
            if (i != -2 && i2 != -2) {
                return -1;
            }
        }
        return i3;
    }

    public abstract void draw(Canvas canvas);

    public int getAlpha() {
        return 255;
    }

    public ConstantState getConstantState() {
        return null;
    }

    public Drawable getCurrent() {
        return this;
    }

    public int getIntrinsicHeight() {
        return -1;
    }

    public int getIntrinsicWidth() {
        return -1;
    }

    public abstract int getOpacity();

    public Region getTransparentRegion() {
        return null;
    }

    public boolean isAutoMirrored() {
        return false;
    }

    public boolean isStateful() {
        return false;
    }

    public void jumpToCurrentState() {
    }

    public Drawable mutate() {
        return this;
    }

    protected void onBoundsChange(Rect rect) {
    }

    protected boolean onLevelChange(int i) {
        return false;
    }

    protected boolean onStateChange(int[] iArr) {
        return false;
    }

    public abstract void setAlpha(int i);

    public void setAutoMirrored(boolean z) {
    }

    public abstract void setColorFilter(ColorFilter colorFilter);

    public void setDither(boolean z) {
    }

    public void setFilterBitmap(boolean z) {
    }

    public void setXfermode(Xfermode xfermode) {
    }

    public void setBounds(int i, int i2, int i3, int i4) {
        Rect rect = this.mBounds;
        if (rect == ZERO_BOUNDS_RECT) {
            rect = new Rect();
            this.mBounds = rect;
        }
        if (rect.left == i && rect.top == i2 && rect.right == i3 && rect.bottom == i4) {
            return;
        }
        if (!rect.isEmpty()) {
            invalidateSelf();
        }
        this.mBounds.set(i, i2, i3, i4);
        onBoundsChange(this.mBounds);
    }

    public void setBounds(Rect rect) {
        setBounds(rect.left, rect.top, rect.right, rect.bottom);
    }

    public final void copyBounds(Rect rect) {
        rect.set(this.mBounds);
    }

    public final Rect copyBounds() {
        return new Rect(this.mBounds);
    }

    public final Rect getBounds() {
        if (this.mBounds == ZERO_BOUNDS_RECT) {
            this.mBounds = new Rect();
        }
        return this.mBounds;
    }

    public void setChangingConfigurations(int i) {
        this.mChangingConfigurations = i;
    }

    public int getChangingConfigurations() {
        return this.mChangingConfigurations;
    }

    public final void setCallback(Callback callback) {
        this.mCallback = new WeakReference<>(callback);
    }

    public Callback getCallback() {
        WeakReference<Callback> weakReference = this.mCallback;
        if (weakReference != null) {
            return weakReference.get();
        }
        return null;
    }

    public void invalidateSelf() {
        Callback callback = getCallback();
        if (callback != null) {
            callback.invalidateDrawable(this);
        }
    }

    public void scheduleSelf(Runnable runnable, long j) {
        Callback callback = getCallback();
        if (callback != null) {
            callback.scheduleDrawable(this, runnable, j);
        }
    }

    public void unscheduleSelf(Runnable runnable) {
        Callback callback = getCallback();
        if (callback != null) {
            callback.unscheduleDrawable(this, runnable);
        }
    }

    public int getLayoutDirection() {
        return this.mLayoutDirection;
    }

    public void setLayoutDirection(int i) {
        if (getLayoutDirection() != i) {
            this.mLayoutDirection = i;
        }
    }

    public void setColorFilter(int i, PorterDuff.Mode mode) {
        setColorFilter(new PorterDuffColorFilter(i, mode));
    }

    public void clearColorFilter() {
        setColorFilter(null);
    }

    public boolean setState(int[] iArr) {
        if (Arrays.equals(this.mStateSet, iArr)) {
            return false;
        }
        this.mStateSet = iArr;
        return onStateChange(iArr);
    }

    public int[] getState() {
        return this.mStateSet;
    }

    public final boolean setLevel(int i) {
        if (this.mLevel == i) {
            return false;
        }
        this.mLevel = i;
        return onLevelChange(i);
    }

    public final int getLevel() {
        return this.mLevel;
    }

    public boolean setVisible(boolean z, boolean z2) {
        boolean z3 = this.mVisible != z;
        if (z3) {
            this.mVisible = z;
            invalidateSelf();
        }
        return z3;
    }

    public final boolean isVisible() {
        return this.mVisible;
    }

    public int getMinimumWidth() {
        int intrinsicWidth = getIntrinsicWidth();
        if (intrinsicWidth > 0) {
            return intrinsicWidth;
        }
        return 0;
    }

    public int getMinimumHeight() {
        int intrinsicHeight = getIntrinsicHeight();
        if (intrinsicHeight > 0) {
            return intrinsicHeight;
        }
        return 0;
    }

    public boolean getPadding(Rect rect) {
        rect.set(0, 0, 0, 0);
        return false;
    }

    public Insets getOpticalInsets() {
        return Insets.NONE;
    }

    public static Drawable createFromStream(InputStream inputStream, String str) {
        Trace.traceBegin(Trace.TRACE_TAG_RESOURCES, str != null ? str : "Unknown drawable");
        try {
            return createFromResourceStream(null, null, inputStream, str, null);
        } finally {
            Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
        }
    }

    public static Drawable createFromResourceStream(Resources resources, TypedValue typedValue, InputStream inputStream, String str) {
        Trace.traceBegin(Trace.TRACE_TAG_RESOURCES, str != null ? str : "Unknown drawable");
        try {
            return createFromResourceStream(resources, typedValue, inputStream, str, null);
        } finally {
            Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
        }
    }

    public static Drawable createFromResourceStream(Resources resources, TypedValue typedValue, InputStream inputStream, String str, BitmapFactory.Options options) {
        byte[] bArr;
        Rect rect;
        if (inputStream == null) {
            return null;
        }
        Rect rect2 = new Rect();
        if (options == null) {
            options = new BitmapFactory.Options();
        }
        options.inScreenDensity = resources != null ? resources.getDisplayMetrics().noncompatDensityDpi : DisplayMetrics.DENSITY_DEVICE;
        Bitmap bitmapDecodeResourceStream = BitmapFactory.decodeResourceStream(resources, typedValue, inputStream, rect2, options);
        if (bitmapDecodeResourceStream == null) {
            return null;
        }
        byte[] ninePatchChunk = bitmapDecodeResourceStream.getNinePatchChunk();
        if (ninePatchChunk == null || !NinePatch.isNinePatchChunk(ninePatchChunk)) {
            bArr = null;
            rect = null;
        } else {
            bArr = ninePatchChunk;
            rect = rect2;
        }
        int[] layoutBounds = bitmapDecodeResourceStream.getLayoutBounds();
        return drawableFromBitmap(resources, bitmapDecodeResourceStream, bArr, rect, layoutBounds != null ? new Rect(layoutBounds[0], layoutBounds[1], layoutBounds[2], layoutBounds[3]) : null, str);
    }

    public static Drawable createFromXml(Resources resources, XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        int next;
        AttributeSet attributeSetAsAttributeSet = Xml.asAttributeSet(xmlPullParser);
        do {
            next = xmlPullParser.next();
            if (next == 2) {
                break;
            }
        } while (next != 1);
        if (next != 2) {
            throw new XmlPullParserException("No start tag found");
        }
        Drawable drawableCreateFromXmlInner = createFromXmlInner(resources, xmlPullParser, attributeSetAsAttributeSet);
        if (drawableCreateFromXmlInner != null) {
            return drawableCreateFromXmlInner;
        }
        throw new RuntimeException("Unknown initial tag: " + xmlPullParser.getName());
    }

    public static Drawable createFromXmlInner(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        Drawable ninePatchDrawable;
        String name = xmlPullParser.getName();
        if (name.equals("selector")) {
            ninePatchDrawable = new StateListDrawable();
        } else if (name.equals("level-list")) {
            ninePatchDrawable = new LevelListDrawable();
        } else if (name.equals("layer-list")) {
            ninePatchDrawable = new LayerDrawable();
        } else if (name.equals("transition")) {
            ninePatchDrawable = new TransitionDrawable();
        } else if (name.equals(CalendarContract.ColorsColumns.COLOR)) {
            ninePatchDrawable = new ColorDrawable();
        } else if (name.equals("shape")) {
            ninePatchDrawable = new GradientDrawable();
        } else if (name.equals(BatteryManager.EXTRA_SCALE)) {
            ninePatchDrawable = new ScaleDrawable();
        } else if (name.equals("clip")) {
            ninePatchDrawable = new ClipDrawable();
        } else if (name.equals("rotate")) {
            ninePatchDrawable = new RotateDrawable();
        } else if (name.equals("animated-rotate")) {
            ninePatchDrawable = new AnimatedRotateDrawable();
        } else if (name.equals("animation-list")) {
            ninePatchDrawable = new AnimationDrawable();
        } else if (name.equals("inset")) {
            ninePatchDrawable = new InsetDrawable();
        } else if (name.equals("bitmap")) {
            ninePatchDrawable = new BitmapDrawable(resources);
            if (resources != null) {
                ((BitmapDrawable) ninePatchDrawable).setTargetDensity(resources.getDisplayMetrics());
            }
        } else if (name.equals("nine-patch")) {
            ninePatchDrawable = new NinePatchDrawable();
            if (resources != null) {
                ((NinePatchDrawable) ninePatchDrawable).setTargetDensity(resources.getDisplayMetrics());
            }
        } else {
            throw new XmlPullParserException(xmlPullParser.getPositionDescription() + ": invalid drawable tag " + name);
        }
        ninePatchDrawable.inflate(resources, xmlPullParser, attributeSet);
        return ninePatchDrawable;
    }

    public static Drawable createFromPath(String str) {
        if (str == null) {
            return null;
        }
        Trace.traceBegin(Trace.TRACE_TAG_RESOURCES, str);
        try {
            Bitmap bitmapDecodeFile = BitmapFactory.decodeFile(str);
            if (bitmapDecodeFile != null) {
                return drawableFromBitmap(null, bitmapDecodeFile, null, null, null, str);
            }
            return null;
        } finally {
            Trace.traceEnd(Trace.TRACE_TAG_RESOURCES);
        }
    }

    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        TypedArray typedArrayObtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.Drawable);
        inflateWithAttributes(resources, xmlPullParser, typedArrayObtainAttributes, 0);
        typedArrayObtainAttributes.recycle();
    }

    void inflateWithAttributes(Resources resources, XmlPullParser xmlPullParser, TypedArray typedArray, int i) throws XmlPullParserException, IOException {
        this.mVisible = typedArray.getBoolean(i, this.mVisible);
    }

    public static abstract class ConstantState {
        public Bitmap getBitmap() {
            return null;
        }

        public abstract int getChangingConfigurations();

        public abstract Drawable newDrawable();

        public Drawable newDrawable(Resources resources) {
            return newDrawable();
        }
    }

    private static Drawable drawableFromBitmap(Resources resources, Bitmap bitmap, byte[] bArr, Rect rect, Rect rect2, String str) {
        if (bArr != null) {
            return new NinePatchDrawable(resources, bitmap, bArr, rect, rect2, str);
        }
        return new BitmapDrawable(resources, bitmap);
    }
}
