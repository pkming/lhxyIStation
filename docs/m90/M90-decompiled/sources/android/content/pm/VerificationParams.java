package android.content.pm;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class VerificationParams implements Parcelable {
    public static final Parcelable.Creator<VerificationParams> CREATOR = new Parcelable.Creator<VerificationParams>() { // from class: android.content.pm.VerificationParams.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VerificationParams createFromParcel(Parcel parcel) {
            return new VerificationParams(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public VerificationParams[] newArray(int i) {
            return new VerificationParams[i];
        }
    };
    public static final int NO_UID = -1;
    private static final String TO_STRING_PREFIX = "VerificationParams{";
    private int mInstallerUid;
    private final ManifestDigest mManifestDigest;
    private final Uri mOriginatingURI;
    private final int mOriginatingUid;
    private final Uri mReferrer;
    private final Uri mVerificationURI;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public VerificationParams(Uri uri, Uri uri2, Uri uri3, int i, ManifestDigest manifestDigest) {
        this.mVerificationURI = uri;
        this.mOriginatingURI = uri2;
        this.mReferrer = uri3;
        this.mOriginatingUid = i;
        this.mManifestDigest = manifestDigest;
        this.mInstallerUid = -1;
    }

    public Uri getVerificationURI() {
        return this.mVerificationURI;
    }

    public Uri getOriginatingURI() {
        return this.mOriginatingURI;
    }

    public Uri getReferrer() {
        return this.mReferrer;
    }

    public int getOriginatingUid() {
        return this.mOriginatingUid;
    }

    public ManifestDigest getManifestDigest() {
        return this.mManifestDigest;
    }

    public int getInstallerUid() {
        return this.mInstallerUid;
    }

    public void setInstallerUid(int i) {
        this.mInstallerUid = i;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof VerificationParams)) {
            return false;
        }
        VerificationParams verificationParams = (VerificationParams) obj;
        Uri uri = this.mVerificationURI;
        if (uri == null) {
            if (verificationParams.mVerificationURI != null) {
                return false;
            }
        } else if (!uri.equals(verificationParams.mVerificationURI)) {
            return false;
        }
        Uri uri2 = this.mOriginatingURI;
        if (uri2 == null) {
            if (verificationParams.mOriginatingURI != null) {
                return false;
            }
        } else if (!uri2.equals(verificationParams.mOriginatingURI)) {
            return false;
        }
        Uri uri3 = this.mReferrer;
        if (uri3 == null) {
            if (verificationParams.mReferrer != null) {
                return false;
            }
        } else if (!uri3.equals(verificationParams.mReferrer)) {
            return false;
        }
        if (this.mOriginatingUid != verificationParams.mOriginatingUid) {
            return false;
        }
        ManifestDigest manifestDigest = this.mManifestDigest;
        if (manifestDigest == null) {
            if (verificationParams.mManifestDigest != null) {
                return false;
            }
        } else if (!manifestDigest.equals(verificationParams.mManifestDigest)) {
            return false;
        }
        return this.mInstallerUid == verificationParams.mInstallerUid;
    }

    public int hashCode() {
        Uri uri = this.mVerificationURI;
        int iHashCode = ((uri == null ? 1 : uri.hashCode()) * 5) + 3;
        Uri uri2 = this.mOriginatingURI;
        int iHashCode2 = iHashCode + ((uri2 == null ? 1 : uri2.hashCode()) * 7);
        Uri uri3 = this.mReferrer;
        int iHashCode3 = iHashCode2 + ((uri3 == null ? 1 : uri3.hashCode()) * 11) + (this.mOriginatingUid * 13);
        ManifestDigest manifestDigest = this.mManifestDigest;
        return iHashCode3 + ((manifestDigest != null ? manifestDigest.hashCode() : 1) * 17) + (this.mInstallerUid * 19);
    }

    public String toString() {
        return TO_STRING_PREFIX + "mVerificationURI=" + this.mVerificationURI.toString() + ",mOriginatingURI=" + this.mOriginatingURI.toString() + ",mReferrer=" + this.mReferrer.toString() + ",mOriginatingUid=" + this.mOriginatingUid + ",mManifestDigest=" + this.mManifestDigest.toString() + ",mInstallerUid=" + this.mInstallerUid + '}';
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(this.mVerificationURI, 0);
        parcel.writeParcelable(this.mOriginatingURI, 0);
        parcel.writeParcelable(this.mReferrer, 0);
        parcel.writeInt(this.mOriginatingUid);
        parcel.writeParcelable(this.mManifestDigest, 0);
        parcel.writeInt(this.mInstallerUid);
    }

    private VerificationParams(Parcel parcel) {
        this.mVerificationURI = (Uri) parcel.readParcelable(Uri.class.getClassLoader());
        this.mOriginatingURI = (Uri) parcel.readParcelable(Uri.class.getClassLoader());
        this.mReferrer = (Uri) parcel.readParcelable(Uri.class.getClassLoader());
        this.mOriginatingUid = parcel.readInt();
        this.mManifestDigest = (ManifestDigest) parcel.readParcelable(ManifestDigest.class.getClassLoader());
        this.mInstallerUid = parcel.readInt();
    }
}
