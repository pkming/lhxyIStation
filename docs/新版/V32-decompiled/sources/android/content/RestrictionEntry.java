package android.content;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: classes.dex */
public class RestrictionEntry implements Parcelable {
    public static final Parcelable.Creator<RestrictionEntry> CREATOR = new Parcelable.Creator<RestrictionEntry>() { // from class: android.content.RestrictionEntry.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RestrictionEntry createFromParcel(Parcel parcel) {
            return new RestrictionEntry(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public RestrictionEntry[] newArray(int i) {
            return new RestrictionEntry[i];
        }
    };
    public static final int TYPE_BOOLEAN = 1;
    public static final int TYPE_CHOICE = 2;
    public static final int TYPE_CHOICE_LEVEL = 3;
    public static final int TYPE_MULTI_SELECT = 4;
    public static final int TYPE_NULL = 0;
    private String[] choices;
    private String currentValue;
    private String[] currentValues;
    private String description;
    private String key;
    private String title;
    private int type;
    private String[] values;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public RestrictionEntry(String str, String str2) {
        this.key = str;
        this.type = 2;
        this.currentValue = str2;
    }

    public RestrictionEntry(String str, boolean z) {
        this.key = str;
        this.type = 1;
        setSelectedState(z);
    }

    public RestrictionEntry(String str, String[] strArr) {
        this.key = str;
        this.type = 4;
        this.currentValues = strArr;
    }

    public void setType(int i) {
        this.type = i;
    }

    public int getType() {
        return this.type;
    }

    public String getSelectedString() {
        return this.currentValue;
    }

    public String[] getAllSelectedStrings() {
        return this.currentValues;
    }

    public boolean getSelectedState() {
        return Boolean.parseBoolean(this.currentValue);
    }

    public void setSelectedString(String str) {
        this.currentValue = str;
    }

    public void setSelectedState(boolean z) {
        this.currentValue = Boolean.toString(z);
    }

    public void setAllSelectedStrings(String[] strArr) {
        this.currentValues = strArr;
    }

    public void setChoiceValues(String[] strArr) {
        this.values = strArr;
    }

    public void setChoiceValues(Context context, int i) {
        this.values = context.getResources().getStringArray(i);
    }

    public String[] getChoiceValues() {
        return this.values;
    }

    public void setChoiceEntries(String[] strArr) {
        this.choices = strArr;
    }

    public void setChoiceEntries(Context context, int i) {
        this.choices = context.getResources().getStringArray(i);
    }

    public String[] getChoiceEntries() {
        return this.choices;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String str) {
        this.description = str;
    }

    public String getKey() {
        return this.key;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String str) {
        this.title = str;
    }

    private boolean equalArrays(String[] strArr, String[] strArr2) {
        if (strArr.length != strArr2.length) {
            return false;
        }
        for (int i = 0; i < strArr.length; i++) {
            if (!strArr[i].equals(strArr2[i])) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object obj) {
        String[] strArr;
        String str;
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RestrictionEntry)) {
            return false;
        }
        RestrictionEntry restrictionEntry = (RestrictionEntry) obj;
        if (this.type == restrictionEntry.type && this.key.equals(restrictionEntry.key)) {
            if (this.currentValues == null && restrictionEntry.currentValues == null && (str = this.currentValue) != null && str.equals(restrictionEntry.currentValue)) {
                return true;
            }
            if (this.currentValue == null && restrictionEntry.currentValue == null && (strArr = this.currentValues) != null && equalArrays(strArr, restrictionEntry.currentValues)) {
                return true;
            }
        }
        return false;
    }

    public int hashCode() {
        int iHashCode = 527 + this.key.hashCode();
        String str = this.currentValue;
        if (str != null) {
            return (iHashCode * 31) + str.hashCode();
        }
        String[] strArr = this.currentValues;
        if (strArr == null) {
            return iHashCode;
        }
        for (String str2 : strArr) {
            if (str2 != null) {
                iHashCode = (iHashCode * 31) + str2.hashCode();
            }
        }
        return iHashCode;
    }

    private String[] readArray(Parcel parcel) {
        int i = parcel.readInt();
        String[] strArr = new String[i];
        for (int i2 = 0; i2 < i; i2++) {
            strArr[i2] = parcel.readString();
        }
        return strArr;
    }

    public RestrictionEntry(Parcel parcel) {
        this.type = parcel.readInt();
        this.key = parcel.readString();
        this.title = parcel.readString();
        this.description = parcel.readString();
        this.choices = readArray(parcel);
        this.values = readArray(parcel);
        this.currentValue = parcel.readString();
        this.currentValues = readArray(parcel);
    }

    private void writeArray(Parcel parcel, String[] strArr) {
        if (strArr == null) {
            parcel.writeInt(0);
            return;
        }
        parcel.writeInt(strArr.length);
        for (String str : strArr) {
            parcel.writeString(str);
        }
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.type);
        parcel.writeString(this.key);
        parcel.writeString(this.title);
        parcel.writeString(this.description);
        writeArray(parcel, this.choices);
        writeArray(parcel, this.values);
        parcel.writeString(this.currentValue);
        writeArray(parcel, this.currentValues);
    }

    public String toString() {
        return "RestrictionsEntry {type=" + this.type + ", key=" + this.key + ", value=" + this.currentValue + "}";
    }
}
