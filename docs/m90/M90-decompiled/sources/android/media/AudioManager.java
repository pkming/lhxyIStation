package android.media;

import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.media.IAudioFocusDispatcher;
import android.media.IAudioService;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import com.softwinner.utils.Config;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class AudioManager {
    public static final String ACTION_AUDIO_BECOMING_NOISY = "android.media.AUDIO_BECOMING_NOISY";

    @Deprecated
    public static final String ACTION_SCO_AUDIO_STATE_CHANGED = "android.media.SCO_AUDIO_STATE_CHANGED";
    public static final String ACTION_SCO_AUDIO_STATE_UPDATED = "android.media.ACTION_SCO_AUDIO_STATE_UPDATED";
    public static final int ADJUST_LOWER = -1;
    public static final int ADJUST_RAISE = 1;
    public static final int ADJUST_SAME = 0;
    public static final int AUDIOFOCUS_GAIN = 1;
    public static final int AUDIOFOCUS_GAIN_TRANSIENT = 2;
    public static final int AUDIOFOCUS_GAIN_TRANSIENT_EXCLUSIVE = 4;
    public static final int AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK = 3;
    public static final int AUDIOFOCUS_LOSS = -1;
    public static final int AUDIOFOCUS_LOSS_TRANSIENT = -2;
    public static final int AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK = -3;
    public static final int AUDIOFOCUS_NONE = 0;
    public static final int AUDIOFOCUS_REQUEST_FAILED = 0;
    public static final int AUDIOFOCUS_REQUEST_GRANTED = 1;
    public static final String AUDIO_INPUT_ACTIVE = "audio_devices_in_active";
    public static final String AUDIO_INPUT_TYPE = "audio_devices_in";
    public static final String AUDIO_NAME_CODEC = "AUDIO_CODEC";
    public static final String AUDIO_NAME_HDMI = "AUDIO_HDMI";
    public static final String AUDIO_NAME_I2S = "AUDIO_I2S";
    public static final String AUDIO_NAME_SPDIF = "AUDIO_SPDIF";
    public static final String AUDIO_OUTPUT_ACTIVE = "audio_devices_out_active";
    public static final String AUDIO_OUTPUT_TYPE = "audio_devices_out";
    public static final int[] DEFAULT_STREAM_VOLUME = {4, 7, 5, 11, 6, 5, 7, 7, 11, 11, 11, 11};
    public static final int DEVICE_OUT_ANLG_DOCK_HEADSET = 2048;
    public static final int DEVICE_OUT_AUX_DIGITAL = 1024;
    public static final int DEVICE_OUT_BLUETOOTH_A2DP = 128;
    public static final int DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES = 256;
    public static final int DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER = 512;
    public static final int DEVICE_OUT_BLUETOOTH_SCO = 16;
    public static final int DEVICE_OUT_BLUETOOTH_SCO_CARKIT = 64;
    public static final int DEVICE_OUT_BLUETOOTH_SCO_HEADSET = 32;
    public static final int DEVICE_OUT_DEFAULT = 1073741824;
    public static final int DEVICE_OUT_DGTL_DOCK_HEADSET = 4096;
    public static final int DEVICE_OUT_EARPIECE = 1;
    public static final int DEVICE_OUT_SPEAKER = 2;
    public static final int DEVICE_OUT_USB_ACCESSORY = 8192;
    public static final int DEVICE_OUT_USB_DEVICE = 16384;
    public static final int DEVICE_OUT_WIRED_HEADPHONE = 8;
    public static final int DEVICE_OUT_WIRED_HEADSET = 4;
    public static final String EXTRA_MASTER_VOLUME_MUTED = "android.media.EXTRA_MASTER_VOLUME_MUTED";
    public static final String EXTRA_MASTER_VOLUME_VALUE = "android.media.EXTRA_MASTER_VOLUME_VALUE";
    public static final String EXTRA_PREV_MASTER_VOLUME_VALUE = "android.media.EXTRA_PREV_MASTER_VOLUME_VALUE";
    public static final String EXTRA_PREV_VOLUME_STREAM_VALUE = "android.media.EXTRA_PREV_VOLUME_STREAM_VALUE";
    public static final String EXTRA_RINGER_MODE = "android.media.EXTRA_RINGER_MODE";
    public static final String EXTRA_SCO_AUDIO_PREVIOUS_STATE = "android.media.extra.SCO_AUDIO_PREVIOUS_STATE";
    public static final String EXTRA_SCO_AUDIO_STATE = "android.media.extra.SCO_AUDIO_STATE";
    public static final String EXTRA_VIBRATE_SETTING = "android.media.EXTRA_VIBRATE_SETTING";
    public static final String EXTRA_VIBRATE_TYPE = "android.media.EXTRA_VIBRATE_TYPE";
    public static final String EXTRA_VOLUME_STREAM_TYPE = "android.media.EXTRA_VOLUME_STREAM_TYPE";
    public static final String EXTRA_VOLUME_STREAM_VALUE = "android.media.EXTRA_VOLUME_STREAM_VALUE";
    public static final int FLAG_ALLOW_RINGER_MODES = 2;
    public static final int FLAG_BLUETOOTH_ABS_VOLUME = 64;
    public static final int FLAG_FIXED_VOLUME = 32;
    public static final int FLAG_PLAY_SOUND = 4;
    public static final int FLAG_REMOVE_SOUND_AND_VIBRATE = 8;
    public static final int FLAG_SHOW_UI = 1;
    public static final int FLAG_VIBRATE = 16;
    public static final int FX_FOCUS_NAVIGATION_DOWN = 2;
    public static final int FX_FOCUS_NAVIGATION_LEFT = 3;
    public static final int FX_FOCUS_NAVIGATION_RIGHT = 4;
    public static final int FX_FOCUS_NAVIGATION_UP = 1;
    public static final int FX_KEYPRESS_DELETE = 7;
    public static final int FX_KEYPRESS_INVALID = 9;
    public static final int FX_KEYPRESS_RETURN = 8;
    public static final int FX_KEYPRESS_SPACEBAR = 6;
    public static final int FX_KEYPRESS_STANDARD = 5;
    public static final int FX_KEY_CLICK = 0;
    public static final String MASTER_MUTE_CHANGED_ACTION = "android.media.MASTER_MUTE_CHANGED_ACTION";
    public static final String MASTER_VOLUME_CHANGED_ACTION = "android.media.MASTER_VOLUME_CHANGED_ACTION";
    public static final int MODE_CURRENT = -1;
    public static final int MODE_FACTORY_TEST = 4;
    public static final int MODE_FM = 5;
    public static final int MODE_INVALID = -2;
    public static final int MODE_IN_CALL = 2;
    public static final int MODE_IN_COMMUNICATION = 3;
    public static final int MODE_NORMAL = 0;
    public static final int MODE_RINGTONE = 1;
    public static final int NUM_SOUND_EFFECTS = 10;

    @Deprecated
    public static final int NUM_STREAMS = 5;
    public static final String PROPERTY_OUTPUT_FRAMES_PER_BUFFER = "android.media.property.OUTPUT_FRAMES_PER_BUFFER";
    public static final String PROPERTY_OUTPUT_SAMPLE_RATE = "android.media.property.OUTPUT_SAMPLE_RATE";
    public static final String RINGER_MODE_CHANGED_ACTION = "android.media.RINGER_MODE_CHANGED";
    private static final int RINGER_MODE_MAX = 2;
    public static final int RINGER_MODE_NORMAL = 2;
    public static final int RINGER_MODE_SILENT = 0;
    public static final int RINGER_MODE_VIBRATE = 1;

    @Deprecated
    public static final int ROUTE_ALL = -1;

    @Deprecated
    public static final int ROUTE_BLUETOOTH = 4;

    @Deprecated
    public static final int ROUTE_BLUETOOTH_A2DP = 16;

    @Deprecated
    public static final int ROUTE_BLUETOOTH_SCO = 4;

    @Deprecated
    public static final int ROUTE_EARPIECE = 1;

    @Deprecated
    public static final int ROUTE_HEADSET = 8;

    @Deprecated
    public static final int ROUTE_SPEAKER = 2;
    public static final int SCO_AUDIO_STATE_CONNECTED = 1;
    public static final int SCO_AUDIO_STATE_CONNECTING = 2;
    public static final int SCO_AUDIO_STATE_DISCONNECTED = 0;
    public static final int SCO_AUDIO_STATE_ERROR = -1;
    public static final int SHOW_MUTE_UI = 65536;
    public static final int STREAM_ALARM = 4;
    public static final int STREAM_BLUETOOTH_SCO = 6;
    public static final int STREAM_DTMF = 8;
    public static final int STREAM_EXTERNAL = 11;
    public static final int STREAM_FM = 10;
    public static final int STREAM_MUSIC = 3;
    public static final int STREAM_NOTIFICATION = 5;
    public static final int STREAM_RING = 2;
    public static final int STREAM_SYSTEM = 1;
    public static final int STREAM_SYSTEM_ENFORCED = 7;
    public static final int STREAM_TTS = 9;
    public static final int STREAM_VOICE_CALL = 0;
    private static String TAG = "AudioManager";
    public static final int USE_DEFAULT_STREAM_TYPE = Integer.MIN_VALUE;
    public static final String VIBRATE_SETTING_CHANGED_ACTION = "android.media.VIBRATE_SETTING_CHANGED";
    public static final int VIBRATE_SETTING_OFF = 0;
    public static final int VIBRATE_SETTING_ON = 1;
    public static final int VIBRATE_SETTING_ONLY_SILENT = 2;
    public static final int VIBRATE_TYPE_NOTIFICATION = 1;
    public static final int VIBRATE_TYPE_RINGER = 0;
    public static final String VOLUME_CHANGED_ACTION = "android.media.VOLUME_CHANGED_ACTION";
    private static final String hdmiAvailable = "audio.hdmi.available";
    private static final String hdmiExpected = "audio.hdmi.expected";
    private static IAudioService sService;
    private final Context mContext;
    private final boolean mUseMasterVolume;
    private final boolean mUseVolumeKeySounds;
    private long mVolumeKeyUpTime;
    private final Binder mToken = new Binder();
    private final HashMap<String, OnAudioFocusChangeListener> mAudioFocusIdListenerMap = new HashMap<>();
    private final Object mFocusListenerLock = new Object();
    private final FocusEventHandlerDelegate mAudioFocusEventHandlerDelegate = new FocusEventHandlerDelegate();
    private final IAudioFocusDispatcher mAudioFocusDispatcher = new IAudioFocusDispatcher.Stub() { // from class: android.media.AudioManager.1
        @Override // android.media.IAudioFocusDispatcher
        public void dispatchAudioFocusChange(int i, String str) {
            AudioManager.this.mAudioFocusEventHandlerDelegate.getHandler().sendMessage(AudioManager.this.mAudioFocusEventHandlerDelegate.getHandler().obtainMessage(i, str));
        }
    };
    private final IBinder mICallBack = new Binder();

    public interface OnAudioFocusChangeListener {
        void onAudioFocusChange(int i);
    }

    public static boolean isValidRingerMode(int i) {
        return i >= 0 && i <= 2;
    }

    @Deprecated
    public int getRouting(int i) {
        return -1;
    }

    @Deprecated
    public void setBluetoothA2dpOn(boolean z) {
    }

    @Deprecated
    public void setRouting(int i, int i2, int i3) {
    }

    @Deprecated
    public void setWiredHeadsetOn(boolean z) {
    }

    public AudioManager(Context context) {
        this.mContext = context;
        this.mUseMasterVolume = context.getResources().getBoolean(17891345);
        this.mUseVolumeKeySounds = context.getResources().getBoolean(17891346);
    }

    private static IAudioService getService() {
        IAudioService iAudioService = sService;
        if (iAudioService != null) {
            return iAudioService;
        }
        IAudioService iAudioServiceAsInterface = IAudioService.Stub.asInterface(ServiceManager.getService(Context.AUDIO_SERVICE));
        sService = iAudioServiceAsInterface;
        return iAudioServiceAsInterface;
    }

    public void dispatchMediaKeyEvent(KeyEvent keyEvent) {
        try {
            getService().dispatchMediaKeyEvent(keyEvent);
        } catch (RemoteException e) {
            Log.e(TAG, "dispatchMediaKeyEvent threw exception ", e);
        }
    }

    public void preDispatchKeyEvent(KeyEvent keyEvent, int i) {
        int keyCode = keyEvent.getKeyCode();
        if (keyCode == 25 || keyCode == 24 || keyCode == 164 || this.mVolumeKeyUpTime + 300 <= SystemClock.uptimeMillis()) {
            return;
        }
        if (this.mUseMasterVolume) {
            adjustMasterVolume(0, 8);
        } else {
            adjustSuggestedStreamVolume(0, i, 8);
        }
    }

    public void handleKeyDown(KeyEvent keyEvent, int i) {
        int keyCode = keyEvent.getKeyCode();
        if (keyCode == 24 || keyCode == 25) {
            if (this.mUseMasterVolume) {
                adjustMasterVolume(keyCode != 24 ? -1 : 1, 17);
                return;
            } else {
                adjustSuggestedStreamVolume(keyCode != 24 ? -1 : 1, i, 17);
                return;
            }
        }
        if (keyCode == 164 && keyEvent.getRepeatCount() == 0 && this.mUseMasterVolume) {
            setMasterMute(!isMasterMute());
        }
    }

    public void handleKeyUp(KeyEvent keyEvent, int i) {
        int keyCode = keyEvent.getKeyCode();
        if (keyCode == 24 || keyCode == 25) {
            if (this.mUseVolumeKeySounds) {
                if (this.mUseMasterVolume) {
                    adjustMasterVolume(0, 4);
                } else {
                    adjustSuggestedStreamVolume(0, i, 4);
                }
            }
            this.mVolumeKeyUpTime = SystemClock.uptimeMillis();
        }
    }

    public void adjustStreamVolume(int i, int i2, int i3) {
        IAudioService service = getService();
        try {
            if (this.mUseMasterVolume) {
                service.adjustMasterVolume(i2, i3, this.mContext.getOpPackageName());
            } else {
                service.adjustStreamVolume(i, i2, i3, this.mContext.getOpPackageName());
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in adjustStreamVolume", e);
        }
    }

    public void adjustVolume(int i, int i2) {
        IAudioService service = getService();
        try {
            if (this.mUseMasterVolume) {
                service.adjustMasterVolume(i, i2, this.mContext.getOpPackageName());
            } else {
                service.adjustVolume(i, i2, this.mContext.getOpPackageName());
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in adjustVolume", e);
        }
    }

    public void adjustSuggestedStreamVolume(int i, int i2, int i3) {
        IAudioService service = getService();
        try {
            if (this.mUseMasterVolume) {
                service.adjustMasterVolume(i, i3, this.mContext.getOpPackageName());
            } else {
                service.adjustSuggestedStreamVolume(i, i2, i3, this.mContext.getOpPackageName());
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in adjustSuggestedStreamVolume", e);
        }
    }

    public void adjustMasterVolume(int i, int i2) {
        try {
            getService().adjustMasterVolume(i, i2, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in adjustMasterVolume", e);
        }
    }

    public int getRingerMode() {
        try {
            return getService().getRingerMode();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getRingerMode", e);
            return 2;
        }
    }

    public int getStreamMaxVolume(int i) {
        IAudioService service = getService();
        try {
            if (this.mUseMasterVolume) {
                return service.getMasterMaxVolume();
            }
            return service.getStreamMaxVolume(i);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getStreamMaxVolume", e);
            return 0;
        }
    }

    public int getStreamVolume(int i) {
        IAudioService service = getService();
        try {
            if (this.mUseMasterVolume) {
                return service.getMasterVolume();
            }
            return service.getStreamVolume(i);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getStreamVolume", e);
            return 0;
        }
    }

    public int getLastAudibleStreamVolume(int i) {
        IAudioService service = getService();
        try {
            if (this.mUseMasterVolume) {
                return service.getLastAudibleMasterVolume();
            }
            return service.getLastAudibleStreamVolume(i);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getLastAudibleStreamVolume", e);
            return 0;
        }
    }

    public int getMasterStreamType() {
        try {
            return getService().getMasterStreamType();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getMasterStreamType", e);
            return 2;
        }
    }

    public void setRingerMode(int i) {
        if (Config.getTargetPlatform(1) == 8) {
            Log.d("CMCC-WASU", "AudioManager setRingMode return!");
        } else if (isValidRingerMode(i)) {
            try {
                getService().setRingerMode(i);
            } catch (RemoteException e) {
                Log.e(TAG, "Dead object in setRingerMode", e);
            }
        }
    }

    public void setStreamVolume(int i, int i2, int i3) {
        IAudioService service = getService();
        try {
            if (this.mUseMasterVolume) {
                service.setMasterVolume(i2, i3, this.mContext.getOpPackageName());
            } else {
                service.setStreamVolume(i, i2, i3, this.mContext.getOpPackageName());
            }
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setStreamVolume", e);
        }
    }

    public int getMasterMaxVolume() {
        try {
            return getService().getMasterMaxVolume();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getMasterMaxVolume", e);
            return 0;
        }
    }

    public int getMasterVolume() {
        try {
            return getService().getMasterVolume();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getMasterVolume", e);
            return 0;
        }
    }

    public int getLastAudibleMasterVolume() {
        try {
            return getService().getLastAudibleMasterVolume();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getLastAudibleMasterVolume", e);
            return 0;
        }
    }

    public void setMasterVolume(int i, int i2) {
        try {
            getService().setMasterVolume(i, i2, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setMasterVolume", e);
        }
    }

    public void setStreamSolo(int i, boolean z) {
        try {
            getService().setStreamSolo(i, z, this.mICallBack);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setStreamSolo", e);
        }
    }

    public void setStreamMute(int i, boolean z) {
        try {
            getService().setStreamMute(i, z, this.mICallBack);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setStreamMute", e);
        }
    }

    public boolean isStreamMute(int i) {
        try {
            return getService().isStreamMute(i);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in isStreamMute", e);
            return false;
        }
    }

    public void setMasterMute(boolean z) {
        setMasterMute(z, 1);
    }

    public void setMasterMute(boolean z, int i) {
        try {
            getService().setMasterMute(z, i, this.mICallBack);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setMasterMute", e);
        }
    }

    public boolean isMasterMute() {
        try {
            return getService().isMasterMute();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in isMasterMute", e);
            return false;
        }
    }

    public void forceVolumeControlStream(int i) {
        try {
            getService().forceVolumeControlStream(i, this.mICallBack);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in forceVolumeControlStream", e);
        }
    }

    public boolean shouldVibrate(int i) {
        try {
            return getService().shouldVibrate(i);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in shouldVibrate", e);
            return false;
        }
    }

    public int getVibrateSetting(int i) {
        try {
            return getService().getVibrateSetting(i);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getVibrateSetting", e);
            return 0;
        }
    }

    public void setVibrateSetting(int i, int i2) {
        try {
            getService().setVibrateSetting(i, i2);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setVibrateSetting", e);
        }
    }

    public void setSpeakerphoneOn(boolean z) {
        try {
            getService().setSpeakerphoneOn(z);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setSpeakerphoneOn", e);
        }
    }

    public boolean isSpeakerphoneOn() {
        try {
            return getService().isSpeakerphoneOn();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in isSpeakerphoneOn", e);
            return false;
        }
    }

    public boolean isBluetoothScoAvailableOffCall() {
        return this.mContext.getResources().getBoolean(17891381);
    }

    public void startBluetoothSco() {
        try {
            getService().startBluetoothSco(this.mICallBack, this.mContext.getApplicationInfo().targetSdkVersion);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in startBluetoothSco", e);
        }
    }

    public void stopBluetoothSco() {
        try {
            getService().stopBluetoothSco(this.mICallBack);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in stopBluetoothSco", e);
        }
    }

    public void setBluetoothScoOn(boolean z) {
        try {
            getService().setBluetoothScoOn(z);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setBluetoothScoOn", e);
        }
    }

    public boolean isBluetoothScoOn() {
        try {
            return getService().isBluetoothScoOn();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in isBluetoothScoOn", e);
            return false;
        }
    }

    public boolean isBluetoothA2dpOn() {
        return AudioSystem.getDeviceConnectionState(128, "") != 0;
    }

    public boolean isWiredHeadsetOn() {
        return (AudioSystem.getDeviceConnectionState(4, "") == 0 && AudioSystem.getDeviceConnectionState(8, "") == 0) ? false : true;
    }

    public void setMicrophoneMute(boolean z) {
        AudioSystem.muteMicrophone(z);
    }

    public boolean isMicrophoneMute() {
        return AudioSystem.isMicrophoneMuted();
    }

    public void setMode(int i) {
        try {
            getService().setMode(i, this.mICallBack);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setMode", e);
        }
    }

    public int getMode() {
        try {
            return getService().getMode();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in getMode", e);
            return -2;
        }
    }

    public boolean isMusicActive() {
        return AudioSystem.isStreamActive(3, 0);
    }

    public boolean isMusicActiveRemotely() {
        return AudioSystem.isStreamActiveRemotely(3, 0);
    }

    public boolean isLocalOrRemoteMusicActive() {
        try {
            return getService().isLocalOrRemoteMusicActive();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in isLocalOrRemoteMusicActive()", e);
            return false;
        }
    }

    public boolean isAudioFocusExclusive() {
        try {
            return getService().getCurrentAudioFocus() == 4;
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in isAudioFocusExclusive()", e);
            return false;
        }
    }

    public void adjustLocalOrRemoteStreamVolume(int i, int i2) {
        if (i != 3) {
            Log.w(TAG, "adjustLocalOrRemoteStreamVolume() doesn't support stream " + i);
        }
        try {
            getService().adjustLocalOrRemoteStreamVolume(i, i2, this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in adjustLocalOrRemoteStreamVolume", e);
        }
    }

    @Deprecated
    public void setParameter(String str, String str2) {
        setParameters(str + "=" + str2);
    }

    public void setParameters(String str) {
        AudioSystem.setParameters(str);
    }

    public String getParameters(String str) {
        return AudioSystem.getParameters(str);
    }

    public void playSoundEffect(int i) {
        if (i < 0 || i >= 10 || !querySoundEffectsEnabled()) {
            return;
        }
        try {
            getService().playSoundEffect(i);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in playSoundEffect" + e);
        }
    }

    public void playSoundEffect(int i, float f) {
        if (i < 0 || i >= 10) {
            return;
        }
        try {
            getService().playSoundEffectVolume(i, f);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in playSoundEffect" + e);
        }
    }

    private boolean querySoundEffectsEnabled() {
        return Settings.System.getInt(this.mContext.getContentResolver(), Settings.System.SOUND_EFFECTS_ENABLED, 0) != 0;
    }

    public void loadSoundEffects() {
        try {
            getService().loadSoundEffects();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in loadSoundEffects" + e);
        }
    }

    public void unloadSoundEffects() {
        try {
            getService().unloadSoundEffects();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in unloadSoundEffects" + e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public OnAudioFocusChangeListener findFocusListener(String str) {
        return this.mAudioFocusIdListenerMap.get(str);
    }

    private class FocusEventHandlerDelegate {
        private final Handler mHandler;

        FocusEventHandlerDelegate() {
            Looper looperMyLooper = Looper.myLooper();
            looperMyLooper = looperMyLooper == null ? Looper.getMainLooper() : looperMyLooper;
            if (looperMyLooper != null) {
                this.mHandler = new Handler(looperMyLooper) { // from class: android.media.AudioManager.FocusEventHandlerDelegate.1
                    @Override // android.os.Handler
                    public void handleMessage(Message message) {
                        OnAudioFocusChangeListener onAudioFocusChangeListenerFindFocusListener;
                        synchronized (AudioManager.this.mFocusListenerLock) {
                            onAudioFocusChangeListenerFindFocusListener = AudioManager.this.findFocusListener((String) message.obj);
                        }
                        if (onAudioFocusChangeListenerFindFocusListener != null) {
                            onAudioFocusChangeListenerFindFocusListener.onAudioFocusChange(message.what);
                        }
                    }
                };
            } else {
                this.mHandler = null;
            }
        }

        Handler getHandler() {
            return this.mHandler;
        }
    }

    private String getIdForAudioFocusListener(OnAudioFocusChangeListener onAudioFocusChangeListener) {
        if (onAudioFocusChangeListener == null) {
            return new String(toString());
        }
        return new String(toString() + onAudioFocusChangeListener.toString());
    }

    public void registerAudioFocusListener(OnAudioFocusChangeListener onAudioFocusChangeListener) {
        synchronized (this.mFocusListenerLock) {
            if (this.mAudioFocusIdListenerMap.containsKey(getIdForAudioFocusListener(onAudioFocusChangeListener))) {
                return;
            }
            this.mAudioFocusIdListenerMap.put(getIdForAudioFocusListener(onAudioFocusChangeListener), onAudioFocusChangeListener);
        }
    }

    public void unregisterAudioFocusListener(OnAudioFocusChangeListener onAudioFocusChangeListener) {
        synchronized (this.mFocusListenerLock) {
            this.mAudioFocusIdListenerMap.remove(getIdForAudioFocusListener(onAudioFocusChangeListener));
        }
    }

    public int requestAudioFocus(OnAudioFocusChangeListener onAudioFocusChangeListener, int i, int i2) {
        if (i2 < 1 || i2 > 4) {
            Log.e(TAG, "Invalid duration hint, audio focus request denied");
            return 0;
        }
        registerAudioFocusListener(onAudioFocusChangeListener);
        try {
            return getService().requestAudioFocus(i, i2, this.mICallBack, this.mAudioFocusDispatcher, getIdForAudioFocusListener(onAudioFocusChangeListener), this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            Log.e(TAG, "Can't call requestAudioFocus() on AudioService due to " + e);
            return 0;
        }
    }

    public void requestAudioFocusForCall(int i, int i2) {
        try {
            getService().requestAudioFocus(i, i2, this.mICallBack, null, "AudioFocus_For_Phone_Ring_And_Calls", this.mContext.getOpPackageName());
        } catch (RemoteException e) {
            Log.e(TAG, "Can't call requestAudioFocusForCall() on AudioService due to " + e);
        }
    }

    public void abandonAudioFocusForCall() {
        try {
            getService().abandonAudioFocus(null, "AudioFocus_For_Phone_Ring_And_Calls");
        } catch (RemoteException e) {
            Log.e(TAG, "Can't call abandonAudioFocusForCall() on AudioService due to " + e);
        }
    }

    public int abandonAudioFocus(OnAudioFocusChangeListener onAudioFocusChangeListener) {
        unregisterAudioFocusListener(onAudioFocusChangeListener);
        try {
            return getService().abandonAudioFocus(this.mAudioFocusDispatcher, getIdForAudioFocusListener(onAudioFocusChangeListener));
        } catch (RemoteException e) {
            Log.e(TAG, "Can't call abandonAudioFocus() on AudioService due to " + e);
            return 0;
        }
    }

    public void registerMediaButtonEventReceiver(ComponentName componentName) {
        if (componentName == null) {
            return;
        }
        if (!componentName.getPackageName().equals(this.mContext.getPackageName())) {
            Log.e(TAG, "registerMediaButtonEventReceiver() error: receiver and context package names don't match");
            return;
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.setComponent(componentName);
        registerMediaButtonIntent(PendingIntent.getBroadcast(this.mContext, 0, intent, 0), componentName);
    }

    public void registerMediaButtonEventReceiver(PendingIntent pendingIntent) {
        if (pendingIntent == null) {
            return;
        }
        registerMediaButtonIntent(pendingIntent, null);
    }

    public void registerMediaButtonIntent(PendingIntent pendingIntent, ComponentName componentName) {
        Binder binder;
        if (pendingIntent == null) {
            Log.e(TAG, "Cannot call registerMediaButtonIntent() with a null parameter");
            return;
        }
        IAudioService service = getService();
        if (componentName == null) {
            try {
                binder = this.mToken;
            } catch (RemoteException e) {
                Log.e(TAG, "Dead object in registerMediaButtonIntent" + e);
                return;
            }
        } else {
            binder = null;
        }
        service.registerMediaButtonIntent(pendingIntent, componentName, binder);
    }

    public void registerMediaButtonEventReceiverForCalls(ComponentName componentName) {
        if (componentName == null) {
            return;
        }
        try {
            getService().registerMediaButtonEventReceiverForCalls(componentName);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in registerMediaButtonEventReceiverForCalls", e);
        }
    }

    public void unregisterMediaButtonEventReceiverForCalls() {
        try {
            getService().unregisterMediaButtonEventReceiverForCalls();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in unregisterMediaButtonEventReceiverForCalls", e);
        }
    }

    public void unregisterMediaButtonEventReceiver(ComponentName componentName) {
        if (componentName == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.setComponent(componentName);
        unregisterMediaButtonIntent(PendingIntent.getBroadcast(this.mContext, 0, intent, 0));
    }

    public void unregisterMediaButtonEventReceiver(PendingIntent pendingIntent) {
        if (pendingIntent == null) {
            return;
        }
        unregisterMediaButtonIntent(pendingIntent);
    }

    public void unregisterMediaButtonIntent(PendingIntent pendingIntent) {
        try {
            getService().unregisterMediaButtonIntent(pendingIntent);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in unregisterMediaButtonIntent" + e);
        }
    }

    public void registerRemoteControlClient(RemoteControlClient remoteControlClient) {
        if (remoteControlClient == null || remoteControlClient.getRcMediaIntent() == null) {
            return;
        }
        try {
            remoteControlClient.setRcseId(getService().registerRemoteControlClient(remoteControlClient.getRcMediaIntent(), remoteControlClient.getIRemoteControlClient(), this.mContext.getPackageName()));
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in registerRemoteControlClient" + e);
        }
    }

    public void unregisterRemoteControlClient(RemoteControlClient remoteControlClient) {
        if (remoteControlClient == null || remoteControlClient.getRcMediaIntent() == null) {
            return;
        }
        try {
            getService().unregisterRemoteControlClient(remoteControlClient.getRcMediaIntent(), remoteControlClient.getIRemoteControlClient());
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in unregisterRemoteControlClient" + e);
        }
    }

    public boolean registerRemoteController(RemoteController remoteController) {
        if (remoteController == null) {
            return false;
        }
        IAudioService service = getService();
        ComponentName componentName = new ComponentName(this.mContext, remoteController.getUpdateListener().getClass());
        try {
            int[] artworkSize = remoteController.getArtworkSize();
            boolean zRegisterRemoteController = service.registerRemoteController(remoteController.getRcDisplay(), artworkSize[0], artworkSize[1], componentName);
            remoteController.setIsRegistered(zRegisterRemoteController);
            return zRegisterRemoteController;
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in registerRemoteController " + e);
            return false;
        }
    }

    public void unregisterRemoteController(RemoteController remoteController) {
        if (remoteController == null) {
            return;
        }
        try {
            getService().unregisterRemoteControlDisplay(remoteController.getRcDisplay());
            remoteController.setIsRegistered(false);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in unregisterRemoteControlDisplay " + e);
        }
    }

    public void registerRemoteControlDisplay(IRemoteControlDisplay iRemoteControlDisplay) {
        registerRemoteControlDisplay(iRemoteControlDisplay, -1, -1);
    }

    public void registerRemoteControlDisplay(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2) {
        if (iRemoteControlDisplay == null) {
            return;
        }
        try {
            getService().registerRemoteControlDisplay(iRemoteControlDisplay, i, i2);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in registerRemoteControlDisplay " + e);
        }
    }

    public void unregisterRemoteControlDisplay(IRemoteControlDisplay iRemoteControlDisplay) {
        if (iRemoteControlDisplay == null) {
            return;
        }
        try {
            getService().unregisterRemoteControlDisplay(iRemoteControlDisplay);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in unregisterRemoteControlDisplay " + e);
        }
    }

    public void remoteControlDisplayUsesBitmapSize(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2) {
        if (iRemoteControlDisplay == null) {
            return;
        }
        try {
            getService().remoteControlDisplayUsesBitmapSize(iRemoteControlDisplay, i, i2);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in remoteControlDisplayUsesBitmapSize " + e);
        }
    }

    public void remoteControlDisplayWantsPlaybackPositionSync(IRemoteControlDisplay iRemoteControlDisplay, boolean z) {
        if (iRemoteControlDisplay == null) {
            return;
        }
        try {
            getService().remoteControlDisplayWantsPlaybackPositionSync(iRemoteControlDisplay, z);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in remoteControlDisplayWantsPlaybackPositionSync " + e);
        }
    }

    public void setRemoteControlClientPlaybackPosition(int i, long j) {
        if (j < 0) {
            return;
        }
        try {
            getService().setRemoteControlClientPlaybackPosition(i, j);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setRccPlaybackPosition(" + i + ", " + j + ")", e);
        }
    }

    public void updateRemoteControlClientMetadata(int i, int i2, Rating rating) {
        try {
            getService().updateRemoteControlClientMetadata(i, i2, rating);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in updateRemoteControlClientMetadata(" + i + ", " + i2 + ", " + rating + ")", e);
        }
    }

    public void reloadAudioSettings() {
        try {
            getService().reloadAudioSettings();
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in reloadAudioSettings" + e);
        }
    }

    public void avrcpSupportsAbsoluteVolume(String str, boolean z) {
        try {
            getService().avrcpSupportsAbsoluteVolume(str, z);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in avrcpSupportsAbsoluteVolume", e);
        }
    }

    public boolean isSilentMode() {
        int ringerMode = getRingerMode();
        return ringerMode == 0 || ringerMode == 1;
    }

    public int getDevicesForStream(int i) {
        if (i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 8 || i == 11) {
            return AudioSystem.getDevicesForStream(i);
        }
        return 0;
    }

    public void setWiredDeviceConnectionState(int i, int i2, String str) {
        try {
            getService().setWiredDeviceConnectionState(i, i2, str);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setWiredDeviceConnectionState " + e);
        }
    }

    public int setBluetoothA2dpDeviceConnectionState(BluetoothDevice bluetoothDevice, int i, int i2) {
        try {
            try {
                return getService().setBluetoothA2dpDeviceConnectionState(bluetoothDevice, i, i2);
            } catch (RemoteException e) {
                Log.e(TAG, "Dead object in setBluetoothA2dpDeviceConnectionState " + e);
                return 0;
            }
        } catch (Throwable unused) {
            return 0;
        }
    }

    public IRingtonePlayer getRingtonePlayer() {
        try {
            return getService().getRingtonePlayer();
        } catch (RemoteException unused) {
            return null;
        }
    }

    public String getProperty(String str) {
        int primaryOutputFrameCount;
        if (PROPERTY_OUTPUT_SAMPLE_RATE.equals(str)) {
            int primaryOutputSamplingRate = AudioSystem.getPrimaryOutputSamplingRate();
            if (primaryOutputSamplingRate > 0) {
                return Integer.toString(primaryOutputSamplingRate);
            }
            return null;
        }
        if (!PROPERTY_OUTPUT_FRAMES_PER_BUFFER.equals(str) || (primaryOutputFrameCount = AudioSystem.getPrimaryOutputFrameCount()) <= 0) {
            return null;
        }
        return Integer.toString(primaryOutputFrameCount);
    }

    public int getOutputLatency(int i) {
        return AudioSystem.getOutputLatency(i);
    }

    private static void writeFileVal(String str, int i) {
        SystemProperties.set(str, String.valueOf(i));
    }

    private static int readFileVal(String str) {
        return SystemProperties.getInt(str, 0);
    }

    public static void setHdmiAvailable(boolean z) {
        writeFileVal(hdmiAvailable, !z ? 0 : 1);
    }

    public static boolean getHdmiAvailable() {
        return readFileVal(hdmiAvailable) == 1;
    }

    public static boolean getHdmiExpected() {
        return readFileVal(hdmiExpected) == 1;
    }

    public static void setHdmiExpected(boolean z) {
        writeFileVal(hdmiExpected, !z ? 0 : 1);
    }

    public ArrayList<String> getAudioDevices(String str) {
        if (!str.equals(AUDIO_INPUT_TYPE) && !str.equals(AUDIO_OUTPUT_TYPE)) {
            return null;
        }
        ArrayList<String> arrayList = new ArrayList<>();
        String parameters = getParameters(str);
        if (parameters == null) {
            return null;
        }
        Log.d(TAG, "type " + str + "  list " + parameters);
        String[] strArrSplit = parameters.split(",");
        for (String str2 : strArrSplit) {
            if (!"".equals(str2)) {
                arrayList.add(str2);
            }
        }
        return arrayList;
    }

    public ArrayList<String> getActiveAudioDevices(String str) {
        if (!str.equals(AUDIO_INPUT_ACTIVE) && !str.equals(AUDIO_OUTPUT_ACTIVE)) {
            return null;
        }
        ArrayList<String> arrayList = new ArrayList<>();
        String parameters = getParameters(str);
        if (parameters == null) {
            return null;
        }
        Log.d(TAG, "type " + str + "  list " + parameters);
        for (String str2 : parameters.split(",")) {
            if (!"".equals(str2)) {
                arrayList.add(str2);
            }
        }
        if (str.equals(AUDIO_OUTPUT_ACTIVE) && !arrayList.contains(AUDIO_NAME_HDMI) && getHdmiExpected()) {
            arrayList.add(AUDIO_NAME_HDMI);
        }
        return arrayList;
    }

    public void setAudioDeviceActive(ArrayList<String> arrayList, String str) {
        boolean z;
        if ((str.equals(AUDIO_INPUT_ACTIVE) || str.equals(AUDIO_OUTPUT_ACTIVE)) && arrayList != null) {
            String str2 = null;
            ArrayList<String> arrayList2 = (ArrayList) arrayList.clone();
            boolean z2 = Settings.System.getInt(this.mContext.getContentResolver(), Settings.System.ENABLE_PASS_THROUGH, 0) == 1;
            if (z2 && str.equals(AUDIO_OUTPUT_ACTIVE)) {
                Iterator it = arrayList2.iterator();
                while (true) {
                    if (!it.hasNext()) {
                        z = false;
                        break;
                    }
                    String str3 = (String) it.next();
                    if (str3.contains("USB")) {
                        Log.d(TAG, "USB Audio Output Device " + str3 + " is Connect!");
                        z = true;
                        str2 = str3;
                        break;
                    }
                }
                if (z) {
                    MediaPlayer.setRawDataMode(MediaPlayer.AUDIO_DATA_MODE_PCM);
                } else {
                    boolean z3 = new DisplayManager(this.mContext).getDisplayOutputType(1) == 4;
                    Log.d(TAG, "Hdmi status " + z3 + "," + arrayList2.contains(AUDIO_NAME_HDMI) + "," + arrayList2.size());
                    if (arrayList2.size() > 0) {
                        Log.d(TAG, "HDMI " + ((String) arrayList2.get(0)));
                    }
                    if (arrayList2.contains(AUDIO_NAME_HDMI) && z3) {
                        MediaPlayer.setRawDataMode(MediaPlayer.AUDIO_DATA_MODE_HDMI_RAW);
                        str2 = AUDIO_NAME_HDMI;
                    } else if (arrayList2.contains(AUDIO_NAME_SPDIF)) {
                        MediaPlayer.setRawDataMode(MediaPlayer.AUDIO_DATA_MODE_SPDIF_RAW);
                        str2 = AUDIO_NAME_SPDIF;
                    } else {
                        MediaPlayer.setRawDataMode(MediaPlayer.AUDIO_DATA_MODE_PCM);
                        str2 = AUDIO_NAME_CODEC;
                    }
                }
            } else {
                str.equals(AUDIO_OUTPUT_ACTIVE);
                for (String str4 : arrayList2) {
                    str2 = str2 == null ? str4 : str2 + "," + str4;
                }
            }
            if (!z2) {
                MediaPlayer.setRawDataMode(MediaPlayer.AUDIO_DATA_MODE_PCM);
            }
            setParameter(str, str2);
            if (str.equals(AUDIO_OUTPUT_ACTIVE)) {
                Settings.System.putString(this.mContext.getContentResolver(), Settings.System.AUDIO_OUTPUT_CHANNEL, str2);
                Log.d(TAG, "Update Settings.System.AUDIO_OUTPUT_CHANNEL, now save audio is " + str2);
            }
        }
    }
}
