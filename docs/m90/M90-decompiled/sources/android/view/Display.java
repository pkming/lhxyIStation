package android.view;

import android.content.res.CompatibilityInfo;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.display.DisplayManagerGlobal;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.util.DisplayMetrics;

/* JADX INFO: loaded from: classes.dex */
public final class Display {
    private static final int CACHED_APP_SIZE_DURATION_MILLIS = 20;
    private static final boolean DEBUG = false;
    public static final int DEFAULT_DISPLAY = 0;
    public static final int FLAG_PRESENTATION = 8;
    public static final int FLAG_PRIVATE = 4;
    public static final int FLAG_SECURE = 2;
    public static final int FLAG_SUPPORTS_PROTECTED_BUFFERS = 1;
    private static final String TAG = "Display";
    public static final int TYPE_BUILT_IN = 1;
    public static final int TYPE_HDMI = 2;
    public static final int TYPE_OVERLAY = 4;
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_VIRTUAL = 5;
    public static final int TYPE_WIFI = 3;
    private final String mAddress;
    private int mCachedAppHeightCompat;
    private int mCachedAppWidthCompat;
    private final DisplayAdjustments mDisplayAdjustments;
    private final int mDisplayId;
    private DisplayInfo mDisplayInfo;
    private final int mFlags;
    private final DisplayManagerGlobal mGlobal;
    private long mLastCachedAppSizeUpdate;
    private final int mLayerStack;
    private final String mOwnerPackageName;
    private final int mOwnerUid;
    private final int mType;
    private final DisplayMetrics mTempMetrics = new DisplayMetrics();
    private boolean mIsValid = true;

    public static boolean hasAccess(int i, int i2, int i3) {
        return (i2 & 4) == 0 || i == i3 || i == 1000 || i == 0;
    }

    @Deprecated
    public int getPixelFormat() {
        return 1;
    }

    public Display(DisplayManagerGlobal displayManagerGlobal, int i, DisplayInfo displayInfo, DisplayAdjustments displayAdjustments) {
        this.mGlobal = displayManagerGlobal;
        this.mDisplayId = i;
        this.mDisplayInfo = displayInfo;
        this.mDisplayAdjustments = new DisplayAdjustments(displayAdjustments);
        this.mLayerStack = displayInfo.layerStack;
        this.mFlags = displayInfo.flags;
        this.mType = displayInfo.type;
        this.mAddress = displayInfo.address;
        this.mOwnerUid = displayInfo.ownerUid;
        this.mOwnerPackageName = displayInfo.ownerPackageName;
    }

    public int getDisplayId() {
        return this.mDisplayId;
    }

    public boolean isValid() {
        boolean z;
        synchronized (this) {
            updateDisplayInfoLocked();
            z = this.mIsValid;
        }
        return z;
    }

    public boolean getDisplayInfo(DisplayInfo displayInfo) {
        boolean z;
        synchronized (this) {
            updateDisplayInfoLocked();
            displayInfo.copyFrom(this.mDisplayInfo);
            z = this.mIsValid;
        }
        return z;
    }

    public int getLayerStack() {
        return this.mLayerStack;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public int getType() {
        return this.mType;
    }

    public String getAddress() {
        return this.mAddress;
    }

    public int getOwnerUid() {
        return this.mOwnerUid;
    }

    public String getOwnerPackageName() {
        return this.mOwnerPackageName;
    }

    public DisplayAdjustments getDisplayAdjustments() {
        return this.mDisplayAdjustments;
    }

    public String getName() {
        String str;
        synchronized (this) {
            updateDisplayInfoLocked();
            str = this.mDisplayInfo.name;
        }
        return str;
    }

    public void getSize(Point point) {
        synchronized (this) {
            updateDisplayInfoLocked();
            this.mDisplayInfo.getAppMetrics(this.mTempMetrics, this.mDisplayAdjustments);
            point.x = this.mTempMetrics.widthPixels;
            point.y = this.mTempMetrics.heightPixels;
        }
    }

    public void getRectSize(Rect rect) {
        synchronized (this) {
            updateDisplayInfoLocked();
            this.mDisplayInfo.getAppMetrics(this.mTempMetrics, this.mDisplayAdjustments);
            rect.set(0, 0, this.mTempMetrics.widthPixels, this.mTempMetrics.heightPixels);
        }
    }

    public void getCurrentSizeRange(Point point, Point point2) {
        synchronized (this) {
            updateDisplayInfoLocked();
            point.x = this.mDisplayInfo.smallestNominalAppWidth;
            point.y = this.mDisplayInfo.smallestNominalAppHeight;
            point2.x = this.mDisplayInfo.largestNominalAppWidth;
            point2.y = this.mDisplayInfo.largestNominalAppHeight;
        }
    }

    public int getMaximumSizeDimension() {
        int iMax;
        synchronized (this) {
            updateDisplayInfoLocked();
            iMax = Math.max(this.mDisplayInfo.logicalWidth, this.mDisplayInfo.logicalHeight);
        }
        return iMax;
    }

    @Deprecated
    public int getWidth() {
        int i;
        synchronized (this) {
            updateCachedAppSizeIfNeededLocked();
            i = this.mCachedAppWidthCompat;
        }
        return i;
    }

    @Deprecated
    public int getHeight() {
        int i;
        synchronized (this) {
            updateCachedAppSizeIfNeededLocked();
            i = this.mCachedAppHeightCompat;
        }
        return i;
    }

    public void getOverscanInsets(Rect rect) {
        synchronized (this) {
            updateDisplayInfoLocked();
            rect.set(this.mDisplayInfo.overscanLeft, this.mDisplayInfo.overscanTop, this.mDisplayInfo.overscanRight, this.mDisplayInfo.overscanBottom);
        }
    }

    public int getRotation() {
        int i;
        synchronized (this) {
            updateDisplayInfoLocked();
            i = this.mDisplayInfo.rotation;
        }
        return i;
    }

    @Deprecated
    public int getOrientation() {
        return getRotation();
    }

    public float getRefreshRate() {
        float f;
        synchronized (this) {
            updateDisplayInfoLocked();
            f = this.mDisplayInfo.refreshRate;
        }
        return f;
    }

    public void getMetrics(DisplayMetrics displayMetrics) {
        synchronized (this) {
            updateDisplayInfoLocked();
            this.mDisplayInfo.getAppMetrics(displayMetrics, this.mDisplayAdjustments);
        }
    }

    public void getRealSize(Point point) {
        synchronized (this) {
            updateDisplayInfoLocked();
            point.x = this.mDisplayInfo.logicalWidth;
            point.y = this.mDisplayInfo.logicalHeight;
        }
    }

    public void getRealMetrics(DisplayMetrics displayMetrics) {
        synchronized (this) {
            updateDisplayInfoLocked();
            this.mDisplayInfo.getLogicalMetrics(displayMetrics, CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO, this.mDisplayAdjustments.getActivityToken());
        }
    }

    public boolean hasAccess(int i) {
        return hasAccess(i, this.mFlags, this.mOwnerUid);
    }

    public boolean isPublicPresentation() {
        return (this.mFlags & 12) == 8;
    }

    private void updateDisplayInfoLocked() {
        DisplayInfo displayInfo = this.mGlobal.getDisplayInfo(this.mDisplayId);
        if (displayInfo == null) {
            if (this.mIsValid) {
                this.mIsValid = false;
            }
        } else {
            this.mDisplayInfo = displayInfo;
            if (this.mIsValid) {
                return;
            }
            this.mIsValid = true;
        }
    }

    private void updateCachedAppSizeIfNeededLocked() {
        long jUptimeMillis = SystemClock.uptimeMillis();
        if (jUptimeMillis > this.mLastCachedAppSizeUpdate + 20) {
            updateDisplayInfoLocked();
            this.mDisplayInfo.getAppMetrics(this.mTempMetrics, this.mDisplayAdjustments);
            this.mCachedAppWidthCompat = this.mTempMetrics.widthPixels;
            this.mCachedAppHeightCompat = this.mTempMetrics.heightPixels;
            this.mLastCachedAppSizeUpdate = jUptimeMillis;
        }
    }

    public String toString() {
        String str;
        synchronized (this) {
            updateDisplayInfoLocked();
            this.mDisplayInfo.getAppMetrics(this.mTempMetrics, this.mDisplayAdjustments);
            str = "Display id " + this.mDisplayId + ": " + this.mDisplayInfo + ", " + this.mTempMetrics + ", isValid=" + this.mIsValid;
        }
        return str;
    }

    public static String typeToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? i != 3 ? i != 4 ? i != 5 ? Integer.toString(i) : "VIRTUAL" : "OVERLAY" : "WIFI" : "HDMI" : "BUILT_IN" : MediaPlayer.CHARSET_UNKNOWN;
    }
}
