package android.print;

import android.bluetooth.BluetoothHealth;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Camera;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.amap.api.services.core.AMapException;
import com.unisound.client.SpeechConstants;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public final class PrintAttributes implements Parcelable {
    public static final int COLOR_MODE_COLOR = 2;
    public static final int COLOR_MODE_MONOCHROME = 1;
    public static final Parcelable.Creator<PrintAttributes> CREATOR = new Parcelable.Creator<PrintAttributes>() { // from class: android.print.PrintAttributes.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrintAttributes createFromParcel(Parcel parcel) {
            return new PrintAttributes(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrintAttributes[] newArray(int i) {
            return new PrintAttributes[i];
        }
    };
    private static final int VALID_COLOR_MODES = 3;
    private int mColorMode;
    private MediaSize mMediaSize;
    private Margins mMinMargins;
    private Resolution mResolution;

    static String colorModeToString(int i) {
        return i != 1 ? i != 2 ? "COLOR_MODE_UNKNOWN" : "COLOR_MODE_COLOR" : "COLOR_MODE_MONOCHROME";
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    PrintAttributes() {
    }

    private PrintAttributes(Parcel parcel) {
        this.mMediaSize = parcel.readInt() == 1 ? MediaSize.createFromParcel(parcel) : null;
        this.mResolution = parcel.readInt() == 1 ? Resolution.createFromParcel(parcel) : null;
        this.mMinMargins = parcel.readInt() == 1 ? Margins.createFromParcel(parcel) : null;
        this.mColorMode = parcel.readInt();
    }

    public MediaSize getMediaSize() {
        return this.mMediaSize;
    }

    public void setMediaSize(MediaSize mediaSize) {
        this.mMediaSize = mediaSize;
    }

    public Resolution getResolution() {
        return this.mResolution;
    }

    public void setResolution(Resolution resolution) {
        this.mResolution = resolution;
    }

    public Margins getMinMargins() {
        return this.mMinMargins;
    }

    public void setMinMargins(Margins margins) {
        this.mMinMargins = margins;
    }

    public int getColorMode() {
        return this.mColorMode;
    }

    public void setColorMode(int i) {
        enforceValidColorMode(i);
        this.mColorMode = i;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        if (this.mMediaSize != null) {
            parcel.writeInt(1);
            this.mMediaSize.writeToParcel(parcel);
        } else {
            parcel.writeInt(0);
        }
        if (this.mResolution != null) {
            parcel.writeInt(1);
            this.mResolution.writeToParcel(parcel);
        } else {
            parcel.writeInt(0);
        }
        if (this.mMinMargins != null) {
            parcel.writeInt(1);
            this.mMinMargins.writeToParcel(parcel);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.mColorMode);
    }

    public int hashCode() {
        int i = (this.mColorMode + 31) * 31;
        Margins margins = this.mMinMargins;
        int iHashCode = (i + (margins == null ? 0 : margins.hashCode())) * 31;
        MediaSize mediaSize = this.mMediaSize;
        int iHashCode2 = (iHashCode + (mediaSize == null ? 0 : mediaSize.hashCode())) * 31;
        Resolution resolution = this.mResolution;
        return iHashCode2 + (resolution != null ? resolution.hashCode() : 0);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PrintAttributes printAttributes = (PrintAttributes) obj;
        if (this.mColorMode != printAttributes.mColorMode) {
            return false;
        }
        Margins margins = this.mMinMargins;
        if (margins == null) {
            if (printAttributes.mMinMargins != null) {
                return false;
            }
        } else if (!margins.equals(printAttributes.mMinMargins)) {
            return false;
        }
        MediaSize mediaSize = this.mMediaSize;
        if (mediaSize == null) {
            if (printAttributes.mMediaSize != null) {
                return false;
            }
        } else if (!mediaSize.equals(printAttributes.mMediaSize)) {
            return false;
        }
        Resolution resolution = this.mResolution;
        if (resolution == null) {
            if (printAttributes.mResolution != null) {
                return false;
            }
        } else if (!resolution.equals(printAttributes.mResolution)) {
            return false;
        }
        return true;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PrintAttributes{");
        sb.append("mediaSize: ").append(this.mMediaSize);
        if (this.mMediaSize != null) {
            sb.append(", orientation: ").append(this.mMediaSize.isPortrait() ? Camera.Parameters.SCENE_MODE_PORTRAIT : Camera.Parameters.SCENE_MODE_LANDSCAPE);
        } else {
            sb.append(", orientation: ").append("null");
        }
        sb.append(", resolution: ").append(this.mResolution);
        sb.append(", minMargins: ").append(this.mMinMargins);
        sb.append(", colorMode: ").append(colorModeToString(this.mColorMode));
        sb.append("}");
        return sb.toString();
    }

    public void clear() {
        this.mMediaSize = null;
        this.mResolution = null;
        this.mMinMargins = null;
        this.mColorMode = 0;
    }

    public void copyFrom(PrintAttributes printAttributes) {
        this.mMediaSize = printAttributes.mMediaSize;
        this.mResolution = printAttributes.mResolution;
        this.mMinMargins = printAttributes.mMinMargins;
        this.mColorMode = printAttributes.mColorMode;
    }

    public static final class MediaSize {
        private static final String LOG_TAG = "MediaSize";
        private final int mHeightMils;
        private final String mId;
        public final String mLabel;
        public final int mLabelResId;
        public final String mPackageName;
        private final int mWidthMils;
        private static final Map<String, MediaSize> sIdToMediaSizeMap = new ArrayMap();
        public static final MediaSize UNKNOWN_PORTRAIT = new MediaSize("UNKNOWN_PORTRAIT", "android", 17040859, 1, Integer.MAX_VALUE);
        public static final MediaSize UNKNOWN_LANDSCAPE = new MediaSize("UNKNOWN_LANDSCAPE", "android", 17040860, Integer.MAX_VALUE, 1);
        public static final MediaSize ISO_A0 = new MediaSize("ISO_A0", "android", 17040778, 33110, 46810);
        public static final MediaSize ISO_A1 = new MediaSize("ISO_A1", "android", 17040779, 23390, 33110);
        public static final MediaSize ISO_A2 = new MediaSize("ISO_A2", "android", 17040780, 16540, 23390);
        public static final MediaSize ISO_A3 = new MediaSize("ISO_A3", "android", 17040781, 11690, 16540);
        public static final MediaSize ISO_A4 = new MediaSize("ISO_A4", "android", 17040782, 8270, 11690);
        public static final MediaSize ISO_A5 = new MediaSize("ISO_A5", "android", 17040783, 5830, 8270);
        public static final MediaSize ISO_A6 = new MediaSize("ISO_A6", "android", 17040784, 4130, 5830);
        public static final MediaSize ISO_A7 = new MediaSize("ISO_A7", "android", 17040785, 2910, 4130);
        public static final MediaSize ISO_A8 = new MediaSize("ISO_A8", "android", 17040786, 2050, 2910);
        public static final MediaSize ISO_A9 = new MediaSize("ISO_A9", "android", 17040787, 1460, 2050);
        public static final MediaSize ISO_A10 = new MediaSize("ISO_A10", "android", 17040788, 1020, 1460);
        public static final MediaSize ISO_B0 = new MediaSize("ISO_B0", "android", 17040789, 39370, 55670);
        public static final MediaSize ISO_B1 = new MediaSize("ISO_B1", "android", 17040790, 27830, 39370);
        public static final MediaSize ISO_B2 = new MediaSize("ISO_B2", "android", 17040791, 19690, 27830);
        public static final MediaSize ISO_B3 = new MediaSize("ISO_B3", "android", 17040792, 13900, 19690);
        public static final MediaSize ISO_B4 = new MediaSize("ISO_B4", "android", 17040793, 9840, 13900);
        public static final MediaSize ISO_B5 = new MediaSize("ISO_B5", "android", 17040794, 6930, 9840);
        public static final MediaSize ISO_B6 = new MediaSize("ISO_B6", "android", 17040795, 4920, 6930);
        public static final MediaSize ISO_B7 = new MediaSize("ISO_B7", "android", 17040796, 3460, 4920);
        public static final MediaSize ISO_B8 = new MediaSize("ISO_B8", "android", 17040797, 2440, 3460);
        public static final MediaSize ISO_B9 = new MediaSize("ISO_B9", "android", 17040798, 1730, 2440);
        public static final MediaSize ISO_B10 = new MediaSize("ISO_B10", "android", 17040799, 1220, 1730);
        public static final MediaSize ISO_C0 = new MediaSize("ISO_C0", "android", 17040800, 36100, 51060);
        public static final MediaSize ISO_C1 = new MediaSize("ISO_C1", "android", 17040801, 25510, 36100);
        public static final MediaSize ISO_C2 = new MediaSize("ISO_C2", "android", 17040802, 18030, 25510);
        public static final MediaSize ISO_C3 = new MediaSize("ISO_C3", "android", 17040803, 12760, 18030);
        public static final MediaSize ISO_C4 = new MediaSize("ISO_C4", "android", 17040804, 9020, 12760);
        public static final MediaSize ISO_C5 = new MediaSize("ISO_C5", "android", 17040805, 6380, 9020);
        public static final MediaSize ISO_C6 = new MediaSize("ISO_C6", "android", 17040806, 4490, 6380);
        public static final MediaSize ISO_C7 = new MediaSize("ISO_C7", "android", 17040807, 3190, 4490);
        public static final MediaSize ISO_C8 = new MediaSize("ISO_C8", "android", 17040808, 2240, 3190);
        public static final MediaSize ISO_C9 = new MediaSize("ISO_C9", "android", 17040809, 1570, 2240);
        public static final MediaSize ISO_C10 = new MediaSize("ISO_C10", "android", 17040810, 1100, 1570);
        public static final MediaSize NA_LETTER = new MediaSize("NA_LETTER", "android", 17040811, 8500, 11000);
        public static final MediaSize NA_GOVT_LETTER = new MediaSize("NA_GOVT_LETTER", "android", 17040812, 8000, 10500);
        public static final MediaSize NA_LEGAL = new MediaSize("NA_LEGAL", "android", 17040813, 8500, 14000);
        public static final MediaSize NA_JUNIOR_LEGAL = new MediaSize("NA_JUNIOR_LEGAL", "android", 17040814, 8000, 5000);
        public static final MediaSize NA_LEDGER = new MediaSize("NA_LEDGER", "android", 17040815, 17000, 11000);
        public static final MediaSize NA_TABLOID = new MediaSize("NA_TABLOID", "android", 17040816, 11000, 17000);
        public static final MediaSize NA_INDEX_3X5 = new MediaSize("NA_INDEX_3X5", "android", 17040817, 3000, 5000);
        public static final MediaSize NA_INDEX_4X6 = new MediaSize("NA_INDEX_4X6", "android", 17040818, AMapException.CODE_AMAP_SHARE_LICENSE_IS_EXPIRED, BluetoothHealth.HEALTH_OPERATION_SUCCESS);
        public static final MediaSize NA_INDEX_5X8 = new MediaSize("NA_INDEX_5X8", "android", 17040819, 5000, 8000);
        public static final MediaSize NA_MONARCH = new MediaSize("NA_MONARCH", "android", 17040820, 7250, 10500);
        public static final MediaSize NA_QUARTO = new MediaSize("NA_QUARTO", "android", 17040821, 8000, 10000);
        public static final MediaSize NA_FOOLSCAP = new MediaSize("NA_FOOLSCAP", "android", 17040822, 8000, 13000);
        public static final MediaSize ROC_8K = new MediaSize("ROC_8K", "android", 17040823, 10629, 15354);
        public static final MediaSize ROC_16K = new MediaSize("ROC_16K", "android", 17040824, 7677, 10629);
        public static final MediaSize PRC_1 = new MediaSize("PRC_1", "android", 17040825, SpeechConstants.VPR_REQUEST_AUDIO_SERVER, 6496);
        public static final MediaSize PRC_2 = new MediaSize("PRC_2", "android", 17040826, SpeechConstants.VPR_REQUEST_AUDIO_SERVER, 6929);
        public static final MediaSize PRC_3 = new MediaSize("PRC_3", "android", 17040827, 4921, 6929);
        public static final MediaSize PRC_4 = new MediaSize("PRC_4", "android", 17040828, 4330, 8189);
        public static final MediaSize PRC_5 = new MediaSize("PRC_5", "android", 17040829, 4330, 8661);
        public static final MediaSize PRC_6 = new MediaSize("PRC_6", "android", 17040830, 4724, 12599);
        public static final MediaSize PRC_7 = new MediaSize("PRC_7", "android", 17040831, 6299, 9055);
        public static final MediaSize PRC_8 = new MediaSize("PRC_8", "android", 17040832, 4724, 12165);
        public static final MediaSize PRC_9 = new MediaSize("PRC_9", "android", 17040833, 9016, 12756);
        public static final MediaSize PRC_10 = new MediaSize("PRC_10", "android", 17040834, 12756, 18032);
        public static final MediaSize PRC_16K = new MediaSize("PRC_16K", "android", 17040835, 5749, 8465);
        public static final MediaSize OM_PA_KAI = new MediaSize("OM_PA_KAI", "android", 17040836, 10512, 15315);
        public static final MediaSize OM_DAI_PA_KAI = new MediaSize("OM_DAI_PA_KAI", "android", 17040837, 10827, 15551);
        public static final MediaSize OM_JUURO_KU_KAI = new MediaSize("OM_JUURO_KU_KAI", "android", 17040838, 7796, 10827);
        public static final MediaSize JIS_B10 = new MediaSize("JIS_B10", "android", 17040839, 1259, 1772);
        public static final MediaSize JIS_B9 = new MediaSize("JIS_B9", "android", 17040840, 1772, 2520);
        public static final MediaSize JIS_B8 = new MediaSize("JIS_B8", "android", 17040841, 2520, 3583);
        public static final MediaSize JIS_B7 = new MediaSize("JIS_B7", "android", 17040842, 3583, 5049);
        public static final MediaSize JIS_B6 = new MediaSize("JIS_B6", "android", 17040843, 5049, 7165);
        public static final MediaSize JIS_B5 = new MediaSize("JIS_B5", "android", 17040844, 7165, 10118);
        public static final MediaSize JIS_B4 = new MediaSize("JIS_B4", "android", 17040845, 10118, 14331);
        public static final MediaSize JIS_B3 = new MediaSize("JIS_B3", "android", 17040846, 14331, 20276);
        public static final MediaSize JIS_B2 = new MediaSize("JIS_B2", "android", 17040847, 20276, 28661);
        public static final MediaSize JIS_B1 = new MediaSize("JIS_B1", "android", 17040848, 28661, 40551);
        public static final MediaSize JIS_B0 = new MediaSize("JIS_B0", "android", 17040849, 40551, 57323);
        public static final MediaSize JIS_EXEC = new MediaSize("JIS_EXEC", "android", 17040850, 8504, 12992);
        public static final MediaSize JPN_CHOU4 = new MediaSize("JPN_CHOU4", "android", 17040851, 3543, 8071);
        public static final MediaSize JPN_CHOU3 = new MediaSize("JPN_CHOU3", "android", 17040852, 4724, 9252);
        public static final MediaSize JPN_CHOU2 = new MediaSize("JPN_CHOU2", "android", 17040853, 4374, 5748);
        public static final MediaSize JPN_HAGAKI = new MediaSize("JPN_HAGAKI", "android", 17040854, 3937, 5827);
        public static final MediaSize JPN_OUFUKU = new MediaSize("JPN_OUFUKU", "android", 17040855, 5827, 7874);
        public static final MediaSize JPN_KAHU = new MediaSize("JPN_KAHU", "android", 17040856, 9449, 12681);
        public static final MediaSize JPN_KAKU2 = new MediaSize("JPN_KAKU2", "android", 17040857, 9449, 13071);
        public static final MediaSize JPN_YOU4 = new MediaSize("JPN_YOU4", "android", 17040858, 4134, 9252);

        public MediaSize(String str, String str2, int i, int i2, int i3) {
            if (TextUtils.isEmpty(str)) {
                throw new IllegalArgumentException("id cannot be empty.");
            }
            if (TextUtils.isEmpty(str2)) {
                throw new IllegalArgumentException("packageName cannot be empty.");
            }
            if (i <= 0) {
                throw new IllegalArgumentException("labelResId must be greater than zero.");
            }
            if (i2 <= 0) {
                throw new IllegalArgumentException("widthMils cannot be less than or equal to zero.");
            }
            if (i3 <= 0) {
                throw new IllegalArgumentException("heightMils cannot be less than or euqual to zero.");
            }
            this.mPackageName = str2;
            this.mId = str;
            this.mLabelResId = i;
            this.mWidthMils = i2;
            this.mHeightMils = i3;
            this.mLabel = null;
            sIdToMediaSizeMap.put(str, this);
        }

        public MediaSize(String str, String str2, int i, int i2) {
            if (TextUtils.isEmpty(str)) {
                throw new IllegalArgumentException("id cannot be empty.");
            }
            if (TextUtils.isEmpty(str2)) {
                throw new IllegalArgumentException("label cannot be empty.");
            }
            if (i <= 0) {
                throw new IllegalArgumentException("widthMils cannot be less than or equal to zero.");
            }
            if (i2 <= 0) {
                throw new IllegalArgumentException("heightMils cannot be less than or euqual to zero.");
            }
            this.mId = str;
            this.mLabel = str2;
            this.mWidthMils = i;
            this.mHeightMils = i2;
            this.mLabelResId = 0;
            this.mPackageName = null;
        }

        public MediaSize(String str, String str2, String str3, int i, int i2, int i3) {
            this.mPackageName = str3;
            this.mId = str;
            this.mLabelResId = i3;
            this.mWidthMils = i;
            this.mHeightMils = i2;
            this.mLabel = str2;
        }

        public String getId() {
            return this.mId;
        }

        public String getLabel(PackageManager packageManager) {
            if (!TextUtils.isEmpty(this.mPackageName) && this.mLabelResId > 0) {
                try {
                    return packageManager.getResourcesForApplication(this.mPackageName).getString(this.mLabelResId);
                } catch (PackageManager.NameNotFoundException unused) {
                    Log.w(LOG_TAG, "Could not load resouce" + this.mLabelResId + " from package " + this.mPackageName);
                } catch (Resources.NotFoundException unused2) {
                    Log.w(LOG_TAG, "Could not load resouce" + this.mLabelResId + " from package " + this.mPackageName);
                }
            }
            return this.mLabel;
        }

        public int getWidthMils() {
            return this.mWidthMils;
        }

        public int getHeightMils() {
            return this.mHeightMils;
        }

        public boolean isPortrait() {
            return this.mHeightMils >= this.mWidthMils;
        }

        public MediaSize asPortrait() {
            return isPortrait() ? this : new MediaSize(this.mId, this.mLabel, this.mPackageName, Math.min(this.mWidthMils, this.mHeightMils), Math.max(this.mWidthMils, this.mHeightMils), this.mLabelResId);
        }

        public MediaSize asLandscape() {
            return !isPortrait() ? this : new MediaSize(this.mId, this.mLabel, this.mPackageName, Math.max(this.mWidthMils, this.mHeightMils), Math.min(this.mWidthMils, this.mHeightMils), this.mLabelResId);
        }

        void writeToParcel(Parcel parcel) {
            parcel.writeString(this.mId);
            parcel.writeString(this.mLabel);
            parcel.writeString(this.mPackageName);
            parcel.writeInt(this.mWidthMils);
            parcel.writeInt(this.mHeightMils);
            parcel.writeInt(this.mLabelResId);
        }

        static MediaSize createFromParcel(Parcel parcel) {
            return new MediaSize(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt());
        }

        public int hashCode() {
            return ((this.mWidthMils + 31) * 31) + this.mHeightMils;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            MediaSize mediaSize = (MediaSize) obj;
            return this.mWidthMils == mediaSize.mWidthMils && this.mHeightMils == mediaSize.mHeightMils;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("MediaSize{");
            sb.append("id: ").append(this.mId);
            sb.append(", label: ").append(this.mLabel);
            sb.append(", packageName: ").append(this.mPackageName);
            sb.append(", heightMils: ").append(this.mHeightMils);
            sb.append(", widthMils: ").append(this.mWidthMils);
            sb.append(", labelResId: ").append(this.mLabelResId);
            sb.append("}");
            return sb.toString();
        }

        public static MediaSize getStandardMediaSizeById(String str) {
            return sIdToMediaSizeMap.get(str);
        }
    }

    public static final class Resolution {
        private final int mHorizontalDpi;
        private final String mId;
        private final String mLabel;
        private final int mVerticalDpi;

        public Resolution(String str, String str2, int i, int i2) {
            if (TextUtils.isEmpty(str)) {
                throw new IllegalArgumentException("id cannot be empty.");
            }
            if (TextUtils.isEmpty(str2)) {
                throw new IllegalArgumentException("label cannot be empty.");
            }
            if (i <= 0) {
                throw new IllegalArgumentException("horizontalDpi cannot be less than or equal to zero.");
            }
            if (i2 <= 0) {
                throw new IllegalArgumentException("verticalDpi cannot be less than or equal to zero.");
            }
            this.mId = str;
            this.mLabel = str2;
            this.mHorizontalDpi = i;
            this.mVerticalDpi = i2;
        }

        public String getId() {
            return this.mId;
        }

        public String getLabel() {
            return this.mLabel;
        }

        public int getHorizontalDpi() {
            return this.mHorizontalDpi;
        }

        public int getVerticalDpi() {
            return this.mVerticalDpi;
        }

        void writeToParcel(Parcel parcel) {
            parcel.writeString(this.mId);
            parcel.writeString(this.mLabel);
            parcel.writeInt(this.mHorizontalDpi);
            parcel.writeInt(this.mVerticalDpi);
        }

        static Resolution createFromParcel(Parcel parcel) {
            return new Resolution(parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readInt());
        }

        public int hashCode() {
            return ((this.mHorizontalDpi + 31) * 31) + this.mVerticalDpi;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Resolution resolution = (Resolution) obj;
            return this.mHorizontalDpi == resolution.mHorizontalDpi && this.mVerticalDpi == resolution.mVerticalDpi;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Resolution{");
            sb.append("id: ").append(this.mId);
            sb.append(", label: ").append(this.mLabel);
            sb.append(", horizontalDpi: ").append(this.mHorizontalDpi);
            sb.append(", verticalDpi: ").append(this.mVerticalDpi);
            sb.append("}");
            return sb.toString();
        }
    }

    public static final class Margins {
        public static final Margins NO_MARGINS = new Margins(0, 0, 0, 0);
        private final int mBottomMils;
        private final int mLeftMils;
        private final int mRightMils;
        private final int mTopMils;

        public Margins(int i, int i2, int i3, int i4) {
            this.mTopMils = i2;
            this.mLeftMils = i;
            this.mRightMils = i3;
            this.mBottomMils = i4;
        }

        public int getLeftMils() {
            return this.mLeftMils;
        }

        public int getTopMils() {
            return this.mTopMils;
        }

        public int getRightMils() {
            return this.mRightMils;
        }

        public int getBottomMils() {
            return this.mBottomMils;
        }

        void writeToParcel(Parcel parcel) {
            parcel.writeInt(this.mLeftMils);
            parcel.writeInt(this.mTopMils);
            parcel.writeInt(this.mRightMils);
            parcel.writeInt(this.mBottomMils);
        }

        static Margins createFromParcel(Parcel parcel) {
            return new Margins(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt());
        }

        public int hashCode() {
            return ((((((this.mBottomMils + 31) * 31) + this.mLeftMils) * 31) + this.mRightMils) * 31) + this.mTopMils;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Margins margins = (Margins) obj;
            return this.mBottomMils == margins.mBottomMils && this.mLeftMils == margins.mLeftMils && this.mRightMils == margins.mRightMils && this.mTopMils == margins.mTopMils;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Margins{");
            sb.append("leftMils: ").append(this.mLeftMils);
            sb.append(", topMils: ").append(this.mTopMils);
            sb.append(", rightMils: ").append(this.mRightMils);
            sb.append(", bottomMils: ").append(this.mBottomMils);
            sb.append("}");
            return sb.toString();
        }
    }

    static void enforceValidColorMode(int i) {
        if ((i & 3) == 0 && Integer.bitCount(i) == 1) {
            throw new IllegalArgumentException("invalid color mode: " + i);
        }
    }

    public static final class Builder {
        private final PrintAttributes mAttributes = new PrintAttributes();

        public Builder setMediaSize(MediaSize mediaSize) {
            this.mAttributes.setMediaSize(mediaSize);
            return this;
        }

        public Builder setResolution(Resolution resolution) {
            this.mAttributes.setResolution(resolution);
            return this;
        }

        public Builder setMinMargins(Margins margins) {
            this.mAttributes.setMinMargins(margins);
            return this;
        }

        public Builder setColorMode(int i) {
            if (Integer.bitCount(i) > 1) {
                throw new IllegalArgumentException("can specify at most one colorMode bit.");
            }
            this.mAttributes.setColorMode(i);
            return this;
        }

        public PrintAttributes build() {
            return this.mAttributes;
        }
    }
}
