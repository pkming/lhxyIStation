package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class PackageInfoLite implements Parcelable {
    public static final Parcelable.Creator<PackageInfoLite> CREATOR = new Parcelable.Creator<PackageInfoLite>() { // from class: android.content.pm.PackageInfoLite.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PackageInfoLite createFromParcel(Parcel parcel) {
            return new PackageInfoLite(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PackageInfoLite[] newArray(int i) {
            return new PackageInfoLite[i];
        }
    };
    public int installLocation;
    public String packageName;
    public int recommendedInstallLocation;
    public VerifierInfo[] verifiers;
    public int versionCode;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public PackageInfoLite() {
    }

    public String toString() {
        return "PackageInfoLite{" + Integer.toHexString(System.identityHashCode(this)) + " " + this.packageName + "}";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.packageName);
        parcel.writeInt(this.versionCode);
        parcel.writeInt(this.recommendedInstallLocation);
        parcel.writeInt(this.installLocation);
        VerifierInfo[] verifierInfoArr = this.verifiers;
        if (verifierInfoArr == null || verifierInfoArr.length == 0) {
            parcel.writeInt(0);
        } else {
            parcel.writeInt(verifierInfoArr.length);
            parcel.writeTypedArray(this.verifiers, i);
        }
    }

    private PackageInfoLite(Parcel parcel) {
        this.packageName = parcel.readString();
        this.versionCode = parcel.readInt();
        this.recommendedInstallLocation = parcel.readInt();
        this.installLocation = parcel.readInt();
        int i = parcel.readInt();
        if (i == 0) {
            this.verifiers = new VerifierInfo[0];
            return;
        }
        VerifierInfo[] verifierInfoArr = new VerifierInfo[i];
        this.verifiers = verifierInfoArr;
        parcel.readTypedArray(verifierInfoArr, VerifierInfo.CREATOR);
    }
}
