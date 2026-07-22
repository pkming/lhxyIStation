package android.view;

import android.bluetooth.BluetoothClass;
import android.hardware.input.InputManager;
import android.media.MediaPlayer;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AndroidRuntimeException;
import android.util.SparseIntArray;
import java.text.Normalizer;
import org.apache.commons.net.telnet.TelnetCommand;

/* JADX INFO: loaded from: classes.dex */
public class KeyCharacterMap implements Parcelable {
    private static final int ACCENT_ACUTE = 180;
    private static final int ACCENT_BREVE = 728;
    private static final int ACCENT_CARON = 711;
    private static final int ACCENT_CEDILLA = 184;
    private static final int ACCENT_CIRCUMFLEX = 710;
    private static final int ACCENT_CIRCUMFLEX_LEGACY = 94;
    private static final int ACCENT_COMMA_ABOVE = 8125;
    private static final int ACCENT_COMMA_ABOVE_RIGHT = 700;
    private static final int ACCENT_DOT_ABOVE = 729;
    private static final int ACCENT_DOT_BELOW = 46;
    private static final int ACCENT_DOUBLE_ACUTE = 733;
    private static final int ACCENT_GRAVE = 715;
    private static final int ACCENT_GRAVE_LEGACY = 96;
    private static final int ACCENT_HOOK_ABOVE = 704;
    private static final int ACCENT_HORN = 39;
    private static final int ACCENT_MACRON = 175;
    private static final int ACCENT_MACRON_BELOW = 717;
    private static final int ACCENT_OGONEK = 731;
    private static final int ACCENT_REVERSED_COMMA_ABOVE = 701;
    private static final int ACCENT_RING_ABOVE = 730;
    private static final int ACCENT_STROKE = 45;
    private static final int ACCENT_TILDE = 732;
    private static final int ACCENT_TILDE_LEGACY = 126;
    private static final int ACCENT_TURNED_COMMA_ABOVE = 699;
    private static final int ACCENT_UMLAUT = 168;
    private static final int ACCENT_VERTICAL_LINE_ABOVE = 712;
    private static final int ACCENT_VERTICAL_LINE_BELOW = 716;
    public static final int ALPHA = 3;

    @Deprecated
    public static final int BUILT_IN_KEYBOARD = 0;
    private static final int CHAR_SPACE = 32;
    public static final int COMBINING_ACCENT = Integer.MIN_VALUE;
    public static final int COMBINING_ACCENT_MASK = Integer.MAX_VALUE;
    public static final Parcelable.Creator<KeyCharacterMap> CREATOR;
    public static final int FULL = 4;
    public static final char HEX_INPUT = 61184;
    public static final int MODIFIER_BEHAVIOR_CHORDED = 0;
    public static final int MODIFIER_BEHAVIOR_CHORDED_OR_TOGGLED = 1;
    public static final int NUMERIC = 1;
    public static final char PICKER_DIALOG_INPUT = 61185;
    public static final int PREDICTIVE = 2;
    public static final int SPECIAL_FUNCTION = 5;
    public static final int VIRTUAL_KEYBOARD = -1;
    private static final SparseIntArray sAccentToCombining;
    private static final SparseIntArray sCombiningToAccent;
    private static final StringBuilder sDeadKeyBuilder;
    private static final SparseIntArray sDeadKeyCache;
    private int mPtr;

    @Deprecated
    public static class KeyData {
        public static final int META_LENGTH = 4;
        public char displayLabel;
        public char[] meta = new char[4];
        public char number;
    }

    private static native void nativeDispose(int i);

    private static native char nativeGetCharacter(int i, int i2, int i3);

    private static native char nativeGetDisplayLabel(int i, int i2);

    private static native KeyEvent[] nativeGetEvents(int i, char[] cArr);

    private static native boolean nativeGetFallbackAction(int i, int i2, int i3, FallbackAction fallbackAction);

    private static native int nativeGetKeyboardType(int i);

    private static native char nativeGetMatch(int i, int i2, char[] cArr, int i3);

    private static native char nativeGetNumber(int i, int i2);

    private static native int nativeReadFromParcel(Parcel parcel);

    private static native void nativeWriteToParcel(int i, Parcel parcel);

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    static {
        SparseIntArray sparseIntArray = new SparseIntArray();
        sCombiningToAccent = sparseIntArray;
        SparseIntArray sparseIntArray2 = new SparseIntArray();
        sAccentToCombining = sparseIntArray2;
        addCombining(768, ACCENT_GRAVE);
        addCombining(769, 180);
        addCombining(770, ACCENT_CIRCUMFLEX);
        addCombining(771, ACCENT_TILDE);
        addCombining(772, 175);
        addCombining(774, ACCENT_BREVE);
        addCombining(775, ACCENT_DOT_ABOVE);
        addCombining(776, 168);
        addCombining(777, ACCENT_HOOK_ABOVE);
        addCombining(778, ACCENT_RING_ABOVE);
        addCombining(779, ACCENT_DOUBLE_ACUTE);
        addCombining(780, ACCENT_CARON);
        addCombining(781, ACCENT_VERTICAL_LINE_ABOVE);
        addCombining(786, ACCENT_TURNED_COMMA_ABOVE);
        addCombining(787, ACCENT_COMMA_ABOVE);
        addCombining(788, 701);
        addCombining(789, 700);
        addCombining(795, 39);
        addCombining(MediaPlayer.MEDIA_INFO_EXTERNAL_METADATA_UPDATE, 46);
        addCombining(807, 184);
        addCombining(808, ACCENT_OGONEK);
        addCombining(809, ACCENT_VERTICAL_LINE_BELOW);
        addCombining(817, ACCENT_MACRON_BELOW);
        addCombining(821, 45);
        sparseIntArray.append(832, ACCENT_GRAVE);
        sparseIntArray.append(833, 180);
        sparseIntArray.append(835, ACCENT_COMMA_ABOVE);
        sparseIntArray2.append(96, 768);
        sparseIntArray2.append(94, 770);
        sparseIntArray2.append(126, 771);
        sDeadKeyCache = new SparseIntArray();
        sDeadKeyBuilder = new StringBuilder();
        addDeadKey(45, 68, BluetoothClass.Device.COMPUTER_HANDHELD_PC_PDA);
        addDeadKey(45, 71, 484);
        addDeadKey(45, 72, 294);
        addDeadKey(45, 73, 407);
        addDeadKey(45, 76, 321);
        addDeadKey(45, 79, 216);
        addDeadKey(45, 84, 358);
        addDeadKey(45, 100, 273);
        addDeadKey(45, 103, 485);
        addDeadKey(45, 104, 295);
        addDeadKey(45, 105, 616);
        addDeadKey(45, 108, 322);
        addDeadKey(45, 111, TelnetCommand.EL);
        addDeadKey(45, 116, 359);
        CREATOR = new Parcelable.Creator<KeyCharacterMap>() { // from class: android.view.KeyCharacterMap.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public KeyCharacterMap createFromParcel(Parcel parcel) {
                return new KeyCharacterMap(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public KeyCharacterMap[] newArray(int i) {
                return new KeyCharacterMap[i];
            }
        };
    }

    private static void addCombining(int i, int i2) {
        sCombiningToAccent.append(i, i2);
        sAccentToCombining.append(i2, i);
    }

    private static void addDeadKey(int i, int i2, int i3) {
        int i4 = sAccentToCombining.get(i);
        if (i4 == 0) {
            throw new IllegalStateException("Invalid dead key declaration.");
        }
        sDeadKeyCache.put((i4 << 16) | i2, i3);
    }

    private KeyCharacterMap(Parcel parcel) {
        if (parcel == null) {
            throw new IllegalArgumentException("parcel must not be null");
        }
        int iNativeReadFromParcel = nativeReadFromParcel(parcel);
        this.mPtr = iNativeReadFromParcel;
        if (iNativeReadFromParcel == 0) {
            throw new RuntimeException("Could not read KeyCharacterMap from parcel.");
        }
    }

    private KeyCharacterMap(int i) {
        this.mPtr = i;
    }

    protected void finalize() throws Throwable {
        int i = this.mPtr;
        if (i != 0) {
            nativeDispose(i);
            this.mPtr = 0;
        }
    }

    public static KeyCharacterMap load(int i) {
        InputManager inputManager = InputManager.getInstance();
        InputDevice inputDevice = inputManager.getInputDevice(i);
        if (inputDevice == null && (inputDevice = inputManager.getInputDevice(-1)) == null) {
            throw new UnavailableException("Could not load key character map for device " + i);
        }
        return inputDevice.getKeyCharacterMap();
    }

    public int get(int i, int i2) {
        char cNativeGetCharacter = nativeGetCharacter(this.mPtr, i, KeyEvent.normalizeMetaState(i2));
        int i3 = sCombiningToAccent.get(cNativeGetCharacter);
        return i3 != 0 ? Integer.MIN_VALUE | i3 : cNativeGetCharacter;
    }

    public FallbackAction getFallbackAction(int i, int i2) {
        FallbackAction fallbackActionObtain = FallbackAction.obtain();
        if (nativeGetFallbackAction(this.mPtr, i, KeyEvent.normalizeMetaState(i2), fallbackActionObtain)) {
            fallbackActionObtain.metaState = KeyEvent.normalizeMetaState(fallbackActionObtain.metaState);
            return fallbackActionObtain;
        }
        fallbackActionObtain.recycle();
        return null;
    }

    public char getNumber(int i) {
        return nativeGetNumber(this.mPtr, i);
    }

    public char getMatch(int i, char[] cArr) {
        return getMatch(i, cArr, 0);
    }

    public char getMatch(int i, char[] cArr, int i2) {
        if (cArr == null) {
            throw new IllegalArgumentException("chars must not be null.");
        }
        return nativeGetMatch(this.mPtr, i, cArr, KeyEvent.normalizeMetaState(i2));
    }

    public char getDisplayLabel(int i) {
        return nativeGetDisplayLabel(this.mPtr, i);
    }

    public static int getDeadChar(int i, int i2) {
        int i3;
        if (i2 == i || 32 == i2) {
            return i;
        }
        int i4 = sAccentToCombining.get(i);
        if (i4 == 0) {
            return 0;
        }
        int i5 = (i4 << 16) | i2;
        SparseIntArray sparseIntArray = sDeadKeyCache;
        synchronized (sparseIntArray) {
            i3 = sparseIntArray.get(i5, -1);
            if (i3 == -1) {
                StringBuilder sb = sDeadKeyBuilder;
                sb.setLength(0);
                sb.append((char) i2);
                sb.append((char) i4);
                String strNormalize = Normalizer.normalize(sb, Normalizer.Form.NFC);
                int iCodePointAt = strNormalize.codePointCount(0, strNormalize.length()) == 1 ? strNormalize.codePointAt(0) : 0;
                sparseIntArray.put(i5, iCodePointAt);
                i3 = iCodePointAt;
            }
        }
        return i3;
    }

    @Deprecated
    public boolean getKeyData(int i, KeyData keyData) {
        if (keyData.meta.length < 4) {
            throw new IndexOutOfBoundsException("results.meta.length must be >= 4");
        }
        char cNativeGetDisplayLabel = nativeGetDisplayLabel(this.mPtr, i);
        if (cNativeGetDisplayLabel == 0) {
            return false;
        }
        keyData.displayLabel = cNativeGetDisplayLabel;
        keyData.number = nativeGetNumber(this.mPtr, i);
        keyData.meta[0] = nativeGetCharacter(this.mPtr, i, 0);
        keyData.meta[1] = nativeGetCharacter(this.mPtr, i, 1);
        keyData.meta[2] = nativeGetCharacter(this.mPtr, i, 2);
        keyData.meta[3] = nativeGetCharacter(this.mPtr, i, 3);
        return true;
    }

    public KeyEvent[] getEvents(char[] cArr) {
        if (cArr == null) {
            throw new IllegalArgumentException("chars must not be null.");
        }
        return nativeGetEvents(this.mPtr, cArr);
    }

    public boolean isPrintingKey(int i) {
        switch (Character.getType(nativeGetDisplayLabel(this.mPtr, i))) {
            case 12:
            case 13:
            case 14:
            case 15:
            case 16:
                return false;
            default:
                return true;
        }
    }

    public int getKeyboardType() {
        return nativeGetKeyboardType(this.mPtr);
    }

    public int getModifierBehavior() {
        int keyboardType = getKeyboardType();
        return (keyboardType == 4 || keyboardType == 5) ? 0 : 1;
    }

    public static boolean deviceHasKey(int i) {
        return InputManager.getInstance().deviceHasKeys(new int[]{i})[0];
    }

    public static boolean[] deviceHasKeys(int[] iArr) {
        return InputManager.getInstance().deviceHasKeys(iArr);
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        if (parcel == null) {
            throw new IllegalArgumentException("parcel must not be null");
        }
        nativeWriteToParcel(this.mPtr, parcel);
    }

    public static class UnavailableException extends AndroidRuntimeException {
        public UnavailableException(String str) {
            super(str);
        }
    }

    public static final class FallbackAction {
        private static final int MAX_RECYCLED = 10;
        private static FallbackAction sRecycleBin;
        private static final Object sRecycleLock = new Object();
        private static int sRecycledCount;
        public int keyCode;
        public int metaState;
        private FallbackAction next;

        private FallbackAction() {
        }

        public static FallbackAction obtain() {
            FallbackAction fallbackAction;
            synchronized (sRecycleLock) {
                fallbackAction = sRecycleBin;
                if (fallbackAction == null) {
                    fallbackAction = new FallbackAction();
                } else {
                    sRecycleBin = fallbackAction.next;
                    sRecycledCount--;
                    fallbackAction.next = null;
                }
            }
            return fallbackAction;
        }

        public void recycle() {
            synchronized (sRecycleLock) {
                int i = sRecycledCount;
                if (i < 10) {
                    this.next = sRecycleBin;
                    sRecycleBin = this;
                    sRecycledCount = i + 1;
                } else {
                    this.next = null;
                }
            }
        }
    }
}
