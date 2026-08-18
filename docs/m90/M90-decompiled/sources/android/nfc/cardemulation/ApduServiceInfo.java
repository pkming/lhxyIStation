package android.nfc.cardemulation;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public final class ApduServiceInfo implements Parcelable {
    public static final Parcelable.Creator<ApduServiceInfo> CREATOR = new Parcelable.Creator<ApduServiceInfo>() { // from class: android.nfc.cardemulation.ApduServiceInfo.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ApduServiceInfo createFromParcel(Parcel parcel) {
            ResolveInfo resolveInfoCreateFromParcel = ResolveInfo.CREATOR.createFromParcel(parcel);
            String string = parcel.readString();
            boolean z = parcel.readInt() != 0;
            ArrayList arrayList = new ArrayList();
            if (parcel.readInt() > 0) {
                parcel.readTypedList(arrayList, AidGroup.CREATOR);
            }
            return new ApduServiceInfo(resolveInfoCreateFromParcel, z, string, arrayList, parcel.readInt() != 0, parcel.readInt());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ApduServiceInfo[] newArray(int i) {
            return new ApduServiceInfo[i];
        }
    };
    static final String TAG = "ApduServiceInfo";
    final ArrayList<AidGroup> mAidGroups;
    final ArrayList<String> mAids;
    final int mBannerResourceId;
    final HashMap<String, AidGroup> mCategoryToGroup;
    final String mDescription;
    final boolean mOnHost;
    final boolean mRequiresDeviceUnlock;
    final ResolveInfo mService;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ApduServiceInfo(ResolveInfo resolveInfo, boolean z, String str, ArrayList<AidGroup> arrayList, boolean z2, int i) {
        this.mService = resolveInfo;
        this.mDescription = str;
        this.mAidGroups = arrayList;
        this.mAids = new ArrayList<>();
        this.mCategoryToGroup = new HashMap<>();
        this.mOnHost = z;
        this.mRequiresDeviceUnlock = z2;
        for (AidGroup aidGroup : arrayList) {
            this.mCategoryToGroup.put(aidGroup.category, aidGroup);
            this.mAids.addAll(aidGroup.aids);
        }
        this.mBannerResourceId = i;
    }

    /* JADX WARN: Code restructure failed: missing block: B:62:0x0148, code lost:
    
        if (r3.aids.size() <= 0) goto L102;
     */
    /* JADX WARN: Code restructure failed: missing block: B:64:0x0152, code lost:
    
        if (r17.mCategoryToGroup.containsKey(r3.category) != false) goto L106;
     */
    /* JADX WARN: Code restructure failed: missing block: B:65:0x0154, code lost:
    
        r17.mAidGroups.add(r3);
        r17.mCategoryToGroup.put(r3.category, r3);
     */
    /* JADX WARN: Code restructure failed: missing block: B:66:0x0162, code lost:
    
        android.util.Log.e(android.nfc.cardemulation.ApduServiceInfo.TAG, "Not adding <aid-group> with empty or invalid AIDs");
     */
    /* JADX WARN: Not initialized variable reg: 6, insn: 0x01be: MOVE (r5 I:??[OBJECT, ARRAY]) = (r6 I:??[OBJECT, ARRAY]), block:B:83:0x01be */
    /* JADX WARN: Not initialized variable reg: 6, insn: 0x01c0: MOVE (r5 I:??[OBJECT, ARRAY]) = (r6 I:??[OBJECT, ARRAY]), block:B:84:0x01c0 */
    /* JADX WARN: Removed duplicated region for block: B:92:0x01e9  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public ApduServiceInfo(android.content.pm.PackageManager r18, android.content.pm.ResolveInfo r19, boolean r20) throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 493
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.nfc.cardemulation.ApduServiceInfo.<init>(android.content.pm.PackageManager, android.content.pm.ResolveInfo, boolean):void");
    }

    public ComponentName getComponent() {
        return new ComponentName(this.mService.serviceInfo.packageName, this.mService.serviceInfo.name);
    }

    public ArrayList<String> getAids() {
        return this.mAids;
    }

    public ArrayList<AidGroup> getAidGroups() {
        return this.mAidGroups;
    }

    public boolean hasCategory(String str) {
        return this.mCategoryToGroup.containsKey(str);
    }

    public boolean isOnHost() {
        return this.mOnHost;
    }

    public boolean requiresUnlock() {
        return this.mRequiresDeviceUnlock;
    }

    public String getDescription() {
        return this.mDescription;
    }

    public CharSequence loadLabel(PackageManager packageManager) {
        return this.mService.loadLabel(packageManager);
    }

    public Drawable loadIcon(PackageManager packageManager) {
        return this.mService.loadIcon(packageManager);
    }

    public Drawable loadBanner(PackageManager packageManager) {
        try {
            return packageManager.getResourcesForApplication(this.mService.serviceInfo.packageName).getDrawable(this.mBannerResourceId);
        } catch (PackageManager.NameNotFoundException unused) {
            Log.e(TAG, "Could not load banner.");
            return null;
        } catch (Resources.NotFoundException unused2) {
            Log.e(TAG, "Could not load banner.");
            return null;
        }
    }

    static boolean isValidAid(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0 || length % 2 != 0) {
            Log.e(TAG, "AID " + str + " is not correctly formatted.");
            return false;
        }
        if (length >= 10) {
            return true;
        }
        Log.e(TAG, "AID " + str + " is shorter than 5 bytes.");
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("ApduService: ");
        sb.append(getComponent());
        sb.append(", description: " + this.mDescription);
        sb.append(", AID Groups: ");
        Iterator<AidGroup> it = this.mAidGroups.iterator();
        while (it.hasNext()) {
            sb.append(it.next().toString());
        }
        return sb.toString();
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof ApduServiceInfo) {
            return ((ApduServiceInfo) obj).getComponent().equals(getComponent());
        }
        return false;
    }

    public int hashCode() {
        return getComponent().hashCode();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        this.mService.writeToParcel(parcel, i);
        parcel.writeString(this.mDescription);
        parcel.writeInt(this.mOnHost ? 1 : 0);
        parcel.writeInt(this.mAidGroups.size());
        if (this.mAidGroups.size() > 0) {
            parcel.writeTypedList(this.mAidGroups);
        }
        parcel.writeInt(this.mRequiresDeviceUnlock ? 1 : 0);
        parcel.writeInt(this.mBannerResourceId);
    }

    public static class AidGroup implements Parcelable {
        public static final Parcelable.Creator<AidGroup> CREATOR = new Parcelable.Creator<AidGroup>() { // from class: android.nfc.cardemulation.ApduServiceInfo.AidGroup.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public AidGroup createFromParcel(Parcel parcel) {
                String string = parcel.readString();
                String string2 = parcel.readString();
                int i = parcel.readInt();
                ArrayList arrayList = new ArrayList();
                if (i > 0) {
                    parcel.readStringList(arrayList);
                }
                return new AidGroup(arrayList, string, string2);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public AidGroup[] newArray(int i) {
                return new AidGroup[i];
            }
        };
        final ArrayList<String> aids;
        final String category;
        final String description;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        AidGroup(ArrayList<String> arrayList, String str, String str2) {
            this.aids = arrayList;
            this.category = str;
            this.description = str2;
        }

        AidGroup(String str, String str2) {
            this.aids = new ArrayList<>();
            this.category = str;
            this.description = str2;
        }

        public String getCategory() {
            return this.category;
        }

        public ArrayList<String> getAids() {
            return this.aids;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder("Category: " + this.category + ", description: " + this.description + ", AIDs:");
            Iterator<String> it = this.aids.iterator();
            while (it.hasNext()) {
                sb.append(it.next());
                sb.append(", ");
            }
            return sb.toString();
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.category);
            parcel.writeString(this.description);
            parcel.writeInt(this.aids.size());
            if (this.aids.size() > 0) {
                parcel.writeStringList(this.aids);
            }
        }
    }
}
