package android.app.admin;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Printer;
import android.util.SparseArray;
import android.util.Xml;
import com.android.internal.R;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* JADX INFO: loaded from: classes.dex */
public final class DeviceAdminInfo implements Parcelable {
    public static final Parcelable.Creator<DeviceAdminInfo> CREATOR;
    static final String TAG = "DeviceAdminInfo";
    public static final int USES_ENCRYPTED_STORAGE = 7;
    public static final int USES_POLICY_DISABLE_CAMERA = 8;
    public static final int USES_POLICY_DISABLE_KEYGUARD_FEATURES = 9;
    public static final int USES_POLICY_EXPIRE_PASSWORD = 6;
    public static final int USES_POLICY_FORCE_LOCK = 3;
    public static final int USES_POLICY_LIMIT_PASSWORD = 0;
    public static final int USES_POLICY_RESET_PASSWORD = 2;
    public static final int USES_POLICY_SETS_GLOBAL_PROXY = 5;
    public static final int USES_POLICY_WATCH_LOGIN = 1;
    public static final int USES_POLICY_WIPE_DATA = 4;
    final ResolveInfo mReceiver;
    int mUsesPolicies;
    boolean mVisible;
    static ArrayList<PolicyInfo> sPoliciesDisplayOrder = new ArrayList<>();
    static HashMap<String, Integer> sKnownPolicies = new HashMap<>();
    static SparseArray<PolicyInfo> sRevKnownPolicies = new SparseArray<>();

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static class PolicyInfo {
        public final int description;
        public final int ident;
        public final int label;
        public final String tag;

        public PolicyInfo(int i, String str, int i2, int i3) {
            this.ident = i;
            this.tag = str;
            this.label = i2;
            this.description = i3;
        }
    }

    static {
        sPoliciesDisplayOrder.add(new PolicyInfo(4, "wipe-data", 17040046, 17040047));
        sPoliciesDisplayOrder.add(new PolicyInfo(2, "reset-password", 17040042, 17040043));
        sPoliciesDisplayOrder.add(new PolicyInfo(0, "limit-password", 17040038, 17040039));
        sPoliciesDisplayOrder.add(new PolicyInfo(1, "watch-login", 17040040, 17040041));
        sPoliciesDisplayOrder.add(new PolicyInfo(3, "force-lock", 17040044, 17040045));
        sPoliciesDisplayOrder.add(new PolicyInfo(5, "set-global-proxy", 17040048, 17040049));
        sPoliciesDisplayOrder.add(new PolicyInfo(6, "expire-password", 17040050, 17040051));
        sPoliciesDisplayOrder.add(new PolicyInfo(7, "encrypted-storage", 17040052, 17040053));
        sPoliciesDisplayOrder.add(new PolicyInfo(8, "disable-camera", 17040054, 17040055));
        sPoliciesDisplayOrder.add(new PolicyInfo(9, "disable-keyguard-features", 17040056, 17040057));
        for (int i = 0; i < sPoliciesDisplayOrder.size(); i++) {
            PolicyInfo policyInfo = sPoliciesDisplayOrder.get(i);
            sRevKnownPolicies.put(policyInfo.ident, policyInfo);
            sKnownPolicies.put(policyInfo.tag, Integer.valueOf(policyInfo.ident));
        }
        CREATOR = new Parcelable.Creator<DeviceAdminInfo>() { // from class: android.app.admin.DeviceAdminInfo.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public DeviceAdminInfo createFromParcel(Parcel parcel) {
                return new DeviceAdminInfo(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public DeviceAdminInfo[] newArray(int i2) {
                return new DeviceAdminInfo[i2];
            }
        };
    }

    public DeviceAdminInfo(Context context, ResolveInfo resolveInfo) throws XmlPullParserException, IOException {
        int next;
        this.mReceiver = resolveInfo;
        ActivityInfo activityInfo = resolveInfo.activityInfo;
        PackageManager packageManager = context.getPackageManager();
        XmlResourceParser xmlResourceParser = null;
        try {
            try {
                XmlResourceParser xmlResourceParserLoadXmlMetaData = activityInfo.loadXmlMetaData(packageManager, DeviceAdminReceiver.DEVICE_ADMIN_META_DATA);
                if (xmlResourceParserLoadXmlMetaData == null) {
                    throw new XmlPullParserException("No android.app.device_admin meta-data");
                }
                Resources resourcesForApplication = packageManager.getResourcesForApplication(activityInfo.applicationInfo);
                AttributeSet attributeSetAsAttributeSet = Xml.asAttributeSet(xmlResourceParserLoadXmlMetaData);
                do {
                    next = xmlResourceParserLoadXmlMetaData.next();
                    if (next == 1) {
                        break;
                    }
                } while (next != 2);
                if (!"device-admin".equals(xmlResourceParserLoadXmlMetaData.getName())) {
                    throw new XmlPullParserException("Meta-data does not start with device-admin tag");
                }
                TypedArray typedArrayObtainAttributes = resourcesForApplication.obtainAttributes(attributeSetAsAttributeSet, R.styleable.DeviceAdmin);
                this.mVisible = typedArrayObtainAttributes.getBoolean(0, true);
                typedArrayObtainAttributes.recycle();
                int depth = xmlResourceParserLoadXmlMetaData.getDepth();
                while (true) {
                    int next2 = xmlResourceParserLoadXmlMetaData.next();
                    if (next2 == 1 || (next2 == 3 && xmlResourceParserLoadXmlMetaData.getDepth() <= depth)) {
                        break;
                    }
                    if (next2 != 3 && next2 != 4 && xmlResourceParserLoadXmlMetaData.getName().equals("uses-policies")) {
                        int depth2 = xmlResourceParserLoadXmlMetaData.getDepth();
                        while (true) {
                            int next3 = xmlResourceParserLoadXmlMetaData.next();
                            if (next3 == 1 || (next3 == 3 && xmlResourceParserLoadXmlMetaData.getDepth() <= depth2)) {
                                break;
                            }
                            if (next3 != 3 && next3 != 4) {
                                String name = xmlResourceParserLoadXmlMetaData.getName();
                                Integer num = sKnownPolicies.get(name);
                                if (num != null) {
                                    this.mUsesPolicies |= 1 << num.intValue();
                                } else {
                                    Log.w(TAG, "Unknown tag under uses-policies of " + getComponent() + ": " + name);
                                }
                            }
                        }
                    }
                }
                if (xmlResourceParserLoadXmlMetaData != null) {
                    xmlResourceParserLoadXmlMetaData.close();
                }
            } catch (PackageManager.NameNotFoundException unused) {
                throw new XmlPullParserException("Unable to create context for: " + activityInfo.packageName);
            }
        } catch (Throwable th) {
            if (0 != 0) {
                xmlResourceParser.close();
            }
            throw th;
        }
    }

    DeviceAdminInfo(Parcel parcel) {
        this.mReceiver = ResolveInfo.CREATOR.createFromParcel(parcel);
        this.mUsesPolicies = parcel.readInt();
    }

    public String getPackageName() {
        return this.mReceiver.activityInfo.packageName;
    }

    public String getReceiverName() {
        return this.mReceiver.activityInfo.name;
    }

    public ActivityInfo getActivityInfo() {
        return this.mReceiver.activityInfo;
    }

    public ComponentName getComponent() {
        return new ComponentName(this.mReceiver.activityInfo.packageName, this.mReceiver.activityInfo.name);
    }

    public CharSequence loadLabel(PackageManager packageManager) {
        return this.mReceiver.loadLabel(packageManager);
    }

    public CharSequence loadDescription(PackageManager packageManager) throws Resources.NotFoundException {
        if (this.mReceiver.activityInfo.descriptionRes != 0) {
            String str = this.mReceiver.resolvePackageName;
            ApplicationInfo applicationInfo = null;
            if (str == null) {
                str = this.mReceiver.activityInfo.packageName;
                applicationInfo = this.mReceiver.activityInfo.applicationInfo;
            }
            return packageManager.getText(str, this.mReceiver.activityInfo.descriptionRes, applicationInfo);
        }
        throw new Resources.NotFoundException();
    }

    public Drawable loadIcon(PackageManager packageManager) {
        return this.mReceiver.loadIcon(packageManager);
    }

    public boolean isVisible() {
        return this.mVisible;
    }

    public boolean usesPolicy(int i) {
        return ((1 << i) & this.mUsesPolicies) != 0;
    }

    public String getTagForPolicy(int i) {
        return sRevKnownPolicies.get(i).tag;
    }

    public ArrayList<PolicyInfo> getUsedPolicies() {
        ArrayList<PolicyInfo> arrayList = new ArrayList<>();
        for (int i = 0; i < sPoliciesDisplayOrder.size(); i++) {
            PolicyInfo policyInfo = sPoliciesDisplayOrder.get(i);
            if (usesPolicy(policyInfo.ident)) {
                arrayList.add(policyInfo);
            }
        }
        return arrayList;
    }

    public void writePoliciesToXml(XmlSerializer xmlSerializer) throws IllegalStateException, IOException, IllegalArgumentException {
        xmlSerializer.attribute(null, "flags", Integer.toString(this.mUsesPolicies));
    }

    public void readPoliciesFromXml(XmlPullParser xmlPullParser) throws XmlPullParserException, IOException {
        this.mUsesPolicies = Integer.parseInt(xmlPullParser.getAttributeValue(null, "flags"));
    }

    public void dump(Printer printer, String str) {
        printer.println(str + "Receiver:");
        this.mReceiver.dump(printer, str + "  ");
    }

    public String toString() {
        return "DeviceAdminInfo{" + this.mReceiver.activityInfo.name + "}";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        this.mReceiver.writeToParcel(parcel, i);
        parcel.writeInt(this.mUsesPolicies);
    }
}
