package android.accessibilityservice;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.ServiceInfo;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.media.MediaPlayer;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;
import android.util.TypedValue;
import android.util.Xml;
import android.view.accessibility.AccessibilityEvent;
import com.android.internal.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class AccessibilityServiceInfo implements Parcelable {
    public static final int CAPABILITY_CAN_REQUEST_ENHANCED_WEB_ACCESSIBILITY = 4;
    public static final int CAPABILITY_CAN_REQUEST_FILTER_KEY_EVENTS = 8;
    public static final int CAPABILITY_CAN_REQUEST_TOUCH_EXPLORATION = 2;
    public static final int CAPABILITY_CAN_RETRIEVE_WINDOW_CONTENT = 1;
    public static final Parcelable.Creator<AccessibilityServiceInfo> CREATOR;
    public static final int DEFAULT = 1;
    public static final int FEEDBACK_ALL_MASK = -1;
    public static final int FEEDBACK_AUDIBLE = 4;
    public static final int FEEDBACK_BRAILLE = 32;
    public static final int FEEDBACK_GENERIC = 16;
    public static final int FEEDBACK_HAPTIC = 2;
    public static final int FEEDBACK_SPOKEN = 1;
    public static final int FEEDBACK_VISUAL = 8;
    public static final int FLAG_INCLUDE_NOT_IMPORTANT_VIEWS = 2;
    public static final int FLAG_REPORT_VIEW_IDS = 16;
    public static final int FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY = 8;
    public static final int FLAG_REQUEST_FILTER_KEY_EVENTS = 32;
    public static final int FLAG_REQUEST_TOUCH_EXPLORATION_MODE = 4;
    private static final String TAG_ACCESSIBILITY_SERVICE = "accessibility-service";
    private static final SparseArray<CapabilityInfo> sAvailableCapabilityInfos;
    public int eventTypes;
    public int feedbackType;
    public int flags;
    private int mCapabilities;
    private int mDescriptionResId;
    private String mId;
    private String mNonLocalizedDescription;
    private ResolveInfo mResolveInfo;
    private String mSettingsActivityName;
    public long notificationTimeout;
    public String[] packageNames;

    public static String capabilityToString(int i) {
        return i != 1 ? i != 2 ? i != 4 ? i != 8 ? MediaPlayer.CHARSET_UNKNOWN : "CAPABILITY_CAN_FILTER_KEY_EVENTS" : "CAPABILITY_CAN_REQUEST_ENHANCED_WEB_ACCESSIBILITY" : "CAPABILITY_CAN_REQUEST_TOUCH_EXPLORATION" : "CAPABILITY_CAN_RETRIEVE_WINDOW_CONTENT";
    }

    public static String flagToString(int i) {
        if (i == 1) {
            return "DEFAULT";
        }
        if (i == 2) {
            return "FLAG_INCLUDE_NOT_IMPORTANT_VIEWS";
        }
        if (i == 4) {
            return "FLAG_REQUEST_TOUCH_EXPLORATION_MODE";
        }
        if (i == 8) {
            return "FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY";
        }
        if (i == 16) {
            return "FLAG_REPORT_VIEW_IDS";
        }
        if (i != 32) {
            return null;
        }
        return "FLAG_REQUEST_FILTER_KEY_EVENTS";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    static {
        SparseArray<CapabilityInfo> sparseArray = new SparseArray<>();
        sAvailableCapabilityInfos = sparseArray;
        sparseArray.put(1, new CapabilityInfo(1, 17039634, 17039635));
        sparseArray.put(2, new CapabilityInfo(2, 17039636, 17039637));
        sparseArray.put(4, new CapabilityInfo(4, 17039638, 17039639));
        sparseArray.put(8, new CapabilityInfo(8, 17039640, 17039641));
        CREATOR = new Parcelable.Creator<AccessibilityServiceInfo>() { // from class: android.accessibilityservice.AccessibilityServiceInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public AccessibilityServiceInfo createFromParcel(Parcel parcel) {
                AccessibilityServiceInfo accessibilityServiceInfo = new AccessibilityServiceInfo();
                accessibilityServiceInfo.initFromParcel(parcel);
                return accessibilityServiceInfo;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public AccessibilityServiceInfo[] newArray(int i) {
                return new AccessibilityServiceInfo[i];
            }
        };
    }

    public AccessibilityServiceInfo() {
    }

    public AccessibilityServiceInfo(ResolveInfo resolveInfo, Context context) throws XmlPullParserException, IOException {
        ServiceInfo serviceInfo = resolveInfo.serviceInfo;
        this.mId = new ComponentName(serviceInfo.packageName, serviceInfo.name).flattenToShortString();
        this.mResolveInfo = resolveInfo;
        XmlResourceParser xmlResourceParser = null;
        try {
            try {
                PackageManager packageManager = context.getPackageManager();
                XmlResourceParser xmlResourceParserLoadXmlMetaData = serviceInfo.loadXmlMetaData(packageManager, AccessibilityService.SERVICE_META_DATA);
                if (xmlResourceParserLoadXmlMetaData == null) {
                    if (xmlResourceParserLoadXmlMetaData != null) {
                        xmlResourceParserLoadXmlMetaData.close();
                        return;
                    }
                    return;
                }
                for (int next = 0; next != 1 && next != 2; next = xmlResourceParserLoadXmlMetaData.next()) {
                }
                if (!TAG_ACCESSIBILITY_SERVICE.equals(xmlResourceParserLoadXmlMetaData.getName())) {
                    throw new XmlPullParserException("Meta-data does not start withaccessibility-service tag");
                }
                TypedArray typedArrayObtainAttributes = packageManager.getResourcesForApplication(serviceInfo.applicationInfo).obtainAttributes(Xml.asAttributeSet(xmlResourceParserLoadXmlMetaData), R.styleable.AccessibilityService);
                this.eventTypes = typedArrayObtainAttributes.getInt(2, 0);
                String string = typedArrayObtainAttributes.getString(3);
                if (string != null) {
                    this.packageNames = string.split("(\\s)*,(\\s)*");
                }
                this.feedbackType = typedArrayObtainAttributes.getInt(4, 0);
                this.notificationTimeout = typedArrayObtainAttributes.getInt(5, 0);
                this.flags = typedArrayObtainAttributes.getInt(6, 0);
                this.mSettingsActivityName = typedArrayObtainAttributes.getString(1);
                if (typedArrayObtainAttributes.getBoolean(7, false)) {
                    this.mCapabilities = 1 | this.mCapabilities;
                }
                if (typedArrayObtainAttributes.getBoolean(8, false)) {
                    this.mCapabilities = 2 | this.mCapabilities;
                }
                if (typedArrayObtainAttributes.getBoolean(9, false)) {
                    this.mCapabilities = 4 | this.mCapabilities;
                }
                if (typedArrayObtainAttributes.getBoolean(10, false)) {
                    this.mCapabilities |= 8;
                }
                TypedValue typedValuePeekValue = typedArrayObtainAttributes.peekValue(0);
                if (typedValuePeekValue != null) {
                    this.mDescriptionResId = typedValuePeekValue.resourceId;
                    CharSequence charSequenceCoerceToString = typedValuePeekValue.coerceToString();
                    if (charSequenceCoerceToString != null) {
                        this.mNonLocalizedDescription = charSequenceCoerceToString.toString().trim();
                    }
                }
                typedArrayObtainAttributes.recycle();
                if (xmlResourceParserLoadXmlMetaData != null) {
                    xmlResourceParserLoadXmlMetaData.close();
                }
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

    public void updateDynamicallyConfigurableProperties(AccessibilityServiceInfo accessibilityServiceInfo) {
        this.eventTypes = accessibilityServiceInfo.eventTypes;
        this.packageNames = accessibilityServiceInfo.packageNames;
        this.feedbackType = accessibilityServiceInfo.feedbackType;
        this.notificationTimeout = accessibilityServiceInfo.notificationTimeout;
        this.flags = accessibilityServiceInfo.flags;
    }

    public void setComponentName(ComponentName componentName) {
        this.mId = componentName.flattenToShortString();
    }

    public String getId() {
        return this.mId;
    }

    public ResolveInfo getResolveInfo() {
        return this.mResolveInfo;
    }

    public String getSettingsActivityName() {
        return this.mSettingsActivityName;
    }

    public boolean getCanRetrieveWindowContent() {
        return (this.mCapabilities & 1) != 0;
    }

    public int getCapabilities() {
        return this.mCapabilities;
    }

    public void setCapabilities(int i) {
        this.mCapabilities = i;
    }

    public String getDescription() {
        return this.mNonLocalizedDescription;
    }

    public String loadDescription(PackageManager packageManager) {
        if (this.mDescriptionResId == 0) {
            return this.mNonLocalizedDescription;
        }
        ServiceInfo serviceInfo = this.mResolveInfo.serviceInfo;
        CharSequence text = packageManager.getText(serviceInfo.packageName, this.mDescriptionResId, serviceInfo.applicationInfo);
        if (text != null) {
            return text.toString().trim();
        }
        return null;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.eventTypes);
        parcel.writeStringArray(this.packageNames);
        parcel.writeInt(this.feedbackType);
        parcel.writeLong(this.notificationTimeout);
        parcel.writeInt(this.flags);
        parcel.writeString(this.mId);
        parcel.writeParcelable(this.mResolveInfo, 0);
        parcel.writeString(this.mSettingsActivityName);
        parcel.writeInt(this.mCapabilities);
        parcel.writeInt(this.mDescriptionResId);
        parcel.writeString(this.mNonLocalizedDescription);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initFromParcel(Parcel parcel) {
        this.eventTypes = parcel.readInt();
        this.packageNames = parcel.readStringArray();
        this.feedbackType = parcel.readInt();
        this.notificationTimeout = parcel.readLong();
        this.flags = parcel.readInt();
        this.mId = parcel.readString();
        this.mResolveInfo = (ResolveInfo) parcel.readParcelable(null);
        this.mSettingsActivityName = parcel.readString();
        this.mCapabilities = parcel.readInt();
        this.mDescriptionResId = parcel.readInt();
        this.mNonLocalizedDescription = parcel.readString();
    }

    public int hashCode() {
        String str = this.mId;
        return (str == null ? 0 : str.hashCode()) + 31;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AccessibilityServiceInfo accessibilityServiceInfo = (AccessibilityServiceInfo) obj;
        String str = this.mId;
        if (str == null) {
            if (accessibilityServiceInfo.mId != null) {
                return false;
            }
        } else if (!str.equals(accessibilityServiceInfo.mId)) {
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendEventTypes(sb, this.eventTypes);
        sb.append(", ");
        appendPackageNames(sb, this.packageNames);
        sb.append(", ");
        appendFeedbackTypes(sb, this.feedbackType);
        sb.append(", ");
        sb.append("notificationTimeout: ").append(this.notificationTimeout);
        sb.append(", ");
        appendFlags(sb, this.flags);
        sb.append(", ");
        sb.append("id: ").append(this.mId);
        sb.append(", ");
        sb.append("resolveInfo: ").append(this.mResolveInfo);
        sb.append(", ");
        sb.append("settingsActivityName: ").append(this.mSettingsActivityName);
        sb.append(", ");
        appendCapabilities(sb, this.mCapabilities);
        return sb.toString();
    }

    private static void appendFeedbackTypes(StringBuilder sb, int i) {
        sb.append("feedbackTypes:");
        sb.append("[");
        while (i != 0) {
            int iNumberOfTrailingZeros = 1 << Integer.numberOfTrailingZeros(i);
            sb.append(feedbackTypeToString(iNumberOfTrailingZeros));
            i &= ~iNumberOfTrailingZeros;
            if (i != 0) {
                sb.append(", ");
            }
        }
        sb.append("]");
    }

    private static void appendPackageNames(StringBuilder sb, String[] strArr) {
        sb.append("packageNames:");
        sb.append("[");
        if (strArr != null) {
            int length = strArr.length;
            for (int i = 0; i < length; i++) {
                sb.append(strArr[i]);
                if (i < length - 1) {
                    sb.append(", ");
                }
            }
        }
        sb.append("]");
    }

    private static void appendEventTypes(StringBuilder sb, int i) {
        sb.append("eventTypes:");
        sb.append("[");
        while (i != 0) {
            int iNumberOfTrailingZeros = 1 << Integer.numberOfTrailingZeros(i);
            sb.append(AccessibilityEvent.eventTypeToString(iNumberOfTrailingZeros));
            i &= ~iNumberOfTrailingZeros;
            if (i != 0) {
                sb.append(", ");
            }
        }
        sb.append("]");
    }

    private static void appendFlags(StringBuilder sb, int i) {
        sb.append("flags:");
        sb.append("[");
        while (i != 0) {
            int iNumberOfTrailingZeros = 1 << Integer.numberOfTrailingZeros(i);
            sb.append(flagToString(iNumberOfTrailingZeros));
            i &= ~iNumberOfTrailingZeros;
            if (i != 0) {
                sb.append(", ");
            }
        }
        sb.append("]");
    }

    private static void appendCapabilities(StringBuilder sb, int i) {
        sb.append("capabilities:");
        sb.append("[");
        while (i != 0) {
            int iNumberOfTrailingZeros = 1 << Integer.numberOfTrailingZeros(i);
            sb.append(capabilityToString(iNumberOfTrailingZeros));
            i &= ~iNumberOfTrailingZeros;
            if (i != 0) {
                sb.append(", ");
            }
        }
        sb.append("]");
    }

    public static String feedbackTypeToString(int i) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        while (i != 0) {
            int iNumberOfTrailingZeros = 1 << Integer.numberOfTrailingZeros(i);
            i &= ~iNumberOfTrailingZeros;
            if (iNumberOfTrailingZeros == 1) {
                if (sb.length() > 1) {
                    sb.append(", ");
                }
                sb.append("FEEDBACK_SPOKEN");
            } else if (iNumberOfTrailingZeros == 2) {
                if (sb.length() > 1) {
                    sb.append(", ");
                }
                sb.append("FEEDBACK_HAPTIC");
            } else if (iNumberOfTrailingZeros == 4) {
                if (sb.length() > 1) {
                    sb.append(", ");
                }
                sb.append("FEEDBACK_AUDIBLE");
            } else if (iNumberOfTrailingZeros == 8) {
                if (sb.length() > 1) {
                    sb.append(", ");
                }
                sb.append("FEEDBACK_VISUAL");
            } else if (iNumberOfTrailingZeros == 16) {
                if (sb.length() > 1) {
                    sb.append(", ");
                }
                sb.append("FEEDBACK_GENERIC");
            } else if (iNumberOfTrailingZeros == 32) {
                if (sb.length() > 1) {
                    sb.append(", ");
                }
                sb.append("FEEDBACK_BRAILLE");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public List<CapabilityInfo> getCapabilityInfos() {
        int i = this.mCapabilities;
        if (i == 0) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList();
        while (i != 0) {
            int iNumberOfTrailingZeros = 1 << Integer.numberOfTrailingZeros(i);
            i &= ~iNumberOfTrailingZeros;
            CapabilityInfo capabilityInfo = sAvailableCapabilityInfos.get(iNumberOfTrailingZeros);
            if (capabilityInfo != null) {
                arrayList.add(capabilityInfo);
            }
        }
        return arrayList;
    }

    public static final class CapabilityInfo {
        public final int capability;
        public final int descResId;
        public final int titleResId;

        public CapabilityInfo(int i, int i2, int i3) {
            this.capability = i;
            this.titleResId = i2;
            this.descResId = i3;
        }
    }
}
