package android.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.IContentProvider;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.util.AndroidException;
import android.util.Log;
import com.android.internal.widget.ILockSettings;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public final class Settings {
    public static final String ACTION_ACCESSIBILITY_SETTINGS = "android.settings.ACCESSIBILITY_SETTINGS";
    public static final String ACTION_ADD_ACCOUNT = "android.settings.ADD_ACCOUNT_SETTINGS";
    public static final String ACTION_AIRPLANE_MODE_SETTINGS = "android.settings.AIRPLANE_MODE_SETTINGS";
    public static final String ACTION_APN_SETTINGS = "android.settings.APN_SETTINGS";
    public static final String ACTION_APPLICATION_DETAILS_SETTINGS = "android.settings.APPLICATION_DETAILS_SETTINGS";
    public static final String ACTION_APPLICATION_DEVELOPMENT_SETTINGS = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS";
    public static final String ACTION_APPLICATION_SETTINGS = "android.settings.APPLICATION_SETTINGS";
    public static final String ACTION_APP_OPS_SETTINGS = "android.settings.APP_OPS_SETTINGS";
    public static final String ACTION_BLUETOOTH_SETTINGS = "android.settings.BLUETOOTH_SETTINGS";
    public static final String ACTION_CAPTIONING_SETTINGS = "android.settings.CAPTIONING_SETTINGS";
    public static final String ACTION_DATA_ROAMING_SETTINGS = "android.settings.DATA_ROAMING_SETTINGS";
    public static final String ACTION_DATE_SETTINGS = "android.settings.DATE_SETTINGS";
    public static final String ACTION_DEVICE_INFO_SETTINGS = "android.settings.DEVICE_INFO_SETTINGS";
    public static final String ACTION_DISPLAY_SETTINGS = "android.settings.DISPLAY_SETTINGS";
    public static final String ACTION_DREAM_SETTINGS = "android.settings.DREAM_SETTINGS";
    public static final String ACTION_ETHERNET_SETTINGS = "android.settings.ETHERNET_SETTINGS";
    public static final String ACTION_INPUT_METHOD_SETTINGS = "android.settings.INPUT_METHOD_SETTINGS";
    public static final String ACTION_INPUT_METHOD_SUBTYPE_SETTINGS = "android.settings.INPUT_METHOD_SUBTYPE_SETTINGS";
    public static final String ACTION_INTERNAL_STORAGE_SETTINGS = "android.settings.INTERNAL_STORAGE_SETTINGS";
    public static final String ACTION_LOCALE_SETTINGS = "android.settings.LOCALE_SETTINGS";
    public static final String ACTION_LOCATION_SOURCE_SETTINGS = "android.settings.LOCATION_SOURCE_SETTINGS";
    public static final String ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS = "android.settings.MANAGE_ALL_APPLICATIONS_SETTINGS";
    public static final String ACTION_MANAGE_APPLICATIONS_SETTINGS = "android.settings.MANAGE_APPLICATIONS_SETTINGS";
    public static final String ACTION_MEMORY_CARD_SETTINGS = "android.settings.MEMORY_CARD_SETTINGS";
    public static final String ACTION_MONITORING_CERT_INFO = "com.android.settings.MONITORING_CERT_INFO";
    public static final String ACTION_NETWORK_OPERATOR_SETTINGS = "android.settings.NETWORK_OPERATOR_SETTINGS";
    public static final String ACTION_NFCSHARING_SETTINGS = "android.settings.NFCSHARING_SETTINGS";
    public static final String ACTION_NFC_PAYMENT_SETTINGS = "android.settings.NFC_PAYMENT_SETTINGS";
    public static final String ACTION_NFC_SETTINGS = "android.settings.NFC_SETTINGS";
    public static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.NOTIFICATION_LISTENER_SETTINGS";
    public static final String ACTION_PRINT_SETTINGS = "android.settings.ACTION_PRINT_SETTINGS";
    public static final String ACTION_PRIVACY_SETTINGS = "android.settings.PRIVACY_SETTINGS";
    public static final String ACTION_QUICK_LAUNCH_SETTINGS = "android.settings.QUICK_LAUNCH_SETTINGS";
    public static final String ACTION_SEARCH_SETTINGS = "android.search.action.SEARCH_SETTINGS";
    public static final String ACTION_SECURITY_SETTINGS = "android.settings.SECURITY_SETTINGS";
    public static final String ACTION_SETTINGS = "android.settings.SETTINGS";
    public static final String ACTION_SHOW_INPUT_METHOD_PICKER = "android.settings.SHOW_INPUT_METHOD_PICKER";
    public static final String ACTION_SOUND_SETTINGS = "android.settings.SOUND_SETTINGS";
    public static final String ACTION_SYNC_SETTINGS = "android.settings.SYNC_SETTINGS";
    public static final String ACTION_SYSTEM_UPDATE_SETTINGS = "android.settings.SYSTEM_UPDATE_SETTINGS";
    public static final String ACTION_TRUSTED_CREDENTIALS_USER = "com.android.settings.TRUSTED_CREDENTIALS_USER";
    public static final String ACTION_USER_DICTIONARY_INSERT = "com.android.settings.USER_DICTIONARY_INSERT";
    public static final String ACTION_USER_DICTIONARY_SETTINGS = "android.settings.USER_DICTIONARY_SETTINGS";
    public static final String ACTION_WIFI_DISPLAY_SETTINGS = "android.settings.WIFI_DISPLAY_SETTINGS";
    public static final String ACTION_WIFI_IP_SETTINGS = "android.settings.WIFI_IP_SETTINGS";
    public static final String ACTION_WIFI_SETTINGS = "android.settings.WIFI_SETTINGS";
    public static final String ACTION_WIRELESS_SETTINGS = "android.settings.WIRELESS_SETTINGS";
    public static final String AUTHORITY = "settings";
    public static final String CALL_METHOD_GET_GLOBAL = "GET_global";
    public static final String CALL_METHOD_GET_SECURE = "GET_secure";
    public static final String CALL_METHOD_GET_SYSTEM = "GET_system";
    public static final String CALL_METHOD_PUT_GLOBAL = "PUT_global";
    public static final String CALL_METHOD_PUT_SECURE = "PUT_secure";
    public static final String CALL_METHOD_PUT_SYSTEM = "PUT_system";
    public static final String CALL_METHOD_USER_KEY = "_user";
    public static final String EXTRA_ACCOUNT_TYPES = "account_types";
    public static final String EXTRA_AUTHORITIES = "authorities";
    public static final String EXTRA_INPUT_METHOD_ID = "input_method_id";
    private static final String JID_RESOURCE_PREFIX = "android";
    private static final boolean LOCAL_LOGV = false;
    private static final String TAG = "Settings";
    private static final Object mLocationSettingsLock = new Object();

    public static class SettingNotFoundException extends AndroidException {
        public SettingNotFoundException(String str) {
            super(str);
        }
    }

    public static class NameValueTable implements BaseColumns {
        public static final String NAME = "name";
        public static final String VALUE = "value";

        protected static boolean putString(ContentResolver contentResolver, Uri uri, String str, String str2) {
            try {
                ContentValues contentValues = new ContentValues();
                contentValues.put("name", str);
                contentValues.put("value", str2);
                contentResolver.insert(uri, contentValues);
                return true;
            } catch (SQLException e) {
                Log.w(Settings.TAG, "Can't set key " + str + " in " + uri, e);
                return false;
            }
        }

        public static Uri getUriFor(Uri uri, String str) {
            return Uri.withAppendedPath(uri, str);
        }
    }

    private static class NameValueCache {
        private static final String NAME_EQ_PLACEHOLDER = "name=?";
        private static final String[] SELECT_VALUE = {"value"};
        private final String mCallGetCommand;
        private final String mCallSetCommand;
        private final Uri mUri;
        private final String mVersionSystemProperty;
        private final HashMap<String, String> mValues = new HashMap<>();
        private long mValuesVersion = 0;
        private IContentProvider mContentProvider = null;

        public NameValueCache(String str, Uri uri, String str2, String str3) {
            this.mVersionSystemProperty = str;
            this.mUri = uri;
            this.mCallGetCommand = str2;
            this.mCallSetCommand = str3;
        }

        private IContentProvider lazyGetProvider(ContentResolver contentResolver) {
            IContentProvider iContentProviderAcquireProvider;
            synchronized (this) {
                iContentProviderAcquireProvider = this.mContentProvider;
                if (iContentProviderAcquireProvider == null) {
                    iContentProviderAcquireProvider = contentResolver.acquireProvider(this.mUri.getAuthority());
                    this.mContentProvider = iContentProviderAcquireProvider;
                }
            }
            return iContentProviderAcquireProvider;
        }

        public boolean putStringForUser(ContentResolver contentResolver, String str, String str2, int i) {
            try {
                Bundle bundle = new Bundle();
                bundle.putString("value", str2);
                bundle.putInt(Settings.CALL_METHOD_USER_KEY, i);
                lazyGetProvider(contentResolver).call(contentResolver.getPackageName(), this.mCallSetCommand, str, bundle);
                return true;
            } catch (RemoteException e) {
                Log.w(Settings.TAG, "Can't set key " + str + " in " + this.mUri, e);
                return false;
            }
        }

        /* JADX WARN: Multi-variable type inference failed */
        /* JADX WARN: Removed duplicated region for block: B:71:0x00ff  */
        /* JADX WARN: Type inference failed for: r11v0 */
        /* JADX WARN: Type inference failed for: r11v1, types: [android.database.Cursor] */
        /* JADX WARN: Type inference failed for: r11v2 */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public java.lang.String getStringForUser(android.content.ContentResolver r13, java.lang.String r14, int r15) throws java.lang.Throwable {
            /*
                Method dump skipped, instruction units count: 259
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: android.provider.Settings.NameValueCache.getStringForUser(android.content.ContentResolver, java.lang.String, int):java.lang.String");
        }
    }

    public static final class System extends NameValueTable {
        public static final String ACCELEROMETER_COORDINATE = "accelerometer_coordinate";
        public static final String ACCELEROMETER_ROTATION = "accelerometer_rotation";

        @Deprecated
        public static final String ADB_ENABLED = "adb_enabled";
        public static final String ADVANCED_SETTINGS = "advanced_settings";
        public static final int ADVANCED_SETTINGS_DEFAULT = 0;

        @Deprecated
        public static final String AIRPLANE_MODE_ON = "airplane_mode_on";

        @Deprecated
        public static final String AIRPLANE_MODE_RADIOS = "airplane_mode_radios";

        @Deprecated
        public static final String AIRPLANE_MODE_TOGGLEABLE_RADIOS = "airplane_mode_toggleable_radios";
        public static final String ALARM_ALERT = "alarm_alert";

        @Deprecated
        public static final String ALWAYS_FINISH_ACTIVITIES = "always_finish_activities";

        @Deprecated
        public static final String ANDROID_ID = "android_id";

        @Deprecated
        public static final String ANIMATOR_DURATION_SCALE = "animator_duration_scale";
        public static final String APPEND_FOR_LAST_AUDIBLE = "_last_audible";
        public static final String AUDIO_MANAGE_POLICY = "audio_manage_policy";
        public static final String AUDIO_OUTPUT_CHANNEL = "audio_output_channel";
        public static final String AUDIO_OUTPUT_TYPE = "audio_output_type";

        @Deprecated
        public static final String AUTO_TIME = "auto_time";

        @Deprecated
        public static final String AUTO_TIME_ZONE = "auto_time_zone";
        public static final String BD_FOLDER_PLAY_MODE = "bd_folder_play_mode";
        public static final String BLUETOOTH_DISCOVERABILITY = "bluetooth_discoverability";
        public static final String BLUETOOTH_DISCOVERABILITY_TIMEOUT = "bluetooth_discoverability_timeout";

        @Deprecated
        public static final String BLUETOOTH_ON = "bluetooth_on";
        public static final String BOOT_FAST_ENABLE = "boot_fast_enable";
        public static final String BRIGHTNESS_LIGHT_MODE = "brightness_light_mode";
        public static final String BRIGHT_SYSTEM_MODE = "bright_system_mode";

        @Deprecated
        public static final String CAR_DOCK_SOUND = "car_dock_sound";

        @Deprecated
        public static final String CAR_UNDOCK_SOUND = "car_undock_sound";
        public static final String COLOR_BRIGHTNESS = "color_brightness";
        public static final String COLOR_CONTRAST = "color_contrast";
        public static final String COLOR_SATURATION = "color_saturation";
        public static final Uri CONTENT_URI;
        public static final String CPU_FAST_ENABLE = "cpu_fast_enable";

        @Deprecated
        public static final String DATA_ROAMING = "data_roaming";
        public static final String DATE_FORMAT = "date_format";

        @Deprecated
        public static final String DEBUG_APP = "debug_app";
        public static final Uri DEFAULT_ALARM_ALERT_URI;
        public static final Uri DEFAULT_NOTIFICATION_URI;
        public static final Uri DEFAULT_RINGTONE_URI;

        @Deprecated
        public static final String DESK_DOCK_SOUND = "desk_dock_sound";

        @Deprecated
        public static final String DESK_UNDOCK_SOUND = "desk_undock_sound";

        @Deprecated
        public static final String DEVICE_PROVISIONED = "device_provisioned";

        @Deprecated
        public static final String DIM_SCREEN = "dim_screen";
        public static final String DIRECTLY_POWER_OFF = "directly_power_off";
        public static final String DISPLAY2_ENHANCE_MODE = "display2_enhance_mode";
        public static final String DISPLAY_ADAPTION_ENABLE = "display_adapter_enable";
        public static final String DISPLAY_ADAPTION_MODE = "display_adaption_mode";
        public static final String DISPLAY_AREA_H_PERCENT = "display_area_h_percent";
        public static final String DISPLAY_AREA_RATIO = "display_area_ratio";
        public static final String DISPLAY_AREA_V_PERCENT = "display_area_v_percent";
        public static final String DISPLAY_OUTPUT_FORMAT = "display_output_format";

        @Deprecated
        public static final String DOCK_SOUNDS_ENABLED = "dock_sounds_enabled";
        public static final String DTMF_TONE_TYPE_WHEN_DIALING = "dtmf_tone_type";
        public static final String DTMF_TONE_WHEN_DIALING = "dtmf_tone";
        public static final String EGG_MODE = "egg_mode";
        public static final String ENABLE_PASS_THROUGH = "enable_pass_through";
        public static final String END_BUTTON_BEHAVIOR = "end_button_behavior";
        public static final int END_BUTTON_BEHAVIOR_DEFAULT = 2;
        public static final int END_BUTTON_BEHAVIOR_HOME = 1;
        public static final int END_BUTTON_BEHAVIOR_SLEEP = 2;
        public static final String FONT_SCALE = "font_scale";
        public static final String HAPTIC_FEEDBACK_ENABLED = "haptic_feedback_enabled";
        public static final String HDMI_FULL_SCREEN = "hdmi_full_screen";
        public static final String HDMI_OUTPUT_MODE = "hdmi_output_mode";
        public static final String HEARING_AID = "hearing_aid";
        public static final String HIDE_ROTATION_LOCK_TOGGLE_FOR_ACCESSIBILITY = "hide_rotation_lock_toggle_for_accessibility";

        @Deprecated
        public static final String HTTP_PROXY = "http_proxy";

        @Deprecated
        public static final String INSTALL_NON_MARKET_APPS = "install_non_market_apps";
        public static final String IS_SCAN_TF_CARD = "is_scan_tf_card";
        public static final String IS_SCAN_USB_HOST = "is_scan_usb_host";

        @Deprecated
        public static final String LOCATION_PROVIDERS_ALLOWED = "location_providers_allowed";
        public static final String LOCKSCREEN_DISABLED = "lockscreen.disabled";
        public static final String LOCKSCREEN_SOUNDS_ENABLED = "lockscreen_sounds_enabled";

        @Deprecated
        public static final String LOCK_PATTERN_ENABLED = "lock_pattern_autolock";

        @Deprecated
        public static final String LOCK_PATTERN_TACTILE_FEEDBACK_ENABLED = "lock_pattern_tactile_feedback_enabled";

        @Deprecated
        public static final String LOCK_PATTERN_VISIBLE = "lock_pattern_visible_pattern";

        @Deprecated
        public static final String LOCK_SOUND = "lock_sound";

        @Deprecated
        public static final String LOGGING_ID = "logging_id";

        @Deprecated
        public static final String LOW_BATTERY_SOUND = "low_battery_sound";
        public static final String MEDIA_BUTTON_RECEIVER = "media_button_receiver";

        @Deprecated
        public static final String MODE_RINGER = "mode_ringer";
        public static final String MODE_RINGER_STREAMS_AFFECTED = "mode_ringer_streams_affected";
        public static final String MOUSE_ADVANCE = "mouse_advance";
        private static final HashSet<String> MOVED_TO_GLOBAL;
        private static final HashSet<String> MOVED_TO_SECURE;
        private static final HashSet<String> MOVED_TO_SECURE_THEN_GLOBAL;
        public static final String MUTE_STREAMS_AFFECTED = "mute_streams_affected";

        @Deprecated
        public static final String NETWORK_PREFERENCE = "network_preference";
        public static final String NEXT_ALARM_FORMATTED = "next_alarm_formatted";

        @Deprecated
        public static final String NOTIFICATIONS_USE_RING_VOLUME = "notifications_use_ring_volume";
        public static final String NOTIFICATION_LIGHT_PULSE = "notification_light_pulse";
        public static final String NOTIFICATION_SOUND = "notification_sound";

        @Deprecated
        public static final String PARENTAL_CONTROL_ENABLED = "parental_control_enabled";

        @Deprecated
        public static final String PARENTAL_CONTROL_LAST_UPDATE = "parental_control_last_update";

        @Deprecated
        public static final String PARENTAL_CONTROL_REDIRECT_URL = "parental_control_redirect_url";
        public static final String POINTER_LOCATION = "pointer_location";
        public static final String POINTER_SPEED = "pointer_speed";

        @Deprecated
        public static final String POWER_SOUNDS_ENABLED = "power_sounds_enabled";

        @Deprecated
        public static final String RADIO_BLUETOOTH = "bluetooth";

        @Deprecated
        public static final String RADIO_CELL = "cell";

        @Deprecated
        public static final String RADIO_NFC = "nfc";

        @Deprecated
        public static final String RADIO_WIFI = "wifi";

        @Deprecated
        public static final String RADIO_WIMAX = "wimax";
        public static final String RINGTONE = "ringtone";
        public static final String ROTATION_DIRECTION = "rotation_direction";
        public static final String ROTATION_MODE_SET = "rotation_mode_set";
        public static final String SCREEN_AUTO_BRIGHTNESS_ADJ = "screen_auto_brightness_adj";
        public static final String SCREEN_BRIGHTNESS = "screen_brightness";
        public static final String SCREEN_BRIGHTNESS_MODE = "screen_brightness_mode";
        public static final int SCREEN_BRIGHTNESS_MODE_AUTOMATIC = 1;
        public static final int SCREEN_BRIGHTNESS_MODE_MANUAL = 0;
        public static final String SCREEN_OFF_TIMEOUT = "screen_off_timeout";

        @Deprecated
        public static final String SETTINGS_CLASSNAME = "settings_classname";
        public static final String[] SETTINGS_TO_BACKUP;
        public static final String SETUP_WIZARD_HAS_RUN = "setup_wizard_has_run";
        public static final String SHORTCUT_KEY_0 = "shortcut_key_0";
        public static final String SHORTCUT_KEY_1 = "shortcut_key_1";
        public static final String SHORTCUT_KEY_2 = "shortcut_key_2";
        public static final String SHORTCUT_KEY_3 = "shortcut_key_3";
        public static final String SHORTCUT_PATH_SEPARATOR = "--split--";
        public static final String SHORTCUT_PATH_TYPE_APP = "app";
        public static final String SHORTCUT_PATH_TYPE_WEBSITE = "website";
        public static final String SHOW_GTALK_SERVICE_STATUS = "SHOW_GTALK_SERVICE_STATUS";

        @Deprecated
        public static final String SHOW_PROCESSES = "show_processes";
        public static final String SHOW_TOUCHES = "show_touches";

        @Deprecated
        public static final String SHOW_WEB_SUGGESTIONS = "show_web_suggestions";
        public static final String SIP_ADDRESS_ONLY = "SIP_ADDRESS_ONLY";
        public static final String SIP_ALWAYS = "SIP_ALWAYS";
        public static final String SIP_ASK_ME_EACH_TIME = "SIP_ASK_ME_EACH_TIME";
        public static final String SIP_CALL_OPTIONS = "sip_call_options";
        public static final String SIP_RECEIVE_CALLS = "sip_receive_calls";
        public static final String SMART_BRIGHTNESS_ENABLE = "smart_brightness_enable";
        public static final String SMART_BRIGHTNESS_PREVIEW_ENABLE = "smart_brightness_enable";
        public static final String SOUND_EFFECTS_ENABLED = "sound_effects_enabled";

        @Deprecated
        public static final String STAY_ON_WHILE_PLUGGED_IN = "stay_on_while_plugged_in";
        public static final String SYS_PROP_SETTING_VERSION = "sys.settings_system_version";
        public static final String TEXT_AUTO_CAPS = "auto_caps";
        public static final String TEXT_AUTO_PUNCTUATE = "auto_punctuate";
        public static final String TEXT_AUTO_REPLACE = "auto_replace";
        public static final String TEXT_SHOW_PASSWORD = "show_password";
        public static final String TIME_12_24 = "time_12_24";

        @Deprecated
        public static final String TRANSITION_ANIMATION_SCALE = "transition_animation_scale";
        public static final String TTY_MODE = "tty_mode";

        @Deprecated
        public static final String UNLOCK_SOUND = "unlock_sound";

        @Deprecated
        public static final String USB_MASS_STORAGE_ENABLED = "usb_mass_storage_enabled";
        public static final String USER_ROTATION = "user_rotation";

        @Deprecated
        public static final String USE_GOOGLE_MAIL = "use_google_mail";
        public static final String VIBRATE_INPUT_DEVICES = "vibrate_input_devices";
        public static final String VIBRATE_IN_SILENT = "vibrate_in_silent";
        public static final String VIBRATE_ON = "vibrate_on";
        public static final String VIBRATE_WHEN_RINGING = "vibrate_when_ringing";
        public static final String VOLUME_ALARM = "volume_alarm";
        public static final String VOLUME_BLUETOOTH_SCO = "volume_bluetooth_sco";
        public static final String VOLUME_FM = "volume_fm";
        public static final String VOLUME_MASTER = "volume_master";
        public static final String VOLUME_MASTER_MUTE = "volume_master_mute";
        public static final String VOLUME_MUSIC = "volume_music";
        public static final String VOLUME_NOTIFICATION = "volume_notification";
        public static final String VOLUME_RING = "volume_ring";
        public static final String[] VOLUME_SETTINGS;
        public static final String VOLUME_SYSTEM = "volume_system";
        public static final String VOLUME_VOICE = "volume_voice";

        @Deprecated
        public static final String WAIT_FOR_DEBUGGER = "wait_for_debugger";

        @Deprecated
        public static final String WALLPAPER_ACTIVITY = "wallpaper_activity";

        @Deprecated
        public static final String WIFI_AP_ON = "wifi_ap_on";

        @Deprecated
        public static final String WIFI_MAX_DHCP_RETRY_COUNT = "wifi_max_dhcp_retry_count";

        @Deprecated
        public static final String WIFI_MOBILE_DATA_TRANSITION_WAKELOCK_TIMEOUT_MS = "wifi_mobile_data_transition_wakelock_timeout_ms";

        @Deprecated
        public static final String WIFI_NETWORKS_AVAILABLE_NOTIFICATION_ON = "wifi_networks_available_notification_on";

        @Deprecated
        public static final String WIFI_NETWORKS_AVAILABLE_REPEAT_DELAY = "wifi_networks_available_repeat_delay";

        @Deprecated
        public static final String WIFI_NUM_OPEN_NETWORKS_KEPT = "wifi_num_open_networks_kept";

        @Deprecated
        public static final String WIFI_ON = "wifi_on";

        @Deprecated
        public static final String WIFI_SLEEP_POLICY = "wifi_sleep_policy";

        @Deprecated
        public static final int WIFI_SLEEP_POLICY_DEFAULT = 0;

        @Deprecated
        public static final int WIFI_SLEEP_POLICY_NEVER = 2;

        @Deprecated
        public static final int WIFI_SLEEP_POLICY_NEVER_WHILE_PLUGGED = 1;

        @Deprecated
        public static final String WIFI_STATIC_DNS1 = "wifi_static_dns1";

        @Deprecated
        public static final String WIFI_STATIC_DNS2 = "wifi_static_dns2";

        @Deprecated
        public static final String WIFI_STATIC_GATEWAY = "wifi_static_gateway";

        @Deprecated
        public static final String WIFI_STATIC_IP = "wifi_static_ip";

        @Deprecated
        public static final String WIFI_STATIC_NETMASK = "wifi_static_netmask";

        @Deprecated
        public static final String WIFI_USE_STATIC_IP = "wifi_use_static_ip";

        @Deprecated
        public static final String WIFI_WATCHDOG_ACCEPTABLE_PACKET_LOSS_PERCENTAGE = "wifi_watchdog_acceptable_packet_loss_percentage";

        @Deprecated
        public static final String WIFI_WATCHDOG_AP_COUNT = "wifi_watchdog_ap_count";

        @Deprecated
        public static final String WIFI_WATCHDOG_BACKGROUND_CHECK_DELAY_MS = "wifi_watchdog_background_check_delay_ms";

        @Deprecated
        public static final String WIFI_WATCHDOG_BACKGROUND_CHECK_ENABLED = "wifi_watchdog_background_check_enabled";

        @Deprecated
        public static final String WIFI_WATCHDOG_BACKGROUND_CHECK_TIMEOUT_MS = "wifi_watchdog_background_check_timeout_ms";

        @Deprecated
        public static final String WIFI_WATCHDOG_INITIAL_IGNORED_PING_COUNT = "wifi_watchdog_initial_ignored_ping_count";

        @Deprecated
        public static final String WIFI_WATCHDOG_MAX_AP_CHECKS = "wifi_watchdog_max_ap_checks";

        @Deprecated
        public static final String WIFI_WATCHDOG_ON = "wifi_watchdog_on";

        @Deprecated
        public static final String WIFI_WATCHDOG_PING_COUNT = "wifi_watchdog_ping_count";

        @Deprecated
        public static final String WIFI_WATCHDOG_PING_DELAY_MS = "wifi_watchdog_ping_delay_ms";

        @Deprecated
        public static final String WIFI_WATCHDOG_PING_TIMEOUT_MS = "wifi_watchdog_ping_timeout_ms";

        @Deprecated
        public static final String WINDOW_ANIMATION_SCALE = "window_animation_scale";
        public static final String WINDOW_ORIENTATION_LISTENER_LOG = "window_orientation_listener_log";
        private static KeyShortcutInfo[] keyShortcutInfoArray;
        private static final NameValueCache sNameValueCache;

        public static boolean hasInterestingConfigurationChanges(int i) {
            return (i & 1073741824) != 0;
        }

        static {
            Uri uri = Uri.parse("content://settings/system");
            CONTENT_URI = uri;
            sNameValueCache = new NameValueCache(SYS_PROP_SETTING_VERSION, uri, Settings.CALL_METHOD_GET_SYSTEM, Settings.CALL_METHOD_PUT_SYSTEM);
            HashSet<String> hashSet = new HashSet<>(30);
            MOVED_TO_SECURE = hashSet;
            hashSet.add("android_id");
            hashSet.add("http_proxy");
            hashSet.add("location_providers_allowed");
            hashSet.add(Secure.LOCK_BIOMETRIC_WEAK_FLAGS);
            hashSet.add("lock_pattern_autolock");
            hashSet.add("lock_pattern_visible_pattern");
            hashSet.add("lock_pattern_tactile_feedback_enabled");
            hashSet.add("logging_id");
            hashSet.add("parental_control_enabled");
            hashSet.add("parental_control_last_update");
            hashSet.add("parental_control_redirect_url");
            hashSet.add("settings_classname");
            hashSet.add("use_google_mail");
            hashSet.add("wifi_networks_available_notification_on");
            hashSet.add("wifi_networks_available_repeat_delay");
            hashSet.add("wifi_num_open_networks_kept");
            hashSet.add("wifi_on");
            hashSet.add("wifi_watchdog_acceptable_packet_loss_percentage");
            hashSet.add("wifi_watchdog_ap_count");
            hashSet.add("wifi_watchdog_background_check_delay_ms");
            hashSet.add("wifi_watchdog_background_check_enabled");
            hashSet.add("wifi_watchdog_background_check_timeout_ms");
            hashSet.add("wifi_watchdog_initial_ignored_ping_count");
            hashSet.add("wifi_watchdog_max_ap_checks");
            hashSet.add("wifi_watchdog_on");
            hashSet.add("wifi_watchdog_ping_count");
            hashSet.add("wifi_watchdog_ping_delay_ms");
            hashSet.add("wifi_watchdog_ping_timeout_ms");
            hashSet.add(Secure.HOME_PAGE);
            hashSet.add(Secure.UPGRADE_URL);
            hashSet.add(Secure.UPGRADE_PATH);
            hashSet.add(Secure.NTVUSERACCOUNT);
            hashSet.add(Secure.NTVUSERSUFFIX);
            hashSet.add(Secure.NTVUSERPASSWORD);
            hashSet.add("ntp_server");
            hashSet.add(Secure.DEFAULT_SCREEN_RATIO);
            hashSet.add(Secure.DEFAULT_PLAYER_QUALITY);
            HashSet<String> hashSet2 = new HashSet<>();
            MOVED_TO_GLOBAL = hashSet2;
            HashSet<String> hashSet3 = new HashSet<>();
            MOVED_TO_SECURE_THEN_GLOBAL = hashSet3;
            hashSet3.add("adb_enabled");
            hashSet3.add("bluetooth_on");
            hashSet3.add("data_roaming");
            hashSet3.add("device_provisioned");
            hashSet3.add("install_non_market_apps");
            hashSet3.add("usb_mass_storage_enabled");
            hashSet3.add("http_proxy");
            hashSet2.add("airplane_mode_on");
            hashSet2.add("airplane_mode_radios");
            hashSet2.add("airplane_mode_toggleable_radios");
            hashSet2.add("auto_time");
            hashSet2.add("auto_time_zone");
            hashSet2.add("car_dock_sound");
            hashSet2.add("car_undock_sound");
            hashSet2.add("desk_dock_sound");
            hashSet2.add("desk_undock_sound");
            hashSet2.add("dock_sounds_enabled");
            hashSet2.add("lock_sound");
            hashSet2.add("unlock_sound");
            hashSet2.add("low_battery_sound");
            hashSet2.add("power_sounds_enabled");
            hashSet2.add("stay_on_while_plugged_in");
            hashSet2.add("wifi_sleep_policy");
            hashSet2.add("mode_ringer");
            hashSet2.add("window_animation_scale");
            hashSet2.add("transition_animation_scale");
            hashSet2.add("animator_duration_scale");
            hashSet2.add(Global.FANCY_IME_ANIMATIONS);
            hashSet2.add(Global.COMPATIBILITY_MODE);
            hashSet2.add(Global.EMERGENCY_TONE);
            hashSet2.add(Global.CALL_AUTO_RETRY);
            hashSet2.add("debug_app");
            hashSet2.add("wait_for_debugger");
            hashSet2.add("show_processes");
            hashSet2.add("always_finish_activities");
            hashSet2.add(Global.TZINFO_UPDATE_CONTENT_URL);
            hashSet2.add(Global.TZINFO_UPDATE_METADATA_URL);
            hashSet2.add(Global.SELINUX_UPDATE_CONTENT_URL);
            hashSet2.add(Global.SELINUX_UPDATE_METADATA_URL);
            hashSet2.add(Global.SMS_SHORT_CODES_UPDATE_CONTENT_URL);
            hashSet2.add(Global.SMS_SHORT_CODES_UPDATE_METADATA_URL);
            hashSet2.add(Global.CERT_PIN_UPDATE_CONTENT_URL);
            hashSet2.add(Global.CERT_PIN_UPDATE_METADATA_URL);
            hashSet2.add("wifi_ap_on");
            VOLUME_SETTINGS = new String[]{VOLUME_VOICE, VOLUME_SYSTEM, VOLUME_RING, VOLUME_MUSIC, VOLUME_ALARM, VOLUME_NOTIFICATION, VOLUME_BLUETOOTH_SCO, "no volume", "no volume", VOLUME_FM, VOLUME_FM};
            DEFAULT_RINGTONE_URI = getUriFor(RINGTONE);
            DEFAULT_NOTIFICATION_URI = getUriFor(NOTIFICATION_SOUND);
            DEFAULT_ALARM_ALERT_URI = getUriFor(ALARM_ALERT);
            keyShortcutInfoArray = new KeyShortcutInfo[]{new KeyShortcutInfo(183, SHORTCUT_KEY_0), new KeyShortcutInfo(184, SHORTCUT_KEY_1), new KeyShortcutInfo(185, SHORTCUT_KEY_2), new KeyShortcutInfo(186, SHORTCUT_KEY_3)};
            SETTINGS_TO_BACKUP = new String[]{"stay_on_while_plugged_in", WIFI_USE_STATIC_IP, WIFI_STATIC_IP, WIFI_STATIC_GATEWAY, WIFI_STATIC_NETMASK, WIFI_STATIC_DNS1, WIFI_STATIC_DNS2, BLUETOOTH_DISCOVERABILITY, BLUETOOTH_DISCOVERABILITY_TIMEOUT, DIM_SCREEN, SCREEN_OFF_TIMEOUT, SCREEN_BRIGHTNESS, SCREEN_BRIGHTNESS_MODE, SCREEN_AUTO_BRIGHTNESS_ADJ, VIBRATE_INPUT_DEVICES, MODE_RINGER_STREAMS_AFFECTED, VOLUME_VOICE, VOLUME_SYSTEM, VOLUME_RING, VOLUME_MUSIC, VOLUME_ALARM, VOLUME_NOTIFICATION, VOLUME_BLUETOOTH_SCO, "volume_voice_last_audible", "volume_system_last_audible", "volume_ring_last_audible", "volume_music_last_audible", "volume_alarm_last_audible", "volume_notification_last_audible", "volume_bluetooth_sco_last_audible", TEXT_AUTO_REPLACE, TEXT_AUTO_CAPS, TEXT_AUTO_PUNCTUATE, TEXT_SHOW_PASSWORD, "auto_time", "auto_time_zone", TIME_12_24, DATE_FORMAT, DTMF_TONE_WHEN_DIALING, DTMF_TONE_TYPE_WHEN_DIALING, HEARING_AID, TTY_MODE, SOUND_EFFECTS_ENABLED, HAPTIC_FEEDBACK_ENABLED, "power_sounds_enabled", "dock_sounds_enabled", LOCKSCREEN_SOUNDS_ENABLED, SHOW_WEB_SUGGESTIONS, NOTIFICATION_LIGHT_PULSE, SIP_CALL_OPTIONS, SIP_RECEIVE_CALLS, POINTER_SPEED, VIBRATE_WHEN_RINGING, RINGTONE, NOTIFICATION_SOUND, BRIGHT_SYSTEM_MODE, BRIGHTNESS_LIGHT_MODE, IS_SCAN_TF_CARD, DISPLAY_ADAPTION_MODE, DISPLAY_ADAPTION_ENABLE, "smart_brightness_enable", SHORTCUT_KEY_0, SHORTCUT_KEY_1, SHORTCUT_KEY_2, SHORTCUT_KEY_3, DISPLAY_ADAPTION_ENABLE, DISPLAY_AREA_RATIO, DISPLAY_AREA_H_PERCENT, DISPLAY_AREA_V_PERCENT, IS_SCAN_USB_HOST, DISPLAY_OUTPUT_FORMAT, CPU_FAST_ENABLE, MOUSE_ADVANCE, COLOR_BRIGHTNESS, COLOR_CONTRAST, COLOR_SATURATION, AUDIO_OUTPUT_TYPE, AUDIO_OUTPUT_CHANNEL, AUDIO_MANAGE_POLICY, DIRECTLY_POWER_OFF, BD_FOLDER_PLAY_MODE, HDMI_OUTPUT_MODE, BOOT_FAST_ENABLE, CPU_FAST_ENABLE, ENABLE_PASS_THROUGH, HDMI_FULL_SCREEN};
        }

        public static void getMovedKeys(HashSet<String> hashSet) {
            hashSet.addAll(MOVED_TO_GLOBAL);
            hashSet.addAll(MOVED_TO_SECURE_THEN_GLOBAL);
        }

        public static void getNonLegacyMovedKeys(HashSet<String> hashSet) {
            hashSet.addAll(MOVED_TO_GLOBAL);
        }

        public static String getString(ContentResolver contentResolver, String str) {
            return getStringForUser(contentResolver, str, UserHandle.myUserId());
        }

        public static String getStringForUser(ContentResolver contentResolver, String str, int i) {
            if (MOVED_TO_SECURE.contains(str)) {
                Log.w(Settings.TAG, "Setting " + str + " has moved from android.provider.Settings.System to android.provider.Settings.Secure, returning read-only value.");
                return Secure.getStringForUser(contentResolver, str, i);
            }
            if (MOVED_TO_GLOBAL.contains(str) || MOVED_TO_SECURE_THEN_GLOBAL.contains(str)) {
                Log.w(Settings.TAG, "Setting " + str + " has moved from android.provider.Settings.System to android.provider.Settings.Global, returning read-only value.");
                return Global.getStringForUser(contentResolver, str, i);
            }
            return sNameValueCache.getStringForUser(contentResolver, str, i);
        }

        public static boolean putString(ContentResolver contentResolver, String str, String str2) {
            return putStringForUser(contentResolver, str, str2, UserHandle.myUserId());
        }

        public static boolean putStringForUser(ContentResolver contentResolver, String str, String str2, int i) {
            if (MOVED_TO_SECURE.contains(str)) {
                Log.w(Settings.TAG, "Setting " + str + " has moved from android.provider.Settings.System to android.provider.Settings.Secure, value is unchanged.");
                return false;
            }
            if (MOVED_TO_GLOBAL.contains(str) || MOVED_TO_SECURE_THEN_GLOBAL.contains(str)) {
                Log.w(Settings.TAG, "Setting " + str + " has moved from android.provider.Settings.System to android.provider.Settings.Global, value is unchanged.");
                return false;
            }
            return sNameValueCache.putStringForUser(contentResolver, str, str2, i);
        }

        public static Uri getUriFor(String str) {
            if (MOVED_TO_SECURE.contains(str)) {
                Log.w(Settings.TAG, "Setting " + str + " has moved from android.provider.Settings.System to android.provider.Settings.Secure, returning Secure URI.");
                return Secure.getUriFor(Secure.CONTENT_URI, str);
            }
            if (MOVED_TO_GLOBAL.contains(str) || MOVED_TO_SECURE_THEN_GLOBAL.contains(str)) {
                Log.w(Settings.TAG, "Setting " + str + " has moved from android.provider.Settings.System to android.provider.Settings.Global, returning read-only global URI.");
                return Global.getUriFor(Global.CONTENT_URI, str);
            }
            return getUriFor(CONTENT_URI, str);
        }

        public static int getInt(ContentResolver contentResolver, String str, int i) {
            return getIntForUser(contentResolver, str, i, UserHandle.myUserId());
        }

        public static int getIntForUser(ContentResolver contentResolver, String str, int i, int i2) {
            String stringForUser = getStringForUser(contentResolver, str, i2);
            if (stringForUser == null) {
                return i;
            }
            try {
                return Integer.parseInt(stringForUser);
            } catch (NumberFormatException unused) {
                return i;
            }
        }

        public static int getInt(ContentResolver contentResolver, String str) throws SettingNotFoundException {
            return getIntForUser(contentResolver, str, UserHandle.myUserId());
        }

        public static int getIntForUser(ContentResolver contentResolver, String str, int i) throws SettingNotFoundException {
            try {
                return Integer.parseInt(getStringForUser(contentResolver, str, i));
            } catch (NumberFormatException unused) {
                throw new SettingNotFoundException(str);
            }
        }

        public static boolean putInt(ContentResolver contentResolver, String str, int i) {
            return putIntForUser(contentResolver, str, i, UserHandle.myUserId());
        }

        public static boolean putIntForUser(ContentResolver contentResolver, String str, int i, int i2) {
            return putStringForUser(contentResolver, str, Integer.toString(i), i2);
        }

        public static long getLong(ContentResolver contentResolver, String str, long j) {
            return getLongForUser(contentResolver, str, j, UserHandle.myUserId());
        }

        public static long getLongForUser(ContentResolver contentResolver, String str, long j, int i) {
            String stringForUser = getStringForUser(contentResolver, str, i);
            if (stringForUser == null) {
                return j;
            }
            try {
                return Long.parseLong(stringForUser);
            } catch (NumberFormatException unused) {
                return j;
            }
        }

        public static long getLong(ContentResolver contentResolver, String str) throws SettingNotFoundException {
            return getLongForUser(contentResolver, str, UserHandle.myUserId());
        }

        public static long getLongForUser(ContentResolver contentResolver, String str, int i) throws SettingNotFoundException {
            try {
                return Long.parseLong(getStringForUser(contentResolver, str, i));
            } catch (NumberFormatException unused) {
                throw new SettingNotFoundException(str);
            }
        }

        public static boolean putLong(ContentResolver contentResolver, String str, long j) {
            return putLongForUser(contentResolver, str, j, UserHandle.myUserId());
        }

        public static boolean putLongForUser(ContentResolver contentResolver, String str, long j, int i) {
            return putStringForUser(contentResolver, str, Long.toString(j), i);
        }

        public static float getFloat(ContentResolver contentResolver, String str, float f) {
            return getFloatForUser(contentResolver, str, f, UserHandle.myUserId());
        }

        public static float getFloatForUser(ContentResolver contentResolver, String str, float f, int i) {
            String stringForUser = getStringForUser(contentResolver, str, i);
            if (stringForUser == null) {
                return f;
            }
            try {
                return Float.parseFloat(stringForUser);
            } catch (NumberFormatException unused) {
                return f;
            }
        }

        public static float getFloat(ContentResolver contentResolver, String str) throws SettingNotFoundException {
            return getFloatForUser(contentResolver, str, UserHandle.myUserId());
        }

        public static float getFloatForUser(ContentResolver contentResolver, String str, int i) throws SettingNotFoundException {
            String stringForUser = getStringForUser(contentResolver, str, i);
            if (stringForUser == null) {
                throw new SettingNotFoundException(str);
            }
            try {
                return Float.parseFloat(stringForUser);
            } catch (NumberFormatException unused) {
                throw new SettingNotFoundException(str);
            }
        }

        public static boolean putFloat(ContentResolver contentResolver, String str, float f) {
            return putFloatForUser(contentResolver, str, f, UserHandle.myUserId());
        }

        public static boolean putFloatForUser(ContentResolver contentResolver, String str, float f, int i) {
            return putStringForUser(contentResolver, str, Float.toString(f), i);
        }

        public static void getConfiguration(ContentResolver contentResolver, Configuration configuration) {
            getConfigurationForUser(contentResolver, configuration, UserHandle.myUserId());
        }

        public static void getConfigurationForUser(ContentResolver contentResolver, Configuration configuration, int i) {
            configuration.fontScale = getFloatForUser(contentResolver, FONT_SCALE, configuration.fontScale, i);
            if (configuration.fontScale < 0.0f) {
                configuration.fontScale = 1.0f;
            }
        }

        public static void clearConfiguration(Configuration configuration) {
            configuration.fontScale = 0.0f;
        }

        public static boolean putConfiguration(ContentResolver contentResolver, Configuration configuration) {
            return putConfigurationForUser(contentResolver, configuration, UserHandle.myUserId());
        }

        public static boolean putConfigurationForUser(ContentResolver contentResolver, Configuration configuration, int i) {
            return putFloatForUser(contentResolver, FONT_SCALE, configuration.fontScale, i);
        }

        @Deprecated
        public static boolean getShowGTalkServiceStatus(ContentResolver contentResolver) {
            return getShowGTalkServiceStatusForUser(contentResolver, UserHandle.myUserId());
        }

        public static boolean getShowGTalkServiceStatusForUser(ContentResolver contentResolver, int i) {
            return getIntForUser(contentResolver, SHOW_GTALK_SERVICE_STATUS, 0, i) != 0;
        }

        @Deprecated
        public static void setShowGTalkServiceStatus(ContentResolver contentResolver, boolean z) {
            setShowGTalkServiceStatusForUser(contentResolver, z, UserHandle.myUserId());
        }

        @Deprecated
        public static void setShowGTalkServiceStatusForUser(ContentResolver contentResolver, boolean z, int i) {
            putIntForUser(contentResolver, SHOW_GTALK_SERVICE_STATUS, z ? 1 : 0, i);
        }

        public static String findNameByKey(int i) {
            int i2 = 0;
            while (true) {
                KeyShortcutInfo[] keyShortcutInfoArr = keyShortcutInfoArray;
                if (i2 >= keyShortcutInfoArr.length) {
                    return null;
                }
                if (keyShortcutInfoArr[i2].key == i) {
                    return keyShortcutInfoArray[i2].name;
                }
                i2++;
            }
        }

        private static class KeyShortcutInfo {
            public int key;
            public String name;

            public KeyShortcutInfo(int i, String str) {
                this.key = i;
                this.name = str;
            }
        }
    }

    public static final class Secure extends NameValueTable {
        public static final String ACCESSIBILITY_CAPTIONING_BACKGROUND_COLOR = "accessibility_captioning_background_color";
        public static final String ACCESSIBILITY_CAPTIONING_EDGE_COLOR = "accessibility_captioning_edge_color";
        public static final String ACCESSIBILITY_CAPTIONING_EDGE_TYPE = "accessibility_captioning_edge_type";
        public static final String ACCESSIBILITY_CAPTIONING_ENABLED = "accessibility_captioning_enabled";
        public static final String ACCESSIBILITY_CAPTIONING_FONT_SCALE = "accessibility_captioning_font_scale";
        public static final String ACCESSIBILITY_CAPTIONING_FOREGROUND_COLOR = "accessibility_captioning_foreground_color";
        public static final String ACCESSIBILITY_CAPTIONING_LOCALE = "accessibility_captioning_locale";
        public static final String ACCESSIBILITY_CAPTIONING_PRESET = "accessibility_captioning_preset";
        public static final String ACCESSIBILITY_CAPTIONING_TYPEFACE = "accessibility_captioning_typeface";
        public static final String ACCESSIBILITY_DISPLAY_MAGNIFICATION_AUTO_UPDATE = "accessibility_display_magnification_auto_update";
        public static final String ACCESSIBILITY_DISPLAY_MAGNIFICATION_ENABLED = "accessibility_display_magnification_enabled";
        public static final String ACCESSIBILITY_DISPLAY_MAGNIFICATION_SCALE = "accessibility_display_magnification_scale";
        public static final String ACCESSIBILITY_ENABLED = "accessibility_enabled";
        public static final String ACCESSIBILITY_SCREEN_READER_URL = "accessibility_script_injection_url";
        public static final String ACCESSIBILITY_SCRIPT_INJECTION = "accessibility_script_injection";
        public static final String ACCESSIBILITY_SPEAK_PASSWORD = "speak_password";
        public static final String ACCESSIBILITY_WEB_CONTENT_KEY_BINDINGS = "accessibility_web_content_key_bindings";

        @Deprecated
        public static final String ADB_ENABLED = "adb_enabled";
        public static final String ALLOWED_GEOLOCATION_ORIGINS = "allowed_geolocation_origins";
        public static final String ALLOW_MOCK_LOCATION = "mock_location";
        public static final String ANDROID_ID = "android_id";
        public static final String ANR_SHOW_BACKGROUND = "anr_show_background";

        @Deprecated
        public static final String BACKGROUND_DATA = "background_data";
        public static final String BACKUP_AUTO_RESTORE = "backup_auto_restore";
        public static final String BACKUP_ENABLED = "backup_enabled";
        public static final String BACKUP_PROVISIONED = "backup_provisioned";
        public static final String BACKUP_TRANSPORT = "backup_transport";
        public static final String BAR_SERVICE_COMPONENT = "bar_service_component";
        public static final String BLUETOOTH_HCI_LOG = "bluetooth_hci_log";

        @Deprecated
        public static final String BLUETOOTH_ON = "bluetooth_on";

        @Deprecated
        public static final String BUGREPORT_IN_POWER_MENU = "bugreport_in_power_menu";
        public static final Uri CONTENT_URI;

        @Deprecated
        public static final String DATA_ROAMING = "data_roaming";
        public static final String DEFAULT_INPUT_METHOD = "default_input_method";
        public static final String DEFAULT_PLAYER_QUALITY = "default_player_quality";
        public static final String DEFAULT_SCREEN_RATIO = "default_screen_ratio";

        @Deprecated
        public static final String DEVELOPMENT_SETTINGS_ENABLED = "development_settings_enabled";

        @Deprecated
        public static final String DEVICE_PROVISIONED = "device_provisioned";
        public static final String DHCP_IPVER = "dhcp_ipver";
        public static final String DHCP_OPTION = "dhcp_option";
        public static final String DHCP_PSWD = "dhcp_pswd";
        public static final String DHCP_USER = "dhcp_user";
        public static final String DISABLED_SYSTEM_INPUT_METHODS = "disabled_system_input_methods";
        public static final String DISPLAY_AREA = "display_area";
        public static final String ENABLED_ACCESSIBILITY_SERVICES = "enabled_accessibility_services";
        public static final String ENABLED_INPUT_METHODS = "enabled_input_methods";
        public static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
        public static final String ENABLED_ON_FIRST_BOOT_SYSTEM_PRINT_SERVICES = "enabled_on_first_boot_system_print_services";
        public static final String ENABLED_PRINT_SERVICES = "enabled_print_services";
        public static final String ENHANCED_VOICE_PRIVACY_ENABLED = "enhanced_voice_privacy_enabled";
        public static final String HOME_PAGE = "home_page";

        @Deprecated
        public static final String HTTP_PROXY = "http_proxy";
        public static final String IMMERSIVE_MODE_CONFIRMATIONS = "immersive_mode_confirmations";
        public static final String INCALL_POWER_BUTTON_BEHAVIOR = "incall_power_button_behavior";
        public static final int INCALL_POWER_BUTTON_BEHAVIOR_DEFAULT = 1;
        public static final int INCALL_POWER_BUTTON_BEHAVIOR_HANGUP = 2;
        public static final int INCALL_POWER_BUTTON_BEHAVIOR_SCREEN_OFF = 1;
        public static final String INPUT_METHODS_SUBTYPE_HISTORY = "input_methods_subtype_history";
        public static final String INPUT_METHOD_SELECTOR_VISIBILITY = "input_method_selector_visibility";

        @Deprecated
        public static final String INSTALL_NON_MARKET_APPS = "install_non_market_apps";
        public static final String LAST_SETUP_SHOWN = "last_setup_shown";
        public static final String LOCATION_MODE = "location_mode";
        public static final int LOCATION_MODE_BATTERY_SAVING = 2;
        public static final int LOCATION_MODE_HIGH_ACCURACY = 3;
        public static final int LOCATION_MODE_OFF = 0;
        public static final int LOCATION_MODE_SENSORS_ONLY = 1;

        @Deprecated
        public static final String LOCATION_PROVIDERS_ALLOWED = "location_providers_allowed";
        public static final String LOCK_BIOMETRIC_WEAK_FLAGS = "lock_biometric_weak_flags";
        public static final String LOCK_PATTERN_ENABLED = "lock_pattern_autolock";

        @Deprecated
        public static final String LOCK_PATTERN_TACTILE_FEEDBACK_ENABLED = "lock_pattern_tactile_feedback_enabled";
        public static final String LOCK_PATTERN_VISIBLE = "lock_pattern_visible_pattern";
        public static final String LOCK_SCREEN_APPWIDGET_IDS = "lock_screen_appwidget_ids";
        public static final String LOCK_SCREEN_FALLBACK_APPWIDGET_ID = "lock_screen_fallback_appwidget_id";
        public static final String LOCK_SCREEN_LOCK_AFTER_TIMEOUT = "lock_screen_lock_after_timeout";
        public static final String LOCK_SCREEN_OWNER_INFO = "lock_screen_owner_info";
        public static final String LOCK_SCREEN_OWNER_INFO_ENABLED = "lock_screen_owner_info_enabled";
        public static final String LOCK_SCREEN_STICKY_APPWIDGET = "lock_screen_sticky_appwidget";

        @Deprecated
        public static final String LOGGING_ID = "logging_id";
        public static final String LONG_PRESS_TIMEOUT = "long_press_timeout";
        public static final String MOUNT_PLAY_NOTIFICATION_SND = "mount_play_not_snd";
        public static final String MOUNT_UMS_AUTOSTART = "mount_ums_autostart";
        public static final String MOUNT_UMS_NOTIFY_ENABLED = "mount_ums_notify_enabled";
        public static final String MOUNT_UMS_PROMPT = "mount_ums_prompt";
        private static final HashSet<String> MOVED_TO_GLOBAL;
        private static final HashSet<String> MOVED_TO_LOCK_SETTINGS;

        @Deprecated
        public static final String NETWORK_PREFERENCE = "network_preference";
        public static final String NFC_PAYMENT_DEFAULT_COMPONENT = "nfc_payment_default_component";
        public static final String NTP_SERVER = "ntp_server";
        public static final String NTVUSERACCOUNT = "ntvuseraccount";
        public static final String NTVUSERPASSWORD = "ntvuserpassword";
        public static final String NTVUSERSUFFIX = "ntvusersuffix";
        public static final String PACKAGE_VERIFIER_USER_CONSENT = "package_verifier_user_consent";
        public static final String PARENTAL_CONTROL_ENABLED = "parental_control_enabled";
        public static final String PARENTAL_CONTROL_LAST_UPDATE = "parental_control_last_update";
        public static final String PARENTAL_CONTROL_REDIRECT_URL = "parental_control_redirect_url";
        public static final String PAYMENT_SERVICE_SEARCH_URI = "payment_service_search_uri";
        public static final String PPPOE_AUTO_CONN = "pppoe_auto_conn";
        public static final String PPPOE_ENABLE = "pppoe_enable";
        public static final String PPPOE_INTERFACE = "pppoe_interface";
        public static final String PPPOE_PSWD = "pppoe_pswd";
        public static final String PPPOE_USERNAME = "pppoe_username";
        public static final String PREFERRED_TTY_MODE = "preferred_tty_mode";
        public static final String PRINT_SERVICE_SEARCH_URI = "print_service_search_uri";
        public static final String SCREENSAVER_ACTIVATE_ON_DOCK = "screensaver_activate_on_dock";
        public static final String SCREENSAVER_ACTIVATE_ON_SLEEP = "screensaver_activate_on_sleep";
        public static final String SCREENSAVER_COMPONENTS = "screensaver_components";
        public static final String SCREENSAVER_DEFAULT_COMPONENT = "screensaver_default_component";
        public static final String SCREENSAVER_ENABLED = "screensaver_enabled";
        public static final String SEARCH_GLOBAL_SEARCH_ACTIVITY = "search_global_search_activity";
        public static final String SEARCH_MAX_RESULTS_PER_SOURCE = "search_max_results_per_source";
        public static final String SEARCH_MAX_RESULTS_TO_DISPLAY = "search_max_results_to_display";
        public static final String SEARCH_MAX_SHORTCUTS_RETURNED = "search_max_shortcuts_returned";
        public static final String SEARCH_MAX_SOURCE_EVENT_AGE_MILLIS = "search_max_source_event_age_millis";
        public static final String SEARCH_MAX_STAT_AGE_MILLIS = "search_max_stat_age_millis";
        public static final String SEARCH_MIN_CLICKS_FOR_SOURCE_RANKING = "search_min_clicks_for_source_ranking";
        public static final String SEARCH_MIN_IMPRESSIONS_FOR_SOURCE_RANKING = "search_min_impressions_for_source_ranking";
        public static final String SEARCH_NUM_PROMOTED_SOURCES = "search_num_promoted_sources";
        public static final String SEARCH_PER_SOURCE_CONCURRENT_QUERY_LIMIT = "search_per_source_concurrent_query_limit";
        public static final String SEARCH_PREFILL_MILLIS = "search_prefill_millis";
        public static final String SEARCH_PROMOTED_SOURCE_DEADLINE_MILLIS = "search_promoted_source_deadline_millis";
        public static final String SEARCH_QUERY_THREAD_CORE_POOL_SIZE = "search_query_thread_core_pool_size";
        public static final String SEARCH_QUERY_THREAD_MAX_POOL_SIZE = "search_query_thread_max_pool_size";
        public static final String SEARCH_SHORTCUT_REFRESH_CORE_POOL_SIZE = "search_shortcut_refresh_core_pool_size";
        public static final String SEARCH_SHORTCUT_REFRESH_MAX_POOL_SIZE = "search_shortcut_refresh_max_pool_size";
        public static final String SEARCH_SOURCE_TIMEOUT_MILLIS = "search_source_timeout_millis";
        public static final String SEARCH_THREAD_KEEPALIVE_SECONDS = "search_thread_keepalive_seconds";
        public static final String SEARCH_WEB_RESULTS_OVERRIDE_LIMIT = "search_web_results_override_limit";
        public static final String SELECTED_INPUT_METHOD_SUBTYPE = "selected_input_method_subtype";
        public static final String SELECTED_SPELL_CHECKER = "selected_spell_checker";
        public static final String SELECTED_SPELL_CHECKER_SUBTYPE = "selected_spell_checker_subtype";
        public static final String SETTINGS_CLASSNAME = "settings_classname";
        public static final String[] SETTINGS_TO_BACKUP;
        public static final String SMS_DEFAULT_APPLICATION = "sms_default_application";
        public static final String SPELL_CHECKER_ENABLED = "spell_checker_enabled";
        public static final String SYS_PROP_SETTING_VERSION = "sys.settings_secure_version";
        public static final String TOUCH_EXPLORATION_ENABLED = "touch_exploration_enabled";
        public static final String TOUCH_EXPLORATION_GRANTED_ACCESSIBILITY_SERVICES = "touch_exploration_granted_accessibility_services";

        @Deprecated
        public static final String TTS_DEFAULT_COUNTRY = "tts_default_country";

        @Deprecated
        public static final String TTS_DEFAULT_LANG = "tts_default_lang";
        public static final String TTS_DEFAULT_LOCALE = "tts_default_locale";
        public static final String TTS_DEFAULT_PITCH = "tts_default_pitch";
        public static final String TTS_DEFAULT_RATE = "tts_default_rate";
        public static final String TTS_DEFAULT_SYNTH = "tts_default_synth";

        @Deprecated
        public static final String TTS_DEFAULT_VARIANT = "tts_default_variant";
        public static final String TTS_ENABLED_PLUGINS = "tts_enabled_plugins";

        @Deprecated
        public static final String TTS_USE_DEFAULTS = "tts_use_defaults";
        public static final String TTY_MODE_ENABLED = "tty_mode_enabled";
        public static final String UI_NIGHT_MODE = "ui_night_mode";
        public static final String UPGRADE_PATH = "upgrade_path";
        public static final String UPGRADE_URL = "upgrade_url";

        @Deprecated
        public static final String USB_MASS_STORAGE_ENABLED = "usb_mass_storage_enabled";
        public static final String USER_SETUP_COMPLETE = "user_setup_complete";

        @Deprecated
        public static final String USE_GOOGLE_MAIL = "use_google_mail";
        public static final String VOICE_RECOGNITION_SERVICE = "voice_recognition_service";

        @Deprecated
        public static final String WIFI_AP_ON = "wifi_ap_on";
        public static final String WIFI_CHEAT_ON = "wifi_cheat_on";
        public static final String WIFI_COUNTRY_CODE = "wifi_country_code";

        @Deprecated
        public static final String WIFI_IDLE_MS = "wifi_idle_ms";

        @Deprecated
        public static final String WIFI_MAX_DHCP_RETRY_COUNT = "wifi_max_dhcp_retry_count";

        @Deprecated
        public static final String WIFI_MOBILE_DATA_TRANSITION_WAKELOCK_TIMEOUT_MS = "wifi_mobile_data_transition_wakelock_timeout_ms";

        @Deprecated
        public static final String WIFI_NETWORKS_AVAILABLE_NOTIFICATION_ON = "wifi_networks_available_notification_on";

        @Deprecated
        public static final String WIFI_NETWORKS_AVAILABLE_REPEAT_DELAY = "wifi_networks_available_repeat_delay";

        @Deprecated
        public static final String WIFI_NUM_OPEN_NETWORKS_KEPT = "wifi_num_open_networks_kept";

        @Deprecated
        public static final String WIFI_ON = "wifi_on";

        @Deprecated
        public static final String WIFI_WATCHDOG_ACCEPTABLE_PACKET_LOSS_PERCENTAGE = "wifi_watchdog_acceptable_packet_loss_percentage";

        @Deprecated
        public static final String WIFI_WATCHDOG_AP_COUNT = "wifi_watchdog_ap_count";

        @Deprecated
        public static final String WIFI_WATCHDOG_BACKGROUND_CHECK_DELAY_MS = "wifi_watchdog_background_check_delay_ms";

        @Deprecated
        public static final String WIFI_WATCHDOG_BACKGROUND_CHECK_ENABLED = "wifi_watchdog_background_check_enabled";

        @Deprecated
        public static final String WIFI_WATCHDOG_BACKGROUND_CHECK_TIMEOUT_MS = "wifi_watchdog_background_check_timeout_ms";

        @Deprecated
        public static final String WIFI_WATCHDOG_INITIAL_IGNORED_PING_COUNT = "wifi_watchdog_initial_ignored_ping_count";

        @Deprecated
        public static final String WIFI_WATCHDOG_MAX_AP_CHECKS = "wifi_watchdog_max_ap_checks";

        @Deprecated
        public static final String WIFI_WATCHDOG_ON = "wifi_watchdog_on";

        @Deprecated
        public static final String WIFI_WATCHDOG_PING_COUNT = "wifi_watchdog_ping_count";

        @Deprecated
        public static final String WIFI_WATCHDOG_PING_DELAY_MS = "wifi_watchdog_ping_delay_ms";

        @Deprecated
        public static final String WIFI_WATCHDOG_PING_TIMEOUT_MS = "wifi_watchdog_ping_timeout_ms";

        @Deprecated
        public static final String WIFI_WATCHDOG_WATCH_LIST = "wifi_watchdog_watch_list";
        private static boolean sIsSystemProcess;
        private static ILockSettings sLockSettings;
        private static final NameValueCache sNameValueCache;

        static {
            Uri uri = Uri.parse("content://settings/secure");
            CONTENT_URI = uri;
            sNameValueCache = new NameValueCache(SYS_PROP_SETTING_VERSION, uri, Settings.CALL_METHOD_GET_SECURE, Settings.CALL_METHOD_PUT_SECURE);
            sLockSettings = null;
            HashSet<String> hashSet = new HashSet<>(3);
            MOVED_TO_LOCK_SETTINGS = hashSet;
            hashSet.add("lock_pattern_autolock");
            hashSet.add("lock_pattern_visible_pattern");
            hashSet.add("lock_pattern_tactile_feedback_enabled");
            HashSet<String> hashSet2 = new HashSet<>();
            MOVED_TO_GLOBAL = hashSet2;
            hashSet2.add("adb_enabled");
            hashSet2.add(Global.ASSISTED_GPS_ENABLED);
            hashSet2.add("bluetooth_on");
            hashSet2.add("bugreport_in_power_menu");
            hashSet2.add(Global.CDMA_CELL_BROADCAST_SMS);
            hashSet2.add(Global.CDMA_ROAMING_MODE);
            hashSet2.add(Global.CDMA_SUBSCRIPTION_MODE);
            hashSet2.add(Global.DATA_ACTIVITY_TIMEOUT_MOBILE);
            hashSet2.add(Global.DATA_ACTIVITY_TIMEOUT_WIFI);
            hashSet2.add("data_roaming");
            hashSet2.add("development_settings_enabled");
            hashSet2.add("device_provisioned");
            hashSet2.add(Global.DISPLAY_DENSITY_FORCED);
            hashSet2.add(Global.DISPLAY_SIZE_FORCED);
            hashSet2.add(Global.DOWNLOAD_MAX_BYTES_OVER_MOBILE);
            hashSet2.add(Global.DOWNLOAD_RECOMMENDED_MAX_BYTES_OVER_MOBILE);
            hashSet2.add("install_non_market_apps");
            hashSet2.add(Global.MOBILE_DATA);
            hashSet2.add(Global.NETSTATS_DEV_BUCKET_DURATION);
            hashSet2.add(Global.NETSTATS_DEV_DELETE_AGE);
            hashSet2.add(Global.NETSTATS_DEV_PERSIST_BYTES);
            hashSet2.add(Global.NETSTATS_DEV_ROTATE_AGE);
            hashSet2.add(Global.NETSTATS_ENABLED);
            hashSet2.add(Global.NETSTATS_GLOBAL_ALERT_BYTES);
            hashSet2.add(Global.NETSTATS_POLL_INTERVAL);
            hashSet2.add(Global.NETSTATS_REPORT_XT_OVER_DEV);
            hashSet2.add(Global.NETSTATS_SAMPLE_ENABLED);
            hashSet2.add(Global.NETSTATS_TIME_CACHE_MAX_AGE);
            hashSet2.add(Global.NETSTATS_UID_BUCKET_DURATION);
            hashSet2.add(Global.NETSTATS_UID_DELETE_AGE);
            hashSet2.add(Global.NETSTATS_UID_PERSIST_BYTES);
            hashSet2.add(Global.NETSTATS_UID_ROTATE_AGE);
            hashSet2.add(Global.NETSTATS_UID_TAG_BUCKET_DURATION);
            hashSet2.add(Global.NETSTATS_UID_TAG_DELETE_AGE);
            hashSet2.add(Global.NETSTATS_UID_TAG_PERSIST_BYTES);
            hashSet2.add(Global.NETSTATS_UID_TAG_ROTATE_AGE);
            hashSet2.add("network_preference");
            hashSet2.add(Global.NITZ_UPDATE_DIFF);
            hashSet2.add(Global.NITZ_UPDATE_SPACING);
            hashSet2.add("ntp_server");
            hashSet2.add(Global.NTP_TIMEOUT);
            hashSet2.add(Global.PDP_WATCHDOG_ERROR_POLL_COUNT);
            hashSet2.add(Global.PDP_WATCHDOG_LONG_POLL_INTERVAL_MS);
            hashSet2.add(Global.PDP_WATCHDOG_MAX_PDP_RESET_FAIL_COUNT);
            hashSet2.add(Global.PDP_WATCHDOG_POLL_INTERVAL_MS);
            hashSet2.add(Global.PDP_WATCHDOG_TRIGGER_PACKET_COUNT);
            hashSet2.add(Global.SAMPLING_PROFILER_MS);
            hashSet2.add(Global.SETUP_PREPAID_DATA_SERVICE_URL);
            hashSet2.add(Global.SETUP_PREPAID_DETECTION_REDIR_HOST);
            hashSet2.add(Global.SETUP_PREPAID_DETECTION_TARGET_URL);
            hashSet2.add(Global.TETHER_DUN_APN);
            hashSet2.add(Global.TETHER_DUN_REQUIRED);
            hashSet2.add(Global.TETHER_SUPPORTED);
            hashSet2.add("usb_mass_storage_enabled");
            hashSet2.add("use_google_mail");
            hashSet2.add(Global.WEB_AUTOFILL_QUERY_URL);
            hashSet2.add("wifi_country_code");
            hashSet2.add(Global.WIFI_FRAMEWORK_SCAN_INTERVAL_MS);
            hashSet2.add(Global.WIFI_FREQUENCY_BAND);
            hashSet2.add("wifi_idle_ms");
            hashSet2.add("wifi_max_dhcp_retry_count");
            hashSet2.add("wifi_mobile_data_transition_wakelock_timeout_ms");
            hashSet2.add("wifi_networks_available_notification_on");
            hashSet2.add("wifi_networks_available_repeat_delay");
            hashSet2.add("wifi_num_open_networks_kept");
            hashSet2.add("wifi_on");
            hashSet2.add("wifi_ap_on");
            hashSet2.add(Global.WIFI_P2P_DEVICE_NAME);
            hashSet2.add(Global.WIFI_SAVED_STATE);
            hashSet2.add(Global.WIFI_SUPPLICANT_SCAN_INTERVAL_MS);
            hashSet2.add(Global.WIFI_SUSPEND_OPTIMIZATIONS_ENABLED);
            hashSet2.add("wifi_watchdog_on");
            hashSet2.add(Global.WIFI_WATCHDOG_POOR_NETWORK_TEST_ENABLED);
            hashSet2.add(Global.ETHERNET_USE_IPOE);
            hashSet2.add(Global.ETHERNET_USE_PPPOE);
            hashSet2.add(Global.ETHERNET_ON);
            hashSet2.add(Global.ETHERNET_MODE);
            hashSet2.add(Global.ETHERNET_IPADDR);
            hashSet2.add(Global.ETHERNET_NETMASK);
            hashSet2.add(Global.ETHERNET_DNS1);
            hashSet2.add(Global.ETHERNET_DNS2);
            hashSet2.add(Global.ETHERNET_GATEWAY);
            hashSet2.add(Global.ETHERNET_IFACENAME);
            hashSet2.add(Global.WIMAX_NETWORKS_AVAILABLE_NOTIFICATION_ON);
            hashSet2.add(Global.PACKAGE_VERIFIER_ENABLE);
            hashSet2.add(Global.PACKAGE_VERIFIER_TIMEOUT);
            hashSet2.add(Global.PACKAGE_VERIFIER_DEFAULT_RESPONSE);
            hashSet2.add(Global.DATA_STALL_ALARM_NON_AGGRESSIVE_DELAY_IN_MS);
            hashSet2.add(Global.DATA_STALL_ALARM_AGGRESSIVE_DELAY_IN_MS);
            hashSet2.add(Global.GPRS_REGISTER_CHECK_PERIOD_MS);
            hashSet2.add(Global.WTF_IS_FATAL);
            hashSet2.add(Global.BATTERY_DISCHARGE_DURATION_THRESHOLD);
            hashSet2.add(Global.BATTERY_DISCHARGE_THRESHOLD);
            hashSet2.add(Global.SEND_ACTION_APP_ERROR);
            hashSet2.add(Global.DROPBOX_AGE_SECONDS);
            hashSet2.add(Global.DROPBOX_MAX_FILES);
            hashSet2.add(Global.DROPBOX_QUOTA_KB);
            hashSet2.add(Global.DROPBOX_QUOTA_PERCENT);
            hashSet2.add(Global.DROPBOX_RESERVE_PERCENT);
            hashSet2.add(Global.DROPBOX_TAG_PREFIX);
            hashSet2.add(Global.ERROR_LOGCAT_PREFIX);
            hashSet2.add(Global.SYS_FREE_STORAGE_LOG_INTERVAL);
            hashSet2.add(Global.DISK_FREE_CHANGE_REPORTING_THRESHOLD);
            hashSet2.add(Global.SYS_STORAGE_THRESHOLD_PERCENTAGE);
            hashSet2.add(Global.SYS_STORAGE_THRESHOLD_MAX_BYTES);
            hashSet2.add(Global.SYS_STORAGE_FULL_THRESHOLD_BYTES);
            hashSet2.add(Global.SYNC_MAX_RETRY_DELAY_IN_SECONDS);
            hashSet2.add(Global.CONNECTIVITY_CHANGE_DELAY);
            hashSet2.add(Global.CAPTIVE_PORTAL_DETECTION_ENABLED);
            hashSet2.add(Global.CAPTIVE_PORTAL_SERVER);
            hashSet2.add(Global.NSD_ON);
            hashSet2.add(Global.SET_INSTALL_LOCATION);
            hashSet2.add(Global.DEFAULT_INSTALL_LOCATION);
            hashSet2.add(Global.INET_CONDITION_DEBOUNCE_UP_DELAY);
            hashSet2.add(Global.INET_CONDITION_DEBOUNCE_DOWN_DELAY);
            hashSet2.add(Global.READ_EXTERNAL_STORAGE_ENFORCED_DEFAULT);
            hashSet2.add("http_proxy");
            hashSet2.add(Global.GLOBAL_HTTP_PROXY_HOST);
            hashSet2.add(Global.GLOBAL_HTTP_PROXY_PORT);
            hashSet2.add(Global.GLOBAL_HTTP_PROXY_EXCLUSION_LIST);
            hashSet2.add(Global.SET_GLOBAL_HTTP_PROXY);
            hashSet2.add(Global.DEFAULT_DNS_SERVER);
            hashSet2.add(Global.PREFERRED_NETWORK_MODE);
            SETTINGS_TO_BACKUP = new String[]{"bugreport_in_power_menu", ALLOW_MOCK_LOCATION, "parental_control_enabled", "parental_control_redirect_url", "usb_mass_storage_enabled", ACCESSIBILITY_DISPLAY_MAGNIFICATION_ENABLED, ACCESSIBILITY_DISPLAY_MAGNIFICATION_SCALE, ACCESSIBILITY_DISPLAY_MAGNIFICATION_AUTO_UPDATE, ACCESSIBILITY_SCRIPT_INJECTION, BACKUP_AUTO_RESTORE, ENABLED_ACCESSIBILITY_SERVICES, TOUCH_EXPLORATION_GRANTED_ACCESSIBILITY_SERVICES, TOUCH_EXPLORATION_ENABLED, ACCESSIBILITY_ENABLED, ACCESSIBILITY_SPEAK_PASSWORD, ACCESSIBILITY_CAPTIONING_ENABLED, ACCESSIBILITY_CAPTIONING_LOCALE, ACCESSIBILITY_CAPTIONING_BACKGROUND_COLOR, ACCESSIBILITY_CAPTIONING_FOREGROUND_COLOR, ACCESSIBILITY_CAPTIONING_EDGE_TYPE, ACCESSIBILITY_CAPTIONING_EDGE_COLOR, ACCESSIBILITY_CAPTIONING_TYPEFACE, ACCESSIBILITY_CAPTIONING_FONT_SCALE, TTS_USE_DEFAULTS, TTS_DEFAULT_RATE, TTS_DEFAULT_PITCH, TTS_DEFAULT_SYNTH, TTS_DEFAULT_LANG, TTS_DEFAULT_COUNTRY, TTS_ENABLED_PLUGINS, TTS_DEFAULT_LOCALE, "wifi_networks_available_notification_on", "wifi_networks_available_repeat_delay", "wifi_num_open_networks_kept", MOUNT_PLAY_NOTIFICATION_SND, MOUNT_UMS_AUTOSTART, MOUNT_UMS_PROMPT, MOUNT_UMS_NOTIFY_ENABLED, UI_NIGHT_MODE};
        }

        public static void getMovedKeys(HashSet<String> hashSet) {
            hashSet.addAll(MOVED_TO_GLOBAL);
        }

        public static String getString(ContentResolver contentResolver, String str) {
            return getStringForUser(contentResolver, str, UserHandle.myUserId());
        }

        public static String getStringForUser(ContentResolver contentResolver, String str, int i) {
            if (MOVED_TO_GLOBAL.contains(str)) {
                Log.w(Settings.TAG, "Setting " + str + " has moved from android.provider.Settings.Secure to android.provider.Settings.Global.");
                return Global.getStringForUser(contentResolver, str, i);
            }
            if (MOVED_TO_LOCK_SETTINGS.contains(str)) {
                synchronized (Secure.class) {
                    if (sLockSettings == null) {
                        sLockSettings = ILockSettings.Stub.asInterface(ServiceManager.getService("lock_settings"));
                        sIsSystemProcess = Process.myUid() == 1000;
                    }
                }
                ILockSettings iLockSettings = sLockSettings;
                if (iLockSettings != null && !sIsSystemProcess) {
                    try {
                        return iLockSettings.getString(str, "0", i);
                    } catch (RemoteException unused) {
                    }
                }
            }
            return sNameValueCache.getStringForUser(contentResolver, str, i);
        }

        public static boolean putString(ContentResolver contentResolver, String str, String str2) {
            return putStringForUser(contentResolver, str, str2, UserHandle.myUserId());
        }

        public static boolean putStringForUser(ContentResolver contentResolver, String str, String str2, int i) {
            if (MOVED_TO_GLOBAL.contains(str)) {
                Log.w(Settings.TAG, "Setting " + str + " has moved from android.provider.Settings.System to android.provider.Settings.Global");
                return Global.putStringForUser(contentResolver, str, str2, i);
            }
            return sNameValueCache.putStringForUser(contentResolver, str, str2, i);
        }

        public static Uri getUriFor(String str) {
            if (MOVED_TO_GLOBAL.contains(str)) {
                Log.w(Settings.TAG, "Setting " + str + " has moved from android.provider.Settings.Secure to android.provider.Settings.Global, returning global URI.");
                return Global.getUriFor(Global.CONTENT_URI, str);
            }
            return getUriFor(CONTENT_URI, str);
        }

        public static int getInt(ContentResolver contentResolver, String str, int i) {
            return getIntForUser(contentResolver, str, i, UserHandle.myUserId());
        }

        public static int getIntForUser(ContentResolver contentResolver, String str, int i, int i2) {
            if (LOCATION_MODE.equals(str)) {
                return getLocationModeForUser(contentResolver, i2);
            }
            String stringForUser = getStringForUser(contentResolver, str, i2);
            if (stringForUser == null) {
                return i;
            }
            try {
                return Integer.parseInt(stringForUser);
            } catch (NumberFormatException unused) {
                return i;
            }
        }

        public static int getInt(ContentResolver contentResolver, String str) throws SettingNotFoundException {
            return getIntForUser(contentResolver, str, UserHandle.myUserId());
        }

        public static int getIntForUser(ContentResolver contentResolver, String str, int i) throws SettingNotFoundException {
            if (LOCATION_MODE.equals(str)) {
                return getLocationModeForUser(contentResolver, i);
            }
            try {
                return Integer.parseInt(getStringForUser(contentResolver, str, i));
            } catch (NumberFormatException unused) {
                throw new SettingNotFoundException(str);
            }
        }

        public static boolean putInt(ContentResolver contentResolver, String str, int i) {
            return putIntForUser(contentResolver, str, i, UserHandle.myUserId());
        }

        public static boolean putIntForUser(ContentResolver contentResolver, String str, int i, int i2) {
            if (LOCATION_MODE.equals(str)) {
                return setLocationModeForUser(contentResolver, i, i2);
            }
            return putStringForUser(contentResolver, str, Integer.toString(i), i2);
        }

        public static long getLong(ContentResolver contentResolver, String str, long j) {
            return getLongForUser(contentResolver, str, j, UserHandle.myUserId());
        }

        public static long getLongForUser(ContentResolver contentResolver, String str, long j, int i) {
            String stringForUser = getStringForUser(contentResolver, str, i);
            if (stringForUser == null) {
                return j;
            }
            try {
                return Long.parseLong(stringForUser);
            } catch (NumberFormatException unused) {
                return j;
            }
        }

        public static long getLong(ContentResolver contentResolver, String str) throws SettingNotFoundException {
            return getLongForUser(contentResolver, str, UserHandle.myUserId());
        }

        public static long getLongForUser(ContentResolver contentResolver, String str, int i) throws SettingNotFoundException {
            try {
                return Long.parseLong(getStringForUser(contentResolver, str, i));
            } catch (NumberFormatException unused) {
                throw new SettingNotFoundException(str);
            }
        }

        public static boolean putLong(ContentResolver contentResolver, String str, long j) {
            return putLongForUser(contentResolver, str, j, UserHandle.myUserId());
        }

        public static boolean putLongForUser(ContentResolver contentResolver, String str, long j, int i) {
            return putStringForUser(contentResolver, str, Long.toString(j), i);
        }

        public static float getFloat(ContentResolver contentResolver, String str, float f) {
            return getFloatForUser(contentResolver, str, f, UserHandle.myUserId());
        }

        public static float getFloatForUser(ContentResolver contentResolver, String str, float f, int i) {
            String stringForUser = getStringForUser(contentResolver, str, i);
            if (stringForUser == null) {
                return f;
            }
            try {
                return Float.parseFloat(stringForUser);
            } catch (NumberFormatException unused) {
                return f;
            }
        }

        public static float getFloat(ContentResolver contentResolver, String str) throws SettingNotFoundException {
            return getFloatForUser(contentResolver, str, UserHandle.myUserId());
        }

        public static float getFloatForUser(ContentResolver contentResolver, String str, int i) throws SettingNotFoundException {
            String stringForUser = getStringForUser(contentResolver, str, i);
            if (stringForUser == null) {
                throw new SettingNotFoundException(str);
            }
            try {
                return Float.parseFloat(stringForUser);
            } catch (NumberFormatException unused) {
                throw new SettingNotFoundException(str);
            }
        }

        public static boolean putFloat(ContentResolver contentResolver, String str, float f) {
            return putFloatForUser(contentResolver, str, f, UserHandle.myUserId());
        }

        public static boolean putFloatForUser(ContentResolver contentResolver, String str, float f, int i) {
            return putStringForUser(contentResolver, str, Float.toString(f), i);
        }

        @Deprecated
        public static final boolean isLocationProviderEnabled(ContentResolver contentResolver, String str) {
            return isLocationProviderEnabledForUser(contentResolver, str, UserHandle.myUserId());
        }

        @Deprecated
        public static final boolean isLocationProviderEnabledForUser(ContentResolver contentResolver, String str, int i) {
            return TextUtils.delimitedStringContains(getStringForUser(contentResolver, "location_providers_allowed", i), PhoneNumberUtils.PAUSE, str);
        }

        @Deprecated
        public static final void setLocationProviderEnabled(ContentResolver contentResolver, String str, boolean z) {
            setLocationProviderEnabledForUser(contentResolver, str, z, UserHandle.myUserId());
        }

        @Deprecated
        public static final boolean setLocationProviderEnabledForUser(ContentResolver contentResolver, String str, boolean z, int i) {
            String str2;
            boolean zPutStringForUser;
            synchronized (Settings.mLocationSettingsLock) {
                if (z) {
                    str2 = "+" + str;
                } else {
                    str2 = "-" + str;
                }
                zPutStringForUser = putStringForUser(contentResolver, "location_providers_allowed", str2, i);
            }
            return zPutStringForUser;
        }

        private static final boolean setLocationModeForUser(ContentResolver contentResolver, int i, int i2) {
            boolean z;
            boolean z2;
            boolean z3;
            boolean locationProviderEnabledForUser;
            synchronized (Settings.mLocationSettingsLock) {
                z = false;
                try {
                    if (i != 0) {
                        if (i == 1) {
                            z3 = false;
                            z2 = true;
                        } else if (i == 2) {
                            z2 = false;
                            z3 = true;
                        } else {
                            if (i != 3) {
                                throw new IllegalArgumentException("Invalid location mode: " + i);
                            }
                            z2 = true;
                        }
                        locationProviderEnabledForUser = setLocationProviderEnabledForUser(contentResolver, "gps", z2, i2);
                        boolean locationProviderEnabledForUser2 = setLocationProviderEnabledForUser(contentResolver, LocationManager.NETWORK_PROVIDER, z3, i2);
                        if (locationProviderEnabledForUser && locationProviderEnabledForUser2) {
                            z = true;
                        }
                    } else {
                        z2 = false;
                    }
                    z3 = z2;
                    locationProviderEnabledForUser = setLocationProviderEnabledForUser(contentResolver, "gps", z2, i2);
                    boolean locationProviderEnabledForUser22 = setLocationProviderEnabledForUser(contentResolver, LocationManager.NETWORK_PROVIDER, z3, i2);
                    if (locationProviderEnabledForUser) {
                        z = true;
                    }
                } catch (Throwable th) {
                    throw th;
                }
            }
            return z;
        }

        private static final int getLocationModeForUser(ContentResolver contentResolver, int i) {
            synchronized (Settings.mLocationSettingsLock) {
                boolean zIsLocationProviderEnabledForUser = isLocationProviderEnabledForUser(contentResolver, "gps", i);
                boolean zIsLocationProviderEnabledForUser2 = isLocationProviderEnabledForUser(contentResolver, LocationManager.NETWORK_PROVIDER, i);
                if (zIsLocationProviderEnabledForUser && zIsLocationProviderEnabledForUser2) {
                    return 3;
                }
                if (zIsLocationProviderEnabledForUser) {
                    return 1;
                }
                return zIsLocationProviderEnabledForUser2 ? 2 : 0;
            }
        }
    }

    public static final class Global extends NameValueTable {
        public static final String ADB_ENABLED = "adb_enabled";
        public static final String AIRPLANE_MODE_ON = "airplane_mode_on";
        public static final String AIRPLANE_MODE_ON_FAST_BOOT = "airplane_mode_on_fast_boot";
        public static final String AIRPLANE_MODE_RADIOS = "airplane_mode_radios";
        public static final String AIRPLANE_MODE_TOGGLEABLE_RADIOS = "airplane_mode_toggleable_radios";
        public static final String ALWAYS_FINISH_ACTIVITIES = "always_finish_activities";
        public static final String ANIMATOR_DURATION_SCALE = "animator_duration_scale";
        public static final String ASSISTED_GPS_ENABLED = "assisted_gps_enabled";
        public static final String AUDIO_SAFE_VOLUME_STATE = "audio_safe_volume_state";
        public static final String AUTO_TIME = "auto_time";
        public static final String AUTO_TIME_ZONE = "auto_time_zone";
        public static final String BATTERY_DISCHARGE_DURATION_THRESHOLD = "battery_discharge_duration_threshold";
        public static final String BATTERY_DISCHARGE_THRESHOLD = "battery_discharge_threshold";
        public static final String BLUETOOTH_A2DP_SINK_PRIORITY_PREFIX = "bluetooth_a2dp_sink_priority_";
        public static final String BLUETOOTH_HEADSET_PRIORITY_PREFIX = "bluetooth_headset_priority_";
        public static final String BLUETOOTH_INPUT_DEVICE_PRIORITY_PREFIX = "bluetooth_input_device_priority_";
        public static final String BLUETOOTH_MAP_PRIORITY_PREFIX = "bluetooth_map_priority_";
        public static final String BLUETOOTH_ON = "bluetooth_on";
        public static final String BUGREPORT_IN_POWER_MENU = "bugreport_in_power_menu";
        public static final String CALL_AUTO_RETRY = "call_auto_retry";
        public static final String CAPTIVE_PORTAL_DETECTION_ENABLED = "captive_portal_detection_enabled";
        public static final String CAPTIVE_PORTAL_SERVER = "captive_portal_server";
        public static final String CAR_DOCK_SOUND = "car_dock_sound";
        public static final String CAR_UNDOCK_SOUND = "car_undock_sound";
        public static final String CDMA_CELL_BROADCAST_SMS = "cdma_cell_broadcast_sms";
        public static final String CDMA_ROAMING_MODE = "roaming_settings";
        public static final String CDMA_SUBSCRIPTION_MODE = "subscription_mode";
        public static final String CERT_PIN_UPDATE_CONTENT_URL = "cert_pin_content_url";
        public static final String CERT_PIN_UPDATE_METADATA_URL = "cert_pin_metadata_url";
        public static final String COMPATIBILITY_MODE = "compatibility_mode";
        public static final String CONNECTIVITY_CHANGE_DELAY = "connectivity_change_delay";
        public static final String CONNECTIVITY_SAMPLING_INTERVAL_IN_SECONDS = "connectivity_sampling_interval_in_seconds";
        public static final Uri CONTENT_URI;
        public static final String DATA_ACTIVITY_TIMEOUT_MOBILE = "data_activity_timeout_mobile";
        public static final String DATA_ACTIVITY_TIMEOUT_WIFI = "data_activity_timeout_wifi";
        public static final String DATA_ROAMING = "data_roaming";
        public static final String DATA_STALL_ALARM_AGGRESSIVE_DELAY_IN_MS = "data_stall_alarm_aggressive_delay_in_ms";
        public static final String DATA_STALL_ALARM_NON_AGGRESSIVE_DELAY_IN_MS = "data_stall_alarm_non_aggressive_delay_in_ms";
        public static final String DEBUG_APP = "debug_app";
        public static final String DEFAULT_DNS_SERVER = "default_dns_server";
        public static final String DEFAULT_INSTALL_LOCATION = "default_install_location";
        public static final String DESK_DOCK_SOUND = "desk_dock_sound";
        public static final String DESK_UNDOCK_SOUND = "desk_undock_sound";
        public static final String DEVELOPMENT_FORCE_RTL = "debug.force_rtl";
        public static final String DEVELOPMENT_SETTINGS_ENABLED = "development_settings_enabled";
        public static final String DEVICE_PROVISIONED = "device_provisioned";
        public static final String DISK_FREE_CHANGE_REPORTING_THRESHOLD = "disk_free_change_reporting_threshold";
        public static final String DISPLAY_DENSITY_FORCED = "display_density_forced";
        public static final String DISPLAY_SIZE_FORCED = "display_size_forced";
        public static final String DOCK_AUDIO_MEDIA_ENABLED = "dock_audio_media_enabled";
        public static final String DOCK_SOUNDS_ENABLED = "dock_sounds_enabled";
        public static final String DOWNLOAD_MAX_BYTES_OVER_MOBILE = "download_manager_max_bytes_over_mobile";
        public static final String DOWNLOAD_RECOMMENDED_MAX_BYTES_OVER_MOBILE = "download_manager_recommended_max_bytes_over_mobile";
        public static final String DROPBOX_AGE_SECONDS = "dropbox_age_seconds";
        public static final String DROPBOX_MAX_FILES = "dropbox_max_files";
        public static final String DROPBOX_QUOTA_KB = "dropbox_quota_kb";
        public static final String DROPBOX_QUOTA_PERCENT = "dropbox_quota_percent";
        public static final String DROPBOX_RESERVE_PERCENT = "dropbox_reserve_percent";
        public static final String DROPBOX_TAG_PREFIX = "dropbox:";
        public static final String EMERGENCY_TONE = "emergency_tone";
        public static final String ENABLE_ACCESSIBILITY_GLOBAL_GESTURE_ENABLED = "enable_accessibility_global_gesture_enabled";
        public static final String ERROR_LOGCAT_PREFIX = "logcat_for_";
        public static final String ETHERNET_DNS1 = "eth_dns1";
        public static final String ETHERNET_DNS2 = "eth_dns2";
        public static final String ETHERNET_GATEWAY = "eth_gateway";
        public static final String ETHERNET_HWADDR = "eth_hwaddr";
        public static final String ETHERNET_IFACENAME = "eth_ifname";
        public static final String ETHERNET_IPADDR = "eth_ipaddr";
        public static final String ETHERNET_MODE = "eth_mode";
        public static final String ETHERNET_NETMASK = "eth_netmask";
        public static final String ETHERNET_ON = "eth_on";
        public static final String ETHERNET_USE_IPOE = "eth_use_ipoe";
        public static final String ETHERNET_USE_PPPOE = "eth_use_pppoe";
        public static final String FANCY_IME_ANIMATIONS = "fancy_ime_animations";
        public static final String GLOBAL_HTTP_PROXY_EXCLUSION_LIST = "global_http_proxy_exclusion_list";
        public static final String GLOBAL_HTTP_PROXY_HOST = "global_http_proxy_host";
        public static final String GLOBAL_HTTP_PROXY_PAC = "global_proxy_pac_url";
        public static final String GLOBAL_HTTP_PROXY_PORT = "global_http_proxy_port";
        public static final String GPRS_REGISTER_CHECK_PERIOD_MS = "gprs_register_check_period_ms";
        public static final String HTTP_PROXY = "http_proxy";
        public static final String INET_CONDITION_DEBOUNCE_DOWN_DELAY = "inet_condition_debounce_down_delay";
        public static final String INET_CONDITION_DEBOUNCE_UP_DELAY = "inet_condition_debounce_up_delay";
        public static final String INSTALL_NON_MARKET_APPS = "install_non_market_apps";
        public static final String INTENT_FIREWALL_UPDATE_CONTENT_URL = "intent_firewall_content_url";
        public static final String INTENT_FIREWALL_UPDATE_METADATA_URL = "intent_firewall_metadata_url";
        public static final String LOCK_SOUND = "lock_sound";
        public static final String LOW_BATTERY_SOUND = "low_battery_sound";
        public static final String LOW_BATTERY_SOUND_TIMEOUT = "low_battery_sound_timeout";
        public static final String MDC_INITIAL_MAX_RETRY = "mdc_initial_max_retry";
        public static final String MOBILE_DATA = "mobile_data";
        public static final String MODE_RINGER = "mode_ringer";
        public static final String NETSTATS_DEV_BUCKET_DURATION = "netstats_dev_bucket_duration";
        public static final String NETSTATS_DEV_DELETE_AGE = "netstats_dev_delete_age";
        public static final String NETSTATS_DEV_PERSIST_BYTES = "netstats_dev_persist_bytes";
        public static final String NETSTATS_DEV_ROTATE_AGE = "netstats_dev_rotate_age";
        public static final String NETSTATS_ENABLED = "netstats_enabled";
        public static final String NETSTATS_GLOBAL_ALERT_BYTES = "netstats_global_alert_bytes";
        public static final String NETSTATS_POLL_INTERVAL = "netstats_poll_interval";
        public static final String NETSTATS_REPORT_XT_OVER_DEV = "netstats_report_xt_over_dev";
        public static final String NETSTATS_SAMPLE_ENABLED = "netstats_sample_enabled";
        public static final String NETSTATS_TIME_CACHE_MAX_AGE = "netstats_time_cache_max_age";
        public static final String NETSTATS_UID_BUCKET_DURATION = "netstats_uid_bucket_duration";
        public static final String NETSTATS_UID_DELETE_AGE = "netstats_uid_delete_age";
        public static final String NETSTATS_UID_PERSIST_BYTES = "netstats_uid_persist_bytes";
        public static final String NETSTATS_UID_ROTATE_AGE = "netstats_uid_rotate_age";
        public static final String NETSTATS_UID_TAG_BUCKET_DURATION = "netstats_uid_tag_bucket_duration";
        public static final String NETSTATS_UID_TAG_DELETE_AGE = "netstats_uid_tag_delete_age";
        public static final String NETSTATS_UID_TAG_PERSIST_BYTES = "netstats_uid_tag_persist_bytes";
        public static final String NETSTATS_UID_TAG_ROTATE_AGE = "netstats_uid_tag_rotate_age";
        public static final String NETWORK_PREFERENCE = "network_preference";
        public static final String NITZ_UPDATE_DIFF = "nitz_update_diff";
        public static final String NITZ_UPDATE_SPACING = "nitz_update_spacing";
        public static final String NSD_ON = "nsd_on";
        public static final String NTP_SERVER = "ntp_server";
        public static final String NTP_TIMEOUT = "ntp_timeout";
        public static final String OVERLAY_DISPLAY_DEVICES = "overlay_display_devices";
        public static final String PACKAGE_VERIFIER_DEFAULT_RESPONSE = "verifier_default_response";
        public static final String PACKAGE_VERIFIER_ENABLE = "package_verifier_enable";
        public static final String PACKAGE_VERIFIER_INCLUDE_ADB = "verifier_verify_adb_installs";
        public static final String PACKAGE_VERIFIER_SETTING_VISIBLE = "verifier_setting_visible";
        public static final String PACKAGE_VERIFIER_TIMEOUT = "verifier_timeout";
        public static final String PAC_CHANGE_DELAY = "pac_change_delay";
        public static final String PDP_WATCHDOG_ERROR_POLL_COUNT = "pdp_watchdog_error_poll_count";
        public static final String PDP_WATCHDOG_ERROR_POLL_INTERVAL_MS = "pdp_watchdog_error_poll_interval_ms";
        public static final String PDP_WATCHDOG_LONG_POLL_INTERVAL_MS = "pdp_watchdog_long_poll_interval_ms";
        public static final String PDP_WATCHDOG_MAX_PDP_RESET_FAIL_COUNT = "pdp_watchdog_max_pdp_reset_fail_count";
        public static final String PDP_WATCHDOG_POLL_INTERVAL_MS = "pdp_watchdog_poll_interval_ms";
        public static final String PDP_WATCHDOG_TRIGGER_PACKET_COUNT = "pdp_watchdog_trigger_packet_count";
        public static final String POWER_SOUNDS_ENABLED = "power_sounds_enabled";
        public static final String PREFERRED_NETWORK_MODE = "preferred_network_mode";
        public static final String PROVISIONING_APN_ALARM_DELAY_IN_MS = "provisioning_apn_alarm_delay_in_ms";
        public static final String RADIO_BLUETOOTH = "bluetooth";
        public static final String RADIO_CELL = "cell";
        public static final String RADIO_NFC = "nfc";
        public static final String RADIO_WIFI = "wifi";
        public static final String RADIO_WIMAX = "wimax";
        public static final String READ_EXTERNAL_STORAGE_ENFORCED_DEFAULT = "read_external_storage_enforced_default";
        public static final String SAMPLING_PROFILER_MS = "sampling_profiler_ms";
        public static final String SELINUX_STATUS = "selinux_status";
        public static final String SELINUX_UPDATE_CONTENT_URL = "selinux_content_url";
        public static final String SELINUX_UPDATE_METADATA_URL = "selinux_metadata_url";
        public static final String SEND_ACTION_APP_ERROR = "send_action_app_error";
        public static final String[] SETTINGS_TO_BACKUP;
        public static final String SETUP_PREPAID_DATA_SERVICE_URL = "setup_prepaid_data_service_url";
        public static final String SETUP_PREPAID_DETECTION_REDIR_HOST = "setup_prepaid_detection_redir_host";
        public static final String SETUP_PREPAID_DETECTION_TARGET_URL = "setup_prepaid_detection_target_url";
        public static final String SET_GLOBAL_HTTP_PROXY = "set_global_http_proxy";
        public static final String SET_INSTALL_LOCATION = "set_install_location";
        public static final String SHOW_PROCESSES = "show_processes";
        public static final String SMS_OUTGOING_CHECK_INTERVAL_MS = "sms_outgoing_check_interval_ms";
        public static final String SMS_OUTGOING_CHECK_MAX_COUNT = "sms_outgoing_check_max_count";
        public static final String SMS_SHORT_CODES_UPDATE_CONTENT_URL = "sms_short_codes_content_url";
        public static final String SMS_SHORT_CODES_UPDATE_METADATA_URL = "sms_short_codes_metadata_url";
        public static final String SMS_SHORT_CODE_CONFIRMATION = "sms_short_code_confirmation";
        public static final String SMS_SHORT_CODE_RULE = "sms_short_code_rule";
        public static final String STAY_ON_WHILE_PLUGGED_IN = "stay_on_while_plugged_in";
        public static final String SYNC_MAX_RETRY_DELAY_IN_SECONDS = "sync_max_retry_delay_in_seconds";
        public static final String SYS_FREE_STORAGE_LOG_INTERVAL = "sys_free_storage_log_interval";
        public static final String SYS_PROP_SETTING_VERSION = "sys.settings_global_version";
        public static final String SYS_STORAGE_FULL_THRESHOLD_BYTES = "sys_storage_full_threshold_bytes";
        public static final String SYS_STORAGE_THRESHOLD_MAX_BYTES = "sys_storage_threshold_max_bytes";
        public static final String SYS_STORAGE_THRESHOLD_PERCENTAGE = "sys_storage_threshold_percentage";
        public static final String TETHER_DUN_APN = "tether_dun_apn";
        public static final String TETHER_DUN_REQUIRED = "tether_dun_required";
        public static final String TETHER_SUPPORTED = "tether_supported";
        public static final String TRANSITION_ANIMATION_SCALE = "transition_animation_scale";
        public static final String TZINFO_UPDATE_CONTENT_URL = "tzinfo_content_url";
        public static final String TZINFO_UPDATE_METADATA_URL = "tzinfo_metadata_url";
        public static final String UNLOCK_SOUND = "unlock_sound";
        public static final String USB_MASS_STORAGE_ENABLED = "usb_mass_storage_enabled";
        public static final String USE_GOOGLE_MAIL = "use_google_mail";
        public static final String WAIT_FOR_DEBUGGER = "wait_for_debugger";
        public static final String WEB_AUTOFILL_QUERY_URL = "web_autofill_query_url";
        public static final String WIFI_AP_ON = "wifi_ap_on";
        public static final String WIFI_COUNTRY_CODE = "wifi_country_code";
        public static final String WIFI_DISPLAY_CERTIFICATION_ON = "wifi_display_certification_on";
        public static final String WIFI_DISPLAY_ON = "wifi_display_on";
        public static final String WIFI_DISPLAY_WPS_CONFIG = "wifi_display_wps_config";
        public static final String WIFI_FRAMEWORK_SCAN_INTERVAL_MS = "wifi_framework_scan_interval_ms";
        public static final String WIFI_FREQUENCY_BAND = "wifi_frequency_band";
        public static final String WIFI_IDLE_MS = "wifi_idle_ms";
        public static final String WIFI_MAX_DHCP_RETRY_COUNT = "wifi_max_dhcp_retry_count";
        public static final String WIFI_MOBILE_DATA_TRANSITION_WAKELOCK_TIMEOUT_MS = "wifi_mobile_data_transition_wakelock_timeout_ms";
        public static final String WIFI_NETWORKS_AVAILABLE_NOTIFICATION_ON = "wifi_networks_available_notification_on";
        public static final String WIFI_NETWORKS_AVAILABLE_REPEAT_DELAY = "wifi_networks_available_repeat_delay";
        public static final String WIFI_NUM_OPEN_NETWORKS_KEPT = "wifi_num_open_networks_kept";
        public static final String WIFI_ON = "wifi_on";
        public static final String WIFI_P2P_DEVICE_NAME = "wifi_p2p_device_name";
        public static final String WIFI_REENABLE_DELAY_MS = "wifi_reenable_delay";
        public static final String WIFI_SAVED_STATE = "wifi_saved_state";
        public static final String WIFI_SCAN_ALWAYS_AVAILABLE = "wifi_scan_always_enabled";
        public static final String WIFI_SCAN_INTERVAL_WHEN_P2P_CONNECTED_MS = "wifi_scan_interval_p2p_connected_ms";
        public static final String WIFI_SLEEP_POLICY = "wifi_sleep_policy";
        public static final int WIFI_SLEEP_POLICY_DEFAULT = 0;
        public static final int WIFI_SLEEP_POLICY_NEVER = 2;
        public static final int WIFI_SLEEP_POLICY_NEVER_WHILE_PLUGGED = 1;
        public static final String WIFI_SUPPLICANT_SCAN_INTERVAL_MS = "wifi_supplicant_scan_interval_ms";
        public static final String WIFI_SUSPEND_OPTIMIZATIONS_ENABLED = "wifi_suspend_optimizations_enabled";
        public static final String WIFI_WATCHDOG_ON = "wifi_watchdog_on";
        public static final String WIFI_WATCHDOG_POOR_NETWORK_TEST_ENABLED = "wifi_watchdog_poor_network_test_enabled";
        public static final String WIMAX_NETWORKS_AVAILABLE_NOTIFICATION_ON = "wimax_networks_available_notification_on";
        public static final String WINDOW_ANIMATION_SCALE = "window_animation_scale";
        public static final String WIRELESS_CHARGING_STARTED_SOUND = "wireless_charging_started_sound";
        public static final String WTF_IS_FATAL = "wtf_is_fatal";
        private static NameValueCache sNameValueCache;

        static {
            Uri uri = Uri.parse("content://settings/global");
            CONTENT_URI = uri;
            SETTINGS_TO_BACKUP = new String[]{"bugreport_in_power_menu", "stay_on_while_plugged_in", "auto_time", "auto_time_zone", "power_sounds_enabled", "dock_sounds_enabled", "usb_mass_storage_enabled", ENABLE_ACCESSIBILITY_GLOBAL_GESTURE_ENABLED, "wifi_networks_available_notification_on", "wifi_networks_available_repeat_delay", WIFI_WATCHDOG_POOR_NETWORK_TEST_ENABLED, "wifi_num_open_networks_kept", EMERGENCY_TONE, CALL_AUTO_RETRY, DOCK_AUDIO_MEDIA_ENABLED};
            sNameValueCache = new NameValueCache(SYS_PROP_SETTING_VERSION, uri, Settings.CALL_METHOD_GET_GLOBAL, Settings.CALL_METHOD_PUT_GLOBAL);
        }

        public static final String getBluetoothHeadsetPriorityKey(String str) {
            return BLUETOOTH_HEADSET_PRIORITY_PREFIX + str.toUpperCase(Locale.ROOT);
        }

        public static final String getBluetoothA2dpSinkPriorityKey(String str) {
            return BLUETOOTH_A2DP_SINK_PRIORITY_PREFIX + str.toUpperCase(Locale.ROOT);
        }

        public static final String getBluetoothInputDevicePriorityKey(String str) {
            return BLUETOOTH_INPUT_DEVICE_PRIORITY_PREFIX + str.toUpperCase(Locale.ROOT);
        }

        public static final String getBluetoothMapPriorityKey(String str) {
            return BLUETOOTH_MAP_PRIORITY_PREFIX + str.toUpperCase(Locale.ROOT);
        }

        public static String getString(ContentResolver contentResolver, String str) {
            return getStringForUser(contentResolver, str, UserHandle.myUserId());
        }

        public static String getStringForUser(ContentResolver contentResolver, String str, int i) {
            return sNameValueCache.getStringForUser(contentResolver, str, i);
        }

        public static boolean putString(ContentResolver contentResolver, String str, String str2) {
            return putStringForUser(contentResolver, str, str2, UserHandle.myUserId());
        }

        public static boolean putStringForUser(ContentResolver contentResolver, String str, String str2, int i) {
            return sNameValueCache.putStringForUser(contentResolver, str, str2, i);
        }

        public static Uri getUriFor(String str) {
            return getUriFor(CONTENT_URI, str);
        }

        public static int getInt(ContentResolver contentResolver, String str, int i) {
            String string = getString(contentResolver, str);
            if (string == null) {
                return i;
            }
            try {
                return Integer.parseInt(string);
            } catch (NumberFormatException unused) {
                return i;
            }
        }

        public static int getInt(ContentResolver contentResolver, String str) throws SettingNotFoundException {
            try {
                return Integer.parseInt(getString(contentResolver, str));
            } catch (NumberFormatException unused) {
                throw new SettingNotFoundException(str);
            }
        }

        public static boolean putInt(ContentResolver contentResolver, String str, int i) {
            if (WIFI_SCAN_ALWAYS_AVAILABLE.equals(str) && "true".equals(SystemProperties.get("ro.wifi.usb"))) {
                return putString(contentResolver, str, Integer.toString(0));
            }
            return putString(contentResolver, str, Integer.toString(i));
        }

        public static long getLong(ContentResolver contentResolver, String str, long j) {
            String string = getString(contentResolver, str);
            if (string == null) {
                return j;
            }
            try {
                return Long.parseLong(string);
            } catch (NumberFormatException unused) {
                return j;
            }
        }

        public static long getLong(ContentResolver contentResolver, String str) throws SettingNotFoundException {
            try {
                return Long.parseLong(getString(contentResolver, str));
            } catch (NumberFormatException unused) {
                throw new SettingNotFoundException(str);
            }
        }

        public static boolean putLong(ContentResolver contentResolver, String str, long j) {
            return putString(contentResolver, str, Long.toString(j));
        }

        public static float getFloat(ContentResolver contentResolver, String str, float f) {
            String string = getString(contentResolver, str);
            if (string == null) {
                return f;
            }
            try {
                return Float.parseFloat(string);
            } catch (NumberFormatException unused) {
                return f;
            }
        }

        public static float getFloat(ContentResolver contentResolver, String str) throws SettingNotFoundException {
            String string = getString(contentResolver, str);
            if (string == null) {
                throw new SettingNotFoundException(str);
            }
            try {
                return Float.parseFloat(string);
            } catch (NumberFormatException unused) {
                throw new SettingNotFoundException(str);
            }
        }

        public static boolean putFloat(ContentResolver contentResolver, String str, float f) {
            return putString(contentResolver, str, Float.toString(f));
        }
    }

    public static final class Bookmarks implements BaseColumns {
        public static final String FOLDER = "folder";
        public static final String ID = "_id";
        public static final String INTENT = "intent";
        public static final String ORDERING = "ordering";
        public static final String SHORTCUT = "shortcut";
        private static final String TAG = "Bookmarks";
        public static final String TITLE = "title";
        private static final String sShortcutSelection = "shortcut=?";
        public static final Uri CONTENT_URI = Uri.parse("content://settings/bookmarks");
        private static final String[] sIntentProjection = {"intent"};
        private static final String[] sShortcutProjection = {"_id", "shortcut"};

        public static CharSequence getLabelForFolder(Resources resources, String str) {
            return str;
        }

        public static Intent getIntentForShortcut(ContentResolver contentResolver, char c) {
            Cursor cursorQuery = contentResolver.query(CONTENT_URI, sIntentProjection, sShortcutSelection, new String[]{String.valueOf((int) c)}, ORDERING);
            Intent uri = null;
            while (uri == null) {
                try {
                    if (!cursorQuery.moveToNext()) {
                        break;
                    }
                    try {
                        uri = Intent.parseUri(cursorQuery.getString(cursorQuery.getColumnIndexOrThrow("intent")), 0);
                    } catch (IllegalArgumentException e) {
                        Log.w(TAG, "Intent column not found", e);
                    } catch (URISyntaxException unused) {
                    }
                } finally {
                    if (cursorQuery != null) {
                        cursorQuery.close();
                    }
                }
            }
            return uri;
        }

        public static Uri add(ContentResolver contentResolver, Intent intent, String str, String str2, char c, int i) {
            if (c != 0) {
                contentResolver.delete(CONTENT_URI, sShortcutSelection, new String[]{String.valueOf((int) c)});
            }
            ContentValues contentValues = new ContentValues();
            if (str != null) {
                contentValues.put("title", str);
            }
            if (str2 != null) {
                contentValues.put("folder", str2);
            }
            contentValues.put("intent", intent.toUri(0));
            if (c != 0) {
                contentValues.put("shortcut", Integer.valueOf(c));
            }
            contentValues.put(ORDERING, Integer.valueOf(i));
            return contentResolver.insert(CONTENT_URI, contentValues);
        }

        public static CharSequence getTitle(Context context, Cursor cursor) {
            int columnIndex = cursor.getColumnIndex("title");
            int columnIndex2 = cursor.getColumnIndex("intent");
            if (columnIndex == -1 || columnIndex2 == -1) {
                throw new IllegalArgumentException("The cursor must contain the TITLE and INTENT columns.");
            }
            String string = cursor.getString(columnIndex);
            if (!TextUtils.isEmpty(string)) {
                return string;
            }
            String string2 = cursor.getString(columnIndex2);
            if (TextUtils.isEmpty(string2)) {
                return "";
            }
            try {
                Intent uri = Intent.parseUri(string2, 0);
                PackageManager packageManager = context.getPackageManager();
                ResolveInfo resolveInfoResolveActivity = packageManager.resolveActivity(uri, 0);
                return resolveInfoResolveActivity != null ? resolveInfoResolveActivity.loadLabel(packageManager) : "";
            } catch (URISyntaxException unused) {
                return "";
            }
        }
    }

    public static String getGTalkDeviceId(long j) {
        return "android-" + Long.toHexString(j);
    }
}
