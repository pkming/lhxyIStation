package android.app;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.service.wallpaper.WallpaperService;
import android.util.AttributeSet;
import android.util.Printer;
import android.util.Xml;
import com.android.internal.R;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public final class WallpaperInfo implements Parcelable {
    public static final Parcelable.Creator<WallpaperInfo> CREATOR = new Parcelable.Creator<WallpaperInfo>() { // from class: android.app.WallpaperInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WallpaperInfo createFromParcel(Parcel parcel) {
            return new WallpaperInfo(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public WallpaperInfo[] newArray(int i) {
            return new WallpaperInfo[i];
        }
    };
    static final String TAG = "WallpaperInfo";
    final int mAuthorResource;
    final int mDescriptionResource;
    final ResolveInfo mService;
    final String mSettingsActivityName;
    final int mThumbnailResource;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public WallpaperInfo(Context context, ResolveInfo resolveInfo) throws XmlPullParserException, IOException {
        int next;
        this.mService = resolveInfo;
        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
        PackageManager packageManager = context.getPackageManager();
        XmlResourceParser xmlResourceParser = null;
        try {
            try {
                XmlResourceParser xmlResourceParserLoadXmlMetaData = serviceInfo.loadXmlMetaData(packageManager, WallpaperService.SERVICE_META_DATA);
                if (xmlResourceParserLoadXmlMetaData == null) {
                    throw new XmlPullParserException("No android.service.wallpaper meta-data");
                }
                Resources resourcesForApplication = packageManager.getResourcesForApplication(serviceInfo.applicationInfo);
                AttributeSet attributeSetAsAttributeSet = Xml.asAttributeSet(xmlResourceParserLoadXmlMetaData);
                do {
                    next = xmlResourceParserLoadXmlMetaData.next();
                    if (next == 1) {
                        break;
                    }
                } while (next != 2);
                if (!Context.WALLPAPER_SERVICE.equals(xmlResourceParserLoadXmlMetaData.getName())) {
                    throw new XmlPullParserException("Meta-data does not start with wallpaper tag");
                }
                TypedArray typedArrayObtainAttributes = resourcesForApplication.obtainAttributes(attributeSetAsAttributeSet, R.styleable.Wallpaper);
                String string = typedArrayObtainAttributes.getString(1);
                int resourceId = typedArrayObtainAttributes.getResourceId(2, -1);
                int resourceId2 = typedArrayObtainAttributes.getResourceId(3, -1);
                int resourceId3 = typedArrayObtainAttributes.getResourceId(0, -1);
                typedArrayObtainAttributes.recycle();
                if (xmlResourceParserLoadXmlMetaData != null) {
                    xmlResourceParserLoadXmlMetaData.close();
                }
                this.mSettingsActivityName = string;
                this.mThumbnailResource = resourceId;
                this.mAuthorResource = resourceId2;
                this.mDescriptionResource = resourceId3;
            } catch (PackageManager.NameNotFoundException unused) {
                throw new XmlPullParserException("Unable to create context for: " + serviceInfo.packageName);
            }
        } catch (Throwable th) {
            if (0 != 0) {
                xmlResourceParser.close();
            }
            throw th;
        }
    }

    WallpaperInfo(Parcel parcel) {
        this.mSettingsActivityName = parcel.readString();
        this.mThumbnailResource = parcel.readInt();
        this.mAuthorResource = parcel.readInt();
        this.mDescriptionResource = parcel.readInt();
        this.mService = ResolveInfo.CREATOR.createFromParcel(parcel);
    }

    public String getPackageName() {
        return this.mService.serviceInfo.packageName;
    }

    public String getServiceName() {
        return this.mService.serviceInfo.name;
    }

    public ServiceInfo getServiceInfo() {
        return this.mService.serviceInfo;
    }

    public ComponentName getComponent() {
        return new ComponentName(this.mService.serviceInfo.packageName, this.mService.serviceInfo.name);
    }

    public CharSequence loadLabel(PackageManager packageManager) {
        return this.mService.loadLabel(packageManager);
    }

    public Drawable loadIcon(PackageManager packageManager) {
        return this.mService.loadIcon(packageManager);
    }

    public Drawable loadThumbnail(PackageManager packageManager) {
        if (this.mThumbnailResource < 0) {
            return null;
        }
        return packageManager.getDrawable(this.mService.serviceInfo.packageName, this.mThumbnailResource, this.mService.serviceInfo.applicationInfo);
    }

    public CharSequence loadAuthor(PackageManager packageManager) throws Resources.NotFoundException {
        if (this.mAuthorResource <= 0) {
            throw new Resources.NotFoundException();
        }
        String str = this.mService.resolvePackageName;
        ApplicationInfo applicationInfo = null;
        if (str == null) {
            str = this.mService.serviceInfo.packageName;
            applicationInfo = this.mService.serviceInfo.applicationInfo;
        }
        return packageManager.getText(str, this.mAuthorResource, applicationInfo);
    }

    public CharSequence loadDescription(PackageManager packageManager) throws Resources.NotFoundException {
        ApplicationInfo applicationInfo;
        String str = this.mService.resolvePackageName;
        if (str == null) {
            str = this.mService.serviceInfo.packageName;
            applicationInfo = this.mService.serviceInfo.applicationInfo;
        } else {
            applicationInfo = null;
        }
        if (this.mService.serviceInfo.descriptionRes != 0) {
            return packageManager.getText(str, this.mService.serviceInfo.descriptionRes, applicationInfo);
        }
        int i = this.mDescriptionResource;
        if (i <= 0) {
            throw new Resources.NotFoundException();
        }
        return packageManager.getText(str, i, this.mService.serviceInfo.applicationInfo);
    }

    public String getSettingsActivity() {
        return this.mSettingsActivityName;
    }

    public void dump(Printer printer, String str) {
        printer.println(str + "Service:");
        this.mService.dump(printer, str + "  ");
        printer.println(str + "mSettingsActivityName=" + this.mSettingsActivityName);
    }

    public String toString() {
        return "WallpaperInfo{" + this.mService.serviceInfo.name + ", settings: " + this.mSettingsActivityName + "}";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mSettingsActivityName);
        parcel.writeInt(this.mThumbnailResource);
        parcel.writeInt(this.mAuthorResource);
        parcel.writeInt(this.mDescriptionResource);
        this.mService.writeToParcel(parcel, i);
    }
}
