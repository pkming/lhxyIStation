package android.content.pm;

import android.os.Parcel;
import android.os.Parcelable;
import android.telephony.PhoneNumberUtils;
import android.util.Slog;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import libcore.io.IoUtils;

/* JADX INFO: loaded from: classes.dex */
public class ManifestDigest implements Parcelable {
    public static final Parcelable.Creator<ManifestDigest> CREATOR = new Parcelable.Creator<ManifestDigest>() { // from class: android.content.pm.ManifestDigest.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ManifestDigest createFromParcel(Parcel parcel) {
            return new ManifestDigest(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ManifestDigest[] newArray(int i) {
            return new ManifestDigest[i];
        }
    };
    private static final String DIGEST_ALGORITHM = "SHA-256";
    private static final String TAG = "ManifestDigest";
    private static final String TO_STRING_PREFIX = "ManifestDigest {mDigest=";
    private final byte[] mDigest;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    ManifestDigest(byte[] bArr) {
        this.mDigest = bArr;
    }

    private ManifestDigest(Parcel parcel) {
        this.mDigest = parcel.createByteArray();
    }

    static ManifestDigest fromInputStream(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGORITHM);
            DigestInputStream digestInputStream = new DigestInputStream(new BufferedInputStream(inputStream), messageDigest);
            try {
                try {
                    do {
                    } while (digestInputStream.read(new byte[8192], 0, 8192) != -1);
                    IoUtils.closeQuietly(digestInputStream);
                    return new ManifestDigest(messageDigest.digest());
                } catch (IOException unused) {
                    Slog.w(TAG, "Could not read manifest");
                    IoUtils.closeQuietly(digestInputStream);
                    return null;
                }
            } catch (Throwable th) {
                IoUtils.closeQuietly(digestInputStream);
                throw th;
            }
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 must be available", e);
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ManifestDigest)) {
            return false;
        }
        ManifestDigest manifestDigest = (ManifestDigest) obj;
        return this == manifestDigest || Arrays.equals(this.mDigest, manifestDigest.mDigest);
    }

    public int hashCode() {
        return Arrays.hashCode(this.mDigest);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(24 + (this.mDigest.length * 3) + 1);
        sb.append(TO_STRING_PREFIX);
        int length = this.mDigest.length;
        for (int i = 0; i < length; i++) {
            IntegralToString.appendByteAsHex(sb, this.mDigest[i], false);
            sb.append(PhoneNumberUtils.PAUSE);
        }
        sb.append('}');
        return sb.toString();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByteArray(this.mDigest);
    }
}
