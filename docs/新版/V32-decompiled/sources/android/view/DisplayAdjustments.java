package android.view;

import android.content.res.CompatibilityInfo;
import android.os.IBinder;
import com.android.internal.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class DisplayAdjustments {
    public static final DisplayAdjustments DEFAULT_DISPLAY_ADJUSTMENTS = new DisplayAdjustments();
    public static final boolean DEVELOPMENT_RESOURCES_DEPEND_ON_ACTIVITY_TOKEN = false;
    private volatile IBinder mActivityToken;
    private volatile CompatibilityInfo mCompatInfo;

    public DisplayAdjustments() {
        this.mCompatInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
    }

    public DisplayAdjustments(IBinder iBinder) {
        this.mCompatInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        this.mActivityToken = iBinder;
    }

    public DisplayAdjustments(DisplayAdjustments displayAdjustments) {
        this(displayAdjustments.getCompatibilityInfo(), displayAdjustments.getActivityToken());
    }

    public DisplayAdjustments(CompatibilityInfo compatibilityInfo, IBinder iBinder) {
        this.mCompatInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        setCompatibilityInfo(compatibilityInfo);
        this.mActivityToken = iBinder;
    }

    public void setCompatibilityInfo(CompatibilityInfo compatibilityInfo) {
        if (this == DEFAULT_DISPLAY_ADJUSTMENTS) {
            throw new IllegalArgumentException("setCompatbilityInfo: Cannot modify DEFAULT_DISPLAY_ADJUSTMENTS");
        }
        if (compatibilityInfo != null && (compatibilityInfo.isScalingRequired() || !compatibilityInfo.supportsScreen())) {
            this.mCompatInfo = compatibilityInfo;
        } else {
            this.mCompatInfo = CompatibilityInfo.DEFAULT_COMPATIBILITY_INFO;
        }
    }

    public CompatibilityInfo getCompatibilityInfo() {
        return this.mCompatInfo;
    }

    public void setActivityToken(IBinder iBinder) {
        if (this == DEFAULT_DISPLAY_ADJUSTMENTS) {
            throw new IllegalArgumentException("setActivityToken: Cannot modify DEFAULT_DISPLAY_ADJUSTMENTS");
        }
        this.mActivityToken = iBinder;
    }

    public IBinder getActivityToken() {
        return this.mActivityToken;
    }

    public int hashCode() {
        return 527 + this.mCompatInfo.hashCode();
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof DisplayAdjustments)) {
            return false;
        }
        DisplayAdjustments displayAdjustments = (DisplayAdjustments) obj;
        return Objects.equal(displayAdjustments.mCompatInfo, this.mCompatInfo) && Objects.equal(displayAdjustments.mActivityToken, this.mActivityToken);
    }
}
