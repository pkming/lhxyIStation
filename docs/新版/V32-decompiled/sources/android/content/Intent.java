package android.content;

import android.content.ClipData;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.StrictMode;
import android.telephony.PhoneNumberUtils;
import android.text.format.DateFormat;
import android.util.ArraySet;
import android.util.AttributeSet;
import android.util.Log;
import com.android.internal.R;
import com.android.internal.util.XmlUtils;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/* JADX INFO: loaded from: classes.dex */
public class Intent implements Parcelable, Cloneable {
    public static final String ACTION_ADVANCED_SETTINGS_CHANGED = "android.intent.action.ADVANCED_SETTINGS";
    public static final String ACTION_AIRPLANE_MODE_CHANGED = "android.intent.action.AIRPLANE_MODE";
    public static final String ACTION_ALARM_CHANGED = "android.intent.action.ALARM_CHANGED";
    public static final String ACTION_ALL_APPS = "android.intent.action.ALL_APPS";
    public static final String ACTION_ANALOG_AUDIO_DOCK_PLUG = "android.intent.action.ANALOG_AUDIO_DOCK_PLUG";
    public static final String ACTION_ANSWER = "android.intent.action.ANSWER";
    public static final String ACTION_APP_ERROR = "android.intent.action.APP_ERROR";
    public static final String ACTION_ASSIST = "android.intent.action.ASSIST";
    public static final String ACTION_ATTACH_DATA = "android.intent.action.ATTACH_DATA";
    public static final String ACTION_AUDIO_PLUG_IN_OUT = "android.intent.action.AUDIO_PLUG_IN_OUT";
    public static final String ACTION_BATTERY_CHANGED = "android.intent.action.BATTERY_CHANGED";
    public static final String ACTION_BATTERY_LOW = "android.intent.action.BATTERY_LOW";
    public static final String ACTION_BATTERY_OKAY = "android.intent.action.BATTERY_OKAY";
    public static final String ACTION_BOOT_COMPLETED = "android.intent.action.BOOT_COMPLETED";
    public static final String ACTION_BOOT_FAST = "android.intent.action.BOOT_FAST";
    public static final String ACTION_BUG_REPORT = "android.intent.action.BUG_REPORT";
    public static final String ACTION_CALL = "android.intent.action.CALL";
    public static final String ACTION_CALL_BUTTON = "android.intent.action.CALL_BUTTON";
    public static final String ACTION_CALL_EMERGENCY = "android.intent.action.CALL_EMERGENCY";
    public static final String ACTION_CALL_PRIVILEGED = "android.intent.action.CALL_PRIVILEGED";
    public static final String ACTION_CAMERA_BUTTON = "android.intent.action.CAMERA_BUTTON";
    public static final String ACTION_CHOOSER = "android.intent.action.CHOOSER";
    public static final String ACTION_CLEAR_DNS_CACHE = "android.intent.action.CLEAR_DNS_CACHE";
    public static final String ACTION_CLOSE_SYSTEM_DIALOGS = "android.intent.action.CLOSE_SYSTEM_DIALOGS";
    public static final String ACTION_CONFIGURATION_CHANGED = "android.intent.action.CONFIGURATION_CHANGED";
    public static final String ACTION_CREATE_DOCUMENT = "android.intent.action.CREATE_DOCUMENT";
    public static final String ACTION_CREATE_SHORTCUT = "android.intent.action.CREATE_SHORTCUT";
    public static final String ACTION_DATE_CHANGED = "android.intent.action.DATE_CHANGED";
    public static final String ACTION_DEFAULT = "android.intent.action.VIEW";
    public static final String ACTION_DELETE = "android.intent.action.DELETE";
    public static final String ACTION_DEVICE_STORAGE_FULL = "android.intent.action.DEVICE_STORAGE_FULL";
    public static final String ACTION_DEVICE_STORAGE_LOW = "android.intent.action.DEVICE_STORAGE_LOW";
    public static final String ACTION_DEVICE_STORAGE_NOT_FULL = "android.intent.action.DEVICE_STORAGE_NOT_FULL";
    public static final String ACTION_DEVICE_STORAGE_OK = "android.intent.action.DEVICE_STORAGE_OK";
    public static final String ACTION_DIAL = "android.intent.action.DIAL";
    public static final String ACTION_DIGITAL_AUDIO_DOCK_PLUG = "android.intent.action.DIGITAL_AUDIO_DOCK_PLUG";
    public static final String ACTION_DOCK_EVENT = "android.intent.action.DOCK_EVENT";
    public static final String ACTION_DREAMING_STARTED = "android.intent.action.DREAMING_STARTED";
    public static final String ACTION_DREAMING_STOPPED = "android.intent.action.DREAMING_STOPPED";
    public static final String ACTION_EDIT = "android.intent.action.EDIT";
    public static final String ACTION_EXTERNAL_APPLICATIONS_AVAILABLE = "android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE";
    public static final String ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE = "android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE";
    public static final String ACTION_FACTORY_TEST = "android.intent.action.FACTORY_TEST";
    public static final String ACTION_GET_CONTENT = "android.intent.action.GET_CONTENT";
    public static final String ACTION_GET_RESTRICTION_ENTRIES = "android.intent.action.GET_RESTRICTION_ENTRIES";
    public static final String ACTION_GLOBAL_BUTTON = "android.intent.action.GLOBAL_BUTTON";
    public static final String ACTION_GTALK_SERVICE_CONNECTED = "android.intent.action.GTALK_CONNECTED";
    public static final String ACTION_GTALK_SERVICE_DISCONNECTED = "android.intent.action.GTALK_DISCONNECTED";
    public static final String ACTION_HDMISTATUS_CHANGED = "android.intent.action.HDMISTATUS_CHANGED";
    public static final String ACTION_HDMI_AUDIO_PLUG = "android.intent.action.HDMI_AUDIO_PLUG";
    public static final String ACTION_HEADSET_PLUG = "android.intent.action.HEADSET_PLUG";
    public static final String ACTION_IDLE_MAINTENANCE_END = "android.intent.action.ACTION_IDLE_MAINTENANCE_END";
    public static final String ACTION_IDLE_MAINTENANCE_START = "android.intent.action.ACTION_IDLE_MAINTENANCE_START";
    public static final String ACTION_INPUT_METHOD_CHANGED = "android.intent.action.INPUT_METHOD_CHANGED";
    public static final String ACTION_INSERT = "android.intent.action.INSERT";
    public static final String ACTION_INSERT_OR_EDIT = "android.intent.action.INSERT_OR_EDIT";
    public static final String ACTION_INSTALL_PACKAGE = "android.intent.action.INSTALL_PACKAGE";
    public static final String ACTION_LOCALE_CHANGED = "android.intent.action.LOCALE_CHANGED";
    public static final String ACTION_MAIN = "android.intent.action.MAIN";
    public static final String ACTION_MANAGE_NETWORK_USAGE = "android.intent.action.MANAGE_NETWORK_USAGE";
    public static final String ACTION_MANAGE_PACKAGE_STORAGE = "android.intent.action.MANAGE_PACKAGE_STORAGE";
    public static final String ACTION_MEDIA_BAD_REMOVAL = "android.intent.action.MEDIA_BAD_REMOVAL";
    public static final String ACTION_MEDIA_BUTTON = "android.intent.action.MEDIA_BUTTON";
    public static final String ACTION_MEDIA_CHECKING = "android.intent.action.MEDIA_CHECKING";
    public static final String ACTION_MEDIA_EJECT = "android.intent.action.MEDIA_EJECT";
    public static final String ACTION_MEDIA_MOUNTED = "android.intent.action.MEDIA_MOUNTED";
    public static final String ACTION_MEDIA_NOFS = "android.intent.action.MEDIA_NOFS";
    public static final String ACTION_MEDIA_REMOVED = "android.intent.action.MEDIA_REMOVED";
    public static final String ACTION_MEDIA_SCANNER_FINISHED = "android.intent.action.MEDIA_SCANNER_FINISHED";
    public static final String ACTION_MEDIA_SCANNER_SCAN_FILE = "android.intent.action.MEDIA_SCANNER_SCAN_FILE";
    public static final String ACTION_MEDIA_SCANNER_STARTED = "android.intent.action.MEDIA_SCANNER_STARTED";
    public static final String ACTION_MEDIA_SHARED = "android.intent.action.MEDIA_SHARED";
    public static final String ACTION_MEDIA_UNMOUNTABLE = "android.intent.action.MEDIA_UNMOUNTABLE";
    public static final String ACTION_MEDIA_UNMOUNTED = "android.intent.action.MEDIA_UNMOUNTED";
    public static final String ACTION_MEDIA_UNSHARED = "android.intent.action.MEDIA_UNSHARED";
    public static final String ACTION_MY_PACKAGE_REPLACED = "android.intent.action.MY_PACKAGE_REPLACED";
    public static final String ACTION_NEW_OUTGOING_CALL = "android.intent.action.NEW_OUTGOING_CALL";
    public static final String ACTION_OPEN_DOCUMENT = "android.intent.action.OPEN_DOCUMENT";
    public static final String ACTION_PACKAGE_ADDED = "android.intent.action.PACKAGE_ADDED";
    public static final String ACTION_PACKAGE_CHANGED = "android.intent.action.PACKAGE_CHANGED";
    public static final String ACTION_PACKAGE_DATA_CLEARED = "android.intent.action.PACKAGE_DATA_CLEARED";
    public static final String ACTION_PACKAGE_FIRST_LAUNCH = "android.intent.action.PACKAGE_FIRST_LAUNCH";
    public static final String ACTION_PACKAGE_FULLY_REMOVED = "android.intent.action.PACKAGE_FULLY_REMOVED";

    @Deprecated
    public static final String ACTION_PACKAGE_INSTALL = "android.intent.action.PACKAGE_INSTALL";
    public static final String ACTION_PACKAGE_NEEDS_VERIFICATION = "android.intent.action.PACKAGE_NEEDS_VERIFICATION";
    public static final String ACTION_PACKAGE_REMOVED = "android.intent.action.PACKAGE_REMOVED";
    public static final String ACTION_PACKAGE_REPLACED = "android.intent.action.PACKAGE_REPLACED";
    public static final String ACTION_PACKAGE_RESTARTED = "android.intent.action.PACKAGE_RESTARTED";
    public static final String ACTION_PACKAGE_VERIFIED = "android.intent.action.PACKAGE_VERIFIED";
    public static final String ACTION_PASTE = "android.intent.action.PASTE";
    public static final String ACTION_PICK = "android.intent.action.PICK";
    public static final String ACTION_PICK_ACTIVITY = "android.intent.action.PICK_ACTIVITY";
    public static final String ACTION_POWER_CONNECTED = "android.intent.action.ACTION_POWER_CONNECTED";
    public static final String ACTION_POWER_DISCONNECTED = "android.intent.action.ACTION_POWER_DISCONNECTED";
    public static final String ACTION_POWER_USAGE_SUMMARY = "android.intent.action.POWER_USAGE_SUMMARY";
    public static final String ACTION_PRE_BOOT_COMPLETED = "android.intent.action.PRE_BOOT_COMPLETED";
    public static final String ACTION_PROVIDER_CHANGED = "android.intent.action.PROVIDER_CHANGED";
    public static final String ACTION_QUERY_PACKAGE_RESTART = "android.intent.action.QUERY_PACKAGE_RESTART";
    public static final String ACTION_QUICK_CLOCK = "android.intent.action.QUICK_CLOCK";
    public static final String ACTION_REBOOT = "android.intent.action.REBOOT";
    public static final String ACTION_REMOTE_INTENT = "com.google.android.c2dm.intent.RECEIVE";
    public static final String ACTION_REQUEST_SHUTDOWN = "android.intent.action.ACTION_REQUEST_SHUTDOWN";
    public static final String ACTION_RESTRICTIONS_CHALLENGE = "android.intent.action.RESTRICTIONS_CHALLENGE";
    public static final String ACTION_RUN = "android.intent.action.RUN";
    public static final String ACTION_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    public static final String ACTION_SCREEN_ON = "android.intent.action.SCREEN_ON";
    public static final String ACTION_SEARCH = "android.intent.action.SEARCH";
    public static final String ACTION_SEARCH_LONG_PRESS = "android.intent.action.SEARCH_LONG_PRESS";
    public static final String ACTION_SEND = "android.intent.action.SEND";
    public static final String ACTION_SENDTO = "android.intent.action.SENDTO";
    public static final String ACTION_SEND_MULTIPLE = "android.intent.action.SEND_MULTIPLE";
    public static final String ACTION_SET_WALLPAPER = "android.intent.action.SET_WALLPAPER";
    public static final String ACTION_SHOW_BRIGHTNESS_DIALOG = "android.intent.action.SHOW_BRIGHTNESS_DIALOG";
    public static final String ACTION_SHUTDOWN = "android.intent.action.ACTION_SHUTDOWN";
    public static final String ACTION_SYNC = "android.intent.action.SYNC";
    public static final String ACTION_SYNC_STATE_CHANGED = "android.intent.action.SYNC_STATE_CHANGED";
    public static final String ACTION_SYSTEM_TUTORIAL = "android.intent.action.SYSTEM_TUTORIAL";
    public static final String ACTION_TIMEZONE_CHANGED = "android.intent.action.TIMEZONE_CHANGED";
    public static final String ACTION_TIME_CHANGED = "android.intent.action.TIME_SET";
    public static final String ACTION_TIME_TICK = "android.intent.action.TIME_TICK";
    public static final String ACTION_UID_REMOVED = "android.intent.action.UID_REMOVED";
    public static final String ACTION_UI_BOOT_FINISH = "android.intent.action.UI_BOOT_FINISH ";

    @Deprecated
    public static final String ACTION_UMS_CONNECTED = "android.intent.action.UMS_CONNECTED";

    @Deprecated
    public static final String ACTION_UMS_DISCONNECTED = "android.intent.action.UMS_DISCONNECTED";
    public static final String ACTION_UNINSTALL_PACKAGE = "android.intent.action.UNINSTALL_PACKAGE";
    public static final String ACTION_UPGRADE_SETUP = "android.intent.action.UPGRADE_SETUP";
    public static final String ACTION_USB_AUDIO_ACCESSORY_PLUG = "android.intent.action.USB_AUDIO_ACCESSORY_PLUG";
    public static final String ACTION_USB_AUDIO_DEVICE_PLUG = "android.intent.action.USB_AUDIO_DEVICE_PLUG";
    public static final String ACTION_USER_ADDED = "android.intent.action.USER_ADDED";
    public static final String ACTION_USER_BACKGROUND = "android.intent.action.USER_BACKGROUND";
    public static final String ACTION_USER_FOREGROUND = "android.intent.action.USER_FOREGROUND";
    public static final String ACTION_USER_INFO_CHANGED = "android.intent.action.USER_INFO_CHANGED";
    public static final String ACTION_USER_INITIALIZE = "android.intent.action.USER_INITIALIZE";
    public static final String ACTION_USER_PRESENT = "android.intent.action.USER_PRESENT";
    public static final String ACTION_USER_REMOVED = "android.intent.action.USER_REMOVED";
    public static final String ACTION_USER_STARTED = "android.intent.action.USER_STARTED";
    public static final String ACTION_USER_STARTING = "android.intent.action.USER_STARTING";
    public static final String ACTION_USER_STOPPED = "android.intent.action.USER_STOPPED";
    public static final String ACTION_USER_STOPPING = "android.intent.action.USER_STOPPING";
    public static final String ACTION_USER_SWITCHED = "android.intent.action.USER_SWITCHED";
    public static final String ACTION_VIEW = "android.intent.action.VIEW";
    public static final String ACTION_VOICE_ASSIST = "android.intent.action.VOICE_ASSIST";
    public static final String ACTION_VOICE_COMMAND = "android.intent.action.VOICE_COMMAND";

    @Deprecated
    public static final String ACTION_WALLPAPER_CHANGED = "android.intent.action.WALLPAPER_CHANGED";
    public static final String ACTION_WEB_SEARCH = "android.intent.action.WEB_SEARCH";
    public static final String CATEGORY_ALTERNATIVE = "android.intent.category.ALTERNATIVE";
    public static final String CATEGORY_APP_BROWSER = "android.intent.category.APP_BROWSER";
    public static final String CATEGORY_APP_CALCULATOR = "android.intent.category.APP_CALCULATOR";
    public static final String CATEGORY_APP_CALENDAR = "android.intent.category.APP_CALENDAR";
    public static final String CATEGORY_APP_CONTACTS = "android.intent.category.APP_CONTACTS";
    public static final String CATEGORY_APP_EMAIL = "android.intent.category.APP_EMAIL";
    public static final String CATEGORY_APP_GALLERY = "android.intent.category.APP_GALLERY";
    public static final String CATEGORY_APP_MAPS = "android.intent.category.APP_MAPS";
    public static final String CATEGORY_APP_MARKET = "android.intent.category.APP_MARKET";
    public static final String CATEGORY_APP_MESSAGING = "android.intent.category.APP_MESSAGING";
    public static final String CATEGORY_APP_MUSIC = "android.intent.category.APP_MUSIC";
    public static final String CATEGORY_BROWSABLE = "android.intent.category.BROWSABLE";
    public static final String CATEGORY_CAR_DOCK = "android.intent.category.CAR_DOCK";
    public static final String CATEGORY_CAR_MODE = "android.intent.category.CAR_MODE";
    public static final String CATEGORY_DEFAULT = "android.intent.category.DEFAULT";
    public static final String CATEGORY_DESK_DOCK = "android.intent.category.DESK_DOCK";
    public static final String CATEGORY_DEVELOPMENT_PREFERENCE = "android.intent.category.DEVELOPMENT_PREFERENCE";
    public static final String CATEGORY_EMBED = "android.intent.category.EMBED";
    public static final String CATEGORY_FRAMEWORK_INSTRUMENTATION_TEST = "android.intent.category.FRAMEWORK_INSTRUMENTATION_TEST";
    public static final String CATEGORY_HE_DESK_DOCK = "android.intent.category.HE_DESK_DOCK";
    public static final String CATEGORY_HOME = "android.intent.category.HOME";
    public static final String CATEGORY_INFO = "android.intent.category.INFO";
    public static final String CATEGORY_LAUNCHER = "android.intent.category.LAUNCHER";
    public static final String CATEGORY_LE_DESK_DOCK = "android.intent.category.LE_DESK_DOCK";
    public static final String CATEGORY_MONKEY = "android.intent.category.MONKEY";
    public static final String CATEGORY_OPENABLE = "android.intent.category.OPENABLE";
    public static final String CATEGORY_PREFERENCE = "android.intent.category.PREFERENCE";
    public static final String CATEGORY_SAMPLE_CODE = "android.intent.category.SAMPLE_CODE";
    public static final String CATEGORY_SELECTED_ALTERNATIVE = "android.intent.category.SELECTED_ALTERNATIVE";
    public static final String CATEGORY_TAB = "android.intent.category.TAB";
    public static final String CATEGORY_TEST = "android.intent.category.TEST";
    public static final String CATEGORY_UNIT_TEST = "android.intent.category.UNIT_TEST";
    public static final Parcelable.Creator<Intent> CREATOR = new Parcelable.Creator<Intent>() { // from class: android.content.Intent.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Intent createFromParcel(Parcel parcel) {
            return new Intent(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Intent[] newArray(int i) {
            return new Intent[i];
        }
    };
    public static final String EXTRA_ALARM_COUNT = "android.intent.extra.ALARM_COUNT";
    public static final String EXTRA_ALLOW_MULTIPLE = "android.intent.extra.ALLOW_MULTIPLE";

    @Deprecated
    public static final String EXTRA_ALLOW_REPLACE = "android.intent.extra.ALLOW_REPLACE";
    public static final String EXTRA_ASSIST_CONTEXT = "android.intent.extra.ASSIST_CONTEXT";
    public static final String EXTRA_ASSIST_PACKAGE = "android.intent.extra.ASSIST_PACKAGE";
    public static final String EXTRA_BCC = "android.intent.extra.BCC";
    public static final String EXTRA_BOOT_FAST = "android.intent.extra.boot_fast";
    public static final String EXTRA_BUG_REPORT = "android.intent.extra.BUG_REPORT";
    public static final String EXTRA_CC = "android.intent.extra.CC";

    @Deprecated
    public static final String EXTRA_CHANGED_COMPONENT_NAME = "android.intent.extra.changed_component_name";
    public static final String EXTRA_CHANGED_COMPONENT_NAME_LIST = "android.intent.extra.changed_component_name_list";
    public static final String EXTRA_CHANGED_PACKAGE_LIST = "android.intent.extra.changed_package_list";
    public static final String EXTRA_CHANGED_UID_LIST = "android.intent.extra.changed_uid_list";
    public static final String EXTRA_CLIENT_INTENT = "android.intent.extra.client_intent";
    public static final String EXTRA_CLIENT_LABEL = "android.intent.extra.client_label";
    public static final String EXTRA_DATA_REMOVED = "android.intent.extra.DATA_REMOVED";
    public static final String EXTRA_DOCK_STATE = "android.intent.extra.DOCK_STATE";
    public static final int EXTRA_DOCK_STATE_CAR = 2;
    public static final int EXTRA_DOCK_STATE_DESK = 1;
    public static final int EXTRA_DOCK_STATE_HE_DESK = 4;
    public static final int EXTRA_DOCK_STATE_LE_DESK = 3;
    public static final int EXTRA_DOCK_STATE_UNDOCKED = 0;
    public static final String EXTRA_DONT_KILL_APP = "android.intent.extra.DONT_KILL_APP";
    public static final String EXTRA_EMAIL = "android.intent.extra.EMAIL";
    public static final String EXTRA_HTML_TEXT = "android.intent.extra.HTML_TEXT";
    public static final String EXTRA_INITIAL_INTENTS = "android.intent.extra.INITIAL_INTENTS";
    public static final String EXTRA_INSTALLER_PACKAGE_NAME = "android.intent.extra.INSTALLER_PACKAGE_NAME";
    public static final String EXTRA_INSTALL_RESULT = "android.intent.extra.INSTALL_RESULT";
    public static final String EXTRA_INTENT = "android.intent.extra.INTENT";
    public static final String EXTRA_KEY_CONFIRM = "android.intent.extra.KEY_CONFIRM";
    public static final String EXTRA_KEY_EVENT = "android.intent.extra.KEY_EVENT";
    public static final String EXTRA_LOCAL_ONLY = "android.intent.extra.LOCAL_ONLY";
    public static final String EXTRA_MIME_TYPES = "android.intent.extra.MIME_TYPES";
    public static final String EXTRA_NOT_UNKNOWN_SOURCE = "android.intent.extra.NOT_UNKNOWN_SOURCE";
    public static final String EXTRA_ORIGINATING_UID = "android.intent.extra.ORIGINATING_UID";
    public static final String EXTRA_ORIGINATING_URI = "android.intent.extra.ORIGINATING_URI";
    public static final String EXTRA_PACKAGES = "android.intent.extra.PACKAGES";
    public static final String EXTRA_PHONE_NUMBER = "android.intent.extra.PHONE_NUMBER";
    public static final String EXTRA_REFERRER = "android.intent.extra.REFERRER";
    public static final String EXTRA_REMOTE_INTENT_TOKEN = "android.intent.extra.remote_intent_token";
    public static final String EXTRA_REMOVED_FOR_ALL_USERS = "android.intent.extra.REMOVED_FOR_ALL_USERS";
    public static final String EXTRA_REPLACING = "android.intent.extra.REPLACING";
    public static final String EXTRA_RESTRICTIONS_BUNDLE = "android.intent.extra.restrictions_bundle";
    public static final String EXTRA_RESTRICTIONS_INTENT = "android.intent.extra.restrictions_intent";
    public static final String EXTRA_RESTRICTIONS_LIST = "android.intent.extra.restrictions_list";
    public static final String EXTRA_RETURN_RESULT = "android.intent.extra.RETURN_RESULT";
    public static final String EXTRA_SHORTCUT_ICON = "android.intent.extra.shortcut.ICON";
    public static final String EXTRA_SHORTCUT_ICON_RESOURCE = "android.intent.extra.shortcut.ICON_RESOURCE";
    public static final String EXTRA_SHORTCUT_INTENT = "android.intent.extra.shortcut.INTENT";
    public static final String EXTRA_SHORTCUT_NAME = "android.intent.extra.shortcut.NAME";
    public static final String EXTRA_SHUTDOWN_USERSPACE_ONLY = "android.intent.extra.SHUTDOWN_USERSPACE_ONLY";
    public static final String EXTRA_STREAM = "android.intent.extra.STREAM";
    public static final String EXTRA_SUBJECT = "android.intent.extra.SUBJECT";
    public static final String EXTRA_TEMPLATE = "android.intent.extra.TEMPLATE";
    public static final String EXTRA_TEXT = "android.intent.extra.TEXT";
    public static final String EXTRA_TITLE = "android.intent.extra.TITLE";
    public static final String EXTRA_UID = "android.intent.extra.UID";
    public static final String EXTRA_UNINSTALL_ALL_USERS = "android.intent.extra.UNINSTALL_ALL_USERS";
    public static final String EXTRA_USER_HANDLE = "android.intent.extra.user_handle";
    public static final int FILL_IN_ACTION = 1;
    public static final int FILL_IN_CATEGORIES = 4;
    public static final int FILL_IN_CLIP_DATA = 128;
    public static final int FILL_IN_COMPONENT = 8;
    public static final int FILL_IN_DATA = 2;
    public static final int FILL_IN_PACKAGE = 16;
    public static final int FILL_IN_SELECTOR = 64;
    public static final int FILL_IN_SOURCE_BOUNDS = 32;
    public static final int FLAG_ACTIVITY_BROUGHT_TO_FRONT = 4194304;
    public static final int FLAG_ACTIVITY_CLEAR_TASK = 32768;
    public static final int FLAG_ACTIVITY_CLEAR_TOP = 67108864;
    public static final int FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET = 524288;
    public static final int FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS = 8388608;
    public static final int FLAG_ACTIVITY_FORWARD_RESULT = 33554432;
    public static final int FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY = 1048576;
    public static final int FLAG_ACTIVITY_MULTIPLE_TASK = 134217728;
    public static final int FLAG_ACTIVITY_NEW_TASK = 268435456;
    public static final int FLAG_ACTIVITY_NO_ANIMATION = 65536;
    public static final int FLAG_ACTIVITY_NO_HISTORY = 1073741824;
    public static final int FLAG_ACTIVITY_NO_USER_ACTION = 262144;
    public static final int FLAG_ACTIVITY_PREVIOUS_IS_TOP = 16777216;
    public static final int FLAG_ACTIVITY_REORDER_TO_FRONT = 131072;
    public static final int FLAG_ACTIVITY_RESET_TASK_IF_NEEDED = 2097152;
    public static final int FLAG_ACTIVITY_SINGLE_TOP = 536870912;
    public static final int FLAG_ACTIVITY_TASK_ON_HOME = 16384;
    public static final int FLAG_DEBUG_LOG_RESOLUTION = 8;
    public static final int FLAG_EXCLUDE_STOPPED_PACKAGES = 16;
    public static final int FLAG_FROM_BACKGROUND = 4;
    public static final int FLAG_GRANT_PERSISTABLE_URI_PERMISSION = 64;
    public static final int FLAG_GRANT_READ_URI_PERMISSION = 1;
    public static final int FLAG_GRANT_WRITE_URI_PERMISSION = 2;
    public static final int FLAG_INCLUDE_STOPPED_PACKAGES = 32;
    public static final int FLAG_RECEIVER_BOOT_UPGRADE = 33554432;
    public static final int FLAG_RECEIVER_FOREGROUND = 268435456;
    public static final int FLAG_RECEIVER_NO_ABORT = 134217728;
    public static final int FLAG_RECEIVER_REGISTERED_ONLY = 1073741824;
    public static final int FLAG_RECEIVER_REGISTERED_ONLY_BEFORE_BOOT = 67108864;
    public static final int FLAG_RECEIVER_REPLACE_PENDING = 536870912;
    public static final int IMMUTABLE_FLAGS = 3;
    public static final String METADATA_DOCK_HOME = "android.dock_home";
    public static final String METADATA_SETUP_VERSION = "android.SETUP_VERSION";
    public static final int URI_INTENT_SCHEME = 1;
    private String mAction;
    private ArraySet<String> mCategories;
    private ClipData mClipData;
    private ComponentName mComponent;
    private Uri mData;
    private Bundle mExtras;
    private int mFlags;
    private String mPackage;
    private Intent mSelector;
    private Rect mSourceBounds;
    private String mType;

    public static class ShortcutIconResource implements Parcelable {
        public static final Parcelable.Creator<ShortcutIconResource> CREATOR = new Parcelable.Creator<ShortcutIconResource>() { // from class: android.content.Intent.ShortcutIconResource.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ShortcutIconResource createFromParcel(Parcel parcel) {
                ShortcutIconResource shortcutIconResource = new ShortcutIconResource();
                shortcutIconResource.packageName = parcel.readString();
                shortcutIconResource.resourceName = parcel.readString();
                return shortcutIconResource;
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public ShortcutIconResource[] newArray(int i) {
                return new ShortcutIconResource[i];
            }
        };
        public String packageName;
        public String resourceName;

        @Override // android.os.Parcelable
        public int describeContents() {
            return 0;
        }

        public static ShortcutIconResource fromContext(Context context, int i) {
            ShortcutIconResource shortcutIconResource = new ShortcutIconResource();
            shortcutIconResource.packageName = context.getPackageName();
            shortcutIconResource.resourceName = context.getResources().getResourceName(i);
            return shortcutIconResource;
        }

        @Override // android.os.Parcelable
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(this.packageName);
            parcel.writeString(this.resourceName);
        }

        public String toString() {
            return this.resourceName;
        }
    }

    public static Intent createChooser(Intent intent, CharSequence charSequence) {
        Intent intent2 = new Intent(ACTION_CHOOSER);
        intent2.putExtra(EXTRA_INTENT, intent);
        if (charSequence != null) {
            intent2.putExtra(EXTRA_TITLE, charSequence);
        }
        int flags = intent.getFlags() & 3;
        if (flags != 0) {
            ClipData clipData = intent.getClipData();
            if (clipData == null && intent.getData() != null) {
                clipData = new ClipData(null, intent.getType() != null ? new String[]{intent.getType()} : new String[0], new ClipData.Item(intent.getData()));
            }
            if (clipData != null) {
                intent2.setClipData(clipData);
                intent2.addFlags(flags);
            }
        }
        return intent2;
    }

    public Intent() {
    }

    public Intent(Intent intent) {
        this.mAction = intent.mAction;
        this.mData = intent.mData;
        this.mType = intent.mType;
        this.mPackage = intent.mPackage;
        this.mComponent = intent.mComponent;
        this.mFlags = intent.mFlags;
        if (intent.mCategories != null) {
            this.mCategories = new ArraySet<>(intent.mCategories);
        }
        if (intent.mExtras != null) {
            this.mExtras = new Bundle(intent.mExtras);
        }
        if (intent.mSourceBounds != null) {
            this.mSourceBounds = new Rect(intent.mSourceBounds);
        }
        if (intent.mSelector != null) {
            this.mSelector = new Intent(intent.mSelector);
        }
        if (intent.mClipData != null) {
            this.mClipData = new ClipData(intent.mClipData);
        }
    }

    public Object clone() {
        return new Intent(this);
    }

    private Intent(Intent intent, boolean z) {
        this.mAction = intent.mAction;
        this.mData = intent.mData;
        this.mType = intent.mType;
        this.mPackage = intent.mPackage;
        this.mComponent = intent.mComponent;
        if (intent.mCategories != null) {
            this.mCategories = new ArraySet<>(intent.mCategories);
        }
    }

    public Intent cloneFilter() {
        return new Intent(this, false);
    }

    public Intent(String str) {
        setAction(str);
    }

    public Intent(String str, Uri uri) {
        setAction(str);
        this.mData = uri;
    }

    public Intent(Context context, Class<?> cls) {
        this.mComponent = new ComponentName(context, cls);
    }

    public Intent(String str, Uri uri, Context context, Class<?> cls) {
        setAction(str);
        this.mData = uri;
        this.mComponent = new ComponentName(context, cls);
    }

    public static Intent makeMainActivity(ComponentName componentName) {
        Intent intent = new Intent(ACTION_MAIN);
        intent.setComponent(componentName);
        intent.addCategory(CATEGORY_LAUNCHER);
        return intent;
    }

    public static Intent makeMainSelectorActivity(String str, String str2) {
        Intent intent = new Intent(ACTION_MAIN);
        intent.addCategory(CATEGORY_LAUNCHER);
        Intent intent2 = new Intent();
        intent2.setAction(str);
        intent2.addCategory(str2);
        intent.setSelector(intent2);
        return intent;
    }

    public static Intent makeRestartActivityTask(ComponentName componentName) {
        Intent intentMakeMainActivity = makeMainActivity(componentName);
        intentMakeMainActivity.addFlags(268468224);
        return intentMakeMainActivity;
    }

    @Deprecated
    public static Intent getIntent(String str) throws URISyntaxException {
        return parseUri(str, 0);
    }

    /* JADX WARN: Removed duplicated region for block: B:15:0x0030 A[Catch: IndexOutOfBoundsException -> 0x01ff, TRY_ENTER, TryCatch #1 {IndexOutOfBoundsException -> 0x01ff, blocks: (B:15:0x0030, B:17:0x003a, B:19:0x0042, B:21:0x0047, B:23:0x004f, B:25:0x0055, B:26:0x0058, B:28:0x0060, B:30:0x0068, B:31:0x006a, B:33:0x0072, B:35:0x007f, B:37:0x0087, B:94:0x01ac, B:38:0x008c, B:40:0x0094, B:41:0x0099, B:43:0x00a2, B:44:0x00a6, B:46:0x00ae, B:47:0x00ba, B:49:0x00c3, B:50:0x00c7, B:52:0x00cf, B:53:0x00d7, B:56:0x00e3, B:58:0x00ec, B:59:0x00f4, B:61:0x00f8, B:63:0x0100, B:64:0x0107, B:66:0x0115, B:67:0x011c, B:69:0x0126, B:70:0x012b, B:72:0x0133, B:73:0x013c, B:75:0x0144, B:76:0x014c, B:78:0x0154, B:79:0x015c, B:81:0x0164, B:82:0x016c, B:84:0x0174, B:85:0x017c, B:87:0x0184, B:88:0x018c, B:90:0x0194, B:91:0x019c, B:93:0x01a5, B:95:0x01b0, B:96:0x01b8, B:98:0x01bb, B:101:0x01c2, B:103:0x01c8, B:105:0x01cf, B:106:0x01e6, B:108:0x01ec, B:111:0x01f4, B:112:0x01fd), top: B:120:0x002e, inners: #0 }] */
    /* JADX WARN: Removed duplicated region for block: B:17:0x003a A[Catch: IndexOutOfBoundsException -> 0x01ff, TryCatch #1 {IndexOutOfBoundsException -> 0x01ff, blocks: (B:15:0x0030, B:17:0x003a, B:19:0x0042, B:21:0x0047, B:23:0x004f, B:25:0x0055, B:26:0x0058, B:28:0x0060, B:30:0x0068, B:31:0x006a, B:33:0x0072, B:35:0x007f, B:37:0x0087, B:94:0x01ac, B:38:0x008c, B:40:0x0094, B:41:0x0099, B:43:0x00a2, B:44:0x00a6, B:46:0x00ae, B:47:0x00ba, B:49:0x00c3, B:50:0x00c7, B:52:0x00cf, B:53:0x00d7, B:56:0x00e3, B:58:0x00ec, B:59:0x00f4, B:61:0x00f8, B:63:0x0100, B:64:0x0107, B:66:0x0115, B:67:0x011c, B:69:0x0126, B:70:0x012b, B:72:0x0133, B:73:0x013c, B:75:0x0144, B:76:0x014c, B:78:0x0154, B:79:0x015c, B:81:0x0164, B:82:0x016c, B:84:0x0174, B:85:0x017c, B:87:0x0184, B:88:0x018c, B:90:0x0194, B:91:0x019c, B:93:0x01a5, B:95:0x01b0, B:96:0x01b8, B:98:0x01bb, B:101:0x01c2, B:103:0x01c8, B:105:0x01cf, B:106:0x01e6, B:108:0x01ec, B:111:0x01f4, B:112:0x01fd), top: B:120:0x002e, inners: #0 }] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static android.content.Intent parseUri(java.lang.String r12, int r13) throws java.net.URISyntaxException {
        /*
            Method dump skipped, instruction units count: 520
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.content.Intent.parseUri(java.lang.String, int):android.content.Intent");
    }

    public static Intent getIntentOld(String str) throws URISyntaxException {
        boolean z;
        boolean z2;
        int iLastIndexOf = str.lastIndexOf(35);
        if (iLastIndexOf >= 0) {
            String str2 = null;
            int i = iLastIndexOf + 1;
            if (str.regionMatches(i, "action(", 0, 7)) {
                int i2 = i + 7;
                int iIndexOf = str.indexOf(41, i2);
                String strSubstring = str.substring(i2, iIndexOf);
                z = true;
                i = iIndexOf + 1;
                str2 = strSubstring;
            } else {
                z = false;
            }
            Intent intent = new Intent(str2);
            if (str.regionMatches(i, "categories(", 0, 11)) {
                int i3 = i + 11;
                int iIndexOf2 = str.indexOf(41, i3);
                while (i3 < iIndexOf2) {
                    int iIndexOf3 = str.indexOf(33, i3);
                    if (iIndexOf3 < 0) {
                        iIndexOf3 = iIndexOf2;
                    }
                    if (i3 < iIndexOf3) {
                        intent.addCategory(str.substring(i3, iIndexOf3));
                    }
                    i3 = iIndexOf3 + 1;
                }
                i = iIndexOf2 + 1;
                z = true;
            }
            if (str.regionMatches(i, "type(", 0, 5)) {
                int i4 = i + 5;
                int iIndexOf4 = str.indexOf(41, i4);
                intent.mType = str.substring(i4, iIndexOf4);
                i = iIndexOf4 + 1;
                z = true;
            }
            if (str.regionMatches(i, "launchFlags(", 0, 12)) {
                int i5 = i + 12;
                int iIndexOf5 = str.indexOf(41, i5);
                intent.mFlags = Integer.decode(str.substring(i5, iIndexOf5)).intValue();
                i = iIndexOf5 + 1;
                z = true;
            }
            if (str.regionMatches(i, "component(", 0, 10)) {
                int i6 = i + 10;
                int iIndexOf6 = str.indexOf(41, i6);
                int iIndexOf7 = str.indexOf(33, i6);
                if (iIndexOf7 >= 0 && iIndexOf7 < iIndexOf6) {
                    intent.mComponent = new ComponentName(str.substring(i6, iIndexOf7), str.substring(iIndexOf7 + 1, iIndexOf6));
                }
                i = iIndexOf6 + 1;
                z = true;
            }
            if (str.regionMatches(i, "extras(", 0, 7)) {
                int i7 = i + 7;
                int iIndexOf8 = str.indexOf(41, i7);
                if (iIndexOf8 == -1) {
                    throw new URISyntaxException(str, "EXTRA missing trailing ')'", i7);
                }
                while (i7 < iIndexOf8) {
                    int iIndexOf9 = str.indexOf(61, i7);
                    int i8 = i7 + 1;
                    if (iIndexOf9 <= i8 || i7 >= iIndexOf8) {
                        throw new URISyntaxException(str, "EXTRA missing '='", i7);
                    }
                    char cCharAt = str.charAt(i7);
                    String strSubstring2 = str.substring(i8, iIndexOf9);
                    int i9 = iIndexOf9 + 1;
                    int iIndexOf10 = str.indexOf(33, i9);
                    if (iIndexOf10 == -1 || iIndexOf10 >= iIndexOf8) {
                        iIndexOf10 = iIndexOf8;
                    }
                    if (i9 >= iIndexOf10) {
                        throw new URISyntaxException(str, "EXTRA missing '!'", i9);
                    }
                    String strSubstring3 = str.substring(i9, iIndexOf10);
                    if (intent.mExtras == null) {
                        intent.mExtras = new Bundle();
                    }
                    if (cCharAt == 'B') {
                        intent.mExtras.putBoolean(strSubstring2, Boolean.parseBoolean(strSubstring3));
                    } else if (cCharAt == 'S') {
                        intent.mExtras.putString(strSubstring2, Uri.decode(strSubstring3));
                    } else if (cCharAt == 'f') {
                        intent.mExtras.putFloat(strSubstring2, Float.parseFloat(strSubstring3));
                    } else if (cCharAt == 'i') {
                        intent.mExtras.putInt(strSubstring2, Integer.parseInt(strSubstring3));
                    } else if (cCharAt == 'l') {
                        intent.mExtras.putLong(strSubstring2, Long.parseLong(strSubstring3));
                    } else {
                        if (cCharAt != 's') {
                            switch (cCharAt) {
                                case 'b':
                                    intent.mExtras.putByte(strSubstring2, Byte.parseByte(strSubstring3));
                                    break;
                                case 'c':
                                    intent.mExtras.putChar(strSubstring2, Uri.decode(strSubstring3).charAt(0));
                                    break;
                                case 'd':
                                    try {
                                        intent.mExtras.putDouble(strSubstring2, Double.parseDouble(strSubstring3));
                                    } catch (NumberFormatException unused) {
                                        throw new URISyntaxException(str, "EXTRA value can't be parsed", iIndexOf10);
                                    }
                                    break;
                                default:
                                    throw new URISyntaxException(str, "EXTRA has unknown type", iIndexOf10);
                            }
                            throw new URISyntaxException(str, "EXTRA value can't be parsed", iIndexOf10);
                        }
                        intent.mExtras.putShort(strSubstring2, Short.parseShort(strSubstring3));
                    }
                    char cCharAt2 = str.charAt(iIndexOf10);
                    if (cCharAt2 == ')') {
                        z2 = true;
                    } else {
                        if (cCharAt2 != '!') {
                            throw new URISyntaxException(str, "EXTRA missing '!'", iIndexOf10);
                        }
                        i7 = iIndexOf10 + 1;
                    }
                }
                z2 = true;
            } else {
                z2 = z;
            }
            if (z2) {
                intent.mData = Uri.parse(str.substring(0, iLastIndexOf));
            } else {
                intent.mData = Uri.parse(str);
            }
            if (intent.mAction != null) {
                return intent;
            }
            intent.mAction = "android.intent.action.VIEW";
            return intent;
        }
        return new Intent("android.intent.action.VIEW", Uri.parse(str));
    }

    public String getAction() {
        return this.mAction;
    }

    public Uri getData() {
        return this.mData;
    }

    public String getDataString() {
        Uri uri = this.mData;
        if (uri != null) {
            return uri.toString();
        }
        return null;
    }

    public String getScheme() {
        Uri uri = this.mData;
        if (uri != null) {
            return uri.getScheme();
        }
        return null;
    }

    public String getType() {
        return this.mType;
    }

    public String resolveType(Context context) {
        return resolveType(context.getContentResolver());
    }

    public String resolveType(ContentResolver contentResolver) {
        String str = this.mType;
        if (str != null) {
            return str;
        }
        Uri uri = this.mData;
        if (uri == null || !"content".equals(uri.getScheme())) {
            return null;
        }
        return contentResolver.getType(this.mData);
    }

    public String resolveTypeIfNeeded(ContentResolver contentResolver) {
        if (this.mComponent != null) {
            return this.mType;
        }
        return resolveType(contentResolver);
    }

    public boolean hasCategory(String str) {
        ArraySet<String> arraySet = this.mCategories;
        return arraySet != null && arraySet.contains(str);
    }

    public Set<String> getCategories() {
        return this.mCategories;
    }

    public Intent getSelector() {
        return this.mSelector;
    }

    public ClipData getClipData() {
        return this.mClipData;
    }

    public void setExtrasClassLoader(ClassLoader classLoader) {
        Bundle bundle = this.mExtras;
        if (bundle != null) {
            bundle.setClassLoader(classLoader);
        }
    }

    public boolean hasExtra(String str) {
        Bundle bundle = this.mExtras;
        return bundle != null && bundle.containsKey(str);
    }

    public boolean hasFileDescriptors() {
        Bundle bundle = this.mExtras;
        return bundle != null && bundle.hasFileDescriptors();
    }

    public void setAllowFds(boolean z) {
        Bundle bundle = this.mExtras;
        if (bundle != null) {
            bundle.setAllowFds(z);
        }
    }

    @Deprecated
    public Object getExtra(String str) {
        return getExtra(str, null);
    }

    public boolean getBooleanExtra(String str, boolean z) {
        Bundle bundle = this.mExtras;
        return bundle == null ? z : bundle.getBoolean(str, z);
    }

    public byte getByteExtra(String str, byte b) {
        Bundle bundle = this.mExtras;
        return bundle == null ? b : bundle.getByte(str, b).byteValue();
    }

    public short getShortExtra(String str, short s) {
        Bundle bundle = this.mExtras;
        return bundle == null ? s : bundle.getShort(str, s);
    }

    public char getCharExtra(String str, char c) {
        Bundle bundle = this.mExtras;
        return bundle == null ? c : bundle.getChar(str, c);
    }

    public int getIntExtra(String str, int i) {
        Bundle bundle = this.mExtras;
        return bundle == null ? i : bundle.getInt(str, i);
    }

    public long getLongExtra(String str, long j) {
        Bundle bundle = this.mExtras;
        return bundle == null ? j : bundle.getLong(str, j);
    }

    public float getFloatExtra(String str, float f) {
        Bundle bundle = this.mExtras;
        return bundle == null ? f : bundle.getFloat(str, f);
    }

    public double getDoubleExtra(String str, double d) {
        Bundle bundle = this.mExtras;
        return bundle == null ? d : bundle.getDouble(str, d);
    }

    public String getStringExtra(String str) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getString(str);
    }

    public CharSequence getCharSequenceExtra(String str) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getCharSequence(str);
    }

    public <T extends Parcelable> T getParcelableExtra(String str) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return (T) bundle.getParcelable(str);
    }

    public Parcelable[] getParcelableArrayExtra(String str) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getParcelableArray(str);
    }

    public <T extends Parcelable> ArrayList<T> getParcelableArrayListExtra(String str) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getParcelableArrayList(str);
    }

    public Serializable getSerializableExtra(String str) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getSerializable(str);
    }

    public ArrayList<Integer> getIntegerArrayListExtra(String str) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getIntegerArrayList(str);
    }

    public ArrayList<String> getStringArrayListExtra(String str) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getStringArrayList(str);
    }

    public ArrayList<CharSequence> getCharSequenceArrayListExtra(String str) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getCharSequenceArrayList(str);
    }

    public boolean[] getBooleanArrayExtra(String str) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getBooleanArray(str);
    }

    public byte[] getByteArrayExtra(String str) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getByteArray(str);
    }

    public short[] getShortArrayExtra(String str) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getShortArray(str);
    }

    public char[] getCharArrayExtra(String str) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getCharArray(str);
    }

    public int[] getIntArrayExtra(String str) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getIntArray(str);
    }

    public long[] getLongArrayExtra(String str) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getLongArray(str);
    }

    public float[] getFloatArrayExtra(String str) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getFloatArray(str);
    }

    public double[] getDoubleArrayExtra(String str) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getDoubleArray(str);
    }

    public String[] getStringArrayExtra(String str) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getStringArray(str);
    }

    public CharSequence[] getCharSequenceArrayExtra(String str) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getCharSequenceArray(str);
    }

    public Bundle getBundleExtra(String str) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getBundle(str);
    }

    @Deprecated
    public IBinder getIBinderExtra(String str) {
        Bundle bundle = this.mExtras;
        if (bundle == null) {
            return null;
        }
        return bundle.getIBinder(str);
    }

    @Deprecated
    public Object getExtra(String str, Object obj) {
        Object obj2;
        Bundle bundle = this.mExtras;
        return (bundle == null || (obj2 = bundle.get(str)) == null) ? obj : obj2;
    }

    public Bundle getExtras() {
        if (this.mExtras != null) {
            return new Bundle(this.mExtras);
        }
        return null;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public boolean isExcludingStopped() {
        return (this.mFlags & 48) == 16;
    }

    public String getPackage() {
        return this.mPackage;
    }

    public ComponentName getComponent() {
        return this.mComponent;
    }

    public Rect getSourceBounds() {
        return this.mSourceBounds;
    }

    public ComponentName resolveActivity(PackageManager packageManager) {
        ComponentName componentName = this.mComponent;
        if (componentName != null) {
            return componentName;
        }
        ResolveInfo resolveInfoResolveActivity = packageManager.resolveActivity(this, 65536);
        if (resolveInfoResolveActivity != null) {
            return new ComponentName(resolveInfoResolveActivity.activityInfo.applicationInfo.packageName, resolveInfoResolveActivity.activityInfo.name);
        }
        return null;
    }

    public ActivityInfo resolveActivityInfo(PackageManager packageManager, int i) {
        ComponentName componentName = this.mComponent;
        if (componentName != null) {
            try {
                return packageManager.getActivityInfo(componentName, i);
            } catch (PackageManager.NameNotFoundException unused) {
            }
        } else {
            ResolveInfo resolveInfoResolveActivity = packageManager.resolveActivity(this, i | 65536);
            if (resolveInfoResolveActivity != null) {
                return resolveInfoResolveActivity.activityInfo;
            }
        }
        return null;
    }

    public ComponentName resolveSystemService(PackageManager packageManager, int i) {
        ComponentName componentName = this.mComponent;
        if (componentName != null) {
            return componentName;
        }
        List<ResolveInfo> listQueryIntentServices = packageManager.queryIntentServices(this, i);
        ComponentName componentName2 = null;
        if (listQueryIntentServices == null) {
            return null;
        }
        for (int i2 = 0; i2 < listQueryIntentServices.size(); i2++) {
            ResolveInfo resolveInfo = listQueryIntentServices.get(i2);
            if ((resolveInfo.serviceInfo.applicationInfo.flags & 1) != 0) {
                ComponentName componentName3 = new ComponentName(resolveInfo.serviceInfo.applicationInfo.packageName, resolveInfo.serviceInfo.name);
                if (componentName2 != null) {
                    throw new IllegalStateException("Multiple system services handle " + this + ": " + componentName2 + ", " + componentName3);
                }
                componentName2 = componentName3;
            }
        }
        return componentName2;
    }

    public Intent setAction(String str) {
        this.mAction = str != null ? str.intern() : null;
        return this;
    }

    public Intent setData(Uri uri) {
        this.mData = uri;
        this.mType = null;
        return this;
    }

    public Intent setDataAndNormalize(Uri uri) {
        return setData(uri.normalizeScheme());
    }

    public Intent setType(String str) {
        this.mData = null;
        this.mType = str;
        return this;
    }

    public Intent setTypeAndNormalize(String str) {
        return setType(normalizeMimeType(str));
    }

    public Intent setDataAndType(Uri uri, String str) {
        this.mData = uri;
        this.mType = str;
        return this;
    }

    public Intent setDataAndTypeAndNormalize(Uri uri, String str) {
        return setDataAndType(uri.normalizeScheme(), normalizeMimeType(str));
    }

    public Intent addCategory(String str) {
        if (this.mCategories == null) {
            this.mCategories = new ArraySet<>();
        }
        this.mCategories.add(str.intern());
        return this;
    }

    public void removeCategory(String str) {
        ArraySet<String> arraySet = this.mCategories;
        if (arraySet != null) {
            arraySet.remove(str);
            if (this.mCategories.size() == 0) {
                this.mCategories = null;
            }
        }
    }

    public void setSelector(Intent intent) {
        if (intent == this) {
            throw new IllegalArgumentException("Intent being set as a selector of itself");
        }
        if (intent != null && this.mPackage != null) {
            throw new IllegalArgumentException("Can't set selector when package name is already set");
        }
        this.mSelector = intent;
    }

    public void setClipData(ClipData clipData) {
        this.mClipData = clipData;
    }

    public Intent putExtra(String str, boolean z) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putBoolean(str, z);
        return this;
    }

    public Intent putExtra(String str, byte b) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putByte(str, b);
        return this;
    }

    public Intent putExtra(String str, char c) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putChar(str, c);
        return this;
    }

    public Intent putExtra(String str, short s) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putShort(str, s);
        return this;
    }

    public Intent putExtra(String str, int i) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putInt(str, i);
        return this;
    }

    public Intent putExtra(String str, long j) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putLong(str, j);
        return this;
    }

    public Intent putExtra(String str, float f) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putFloat(str, f);
        return this;
    }

    public Intent putExtra(String str, double d) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putDouble(str, d);
        return this;
    }

    public Intent putExtra(String str, String str2) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putString(str, str2);
        return this;
    }

    public Intent putExtra(String str, CharSequence charSequence) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putCharSequence(str, charSequence);
        return this;
    }

    public Intent putExtra(String str, Parcelable parcelable) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putParcelable(str, parcelable);
        return this;
    }

    public Intent putExtra(String str, Parcelable[] parcelableArr) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putParcelableArray(str, parcelableArr);
        return this;
    }

    public Intent putParcelableArrayListExtra(String str, ArrayList<? extends Parcelable> arrayList) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putParcelableArrayList(str, arrayList);
        return this;
    }

    public Intent putIntegerArrayListExtra(String str, ArrayList<Integer> arrayList) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putIntegerArrayList(str, arrayList);
        return this;
    }

    public Intent putStringArrayListExtra(String str, ArrayList<String> arrayList) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putStringArrayList(str, arrayList);
        return this;
    }

    public Intent putCharSequenceArrayListExtra(String str, ArrayList<CharSequence> arrayList) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putCharSequenceArrayList(str, arrayList);
        return this;
    }

    public Intent putExtra(String str, Serializable serializable) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putSerializable(str, serializable);
        return this;
    }

    public Intent putExtra(String str, boolean[] zArr) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putBooleanArray(str, zArr);
        return this;
    }

    public Intent putExtra(String str, byte[] bArr) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putByteArray(str, bArr);
        return this;
    }

    public Intent putExtra(String str, short[] sArr) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putShortArray(str, sArr);
        return this;
    }

    public Intent putExtra(String str, char[] cArr) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putCharArray(str, cArr);
        return this;
    }

    public Intent putExtra(String str, int[] iArr) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putIntArray(str, iArr);
        return this;
    }

    public Intent putExtra(String str, long[] jArr) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putLongArray(str, jArr);
        return this;
    }

    public Intent putExtra(String str, float[] fArr) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putFloatArray(str, fArr);
        return this;
    }

    public Intent putExtra(String str, double[] dArr) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putDoubleArray(str, dArr);
        return this;
    }

    public Intent putExtra(String str, String[] strArr) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putStringArray(str, strArr);
        return this;
    }

    public Intent putExtra(String str, CharSequence[] charSequenceArr) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putCharSequenceArray(str, charSequenceArr);
        return this;
    }

    public Intent putExtra(String str, Bundle bundle) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putBundle(str, bundle);
        return this;
    }

    @Deprecated
    public Intent putExtra(String str, IBinder iBinder) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putIBinder(str, iBinder);
        return this;
    }

    public Intent putExtras(Intent intent) {
        Bundle bundle = intent.mExtras;
        if (bundle != null) {
            Bundle bundle2 = this.mExtras;
            if (bundle2 == null) {
                this.mExtras = new Bundle(intent.mExtras);
            } else {
                bundle2.putAll(bundle);
            }
        }
        return this;
    }

    public Intent putExtras(Bundle bundle) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putAll(bundle);
        return this;
    }

    public Intent replaceExtras(Intent intent) {
        this.mExtras = intent.mExtras != null ? new Bundle(intent.mExtras) : null;
        return this;
    }

    public Intent replaceExtras(Bundle bundle) {
        this.mExtras = bundle != null ? new Bundle(bundle) : null;
        return this;
    }

    public void removeExtra(String str) {
        Bundle bundle = this.mExtras;
        if (bundle != null) {
            bundle.remove(str);
            if (this.mExtras.size() == 0) {
                this.mExtras = null;
            }
        }
    }

    public Intent setFlags(int i) {
        this.mFlags = i;
        return this;
    }

    public Intent addFlags(int i) {
        this.mFlags = i | this.mFlags;
        return this;
    }

    public Intent setPackage(String str) {
        if (str != null && this.mSelector != null) {
            throw new IllegalArgumentException("Can't set package name when selector is already set");
        }
        this.mPackage = str;
        return this;
    }

    public Intent setComponent(ComponentName componentName) {
        this.mComponent = componentName;
        return this;
    }

    public Intent setClassName(Context context, String str) {
        this.mComponent = new ComponentName(context, str);
        return this;
    }

    public Intent setClassName(String str, String str2) {
        this.mComponent = new ComponentName(str, str2);
        return this;
    }

    public Intent setClass(Context context, Class<?> cls) {
        this.mComponent = new ComponentName(context, cls);
        return this;
    }

    public void setSourceBounds(Rect rect) {
        if (rect != null) {
            this.mSourceBounds = new Rect(rect);
        } else {
            this.mSourceBounds = null;
        }
    }

    public int fillIn(Intent intent, int i) {
        int i2;
        String str = intent.mAction;
        if (str == null || (this.mAction != null && (i & 1) == 0)) {
            i2 = 0;
        } else {
            this.mAction = str;
            i2 = 1;
        }
        Uri uri = intent.mData;
        if ((uri != null || intent.mType != null) && ((this.mData == null && this.mType == null) || (i & 2) != 0)) {
            this.mData = uri;
            this.mType = intent.mType;
            i2 |= 2;
        }
        ArraySet<String> arraySet = intent.mCategories;
        if (arraySet != null && (this.mCategories == null || (i & 4) != 0)) {
            if (arraySet != null) {
                this.mCategories = new ArraySet<>(intent.mCategories);
            }
            i2 |= 4;
        }
        String str2 = intent.mPackage;
        if (str2 != null && ((this.mPackage == null || (i & 16) != 0) && this.mSelector == null)) {
            this.mPackage = str2;
            i2 |= 16;
        }
        if (intent.mSelector != null && (i & 64) != 0 && this.mPackage == null) {
            this.mSelector = new Intent(intent.mSelector);
            this.mPackage = null;
            i2 |= 64;
        }
        ClipData clipData = intent.mClipData;
        if (clipData != null && (this.mClipData == null || (i & 128) != 0)) {
            this.mClipData = clipData;
            i2 |= 128;
        }
        ComponentName componentName = intent.mComponent;
        if (componentName != null && (i & 8) != 0) {
            this.mComponent = componentName;
            i2 |= 8;
        }
        this.mFlags |= intent.mFlags;
        if (intent.mSourceBounds != null && (this.mSourceBounds == null || (i & 32) != 0)) {
            this.mSourceBounds = new Rect(intent.mSourceBounds);
            i2 |= 32;
        }
        if (this.mExtras == null) {
            if (intent.mExtras != null) {
                this.mExtras = new Bundle(intent.mExtras);
            }
        } else if (intent.mExtras != null) {
            try {
                Bundle bundle = new Bundle(intent.mExtras);
                bundle.putAll(this.mExtras);
                this.mExtras = bundle;
            } catch (RuntimeException e) {
                Log.w("Intent", "Failure filling in extras", e);
            }
        }
        return i2;
    }

    public static final class FilterComparison {
        private final int mHashCode;
        private final Intent mIntent;

        public FilterComparison(Intent intent) {
            this.mIntent = intent;
            this.mHashCode = intent.filterHashCode();
        }

        public Intent getIntent() {
            return this.mIntent;
        }

        public boolean equals(Object obj) {
            if (!(obj instanceof FilterComparison)) {
                return false;
            }
            return this.mIntent.filterEquals(((FilterComparison) obj).mIntent);
        }

        public int hashCode() {
            return this.mHashCode;
        }
    }

    /* JADX WARN: Type inference fix 'apply assigned field type' failed
    java.lang.UnsupportedOperationException: ArgType.getObject(), call class: class jadx.core.dex.instructions.args.ArgType$UnknownArg
    	at jadx.core.dex.instructions.args.ArgType.getObject(ArgType.java:593)
    	at jadx.core.dex.attributes.nodes.ClassTypeVarsAttr.getTypeVarsMapFor(ClassTypeVarsAttr.java:35)
    	at jadx.core.dex.nodes.utils.TypeUtils.replaceClassGenerics(TypeUtils.java:177)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.insertExplicitUseCast(FixTypesVisitor.java:397)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.tryFieldTypeWithNewCasts(FixTypesVisitor.java:359)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.applyFieldType(FixTypesVisitor.java:309)
    	at jadx.core.dex.visitors.typeinference.FixTypesVisitor.visit(FixTypesVisitor.java:94)
     */
    public boolean filterEquals(Intent intent) {
        if (intent == null) {
            return false;
        }
        String str = this.mAction;
        String str2 = intent.mAction;
        if (str != str2) {
            if (str != null) {
                if (!str.equals(str2)) {
                    return false;
                }
            } else if (!str2.equals(str)) {
                return false;
            }
        }
        Uri uri = this.mData;
        Uri uri2 = intent.mData;
        if (uri != uri2) {
            if (uri != null) {
                if (!uri.equals(uri2)) {
                    return false;
                }
            } else if (!uri2.equals(uri)) {
                return false;
            }
        }
        String str3 = this.mType;
        String str4 = intent.mType;
        if (str3 != str4) {
            if (str3 != null) {
                if (!str3.equals(str4)) {
                    return false;
                }
            } else if (!str4.equals(str3)) {
                return false;
            }
        }
        String str5 = this.mPackage;
        String str6 = intent.mPackage;
        if (str5 != str6) {
            if (str5 != null) {
                if (!str5.equals(str6)) {
                    return false;
                }
            } else if (!str6.equals(str5)) {
                return false;
            }
        }
        ComponentName componentName = this.mComponent;
        ComponentName componentName2 = intent.mComponent;
        if (componentName != componentName2) {
            if (componentName != null) {
                if (!componentName.equals(componentName2)) {
                    return false;
                }
            } else if (!componentName2.equals(componentName)) {
                return false;
            }
        }
        ArraySet<String> arraySet = this.mCategories;
        ArraySet<String> arraySet2 = intent.mCategories;
        if (arraySet != arraySet2) {
            return arraySet != null ? arraySet.equals(arraySet2) : arraySet2.equals(arraySet);
        }
        return true;
    }

    public int filterHashCode() {
        String str = this.mAction;
        int iHashCode = str != null ? 0 + str.hashCode() : 0;
        Uri uri = this.mData;
        if (uri != null) {
            iHashCode += uri.hashCode();
        }
        String str2 = this.mType;
        if (str2 != null) {
            iHashCode += str2.hashCode();
        }
        String str3 = this.mPackage;
        if (str3 != null) {
            iHashCode += str3.hashCode();
        }
        ComponentName componentName = this.mComponent;
        if (componentName != null) {
            iHashCode += componentName.hashCode();
        }
        ArraySet<String> arraySet = this.mCategories;
        return arraySet != null ? iHashCode + arraySet.hashCode() : iHashCode;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("Intent { ");
        toShortString(sb, true, true, true, false);
        sb.append(" }");
        return sb.toString();
    }

    public String toInsecureString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("Intent { ");
        toShortString(sb, false, true, true, false);
        sb.append(" }");
        return sb.toString();
    }

    public String toInsecureStringWithClip() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("Intent { ");
        toShortString(sb, false, true, true, true);
        sb.append(" }");
        return sb.toString();
    }

    public String toShortString(boolean z, boolean z2, boolean z3, boolean z4) {
        StringBuilder sb = new StringBuilder(128);
        toShortString(sb, z, z2, z3, z4);
        return sb.toString();
    }

    public void toShortString(StringBuilder sb, boolean z, boolean z2, boolean z3, boolean z4) {
        boolean z5;
        boolean z6 = false;
        if (this.mAction != null) {
            sb.append("act=").append(this.mAction);
            z5 = false;
        } else {
            z5 = true;
        }
        if (this.mCategories != null) {
            if (!z5) {
                sb.append(' ');
            }
            sb.append("cat=[");
            for (int i = 0; i < this.mCategories.size(); i++) {
                if (i > 0) {
                    sb.append(PhoneNumberUtils.PAUSE);
                }
                sb.append(this.mCategories.valueAt(i));
            }
            sb.append("]");
            z5 = false;
        }
        if (this.mData != null) {
            if (!z5) {
                sb.append(' ');
            }
            sb.append("dat=");
            if (z) {
                sb.append(this.mData.toSafeString());
            } else {
                sb.append(this.mData);
            }
            z5 = false;
        }
        if (this.mType != null) {
            if (!z5) {
                sb.append(' ');
            }
            sb.append("typ=").append(this.mType);
            z5 = false;
        }
        if (this.mFlags != 0) {
            if (!z5) {
                sb.append(' ');
            }
            sb.append("flg=0x").append(Integer.toHexString(this.mFlags));
            z5 = false;
        }
        if (this.mPackage != null) {
            if (!z5) {
                sb.append(' ');
            }
            sb.append("pkg=").append(this.mPackage);
            z5 = false;
        }
        if (z2 && this.mComponent != null) {
            if (!z5) {
                sb.append(' ');
            }
            sb.append("cmp=").append(this.mComponent.flattenToShortString());
            z5 = false;
        }
        if (this.mSourceBounds != null) {
            if (!z5) {
                sb.append(' ');
            }
            sb.append("bnds=").append(this.mSourceBounds.toShortString());
            z5 = false;
        }
        if (this.mClipData != null) {
            if (!z5) {
                sb.append(' ');
            }
            if (z4) {
                sb.append("clip={");
                this.mClipData.toShortString(sb);
                sb.append('}');
            } else {
                sb.append("(has clip)");
            }
        } else {
            z6 = z5;
        }
        if (z3 && this.mExtras != null) {
            if (!z6) {
                sb.append(' ');
            }
            sb.append("(has extras)");
        }
        if (this.mSelector != null) {
            sb.append(" sel={");
            this.mSelector.toShortString(sb, z, z2, z3, z4);
            sb.append("}");
        }
    }

    @Deprecated
    public String toURI() {
        return toUri(0);
    }

    public String toUri(int i) {
        String strSubstring;
        StringBuilder sb = new StringBuilder(128);
        Uri uri = this.mData;
        if (uri != null) {
            String string = uri.toString();
            if ((i & 1) != 0) {
                int length = string.length();
                int i2 = 0;
                while (true) {
                    if (i2 >= length) {
                        break;
                    }
                    char cCharAt = string.charAt(i2);
                    if ((cCharAt >= 'a' && cCharAt <= 'z') || ((cCharAt >= 'A' && cCharAt <= 'Z') || cCharAt == '.' || cCharAt == '-')) {
                        i2++;
                    } else {
                        if (cCharAt != ':' || i2 <= 0) {
                            break;
                        }
                        strSubstring = string.substring(0, i2);
                        sb.append("intent:");
                        string = string.substring(i2 + 1);
                    }
                }
                strSubstring = null;
                sb.append(string);
            } else {
                strSubstring = null;
                sb.append(string);
            }
        } else {
            if ((i & 1) != 0) {
                sb.append("intent:");
            }
            strSubstring = null;
        }
        sb.append("#Intent;");
        toUriInner(sb, strSubstring, i);
        if (this.mSelector != null) {
            sb.append("SEL;");
            this.mSelector.toUriInner(sb, null, i);
        }
        sb.append("end");
        return sb.toString();
    }

    private void toUriInner(StringBuilder sb, String str, int i) {
        if (str != null) {
            sb.append("scheme=").append(str).append(PhoneNumberUtils.WAIT);
        }
        if (this.mAction != null) {
            sb.append("action=").append(Uri.encode(this.mAction)).append(PhoneNumberUtils.WAIT);
        }
        if (this.mCategories != null) {
            for (int i2 = 0; i2 < this.mCategories.size(); i2++) {
                sb.append("category=").append(Uri.encode(this.mCategories.valueAt(i2))).append(PhoneNumberUtils.WAIT);
            }
        }
        if (this.mType != null) {
            sb.append("type=").append(Uri.encode(this.mType, "/")).append(PhoneNumberUtils.WAIT);
        }
        if (this.mFlags != 0) {
            sb.append("launchFlags=0x").append(Integer.toHexString(this.mFlags)).append(PhoneNumberUtils.WAIT);
        }
        if (this.mPackage != null) {
            sb.append("package=").append(Uri.encode(this.mPackage)).append(PhoneNumberUtils.WAIT);
        }
        if (this.mComponent != null) {
            sb.append("component=").append(Uri.encode(this.mComponent.flattenToShortString(), "/")).append(PhoneNumberUtils.WAIT);
        }
        if (this.mSourceBounds != null) {
            sb.append("sourceBounds=").append(Uri.encode(this.mSourceBounds.flattenToString())).append(PhoneNumberUtils.WAIT);
        }
        Bundle bundle = this.mExtras;
        if (bundle != null) {
            for (String str2 : bundle.keySet()) {
                Object obj = this.mExtras.get(str2);
                char c = obj instanceof String ? 'S' : obj instanceof Boolean ? 'B' : obj instanceof Byte ? 'b' : obj instanceof Character ? 'c' : obj instanceof Double ? DateFormat.DATE : obj instanceof Float ? 'f' : obj instanceof Integer ? 'i' : obj instanceof Long ? 'l' : obj instanceof Short ? 's' : (char) 0;
                if (c != 0) {
                    sb.append(c);
                    sb.append('.');
                    sb.append(Uri.encode(str2));
                    sb.append('=');
                    sb.append(Uri.encode(obj.toString()));
                    sb.append(PhoneNumberUtils.WAIT);
                }
            }
        }
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        Bundle bundle = this.mExtras;
        if (bundle != null) {
            return bundle.describeContents();
        }
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.mAction);
        Uri.writeToParcel(parcel, this.mData);
        parcel.writeString(this.mType);
        parcel.writeInt(this.mFlags);
        parcel.writeString(this.mPackage);
        ComponentName.writeToParcel(this.mComponent, parcel);
        if (this.mSourceBounds != null) {
            parcel.writeInt(1);
            this.mSourceBounds.writeToParcel(parcel, i);
        } else {
            parcel.writeInt(0);
        }
        ArraySet<String> arraySet = this.mCategories;
        if (arraySet != null) {
            int size = arraySet.size();
            parcel.writeInt(size);
            for (int i2 = 0; i2 < size; i2++) {
                parcel.writeString(this.mCategories.valueAt(i2));
            }
        } else {
            parcel.writeInt(0);
        }
        if (this.mSelector != null) {
            parcel.writeInt(1);
            this.mSelector.writeToParcel(parcel, i);
        } else {
            parcel.writeInt(0);
        }
        if (this.mClipData != null) {
            parcel.writeInt(1);
            this.mClipData.writeToParcel(parcel, i);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeBundle(this.mExtras);
    }

    protected Intent(Parcel parcel) {
        readFromParcel(parcel);
    }

    public void readFromParcel(Parcel parcel) {
        setAction(parcel.readString());
        this.mData = Uri.CREATOR.createFromParcel(parcel);
        this.mType = parcel.readString();
        this.mFlags = parcel.readInt();
        this.mPackage = parcel.readString();
        this.mComponent = ComponentName.readFromParcel(parcel);
        if (parcel.readInt() != 0) {
            this.mSourceBounds = Rect.CREATOR.createFromParcel(parcel);
        }
        int i = parcel.readInt();
        if (i > 0) {
            this.mCategories = new ArraySet<>();
            for (int i2 = 0; i2 < i; i2++) {
                this.mCategories.add(parcel.readString().intern());
            }
        } else {
            this.mCategories = null;
        }
        if (parcel.readInt() != 0) {
            this.mSelector = new Intent(parcel);
        }
        if (parcel.readInt() != 0) {
            this.mClipData = new ClipData(parcel);
        }
        this.mExtras = parcel.readBundle();
    }

    public static Intent parseIntent(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet) throws XmlPullParserException, IOException {
        Intent intent = new Intent();
        TypedArray typedArrayObtainAttributes = resources.obtainAttributes(attributeSet, R.styleable.Intent);
        intent.setAction(typedArrayObtainAttributes.getString(2));
        String string = typedArrayObtainAttributes.getString(3);
        intent.setDataAndType(string != null ? Uri.parse(string) : null, typedArrayObtainAttributes.getString(1));
        String string2 = typedArrayObtainAttributes.getString(0);
        String string3 = typedArrayObtainAttributes.getString(4);
        if (string2 != null && string3 != null) {
            intent.setComponent(new ComponentName(string2, string3));
        }
        typedArrayObtainAttributes.recycle();
        int depth = xmlPullParser.getDepth();
        while (true) {
            int next = xmlPullParser.next();
            if (next == 1 || (next == 3 && xmlPullParser.getDepth() <= depth)) {
                break;
            }
            if (next != 3 && next != 4) {
                String name = xmlPullParser.getName();
                if (name.equals("category")) {
                    TypedArray typedArrayObtainAttributes2 = resources.obtainAttributes(attributeSet, R.styleable.IntentCategory);
                    String string4 = typedArrayObtainAttributes2.getString(0);
                    typedArrayObtainAttributes2.recycle();
                    if (string4 != null) {
                        intent.addCategory(string4);
                    }
                    XmlUtils.skipCurrentTag(xmlPullParser);
                } else if (name.equals("extra")) {
                    if (intent.mExtras == null) {
                        intent.mExtras = new Bundle();
                    }
                    resources.parseBundleExtra("extra", attributeSet, intent.mExtras);
                    XmlUtils.skipCurrentTag(xmlPullParser);
                } else {
                    XmlUtils.skipCurrentTag(xmlPullParser);
                }
            }
        }
        return intent;
    }

    public static String normalizeMimeType(String str) {
        if (str == null) {
            return null;
        }
        String lowerCase = str.trim().toLowerCase(Locale.ROOT);
        int iIndexOf = lowerCase.indexOf(59);
        return iIndexOf != -1 ? lowerCase.substring(0, iIndexOf) : lowerCase;
    }

    public void prepareToLeaveProcess() {
        setAllowFds(false);
        Intent intent = this.mSelector;
        if (intent != null) {
            intent.prepareToLeaveProcess();
        }
        ClipData clipData = this.mClipData;
        if (clipData != null) {
            clipData.prepareToLeaveProcess();
        }
        if (this.mData == null || !StrictMode.vmFileUriExposureEnabled()) {
            return;
        }
        if ("android.intent.action.VIEW".equals(this.mAction) || ACTION_EDIT.equals(this.mAction) || ACTION_ATTACH_DATA.equals(this.mAction)) {
            this.mData.checkFileUriExposed("Intent.getData()");
        }
    }

    public boolean migrateExtraStreamToClipData() {
        Bundle bundle = this.mExtras;
        if ((bundle != null && bundle.isParcelled()) || getClipData() != null) {
            return false;
        }
        String action = getAction();
        if (ACTION_CHOOSER.equals(action)) {
            Intent intent = (Intent) getParcelableExtra(EXTRA_INTENT);
            if (intent == null || !intent.migrateExtraStreamToClipData()) {
                return false;
            }
            setClipData(intent.getClipData());
            addFlags(intent.getFlags() & 67);
            return true;
        }
        if (ACTION_SEND.equals(action)) {
            Uri uri = (Uri) getParcelableExtra(EXTRA_STREAM);
            CharSequence charSequenceExtra = getCharSequenceExtra(EXTRA_TEXT);
            String stringExtra = getStringExtra("android.intent.extra.HTML_TEXT");
            if (uri != null || charSequenceExtra != null || stringExtra != null) {
                setClipData(new ClipData(null, new String[]{getType()}, new ClipData.Item(charSequenceExtra, stringExtra, null, uri)));
                addFlags(1);
                return true;
            }
        } else if (ACTION_SEND_MULTIPLE.equals(action)) {
            ArrayList parcelableArrayListExtra = getParcelableArrayListExtra(EXTRA_STREAM);
            ArrayList<CharSequence> charSequenceArrayListExtra = getCharSequenceArrayListExtra(EXTRA_TEXT);
            ArrayList<String> stringArrayListExtra = getStringArrayListExtra("android.intent.extra.HTML_TEXT");
            int size = parcelableArrayListExtra != null ? parcelableArrayListExtra.size() : -1;
            if (charSequenceArrayListExtra != null) {
                if (size >= 0 && size != charSequenceArrayListExtra.size()) {
                    return false;
                }
                size = charSequenceArrayListExtra.size();
            }
            if (stringArrayListExtra != null) {
                if (size >= 0 && size != stringArrayListExtra.size()) {
                    return false;
                }
                size = stringArrayListExtra.size();
            }
            if (size > 0) {
                ClipData clipData = new ClipData(null, new String[]{getType()}, makeClipItem(parcelableArrayListExtra, charSequenceArrayListExtra, stringArrayListExtra, 0));
                for (int i = 1; i < size; i++) {
                    clipData.addItem(makeClipItem(parcelableArrayListExtra, charSequenceArrayListExtra, stringArrayListExtra, i));
                }
                setClipData(clipData);
                addFlags(1);
                return true;
            }
        }
        return false;
    }

    private static ClipData.Item makeClipItem(ArrayList<Uri> arrayList, ArrayList<CharSequence> arrayList2, ArrayList<String> arrayList3, int i) {
        return new ClipData.Item(arrayList2 != null ? arrayList2.get(i) : null, arrayList3 != null ? arrayList3.get(i) : null, null, arrayList != null ? arrayList.get(i) : null);
    }
}
