package android.view;

import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewDebug;
import android.view.ViewGroup;

/* JADX INFO: loaded from: classes.dex */
public interface WindowManager extends ViewManager {
    Display getDefaultDisplay();

    void removeViewImmediate(View view);

    public static class BadTokenException extends RuntimeException {
        public BadTokenException() {
        }

        public BadTokenException(String str) {
            super(str);
        }
    }

    public static class InvalidDisplayException extends RuntimeException {
        public InvalidDisplayException() {
        }

        public InvalidDisplayException(String str) {
            super(str);
        }
    }

    public static class LayoutParams extends ViewGroup.LayoutParams implements Parcelable {
        public static final int ALPHA_CHANGED = 128;
        public static final int ANIMATION_CHANGED = 16;
        public static final float BRIGHTNESS_OVERRIDE_FULL = 1.0f;
        public static final float BRIGHTNESS_OVERRIDE_NONE = -1.0f;
        public static final float BRIGHTNESS_OVERRIDE_OFF = 0.0f;
        public static final int BUTTON_BRIGHTNESS_CHANGED = 8192;
        public static final Parcelable.Creator<LayoutParams> CREATOR = new Parcelable.Creator<LayoutParams>() { // from class: android.view.WindowManager.LayoutParams.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public LayoutParams createFromParcel(Parcel parcel) {
                return new LayoutParams(parcel);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public LayoutParams[] newArray(int i) {
                return new LayoutParams[i];
            }
        };
        public static final int DIM_AMOUNT_CHANGED = 32;
        public static final int EVERYTHING_CHANGED = -1;
        public static final int FIRST_APPLICATION_WINDOW = 1;
        public static final int FIRST_SUB_WINDOW = 1000;
        public static final int FIRST_SYSTEM_WINDOW = 2000;
        public static final int FLAGS_CHANGED = 4;
        public static final int FLAG_ALLOW_LOCK_WHILE_SCREEN_ON = 1;
        public static final int FLAG_ALT_FOCUSABLE_IM = 131072;

        @Deprecated
        public static final int FLAG_BLUR_BEHIND = 4;
        public static final int FLAG_DIM_BEHIND = 2;
        public static final int FLAG_DISMISS_KEYGUARD = 4194304;

        @Deprecated
        public static final int FLAG_DITHER = 4096;
        public static final int FLAG_FORCE_NOT_FULLSCREEN = 2048;
        public static final int FLAG_FULLSCREEN = 1024;
        public static final int FLAG_HARDWARE_ACCELERATED = 16777216;
        public static final int FLAG_IGNORE_CHEEK_PRESSES = 32768;
        public static final int FLAG_KEEP_SCREEN_ON = 128;
        public static final int FLAG_LAYOUT_INSET_DECOR = 65536;
        public static final int FLAG_LAYOUT_IN_OVERSCAN = 33554432;
        public static final int FLAG_LAYOUT_IN_SCREEN = 256;
        public static final int FLAG_LAYOUT_NO_LIMITS = 512;
        public static final int FLAG_LOCAL_FOCUS_MODE = 268435456;
        public static final int FLAG_NEEDS_MENU_KEY = 1073741824;
        public static final int FLAG_NOT_FOCUSABLE = 8;
        public static final int FLAG_NOT_TOUCHABLE = 16;
        public static final int FLAG_NOT_TOUCH_MODAL = 32;
        public static final int FLAG_SCALED = 16384;
        public static final int FLAG_SECURE = 8192;
        public static final int FLAG_SHOW_WALLPAPER = 1048576;
        public static final int FLAG_SHOW_WHEN_LOCKED = 524288;
        public static final int FLAG_SLIPPERY = 536870912;
        public static final int FLAG_SPLIT_TOUCH = 8388608;
        public static final int FLAG_TOUCHABLE_WHEN_WAKING = 64;
        public static final int FLAG_TRANSLUCENT_NAVIGATION = 134217728;
        public static final int FLAG_TRANSLUCENT_STATUS = 67108864;
        public static final int FLAG_TURN_SCREEN_ON = 2097152;
        public static final int FLAG_WATCH_OUTSIDE_TOUCH = 262144;
        public static final int FORMAT_CHANGED = 8;
        public static final int INPUT_FEATURES_CHANGED = 65536;
        public static final int INPUT_FEATURE_DISABLE_POINTER_GESTURES = 1;
        public static final int INPUT_FEATURE_DISABLE_USER_ACTIVITY = 4;
        public static final int INPUT_FEATURE_NO_INPUT_CHANNEL = 2;
        public static final int LAST_APPLICATION_WINDOW = 99;
        public static final int LAST_SUB_WINDOW = 1999;
        public static final int LAST_SYSTEM_WINDOW = 2999;
        public static final int LAYOUT_CHANGED = 1;
        public static final int MEMORY_TYPE_CHANGED = 256;

        @Deprecated
        public static final int MEMORY_TYPE_GPU = 2;

        @Deprecated
        public static final int MEMORY_TYPE_HARDWARE = 1;

        @Deprecated
        public static final int MEMORY_TYPE_NORMAL = 0;

        @Deprecated
        public static final int MEMORY_TYPE_PUSH_BUFFERS = 3;
        public static final int PRIVATE_FLAGS_CHANGED = 131072;
        public static final int PRIVATE_FLAG_COMPATIBLE_WINDOW = 128;
        public static final int PRIVATE_FLAG_FAKE_HARDWARE_ACCELERATED = 1;
        public static final int PRIVATE_FLAG_FORCE_HARDWARE_ACCELERATED = 2;
        public static final int PRIVATE_FLAG_FORCE_SHOW_NAV_BAR = 32;
        public static final int PRIVATE_FLAG_INHERIT_TRANSLUCENT_DECOR = 512;
        public static final int PRIVATE_FLAG_NO_MOVE_ANIMATION = 64;
        public static final int PRIVATE_FLAG_SET_NEEDS_MENU_KEY = 8;
        public static final int PRIVATE_FLAG_SHOW_FOR_ALL_USERS = 16;
        public static final int PRIVATE_FLAG_SYSTEM_ERROR = 256;
        public static final int PRIVATE_FLAG_WANTS_OFFSET_NOTIFICATIONS = 4;
        public static final int ROTATION_ANIMATION_CHANGED = 4096;
        public static final int ROTATION_ANIMATION_CROSSFADE = 1;
        public static final int ROTATION_ANIMATION_JUMPCUT = 2;
        public static final int ROTATION_ANIMATION_ROTATE = 0;
        public static final int SCREEN_BRIGHTNESS_CHANGED = 2048;
        public static final int SCREEN_ORIENTATION_CHANGED = 1024;
        public static final int SOFT_INPUT_ADJUST_NOTHING = 48;
        public static final int SOFT_INPUT_ADJUST_PAN = 32;
        public static final int SOFT_INPUT_ADJUST_RESIZE = 16;
        public static final int SOFT_INPUT_ADJUST_UNSPECIFIED = 0;
        public static final int SOFT_INPUT_IS_FORWARD_NAVIGATION = 256;
        public static final int SOFT_INPUT_MASK_ADJUST = 240;
        public static final int SOFT_INPUT_MASK_STATE = 15;
        public static final int SOFT_INPUT_MODE_CHANGED = 512;
        public static final int SOFT_INPUT_STATE_ALWAYS_HIDDEN = 3;
        public static final int SOFT_INPUT_STATE_ALWAYS_VISIBLE = 5;
        public static final int SOFT_INPUT_STATE_HIDDEN = 2;
        public static final int SOFT_INPUT_STATE_UNCHANGED = 1;
        public static final int SOFT_INPUT_STATE_UNSPECIFIED = 0;
        public static final int SOFT_INPUT_STATE_VISIBLE = 4;
        public static final int SYSTEM_UI_LISTENER_CHANGED = 32768;
        public static final int SYSTEM_UI_VISIBILITY_CHANGED = 16384;
        public static final int TITLE_CHANGED = 64;
        public static final int TRANSLUCENT_FLAGS_CHANGED = 524288;
        public static final int TYPE_APPLICATION = 2;
        public static final int TYPE_APPLICATION_ATTACHED_DIALOG = 1003;
        public static final int TYPE_APPLICATION_MEDIA = 1001;
        public static final int TYPE_APPLICATION_MEDIA_OVERLAY = 1004;
        public static final int TYPE_APPLICATION_PANEL = 1000;
        public static final int TYPE_APPLICATION_STARTING = 3;
        public static final int TYPE_APPLICATION_SUB_PANEL = 1002;
        public static final int TYPE_BASE_APPLICATION = 1;
        public static final int TYPE_BOOT_PROGRESS = 2021;
        public static final int TYPE_CHANGED = 2;
        public static final int TYPE_DISPLAY_OVERLAY = 2026;
        public static final int TYPE_DRAG = 2016;
        public static final int TYPE_DREAM = 2023;
        public static final int TYPE_HIDDEN_NAV_CONSUMER = 2022;
        public static final int TYPE_INPUT_METHOD = 2011;
        public static final int TYPE_INPUT_METHOD_DIALOG = 2012;
        public static final int TYPE_KEYGUARD = 2004;
        public static final int TYPE_KEYGUARD_DIALOG = 2009;
        public static final int TYPE_KEYGUARD_SCRIM = 2029;
        public static final int TYPE_MAGNIFICATION_OVERLAY = 2027;
        public static final int TYPE_NAVIGATION_BAR = 2019;
        public static final int TYPE_NAVIGATION_BAR_PANEL = 2024;
        public static final int TYPE_PHONE = 2002;
        public static final int TYPE_POINTER = 2018;
        public static final int TYPE_PRIORITY_PHONE = 2007;
        public static final int TYPE_PRIVATE_PRESENTATION = 2030;
        public static final int TYPE_RECENTS_OVERLAY = 2028;
        public static final int TYPE_SEARCH_BAR = 2001;
        public static final int TYPE_SECURE_SYSTEM_OVERLAY = 2015;
        public static final int TYPE_STATUS_BAR = 2000;
        public static final int TYPE_STATUS_BAR_PANEL = 2014;
        public static final int TYPE_STATUS_BAR_SUB_PANEL = 2017;
        public static final int TYPE_SYSTEM_ALERT = 2003;
        public static final int TYPE_SYSTEM_DIALOG = 2008;
        public static final int TYPE_SYSTEM_ERROR = 2010;
        public static final int TYPE_SYSTEM_OVERLAY = 2006;
        public static final int TYPE_TOAST = 2005;
        public static final int TYPE_UNIVERSE_BACKGROUND = 2025;
        public static final int TYPE_VOLUME_OVERLAY = 2020;
        public static final int TYPE_WALLPAPER = 2013;
        public static final int USER_ACTIVITY_TIMEOUT_CHANGED = 262144;
        public float alpha;
        public float buttonBrightness;
        public float dimAmount;

        @ViewDebug.ExportedProperty(flagMapping = {@ViewDebug.FlagToString(equals = 1, mask = 1, name = "FLAG_ALLOW_LOCK_WHILE_SCREEN_ON"), @ViewDebug.FlagToString(equals = 2, mask = 2, name = "FLAG_DIM_BEHIND"), @ViewDebug.FlagToString(equals = 4, mask = 4, name = "FLAG_BLUR_BEHIND"), @ViewDebug.FlagToString(equals = 8, mask = 8, name = "FLAG_NOT_FOCUSABLE"), @ViewDebug.FlagToString(equals = 16, mask = 16, name = "FLAG_NOT_TOUCHABLE"), @ViewDebug.FlagToString(equals = 32, mask = 32, name = "FLAG_NOT_TOUCH_MODAL"), @ViewDebug.FlagToString(equals = 64, mask = 64, name = "FLAG_TOUCHABLE_WHEN_WAKING"), @ViewDebug.FlagToString(equals = 128, mask = 128, name = "FLAG_KEEP_SCREEN_ON"), @ViewDebug.FlagToString(equals = 256, mask = 256, name = "FLAG_LAYOUT_IN_SCREEN"), @ViewDebug.FlagToString(equals = 512, mask = 512, name = "FLAG_LAYOUT_NO_LIMITS"), @ViewDebug.FlagToString(equals = 1024, mask = 1024, name = "FLAG_FULLSCREEN"), @ViewDebug.FlagToString(equals = 2048, mask = 2048, name = "FLAG_FORCE_NOT_FULLSCREEN"), @ViewDebug.FlagToString(equals = 4096, mask = 4096, name = "FLAG_DITHER"), @ViewDebug.FlagToString(equals = 8192, mask = 8192, name = "FLAG_SECURE"), @ViewDebug.FlagToString(equals = 16384, mask = 16384, name = "FLAG_SCALED"), @ViewDebug.FlagToString(equals = 32768, mask = 32768, name = "FLAG_IGNORE_CHEEK_PRESSES"), @ViewDebug.FlagToString(equals = 65536, mask = 65536, name = "FLAG_LAYOUT_INSET_DECOR"), @ViewDebug.FlagToString(equals = 131072, mask = 131072, name = "FLAG_ALT_FOCUSABLE_IM"), @ViewDebug.FlagToString(equals = 262144, mask = 262144, name = "FLAG_WATCH_OUTSIDE_TOUCH"), @ViewDebug.FlagToString(equals = 524288, mask = 524288, name = "FLAG_SHOW_WHEN_LOCKED"), @ViewDebug.FlagToString(equals = 1048576, mask = 1048576, name = "FLAG_SHOW_WALLPAPER"), @ViewDebug.FlagToString(equals = 2097152, mask = 2097152, name = "FLAG_TURN_SCREEN_ON"), @ViewDebug.FlagToString(equals = 4194304, mask = 4194304, name = "FLAG_DISMISS_KEYGUARD"), @ViewDebug.FlagToString(equals = 8388608, mask = 8388608, name = "FLAG_SPLIT_TOUCH"), @ViewDebug.FlagToString(equals = 16777216, mask = 16777216, name = "FLAG_HARDWARE_ACCELERATED"), @ViewDebug.FlagToString(equals = 268435456, mask = 268435456, name = "FLAG_LOCAL_FOCUS_MODE"), @ViewDebug.FlagToString(equals = 67108864, mask = 67108864, name = "FLAG_TRANSLUCENT_STATUS"), @ViewDebug.FlagToString(equals = 134217728, mask = 134217728, name = "FLAG_TRANSLUCENT_NAVIGATION")})
        public int flags;
        public int format;
        public int gravity;
        public boolean hasSystemUiListeners;
        public float horizontalMargin;

        @ViewDebug.ExportedProperty
        public float horizontalWeight;
        public int inputFeatures;
        private int[] mCompatibilityParamsBackup;
        private CharSequence mTitle;

        @Deprecated
        public int memoryType;
        public String packageName;
        public int privateFlags;
        public int rotationAnimation;
        public float screenBrightness;
        public int screenOrientation;
        public int softInputMode;
        public int subtreeSystemUiVisibility;
        public int systemUiVisibility;
        public IBinder token;

        @ViewDebug.ExportedProperty(mapping = {@ViewDebug.IntToString(from = 1, to = "TYPE_BASE_APPLICATION"), @ViewDebug.IntToString(from = 2, to = "TYPE_APPLICATION"), @ViewDebug.IntToString(from = 3, to = "TYPE_APPLICATION_STARTING"), @ViewDebug.IntToString(from = 1000, to = "TYPE_APPLICATION_PANEL"), @ViewDebug.IntToString(from = 1001, to = "TYPE_APPLICATION_MEDIA"), @ViewDebug.IntToString(from = 1002, to = "TYPE_APPLICATION_SUB_PANEL"), @ViewDebug.IntToString(from = 1003, to = "TYPE_APPLICATION_ATTACHED_DIALOG"), @ViewDebug.IntToString(from = 1004, to = "TYPE_APPLICATION_MEDIA_OVERLAY"), @ViewDebug.IntToString(from = 2000, to = "TYPE_STATUS_BAR"), @ViewDebug.IntToString(from = 2001, to = "TYPE_SEARCH_BAR"), @ViewDebug.IntToString(from = 2002, to = "TYPE_PHONE"), @ViewDebug.IntToString(from = 2003, to = "TYPE_SYSTEM_ALERT"), @ViewDebug.IntToString(from = 2004, to = "TYPE_KEYGUARD"), @ViewDebug.IntToString(from = 2005, to = "TYPE_TOAST"), @ViewDebug.IntToString(from = 2006, to = "TYPE_SYSTEM_OVERLAY"), @ViewDebug.IntToString(from = 2007, to = "TYPE_PRIORITY_PHONE"), @ViewDebug.IntToString(from = 2008, to = "TYPE_SYSTEM_DIALOG"), @ViewDebug.IntToString(from = TYPE_KEYGUARD_DIALOG, to = "TYPE_KEYGUARD_DIALOG"), @ViewDebug.IntToString(from = 2010, to = "TYPE_SYSTEM_ERROR"), @ViewDebug.IntToString(from = 2011, to = "TYPE_INPUT_METHOD"), @ViewDebug.IntToString(from = 2012, to = "TYPE_INPUT_METHOD_DIALOG"), @ViewDebug.IntToString(from = 2013, to = "TYPE_WALLPAPER"), @ViewDebug.IntToString(from = 2014, to = "TYPE_STATUS_BAR_PANEL"), @ViewDebug.IntToString(from = 2015, to = "TYPE_SECURE_SYSTEM_OVERLAY"), @ViewDebug.IntToString(from = 2016, to = "TYPE_DRAG"), @ViewDebug.IntToString(from = 2017, to = "TYPE_STATUS_BAR_SUB_PANEL"), @ViewDebug.IntToString(from = 2018, to = "TYPE_POINTER"), @ViewDebug.IntToString(from = 2019, to = "TYPE_NAVIGATION_BAR"), @ViewDebug.IntToString(from = 2020, to = "TYPE_VOLUME_OVERLAY"), @ViewDebug.IntToString(from = 2021, to = "TYPE_BOOT_PROGRESS"), @ViewDebug.IntToString(from = 2022, to = "TYPE_HIDDEN_NAV_CONSUMER"), @ViewDebug.IntToString(from = 2023, to = "TYPE_DREAM"), @ViewDebug.IntToString(from = 2024, to = "TYPE_NAVIGATION_BAR_PANEL"), @ViewDebug.IntToString(from = TYPE_DISPLAY_OVERLAY, to = "TYPE_DISPLAY_OVERLAY"), @ViewDebug.IntToString(from = TYPE_MAGNIFICATION_OVERLAY, to = "TYPE_MAGNIFICATION_OVERLAY"), @ViewDebug.IntToString(from = 2030, to = "TYPE_PRIVATE_PRESENTATION")})
        public int type;
        public long userActivityTimeout;
        public float verticalMargin;

        @ViewDebug.ExportedProperty
        public float verticalWeight;
        public int windowAnimations;

        @ViewDebug.ExportedProperty
        public int x;

        @ViewDebug.ExportedProperty
        public int y;

        public static boolean mayUseInputMethod(int i) {
            int i2 = i & 131080;
            return i2 == 0 || i2 == 131080;
        }

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public LayoutParams() {
            super(-1, -1);
            this.alpha = 1.0f;
            this.dimAmount = 1.0f;
            this.screenBrightness = -1.0f;
            this.buttonBrightness = -1.0f;
            this.rotationAnimation = 0;
            this.token = null;
            this.packageName = null;
            this.screenOrientation = -1;
            this.userActivityTimeout = -1L;
            this.mCompatibilityParamsBackup = null;
            this.mTitle = "";
            this.type = 2;
            this.format = -1;
        }

        public LayoutParams(int i) {
            super(-1, -1);
            this.alpha = 1.0f;
            this.dimAmount = 1.0f;
            this.screenBrightness = -1.0f;
            this.buttonBrightness = -1.0f;
            this.rotationAnimation = 0;
            this.token = null;
            this.packageName = null;
            this.screenOrientation = -1;
            this.userActivityTimeout = -1L;
            this.mCompatibilityParamsBackup = null;
            this.mTitle = "";
            this.type = i;
            this.format = -1;
        }

        public LayoutParams(int i, int i2) {
            super(-1, -1);
            this.alpha = 1.0f;
            this.dimAmount = 1.0f;
            this.screenBrightness = -1.0f;
            this.buttonBrightness = -1.0f;
            this.rotationAnimation = 0;
            this.token = null;
            this.packageName = null;
            this.screenOrientation = -1;
            this.userActivityTimeout = -1L;
            this.mCompatibilityParamsBackup = null;
            this.mTitle = "";
            this.type = i;
            this.flags = i2;
            this.format = -1;
        }

        public LayoutParams(int i, int i2, int i3) {
            super(-1, -1);
            this.alpha = 1.0f;
            this.dimAmount = 1.0f;
            this.screenBrightness = -1.0f;
            this.buttonBrightness = -1.0f;
            this.rotationAnimation = 0;
            this.token = null;
            this.packageName = null;
            this.screenOrientation = -1;
            this.userActivityTimeout = -1L;
            this.mCompatibilityParamsBackup = null;
            this.mTitle = "";
            this.type = i;
            this.flags = i2;
            this.format = i3;
        }

        public LayoutParams(int i, int i2, int i3, int i4, int i5) {
            super(i, i2);
            this.alpha = 1.0f;
            this.dimAmount = 1.0f;
            this.screenBrightness = -1.0f;
            this.buttonBrightness = -1.0f;
            this.rotationAnimation = 0;
            this.token = null;
            this.packageName = null;
            this.screenOrientation = -1;
            this.userActivityTimeout = -1L;
            this.mCompatibilityParamsBackup = null;
            this.mTitle = "";
            this.type = i3;
            this.flags = i4;
            this.format = i5;
        }

        public LayoutParams(int i, int i2, int i3, int i4, int i5, int i6, int i7) {
            super(i, i2);
            this.alpha = 1.0f;
            this.dimAmount = 1.0f;
            this.screenBrightness = -1.0f;
            this.buttonBrightness = -1.0f;
            this.rotationAnimation = 0;
            this.token = null;
            this.packageName = null;
            this.screenOrientation = -1;
            this.userActivityTimeout = -1L;
            this.mCompatibilityParamsBackup = null;
            this.mTitle = "";
            this.x = i3;
            this.y = i4;
            this.type = i5;
            this.flags = i6;
            this.format = i7;
        }

        public final void setTitle(CharSequence charSequence) {
            if (charSequence == null) {
                charSequence = "";
            }
            this.mTitle = TextUtils.stringOrSpannedString(charSequence);
        }

        public final CharSequence getTitle() {
            return this.mTitle;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeInt(this.width);
            parcel.writeInt(this.height);
            parcel.writeInt(this.x);
            parcel.writeInt(this.y);
            parcel.writeInt(this.type);
            parcel.writeInt(this.flags);
            parcel.writeInt(this.privateFlags);
            parcel.writeInt(this.softInputMode);
            parcel.writeInt(this.gravity);
            parcel.writeFloat(this.horizontalMargin);
            parcel.writeFloat(this.verticalMargin);
            parcel.writeInt(this.format);
            parcel.writeInt(this.windowAnimations);
            parcel.writeFloat(this.alpha);
            parcel.writeFloat(this.dimAmount);
            parcel.writeFloat(this.screenBrightness);
            parcel.writeFloat(this.buttonBrightness);
            parcel.writeInt(this.rotationAnimation);
            parcel.writeStrongBinder(this.token);
            parcel.writeString(this.packageName);
            TextUtils.writeToParcel(this.mTitle, parcel, i);
            parcel.writeInt(this.screenOrientation);
            parcel.writeInt(this.systemUiVisibility);
            parcel.writeInt(this.subtreeSystemUiVisibility);
            parcel.writeInt(this.hasSystemUiListeners ? 1 : 0);
            parcel.writeInt(this.inputFeatures);
            parcel.writeLong(this.userActivityTimeout);
        }

        public LayoutParams(Parcel parcel) {
            this.alpha = 1.0f;
            this.dimAmount = 1.0f;
            this.screenBrightness = -1.0f;
            this.buttonBrightness = -1.0f;
            this.rotationAnimation = 0;
            this.token = null;
            this.packageName = null;
            this.screenOrientation = -1;
            this.userActivityTimeout = -1L;
            this.mCompatibilityParamsBackup = null;
            this.mTitle = "";
            this.width = parcel.readInt();
            this.height = parcel.readInt();
            this.x = parcel.readInt();
            this.y = parcel.readInt();
            this.type = parcel.readInt();
            this.flags = parcel.readInt();
            this.privateFlags = parcel.readInt();
            this.softInputMode = parcel.readInt();
            this.gravity = parcel.readInt();
            this.horizontalMargin = parcel.readFloat();
            this.verticalMargin = parcel.readFloat();
            this.format = parcel.readInt();
            this.windowAnimations = parcel.readInt();
            this.alpha = parcel.readFloat();
            this.dimAmount = parcel.readFloat();
            this.screenBrightness = parcel.readFloat();
            this.buttonBrightness = parcel.readFloat();
            this.rotationAnimation = parcel.readInt();
            this.token = parcel.readStrongBinder();
            this.packageName = parcel.readString();
            this.mTitle = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel);
            this.screenOrientation = parcel.readInt();
            this.systemUiVisibility = parcel.readInt();
            this.subtreeSystemUiVisibility = parcel.readInt();
            this.hasSystemUiListeners = parcel.readInt() != 0;
            this.inputFeatures = parcel.readInt();
            this.userActivityTimeout = parcel.readLong();
        }

        public final int copyFrom(LayoutParams layoutParams) {
            int i;
            if (this.width != layoutParams.width) {
                this.width = layoutParams.width;
                i = 1;
            } else {
                i = 0;
            }
            if (this.height != layoutParams.height) {
                this.height = layoutParams.height;
                i |= 1;
            }
            int i2 = this.x;
            int i3 = layoutParams.x;
            if (i2 != i3) {
                this.x = i3;
                i |= 1;
            }
            int i4 = this.y;
            int i5 = layoutParams.y;
            if (i4 != i5) {
                this.y = i5;
                i |= 1;
            }
            float f = this.horizontalWeight;
            float f2 = layoutParams.horizontalWeight;
            if (f != f2) {
                this.horizontalWeight = f2;
                i |= 1;
            }
            float f3 = this.verticalWeight;
            float f4 = layoutParams.verticalWeight;
            if (f3 != f4) {
                this.verticalWeight = f4;
                i |= 1;
            }
            float f5 = this.horizontalMargin;
            float f6 = layoutParams.horizontalMargin;
            if (f5 != f6) {
                this.horizontalMargin = f6;
                i |= 1;
            }
            float f7 = this.verticalMargin;
            float f8 = layoutParams.verticalMargin;
            if (f7 != f8) {
                this.verticalMargin = f8;
                i |= 1;
            }
            int i6 = this.type;
            int i7 = layoutParams.type;
            if (i6 != i7) {
                this.type = i7;
                i |= 2;
            }
            int i8 = this.flags;
            int i9 = layoutParams.flags;
            if (i8 != i9) {
                if (((i8 ^ i9) & 201326592) != 0) {
                    i |= 524288;
                }
                this.flags = i9;
                i |= 4;
            }
            int i10 = this.privateFlags;
            int i11 = layoutParams.privateFlags;
            if (i10 != i11) {
                this.privateFlags = i11;
                i |= 131072;
            }
            int i12 = this.softInputMode;
            int i13 = layoutParams.softInputMode;
            if (i12 != i13) {
                this.softInputMode = i13;
                i |= 512;
            }
            int i14 = this.gravity;
            int i15 = layoutParams.gravity;
            if (i14 != i15) {
                this.gravity = i15;
                i |= 1;
            }
            int i16 = this.format;
            int i17 = layoutParams.format;
            if (i16 != i17) {
                this.format = i17;
                i |= 8;
            }
            int i18 = this.windowAnimations;
            int i19 = layoutParams.windowAnimations;
            if (i18 != i19) {
                this.windowAnimations = i19;
                i |= 16;
            }
            if (this.token == null) {
                this.token = layoutParams.token;
            }
            if (this.packageName == null) {
                this.packageName = layoutParams.packageName;
            }
            if (!this.mTitle.equals(layoutParams.mTitle)) {
                this.mTitle = layoutParams.mTitle;
                i |= 64;
            }
            float f9 = this.alpha;
            float f10 = layoutParams.alpha;
            if (f9 != f10) {
                this.alpha = f10;
                i |= 128;
            }
            float f11 = this.dimAmount;
            float f12 = layoutParams.dimAmount;
            if (f11 != f12) {
                this.dimAmount = f12;
                i |= 32;
            }
            float f13 = this.screenBrightness;
            float f14 = layoutParams.screenBrightness;
            if (f13 != f14) {
                this.screenBrightness = f14;
                i |= 2048;
            }
            float f15 = this.buttonBrightness;
            float f16 = layoutParams.buttonBrightness;
            if (f15 != f16) {
                this.buttonBrightness = f16;
                i |= 8192;
            }
            int i20 = this.rotationAnimation;
            int i21 = layoutParams.rotationAnimation;
            if (i20 != i21) {
                this.rotationAnimation = i21;
                i |= 4096;
            }
            int i22 = this.screenOrientation;
            int i23 = layoutParams.screenOrientation;
            if (i22 != i23) {
                this.screenOrientation = i23;
                i |= 1024;
            }
            int i24 = this.systemUiVisibility;
            int i25 = layoutParams.systemUiVisibility;
            if (i24 != i25 || this.subtreeSystemUiVisibility != layoutParams.subtreeSystemUiVisibility) {
                this.systemUiVisibility = i25;
                this.subtreeSystemUiVisibility = layoutParams.subtreeSystemUiVisibility;
                i |= 16384;
            }
            boolean z = this.hasSystemUiListeners;
            boolean z2 = layoutParams.hasSystemUiListeners;
            if (z != z2) {
                this.hasSystemUiListeners = z2;
                i |= 32768;
            }
            int i26 = this.inputFeatures;
            int i27 = layoutParams.inputFeatures;
            if (i26 != i27) {
                this.inputFeatures = i27;
                i |= 65536;
            }
            long j = this.userActivityTimeout;
            long j2 = layoutParams.userActivityTimeout;
            if (j == j2) {
                return i;
            }
            this.userActivityTimeout = j2;
            return i | 262144;
        }

        @Override // android.view.ViewGroup.LayoutParams
        public String debug(String str) {
            Log.d("Debug", str + "Contents of " + this + ":");
            Log.d("Debug", super.debug(""));
            Log.d("Debug", "");
            Log.d("Debug", "WindowManager.LayoutParams={title=" + ((Object) this.mTitle) + "}");
            return "";
        }

        public String toString() {
            StringBuilder sb = new StringBuilder(256);
            sb.append("WM.LayoutParams{");
            sb.append("(");
            sb.append(this.x);
            sb.append(PhoneNumberUtils.PAUSE);
            sb.append(this.y);
            sb.append(")(");
            Object objValueOf = "wrap";
            sb.append(this.width == -1 ? "fill" : this.width == -2 ? "wrap" : Integer.valueOf(this.width));
            sb.append('x');
            if (this.height == -1) {
                objValueOf = "fill";
            } else if (this.height != -2) {
                objValueOf = Integer.valueOf(this.height);
            }
            sb.append(objValueOf);
            sb.append(")");
            if (this.horizontalMargin != 0.0f) {
                sb.append(" hm=");
                sb.append(this.horizontalMargin);
            }
            if (this.verticalMargin != 0.0f) {
                sb.append(" vm=");
                sb.append(this.verticalMargin);
            }
            if (this.gravity != 0) {
                sb.append(" gr=#");
                sb.append(Integer.toHexString(this.gravity));
            }
            if (this.softInputMode != 0) {
                sb.append(" sim=#");
                sb.append(Integer.toHexString(this.softInputMode));
            }
            sb.append(" ty=");
            sb.append(this.type);
            sb.append(" fl=#");
            sb.append(Integer.toHexString(this.flags));
            int i = this.privateFlags;
            if (i != 0) {
                if ((i & 128) != 0) {
                    sb.append(" compatible=true");
                }
                sb.append(" pfl=0x").append(Integer.toHexString(this.privateFlags));
            }
            if (this.format != -1) {
                sb.append(" fmt=");
                sb.append(this.format);
            }
            if (this.windowAnimations != 0) {
                sb.append(" wanim=0x");
                sb.append(Integer.toHexString(this.windowAnimations));
            }
            if (this.screenOrientation != -1) {
                sb.append(" or=");
                sb.append(this.screenOrientation);
            }
            if (this.alpha != 1.0f) {
                sb.append(" alpha=");
                sb.append(this.alpha);
            }
            if (this.screenBrightness != -1.0f) {
                sb.append(" sbrt=");
                sb.append(this.screenBrightness);
            }
            if (this.buttonBrightness != -1.0f) {
                sb.append(" bbrt=");
                sb.append(this.buttonBrightness);
            }
            if (this.rotationAnimation != 0) {
                sb.append(" rotAnim=");
                sb.append(this.rotationAnimation);
            }
            if (this.systemUiVisibility != 0) {
                sb.append(" sysui=0x");
                sb.append(Integer.toHexString(this.systemUiVisibility));
            }
            if (this.subtreeSystemUiVisibility != 0) {
                sb.append(" vsysui=0x");
                sb.append(Integer.toHexString(this.subtreeSystemUiVisibility));
            }
            if (this.hasSystemUiListeners) {
                sb.append(" sysuil=");
                sb.append(this.hasSystemUiListeners);
            }
            if (this.inputFeatures != 0) {
                sb.append(" if=0x").append(Integer.toHexString(this.inputFeatures));
            }
            if (this.userActivityTimeout >= 0) {
                sb.append(" userActivityTimeout=").append(this.userActivityTimeout);
            }
            sb.append('}');
            return sb.toString();
        }

        public void scale(float f) {
            this.x = (int) ((this.x * f) + 0.5f);
            this.y = (int) ((this.y * f) + 0.5f);
            if (this.width > 0) {
                this.width = (int) ((this.width * f) + 0.5f);
            }
            if (this.height > 0) {
                this.height = (int) ((this.height * f) + 0.5f);
            }
        }

        void backup() {
            int[] iArr = this.mCompatibilityParamsBackup;
            if (iArr == null) {
                iArr = new int[4];
                this.mCompatibilityParamsBackup = iArr;
            }
            iArr[0] = this.x;
            iArr[1] = this.y;
            iArr[2] = this.width;
            iArr[3] = this.height;
        }

        void restore() {
            int[] iArr = this.mCompatibilityParamsBackup;
            if (iArr != null) {
                this.x = iArr[0];
                this.y = iArr[1];
                this.width = iArr[2];
                this.height = iArr[3];
            }
        }
    }
}
