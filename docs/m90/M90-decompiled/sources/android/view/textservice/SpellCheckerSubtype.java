package android.view.textservice;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Slog;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public final class SpellCheckerSubtype implements Parcelable {
    public static final Parcelable.Creator<SpellCheckerSubtype> CREATOR = new Parcelable.Creator<SpellCheckerSubtype>() { // from class: android.view.textservice.SpellCheckerSubtype.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SpellCheckerSubtype createFromParcel(Parcel parcel) {
            return new SpellCheckerSubtype(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public SpellCheckerSubtype[] newArray(int i) {
            return new SpellCheckerSubtype[i];
        }
    };
    private static final String EXTRA_VALUE_KEY_VALUE_SEPARATOR = "=";
    private static final String EXTRA_VALUE_PAIR_SEPARATOR = ",";
    private static final String TAG = "SpellCheckerSubtype";
    private HashMap<String, String> mExtraValueHashMapCache;
    private final String mSubtypeExtraValue;
    private final int mSubtypeHashCode;
    private final String mSubtypeLocale;
    private final int mSubtypeNameResId;

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public SpellCheckerSubtype(int i, String str, String str2) {
        this.mSubtypeNameResId = i;
        str = str == null ? "" : str;
        this.mSubtypeLocale = str;
        str2 = str2 == null ? "" : str2;
        this.mSubtypeExtraValue = str2;
        this.mSubtypeHashCode = hashCodeInternal(str, str2);
    }

    SpellCheckerSubtype(Parcel parcel) {
        this.mSubtypeNameResId = parcel.readInt();
        String string = parcel.readString();
        string = string == null ? "" : string;
        this.mSubtypeLocale = string;
        String string2 = parcel.readString();
        String str = string2 != null ? string2 : "";
        this.mSubtypeExtraValue = str;
        this.mSubtypeHashCode = hashCodeInternal(string, str);
    }

    public int getNameResId() {
        return this.mSubtypeNameResId;
    }

    public String getLocale() {
        return this.mSubtypeLocale;
    }

    public String getExtraValue() {
        return this.mSubtypeExtraValue;
    }

    private HashMap<String, String> getExtraValueHashMap() {
        if (this.mExtraValueHashMapCache == null) {
            this.mExtraValueHashMapCache = new HashMap<>();
            for (String str : this.mSubtypeExtraValue.split(EXTRA_VALUE_PAIR_SEPARATOR)) {
                String[] strArrSplit = str.split(EXTRA_VALUE_KEY_VALUE_SEPARATOR);
                if (strArrSplit.length == 1) {
                    this.mExtraValueHashMapCache.put(strArrSplit[0], null);
                } else if (strArrSplit.length > 1) {
                    if (strArrSplit.length > 2) {
                        Slog.w(TAG, "ExtraValue has two or more '='s");
                    }
                    this.mExtraValueHashMapCache.put(strArrSplit[0], strArrSplit[1]);
                }
            }
        }
        return this.mExtraValueHashMapCache;
    }

    public boolean containsExtraValueKey(String str) {
        return getExtraValueHashMap().containsKey(str);
    }

    public String getExtraValueOf(String str) {
        return getExtraValueHashMap().get(str);
    }

    public int hashCode() {
        return this.mSubtypeHashCode;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SpellCheckerSubtype)) {
            return false;
        }
        SpellCheckerSubtype spellCheckerSubtype = (SpellCheckerSubtype) obj;
        return spellCheckerSubtype.hashCode() == hashCode() && spellCheckerSubtype.getNameResId() == getNameResId() && spellCheckerSubtype.getLocale().equals(getLocale()) && spellCheckerSubtype.getExtraValue().equals(getExtraValue());
    }

    public static Locale constructLocaleFromString(String str) {
        if (TextUtils.isEmpty(str)) {
            return null;
        }
        String[] strArrSplit = str.split("_", 3);
        if (strArrSplit.length == 1) {
            return new Locale(strArrSplit[0]);
        }
        if (strArrSplit.length == 2) {
            return new Locale(strArrSplit[0], strArrSplit[1]);
        }
        if (strArrSplit.length == 3) {
            return new Locale(strArrSplit[0], strArrSplit[1], strArrSplit[2]);
        }
        return null;
    }

    public CharSequence getDisplayName(Context context, String str, ApplicationInfo applicationInfo) {
        Locale localeConstructLocaleFromString = constructLocaleFromString(this.mSubtypeLocale);
        String displayName = localeConstructLocaleFromString != null ? localeConstructLocaleFromString.getDisplayName() : this.mSubtypeLocale;
        if (this.mSubtypeNameResId == 0) {
            return displayName;
        }
        CharSequence text = context.getPackageManager().getText(str, this.mSubtypeNameResId, applicationInfo);
        return !TextUtils.isEmpty(text) ? String.format(text.toString(), displayName) : displayName;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(this.mSubtypeNameResId);
        parcel.writeString(this.mSubtypeLocale);
        parcel.writeString(this.mSubtypeExtraValue);
    }

    private static int hashCodeInternal(String str, String str2) {
        return Arrays.hashCode(new Object[]{str, str2});
    }

    public static List<SpellCheckerSubtype> sort(Context context, int i, SpellCheckerInfo spellCheckerInfo, List<SpellCheckerSubtype> list) {
        if (spellCheckerInfo == null) {
            return list;
        }
        HashSet hashSet = new HashSet(list);
        ArrayList arrayList = new ArrayList();
        int subtypeCount = spellCheckerInfo.getSubtypeCount();
        for (int i2 = 0; i2 < subtypeCount; i2++) {
            SpellCheckerSubtype subtypeAt = spellCheckerInfo.getSubtypeAt(i2);
            if (hashSet.contains(subtypeAt)) {
                arrayList.add(subtypeAt);
                hashSet.remove(subtypeAt);
            }
        }
        Iterator it = hashSet.iterator();
        while (it.hasNext()) {
            arrayList.add((SpellCheckerSubtype) it.next());
        }
        return arrayList;
    }
}
