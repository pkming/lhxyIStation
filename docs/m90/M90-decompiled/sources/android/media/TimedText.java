package android.media;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Parcel;
import android.util.Log;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/* JADX INFO: loaded from: classes.dex */
public final class TimedText {
    private static final int FIRST_PRIVATE_KEY = 101;
    private static final int FIRST_PUBLIC_KEY = 1;
    private static final int KEY_BACKGROUND_COLOR_RGBA = 3;
    private static final int KEY_DISPLAY_FLAGS = 1;
    private static final int KEY_END_CHAR = 104;
    private static final int KEY_FONT_ID = 105;
    private static final int KEY_FONT_SIZE = 106;
    private static final int KEY_GLOBAL_SETTING = 101;
    private static final int KEY_HIGHLIGHT_COLOR_RGBA = 4;
    private static final int KEY_LOCAL_SETTING = 102;
    private static final int KEY_SCROLL_DELAY = 5;
    private static final int KEY_START_CHAR = 103;
    private static final int KEY_START_TIME = 7;
    private static final int KEY_STRUCT_AWEXTEND_BMP = 50;
    private static final int KEY_STRUCT_AWEXTEND_HIDESUB = 56;
    private static final int KEY_STRUCT_AWEXTEND_PICHEIGHT = 53;
    private static final int KEY_STRUCT_AWEXTEND_PICWIDTH = 52;
    private static final int KEY_STRUCT_AWEXTEND_PIXEL_FORMAT = 51;
    private static final int KEY_STRUCT_AWEXTEND_REFERENCE_VIDEO_HEIGHT = 58;
    private static final int KEY_STRUCT_AWEXTEND_REFERENCE_VIDEO_WIDTH = 57;
    private static final int KEY_STRUCT_AWEXTEND_SCREENRECT = 55;
    private static final int KEY_STRUCT_AWEXTEND_SUBDISPPOS = 54;
    private static final int KEY_STRUCT_BLINKING_TEXT_LIST = 8;
    private static final int KEY_STRUCT_FONT_LIST = 9;
    private static final int KEY_STRUCT_HIGHLIGHT_LIST = 10;
    private static final int KEY_STRUCT_HYPER_TEXT_LIST = 11;
    private static final int KEY_STRUCT_JUSTIFICATION = 15;
    private static final int KEY_STRUCT_KARAOKE_LIST = 12;
    private static final int KEY_STRUCT_STYLE_LIST = 13;
    private static final int KEY_STRUCT_TEXT = 16;
    private static final int KEY_STRUCT_TEXT_POS = 14;
    private static final int KEY_STYLE_FLAGS = 2;
    private static final int KEY_SUBTITLE_ID = 17;
    private static final int KEY_TEXT_COLOR_RGBA = 107;
    private static final int KEY_WRAP_TEXT = 6;
    private static final int LAST_PRIVATE_KEY = 107;
    private static final int LAST_PUBLIC_KEY = 56;
    public static final int SUB_DISPPOS_BOT_LEFT = 49;
    public static final int SUB_DISPPOS_BOT_MID = 50;
    public static final int SUB_DISPPOS_BOT_RIGHT = 51;
    public static final int SUB_DISPPOS_DEFAULT = 0;
    public static final int SUB_DISPPOS_MID_LEFT = 33;
    public static final int SUB_DISPPOS_MID_MID = 34;
    public static final int SUB_DISPPOS_MID_RIGHT = 35;
    public static final int SUB_DISPPOS_TOP_LEFT = 17;
    public static final int SUB_DISPPOS_TOP_MID = 18;
    public static final int SUB_DISPPOS_TOP_RIGHT = 19;
    public static final int SUB_RENDER_ALIGN_NONE = 0;
    public static final int SUB_RENDER_HALIGN_CENTER = 2;
    public static final int SUB_RENDER_HALIGN_LEFT = 1;
    public static final int SUB_RENDER_HALIGN_RIGHT = 3;
    public static final int SUB_RENDER_VALIGN_BOTTOM = 48;
    public static final int SUB_RENDER_VALIGN_CENTER = 32;
    public static final int SUB_RENDER_VALIGN_TOP = 16;
    public static final int SUN_RENDER_HALIGN_MASK = 15;
    public static final int SUN_RENDER_VALIGN_MASK = 240;
    private static final String TAG = "TimedText";
    private Bitmap mAWExtendBitmap;
    private int mAWExtendBitmapSubtitleFlag;
    private int mAWExtendHideSubFlag;
    private int mAWExtendReferenceVideoHeight;
    private int mAWExtendReferenceVideoWidth;
    private int mAWExtendSubDispPos;
    private int mAWExtendSubtitleID;
    private Rect mAWExtendTextScreenBounds;
    private int mBackgroundColorRGBA;
    private List<CharPos> mBlinkingPosList;
    private int mDisplayFlags;
    private List<Font> mFontList;
    private int mHighlightColorRGBA;
    private List<CharPos> mHighlightPosList;
    private List<HyperText> mHyperTextList;
    private Justification mJustification;
    private List<Karaoke> mKaraokeList;
    private final HashMap<Integer, Object> mKeyObjectMap;
    private int mScrollDelay;
    private List<Style> mStyleList;
    private Rect mTextBounds;
    private String mTextChars;
    private int mWrapText;

    private boolean isValidKey(int i) {
        return (i >= 1 && i <= 56) || (i >= 101 && i <= 107);
    }

    public static final class CharPos {
        public final int endChar;
        public final int startChar;

        public CharPos(int i, int i2) {
            this.startChar = i;
            this.endChar = i2;
        }
    }

    public static final class Justification {
        public final int horizontalJustification;
        public final int verticalJustification;

        public Justification(int i, int i2) {
            this.horizontalJustification = i;
            this.verticalJustification = i2;
        }
    }

    public static final class Style {
        public final int colorRGBA;
        public final int endChar;
        public final int fontID;
        public final int fontSize;
        public final boolean isBold;
        public final boolean isItalic;
        public final boolean isUnderlined;
        public final int startChar;

        public Style(int i, int i2, int i3, boolean z, boolean z2, boolean z3, int i4, int i5) {
            this.startChar = i;
            this.endChar = i2;
            this.fontID = i3;
            this.isBold = z;
            this.isItalic = z2;
            this.isUnderlined = z3;
            this.fontSize = i4;
            this.colorRGBA = i5;
        }
    }

    public static final class Font {
        public final int ID;
        public final String name;

        public Font(int i, String str) {
            this.ID = i;
            this.name = str;
        }
    }

    public static final class Karaoke {
        public final int endChar;
        public final int endTimeMs;
        public final int startChar;
        public final int startTimeMs;

        public Karaoke(int i, int i2, int i3, int i4) {
            this.startTimeMs = i;
            this.endTimeMs = i2;
            this.startChar = i3;
            this.endChar = i4;
        }
    }

    public static final class HyperText {
        public final String URL;
        public final String altString;
        public final int endChar;
        public final int startChar;

        public HyperText(int i, int i2, String str, String str2) {
            this.startChar = i;
            this.endChar = i2;
            this.URL = str;
            this.altString = str2;
        }
    }

    public TimedText(Parcel parcel) {
        HashMap<Integer, Object> map = new HashMap<>();
        this.mKeyObjectMap = map;
        this.mDisplayFlags = -1;
        this.mBackgroundColorRGBA = -1;
        this.mHighlightColorRGBA = -1;
        this.mScrollDelay = -1;
        this.mWrapText = -1;
        this.mBlinkingPosList = null;
        this.mHighlightPosList = null;
        this.mKaraokeList = null;
        this.mFontList = null;
        this.mStyleList = null;
        this.mHyperTextList = null;
        this.mTextBounds = null;
        this.mTextChars = null;
        this.mAWExtendBitmap = null;
        this.mAWExtendBitmapSubtitleFlag = 0;
        this.mAWExtendHideSubFlag = 0;
        this.mAWExtendSubDispPos = 0;
        this.mAWExtendTextScreenBounds = null;
        this.mAWExtendReferenceVideoWidth = 0;
        this.mAWExtendReferenceVideoHeight = 0;
        this.mAWExtendSubtitleID = 0;
        if (parseParcel(parcel)) {
            return;
        }
        map.clear();
        throw new IllegalArgumentException("parseParcel() fails");
    }

    public String getText() {
        return this.mTextChars;
    }

    public Rect getBounds() {
        return this.mTextBounds;
    }

    public Bitmap AWExtend_getBitmap() {
        return this.mAWExtendBitmap;
    }

    public int AWExtend_getBitmapSubtitleFlag() {
        return this.mAWExtendBitmapSubtitleFlag;
    }

    public int AWExtend_getHideSubFlag() {
        return this.mAWExtendHideSubFlag;
    }

    public int AWExtend_getSubDispPos() {
        return this.mAWExtendSubDispPos;
    }

    public Rect AWExtend_getTextScreenBounds() {
        return this.mAWExtendTextScreenBounds;
    }

    public List<Style> AWExtend_getStyleList() {
        return this.mStyleList;
    }

    public int AWExtend_getSubtitleID() {
        return this.mAWExtendSubtitleID;
    }

    public int AWExtend_getReferenceVideoWidth() {
        return this.mAWExtendReferenceVideoWidth;
    }

    public int AWExtend_getReferenceVideoHeight() {
        return this.mAWExtendReferenceVideoHeight;
    }

    /* JADX WARN: Type inference incomplete: some casts might be missing */
    private boolean parseParcel(Parcel parcel) {
        Object objValueOf;
        parcel.setDataPosition(0);
        if (parcel.dataAvail() == 0) {
            return false;
        }
        Log.d(TAG, "----------------parseParcel---------------");
        int i = parcel.readInt();
        if (i == 102) {
            int i2 = parcel.readInt();
            if (i2 != 7) {
                return false;
            }
            this.mKeyObjectMap.put(Integer.valueOf(i2), Integer.valueOf(parcel.readInt()));
            int i3 = parcel.readInt();
            if (i3 == 16) {
                parcel.readInt();
                byte[] bArrCreateByteArray = parcel.createByteArray();
                if (bArrCreateByteArray == null || bArrCreateByteArray.length == 0) {
                    this.mTextChars = null;
                } else {
                    this.mTextChars = new String(bArrCreateByteArray);
                }
                this.mAWExtendBitmapSubtitleFlag = 0;
            } else {
                if (i3 != 50) {
                    Log.w(TAG, "java_parseParcel, find timedtext type=" + i3 + ", so return false");
                    return false;
                }
                if (parcel.readInt() != 51) {
                    Log.w(TAG, "java_parseParcel, aw_extend, fail_1!");
                }
                parcel.readInt();
                if (parcel.readInt() != 52) {
                    Log.w(TAG, "java_parseParcel, aw_extend, fail_2!");
                }
                int i4 = parcel.readInt();
                if (parcel.readInt() != 53) {
                    Log.w(TAG, "java_parseParcel, aw_extend, fail_3!");
                }
                int i5 = parcel.readInt();
                if (parcel.readInt() != 57) {
                    Log.w(TAG, "java_parseParcel, aw_extend, fail_4!");
                }
                this.mAWExtendReferenceVideoWidth = parcel.readInt();
                if (parcel.readInt() != 58) {
                    Log.w(TAG, "java_parseParcel, aw_extend, fail_5!");
                }
                this.mAWExtendReferenceVideoHeight = parcel.readInt();
                parcel.readInt();
                int[] iArrCreateIntArray = parcel.createIntArray();
                if (iArrCreateIntArray == null || iArrCreateIntArray.length == 0) {
                    Log.w(TAG, "java_parseParcel, aw_extend, fail_4!");
                    this.mAWExtendBitmap = null;
                } else {
                    this.mAWExtendBitmap = Bitmap.createBitmap(iArrCreateIntArray, i4, i5, Bitmap.Config.ARGB_8888);
                }
                this.mAWExtendBitmapSubtitleFlag = 1;
            }
        } else if (i != 101) {
            Log.w(TAG, "Invalid timed text key found: " + i);
            return false;
        }
        while (parcel.dataAvail() > 0) {
            int i6 = parcel.readInt();
            if (!isValidKey(i6)) {
                Log.w(TAG, "Invalid timed text key found: " + i6);
                return false;
            }
            if (i6 != 1) {
                if (i6 == 17) {
                    this.mAWExtendSubtitleID = parcel.readInt();
                    Log.d(TAG, "nSubtitleID = " + this.mAWExtendSubtitleID);
                } else if (i6 == 3) {
                    int i7 = parcel.readInt();
                    this.mBackgroundColorRGBA = i7;
                    objValueOf = Integer.valueOf(i7);
                } else if (i6 == 4) {
                    int i8 = parcel.readInt();
                    this.mHighlightColorRGBA = i8;
                    objValueOf = Integer.valueOf(i8);
                } else if (i6 == 5) {
                    int i9 = parcel.readInt();
                    this.mScrollDelay = i9;
                    objValueOf = Integer.valueOf(i9);
                } else if (i6 != 6) {
                    switch (i6) {
                        case 8:
                            readBlinkingText(parcel);
                            objValueOf = this.mBlinkingPosList;
                            break;
                        case 9:
                            readFont(parcel);
                            objValueOf = this.mFontList;
                            break;
                        case 10:
                            readHighlight(parcel);
                            objValueOf = this.mHighlightPosList;
                            break;
                        case 11:
                            readHyperText(parcel);
                            objValueOf = this.mHyperTextList;
                            break;
                        case 12:
                            readKaraoke(parcel);
                            objValueOf = this.mKaraokeList;
                            break;
                        case 13:
                            readStyle(parcel);
                            objValueOf = this.mStyleList;
                            break;
                        case 14:
                            this.mTextBounds = new Rect(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt());
                            break;
                        case 15:
                            Justification justification = new Justification(parcel.readInt(), parcel.readInt());
                            this.mJustification = justification;
                            objValueOf = justification;
                            break;
                        default:
                            switch (i6) {
                                case 54:
                                    this.mAWExtendSubDispPos = parcel.readInt();
                                    break;
                                case 55:
                                    this.mAWExtendTextScreenBounds = new Rect(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt());
                                    break;
                                case 56:
                                    this.mAWExtendHideSubFlag = parcel.readInt();
                                    break;
                            }
                            break;
                    }
                } else {
                    int i10 = parcel.readInt();
                    this.mWrapText = i10;
                    objValueOf = Integer.valueOf(i10);
                }
                objValueOf = null;
            } else {
                int i11 = parcel.readInt();
                this.mDisplayFlags = i11;
                objValueOf = Integer.valueOf(i11);
            }
            if (objValueOf != null) {
                if (this.mKeyObjectMap.containsKey(Integer.valueOf(i6))) {
                    this.mKeyObjectMap.remove(Integer.valueOf(i6));
                }
                this.mKeyObjectMap.put(Integer.valueOf(i6), objValueOf);
            }
        }
        return true;
    }

    private void readStyle(Parcel parcel) {
        int i = -1;
        int i2 = -1;
        int i3 = -1;
        int i4 = -1;
        int i5 = -1;
        boolean z = false;
        boolean z2 = false;
        boolean z3 = false;
        boolean z4 = false;
        while (!z && parcel.dataAvail() > 0) {
            int i6 = parcel.readInt();
            if (i6 != 2) {
                switch (i6) {
                    case 103:
                        i = parcel.readInt();
                        break;
                    case 104:
                        i2 = parcel.readInt();
                        break;
                    case 105:
                        i3 = parcel.readInt();
                        break;
                    case 106:
                        i4 = parcel.readInt();
                        break;
                    case 107:
                        i5 = parcel.readInt();
                        break;
                    default:
                        parcel.setDataPosition(parcel.dataPosition() - 4);
                        z = true;
                        break;
                }
            } else {
                int i7 = parcel.readInt();
                z2 = i7 % 2 == 1;
                z3 = i7 % 4 >= 2;
                z4 = i7 / 4 == 1;
            }
        }
        Style style = new Style(i, i2, i3, z2, z3, z4, i4, i5);
        if (this.mStyleList == null) {
            this.mStyleList = new ArrayList();
        }
        this.mStyleList.add(style);
    }

    private void readFont(Parcel parcel) {
        int i = parcel.readInt();
        for (int i2 = 0; i2 < i; i2++) {
            Font font = new Font(parcel.readInt(), new String(parcel.createByteArray(), 0, parcel.readInt()));
            if (this.mFontList == null) {
                this.mFontList = new ArrayList();
            }
            this.mFontList.add(font);
        }
    }

    private void readHighlight(Parcel parcel) {
        CharPos charPos = new CharPos(parcel.readInt(), parcel.readInt());
        if (this.mHighlightPosList == null) {
            this.mHighlightPosList = new ArrayList();
        }
        this.mHighlightPosList.add(charPos);
    }

    private void readKaraoke(Parcel parcel) {
        int i = parcel.readInt();
        for (int i2 = 0; i2 < i; i2++) {
            Karaoke karaoke = new Karaoke(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt());
            if (this.mKaraokeList == null) {
                this.mKaraokeList = new ArrayList();
            }
            this.mKaraokeList.add(karaoke);
        }
    }

    private void readHyperText(Parcel parcel) {
        HyperText hyperText = new HyperText(parcel.readInt(), parcel.readInt(), new String(parcel.createByteArray(), 0, parcel.readInt()), new String(parcel.createByteArray(), 0, parcel.readInt()));
        if (this.mHyperTextList == null) {
            this.mHyperTextList = new ArrayList();
        }
        this.mHyperTextList.add(hyperText);
    }

    private void readBlinkingText(Parcel parcel) {
        CharPos charPos = new CharPos(parcel.readInt(), parcel.readInt());
        if (this.mBlinkingPosList == null) {
            this.mBlinkingPosList = new ArrayList();
        }
        this.mBlinkingPosList.add(charPos);
    }

    private boolean containsKey(int i) {
        return isValidKey(i) && this.mKeyObjectMap.containsKey(Integer.valueOf(i));
    }

    private Set keySet() {
        return this.mKeyObjectMap.keySet();
    }

    private Object getObject(int i) {
        if (containsKey(i)) {
            return this.mKeyObjectMap.get(Integer.valueOf(i));
        }
        throw new IllegalArgumentException("Invalid key: " + i);
    }
}
