package android.view;

import android.media.AudioSystem;
import android.media.videoeditor.MediaProperties;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.KeyCharacterMap;

/* JADX INFO: loaded from: classes.dex */
public class KeyEvent extends InputEvent implements Parcelable {
    public static final int ACTION_DOWN = 0;
    public static final int ACTION_MULTIPLE = 2;
    public static final int ACTION_UP = 1;
    public static final Parcelable.Creator<KeyEvent> CREATOR;
    static final boolean DEBUG = false;
    public static final int FLAG_CANCELED = 32;
    public static final int FLAG_CANCELED_LONG_PRESS = 256;
    public static final int FLAG_EDITOR_ACTION = 16;
    public static final int FLAG_FALLBACK = 1024;
    public static final int FLAG_FROM_SYSTEM = 8;
    public static final int FLAG_KEEP_TOUCH_MODE = 4;
    public static final int FLAG_LONG_PRESS = 128;
    public static final int FLAG_PREDISPATCH = 536870912;
    public static final int FLAG_SOFT_KEYBOARD = 2;
    public static final int FLAG_START_TRACKING = 1073741824;
    public static final int FLAG_TAINTED = Integer.MIN_VALUE;
    public static final int FLAG_TRACKING = 512;
    public static final int FLAG_VIRTUAL_HARD_KEY = 64;
    public static final int FLAG_WOKE_HERE = 1;
    public static final int KEYCODE_0 = 7;
    public static final int KEYCODE_1 = 8;
    public static final int KEYCODE_2 = 9;
    public static final int KEYCODE_3 = 10;
    public static final int KEYCODE_3D_MODE = 206;
    public static final int KEYCODE_4 = 11;
    public static final int KEYCODE_5 = 12;
    public static final int KEYCODE_6 = 13;
    public static final int KEYCODE_7 = 14;
    public static final int KEYCODE_8 = 15;
    public static final int KEYCODE_9 = 16;
    public static final int KEYCODE_A = 29;
    public static final int KEYCODE_ALT_LEFT = 57;
    public static final int KEYCODE_ALT_RIGHT = 58;
    public static final int KEYCODE_APOSTROPHE = 75;
    public static final int KEYCODE_APPS = 10011;
    public static final int KEYCODE_APP_SWITCH = 187;
    public static final int KEYCODE_ASSIST = 219;
    public static final int KEYCODE_AT = 77;
    public static final int KEYCODE_AUDIO = 10003;
    public static final int KEYCODE_AVR_INPUT = 182;
    public static final int KEYCODE_AVR_POWER = 181;
    public static final int KEYCODE_B = 30;
    public static final int KEYCODE_BACK = 4;
    public static final int KEYCODE_BACKSLASH = 73;
    public static final int KEYCODE_BOOKMARK = 174;
    public static final int KEYCODE_BREAK = 121;
    public static final int KEYCODE_BRIGHTNESS_DOWN = 220;
    public static final int KEYCODE_BRIGHTNESS_UP = 221;
    public static final int KEYCODE_BROWSER = 10012;
    public static final int KEYCODE_BUTTON_1 = 188;
    public static final int KEYCODE_BUTTON_10 = 197;
    public static final int KEYCODE_BUTTON_11 = 198;
    public static final int KEYCODE_BUTTON_12 = 199;
    public static final int KEYCODE_BUTTON_13 = 200;
    public static final int KEYCODE_BUTTON_14 = 201;
    public static final int KEYCODE_BUTTON_15 = 202;
    public static final int KEYCODE_BUTTON_16 = 203;
    public static final int KEYCODE_BUTTON_2 = 189;
    public static final int KEYCODE_BUTTON_3 = 190;
    public static final int KEYCODE_BUTTON_4 = 191;
    public static final int KEYCODE_BUTTON_5 = 192;
    public static final int KEYCODE_BUTTON_6 = 193;
    public static final int KEYCODE_BUTTON_7 = 194;
    public static final int KEYCODE_BUTTON_8 = 195;
    public static final int KEYCODE_BUTTON_9 = 196;
    public static final int KEYCODE_BUTTON_A = 96;
    public static final int KEYCODE_BUTTON_B = 97;
    public static final int KEYCODE_BUTTON_C = 98;
    public static final int KEYCODE_BUTTON_L1 = 102;
    public static final int KEYCODE_BUTTON_L2 = 104;
    public static final int KEYCODE_BUTTON_MODE = 110;
    public static final int KEYCODE_BUTTON_R1 = 103;
    public static final int KEYCODE_BUTTON_R2 = 105;
    public static final int KEYCODE_BUTTON_SELECT = 109;
    public static final int KEYCODE_BUTTON_START = 108;
    public static final int KEYCODE_BUTTON_THUMBL = 106;
    public static final int KEYCODE_BUTTON_THUMBR = 107;
    public static final int KEYCODE_BUTTON_X = 99;
    public static final int KEYCODE_BUTTON_Y = 100;
    public static final int KEYCODE_BUTTON_Z = 101;
    public static final int KEYCODE_C = 31;
    public static final int KEYCODE_CALCULATOR = 210;
    public static final int KEYCODE_CALENDAR = 208;
    public static final int KEYCODE_CALL = 5;
    public static final int KEYCODE_CAMERA = 27;
    public static final int KEYCODE_CAPS_LOCK = 115;
    public static final int KEYCODE_CAPTIONS = 175;
    public static final int KEYCODE_CHANNEL_DOWN = 167;
    public static final int KEYCODE_CHANNEL_UP = 166;
    public static final int KEYCODE_CLEAR = 28;
    public static final int KEYCODE_COMMA = 55;
    public static final int KEYCODE_CONTACTS = 207;
    public static final int KEYCODE_CTRL_LEFT = 113;
    public static final int KEYCODE_CTRL_RIGHT = 114;
    public static final int KEYCODE_D = 32;
    public static final int KEYCODE_DEL = 67;
    public static final int KEYCODE_DPAD_CENTER = 23;
    public static final int KEYCODE_DPAD_DOWN = 20;
    public static final int KEYCODE_DPAD_LEFT = 21;
    public static final int KEYCODE_DPAD_RIGHT = 22;
    public static final int KEYCODE_DPAD_UP = 19;
    public static final int KEYCODE_DVR = 173;
    public static final int KEYCODE_E = 33;
    public static final int KEYCODE_EISU = 212;
    public static final int KEYCODE_ENDCALL = 6;
    public static final int KEYCODE_ENTER = 66;
    public static final int KEYCODE_ENVELOPE = 65;
    public static final int KEYCODE_EQUALS = 70;
    public static final int KEYCODE_ESCAPE = 111;
    public static final int KEYCODE_EXPAND = 10008;
    public static final int KEYCODE_EXPLORER = 64;
    public static final int KEYCODE_F = 34;
    public static final int KEYCODE_F1 = 131;
    public static final int KEYCODE_F10 = 140;
    public static final int KEYCODE_F11 = 141;
    public static final int KEYCODE_F12 = 142;
    public static final int KEYCODE_F2 = 132;
    public static final int KEYCODE_F3 = 133;
    public static final int KEYCODE_F4 = 134;
    public static final int KEYCODE_F5 = 135;
    public static final int KEYCODE_F6 = 136;
    public static final int KEYCODE_F7 = 137;
    public static final int KEYCODE_F8 = 138;
    public static final int KEYCODE_F9 = 139;
    public static final int KEYCODE_FAVOURITE = 10006;
    public static final int KEYCODE_FILE_LOCK = 10015;
    public static final int KEYCODE_FOCUS = 80;
    public static final int KEYCODE_FORWARD = 125;
    public static final int KEYCODE_FORWARD_DEL = 112;
    public static final int KEYCODE_FUNCTION = 119;
    public static final int KEYCODE_G = 35;
    public static final int KEYCODE_GOTO = 10001;
    public static final int KEYCODE_GRAVE = 68;
    public static final int KEYCODE_GUIDE = 172;
    public static final int KEYCODE_H = 36;
    public static final int KEYCODE_HEADSETHOOK = 79;
    public static final int KEYCODE_HELP = 10005;
    public static final int KEYCODE_HENKAN = 214;
    public static final int KEYCODE_HOME = 3;
    public static final int KEYCODE_I = 37;
    public static final int KEYCODE_INFO = 165;
    public static final int KEYCODE_INSERT = 124;
    public static final int KEYCODE_J = 38;
    public static final int KEYCODE_K = 39;
    public static final int KEYCODE_KANA = 218;
    public static final int KEYCODE_KATAKANA_HIRAGANA = 215;
    public static final int KEYCODE_KOUT_VOLUME_DOWN = 373;
    public static final int KEYCODE_KOUT_VOLUME_UP = 372;
    public static final int KEYCODE_L = 40;
    public static final int KEYCODE_LANGUAGE_SWITCH = 204;
    public static final int KEYCODE_LEFT_BRACKET = 71;
    public static final int KEYCODE_LOOP = 10007;
    public static final int KEYCODE_M = 41;
    public static final int KEYCODE_MANNER_MODE = 205;
    public static final int KEYCODE_MEDIA_AUDIO_TRACK = 222;
    public static final int KEYCODE_MEDIA_CLOSE = 128;
    public static final int KEYCODE_MEDIA_EJECT = 129;
    public static final int KEYCODE_MEDIA_FAST_FORWARD = 90;
    public static final int KEYCODE_MEDIA_NEXT = 87;
    public static final int KEYCODE_MEDIA_PAUSE = 127;
    public static final int KEYCODE_MEDIA_PLAY = 126;
    public static final int KEYCODE_MEDIA_PLAY_PAUSE = 85;
    public static final int KEYCODE_MEDIA_PREVIOUS = 88;
    public static final int KEYCODE_MEDIA_RECORD = 130;
    public static final int KEYCODE_MEDIA_REWIND = 89;
    public static final int KEYCODE_MEDIA_STOP = 86;
    public static final int KEYCODE_MENU = 82;
    public static final int KEYCODE_META_LEFT = 117;
    public static final int KEYCODE_META_RIGHT = 118;
    public static final int KEYCODE_MIC_VOLUME_DOWN = 371;
    public static final int KEYCODE_MIC_VOLUME_UP = 370;
    public static final int KEYCODE_MINUS = 69;
    public static final int KEYCODE_MOUSE = 10009;
    public static final int KEYCODE_MOVE_END = 123;
    public static final int KEYCODE_MOVE_HOME = 122;
    public static final int KEYCODE_MOVIE = 10010;
    public static final int KEYCODE_MUHENKAN = 213;
    public static final int KEYCODE_MUSIC = 209;
    public static final int KEYCODE_MUTE = 91;
    public static final int KEYCODE_N = 42;
    public static final int KEYCODE_NOTIFICATION = 83;
    public static final int KEYCODE_NUM = 78;
    public static final int KEYCODE_NUMPAD_0 = 144;
    public static final int KEYCODE_NUMPAD_1 = 145;
    public static final int KEYCODE_NUMPAD_2 = 146;
    public static final int KEYCODE_NUMPAD_3 = 147;
    public static final int KEYCODE_NUMPAD_4 = 148;
    public static final int KEYCODE_NUMPAD_5 = 149;
    public static final int KEYCODE_NUMPAD_6 = 150;
    public static final int KEYCODE_NUMPAD_7 = 151;
    public static final int KEYCODE_NUMPAD_8 = 152;
    public static final int KEYCODE_NUMPAD_9 = 153;
    public static final int KEYCODE_NUMPAD_ADD = 157;
    public static final int KEYCODE_NUMPAD_COMMA = 159;
    public static final int KEYCODE_NUMPAD_DIVIDE = 154;
    public static final int KEYCODE_NUMPAD_DOT = 158;
    public static final int KEYCODE_NUMPAD_ENTER = 160;
    public static final int KEYCODE_NUMPAD_EQUALS = 161;
    public static final int KEYCODE_NUMPAD_LEFT_PAREN = 162;
    public static final int KEYCODE_NUMPAD_MULTIPLY = 155;
    public static final int KEYCODE_NUMPAD_RIGHT_PAREN = 163;
    public static final int KEYCODE_NUMPAD_SUBTRACT = 156;
    public static final int KEYCODE_NUM_LOCK = 143;
    public static final int KEYCODE_O = 43;
    public static final int KEYCODE_P = 44;
    public static final int KEYCODE_PAGE_DOWN = 93;
    public static final int KEYCODE_PAGE_UP = 92;
    public static final int KEYCODE_PERIOD = 56;
    public static final int KEYCODE_PICTSYMBOLS = 94;
    public static final int KEYCODE_PLUS = 81;
    public static final int KEYCODE_POUND = 18;
    public static final int KEYCODE_POWER = 26;
    public static final int KEYCODE_PROG_BLUE = 186;
    public static final int KEYCODE_PROG_GREEN = 184;
    public static final int KEYCODE_PROG_RED = 183;
    public static final int KEYCODE_PROG_YELLOW = 185;
    public static final int KEYCODE_Q = 45;
    public static final int KEYCODE_R = 46;
    public static final int KEYCODE_RIGHT_BRACKET = 72;
    public static final int KEYCODE_RO = 217;
    public static final int KEYCODE_S = 47;
    public static final int KEYCODE_SCREENSHOT = 10013;
    public static final int KEYCODE_SCROLL_LOCK = 116;
    public static final int KEYCODE_SEARCH = 84;
    public static final int KEYCODE_SEMICOLON = 74;
    public static final int KEYCODE_SETTINGS = 176;
    public static final int KEYCODE_SHIFT_LEFT = 59;
    public static final int KEYCODE_SHIFT_RIGHT = 60;
    public static final int KEYCODE_SLASH = 76;
    public static final int KEYCODE_SOFT_LEFT = 1;
    public static final int KEYCODE_SOFT_RIGHT = 2;
    public static final int KEYCODE_SPACE = 62;
    public static final int KEYCODE_SPEECH_RECOGNITION = 10014;
    public static final int KEYCODE_STAR = 17;
    public static final int KEYCODE_STB_INPUT = 180;
    public static final int KEYCODE_STB_POWER = 179;
    public static final int KEYCODE_SUBTITLE = 10002;
    public static final int KEYCODE_SWITCH_CHARSET = 95;
    public static final int KEYCODE_SYM = 63;
    public static final int KEYCODE_SYSRQ = 120;
    public static final int KEYCODE_T = 48;
    public static final int KEYCODE_TAB = 61;
    public static final int KEYCODE_TV = 170;
    public static final int KEYCODE_TV_INPUT = 178;
    public static final int KEYCODE_TV_POWER = 177;
    public static final int KEYCODE_TV_SYSTEM = 10000;
    public static final int KEYCODE_U = 49;
    public static final int KEYCODE_UNKNOWN = 0;
    public static final int KEYCODE_V = 50;
    public static final int KEYCODE_VOLUME_DOWN = 25;
    public static final int KEYCODE_VOLUME_MUTE = 164;
    public static final int KEYCODE_VOLUME_UP = 24;
    public static final int KEYCODE_W = 51;
    public static final int KEYCODE_WINDOW = 171;
    public static final int KEYCODE_X = 52;
    public static final int KEYCODE_Y = 53;
    public static final int KEYCODE_YEN = 216;
    public static final int KEYCODE_Z = 54;
    public static final int KEYCODE_ZENKAKU_HANKAKU = 211;
    public static final int KEYCODE_ZOOM = 10004;
    public static final int KEYCODE_ZOOM_IN = 168;
    public static final int KEYCODE_ZOOM_OUT = 169;
    private static final int LAST_KEYCODE = 10015;

    @Deprecated
    public static final int MAX_KEYCODE = 84;
    private static final int MAX_RECYCLED = 10;
    private static final int META_ALL_MASK = 7827711;
    public static final int META_ALT_LEFT_ON = 16;
    public static final int META_ALT_LOCKED = 512;
    public static final int META_ALT_MASK = 50;
    public static final int META_ALT_ON = 2;
    public static final int META_ALT_RIGHT_ON = 32;
    public static final int META_CAPS_LOCK_ON = 1048576;
    public static final int META_CAP_LOCKED = 256;
    public static final int META_CTRL_LEFT_ON = 8192;
    public static final int META_CTRL_MASK = 28672;
    public static final int META_CTRL_ON = 4096;
    public static final int META_CTRL_RIGHT_ON = 16384;
    public static final int META_FUNCTION_ON = 8;
    private static final int META_INVALID_MODIFIER_MASK = 7343872;
    private static final int META_LOCK_MASK = 7340032;
    public static final int META_META_LEFT_ON = 131072;
    public static final int META_META_MASK = 458752;
    public static final int META_META_ON = 65536;
    public static final int META_META_RIGHT_ON = 262144;
    private static final int META_MODIFIER_MASK = 487679;
    public static final int META_NUM_LOCK_ON = 2097152;
    public static final int META_SCROLL_LOCK_ON = 4194304;
    public static final int META_SELECTING = 2048;
    public static final int META_SHIFT_LEFT_ON = 64;
    public static final int META_SHIFT_MASK = 193;
    public static final int META_SHIFT_ON = 1;
    public static final int META_SHIFT_RIGHT_ON = 128;
    public static final int META_SYM_LOCKED = 1024;
    public static final int META_SYM_ON = 4;
    private static final int META_SYNTHETIC_MASK = 3840;
    static final String TAG = "KeyEvent";
    private static KeyEvent gRecyclerTop;
    private static int gRecyclerUsed;
    private int mAction;
    private String mCharacters;
    private int mDeviceId;
    private long mDownTime;
    private long mEventTime;
    private int mFlags;
    private int mKeyCode;
    private int mMetaState;
    private KeyEvent mNext;
    private int mRepeatCount;
    private int mScanCode;
    private int mSource;
    private static final SparseArray<String> KEYCODE_SYMBOLIC_NAMES = new SparseArray<>();
    private static final String[] META_SYMBOLIC_NAMES = {"META_SHIFT_ON", "META_ALT_ON", "META_SYM_ON", "META_FUNCTION_ON", "META_ALT_LEFT_ON", "META_ALT_RIGHT_ON", "META_SHIFT_LEFT_ON", "META_SHIFT_RIGHT_ON", "META_CAP_LOCKED", "META_ALT_LOCKED", "META_SYM_LOCKED", "0x00000800", "META_CTRL_ON", "META_CTRL_LEFT_ON", "META_CTRL_RIGHT_ON", "0x00008000", "META_META_ON", "META_META_LEFT_ON", "META_META_RIGHT_ON", "0x00080000", "META_CAPS_LOCK_ON", "META_NUM_LOCK_ON", "META_SCROLL_LOCK_ON", "0x00800000", "0x01000000", "0x02000000", "0x04000000", "0x08000000", "0x10000000", "0x20000000", "0x40000000", "0x80000000"};
    private static final Object gRecyclerLock = new Object();

    public interface Callback {
        boolean onKeyDown(int i, KeyEvent keyEvent);

        boolean onKeyLongPress(int i, KeyEvent keyEvent);

        boolean onKeyMultiple(int i, int i2, KeyEvent keyEvent);

        boolean onKeyUp(int i, KeyEvent keyEvent);
    }

    public static int getMaxKeyCode() {
        return 10015;
    }

    public static int getModifierMetaStateMask() {
        return META_MODIFIER_MASK;
    }

    public static final boolean isConfirmKey(int i) {
        return i == 23 || i == 66;
    }

    public static final boolean isGamepadButton(int i) {
        switch (i) {
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
                return true;
            default:
                switch (i) {
                    case 188:
                    case 189:
                    case 190:
                    case 191:
                    case 192:
                    case 193:
                    case 194:
                    case 195:
                    case 196:
                    case 197:
                    case 198:
                    case 199:
                    case 200:
                    case 201:
                    case 202:
                    case 203:
                        return true;
                    default:
                        return false;
                }
        }
    }

    public static boolean isModifierKey(int i) {
        if (i == 63 || i == 78 || i == 113 || i == 114) {
            return true;
        }
        switch (i) {
            case 57:
            case 58:
            case 59:
            case 60:
                return true;
            default:
                switch (i) {
                    case 117:
                    case 118:
                    case 119:
                        return true;
                    default:
                        return false;
                }
        }
    }

    private native boolean native_hasDefaultAction(int i);

    private native boolean native_isSystemKey(int i);

    public static int normalizeMetaState(int i) {
        if ((i & 192) != 0) {
            i |= 1;
        }
        if ((i & 48) != 0) {
            i |= 2;
        }
        if ((i & AudioSystem.DEVICE_OUT_ALL_USB) != 0) {
            i |= 4096;
        }
        if ((393216 & i) != 0) {
            i |= 65536;
        }
        if ((i & 256) != 0) {
            i |= 1048576;
        }
        if ((i & 512) != 0) {
            i |= 2;
        }
        if ((i & 1024) != 0) {
            i |= 4;
        }
        return i & META_ALL_MASK;
    }

    @Override // android.view.InputEvent
    public final void recycleIfNeededAfterDispatch() {
    }

    static /* synthetic */ int access$076(KeyEvent keyEvent, int i) {
        int i2 = i | keyEvent.mFlags;
        keyEvent.mFlags = i2;
        return i2;
    }

    static {
        populateKeycodeSymbolicNames();
        CREATOR = new Parcelable.Creator<KeyEvent>() { // from class: android.view.KeyEvent.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public KeyEvent createFromParcel(Parcel parcel) {
                parcel.readInt();
                return KeyEvent.createFromParcelBody(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public KeyEvent[] newArray(int i) {
                return new KeyEvent[i];
            }
        };
    }

    private static void populateKeycodeSymbolicNames() {
        SparseArray<String> sparseArray = KEYCODE_SYMBOLIC_NAMES;
        sparseArray.append(0, "KEYCODE_UNKNOWN");
        sparseArray.append(1, "KEYCODE_SOFT_LEFT");
        sparseArray.append(2, "KEYCODE_SOFT_RIGHT");
        sparseArray.append(3, "KEYCODE_HOME");
        sparseArray.append(4, "KEYCODE_BACK");
        sparseArray.append(5, "KEYCODE_CALL");
        sparseArray.append(6, "KEYCODE_ENDCALL");
        sparseArray.append(7, "KEYCODE_0");
        sparseArray.append(8, "KEYCODE_1");
        sparseArray.append(9, "KEYCODE_2");
        sparseArray.append(10, "KEYCODE_3");
        sparseArray.append(11, "KEYCODE_4");
        sparseArray.append(12, "KEYCODE_5");
        sparseArray.append(13, "KEYCODE_6");
        sparseArray.append(14, "KEYCODE_7");
        sparseArray.append(15, "KEYCODE_8");
        sparseArray.append(16, "KEYCODE_9");
        sparseArray.append(17, "KEYCODE_STAR");
        sparseArray.append(18, "KEYCODE_POUND");
        sparseArray.append(19, "KEYCODE_DPAD_UP");
        sparseArray.append(20, "KEYCODE_DPAD_DOWN");
        sparseArray.append(21, "KEYCODE_DPAD_LEFT");
        sparseArray.append(22, "KEYCODE_DPAD_RIGHT");
        sparseArray.append(23, "KEYCODE_DPAD_CENTER");
        sparseArray.append(24, "KEYCODE_VOLUME_UP");
        sparseArray.append(25, "KEYCODE_VOLUME_DOWN");
        sparseArray.append(26, "KEYCODE_POWER");
        sparseArray.append(27, "KEYCODE_CAMERA");
        sparseArray.append(28, "KEYCODE_CLEAR");
        sparseArray.append(29, "KEYCODE_A");
        sparseArray.append(30, "KEYCODE_B");
        sparseArray.append(31, "KEYCODE_C");
        sparseArray.append(32, "KEYCODE_D");
        sparseArray.append(33, "KEYCODE_E");
        sparseArray.append(34, "KEYCODE_F");
        sparseArray.append(35, "KEYCODE_G");
        sparseArray.append(36, "KEYCODE_H");
        sparseArray.append(37, "KEYCODE_I");
        sparseArray.append(38, "KEYCODE_J");
        sparseArray.append(39, "KEYCODE_K");
        sparseArray.append(40, "KEYCODE_L");
        sparseArray.append(41, "KEYCODE_M");
        sparseArray.append(42, "KEYCODE_N");
        sparseArray.append(43, "KEYCODE_O");
        sparseArray.append(44, "KEYCODE_P");
        sparseArray.append(45, "KEYCODE_Q");
        sparseArray.append(46, "KEYCODE_R");
        sparseArray.append(47, "KEYCODE_S");
        sparseArray.append(48, "KEYCODE_T");
        sparseArray.append(49, "KEYCODE_U");
        sparseArray.append(50, "KEYCODE_V");
        sparseArray.append(51, "KEYCODE_W");
        sparseArray.append(52, "KEYCODE_X");
        sparseArray.append(53, "KEYCODE_Y");
        sparseArray.append(54, "KEYCODE_Z");
        sparseArray.append(55, "KEYCODE_COMMA");
        sparseArray.append(56, "KEYCODE_PERIOD");
        sparseArray.append(57, "KEYCODE_ALT_LEFT");
        sparseArray.append(58, "KEYCODE_ALT_RIGHT");
        sparseArray.append(59, "KEYCODE_SHIFT_LEFT");
        sparseArray.append(60, "KEYCODE_SHIFT_RIGHT");
        sparseArray.append(61, "KEYCODE_TAB");
        sparseArray.append(62, "KEYCODE_SPACE");
        sparseArray.append(63, "KEYCODE_SYM");
        sparseArray.append(64, "KEYCODE_EXPLORER");
        sparseArray.append(65, "KEYCODE_ENVELOPE");
        sparseArray.append(66, "KEYCODE_ENTER");
        sparseArray.append(67, "KEYCODE_DEL");
        sparseArray.append(68, "KEYCODE_GRAVE");
        sparseArray.append(69, "KEYCODE_MINUS");
        sparseArray.append(70, "KEYCODE_EQUALS");
        sparseArray.append(71, "KEYCODE_LEFT_BRACKET");
        sparseArray.append(72, "KEYCODE_RIGHT_BRACKET");
        sparseArray.append(73, "KEYCODE_BACKSLASH");
        sparseArray.append(74, "KEYCODE_SEMICOLON");
        sparseArray.append(75, "KEYCODE_APOSTROPHE");
        sparseArray.append(76, "KEYCODE_SLASH");
        sparseArray.append(77, "KEYCODE_AT");
        sparseArray.append(78, "KEYCODE_NUM");
        sparseArray.append(79, "KEYCODE_HEADSETHOOK");
        sparseArray.append(80, "KEYCODE_FOCUS");
        sparseArray.append(81, "KEYCODE_PLUS");
        sparseArray.append(82, "KEYCODE_MENU");
        sparseArray.append(83, "KEYCODE_NOTIFICATION");
        sparseArray.append(84, "KEYCODE_SEARCH");
        sparseArray.append(85, "KEYCODE_MEDIA_PLAY_PAUSE");
        sparseArray.append(86, "KEYCODE_MEDIA_STOP");
        sparseArray.append(87, "KEYCODE_MEDIA_NEXT");
        sparseArray.append(88, "KEYCODE_MEDIA_PREVIOUS");
        sparseArray.append(89, "KEYCODE_MEDIA_REWIND");
        sparseArray.append(90, "KEYCODE_MEDIA_FAST_FORWARD");
        sparseArray.append(91, "KEYCODE_MUTE");
        sparseArray.append(92, "KEYCODE_PAGE_UP");
        sparseArray.append(93, "KEYCODE_PAGE_DOWN");
        sparseArray.append(94, "KEYCODE_PICTSYMBOLS");
        sparseArray.append(95, "KEYCODE_SWITCH_CHARSET");
        sparseArray.append(96, "KEYCODE_BUTTON_A");
        sparseArray.append(97, "KEYCODE_BUTTON_B");
        sparseArray.append(98, "KEYCODE_BUTTON_C");
        sparseArray.append(99, "KEYCODE_BUTTON_X");
        sparseArray.append(100, "KEYCODE_BUTTON_Y");
        sparseArray.append(101, "KEYCODE_BUTTON_Z");
        sparseArray.append(102, "KEYCODE_BUTTON_L1");
        sparseArray.append(103, "KEYCODE_BUTTON_R1");
        sparseArray.append(104, "KEYCODE_BUTTON_L2");
        sparseArray.append(105, "KEYCODE_BUTTON_R2");
        sparseArray.append(106, "KEYCODE_BUTTON_THUMBL");
        sparseArray.append(107, "KEYCODE_BUTTON_THUMBR");
        sparseArray.append(108, "KEYCODE_BUTTON_START");
        sparseArray.append(109, "KEYCODE_BUTTON_SELECT");
        sparseArray.append(110, "KEYCODE_BUTTON_MODE");
        sparseArray.append(111, "KEYCODE_ESCAPE");
        sparseArray.append(112, "KEYCODE_FORWARD_DEL");
        sparseArray.append(113, "KEYCODE_CTRL_LEFT");
        sparseArray.append(114, "KEYCODE_CTRL_RIGHT");
        sparseArray.append(115, "KEYCODE_CAPS_LOCK");
        sparseArray.append(116, "KEYCODE_SCROLL_LOCK");
        sparseArray.append(117, "KEYCODE_META_LEFT");
        sparseArray.append(118, "KEYCODE_META_RIGHT");
        sparseArray.append(119, "KEYCODE_FUNCTION");
        sparseArray.append(120, "KEYCODE_SYSRQ");
        sparseArray.append(121, "KEYCODE_BREAK");
        sparseArray.append(122, "KEYCODE_MOVE_HOME");
        sparseArray.append(123, "KEYCODE_MOVE_END");
        sparseArray.append(124, "KEYCODE_INSERT");
        sparseArray.append(125, "KEYCODE_FORWARD");
        sparseArray.append(126, "KEYCODE_MEDIA_PLAY");
        sparseArray.append(127, "KEYCODE_MEDIA_PAUSE");
        sparseArray.append(128, "KEYCODE_MEDIA_CLOSE");
        sparseArray.append(129, "KEYCODE_MEDIA_EJECT");
        sparseArray.append(130, "KEYCODE_MEDIA_RECORD");
        sparseArray.append(131, "KEYCODE_F1");
        sparseArray.append(132, "KEYCODE_F2");
        sparseArray.append(133, "KEYCODE_F3");
        sparseArray.append(134, "KEYCODE_F4");
        sparseArray.append(135, "KEYCODE_F5");
        sparseArray.append(136, "KEYCODE_F6");
        sparseArray.append(137, "KEYCODE_F7");
        sparseArray.append(138, "KEYCODE_F8");
        sparseArray.append(139, "KEYCODE_F9");
        sparseArray.append(140, "KEYCODE_F10");
        sparseArray.append(141, "KEYCODE_F11");
        sparseArray.append(142, "KEYCODE_F12");
        sparseArray.append(143, "KEYCODE_NUM_LOCK");
        sparseArray.append(144, "KEYCODE_NUMPAD_0");
        sparseArray.append(145, "KEYCODE_NUMPAD_1");
        sparseArray.append(146, "KEYCODE_NUMPAD_2");
        sparseArray.append(147, "KEYCODE_NUMPAD_3");
        sparseArray.append(148, "KEYCODE_NUMPAD_4");
        sparseArray.append(149, "KEYCODE_NUMPAD_5");
        sparseArray.append(150, "KEYCODE_NUMPAD_6");
        sparseArray.append(151, "KEYCODE_NUMPAD_7");
        sparseArray.append(152, "KEYCODE_NUMPAD_8");
        sparseArray.append(153, "KEYCODE_NUMPAD_9");
        sparseArray.append(154, "KEYCODE_NUMPAD_DIVIDE");
        sparseArray.append(155, "KEYCODE_NUMPAD_MULTIPLY");
        sparseArray.append(156, "KEYCODE_NUMPAD_SUBTRACT");
        sparseArray.append(157, "KEYCODE_NUMPAD_ADD");
        sparseArray.append(158, "KEYCODE_NUMPAD_DOT");
        sparseArray.append(159, "KEYCODE_NUMPAD_COMMA");
        sparseArray.append(160, "KEYCODE_NUMPAD_ENTER");
        sparseArray.append(161, "KEYCODE_NUMPAD_EQUALS");
        sparseArray.append(162, "KEYCODE_NUMPAD_LEFT_PAREN");
        sparseArray.append(163, "KEYCODE_NUMPAD_RIGHT_PAREN");
        sparseArray.append(164, "KEYCODE_VOLUME_MUTE");
        sparseArray.append(165, "KEYCODE_INFO");
        sparseArray.append(166, "KEYCODE_CHANNEL_UP");
        sparseArray.append(167, "KEYCODE_CHANNEL_DOWN");
        sparseArray.append(168, "KEYCODE_ZOOM_IN");
        sparseArray.append(169, "KEYCODE_ZOOM_OUT");
        sparseArray.append(170, "KEYCODE_TV");
        sparseArray.append(171, "KEYCODE_WINDOW");
        sparseArray.append(172, "KEYCODE_GUIDE");
        sparseArray.append(173, "KEYCODE_DVR");
        sparseArray.append(174, "KEYCODE_BOOKMARK");
        sparseArray.append(175, "KEYCODE_CAPTIONS");
        sparseArray.append(176, "KEYCODE_SETTINGS");
        sparseArray.append(177, "KEYCODE_TV_POWER");
        sparseArray.append(178, "KEYCODE_TV_INPUT");
        sparseArray.append(180, "KEYCODE_STB_INPUT");
        sparseArray.append(179, "KEYCODE_STB_POWER");
        sparseArray.append(181, "KEYCODE_AVR_POWER");
        sparseArray.append(182, "KEYCODE_AVR_INPUT");
        sparseArray.append(183, "KEYCODE_PROG_RED");
        sparseArray.append(184, "KEYCODE_PROG_GREEN");
        sparseArray.append(185, "KEYCODE_PROG_YELLOW");
        sparseArray.append(186, "KEYCODE_PROG_BLUE");
        sparseArray.append(187, "KEYCODE_APP_SWITCH");
        sparseArray.append(188, "KEYCODE_BUTTON_1");
        sparseArray.append(189, "KEYCODE_BUTTON_2");
        sparseArray.append(190, "KEYCODE_BUTTON_3");
        sparseArray.append(191, "KEYCODE_BUTTON_4");
        sparseArray.append(192, "KEYCODE_BUTTON_5");
        sparseArray.append(193, "KEYCODE_BUTTON_6");
        sparseArray.append(194, "KEYCODE_BUTTON_7");
        sparseArray.append(195, "KEYCODE_BUTTON_8");
        sparseArray.append(196, "KEYCODE_BUTTON_9");
        sparseArray.append(197, "KEYCODE_BUTTON_10");
        sparseArray.append(198, "KEYCODE_BUTTON_11");
        sparseArray.append(199, "KEYCODE_BUTTON_12");
        sparseArray.append(200, "KEYCODE_BUTTON_13");
        sparseArray.append(201, "KEYCODE_BUTTON_14");
        sparseArray.append(202, "KEYCODE_BUTTON_15");
        sparseArray.append(203, "KEYCODE_BUTTON_16");
        sparseArray.append(204, "KEYCODE_LANGUAGE_SWITCH");
        sparseArray.append(205, "KEYCODE_MANNER_MODE");
        sparseArray.append(206, "KEYCODE_3D_MODE");
        sparseArray.append(207, "KEYCODE_CONTACTS");
        sparseArray.append(208, "KEYCODE_CALENDAR");
        sparseArray.append(209, "KEYCODE_MUSIC");
        sparseArray.append(210, "KEYCODE_CALCULATOR");
        sparseArray.append(211, "KEYCODE_ZENKAKU_HANKAKU");
        sparseArray.append(212, "KEYCODE_EISU");
        sparseArray.append(213, "KEYCODE_MUHENKAN");
        sparseArray.append(214, "KEYCODE_HENKAN");
        sparseArray.append(215, "KEYCODE_KATAKANA_HIRAGANA");
        sparseArray.append(216, "KEYCODE_YEN");
        sparseArray.append(217, "KEYCODE_RO");
        sparseArray.append(218, "KEYCODE_KANA");
        sparseArray.append(219, "KEYCODE_ASSIST");
        sparseArray.append(220, "KEYCODE_BRIGHTNESS_DOWN");
        sparseArray.append(221, "KEYCODE_BRIGHTNESS_UP");
        sparseArray.append(222, "KEYCODE_MEDIA_AUDIO_TRACK");
        sparseArray.append(KEYCODE_MIC_VOLUME_UP, "KEYCODE_MIC_VOLUME_UP");
        sparseArray.append(KEYCODE_MIC_VOLUME_DOWN, "KEYCODE_MIC_VOLUME_DOWN");
        sparseArray.append(KEYCODE_KOUT_VOLUME_UP, "KEYCODE_KOUT_VOLUME_UP");
        sparseArray.append(KEYCODE_KOUT_VOLUME_DOWN, "KEYCODE_KOUT_VOLUME_DOWN");
        sparseArray.append(10000, "KEYCODE_TV_SYSTEM");
        sparseArray.append(10001, "KEYCODE_GOTO");
        sparseArray.append(10002, "KEYCODE_SUBTITLE");
        sparseArray.append(10003, "KEYCODE_AUDIO");
        sparseArray.append(KEYCODE_ZOOM, "KEYCODE_ZOOM");
        sparseArray.append(KEYCODE_HELP, "KEYCODE_HELP");
        sparseArray.append(KEYCODE_FAVOURITE, "KEYCODE_FAVOURITE");
        sparseArray.append(KEYCODE_LOOP, "KEYCODE_LOOP");
        sparseArray.append(KEYCODE_EXPAND, "KEYCODE_EXPAND");
        sparseArray.append(KEYCODE_MOUSE, "KEYCODE_MOUSE");
        sparseArray.append(KEYCODE_MOVIE, "KEYCODE_MOVIE");
        sparseArray.append(KEYCODE_APPS, "KEYCODE_APPS");
        sparseArray.append(KEYCODE_BROWSER, "KEYCODE_BROWSER");
        sparseArray.append(KEYCODE_SPEECH_RECOGNITION, "KEYCODE_SPEECH_RECOGNITION");
        sparseArray.append(10015, "KEYCODE_FILE_LOCK");
    }

    public static int getDeadChar(int i, int i2) {
        return KeyCharacterMap.getDeadChar(i, i2);
    }

    private KeyEvent() {
    }

    public KeyEvent(int i, int i2) {
        this.mAction = i;
        this.mKeyCode = i2;
        this.mRepeatCount = 0;
        this.mDeviceId = -1;
    }

    public KeyEvent(long j, long j2, int i, int i2, int i3) {
        this.mDownTime = j;
        this.mEventTime = j2;
        this.mAction = i;
        this.mKeyCode = i2;
        this.mRepeatCount = i3;
        this.mDeviceId = -1;
    }

    public KeyEvent(long j, long j2, int i, int i2, int i3, int i4) {
        this.mDownTime = j;
        this.mEventTime = j2;
        this.mAction = i;
        this.mKeyCode = i2;
        this.mRepeatCount = i3;
        this.mMetaState = i4;
        this.mDeviceId = -1;
    }

    public KeyEvent(long j, long j2, int i, int i2, int i3, int i4, int i5, int i6) {
        this.mDownTime = j;
        this.mEventTime = j2;
        this.mAction = i;
        this.mKeyCode = i2;
        this.mRepeatCount = i3;
        this.mMetaState = i4;
        this.mDeviceId = i5;
        this.mScanCode = i6;
    }

    public KeyEvent(long j, long j2, int i, int i2, int i3, int i4, int i5, int i6, int i7) {
        this.mDownTime = j;
        this.mEventTime = j2;
        this.mAction = i;
        this.mKeyCode = i2;
        this.mRepeatCount = i3;
        this.mMetaState = i4;
        this.mDeviceId = i5;
        this.mScanCode = i6;
        this.mFlags = i7;
    }

    public KeyEvent(long j, long j2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
        this.mDownTime = j;
        this.mEventTime = j2;
        this.mAction = i;
        this.mKeyCode = i2;
        this.mRepeatCount = i3;
        this.mMetaState = i4;
        this.mDeviceId = i5;
        this.mScanCode = i6;
        this.mFlags = i7;
        this.mSource = i8;
    }

    public KeyEvent(long j, String str, int i, int i2) {
        this.mDownTime = j;
        this.mEventTime = j;
        this.mCharacters = str;
        this.mAction = 2;
        this.mKeyCode = 0;
        this.mRepeatCount = 0;
        this.mDeviceId = i;
        this.mFlags = i2;
        this.mSource = 257;
    }

    public KeyEvent(KeyEvent keyEvent) {
        this.mDownTime = keyEvent.mDownTime;
        this.mEventTime = keyEvent.mEventTime;
        this.mAction = keyEvent.mAction;
        this.mKeyCode = keyEvent.mKeyCode;
        this.mRepeatCount = keyEvent.mRepeatCount;
        this.mMetaState = keyEvent.mMetaState;
        this.mDeviceId = keyEvent.mDeviceId;
        this.mSource = keyEvent.mSource;
        this.mScanCode = keyEvent.mScanCode;
        this.mFlags = keyEvent.mFlags;
        this.mCharacters = keyEvent.mCharacters;
    }

    @Deprecated
    public KeyEvent(KeyEvent keyEvent, long j, int i) {
        this.mDownTime = keyEvent.mDownTime;
        this.mEventTime = j;
        this.mAction = keyEvent.mAction;
        this.mKeyCode = keyEvent.mKeyCode;
        this.mRepeatCount = i;
        this.mMetaState = keyEvent.mMetaState;
        this.mDeviceId = keyEvent.mDeviceId;
        this.mSource = keyEvent.mSource;
        this.mScanCode = keyEvent.mScanCode;
        this.mFlags = keyEvent.mFlags;
        this.mCharacters = keyEvent.mCharacters;
    }

    private static KeyEvent obtain() {
        synchronized (gRecyclerLock) {
            KeyEvent keyEvent = gRecyclerTop;
            if (keyEvent == null) {
                return new KeyEvent();
            }
            gRecyclerTop = keyEvent.mNext;
            gRecyclerUsed--;
            keyEvent.mNext = null;
            keyEvent.prepareForReuse();
            return keyEvent;
        }
    }

    public static KeyEvent obtain(long j, long j2, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, String str) {
        KeyEvent keyEventObtain = obtain();
        keyEventObtain.mDownTime = j;
        keyEventObtain.mEventTime = j2;
        keyEventObtain.mAction = i;
        keyEventObtain.mKeyCode = i2;
        keyEventObtain.mRepeatCount = i3;
        keyEventObtain.mMetaState = i4;
        keyEventObtain.mDeviceId = i5;
        keyEventObtain.mScanCode = i6;
        keyEventObtain.mFlags = i7;
        keyEventObtain.mSource = i8;
        keyEventObtain.mCharacters = str;
        return keyEventObtain;
    }

    public static KeyEvent obtain(KeyEvent keyEvent) {
        KeyEvent keyEventObtain = obtain();
        keyEventObtain.mDownTime = keyEvent.mDownTime;
        keyEventObtain.mEventTime = keyEvent.mEventTime;
        keyEventObtain.mAction = keyEvent.mAction;
        keyEventObtain.mKeyCode = keyEvent.mKeyCode;
        keyEventObtain.mRepeatCount = keyEvent.mRepeatCount;
        keyEventObtain.mMetaState = keyEvent.mMetaState;
        keyEventObtain.mDeviceId = keyEvent.mDeviceId;
        keyEventObtain.mScanCode = keyEvent.mScanCode;
        keyEventObtain.mFlags = keyEvent.mFlags;
        keyEventObtain.mSource = keyEvent.mSource;
        keyEventObtain.mCharacters = keyEvent.mCharacters;
        return keyEventObtain;
    }

    @Override // android.view.InputEvent
    public KeyEvent copy() {
        return obtain(this);
    }

    @Override // android.view.InputEvent
    public final void recycle() {
        super.recycle();
        this.mCharacters = null;
        synchronized (gRecyclerLock) {
            int i = gRecyclerUsed;
            if (i < 10) {
                gRecyclerUsed = i + 1;
                this.mNext = gRecyclerTop;
                gRecyclerTop = this;
            }
        }
    }

    public static KeyEvent changeTimeRepeat(KeyEvent keyEvent, long j, int i) {
        return new KeyEvent(keyEvent, j, i);
    }

    public static KeyEvent changeTimeRepeat(KeyEvent keyEvent, long j, int i, int i2) {
        KeyEvent keyEvent2 = new KeyEvent(keyEvent);
        keyEvent2.mEventTime = j;
        keyEvent2.mRepeatCount = i;
        keyEvent2.mFlags = i2;
        return keyEvent2;
    }

    private KeyEvent(KeyEvent keyEvent, int i) {
        this.mDownTime = keyEvent.mDownTime;
        this.mEventTime = keyEvent.mEventTime;
        this.mAction = i;
        this.mKeyCode = keyEvent.mKeyCode;
        this.mRepeatCount = keyEvent.mRepeatCount;
        this.mMetaState = keyEvent.mMetaState;
        this.mDeviceId = keyEvent.mDeviceId;
        this.mSource = keyEvent.mSource;
        this.mScanCode = keyEvent.mScanCode;
        this.mFlags = keyEvent.mFlags;
    }

    public static KeyEvent changeAction(KeyEvent keyEvent, int i) {
        return new KeyEvent(keyEvent, i);
    }

    public static KeyEvent changeFlags(KeyEvent keyEvent, int i) {
        KeyEvent keyEvent2 = new KeyEvent(keyEvent);
        keyEvent2.mFlags = i;
        return keyEvent2;
    }

    @Override // android.view.InputEvent
    public final boolean isTainted() {
        return (this.mFlags & Integer.MIN_VALUE) != 0;
    }

    @Override // android.view.InputEvent
    public final void setTainted(boolean z) {
        this.mFlags = z ? this.mFlags | Integer.MIN_VALUE : this.mFlags & Integer.MAX_VALUE;
    }

    @Deprecated
    public final boolean isDown() {
        return this.mAction == 0;
    }

    public final boolean isSystem() {
        return native_isSystemKey(this.mKeyCode);
    }

    public final boolean hasDefaultAction() {
        return native_hasDefaultAction(this.mKeyCode);
    }

    @Override // android.view.InputEvent
    public final int getDeviceId() {
        return this.mDeviceId;
    }

    @Override // android.view.InputEvent
    public final int getSource() {
        return this.mSource;
    }

    @Override // android.view.InputEvent
    public final void setSource(int i) {
        this.mSource = i;
    }

    public final int getMetaState() {
        return this.mMetaState;
    }

    public final int getModifiers() {
        return normalizeMetaState(this.mMetaState) & META_MODIFIER_MASK;
    }

    public final int getFlags() {
        return this.mFlags;
    }

    public static boolean metaStateHasNoModifiers(int i) {
        return (normalizeMetaState(i) & META_MODIFIER_MASK) == 0;
    }

    public static boolean metaStateHasModifiers(int i, int i2) {
        if ((META_INVALID_MODIFIER_MASK & i2) == 0) {
            return metaStateFilterDirectionalModifiers(metaStateFilterDirectionalModifiers(metaStateFilterDirectionalModifiers(metaStateFilterDirectionalModifiers(normalizeMetaState(i) & META_MODIFIER_MASK, i2, 1, 64, 128), i2, 2, 16, 32), i2, 4096, 8192, 16384), i2, 65536, 131072, 262144) == i2;
        }
        throw new IllegalArgumentException("modifiers must not contain META_CAPS_LOCK_ON, META_NUM_LOCK_ON, META_SCROLL_LOCK_ON, META_CAP_LOCKED, META_ALT_LOCKED, META_SYM_LOCKED, or META_SELECTING");
    }

    private static int metaStateFilterDirectionalModifiers(int i, int i2, int i3, int i4, int i5) {
        int i6;
        boolean z = (i2 & i3) != 0;
        int i7 = i4 | i5;
        boolean z2 = (i2 & i7) != 0;
        if (z) {
            if (z2) {
                throw new IllegalArgumentException("modifiers must not contain " + metaStateToString(i3) + " combined with " + metaStateToString(i4) + " or " + metaStateToString(i5));
            }
            i6 = ~i7;
        } else {
            if (!z2) {
                return i;
            }
            i6 = ~i3;
        }
        return i & i6;
    }

    public final boolean hasNoModifiers() {
        return metaStateHasNoModifiers(this.mMetaState);
    }

    public final boolean hasModifiers(int i) {
        return metaStateHasModifiers(this.mMetaState, i);
    }

    public final boolean isAltPressed() {
        return (this.mMetaState & 2) != 0;
    }

    public final boolean isShiftPressed() {
        return (this.mMetaState & 1) != 0;
    }

    public final boolean isSymPressed() {
        return (this.mMetaState & 4) != 0;
    }

    public final boolean isCtrlPressed() {
        return (this.mMetaState & 4096) != 0;
    }

    public final boolean isMetaPressed() {
        return (this.mMetaState & 65536) != 0;
    }

    public final boolean isFunctionPressed() {
        return (this.mMetaState & 8) != 0;
    }

    public final boolean isCapsLockOn() {
        return (this.mMetaState & 1048576) != 0;
    }

    public final boolean isNumLockOn() {
        return (this.mMetaState & 2097152) != 0;
    }

    public final boolean isScrollLockOn() {
        return (this.mMetaState & 4194304) != 0;
    }

    public final int getAction() {
        return this.mAction;
    }

    public final boolean isCanceled() {
        return (this.mFlags & 32) != 0;
    }

    public final void startTracking() {
        this.mFlags |= 1073741824;
    }

    public final boolean isTracking() {
        return (this.mFlags & 512) != 0;
    }

    public final boolean isLongPress() {
        return (this.mFlags & 128) != 0;
    }

    public final int getKeyCode() {
        return this.mKeyCode;
    }

    public final String getCharacters() {
        return this.mCharacters;
    }

    public final int getScanCode() {
        return this.mScanCode;
    }

    public final int getRepeatCount() {
        return this.mRepeatCount;
    }

    public final long getDownTime() {
        return this.mDownTime;
    }

    @Override // android.view.InputEvent
    public final long getEventTime() {
        return this.mEventTime;
    }

    @Override // android.view.InputEvent
    public final long getEventTimeNano() {
        return this.mEventTime * 1000000;
    }

    @Deprecated
    public final int getKeyboardDevice() {
        return this.mDeviceId;
    }

    public final KeyCharacterMap getKeyCharacterMap() {
        return KeyCharacterMap.load(this.mDeviceId);
    }

    public char getDisplayLabel() {
        return getKeyCharacterMap().getDisplayLabel(this.mKeyCode);
    }

    public int getUnicodeChar() {
        return getUnicodeChar(this.mMetaState);
    }

    public int getUnicodeChar(int i) {
        return getKeyCharacterMap().get(this.mKeyCode, i);
    }

    @Deprecated
    public boolean getKeyData(KeyCharacterMap.KeyData keyData) {
        return getKeyCharacterMap().getKeyData(this.mKeyCode, keyData);
    }

    public char getMatch(char[] cArr) {
        return getMatch(cArr, 0);
    }

    public char getMatch(char[] cArr, int i) {
        return getKeyCharacterMap().getMatch(this.mKeyCode, cArr, i);
    }

    public char getNumber() {
        return getKeyCharacterMap().getNumber(this.mKeyCode);
    }

    public boolean isPrintingKey() {
        return getKeyCharacterMap().isPrintingKey(this.mKeyCode);
    }

    @Deprecated
    public final boolean dispatch(Callback callback) {
        return dispatch(callback, null, null);
    }

    public final boolean dispatch(Callback callback, DispatcherState dispatcherState, Object obj) {
        int i = this.mAction;
        boolean z = true;
        if (i == 0) {
            this.mFlags &= -1073741825;
            boolean zOnKeyDown = callback.onKeyDown(this.mKeyCode, this);
            if (dispatcherState == null) {
                return zOnKeyDown;
            }
            if (zOnKeyDown && this.mRepeatCount == 0 && (this.mFlags & 1073741824) != 0) {
                dispatcherState.startTracking(this, obj);
                return zOnKeyDown;
            }
            if (!isLongPress() || !dispatcherState.isTracking(this)) {
                return zOnKeyDown;
            }
            try {
                if (callback.onKeyLongPress(this.mKeyCode, this)) {
                    dispatcherState.performedLongPress(this);
                } else {
                    z = zOnKeyDown;
                }
                return z;
            } catch (AbstractMethodError unused) {
                return zOnKeyDown;
            }
        }
        if (i == 1) {
            if (dispatcherState != null) {
                dispatcherState.handleUpEvent(this);
            }
            return callback.onKeyUp(this.mKeyCode, this);
        }
        boolean zOnKeyDown2 = false;
        if (i != 2) {
            return false;
        }
        int i2 = this.mRepeatCount;
        int i3 = this.mKeyCode;
        if (callback.onKeyMultiple(i3, i2, this)) {
            return true;
        }
        if (i3 != 0) {
            this.mAction = 0;
            this.mRepeatCount = 0;
            zOnKeyDown2 = callback.onKeyDown(i3, this);
            if (zOnKeyDown2) {
                this.mAction = 1;
                callback.onKeyUp(i3, this);
            }
            this.mAction = 2;
            this.mRepeatCount = i2;
        }
        return zOnKeyDown2;
    }

    public static class DispatcherState {
        SparseIntArray mActiveLongPresses = new SparseIntArray();
        int mDownKeyCode;
        Object mDownTarget;

        public void reset() {
            this.mDownKeyCode = 0;
            this.mDownTarget = null;
            this.mActiveLongPresses.clear();
        }

        public void reset(Object obj) {
            if (this.mDownTarget == obj) {
                this.mDownKeyCode = 0;
                this.mDownTarget = null;
            }
        }

        public void startTracking(KeyEvent keyEvent, Object obj) {
            if (keyEvent.getAction() != 0) {
                throw new IllegalArgumentException("Can only start tracking on a down event");
            }
            this.mDownKeyCode = keyEvent.getKeyCode();
            this.mDownTarget = obj;
        }

        public boolean isTracking(KeyEvent keyEvent) {
            return this.mDownKeyCode == keyEvent.getKeyCode();
        }

        public void performedLongPress(KeyEvent keyEvent) {
            this.mActiveLongPresses.put(keyEvent.getKeyCode(), 1);
        }

        public void handleUpEvent(KeyEvent keyEvent) {
            int keyCode = keyEvent.getKeyCode();
            int iIndexOfKey = this.mActiveLongPresses.indexOfKey(keyCode);
            if (iIndexOfKey >= 0) {
                KeyEvent.access$076(keyEvent, MediaProperties.HEIGHT_288);
                this.mActiveLongPresses.removeAt(iIndexOfKey);
            }
            if (this.mDownKeyCode == keyCode) {
                KeyEvent.access$076(keyEvent, 512);
                this.mDownKeyCode = 0;
                this.mDownTarget = null;
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("KeyEvent { action=").append(actionToString(this.mAction));
        sb.append(", keyCode=").append(keyCodeToString(this.mKeyCode));
        sb.append(", scanCode=").append(this.mScanCode);
        if (this.mCharacters != null) {
            sb.append(", characters=\"").append(this.mCharacters).append("\"");
        }
        sb.append(", metaState=").append(metaStateToString(this.mMetaState));
        sb.append(", flags=0x").append(Integer.toHexString(this.mFlags));
        sb.append(", repeatCount=").append(this.mRepeatCount);
        sb.append(", eventTime=").append(this.mEventTime);
        sb.append(", downTime=").append(this.mDownTime);
        sb.append(", deviceId=").append(this.mDeviceId);
        sb.append(", source=0x").append(Integer.toHexString(this.mSource));
        sb.append(" }");
        return sb.toString();
    }

    public static String actionToString(int i) {
        return i != 0 ? i != 1 ? i != 2 ? Integer.toString(i) : "ACTION_MULTIPLE" : "ACTION_UP" : "ACTION_DOWN";
    }

    public static String keyCodeToString(int i) {
        String str = KEYCODE_SYMBOLIC_NAMES.get(i);
        return str != null ? str : Integer.toString(i);
    }

    public static int keyCodeFromString(String str) {
        if (str == null) {
            throw new IllegalArgumentException("symbolicName must not be null");
        }
        int size = KEYCODE_SYMBOLIC_NAMES.size();
        for (int i = 0; i < size; i++) {
            if (str.equals(KEYCODE_SYMBOLIC_NAMES.valueAt(i))) {
                return i;
            }
        }
        try {
            return Integer.parseInt(str, 10);
        } catch (NumberFormatException unused) {
            return 0;
        }
    }

    public static String metaStateToString(int i) {
        if (i == 0) {
            return "0";
        }
        StringBuilder sb = null;
        int i2 = 0;
        while (i != 0) {
            boolean z = (i & 1) != 0;
            i >>>= 1;
            if (z) {
                String str = META_SYMBOLIC_NAMES[i2];
                if (sb != null) {
                    sb.append('|');
                    sb.append(str);
                } else {
                    if (i == 0) {
                        return str;
                    }
                    sb = new StringBuilder(str);
                }
            }
            i2++;
        }
        return sb.toString();
    }

    public static KeyEvent createFromParcelBody(Parcel parcel) {
        return new KeyEvent(parcel);
    }

    private KeyEvent(Parcel parcel) {
        this.mDeviceId = parcel.readInt();
        this.mSource = parcel.readInt();
        this.mAction = parcel.readInt();
        this.mKeyCode = parcel.readInt();
        this.mRepeatCount = parcel.readInt();
        this.mMetaState = parcel.readInt();
        this.mScanCode = parcel.readInt();
        this.mFlags = parcel.readInt();
        this.mDownTime = parcel.readLong();
        this.mEventTime = parcel.readLong();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(2);
        parcel.writeInt(this.mDeviceId);
        parcel.writeInt(this.mSource);
        parcel.writeInt(this.mAction);
        parcel.writeInt(this.mKeyCode);
        parcel.writeInt(this.mRepeatCount);
        parcel.writeInt(this.mMetaState);
        parcel.writeInt(this.mScanCode);
        parcel.writeInt(this.mFlags);
        parcel.writeLong(this.mDownTime);
        parcel.writeLong(this.mEventTime);
    }
}
