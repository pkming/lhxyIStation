package android.content;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public class ClipDescription implements Parcelable {
    public static final Parcelable.Creator<ClipDescription> CREATOR = new Parcelable.Creator<ClipDescription>() { // from class: android.content.ClipDescription.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ClipDescription createFromParcel(Parcel parcel) {
            return new ClipDescription(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ClipDescription[] newArray(int i) {
            return new ClipDescription[i];
        }
    };
    public static final String MIMETYPE_TEXT_HTML = "text/html";
    public static final String MIMETYPE_TEXT_INTENT = "text/vnd.android.intent";
    public static final String MIMETYPE_TEXT_PLAIN = "text/plain";
    public static final String MIMETYPE_TEXT_URILIST = "text/uri-list";
    final CharSequence mLabel;
    final String[] mMimeTypes;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ClipDescription(CharSequence charSequence, String[] strArr) {
        Objects.requireNonNull(strArr, "mimeTypes is null");
        this.mLabel = charSequence;
        this.mMimeTypes = strArr;
    }

    public ClipDescription(ClipDescription clipDescription) {
        this.mLabel = clipDescription.mLabel;
        this.mMimeTypes = clipDescription.mMimeTypes;
    }

    /* JADX WARN: Removed duplicated region for block: B:16:0x002e  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static boolean compareMimeTypes(java.lang.String r5, java.lang.String r6) {
        /*
            int r0 = r6.length()
            r1 = 1
            r2 = 3
            if (r0 != r2) goto L11
        */
        //  java.lang.String r2 = "*/*"
        /*
            boolean r2 = r6.equals(r2)
            if (r2 == 0) goto L11
            return r1
        L11:
            r2 = 47
            int r2 = r6.indexOf(r2)
            r3 = 0
            if (r2 <= 0) goto L35
            int r4 = r2 + 2
            if (r0 != r4) goto L2e
            int r2 = r2 + r1
            char r0 = r6.charAt(r2)
            r4 = 42
            if (r0 != r4) goto L2e
            boolean r5 = r6.regionMatches(r3, r5, r3, r2)
            if (r5 == 0) goto L35
            return r1
        L2e:
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L35
            return r1
        L35:
            return r3
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.ClipDescription.compareMimeTypes(java.lang.String, java.lang.String):boolean");
    }

    public CharSequence getLabel() {
        return this.mLabel;
    }

    public boolean hasMimeType(String str) {
        int i = 0;
        while (true) {
            String[] strArr = this.mMimeTypes;
            if (i >= strArr.length) {
                return false;
            }
            if (compareMimeTypes(strArr[i], str)) {
                return true;
            }
            i++;
        }
    }

    public String[] filterMimeTypes(String str) {
        int i = 0;
        ArrayList arrayList = null;
        while (true) {
            String[] strArr = this.mMimeTypes;
            if (i >= strArr.length) {
                break;
            }
            if (compareMimeTypes(strArr[i], str)) {
                if (arrayList == null) {
                    arrayList = new ArrayList();
                }
                arrayList.add(this.mMimeTypes[i]);
            }
            i++;
        }
        if (arrayList == null) {
            return null;
        }
        String[] strArr2 = new String[arrayList.size()];
        arrayList.toArray(strArr2);
        return strArr2;
    }

    public int getMimeTypeCount() {
        return this.mMimeTypes.length;
    }

    public String getMimeType(int i) {
        return this.mMimeTypes[i];
    }

    public void validate() {
        String[] strArr = this.mMimeTypes;
        Objects.requireNonNull(strArr, "null mime types");
        if (strArr.length <= 0) {
            throw new IllegalArgumentException("must have at least 1 mime type");
        }
        int i = 0;
        while (true) {
            String[] strArr2 = this.mMimeTypes;
            if (i >= strArr2.length) {
                return;
            }
            if (strArr2[i] == null) {
                throw new NullPointerException("mime type at " + i + " is null");
            }
            i++;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("ClipDescription { ");
        toShortString(sb);
        sb.append(" }");
        return sb.toString();
    }

    public boolean toShortString(StringBuilder sb) {
        boolean z = false;
        int i = 0;
        boolean z2 = true;
        while (i < this.mMimeTypes.length) {
            if (!z2) {
                sb.append(' ');
            }
            sb.append(this.mMimeTypes[i]);
            i++;
            z2 = false;
        }
        if (this.mLabel != null) {
            if (!z2) {
                sb.append(' ');
            }
            sb.append('\"');
            sb.append(this.mLabel);
            sb.append('\"');
        } else {
            z = z2;
        }
        return !z;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        TextUtils.writeToParcel(this.mLabel, parcel, i);
        parcel.writeStringArray(this.mMimeTypes);
    }

    ClipDescription(Parcel parcel) {
        this.mLabel = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
        this.mMimeTypes = parcel.createStringArray();
    }
}
