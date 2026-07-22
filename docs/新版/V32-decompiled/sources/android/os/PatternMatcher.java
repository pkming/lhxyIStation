package android.os;

import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class PatternMatcher implements Parcelable {
    public static final Parcelable.Creator<PatternMatcher> CREATOR = new Parcelable.Creator<PatternMatcher>() { // from class: android.os.PatternMatcher.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PatternMatcher createFromParcel(Parcel parcel) {
            return new PatternMatcher(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PatternMatcher[] newArray(int i) {
            return new PatternMatcher[i];
        }
    };
    public static final int PATTERN_LITERAL = 0;
    public static final int PATTERN_PREFIX = 1;
    public static final int PATTERN_SIMPLE_GLOB = 2;
    private final String mPattern;
    private final int mType;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public PatternMatcher(String str, int i) {
        this.mPattern = str;
        this.mType = i;
    }

    public final String getPath() {
        return this.mPattern;
    }

    public final int getType() {
        return this.mType;
    }

    public boolean match(String str) {
        return matchPattern(this.mPattern, str, this.mType);
    }

    public String toString() {
        int i = this.mType;
        return "PatternMatcher{" + (i != 0 ? i != 1 ? i != 2 ? "? " : "GLOB: " : "PREFIX: " : "LITERAL: ") + this.mPattern + "}";
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mPattern);
        parcel.writeInt(this.mType);
    }

    public PatternMatcher(Parcel parcel) {
        this.mPattern = parcel.readString();
        this.mType = parcel.readInt();
    }

    static boolean matchPattern(String str, String str2, int i) {
        if (str2 == null) {
            return false;
        }
        if (i == 0) {
            return str.equals(str2);
        }
        if (i == 1) {
            return str2.startsWith(str);
        }
        if (i != 2) {
            return false;
        }
        int length = str.length();
        if (length <= 0) {
            return str2.length() <= 0;
        }
        int length2 = str2.length();
        char cCharAt = str.charAt(0);
        int i2 = 0;
        int i3 = 0;
        while (i2 < length && i3 < length2) {
            i2++;
            char cCharAt2 = i2 < length ? str.charAt(i2) : (char) 0;
            boolean z = cCharAt == '\\';
            if (z) {
                i2++;
                char c = cCharAt2;
                cCharAt2 = i2 < length ? str.charAt(i2) : (char) 0;
                cCharAt = c;
            }
            if (cCharAt2 == '*') {
                if (z || cCharAt != '.') {
                    while (str2.charAt(i3) == cCharAt && (i3 = i3 + 1) < length2) {
                    }
                    i2++;
                    cCharAt = i2 < length ? str.charAt(i2) : (char) 0;
                } else {
                    if (i2 >= length - 1) {
                        return true;
                    }
                    int i4 = i2 + 1;
                    char cCharAt3 = str.charAt(i4);
                    if (cCharAt3 == '\\') {
                        i4++;
                        cCharAt3 = i4 < length ? str.charAt(i4) : (char) 0;
                    }
                    while (str2.charAt(i3) != cCharAt3 && (i3 = i3 + 1) < length2) {
                    }
                    if (i3 == length2) {
                        return false;
                    }
                    i2 = i4 + 1;
                    cCharAt = i2 < length ? str.charAt(i2) : (char) 0;
                    i3++;
                }
            } else {
                if (cCharAt != '.' && str2.charAt(i3) != cCharAt) {
                    return false;
                }
                i3++;
                cCharAt = cCharAt2;
            }
        }
        if (i2 < length || i3 < length2) {
            return i2 == length - 2 && str.charAt(i2) == '.' && str.charAt(i2 + 1) == '*';
        }
        return true;
    }
}
