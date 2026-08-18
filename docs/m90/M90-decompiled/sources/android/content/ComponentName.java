package android.content;

import android.os.Parcel;
import android.os.Parcelable;
import java.io.PrintWriter;
import java.util.Objects;

/* JADX INFO: loaded from: classes.dex */
public final class ComponentName implements Parcelable, Cloneable, Comparable<ComponentName> {
    public static final Parcelable.Creator<ComponentName> CREATOR = new Parcelable.Creator<ComponentName>() { // from class: android.content.ComponentName.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ComponentName createFromParcel(Parcel parcel) {
            return new ComponentName(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ComponentName[] newArray(int i) {
            return new ComponentName[i];
        }
    };
    private final String mClass;
    private final String mPackage;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public ComponentName(String str, String str2) {
        Objects.requireNonNull(str, "package name is null");
        Objects.requireNonNull(str2, "class name is null");
        this.mPackage = str;
        this.mClass = str2;
    }

    public ComponentName(Context context, String str) {
        Objects.requireNonNull(str, "class name is null");
        this.mPackage = context.getPackageName();
        this.mClass = str;
    }

    public ComponentName(Context context, Class<?> cls) {
        this.mPackage = context.getPackageName();
        this.mClass = cls.getName();
    }

    /* JADX INFO: renamed from: clone, reason: merged with bridge method [inline-methods] */
    public ComponentName m8clone() {
        return new ComponentName(this.mPackage, this.mClass);
    }

    public String getPackageName() {
        return this.mPackage;
    }

    public String getClassName() {
        return this.mClass;
    }

    public String getShortClassName() {
        int length;
        int length2;
        if (this.mClass.startsWith(this.mPackage) && (length2 = this.mClass.length()) > (length = this.mPackage.length()) && this.mClass.charAt(length) == '.') {
            return this.mClass.substring(length, length2);
        }
        return this.mClass;
    }

    private static void appendShortClassName(StringBuilder sb, String str, String str2) {
        int length;
        int length2;
        if (str2.startsWith(str) && (length2 = str2.length()) > (length = str.length()) && str2.charAt(length) == '.') {
            sb.append((CharSequence) str2, length, length2);
        } else {
            sb.append(str2);
        }
    }

    private static void printShortClassName(PrintWriter printWriter, String str, String str2) {
        int length;
        int length2;
        if (str2.startsWith(str) && (length2 = str2.length()) > (length = str.length()) && str2.charAt(length) == '.') {
            printWriter.write(str2, length, length2 - length);
        } else {
            printWriter.print(str2);
        }
    }

    public String flattenToString() {
        return this.mPackage + "/" + this.mClass;
    }

    public String flattenToShortString() {
        StringBuilder sb = new StringBuilder(this.mPackage.length() + this.mClass.length());
        appendShortString(sb, this.mPackage, this.mClass);
        return sb.toString();
    }

    public void appendShortString(StringBuilder sb) {
        appendShortString(sb, this.mPackage, this.mClass);
    }

    public static void appendShortString(StringBuilder sb, String str, String str2) {
        sb.append(str).append('/');
        appendShortClassName(sb, str, str2);
    }

    public static void printShortString(PrintWriter printWriter, String str, String str2) {
        printWriter.print(str);
        printWriter.print('/');
        printShortClassName(printWriter, str, str2);
    }

    public static ComponentName unflattenFromString(String str) {
        int i;
        int iIndexOf = str.indexOf(47);
        if (iIndexOf < 0 || (i = iIndexOf + 1) >= str.length()) {
            return null;
        }
        String strSubstring = str.substring(0, iIndexOf);
        String strSubstring2 = str.substring(i);
        if (strSubstring2.length() > 0 && strSubstring2.charAt(0) == '.') {
            strSubstring2 = strSubstring + strSubstring2;
        }
        return new ComponentName(strSubstring, strSubstring2);
    }

    public String toShortString() {
        return "{" + this.mPackage + "/" + this.mClass + "}";
    }

    public String toString() {
        return "ComponentInfo{" + this.mPackage + "/" + this.mClass + "}";
    }

    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        try {
            ComponentName componentName = (ComponentName) obj;
            if (this.mPackage.equals(componentName.mPackage)) {
                return this.mClass.equals(componentName.mClass);
            }
            return false;
        } catch (ClassCastException unused) {
            return false;
        }
    }

    public int hashCode() {
        return this.mPackage.hashCode() + this.mClass.hashCode();
    }

    @Override // java.lang.Comparable
    public int compareTo(ComponentName componentName) {
        int iCompareTo = this.mPackage.compareTo(componentName.mPackage);
        return iCompareTo != 0 ? iCompareTo : this.mClass.compareTo(componentName.mClass);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mPackage);
        parcel.writeString(this.mClass);
    }

    public static void writeToParcel(ComponentName componentName, Parcel parcel) {
        if (componentName != null) {
            componentName.writeToParcel(parcel, 0);
        } else {
            parcel.writeString(null);
        }
    }

    public static ComponentName readFromParcel(Parcel parcel) {
        String string = parcel.readString();
        if (string != null) {
            return new ComponentName(string, parcel);
        }
        return null;
    }

    public ComponentName(Parcel parcel) {
        String string = parcel.readString();
        this.mPackage = string;
        Objects.requireNonNull(string, "package name is null");
        String string2 = parcel.readString();
        this.mClass = string2;
        Objects.requireNonNull(string2, "class name is null");
    }

    private ComponentName(String str, Parcel parcel) {
        this.mPackage = str;
        this.mClass = parcel.readString();
    }
}
