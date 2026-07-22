package android.content.res;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemProperties;
import android.text.TextUtils;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public final class Configuration implements Parcelable, Comparable<Configuration> {
    public static final int DENSITY_DPI_UNDEFINED = 0;
    public static final int HARDKEYBOARDHIDDEN_NO = 1;
    public static final int HARDKEYBOARDHIDDEN_UNDEFINED = 0;
    public static final int HARDKEYBOARDHIDDEN_YES = 2;
    public static final int KEYBOARDHIDDEN_NO = 1;
    public static final int KEYBOARDHIDDEN_SOFT = 3;
    public static final int KEYBOARDHIDDEN_UNDEFINED = 0;
    public static final int KEYBOARDHIDDEN_YES = 2;
    public static final int KEYBOARD_12KEY = 3;
    public static final int KEYBOARD_NOKEYS = 1;
    public static final int KEYBOARD_QWERTY = 2;
    public static final int KEYBOARD_UNDEFINED = 0;
    public static final int MNC_ZERO = 65535;
    public static final int NATIVE_CONFIG_DENSITY = 256;
    public static final int NATIVE_CONFIG_KEYBOARD = 16;
    public static final int NATIVE_CONFIG_KEYBOARD_HIDDEN = 32;
    public static final int NATIVE_CONFIG_LAYOUTDIR = 16384;
    public static final int NATIVE_CONFIG_LOCALE = 4;
    public static final int NATIVE_CONFIG_MCC = 1;
    public static final int NATIVE_CONFIG_MNC = 2;
    public static final int NATIVE_CONFIG_NAVIGATION = 64;
    public static final int NATIVE_CONFIG_ORIENTATION = 128;
    public static final int NATIVE_CONFIG_SCREEN_LAYOUT = 2048;
    public static final int NATIVE_CONFIG_SCREEN_SIZE = 512;
    public static final int NATIVE_CONFIG_SMALLEST_SCREEN_SIZE = 8192;
    public static final int NATIVE_CONFIG_TOUCHSCREEN = 8;
    public static final int NATIVE_CONFIG_UI_MODE = 4096;
    public static final int NATIVE_CONFIG_VERSION = 1024;
    public static final int NAVIGATIONHIDDEN_NO = 1;
    public static final int NAVIGATIONHIDDEN_UNDEFINED = 0;
    public static final int NAVIGATIONHIDDEN_YES = 2;
    public static final int NAVIGATION_DPAD = 2;
    public static final int NAVIGATION_NONAV = 1;
    public static final int NAVIGATION_TRACKBALL = 3;
    public static final int NAVIGATION_UNDEFINED = 0;
    public static final int NAVIGATION_WHEEL = 4;
    public static final int ORIENTATION_LANDSCAPE = 2;
    public static final int ORIENTATION_PORTRAIT = 1;

    @Deprecated
    public static final int ORIENTATION_SQUARE = 3;
    public static final int ORIENTATION_UNDEFINED = 0;
    public static final int SCREENLAYOUT_COMPAT_NEEDED = 268435456;
    public static final int SCREENLAYOUT_LAYOUTDIR_LTR = 64;
    public static final int SCREENLAYOUT_LAYOUTDIR_MASK = 192;
    public static final int SCREENLAYOUT_LAYOUTDIR_RTL = 128;
    public static final int SCREENLAYOUT_LAYOUTDIR_SHIFT = 6;
    public static final int SCREENLAYOUT_LAYOUTDIR_UNDEFINED = 0;
    public static final int SCREENLAYOUT_LONG_MASK = 48;
    public static final int SCREENLAYOUT_LONG_NO = 16;
    public static final int SCREENLAYOUT_LONG_UNDEFINED = 0;
    public static final int SCREENLAYOUT_LONG_YES = 32;
    public static final int SCREENLAYOUT_SIZE_LARGE = 3;
    public static final int SCREENLAYOUT_SIZE_MASK = 15;
    public static final int SCREENLAYOUT_SIZE_NORMAL = 2;
    public static final int SCREENLAYOUT_SIZE_SMALL = 1;
    public static final int SCREENLAYOUT_SIZE_UNDEFINED = 0;
    public static final int SCREENLAYOUT_SIZE_XLARGE = 4;
    public static final int SCREENLAYOUT_UNDEFINED = 0;
    public static final int SCREEN_HEIGHT_DP_UNDEFINED = 0;
    public static final int SCREEN_WIDTH_DP_UNDEFINED = 0;
    public static final int SMALLEST_SCREEN_WIDTH_DP_UNDEFINED = 0;
    public static final int TOUCHSCREEN_FINGER = 3;
    public static final int TOUCHSCREEN_NOTOUCH = 1;

    @Deprecated
    public static final int TOUCHSCREEN_STYLUS = 2;
    public static final int TOUCHSCREEN_UNDEFINED = 0;
    public static final int UI_MODE_NIGHT_MASK = 48;
    public static final int UI_MODE_NIGHT_NO = 16;
    public static final int UI_MODE_NIGHT_UNDEFINED = 0;
    public static final int UI_MODE_NIGHT_YES = 32;
    public static final int UI_MODE_TYPE_APPLIANCE = 5;
    public static final int UI_MODE_TYPE_CAR = 3;
    public static final int UI_MODE_TYPE_DESK = 2;
    public static final int UI_MODE_TYPE_MASK = 15;
    public static final int UI_MODE_TYPE_NORMAL = 1;
    public static final int UI_MODE_TYPE_TELEVISION = 4;
    public static final int UI_MODE_TYPE_UNDEFINED = 0;
    public int compatScreenHeightDp;
    public int compatScreenWidthDp;
    public int compatSmallestScreenWidthDp;
    public int densityDpi;
    public float fontScale;
    public int hardKeyboardHidden;
    public int keyboard;
    public int keyboardHidden;
    public Locale locale;
    public int mcc;
    public int mnc;
    public int navigation;
    public int navigationHidden;
    public int orientation;
    public int screenHeightDp;
    public int screenLayout;
    public int screenWidthDp;
    public int seq;
    public int smallestScreenWidthDp;
    public int touchscreen;
    public int uiMode;
    public boolean userSetLocale;
    public static final Configuration EMPTY = new Configuration();
    public static final Parcelable.Creator<Configuration> CREATOR = new Parcelable.Creator<Configuration>() { // from class: android.content.res.Configuration.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Configuration createFromParcel(Parcel parcel) {
            return new Configuration(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Configuration[] newArray(int i) {
            return new Configuration[i];
        }
    };

    private static int getScreenLayoutNoDirection(int i) {
        return i & (-193);
    }

    public static boolean needNewResources(int i, int i2) {
        return (i & (i2 | 1073741824)) != 0;
    }

    public static int resetScreenLayout(int i) {
        return (i & (-268435520)) | 36;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public static int reduceScreenLayout(int i, int i2, int i3) {
        boolean z;
        int i4 = 1;
        if (i2 < 470) {
            z = false;
        } else {
            int i5 = (i2 < 960 || i3 < 720) ? (i2 < 640 || i3 < 480) ? 2 : 3 : 4;
            z = i3 > 321 || i2 > 570;
            z = (i2 * 3) / 5 >= i3 - 1;
            i4 = i5;
        }
        if (!z) {
            i = (i & (-49)) | 16;
        }
        if (z) {
            i |= 268435456;
        }
        return i4 < (i & 15) ? (i & (-16)) | i4 : i;
    }

    public boolean isLayoutSizeAtLeast(int i) {
        int i2 = this.screenLayout & 15;
        return i2 != 0 && i2 >= i;
    }

    public Configuration() {
        setToDefaults();
    }

    public Configuration(Configuration configuration) {
        setTo(configuration);
    }

    public void setTo(Configuration configuration) {
        this.fontScale = configuration.fontScale;
        this.mcc = configuration.mcc;
        this.mnc = configuration.mnc;
        Locale locale = configuration.locale;
        if (locale != null) {
            this.locale = (Locale) locale.clone();
        }
        this.userSetLocale = configuration.userSetLocale;
        this.touchscreen = configuration.touchscreen;
        this.keyboard = configuration.keyboard;
        this.keyboardHidden = configuration.keyboardHidden;
        this.hardKeyboardHidden = configuration.hardKeyboardHidden;
        this.navigation = configuration.navigation;
        this.navigationHidden = configuration.navigationHidden;
        this.orientation = configuration.orientation;
        this.screenLayout = configuration.screenLayout;
        this.uiMode = configuration.uiMode;
        this.screenWidthDp = configuration.screenWidthDp;
        this.screenHeightDp = configuration.screenHeightDp;
        this.smallestScreenWidthDp = configuration.smallestScreenWidthDp;
        this.densityDpi = configuration.densityDpi;
        this.compatScreenWidthDp = configuration.compatScreenWidthDp;
        this.compatScreenHeightDp = configuration.compatScreenHeightDp;
        this.compatSmallestScreenWidthDp = configuration.compatSmallestScreenWidthDp;
        this.seq = configuration.seq;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("{");
        sb.append(this.fontScale);
        sb.append(" ");
        int i = this.mcc;
        if (i != 0) {
            sb.append(i);
            sb.append("mcc");
        } else {
            sb.append("?mcc");
        }
        int i2 = this.mnc;
        if (i2 != 0) {
            sb.append(i2);
            sb.append("mnc");
        } else {
            sb.append("?mnc");
        }
        if (this.locale != null) {
            sb.append(" ");
            sb.append(this.locale);
        } else {
            sb.append(" ?locale");
        }
        int i3 = this.screenLayout & 192;
        if (i3 == 0) {
            sb.append(" ?layoutDir");
        } else if (i3 == 64) {
            sb.append(" ldltr");
        } else if (i3 == 128) {
            sb.append(" ldrtl");
        } else {
            sb.append(" layoutDir=");
            sb.append(i3 >> 6);
        }
        if (this.smallestScreenWidthDp != 0) {
            sb.append(" sw");
            sb.append(this.smallestScreenWidthDp);
            sb.append("dp");
        } else {
            sb.append(" ?swdp");
        }
        if (this.screenWidthDp != 0) {
            sb.append(" w");
            sb.append(this.screenWidthDp);
            sb.append("dp");
        } else {
            sb.append(" ?wdp");
        }
        if (this.screenHeightDp != 0) {
            sb.append(" h");
            sb.append(this.screenHeightDp);
            sb.append("dp");
        } else {
            sb.append(" ?hdp");
        }
        if (this.densityDpi != 0) {
            sb.append(" ");
            sb.append(this.densityDpi);
            sb.append("dpi");
        } else {
            sb.append(" ?density");
        }
        int i4 = this.screenLayout & 15;
        if (i4 == 0) {
            sb.append(" ?lsize");
        } else if (i4 == 1) {
            sb.append(" smll");
        } else if (i4 == 2) {
            sb.append(" nrml");
        } else if (i4 == 3) {
            sb.append(" lrg");
        } else if (i4 == 4) {
            sb.append(" xlrg");
        } else {
            sb.append(" layoutSize=");
            sb.append(this.screenLayout & 15);
        }
        int i5 = this.screenLayout & 48;
        if (i5 == 0) {
            sb.append(" ?long");
        } else if (i5 != 16) {
            if (i5 == 32) {
                sb.append(" long");
            } else {
                sb.append(" layoutLong=");
                sb.append(this.screenLayout & 48);
            }
        }
        int i6 = this.orientation;
        if (i6 == 0) {
            sb.append(" ?orien");
        } else if (i6 == 1) {
            sb.append(" port");
        } else if (i6 == 2) {
            sb.append(" land");
        } else {
            sb.append(" orien=");
            sb.append(this.orientation);
        }
        int i7 = this.uiMode & 15;
        if (i7 == 0) {
            sb.append(" ?uimode");
        } else if (i7 != 1) {
            if (i7 == 2) {
                sb.append(" desk");
            } else if (i7 == 3) {
                sb.append(" car");
            } else if (i7 == 4) {
                sb.append(" television");
            } else if (i7 == 5) {
                sb.append(" appliance");
            } else {
                sb.append(" uimode=");
                sb.append(this.uiMode & 15);
            }
        }
        int i8 = this.uiMode & 48;
        if (i8 == 0) {
            sb.append(" ?night");
        } else if (i8 != 16) {
            if (i8 == 32) {
                sb.append(" night");
            } else {
                sb.append(" night=");
                sb.append(this.uiMode & 48);
            }
        }
        int i9 = this.touchscreen;
        if (i9 == 0) {
            sb.append(" ?touch");
        } else if (i9 == 1) {
            sb.append(" -touch");
        } else if (i9 == 2) {
            sb.append(" stylus");
        } else if (i9 == 3) {
            sb.append(" finger");
        } else {
            sb.append(" touch=");
            sb.append(this.touchscreen);
        }
        int i10 = this.keyboard;
        if (i10 == 0) {
            sb.append(" ?keyb");
        } else if (i10 == 1) {
            sb.append(" -keyb");
        } else if (i10 == 2) {
            sb.append(" qwerty");
        } else if (i10 == 3) {
            sb.append(" 12key");
        } else {
            sb.append(" keys=");
            sb.append(this.keyboard);
        }
        int i11 = this.keyboardHidden;
        if (i11 == 0) {
            sb.append("/?");
        } else if (i11 == 1) {
            sb.append("/v");
        } else if (i11 == 2) {
            sb.append("/h");
        } else if (i11 == 3) {
            sb.append("/s");
        } else {
            sb.append("/");
            sb.append(this.keyboardHidden);
        }
        int i12 = this.hardKeyboardHidden;
        if (i12 == 0) {
            sb.append("/?");
        } else if (i12 == 1) {
            sb.append("/v");
        } else if (i12 == 2) {
            sb.append("/h");
        } else {
            sb.append("/");
            sb.append(this.hardKeyboardHidden);
        }
        int i13 = this.navigation;
        if (i13 == 0) {
            sb.append(" ?nav");
        } else if (i13 == 1) {
            sb.append(" -nav");
        } else if (i13 == 2) {
            sb.append(" dpad");
        } else if (i13 == 3) {
            sb.append(" tball");
        } else if (i13 == 4) {
            sb.append(" wheel");
        } else {
            sb.append(" nav=");
            sb.append(this.navigation);
        }
        int i14 = this.navigationHidden;
        if (i14 == 0) {
            sb.append("/?");
        } else if (i14 == 1) {
            sb.append("/v");
        } else if (i14 == 2) {
            sb.append("/h");
        } else {
            sb.append("/");
            sb.append(this.navigationHidden);
        }
        if (this.seq != 0) {
            sb.append(" s.");
            sb.append(this.seq);
        }
        sb.append('}');
        return sb.toString();
    }

    public void setToDefaults() {
        this.fontScale = Float.parseFloat(SystemProperties.get("ro.font.scale", "1.0"));
        this.mnc = 0;
        this.mcc = 0;
        this.locale = null;
        this.userSetLocale = false;
        this.touchscreen = 0;
        this.keyboard = 0;
        this.keyboardHidden = 0;
        this.hardKeyboardHidden = 0;
        this.navigation = 0;
        this.navigationHidden = 0;
        this.orientation = 0;
        this.screenLayout = 0;
        this.uiMode = 0;
        this.compatScreenWidthDp = 0;
        this.screenWidthDp = 0;
        this.compatScreenHeightDp = 0;
        this.screenHeightDp = 0;
        this.compatSmallestScreenWidthDp = 0;
        this.smallestScreenWidthDp = 0;
        this.densityDpi = 0;
        this.seq = 0;
    }

    @Deprecated
    public void makeDefault() {
        setToDefaults();
    }

    public int updateFrom(Configuration configuration) {
        int i;
        int i2;
        Locale locale;
        float f = configuration.fontScale;
        if (f <= 0.0f || this.fontScale == f) {
            i = 0;
        } else {
            i = 1073741824;
            this.fontScale = f;
        }
        int i3 = configuration.mcc;
        if (i3 != 0 && this.mcc != i3) {
            i |= 1;
            this.mcc = i3;
        }
        int i4 = configuration.mnc;
        if (i4 != 0 && this.mnc != i4) {
            i |= 2;
            this.mnc = i4;
        }
        Locale locale2 = configuration.locale;
        if (locale2 != null && ((locale = this.locale) == null || !locale.equals(locale2))) {
            int i5 = i | 4;
            Locale locale3 = configuration.locale;
            Locale locale4 = locale3 != null ? (Locale) locale3.clone() : null;
            this.locale = locale4;
            setLayoutDirection(locale4);
            i = i5 | 8192;
        }
        int i6 = configuration.screenLayout & 192;
        if (i6 != 0) {
            int i7 = this.screenLayout;
            if (i6 != (i7 & 192)) {
                this.screenLayout = i6 | (i7 & (-193));
                i |= 8192;
            }
        }
        if (configuration.userSetLocale && (!this.userSetLocale || (i & 4) != 0)) {
            i |= 4;
            this.userSetLocale = true;
        }
        int i8 = configuration.touchscreen;
        if (i8 != 0 && this.touchscreen != i8) {
            i |= 8;
            this.touchscreen = i8;
        }
        int i9 = configuration.keyboard;
        if (i9 != 0 && this.keyboard != i9) {
            i |= 16;
            this.keyboard = i9;
        }
        int i10 = configuration.keyboardHidden;
        if (i10 != 0 && this.keyboardHidden != i10) {
            i |= 32;
            this.keyboardHidden = i10;
        }
        int i11 = configuration.hardKeyboardHidden;
        if (i11 != 0 && this.hardKeyboardHidden != i11) {
            i |= 32;
            this.hardKeyboardHidden = i11;
        }
        int i12 = configuration.navigation;
        if (i12 != 0 && this.navigation != i12) {
            i |= 64;
            this.navigation = i12;
        }
        int i13 = configuration.navigationHidden;
        if (i13 != 0 && this.navigationHidden != i13) {
            i |= 32;
            this.navigationHidden = i13;
        }
        int i14 = configuration.orientation;
        if (i14 != 0 && this.orientation != i14) {
            i |= 128;
            this.orientation = i14;
        }
        if (getScreenLayoutNoDirection(configuration.screenLayout) != 0 && getScreenLayoutNoDirection(this.screenLayout) != getScreenLayoutNoDirection(configuration.screenLayout)) {
            i |= 256;
            int i15 = configuration.screenLayout;
            if ((i15 & 192) == 0) {
                this.screenLayout = i15 | (this.screenLayout & 192);
            } else {
                this.screenLayout = i15;
            }
        }
        int i16 = configuration.uiMode;
        if (i16 != 0 && (i2 = this.uiMode) != i16) {
            i |= 512;
            if ((i16 & 15) != 0) {
                this.uiMode = (i16 & 15) | (i2 & (-16));
            }
            int i17 = configuration.uiMode;
            if ((i17 & 48) != 0) {
                this.uiMode = (i17 & 48) | (this.uiMode & (-49));
            }
        }
        int i18 = configuration.screenWidthDp;
        if (i18 != 0 && this.screenWidthDp != i18) {
            i |= 1024;
            this.screenWidthDp = i18;
        }
        int i19 = configuration.screenHeightDp;
        if (i19 != 0 && this.screenHeightDp != i19) {
            i |= 1024;
            this.screenHeightDp = i19;
        }
        int i20 = configuration.smallestScreenWidthDp;
        if (i20 != 0 && this.smallestScreenWidthDp != i20) {
            i |= 2048;
            this.smallestScreenWidthDp = i20;
        }
        int i21 = configuration.densityDpi;
        if (i21 != 0 && this.densityDpi != i21) {
            i |= 4096;
            this.densityDpi = i21;
        }
        int i22 = configuration.compatScreenWidthDp;
        if (i22 != 0) {
            this.compatScreenWidthDp = i22;
        }
        int i23 = configuration.compatScreenHeightDp;
        if (i23 != 0) {
            this.compatScreenHeightDp = i23;
        }
        int i24 = configuration.compatSmallestScreenWidthDp;
        if (i24 != 0) {
            this.compatSmallestScreenWidthDp = i24;
        }
        int i25 = configuration.seq;
        if (i25 != 0) {
            this.seq = i25;
        }
        return i;
    }

    public int diff(Configuration configuration) {
        Locale locale;
        float f = configuration.fontScale;
        int i = (f <= 0.0f || this.fontScale == f) ? 0 : 1073741824;
        int i2 = configuration.mcc;
        if (i2 != 0 && this.mcc != i2) {
            i |= 1;
        }
        int i3 = configuration.mnc;
        if (i3 != 0 && this.mnc != i3) {
            i |= 2;
        }
        Locale locale2 = configuration.locale;
        if (locale2 != null && ((locale = this.locale) == null || !locale.equals(locale2))) {
            i = i | 4 | 8192;
        }
        int i4 = configuration.screenLayout;
        int i5 = i4 & 192;
        if (i5 != 0 && i5 != (this.screenLayout & 192)) {
            i |= 8192;
        }
        int i6 = configuration.touchscreen;
        if (i6 != 0 && this.touchscreen != i6) {
            i |= 8;
        }
        int i7 = configuration.keyboard;
        if (i7 != 0 && this.keyboard != i7) {
            i |= 16;
        }
        int i8 = configuration.keyboardHidden;
        if (i8 != 0 && this.keyboardHidden != i8) {
            i |= 32;
        }
        int i9 = configuration.hardKeyboardHidden;
        if (i9 != 0 && this.hardKeyboardHidden != i9) {
            i |= 32;
        }
        int i10 = configuration.navigation;
        if (i10 != 0 && this.navigation != i10) {
            i |= 64;
        }
        int i11 = configuration.navigationHidden;
        if (i11 != 0 && this.navigationHidden != i11) {
            i |= 32;
        }
        int i12 = configuration.orientation;
        if (i12 != 0 && this.orientation != i12) {
            i |= 128;
        }
        if (getScreenLayoutNoDirection(i4) != 0 && getScreenLayoutNoDirection(this.screenLayout) != getScreenLayoutNoDirection(configuration.screenLayout)) {
            i |= 256;
        }
        int i13 = configuration.uiMode;
        if (i13 != 0 && this.uiMode != i13) {
            i |= 512;
        }
        int i14 = configuration.screenWidthDp;
        if (i14 != 0 && this.screenWidthDp != i14) {
            i |= 1024;
        }
        int i15 = configuration.screenHeightDp;
        if (i15 != 0 && this.screenHeightDp != i15) {
            i |= 1024;
        }
        int i16 = configuration.smallestScreenWidthDp;
        if (i16 != 0 && this.smallestScreenWidthDp != i16) {
            i |= 2048;
        }
        int i17 = configuration.densityDpi;
        return (i17 == 0 || this.densityDpi == i17) ? i : i | 4096;
    }

    public boolean isOtherSeqNewer(Configuration configuration) {
        int i;
        if (configuration == null) {
            return false;
        }
        int i2 = configuration.seq;
        if (i2 == 0 || (i = this.seq) == 0) {
            return true;
        }
        int i3 = i2 - i;
        return i3 <= 65536 && i3 > 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(this.fontScale);
        parcel.writeInt(this.mcc);
        parcel.writeInt(this.mnc);
        if (this.locale == null) {
            parcel.writeInt(0);
        } else {
            parcel.writeInt(1);
            parcel.writeString(this.locale.getLanguage());
            parcel.writeString(this.locale.getCountry());
            parcel.writeString(this.locale.getVariant());
        }
        if (this.userSetLocale) {
            parcel.writeInt(1);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.touchscreen);
        parcel.writeInt(this.keyboard);
        parcel.writeInt(this.keyboardHidden);
        parcel.writeInt(this.hardKeyboardHidden);
        parcel.writeInt(this.navigation);
        parcel.writeInt(this.navigationHidden);
        parcel.writeInt(this.orientation);
        parcel.writeInt(this.screenLayout);
        parcel.writeInt(this.uiMode);
        parcel.writeInt(this.screenWidthDp);
        parcel.writeInt(this.screenHeightDp);
        parcel.writeInt(this.smallestScreenWidthDp);
        parcel.writeInt(this.densityDpi);
        parcel.writeInt(this.compatScreenWidthDp);
        parcel.writeInt(this.compatScreenHeightDp);
        parcel.writeInt(this.compatSmallestScreenWidthDp);
        parcel.writeInt(this.seq);
    }

    public void readFromParcel(Parcel parcel) {
        this.fontScale = parcel.readFloat();
        this.mcc = parcel.readInt();
        this.mnc = parcel.readInt();
        if (parcel.readInt() != 0) {
            this.locale = new Locale(parcel.readString(), parcel.readString(), parcel.readString());
        }
        this.userSetLocale = parcel.readInt() == 1;
        this.touchscreen = parcel.readInt();
        this.keyboard = parcel.readInt();
        this.keyboardHidden = parcel.readInt();
        this.hardKeyboardHidden = parcel.readInt();
        this.navigation = parcel.readInt();
        this.navigationHidden = parcel.readInt();
        this.orientation = parcel.readInt();
        this.screenLayout = parcel.readInt();
        this.uiMode = parcel.readInt();
        this.screenWidthDp = parcel.readInt();
        this.screenHeightDp = parcel.readInt();
        this.smallestScreenWidthDp = parcel.readInt();
        this.densityDpi = parcel.readInt();
        this.compatScreenWidthDp = parcel.readInt();
        this.compatScreenHeightDp = parcel.readInt();
        this.compatSmallestScreenWidthDp = parcel.readInt();
        this.seq = parcel.readInt();
    }

    private Configuration(Parcel parcel) {
        readFromParcel(parcel);
    }

    @Override // java.lang.Comparable
    public int compareTo(Configuration configuration) {
        float f = this.fontScale;
        float f2 = configuration.fontScale;
        if (f < f2) {
            return -1;
        }
        if (f > f2) {
            return 1;
        }
        int i = this.mcc - configuration.mcc;
        if (i != 0) {
            return i;
        }
        int i2 = this.mnc - configuration.mnc;
        if (i2 != 0) {
            return i2;
        }
        Locale locale = this.locale;
        if (locale == null) {
            if (configuration.locale != null) {
                return 1;
            }
        } else {
            if (configuration.locale == null) {
                return -1;
            }
            int iCompareTo = locale.getLanguage().compareTo(configuration.locale.getLanguage());
            if (iCompareTo != 0) {
                return iCompareTo;
            }
            int iCompareTo2 = this.locale.getCountry().compareTo(configuration.locale.getCountry());
            if (iCompareTo2 != 0) {
                return iCompareTo2;
            }
            int iCompareTo3 = this.locale.getVariant().compareTo(configuration.locale.getVariant());
            if (iCompareTo3 != 0) {
                return iCompareTo3;
            }
        }
        int i3 = this.touchscreen - configuration.touchscreen;
        if (i3 != 0) {
            return i3;
        }
        int i4 = this.keyboard - configuration.keyboard;
        if (i4 != 0) {
            return i4;
        }
        int i5 = this.keyboardHidden - configuration.keyboardHidden;
        if (i5 != 0) {
            return i5;
        }
        int i6 = this.hardKeyboardHidden - configuration.hardKeyboardHidden;
        if (i6 != 0) {
            return i6;
        }
        int i7 = this.navigation - configuration.navigation;
        if (i7 != 0) {
            return i7;
        }
        int i8 = this.navigationHidden - configuration.navigationHidden;
        if (i8 != 0) {
            return i8;
        }
        int i9 = this.orientation - configuration.orientation;
        if (i9 != 0) {
            return i9;
        }
        int i10 = this.screenLayout - configuration.screenLayout;
        if (i10 != 0) {
            return i10;
        }
        int i11 = this.uiMode - configuration.uiMode;
        if (i11 != 0) {
            return i11;
        }
        int i12 = this.screenWidthDp - configuration.screenWidthDp;
        if (i12 != 0) {
            return i12;
        }
        int i13 = this.screenHeightDp - configuration.screenHeightDp;
        if (i13 != 0) {
            return i13;
        }
        int i14 = this.smallestScreenWidthDp - configuration.smallestScreenWidthDp;
        return i14 != 0 ? i14 : this.densityDpi - configuration.densityDpi;
    }

    public boolean equals(Configuration configuration) {
        if (configuration == null) {
            return false;
        }
        return configuration == this || compareTo(configuration) == 0;
    }

    public boolean equals(Object obj) {
        try {
            return equals((Configuration) obj);
        } catch (ClassCastException unused) {
            return false;
        }
    }

    public int hashCode() {
        int iFloatToIntBits = (((((527 + Float.floatToIntBits(this.fontScale)) * 31) + this.mcc) * 31) + this.mnc) * 31;
        Locale locale = this.locale;
        return ((((((((((((((((((((((((((iFloatToIntBits + (locale != null ? locale.hashCode() : 0)) * 31) + this.touchscreen) * 31) + this.keyboard) * 31) + this.keyboardHidden) * 31) + this.hardKeyboardHidden) * 31) + this.navigation) * 31) + this.navigationHidden) * 31) + this.orientation) * 31) + this.screenLayout) * 31) + this.uiMode) * 31) + this.screenWidthDp) * 31) + this.screenHeightDp) * 31) + this.smallestScreenWidthDp) * 31) + this.densityDpi;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
        this.userSetLocale = true;
        setLayoutDirection(locale);
    }

    public int getLayoutDirection() {
        return (this.screenLayout & 192) == 128 ? 1 : 0;
    }

    public void setLayoutDirection(Locale locale) {
        this.screenLayout = ((TextUtils.getLayoutDirectionFromLocale(locale) + 1) << 6) | (this.screenLayout & (-193));
    }
}
