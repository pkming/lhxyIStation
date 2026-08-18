package android.appwidget;

import android.content.ComponentName;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class AppWidgetProviderInfo implements Parcelable {
    public static final Parcelable.Creator<AppWidgetProviderInfo> CREATOR = new Parcelable.Creator<AppWidgetProviderInfo>() { // from class: android.appwidget.AppWidgetProviderInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AppWidgetProviderInfo createFromParcel(Parcel parcel) {
            return new AppWidgetProviderInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public AppWidgetProviderInfo[] newArray(int i) {
            return new AppWidgetProviderInfo[i];
        }
    };
    public static final int RESIZE_BOTH = 3;
    public static final int RESIZE_HORIZONTAL = 1;
    public static final int RESIZE_NONE = 0;
    public static final int RESIZE_VERTICAL = 2;
    public static final int WIDGET_CATEGORY_HOME_SCREEN = 1;
    public static final int WIDGET_CATEGORY_KEYGUARD = 2;
    public int autoAdvanceViewId;
    public ComponentName configure;
    public int icon;
    public int initialKeyguardLayout;
    public int initialLayout;
    public String label;
    public int minHeight;
    public int minResizeHeight;
    public int minResizeWidth;
    public int minWidth;
    public int previewImage;
    public ComponentName provider;
    public int resizeMode;
    public int updatePeriodMillis;
    public int widgetCategory;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public AppWidgetProviderInfo() {
    }

    public AppWidgetProviderInfo(Parcel parcel) {
        if (parcel.readInt() != 0) {
            this.provider = new ComponentName(parcel);
        }
        this.minWidth = parcel.readInt();
        this.minHeight = parcel.readInt();
        this.minResizeWidth = parcel.readInt();
        this.minResizeHeight = parcel.readInt();
        this.updatePeriodMillis = parcel.readInt();
        this.initialLayout = parcel.readInt();
        this.initialKeyguardLayout = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.configure = new ComponentName(parcel);
        }
        this.label = parcel.readString();
        this.icon = parcel.readInt();
        this.previewImage = parcel.readInt();
        this.autoAdvanceViewId = parcel.readInt();
        this.resizeMode = parcel.readInt();
        this.widgetCategory = parcel.readInt();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        if (this.provider != null) {
            parcel.writeInt(1);
            this.provider.writeToParcel(parcel, i);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.minWidth);
        parcel.writeInt(this.minHeight);
        parcel.writeInt(this.minResizeWidth);
        parcel.writeInt(this.minResizeHeight);
        parcel.writeInt(this.updatePeriodMillis);
        parcel.writeInt(this.initialLayout);
        parcel.writeInt(this.initialKeyguardLayout);
        if (this.configure != null) {
            parcel.writeInt(1);
            this.configure.writeToParcel(parcel, i);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeString(this.label);
        parcel.writeInt(this.icon);
        parcel.writeInt(this.previewImage);
        parcel.writeInt(this.autoAdvanceViewId);
        parcel.writeInt(this.resizeMode);
        parcel.writeInt(this.widgetCategory);
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public AppWidgetProviderInfo m7clone() {
        AppWidgetProviderInfo appWidgetProviderInfo = new AppWidgetProviderInfo();
        ComponentName componentName = this.provider;
        appWidgetProviderInfo.provider = componentName == null ? null : componentName.m8clone();
        appWidgetProviderInfo.minWidth = this.minWidth;
        appWidgetProviderInfo.minHeight = this.minHeight;
        int i = this.minResizeHeight;
        appWidgetProviderInfo.minResizeWidth = i;
        appWidgetProviderInfo.minResizeHeight = i;
        appWidgetProviderInfo.updatePeriodMillis = this.updatePeriodMillis;
        appWidgetProviderInfo.initialLayout = this.initialLayout;
        appWidgetProviderInfo.initialKeyguardLayout = this.initialKeyguardLayout;
        ComponentName componentName2 = this.configure;
        appWidgetProviderInfo.configure = componentName2 == null ? null : componentName2.m8clone();
        String str = this.label;
        appWidgetProviderInfo.label = str != null ? str.substring(0) : null;
        appWidgetProviderInfo.icon = this.icon;
        appWidgetProviderInfo.previewImage = this.previewImage;
        appWidgetProviderInfo.autoAdvanceViewId = this.autoAdvanceViewId;
        appWidgetProviderInfo.resizeMode = this.resizeMode;
        appWidgetProviderInfo.widgetCategory = this.widgetCategory;
        return appWidgetProviderInfo;
    }

    public String toString() {
        return "AppWidgetProviderInfo(provider=" + this.provider + ")";
    }
}
