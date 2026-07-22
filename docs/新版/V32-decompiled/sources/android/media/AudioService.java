package android.media;

import android.Manifest;
import android.app.ActivityManager;
import android.app.ActivityManagerNative;
import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothA2dp;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.ContentObserver;
import android.hardware.usb.UsbManager;
import android.media.AudioSystem;
import android.media.IAudioService;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.util.TimedRemoteCaller;
import android.view.KeyEvent;
import android.view.VolumePanel;
import android.view.WindowManager;
import java.io.FileDescriptor;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;

/* JADX INFO: loaded from: classes.dex */
public class AudioService extends IAudioService.Stub {
    private static final String ASSET_FILE_VERSION = "1.0";
    private static final String ATTR_ASSET_FILE = "file";
    private static final String ATTR_ASSET_ID = "id";
    private static final String ATTR_GROUP_NAME = "name";
    private static final String ATTR_VERSION = "version";
    private static final int BTA2DP_DOCK_TIMEOUT_MILLIS = 8000;
    private static final int BT_HEADSET_CNCT_TIMEOUT_MS = 3000;
    protected static final boolean DEBUG_RC = false;
    protected static final boolean DEBUG_VOL = false;
    private static final int DEFAULT_STREAM_TYPE_OVERRIDE_DELAY_MS = 5000;
    private static final String GROUP_TOUCH_SOUNDS = "touch_sounds";
    private static final int MAX_BATCH_VOLUME_ADJUST_STEPS = 4;
    private static final int MAX_MASTER_VOLUME = 100;
    private static final int MSG_BROADCAST_AUDIO_BECOMING_NOISY = 15;
    private static final int MSG_BROADCAST_BT_CONNECTION_STATE = 19;
    private static final int MSG_BTA2DP_DOCK_TIMEOUT = 6;
    private static final int MSG_BT_HEADSET_CNCT_FAILED = 9;
    private static final int MSG_CHECK_MUSIC_ACTIVE = 14;
    private static final int MSG_CONFIGURE_SAFE_MEDIA_VOLUME = 16;
    private static final int MSG_CONFIGURE_SAFE_MEDIA_VOLUME_FORCED = 17;
    private static final int MSG_LOAD_SOUND_EFFECTS = 7;
    private static final int MSG_MEDIA_SERVER_DIED = 4;
    private static final int MSG_PERSIST_MASTER_VOLUME = 2;
    private static final int MSG_PERSIST_MASTER_VOLUME_MUTE = 11;
    private static final int MSG_PERSIST_RINGER_MODE = 3;
    private static final int MSG_PERSIST_SAFE_VOLUME_STATE = 18;
    private static final int MSG_PERSIST_VOLUME = 1;
    private static final int MSG_PLAY_SOUND_EFFECT = 5;
    private static final int MSG_REPORT_NEW_ROUTES = 12;
    private static final int MSG_SET_A2DP_SINK_CONNECTION_STATE = 102;
    private static final int MSG_SET_A2DP_SRC_CONNECTION_STATE = 101;
    private static final int MSG_SET_ALL_VOLUMES = 10;
    private static final int MSG_SET_DEVICE_VOLUME = 0;
    private static final int MSG_SET_FORCE_BT_A2DP_USE = 13;
    private static final int MSG_SET_FORCE_USE = 8;
    private static final int MSG_SET_WIRED_DEVICE_CONNECTION_STATE = 100;
    private static final int MSG_UNLOAD_SOUND_EFFECTS = 20;
    private static final int MUSIC_ACTIVE_POLL_PERIOD_MS = 60000;
    private static final int NUM_SOUNDPOOL_CHANNELS = 4;
    private static final int PERSIST_DELAY = 500;
    private static final int SAFE_VOLUME_CONFIGURE_TIMEOUT_MS = 30000;
    private static final int SCO_MODE_RAW = 1;
    private static final int SCO_MODE_VIRTUAL_CALL = 0;
    private static final int SCO_STATE_ACTIVATE_REQ = 1;
    private static final int SCO_STATE_ACTIVE_EXTERNAL = 2;
    private static final int SCO_STATE_ACTIVE_INTERNAL = 3;
    private static final int SCO_STATE_DEACTIVATE_EXT_REQ = 4;
    private static final int SCO_STATE_DEACTIVATE_REQ = 5;
    private static final int SCO_STATE_INACTIVE = 0;
    private static final int SENDMSG_NOOP = 1;
    private static final int SENDMSG_QUEUE = 2;
    private static final int SENDMSG_REPLACE = 0;
    private static final int SOUND_EFECTS_LOAD_TIMEOUT_MS = 5000;
    private static final String SOUND_EFFECTS_PATH = "/media/audio/ui/";
    public static final int STREAM_REMOTE_MUSIC = -200;
    private static final String TAG = "AudioService";
    private static final String TAG_ASSET = "asset";
    private static final String TAG_AUDIO_ASSETS = "audio_assets";
    private static final String TAG_GROUP = "group";
    private static final int UNSAFE_VOLUME_MUSIC_ACTIVE_MS_MAX = 72000000;
    private static int sSoundEffectVolumeDb;
    private final int SAFE_MEDIA_VOLUME_ACTIVE;
    private final int SAFE_MEDIA_VOLUME_DISABLED;
    private final int SAFE_MEDIA_VOLUME_INACTIVE;
    private final int SAFE_MEDIA_VOLUME_NOT_CONFIGURED;
    private BluetoothA2dp mA2dp;
    private final Object mA2dpAvrcpLock;
    private final AppOpsManager mAppOps;
    private PowerManager.WakeLock mAudioEventWakeLock;
    private AudioHandler mAudioHandler;
    private final AudioSystem.ErrorCallback mAudioSystemCallback;
    private AudioSystemThread mAudioSystemThread;
    private boolean mAvrcpAbsVolSupported;
    int mBecomingNoisyIntentDevices;
    private boolean mBluetoothA2dpEnabled;
    private final Object mBluetoothA2dpEnabledLock;
    private BluetoothHeadset mBluetoothHeadset;
    private BluetoothDevice mBluetoothHeadsetDevice;
    private BluetoothProfile.ServiceListener mBluetoothProfileServiceListener;
    private boolean mBootCompleted;
    private Boolean mCameraSoundForced;
    private final HashMap<Integer, String> mConnectedDevices;
    private final ContentResolver mContentResolver;
    private final Context mContext;
    final AudioRoutesInfo mCurAudioRoutes;
    private int mDeviceOrientation;
    private int mDeviceRotation;
    private String mDockAddress;
    private boolean mDockAudioMediaEnabled;
    private int mDockState;
    final int mFixedVolumeDevices;
    private ForceControlStreamClient mForceControlStreamClient;
    private final Object mForceControlStreamLock;
    private int mForcedUseForComm;
    private final boolean mHasVibrator;
    private boolean mInFmMode;
    private KeyguardManager mKeyguardManager;
    private final int[] mMasterVolumeRamp;
    private int mMcc;
    private final MediaFocusControl mMediaFocusControl;
    private final boolean mMonitorOrientation;
    private final boolean mMonitorRotation;
    private int mMusicActiveMs;
    private int mMuteAffectedStreams;
    private StreamVolumeCommand mPendingVolumeCommand;
    private int mPrevVolDirection;
    private final BroadcastReceiver mReceiver;
    private int mRingerMode;
    private int mRingerModeAffectedStreams;
    private int mRingerModeMutedStreams;
    private volatile IRingtonePlayer mRingtonePlayer;
    final RemoteCallbackList<IAudioRoutesObserver> mRoutesObservers;
    private final int mSafeMediaVolumeDevices;
    private int mSafeMediaVolumeIndex;
    private Integer mSafeMediaVolumeState;
    private int mScoAudioMode;
    private int mScoAudioState;
    private final ArrayList<ScoClient> mScoClients;
    private int mScoConnectionState;
    private final ArrayList<SetModeDeathHandler> mSetModeDeathHandlers;
    private SettingsObserver mSettingsObserver;
    private SoundPool mSoundPool;
    private SoundPoolCallback mSoundPoolCallBack;
    private SoundPoolListenerThread mSoundPoolListenerThread;
    private Looper mSoundPoolLooper;
    private VolumeStreamState[] mStreamStates;
    private int[] mStreamVolumeAlias;
    private final boolean mUseFixedVolume;
    private final boolean mUseMasterVolume;
    private int mVibrateSetting;
    private final boolean mVoiceCapable;
    private int mVolumeControlStream;
    private VolumePanel mVolumePanel;
    private static final List<String> SOUND_EFFECT_FILES = new ArrayList();
    private static final int[] MAX_STREAM_VOLUME = {5, 7, 7, 15, 7, 7, 15, 7, 15, 15, 15, 15};
    private static final int[] STEAM_VOLUME_OPS = {34, 36, 35, 36, 37, 38, 39, 36, 36, 36, 36, 36};
    private static final String[] RINGER_MODE_NAMES = {"SILENT", "VIBRATE", "NORMAL"};
    private int mMode = 0;
    private final Object mSettingsLock = new Object();
    private final Object mSoundEffectsLock = new Object();
    private final int[][] SOUND_EFFECT_FILES_MAP = (int[][]) Array.newInstance((Class<?>) int.class, 10, 2);
    private final int[] STREAM_VOLUME_ALIAS = {0, 2, 2, 3, 4, 2, 6, 2, 2, 3, 10, 3};
    private final int[] STREAM_VOLUME_ALIAS_NON_VOICE = {0, 3, 2, 3, 4, 2, 6, 3, 3, 3, 10, 3};
    private final String[] STREAM_NAMES = {"STREAM_VOICE_CALL", "STREAM_SYSTEM", "STREAM_RING", "STREAM_MUSIC", "STREAM_ALARM", "STREAM_NOTIFICATION", "STREAM_BLUETOOTH_SCO", "STREAM_SYSTEM_ENFORCED", "STREAM_DTMF", "STREAM_TTS", "STREAM_FM", "STREAM_EXTERNAL"};

    public static int getValueForVibrateSetting(int i, int i2, int i3) {
        int i4 = i2 * 2;
        return (i & (~(3 << i4))) | ((i3 & 3) << i4);
    }

    @Override // android.media.IAudioService
    public int getMasterMaxVolume() {
        return 100;
    }

    public AudioService(Context context) {
        AudioSystem.ErrorCallback errorCallback = new AudioSystem.ErrorCallback() { // from class: android.media.AudioService.1
            @Override // android.media.AudioSystem.ErrorCallback
            public void onError(int i) {
                if (i != 100) {
                    return;
                }
                AudioService.sendMsg(AudioService.this.mAudioHandler, 4, 1, 0, 0, null, 0);
            }
        };
        this.mAudioSystemCallback = errorCallback;
        this.mRingerModeAffectedStreams = 0;
        AudioServiceBroadcastReceiver audioServiceBroadcastReceiver = new AudioServiceBroadcastReceiver();
        this.mReceiver = audioServiceBroadcastReceiver;
        this.mConnectedDevices = new HashMap<>();
        this.mSetModeDeathHandlers = new ArrayList<>();
        this.mScoClients = new ArrayList<>();
        this.mSoundPoolLooper = null;
        this.mPrevVolDirection = 0;
        this.mVolumeControlStream = -1;
        this.mForceControlStreamLock = new Object();
        this.mForceControlStreamClient = null;
        this.mDeviceOrientation = 0;
        this.mDeviceRotation = 0;
        this.mBluetoothA2dpEnabledLock = new Object();
        this.mCurAudioRoutes = new AudioRoutesInfo();
        this.mRoutesObservers = new RemoteCallbackList<>();
        this.mFixedVolumeDevices = 30720;
        this.mDockAudioMediaEnabled = true;
        this.mDockState = 0;
        this.mA2dpAvrcpLock = new Object();
        this.mAvrcpAbsVolSupported = false;
        this.mInFmMode = false;
        this.mBluetoothProfileServiceListener = new BluetoothProfile.ServiceListener() { // from class: android.media.AudioService.2
            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceConnected(int i, BluetoothProfile bluetoothProfile) {
                boolean zConnectAudio = false;
                zConnectAudio = false;
                if (i != 1) {
                    if (i != 2) {
                        if (i != 10) {
                            return;
                        }
                        List<BluetoothDevice> connectedDevices = bluetoothProfile.getConnectedDevices();
                        if (connectedDevices.size() > 0) {
                            BluetoothDevice bluetoothDevice = connectedDevices.get(0);
                            synchronized (AudioService.this.mConnectedDevices) {
                                int connectionState = bluetoothProfile.getConnectionState(bluetoothDevice);
                                AudioService audioService = AudioService.this;
                                audioService.queueMsgUnderWakeLock(audioService.mAudioHandler, 101, connectionState, 0, bluetoothDevice, 0);
                            }
                            return;
                        }
                        return;
                    }
                    synchronized (AudioService.this.mA2dpAvrcpLock) {
                        AudioService.this.mA2dp = (BluetoothA2dp) bluetoothProfile;
                        List<BluetoothDevice> connectedDevices2 = AudioService.this.mA2dp.getConnectedDevices();
                        if (connectedDevices2.size() > 0) {
                            BluetoothDevice bluetoothDevice2 = connectedDevices2.get(0);
                            synchronized (AudioService.this.mConnectedDevices) {
                                int connectionState2 = AudioService.this.mA2dp.getConnectionState(bluetoothDevice2);
                                int iCheckSendBecomingNoisyIntent = AudioService.this.checkSendBecomingNoisyIntent(128, connectionState2 == 2 ? 1 : 0);
                                AudioService audioService2 = AudioService.this;
                                audioService2.queueMsgUnderWakeLock(audioService2.mAudioHandler, 102, connectionState2, 0, bluetoothDevice2, iCheckSendBecomingNoisyIntent);
                            }
                        }
                    }
                    return;
                }
                synchronized (AudioService.this.mScoClients) {
                    AudioService.this.mAudioHandler.removeMessages(9);
                    AudioService.this.mBluetoothHeadset = (BluetoothHeadset) bluetoothProfile;
                    List<BluetoothDevice> connectedDevices3 = AudioService.this.mBluetoothHeadset.getConnectedDevices();
                    if (connectedDevices3.size() > 0) {
                        AudioService.this.mBluetoothHeadsetDevice = connectedDevices3.get(0);
                    } else {
                        AudioService.this.mBluetoothHeadsetDevice = null;
                    }
                    AudioService.this.checkScoAudioState();
                    if (AudioService.this.mScoAudioState == 1 || AudioService.this.mScoAudioState == 5 || AudioService.this.mScoAudioState == 4) {
                        if (AudioService.this.mBluetoothHeadsetDevice != null) {
                            int i2 = AudioService.this.mScoAudioState;
                            if (i2 == 1) {
                                AudioService.this.mScoAudioState = 3;
                                zConnectAudio = AudioService.this.mScoAudioMode == 1 ? AudioService.this.mBluetoothHeadset.connectAudio() : AudioService.this.mBluetoothHeadset.startScoUsingVirtualVoiceCall(AudioService.this.mBluetoothHeadsetDevice);
                            } else if (i2 == 4) {
                                zConnectAudio = AudioService.this.mBluetoothHeadset.stopVoiceRecognition(AudioService.this.mBluetoothHeadsetDevice);
                            } else if (i2 == 5) {
                                zConnectAudio = AudioService.this.mScoAudioMode == 1 ? AudioService.this.mBluetoothHeadset.disconnectAudio() : AudioService.this.mBluetoothHeadset.stopScoUsingVirtualVoiceCall(AudioService.this.mBluetoothHeadsetDevice);
                            }
                        }
                        if (!zConnectAudio) {
                            AudioService.sendMsg(AudioService.this.mAudioHandler, 9, 0, 0, 0, null, 0);
                        }
                    }
                }
            }

            @Override // android.bluetooth.BluetoothProfile.ServiceListener
            public void onServiceDisconnected(int i) {
                if (i == 1) {
                    synchronized (AudioService.this.mScoClients) {
                        AudioService.this.mBluetoothHeadset = null;
                    }
                } else {
                    if (i != 2) {
                        return;
                    }
                    synchronized (AudioService.this.mA2dpAvrcpLock) {
                        AudioService.this.mA2dp = null;
                        synchronized (AudioService.this.mConnectedDevices) {
                            if (AudioService.this.mConnectedDevices.containsKey(128)) {
                                AudioService audioService = AudioService.this;
                                audioService.makeA2dpDeviceUnavailableNow((String) audioService.mConnectedDevices.get(128));
                            }
                        }
                    }
                }
            }
        };
        this.mBecomingNoisyIntentDevices = 32652;
        this.SAFE_MEDIA_VOLUME_NOT_CONFIGURED = 0;
        this.SAFE_MEDIA_VOLUME_DISABLED = 1;
        this.SAFE_MEDIA_VOLUME_INACTIVE = 2;
        this.SAFE_MEDIA_VOLUME_ACTIVE = 3;
        this.mMcc = 0;
        this.mSafeMediaVolumeDevices = 12;
        this.mContext = context;
        ContentResolver contentResolver = context.getContentResolver();
        this.mContentResolver = contentResolver;
        this.mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        this.mVoiceCapable = context.getResources().getBoolean(17891385);
        this.mAudioEventWakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(1, "handleAudioEvent");
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        this.mHasVibrator = vibrator == null ? false : vibrator.hasVibrator();
        int[] iArr = MAX_STREAM_VOLUME;
        iArr[0] = SystemProperties.getInt("ro.config.vc_call_vol_steps", iArr[0]);
        sSoundEffectVolumeDb = context.getResources().getInteger(17694724);
        this.mVolumePanel = new VolumePanel(context, this);
        this.mForcedUseForComm = 0;
        createAudioSystemThread();
        this.mMediaFocusControl = new MediaFocusControl(this.mAudioHandler.getLooper(), context, this.mVolumePanel, this);
        AudioSystem.setErrorCallback(errorCallback);
        boolean z = context.getResources().getBoolean(17891411);
        this.mCameraSoundForced = new Boolean(z);
        sendMsg(this.mAudioHandler, 8, 2, 4, z ? 11 : 0, null, 0);
        this.mSafeMediaVolumeState = new Integer(Settings.Global.getInt(contentResolver, Settings.Global.AUDIO_SAFE_VOLUME_STATE, 0));
        this.mSafeMediaVolumeIndex = context.getResources().getInteger(17694788) * 10;
        this.mUseFixedVolume = context.getResources().getBoolean(17891415);
        updateStreamVolumeAlias(false);
        readPersistedSettings();
        this.mSettingsObserver = new SettingsObserver();
        createStreamStates();
        readAndSetLowRamDevice();
        this.mRingerModeMutedStreams = 0;
        setRingerModeInt(getRingerMode(), false);
        IntentFilter intentFilter = new IntentFilter(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED);
        intentFilter.addAction(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED);
        intentFilter.addAction(Intent.ACTION_DOCK_EVENT);
        intentFilter.addAction(Intent.ACTION_USB_AUDIO_ACCESSORY_PLUG);
        intentFilter.addAction(Intent.ACTION_USB_AUDIO_DEVICE_PLUG);
        intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
        intentFilter.addAction(Intent.ACTION_UI_BOOT_FINISH);
        intentFilter.addAction(Intent.ACTION_SCREEN_ON);
        intentFilter.addAction(Intent.ACTION_SCREEN_OFF);
        intentFilter.addAction(Intent.ACTION_USER_SWITCHED);
        intentFilter.addAction(Intent.ACTION_CONFIGURATION_CHANGED);
        boolean z2 = SystemProperties.getBoolean("ro.audio.monitorOrientation", false);
        this.mMonitorOrientation = z2;
        if (z2) {
            Log.v(TAG, "monitoring device orientation");
            setOrientationForAudioSystem();
        }
        boolean z3 = SystemProperties.getBoolean("ro.audio.monitorRotation", false);
        this.mMonitorRotation = z3;
        if (z3) {
            this.mDeviceRotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
            Log.v(TAG, "monitoring device rotation, initial=" + this.mDeviceRotation);
            setRotationForAudioSystem();
        }
        context.registerReceiver(audioServiceBroadcastReceiver, intentFilter);
        this.mUseMasterVolume = context.getResources().getBoolean(17891345);
        restoreMasterVolume();
        this.mMasterVolumeRamp = context.getResources().getIntArray(17235979);
    }

    private void createAudioSystemThread() {
        AudioSystemThread audioSystemThread = new AudioSystemThread();
        this.mAudioSystemThread = audioSystemThread;
        audioSystemThread.start();
        waitForAudioHandlerCreation();
    }

    private void waitForAudioHandlerCreation() {
        synchronized (this) {
            while (this.mAudioHandler == null) {
                try {
                    wait();
                } catch (InterruptedException unused) {
                    Log.e(TAG, "Interrupted while waiting on volume handler.");
                }
            }
        }
    }

    private void checkAllAliasStreamVolumes() {
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        for (int i = 0; i < numStreamTypes; i++) {
            int[] iArr = this.mStreamVolumeAlias;
            if (i != iArr[i]) {
                VolumeStreamState[] volumeStreamStateArr = this.mStreamStates;
                volumeStreamStateArr[i].setAllIndexes(volumeStreamStateArr[iArr[i]]);
            }
            if (!this.mStreamStates[i].isMuted()) {
                this.mStreamStates[i].applyAllVolumes();
            }
        }
    }

    private void createStreamStates() {
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        VolumeStreamState[] volumeStreamStateArr = new VolumeStreamState[numStreamTypes];
        this.mStreamStates = volumeStreamStateArr;
        for (int i = 0; i < numStreamTypes; i++) {
            volumeStreamStateArr[i] = new VolumeStreamState(Settings.System.VOLUME_SETTINGS[this.mStreamVolumeAlias[i]], i);
        }
        checkAllAliasStreamVolumes();
    }

    private void dumpStreamStates(PrintWriter printWriter) {
        printWriter.println("\nStream volumes (device: index)");
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        for (int i = 0; i < numStreamTypes; i++) {
            printWriter.println("- " + this.STREAM_NAMES[i] + ":");
            this.mStreamStates[i].dump(printWriter);
            printWriter.println("");
        }
        printWriter.print("\n- mute affected streams = 0x");
        printWriter.println(Integer.toHexString(this.mMuteAffectedStreams));
    }

    private void updateStreamVolumeAlias(boolean z) {
        int i;
        if (this.mVoiceCapable) {
            this.mStreamVolumeAlias = this.STREAM_VOLUME_ALIAS;
            i = 2;
        } else {
            this.mStreamVolumeAlias = this.STREAM_VOLUME_ALIAS_NON_VOICE;
            i = 3;
        }
        if (isInCommunication()) {
            this.mRingerModeAffectedStreams &= -257;
            i = 0;
        } else {
            this.mRingerModeAffectedStreams |= 256;
        }
        this.mStreamVolumeAlias[8] = i;
        if (z) {
            VolumeStreamState[] volumeStreamStateArr = this.mStreamStates;
            volumeStreamStateArr[8].setAllIndexes(volumeStreamStateArr[i]);
            setRingerModeInt(getRingerMode(), false);
            sendMsg(this.mAudioHandler, 10, 2, 0, 0, this.mStreamStates[8], 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void readDockAudioSettings(ContentResolver contentResolver) {
        boolean z = Settings.Global.getInt(contentResolver, Settings.Global.DOCK_AUDIO_MEDIA_ENABLED, 0) == 1;
        this.mDockAudioMediaEnabled = z;
        if (z) {
            this.mBecomingNoisyIntentDevices |= 2048;
        } else {
            this.mBecomingNoisyIntentDevices &= -2049;
        }
        sendMsg(this.mAudioHandler, 8, 2, 3, z ? 8 : 0, null, 0);
    }

    private void readPersistedSettings() {
        ContentResolver contentResolver = this.mContentResolver;
        int i = 2;
        int i2 = Settings.Global.getInt(contentResolver, "mode_ringer", 2);
        int i3 = !AudioManager.isValidRingerMode(i2) ? 2 : i2;
        if (i3 == 1 && !this.mHasVibrator) {
            i3 = 0;
        }
        if (i3 != i2) {
            Settings.Global.putInt(contentResolver, "mode_ringer", i3);
        }
        if (this.mUseFixedVolume) {
            i3 = 2;
        }
        synchronized (this.mSettingsLock) {
            this.mRingerMode = i3;
            int valueForVibrateSetting = getValueForVibrateSetting(0, 1, this.mHasVibrator ? 2 : 0);
            this.mVibrateSetting = valueForVibrateSetting;
            if (!this.mHasVibrator) {
                i = 0;
            }
            this.mVibrateSetting = getValueForVibrateSetting(valueForVibrateSetting, 0, i);
            updateRingerModeAffectedStreams();
            readDockAudioSettings(contentResolver);
        }
        this.mMuteAffectedStreams = Settings.System.getIntForUser(contentResolver, Settings.System.MUTE_STREAMS_AFFECTED, 14, -2);
        boolean z = Settings.System.getIntForUser(contentResolver, Settings.System.VOLUME_MASTER_MUTE, 0, -2) == 1;
        if (this.mUseFixedVolume) {
            AudioSystem.setMasterVolume(1.0f);
            z = false;
        }
        AudioSystem.setMasterMute(z);
        broadcastMasterMuteStatus(z);
        broadcastRingerMode(i3);
        broadcastVibrateSetting(0);
        broadcastVibrateSetting(1);
        this.mMediaFocusControl.restoreMediaButtonReceiver();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int rescaleIndex(int i, int i2, int i3) {
        return ((i * this.mStreamStates[i3].getMaxIndex()) + (this.mStreamStates[i2].getMaxIndex() / 2)) / this.mStreamStates[i2].getMaxIndex();
    }

    @Override // android.media.IAudioService
    public boolean isLocalOrRemoteMusicActive() {
        return AudioSystem.isStreamActive(3, 0) || this.mMediaFocusControl.checkUpdateRemoteStateIfActive(3) || AudioSystem.isStreamActiveRemotely(3, 0);
    }

    @Override // android.media.IAudioService
    public void adjustVolume(int i, int i2, String str) {
        adjustSuggestedStreamVolume(i, Integer.MIN_VALUE, i2, str);
    }

    @Override // android.media.IAudioService
    public void adjustLocalOrRemoteStreamVolume(int i, int i2, String str) {
        if (AudioSystem.isStreamActive(3, 0)) {
            adjustStreamVolume(3, i2, 0, str);
        } else if (this.mMediaFocusControl.checkUpdateRemoteStateIfActive(3)) {
            this.mMediaFocusControl.adjustRemoteVolume(3, i2, 0);
        }
    }

    @Override // android.media.IAudioService
    public void adjustSuggestedStreamVolume(int i, int i2, int i3, String str) {
        KeyguardManager keyguardManager;
        int activeStreamType = this.mVolumeControlStream;
        if (activeStreamType == -1) {
            activeStreamType = getActiveStreamType(i2);
        }
        if (activeStreamType != -200 && (i3 & 4) != 0 && (this.mStreamVolumeAlias[activeStreamType] != 2 || ((keyguardManager = this.mKeyguardManager) != null && keyguardManager.isKeyguardLocked()))) {
            i3 &= -5;
        }
        if (activeStreamType == -200) {
            this.mMediaFocusControl.adjustRemoteVolume(3, i, i3 & (-37));
        } else {
            adjustStreamVolume(activeStreamType, i, i3, str);
        }
    }

    @Override // android.media.IAudioService
    public void adjustStreamVolume(int i, int i2, int i3, String str) {
        int iRescaleIndex;
        boolean zCheckForRingerModeChange;
        if (this.mUseFixedVolume) {
            return;
        }
        ensureValidDirection(i2);
        ensureValidStreamType(i);
        int i4 = this.mStreamVolumeAlias[i];
        VolumeStreamState volumeStreamState = this.mStreamStates[i4];
        int deviceForStream = getDeviceForStream(i4);
        int index = volumeStreamState.getIndex(deviceForStream);
        int i5 = deviceForStream & AudioSystem.DEVICE_OUT_ALL_A2DP;
        if ((i5 != 0 || (i3 & 64) == 0) && this.mAppOps.noteOp(STEAM_VOLUME_OPS[i4], Binder.getCallingUid(), str) == 0) {
            synchronized (this.mSafeMediaVolumeState) {
                this.mPendingVolumeCommand = null;
            }
            int i6 = i3 & (-33);
            if (i4 == 3 && (deviceForStream & 30720) != 0) {
                i6 |= 32;
                if (this.mSafeMediaVolumeState.intValue() == 3 && (deviceForStream & 12) != 0) {
                    iRescaleIndex = this.mSafeMediaVolumeIndex;
                } else {
                    iRescaleIndex = volumeStreamState.getMaxIndex();
                }
                if (index != 0) {
                    index = iRescaleIndex;
                }
            } else {
                iRescaleIndex = rescaleIndex(10, i, i4);
            }
            if ((i6 & 2) != 0 || i4 == getMasterStreamType()) {
                if (getRingerMode() == 1) {
                    i6 &= -17;
                }
                zCheckForRingerModeChange = checkForRingerModeChange(index, i2, iRescaleIndex);
            } else {
                zCheckForRingerModeChange = true;
            }
            int index2 = this.mStreamStates[i].getIndex(deviceForStream);
            if (zCheckForRingerModeChange && i2 != 0) {
                if (i4 == 3 && i5 != 0 && (i6 & 64) == 0) {
                    synchronized (this.mA2dpAvrcpLock) {
                        BluetoothA2dp bluetoothA2dp = this.mA2dp;
                        if (bluetoothA2dp != null && this.mAvrcpAbsVolSupported) {
                            bluetoothA2dp.adjustAvrcpAbsoluteVolume(i2);
                        }
                    }
                }
                if (i2 == 1 && !checkSafeMediaVolume(i4, index + iRescaleIndex, deviceForStream)) {
                    Log.e(TAG, "adjustStreamVolume() safe volume index = " + index2);
                    this.mVolumePanel.postDisplaySafeVolumeWarning(i6);
                } else if (volumeStreamState.adjustIndex(i2 * iRescaleIndex, deviceForStream)) {
                    sendMsg(this.mAudioHandler, 0, 2, deviceForStream, 0, volumeStreamState, 0);
                }
            }
            sendVolumeUpdate(i, index2, this.mStreamStates[i].getIndex(deviceForStream), i6);
        }
    }

    @Override // android.media.IAudioService
    public void adjustMasterVolume(int i, int i2, String str) {
        if (this.mUseFixedVolume) {
            return;
        }
        ensureValidSteps(i);
        int iRound = Math.round(AudioSystem.getMasterVolume() * 100.0f);
        int iAbs = Math.abs(i);
        int i3 = i > 0 ? 1 : -1;
        for (int i4 = 0; i4 < iAbs; i4++) {
            iRound += findVolumeDelta(i3, iRound);
        }
        setMasterVolume(iRound, i2, str);
    }

    class StreamVolumeCommand {
        public final int mDevice;
        public final int mFlags;
        public final int mIndex;
        public final int mStreamType;

        StreamVolumeCommand(int i, int i2, int i3, int i4) {
            this.mStreamType = i;
            this.mIndex = i2;
            this.mFlags = i3;
            this.mDevice = i4;
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r6v1 */
    /* JADX WARN: Type inference failed for: r6v3, types: [boolean] */
    private void onSetStreamVolume(int i, int i2, int i3, int i4) {
        setStreamVolumeInt(this.mStreamVolumeAlias[i], i2, i4, false);
        if ((i3 & 2) != 0 || this.mStreamVolumeAlias[i] == getMasterStreamType()) {
            setRingerMode(i2 == 0 ? this.mHasVibrator : 2);
        }
    }

    @Override // android.media.IAudioService
    public void setStreamVolume(int i, int i2, int i3, String str) {
        int index;
        int iRescaleIndex;
        int i4;
        if (this.mUseFixedVolume) {
            return;
        }
        ensureValidStreamType(i);
        int i5 = this.mStreamVolumeAlias[i];
        VolumeStreamState volumeStreamState = this.mStreamStates[i5];
        int deviceForStream = getDeviceForStream(i);
        int i6 = deviceForStream & AudioSystem.DEVICE_OUT_ALL_A2DP;
        if ((i6 != 0 || (i3 & 64) == 0) && this.mAppOps.noteOp(STEAM_VOLUME_OPS[i5], Binder.getCallingUid(), str) == 0) {
            synchronized (this.mSafeMediaVolumeState) {
                this.mPendingVolumeCommand = null;
                index = volumeStreamState.getIndex(deviceForStream);
                iRescaleIndex = rescaleIndex(i2 * 10, i, i5);
                if (i5 == 3 && i6 != 0 && (i3 & 64) == 0) {
                    synchronized (this.mA2dpAvrcpLock) {
                        BluetoothA2dp bluetoothA2dp = this.mA2dp;
                        if (bluetoothA2dp != null && this.mAvrcpAbsVolSupported) {
                            bluetoothA2dp.setAvrcpAbsoluteVolume(iRescaleIndex);
                        }
                    }
                }
                i4 = i3 & (-33);
                if (i5 == 3 && (deviceForStream & 30720) != 0) {
                    i4 |= 32;
                    if (iRescaleIndex != 0) {
                        iRescaleIndex = (this.mSafeMediaVolumeState.intValue() != 3 || (deviceForStream & 12) == 0) ? volumeStreamState.getMaxIndex() : this.mSafeMediaVolumeIndex;
                    }
                }
                if (!checkSafeMediaVolume(i5, iRescaleIndex, deviceForStream)) {
                    this.mVolumePanel.postDisplaySafeVolumeWarning(i4);
                    this.mPendingVolumeCommand = new StreamVolumeCommand(i, iRescaleIndex, i4, deviceForStream);
                } else {
                    onSetStreamVolume(i, iRescaleIndex, i4, deviceForStream);
                    iRescaleIndex = this.mStreamStates[i].getIndex(deviceForStream);
                }
            }
            sendVolumeUpdate(i, index, iRescaleIndex, i4);
        }
    }

    @Override // android.media.IAudioService
    public void forceVolumeControlStream(int i, IBinder iBinder) {
        synchronized (this.mForceControlStreamLock) {
            this.mVolumeControlStream = i;
            if (i == -1) {
                ForceControlStreamClient forceControlStreamClient = this.mForceControlStreamClient;
                if (forceControlStreamClient != null) {
                    forceControlStreamClient.release();
                    this.mForceControlStreamClient = null;
                }
            } else {
                this.mForceControlStreamClient = new ForceControlStreamClient(iBinder);
            }
        }
    }

    private class ForceControlStreamClient implements IBinder.DeathRecipient {
        private IBinder mCb;

        ForceControlStreamClient(IBinder iBinder) {
            if (iBinder != null) {
                try {
                    iBinder.linkToDeath(this, 0);
                } catch (RemoteException unused) {
                    Log.w(AudioService.TAG, "ForceControlStreamClient() could not link to " + iBinder + " binder death");
                    iBinder = null;
                }
            }
            this.mCb = iBinder;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (AudioService.this.mForceControlStreamLock) {
                Log.w(AudioService.TAG, "SCO client died");
                if (AudioService.this.mForceControlStreamClient == this) {
                    AudioService.this.mForceControlStreamClient = null;
                    AudioService.this.mVolumeControlStream = -1;
                } else {
                    Log.w(AudioService.TAG, "unregistered control stream client died");
                }
            }
        }

        public void release() {
            IBinder iBinder = this.mCb;
            if (iBinder != null) {
                iBinder.unlinkToDeath(this, 0);
                this.mCb = null;
            }
        }
    }

    private int findVolumeDelta(int i, int i2) {
        int i3;
        if (i == 1) {
            if (i2 == 100) {
                return 0;
            }
            int[] iArr = this.mMasterVolumeRamp;
            int i4 = iArr[1];
            for (int length = iArr.length - 1; length > 1; length -= 2) {
                int[] iArr2 = this.mMasterVolumeRamp;
                if (i2 >= iArr2[length - 1]) {
                    i3 = iArr2[length];
                }
            }
            return i4;
        }
        if (i != -1 || i2 == 0) {
            return 0;
        }
        int[] iArr3 = this.mMasterVolumeRamp;
        int length2 = iArr3.length;
        i3 = -iArr3[length2 - 1];
        for (int i5 = 2; i5 < length2; i5 += 2) {
            int[] iArr4 = this.mMasterVolumeRamp;
            if (i2 <= iArr4[i5]) {
                return -iArr4[i5 - 1];
            }
        }
        return i3;
    }

    private void sendBroadcastToAll(Intent intent) {
        long jClearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mContext.sendBroadcastAsUser(intent, UserHandle.ALL);
        } finally {
            Binder.restoreCallingIdentity(jClearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendStickyBroadcastToAll(Intent intent) {
        long jClearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mContext.sendStickyBroadcastAsUser(intent, UserHandle.ALL);
        } finally {
            Binder.restoreCallingIdentity(jClearCallingIdentity);
        }
    }

    private void sendVolumeUpdate(int i, int i2, int i3, int i4) {
        if (!this.mVoiceCapable && i == 2) {
            i = 5;
        }
        this.mVolumePanel.postVolumeChanged(i, i4);
        if ((i4 & 32) == 0) {
            Intent intent = new Intent(AudioManager.VOLUME_CHANGED_ACTION);
            intent.putExtra(AudioManager.EXTRA_VOLUME_STREAM_TYPE, i);
            intent.putExtra(AudioManager.EXTRA_VOLUME_STREAM_VALUE, (i3 + 5) / 10);
            intent.putExtra(AudioManager.EXTRA_PREV_VOLUME_STREAM_VALUE, (i2 + 5) / 10);
            sendBroadcastToAll(intent);
        }
    }

    private void sendMasterVolumeUpdate(int i, int i2, int i3) {
        this.mVolumePanel.postMasterVolumeChanged(i);
        Intent intent = new Intent(AudioManager.MASTER_VOLUME_CHANGED_ACTION);
        intent.putExtra(AudioManager.EXTRA_PREV_MASTER_VOLUME_VALUE, i2);
        intent.putExtra(AudioManager.EXTRA_MASTER_VOLUME_VALUE, i3);
        sendBroadcastToAll(intent);
    }

    private void sendMasterMuteUpdate(boolean z, int i) {
        this.mVolumePanel.postMasterMuteChanged(i);
        broadcastMasterMuteStatus(z);
    }

    private void broadcastMasterMuteStatus(boolean z) {
        Intent intent = new Intent(AudioManager.MASTER_MUTE_CHANGED_ACTION);
        intent.putExtra(AudioManager.EXTRA_MASTER_VOLUME_MUTED, z);
        intent.addFlags(603979776);
        sendStickyBroadcastToAll(intent);
    }

    private void setStreamVolumeInt(int i, int i2, int i3, boolean z) {
        VolumeStreamState volumeStreamState = this.mStreamStates[i];
        if (volumeStreamState.setIndex(i2, i3) || z) {
            sendMsg(this.mAudioHandler, 0, 2, i3, 0, volumeStreamState, 0);
        }
    }

    @Override // android.media.IAudioService
    public void setStreamSolo(int i, boolean z, IBinder iBinder) {
        if (this.mUseFixedVolume) {
            return;
        }
        for (int i2 = 0; i2 < this.mStreamStates.length; i2++) {
            if (isStreamAffectedByMute(i2) && i2 != i) {
                this.mStreamStates[i2].mute(iBinder, z);
            }
        }
    }

    @Override // android.media.IAudioService
    public void setStreamMute(int i, boolean z, IBinder iBinder) {
        boolean z2 = (65536 & i) != 0;
        int i2 = i & (-65537);
        if (!this.mUseFixedVolume && isStreamAffectedByMute(i2)) {
            this.mStreamStates[i2].mute(iBinder, z);
            if (z2) {
                this.mVolumePanel.postVolumeChanged(i2, 1);
            }
        }
    }

    @Override // android.media.IAudioService
    public boolean isStreamMute(int i) {
        return this.mStreamStates[i].isMuted();
    }

    @Override // android.media.IAudioService
    public void setMasterMute(boolean z, int i, IBinder iBinder) {
        if (this.mUseFixedVolume || z == AudioSystem.getMasterMute()) {
            return;
        }
        AudioSystem.setMasterMute(z);
        sendMsg(this.mAudioHandler, 11, 0, z ? 1 : 0, 0, null, 500);
        sendMasterMuteUpdate(z, i);
    }

    @Override // android.media.IAudioService
    public boolean isMasterMute() {
        return AudioSystem.getMasterMute();
    }

    protected static int getMaxStreamVolume(int i) {
        return MAX_STREAM_VOLUME[i];
    }

    @Override // android.media.IAudioService
    public int getStreamVolume(int i) {
        ensureValidStreamType(i);
        int deviceForStream = getDeviceForStream(i);
        int index = this.mStreamStates[i].getIndex(deviceForStream);
        if (this.mStreamStates[i].isMuted()) {
            index = 0;
        }
        if (index != 0 && this.mStreamVolumeAlias[i] == 3 && (deviceForStream & 30720) != 0) {
            index = this.mStreamStates[i].getMaxIndex();
        }
        return (index + 5) / 10;
    }

    @Override // android.media.IAudioService
    public int getMasterVolume() {
        if (isMasterMute()) {
            return 0;
        }
        return getLastAudibleMasterVolume();
    }

    @Override // android.media.IAudioService
    public void setMasterVolume(int i, int i2, String str) {
        if (!this.mUseFixedVolume && this.mAppOps.noteOp(33, Binder.getCallingUid(), str) == 0) {
            if (i < 0) {
                i = 0;
            } else if (i > 100) {
                i = 100;
            }
            doSetMasterVolume(i / 100.0f, i2);
        }
    }

    private void doSetMasterVolume(float f, int i) {
        if (AudioSystem.getMasterMute()) {
            return;
        }
        int masterVolume = getMasterVolume();
        AudioSystem.setMasterVolume(f);
        int masterVolume2 = getMasterVolume();
        if (masterVolume2 != masterVolume) {
            sendMsg(this.mAudioHandler, 2, 0, Math.round(f * 1000.0f), 0, null, 500);
        }
        sendMasterVolumeUpdate(i, masterVolume, masterVolume2);
    }

    @Override // android.media.IAudioService
    public int getStreamMaxVolume(int i) {
        ensureValidStreamType(i);
        return (this.mStreamStates[i].getMaxIndex() + 5) / 10;
    }

    @Override // android.media.IAudioService
    public int getLastAudibleStreamVolume(int i) {
        ensureValidStreamType(i);
        return (this.mStreamStates[i].getIndex(getDeviceForStream(i)) + 5) / 10;
    }

    @Override // android.media.IAudioService
    public int getLastAudibleMasterVolume() {
        return Math.round(AudioSystem.getMasterVolume() * 100.0f);
    }

    @Override // android.media.IAudioService
    public int getMasterStreamType() {
        return this.mVoiceCapable ? 2 : 3;
    }

    @Override // android.media.IAudioService
    public int getRingerMode() {
        int i;
        synchronized (this.mSettingsLock) {
            i = this.mRingerMode;
        }
        return i;
    }

    private void ensureValidRingerMode(int i) {
        if (!AudioManager.isValidRingerMode(i)) {
            throw new IllegalArgumentException("Bad ringer mode " + i);
        }
    }

    @Override // android.media.IAudioService
    public void setRingerMode(int i) {
        if (this.mUseFixedVolume) {
            return;
        }
        if (i == 1 && !this.mHasVibrator) {
            i = 0;
        }
        if (i != getRingerMode()) {
            setRingerModeInt(i, true);
            broadcastRingerMode(i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setRingerModeInt(int i, boolean z) {
        synchronized (this.mSettingsLock) {
            this.mRingerMode = i;
        }
        for (int numStreamTypes = AudioSystem.getNumStreamTypes() - 1; numStreamTypes >= 0; numStreamTypes--) {
            if (isStreamMutedByRingerMode(numStreamTypes)) {
                if (!isStreamAffectedByRingerMode(numStreamTypes) || i == 2) {
                    if (this.mVoiceCapable && this.mStreamVolumeAlias[numStreamTypes] == 2) {
                        synchronized (this.mStreamStates[numStreamTypes]) {
                            for (Map.Entry entry : this.mStreamStates[numStreamTypes].mIndex.entrySet()) {
                                if (((Integer) entry.getValue()).intValue() == 0) {
                                    entry.setValue(10);
                                }
                            }
                        }
                    }
                    this.mStreamStates[numStreamTypes].mute(null, false);
                    this.mRingerModeMutedStreams &= ~(1 << numStreamTypes);
                }
            } else if (isStreamAffectedByRingerMode(numStreamTypes) && i != 2) {
                this.mStreamStates[numStreamTypes].mute(null, true);
                this.mRingerModeMutedStreams |= 1 << numStreamTypes;
            }
        }
        if (z) {
            sendMsg(this.mAudioHandler, 3, 0, 0, 0, null, 500);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void restoreMasterVolume() {
        if (this.mUseFixedVolume) {
            AudioSystem.setMasterVolume(1.0f);
        } else if (this.mUseMasterVolume) {
            float floatForUser = Settings.System.getFloatForUser(this.mContentResolver, Settings.System.VOLUME_MASTER, -1.0f, -2);
            if (floatForUser >= 0.0f) {
                AudioSystem.setMasterVolume(floatForUser);
            }
        }
    }

    @Override // android.media.IAudioService
    public boolean shouldVibrate(int i) {
        if (!this.mHasVibrator) {
            return false;
        }
        int vibrateSetting = getVibrateSetting(i);
        return vibrateSetting != 1 ? vibrateSetting == 2 && getRingerMode() == 1 : getRingerMode() != 0;
    }

    @Override // android.media.IAudioService
    public int getVibrateSetting(int i) {
        if (this.mHasVibrator) {
            return (this.mVibrateSetting >> (i * 2)) & 3;
        }
        return 0;
    }

    @Override // android.media.IAudioService
    public void setVibrateSetting(int i, int i2) {
        if (this.mHasVibrator) {
            this.mVibrateSetting = getValueForVibrateSetting(this.mVibrateSetting, i, i2);
            broadcastVibrateSetting(i);
        }
    }

    private class SetModeDeathHandler implements IBinder.DeathRecipient {
        private IBinder mCb;
        private int mMode = 0;
        private int mPid;

        SetModeDeathHandler(IBinder iBinder, int i) {
            this.mCb = iBinder;
            this.mPid = i;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            int i;
            synchronized (AudioService.this.mSetModeDeathHandlers) {
                Log.w(AudioService.TAG, "setMode() client died");
                i = 0;
                if (AudioService.this.mSetModeDeathHandlers.indexOf(this) < 0) {
                    Log.w(AudioService.TAG, "unregistered setMode() client died");
                } else {
                    int modeInt = AudioService.this.setModeInt(0, this.mCb, this.mPid);
                    AudioService.this.setStandbyLock(false);
                    AudioService.this.mInFmMode = false;
                    i = modeInt;
                }
            }
            if (i != 0) {
                long jClearCallingIdentity = Binder.clearCallingIdentity();
                AudioService.this.disconnectBluetoothSco(i);
                Binder.restoreCallingIdentity(jClearCallingIdentity);
            }
        }

        public int getPid() {
            return this.mPid;
        }

        public void setMode(int i) {
            this.mMode = i;
        }

        public int getMode() {
            return this.mMode;
        }

        public IBinder getBinder() {
            return this.mCb;
        }
    }

    @Override // android.media.IAudioService
    public void setMode(int i, IBinder iBinder) {
        int modeInt;
        if (checkAudioSettingsPermission("setMode()") && i >= -1 && i < 7) {
            Log.i(TAG, "setMode(mode=" + i + ")");
            Log.i(TAG, "        mMode=" + this.mMode);
            synchronized (this.mSetModeDeathHandlers) {
                if (i == -1) {
                    i = this.mMode;
                    modeInt = setModeInt(i, iBinder, Binder.getCallingPid());
                } else {
                    modeInt = setModeInt(i, iBinder, Binder.getCallingPid());
                }
            }
            if (modeInt != 0) {
                disconnectBluetoothSco(modeInt);
            }
            boolean z = this.mInFmMode;
            if (!z && i == 5) {
                setStandbyLock(true);
                this.mInFmMode = true;
            } else {
                if (!z || i == 5) {
                    return;
                }
                setStandbyLock(false);
                this.mInFmMode = false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setStandbyLock(boolean z) {
        Log.i(TAG, "setStandbyState(" + z + ")");
        try {
            FileWriter fileWriter = new FileWriter("/sys/power/scene_lock");
            FileWriter fileWriter2 = new FileWriter("/sys/power/scene_unlock");
            if (z) {
                fileWriter.write("talking_standby");
            } else {
                fileWriter2.write("talking_standby");
            }
            fileWriter.close();
            fileWriter2.close();
        } catch (IOException e) {
            Log.i(TAG, "write scene lock error: " + e.getMessage());
        }
    }

    int setModeInt(int i, IBinder iBinder, int i2) {
        int phoneState;
        int pid = 0;
        if (iBinder == null) {
            Log.e(TAG, "setModeInt() called with null binder");
            return 0;
        }
        SetModeDeathHandler setModeDeathHandler = null;
        Iterator<SetModeDeathHandler> it = this.mSetModeDeathHandlers.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            SetModeDeathHandler next = it.next();
            if (next.getPid() == i2) {
                it.remove();
                next.getBinder().unlinkToDeath(next, 0);
                setModeDeathHandler = next;
                break;
            }
        }
        do {
            if (i == 0) {
                if (!this.mSetModeDeathHandlers.isEmpty()) {
                    SetModeDeathHandler setModeDeathHandler2 = this.mSetModeDeathHandlers.get(0);
                    iBinder = setModeDeathHandler2.getBinder();
                    setModeDeathHandler = setModeDeathHandler2;
                    i = setModeDeathHandler2.getMode();
                }
            } else {
                if (setModeDeathHandler == null) {
                    setModeDeathHandler = new SetModeDeathHandler(iBinder, i2);
                }
                try {
                    iBinder.linkToDeath(setModeDeathHandler, 0);
                } catch (RemoteException unused) {
                    Log.w(TAG, "setMode() could not link to " + iBinder + " binder death");
                }
                this.mSetModeDeathHandlers.add(0, setModeDeathHandler);
                setModeDeathHandler.setMode(i);
            }
            if (i != this.mMode) {
                phoneState = AudioSystem.setPhoneState(i);
                if (phoneState == 0) {
                    this.mMode = i;
                } else {
                    if (setModeDeathHandler != null) {
                        this.mSetModeDeathHandlers.remove(setModeDeathHandler);
                        iBinder.unlinkToDeath(setModeDeathHandler, 0);
                    }
                    i = 0;
                }
            } else {
                phoneState = 0;
            }
            if (phoneState == 0) {
                break;
            }
        } while (!this.mSetModeDeathHandlers.isEmpty());
        if (phoneState == 0) {
            if (i != 0) {
                if (this.mSetModeDeathHandlers.isEmpty()) {
                    Log.e(TAG, "setMode() different from MODE_NORMAL with empty mode client stack");
                } else {
                    pid = this.mSetModeDeathHandlers.get(0).getPid();
                }
            }
            int activeStreamType = getActiveStreamType(Integer.MIN_VALUE);
            if (activeStreamType == -200) {
                activeStreamType = 3;
            }
            int deviceForStream = getDeviceForStream(activeStreamType);
            setStreamVolumeInt(this.mStreamVolumeAlias[activeStreamType], this.mStreamStates[this.mStreamVolumeAlias[activeStreamType]].getIndex(deviceForStream), deviceForStream, true);
            updateStreamVolumeAlias(true);
        }
        return pid;
    }

    @Override // android.media.IAudioService
    public int getMode() {
        return this.mMode;
    }

    class LoadSoundEffectReply {
        public int mStatus = 1;

        LoadSoundEffectReply() {
        }
    }

    private void loadTouchSoundAssetDefaults() {
        SOUND_EFFECT_FILES.add("Effect_Tick.ogg");
        for (int i = 0; i < 10; i++) {
            int[][] iArr = this.SOUND_EFFECT_FILES_MAP;
            iArr[i][0] = 0;
            iArr[i][1] = -1;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Not initialized variable reg: 2, insn: 0x00df: MOVE (r1 I:??[OBJECT, ARRAY]) = (r2 I:??[OBJECT, ARRAY]), block:B:58:0x00df */
    /* JADX WARN: Removed duplicated region for block: B:60:0x00e2  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void loadTouchSoundAssets() throws java.lang.Throwable {
        /*
            Method dump skipped, instruction units count: 230
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.loadTouchSoundAssets():void");
    }

    @Override // android.media.IAudioService
    public void playSoundEffect(int i) {
        playSoundEffectVolume(i, -1.0f);
    }

    @Override // android.media.IAudioService
    public void playSoundEffectVolume(int i, float f) {
        sendMsg(this.mAudioHandler, 5, 2, i, (int) (f * 1000.0f), null, 0);
    }

    @Override // android.media.IAudioService
    public boolean loadSoundEffects() {
        LoadSoundEffectReply loadSoundEffectReply = new LoadSoundEffectReply();
        synchronized (loadSoundEffectReply) {
            sendMsg(this.mAudioHandler, 7, 2, 0, 0, loadSoundEffectReply, 0);
            int i = 3;
            while (loadSoundEffectReply.mStatus == 1) {
                int i2 = i - 1;
                if (i <= 0) {
                    break;
                }
                try {
                    loadSoundEffectReply.wait(TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
                } catch (InterruptedException unused) {
                    Log.w(TAG, "loadSoundEffects Interrupted while waiting sound pool loaded.");
                }
                i = i2;
            }
        }
        return loadSoundEffectReply.mStatus == 0;
    }

    @Override // android.media.IAudioService
    public void unloadSoundEffects() {
        sendMsg(this.mAudioHandler, 20, 2, 0, 0, null, 0);
    }

    class SoundPoolListenerThread extends Thread {
        public SoundPoolListenerThread() {
            super("SoundPoolListenerThread");
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Looper.prepare();
            AudioService.this.mSoundPoolLooper = Looper.myLooper();
            synchronized (AudioService.this.mSoundEffectsLock) {
                if (AudioService.this.mSoundPool != null) {
                    AudioService.this.mSoundPoolCallBack = new SoundPoolCallback();
                    AudioService.this.mSoundPool.setOnLoadCompleteListener(AudioService.this.mSoundPoolCallBack);
                }
                AudioService.this.mSoundEffectsLock.notify();
            }
            Looper.loop();
        }
    }

    private final class SoundPoolCallback implements SoundPool.OnLoadCompleteListener {
        List<Integer> mSamples;
        int mStatus;

        private SoundPoolCallback() {
            this.mStatus = 1;
            this.mSamples = new ArrayList();
        }

        public int status() {
            return this.mStatus;
        }

        public void setSamples(int[] iArr) {
            for (int i = 0; i < iArr.length; i++) {
                if (iArr[i] > 0) {
                    this.mSamples.add(Integer.valueOf(iArr[i]));
                }
            }
        }

        @Override // android.media.SoundPool.OnLoadCompleteListener
        public void onLoadComplete(SoundPool soundPool, int i, int i2) {
            synchronized (AudioService.this.mSoundEffectsLock) {
                int iIndexOf = this.mSamples.indexOf(Integer.valueOf(i));
                if (iIndexOf >= 0) {
                    this.mSamples.remove(iIndexOf);
                }
                if (i2 != 0 || this.mSamples.isEmpty()) {
                    this.mStatus = i2;
                    AudioService.this.mSoundEffectsLock.notify();
                }
            }
        }
    }

    @Override // android.media.IAudioService
    public void reloadAudioSettings() {
        readAudioSettings(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void readAudioSettings(boolean z) {
        readPersistedSettings();
        int numStreamTypes = AudioSystem.getNumStreamTypes();
        for (int i = 0; i < numStreamTypes; i++) {
            VolumeStreamState volumeStreamState = this.mStreamStates[i];
            if (!z || this.mStreamVolumeAlias[i] != 3) {
                synchronized (volumeStreamState) {
                    volumeStreamState.readSettings();
                    if (volumeStreamState.isMuted() && ((!isStreamAffectedByMute(i) && !isStreamMutedByRingerMode(i)) || this.mUseFixedVolume)) {
                        int size = volumeStreamState.mDeathHandlers.size();
                        for (int i2 = 0; i2 < size; i2++) {
                            ((VolumeStreamState.VolumeDeathHandler) volumeStreamState.mDeathHandlers.get(i2)).mMuteCount = 1;
                            ((VolumeStreamState.VolumeDeathHandler) volumeStreamState.mDeathHandlers.get(i2)).mute(false);
                        }
                    }
                }
            }
        }
        setRingerModeInt(getRingerMode(), false);
        checkAllAliasStreamVolumes();
        synchronized (this.mSafeMediaVolumeState) {
            if (this.mSafeMediaVolumeState.intValue() == 3) {
                enforceSafeMediaVolume();
            }
        }
    }

    @Override // android.media.IAudioService
    public void setSpeakerphoneOn(boolean z) {
        if (checkAudioSettingsPermission("setSpeakerphoneOn()")) {
            if (z) {
                if (this.mForcedUseForComm == 3) {
                    sendMsg(this.mAudioHandler, 8, 2, 2, 0, null, 0);
                }
                this.mForcedUseForComm = 1;
            } else if (this.mForcedUseForComm == 1) {
                this.mForcedUseForComm = 0;
            }
            sendMsg(this.mAudioHandler, 8, 2, 0, this.mForcedUseForComm, null, 0);
        }
    }

    @Override // android.media.IAudioService
    public boolean isSpeakerphoneOn() {
        return this.mForcedUseForComm == 1;
    }

    @Override // android.media.IAudioService
    public void setBluetoothScoOn(boolean z) {
        if (checkAudioSettingsPermission("setBluetoothScoOn()")) {
            if (z) {
                this.mForcedUseForComm = 3;
            } else if (this.mForcedUseForComm == 3) {
                this.mForcedUseForComm = 0;
            }
            sendMsg(this.mAudioHandler, 8, 2, 0, this.mForcedUseForComm, null, 0);
            sendMsg(this.mAudioHandler, 8, 2, 2, this.mForcedUseForComm, null, 0);
        }
    }

    @Override // android.media.IAudioService
    public boolean isBluetoothScoOn() {
        return this.mForcedUseForComm == 3;
    }

    @Override // android.media.IAudioService
    public void setBluetoothA2dpOn(boolean z) {
        synchronized (this.mBluetoothA2dpEnabledLock) {
            this.mBluetoothA2dpEnabled = z;
            sendMsg(this.mAudioHandler, 13, 2, 1, z ? 0 : 10, null, 0);
        }
    }

    @Override // android.media.IAudioService
    public boolean isBluetoothA2dpOn() {
        boolean z;
        synchronized (this.mBluetoothA2dpEnabledLock) {
            z = this.mBluetoothA2dpEnabled;
        }
        return z;
    }

    @Override // android.media.IAudioService
    public void startBluetoothSco(IBinder iBinder, int i) {
        if (checkAudioSettingsPermission("startBluetoothSco()") && this.mBootCompleted) {
            ScoClient scoClient = getScoClient(iBinder, true);
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            scoClient.incCount(i);
            Binder.restoreCallingIdentity(jClearCallingIdentity);
        }
    }

    @Override // android.media.IAudioService
    public void stopBluetoothSco(IBinder iBinder) {
        if (checkAudioSettingsPermission("stopBluetoothSco()") && this.mBootCompleted) {
            ScoClient scoClient = getScoClient(iBinder, false);
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            if (scoClient != null) {
                scoClient.decCount();
            }
            Binder.restoreCallingIdentity(jClearCallingIdentity);
        }
    }

    private class ScoClient implements IBinder.DeathRecipient {
        private IBinder mCb;
        private int mCreatorPid = Binder.getCallingPid();
        private int mStartcount = 0;

        ScoClient(IBinder iBinder) {
            this.mCb = iBinder;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (AudioService.this.mScoClients) {
                Log.w(AudioService.TAG, "SCO client died");
                if (AudioService.this.mScoClients.indexOf(this) < 0) {
                    Log.w(AudioService.TAG, "unregistered SCO client died");
                } else {
                    clearCount(true);
                    AudioService.this.mScoClients.remove(this);
                }
            }
        }

        public void incCount(int i) {
            synchronized (AudioService.this.mScoClients) {
                requestScoState(12, i);
                if (this.mStartcount == 0) {
                    try {
                        this.mCb.linkToDeath(this, 0);
                    } catch (RemoteException unused) {
                        Log.w(AudioService.TAG, "ScoClient  incCount() could not link to " + this.mCb + " binder death");
                    }
                    this.mStartcount++;
                } else {
                    this.mStartcount++;
                }
            }
        }

        public void decCount() {
            synchronized (AudioService.this.mScoClients) {
                int i = this.mStartcount;
                if (i == 0) {
                    Log.w(AudioService.TAG, "ScoClient.decCount() already 0");
                } else {
                    int i2 = i - 1;
                    this.mStartcount = i2;
                    if (i2 == 0) {
                        try {
                            this.mCb.unlinkToDeath(this, 0);
                        } catch (NoSuchElementException unused) {
                            Log.w(AudioService.TAG, "decCount() going to 0 but not registered to binder");
                        }
                    }
                    requestScoState(10, 0);
                }
            }
        }

        /* JADX WARN: Removed duplicated region for block: B:12:0x0036 A[Catch: all -> 0x003d, TryCatch #1 {, blocks: (B:4:0x0007, B:7:0x000c, B:10:0x0032, B:12:0x0036, B:13:0x003b, B:9:0x0012), top: B:20:0x0007, inners: #0 }] */
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public void clearCount(boolean r6) {
            /*
                r5 = this;
                android.media.AudioService r0 = android.media.AudioService.this
                java.util.ArrayList r0 = android.media.AudioService.access$2100(r0)
                monitor-enter(r0)
                int r1 = r5.mStartcount     // Catch: java.lang.Throwable -> L3d
                r2 = 0
                if (r1 == 0) goto L32
                android.os.IBinder r1 = r5.mCb     // Catch: java.util.NoSuchElementException -> L12 java.lang.Throwable -> L3d
                r1.unlinkToDeath(r5, r2)     // Catch: java.util.NoSuchElementException -> L12 java.lang.Throwable -> L3d
                goto L32
            L12:
                java.lang.String r1 = "AudioService"
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L3d
                r3.<init>()     // Catch: java.lang.Throwable -> L3d
                java.lang.String r4 = "clearCount() mStartcount: "
                java.lang.StringBuilder r3 = r3.append(r4)     // Catch: java.lang.Throwable -> L3d
                int r4 = r5.mStartcount     // Catch: java.lang.Throwable -> L3d
                java.lang.StringBuilder r3 = r3.append(r4)     // Catch: java.lang.Throwable -> L3d
                java.lang.String r4 = " != 0 but not registered to binder"
                java.lang.StringBuilder r3 = r3.append(r4)     // Catch: java.lang.Throwable -> L3d
                java.lang.String r3 = r3.toString()     // Catch: java.lang.Throwable -> L3d
                android.util.Log.w(r1, r3)     // Catch: java.lang.Throwable -> L3d
            L32:
                r5.mStartcount = r2     // Catch: java.lang.Throwable -> L3d
                if (r6 == 0) goto L3b
                r6 = 10
                r5.requestScoState(r6, r2)     // Catch: java.lang.Throwable -> L3d
            L3b:
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L3d
                return
            L3d:
                r6 = move-exception
                monitor-exit(r0)     // Catch: java.lang.Throwable -> L3d
                throw r6
            */
            throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.ScoClient.clearCount(boolean):void");
        }

        public int getCount() {
            return this.mStartcount;
        }

        public IBinder getBinder() {
            return this.mCb;
        }

        public int getPid() {
            return this.mCreatorPid;
        }

        public int totalCount() {
            int count;
            synchronized (AudioService.this.mScoClients) {
                int size = AudioService.this.mScoClients.size();
                count = 0;
                for (int i = 0; i < size; i++) {
                    count += ((ScoClient) AudioService.this.mScoClients.get(i)).getCount();
                }
            }
            return count;
        }

        private void requestScoState(int i, int i2) {
            AudioService.this.checkScoAudioState();
            if (totalCount() == 0) {
                if (i == 12) {
                    AudioService.this.broadcastScoConnectionState(2);
                    synchronized (AudioService.this.mSetModeDeathHandlers) {
                        if ((AudioService.this.mSetModeDeathHandlers.isEmpty() || ((SetModeDeathHandler) AudioService.this.mSetModeDeathHandlers.get(0)).getPid() == this.mCreatorPid) && (AudioService.this.mScoAudioState == 0 || AudioService.this.mScoAudioState == 5)) {
                            if (AudioService.this.mScoAudioState != 0) {
                                AudioService.this.mScoAudioState = 3;
                                AudioService.this.broadcastScoConnectionState(1);
                            } else {
                                AudioService.this.mScoAudioMode = i2 < 18 ? 0 : 1;
                                if (AudioService.this.mBluetoothHeadset == null || AudioService.this.mBluetoothHeadsetDevice == null) {
                                    if (AudioService.this.getBluetoothHeadset()) {
                                        AudioService.this.mScoAudioState = 1;
                                    }
                                } else {
                                    if (AudioService.this.mScoAudioMode == 1 ? AudioService.this.mBluetoothHeadset.connectAudio() : AudioService.this.mBluetoothHeadset.startScoUsingVirtualVoiceCall(AudioService.this.mBluetoothHeadsetDevice)) {
                                        AudioService.this.mScoAudioState = 3;
                                    } else {
                                        AudioService.this.broadcastScoConnectionState(0);
                                    }
                                }
                            }
                        } else {
                            AudioService.this.broadcastScoConnectionState(0);
                        }
                    }
                    return;
                }
                if (i == 10) {
                    if (AudioService.this.mScoAudioState == 3 || AudioService.this.mScoAudioState == 1) {
                        if (AudioService.this.mScoAudioState == 3) {
                            if (AudioService.this.mBluetoothHeadset == null || AudioService.this.mBluetoothHeadsetDevice == null) {
                                if (AudioService.this.getBluetoothHeadset()) {
                                    AudioService.this.mScoAudioState = 5;
                                    return;
                                }
                                return;
                            } else {
                                if (AudioService.this.mScoAudioMode == 1 ? AudioService.this.mBluetoothHeadset.disconnectAudio() : AudioService.this.mBluetoothHeadset.stopScoUsingVirtualVoiceCall(AudioService.this.mBluetoothHeadsetDevice)) {
                                    return;
                                }
                                AudioService.this.mScoAudioState = 0;
                                AudioService.this.broadcastScoConnectionState(0);
                                return;
                            }
                        }
                        AudioService.this.mScoAudioState = 0;
                        AudioService.this.broadcastScoConnectionState(0);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void checkScoAudioState() {
        BluetoothDevice bluetoothDevice;
        BluetoothHeadset bluetoothHeadset = this.mBluetoothHeadset;
        if (bluetoothHeadset == null || (bluetoothDevice = this.mBluetoothHeadsetDevice) == null || this.mScoAudioState != 0 || bluetoothHeadset.getAudioState(bluetoothDevice) == 10) {
            return;
        }
        this.mScoAudioState = 2;
    }

    private ScoClient getScoClient(IBinder iBinder, boolean z) {
        synchronized (this.mScoClients) {
            ScoClient scoClient = null;
            int size = this.mScoClients.size();
            for (int i = 0; i < size; i++) {
                scoClient = this.mScoClients.get(i);
                if (scoClient.getBinder() == iBinder) {
                    return scoClient;
                }
            }
            if (z) {
                scoClient = new ScoClient(iBinder);
                this.mScoClients.add(scoClient);
            }
            return scoClient;
        }
    }

    public void clearAllScoClients(int i, boolean z) {
        synchronized (this.mScoClients) {
            ScoClient scoClient = null;
            int size = this.mScoClients.size();
            for (int i2 = 0; i2 < size; i2++) {
                ScoClient scoClient2 = this.mScoClients.get(i2);
                if (scoClient2.getPid() != i) {
                    scoClient2.clearCount(z);
                } else {
                    scoClient = scoClient2;
                }
            }
            this.mScoClients.clear();
            if (scoClient != null) {
                this.mScoClients.add(scoClient);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean getBluetoothHeadset() {
        BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
        boolean profileProxy = defaultAdapter != null ? defaultAdapter.getProfileProxy(this.mContext, this.mBluetoothProfileServiceListener, 1) : false;
        sendMsg(this.mAudioHandler, 9, 0, 0, 0, null, profileProxy ? 3000 : 0);
        return profileProxy;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void disconnectBluetoothSco(int i) {
        synchronized (this.mScoClients) {
            checkScoAudioState();
            int i2 = this.mScoAudioState;
            if (i2 == 2 || i2 == 4) {
                BluetoothDevice bluetoothDevice = this.mBluetoothHeadsetDevice;
                if (bluetoothDevice != null) {
                    BluetoothHeadset bluetoothHeadset = this.mBluetoothHeadset;
                    if (bluetoothHeadset != null) {
                        if (!bluetoothHeadset.stopVoiceRecognition(bluetoothDevice)) {
                            sendMsg(this.mAudioHandler, 9, 0, 0, 0, null, 0);
                        }
                    } else if (i2 == 2 && getBluetoothHeadset()) {
                        this.mScoAudioState = 4;
                    }
                }
            } else {
                clearAllScoClients(i, true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void resetBluetoothSco() {
        synchronized (this.mScoClients) {
            clearAllScoClients(0, false);
            this.mScoAudioState = 0;
            broadcastScoConnectionState(0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void broadcastScoConnectionState(int i) {
        sendMsg(this.mAudioHandler, 19, 2, i, 0, null, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onBroadcastScoConnectionState(int i) {
        if (i != this.mScoConnectionState) {
            Intent intent = new Intent(AudioManager.ACTION_SCO_AUDIO_STATE_UPDATED);
            intent.putExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, i);
            intent.putExtra(AudioManager.EXTRA_SCO_AUDIO_PREVIOUS_STATE, this.mScoConnectionState);
            sendStickyBroadcastToAll(intent);
            this.mScoConnectionState = i;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onCheckMusicActive() {
        synchronized (this.mSafeMediaVolumeState) {
            if (this.mSafeMediaVolumeState.intValue() == 2) {
                int deviceForStream = getDeviceForStream(3);
                if ((deviceForStream & 12) != 0) {
                    sendMsg(this.mAudioHandler, 14, 0, 0, 0, null, MUSIC_ACTIVE_POLL_PERIOD_MS);
                    int index = this.mStreamStates[3].getIndex(deviceForStream);
                    if (AudioSystem.isStreamActive(3, 0) && index > this.mSafeMediaVolumeIndex) {
                        int i = this.mMusicActiveMs + MUSIC_ACTIVE_POLL_PERIOD_MS;
                        this.mMusicActiveMs = i;
                        if (i > UNSAFE_VOLUME_MUSIC_ACTIVE_MS_MAX) {
                            setSafeMediaVolumeEnabled(true);
                            this.mMusicActiveMs = 0;
                        }
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onConfigureSafeVolume(boolean z) {
        int i;
        synchronized (this.mSafeMediaVolumeState) {
            int i2 = this.mContext.getResources().getConfiguration().mcc;
            int i3 = this.mMcc;
            if (i3 != i2 || (i3 == 0 && z)) {
                this.mSafeMediaVolumeIndex = this.mContext.getResources().getInteger(17694788) * 10;
                if (this.mContext.getResources().getBoolean(17891409)) {
                    if (this.mSafeMediaVolumeState.intValue() != 2) {
                        this.mSafeMediaVolumeState = 3;
                        enforceSafeMediaVolume();
                    }
                    i = 3;
                } else {
                    this.mSafeMediaVolumeState = 1;
                    i = 1;
                }
                this.mMcc = i2;
                sendMsg(this.mAudioHandler, 18, 2, i, 0, null, 0);
            }
        }
    }

    private boolean checkForRingerModeChange(int i, int i2, int i3) {
        int ringerMode = getRingerMode();
        boolean z = false;
        if (ringerMode == 0) {
            if (i2 == 1) {
                ringerMode = this.mHasVibrator ? 1 : 2;
            }
            if (i2 != -1) {
                z = true;
            }
        } else if (ringerMode != 1) {
            if (ringerMode != 2) {
                Log.e(TAG, "checkForRingerModeChange() wrong ringer mode: " + ringerMode);
            } else if (i2 == -1) {
                if (this.mHasVibrator) {
                    if (i3 <= i && i < i3 * 2) {
                        ringerMode = 1;
                        z = true;
                    }
                } else if (i <= i3) {
                    ringerMode = 0;
                }
            }
            z = true;
        } else if (!this.mHasVibrator) {
            Log.e(TAG, "checkForRingerModeChange() current ringer mode is vibratebut no vibrator is present");
            z = true;
        } else if (i2 == -1) {
            if (this.mPrevVolDirection != -1) {
                ringerMode = 0;
            }
        } else if (i2 == 1) {
            ringerMode = 2;
        }
        setRingerMode(ringerMode);
        this.mPrevVolDirection = i2;
        return z;
    }

    public boolean isStreamAffectedByRingerMode(int i) {
        return ((1 << i) & this.mRingerModeAffectedStreams) != 0;
    }

    private boolean isStreamMutedByRingerMode(int i) {
        return ((1 << i) & this.mRingerModeMutedStreams) != 0;
    }

    boolean updateRingerModeAffectedStreams() {
        int i;
        int intForUser = Settings.System.getIntForUser(this.mContentResolver, Settings.System.MODE_RINGER_STREAMS_AFFECTED, 166, -2) | 38;
        int i2 = this.mVoiceCapable ? intForUser & (-9) : intForUser | 8;
        synchronized (this.mCameraSoundForced) {
            i = this.mCameraSoundForced.booleanValue() ? i2 & (-129) : i2 | 128;
        }
        int i3 = this.mStreamVolumeAlias[8] == 2 ? i | 256 : i & (-257);
        if (i3 == this.mRingerModeAffectedStreams) {
            return false;
        }
        Settings.System.putIntForUser(this.mContentResolver, Settings.System.MODE_RINGER_STREAMS_AFFECTED, i3, -2);
        this.mRingerModeAffectedStreams = i3;
        return true;
    }

    public boolean isStreamAffectedByMute(int i) {
        return ((1 << i) & this.mMuteAffectedStreams) != 0;
    }

    private void ensureValidDirection(int i) {
        if (i < -1 || i > 1) {
            throw new IllegalArgumentException("Bad direction " + i);
        }
    }

    private void ensureValidSteps(int i) {
        if (Math.abs(i) > 4) {
            throw new IllegalArgumentException("Bad volume adjust steps " + i);
        }
    }

    private void ensureValidStreamType(int i) {
        if (i < 0 || i >= this.mStreamStates.length) {
            throw new IllegalArgumentException("Bad stream type " + i);
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:10:0x001f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private boolean isInCommunication() {
        /*
            r4 = this;
            boolean r0 = r4.mVoiceCapable
            r1 = 0
            if (r0 == 0) goto L1f
            java.lang.String r0 = "phone"
            android.os.IBinder r0 = android.os.ServiceManager.checkService(r0)     // Catch: android.os.RemoteException -> L17
            com.android.internal.telephony.ITelephony r0 = com.android.internal.telephony.ITelephony.Stub.asInterface(r0)     // Catch: android.os.RemoteException -> L17
            if (r0 == 0) goto L1f
            boolean r0 = r0.isOffhook()     // Catch: android.os.RemoteException -> L17
            goto L20
        L17:
            r0 = move-exception
            java.lang.String r2 = "AudioService"
            java.lang.String r3 = "Couldn't connect to phone service"
            android.util.Log.w(r2, r3, r0)
        L1f:
            r0 = r1
        L20:
            if (r0 != 0) goto L29
            int r0 = r4.getMode()
            r2 = 3
            if (r0 != r2) goto L2a
        L29:
            r1 = 1
        L2a:
            return r1
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.isInCommunication():boolean");
    }

    private boolean isAfMusicActiveRecently(int i) {
        return AudioSystem.isStreamActive(3, i) || AudioSystem.isStreamActiveRemotely(3, i);
    }

    private int getActiveStreamType(int i) {
        if (this.mVoiceCapable) {
            if (isInCommunication()) {
                return AudioSystem.getForceUse(0) == 3 ? 6 : 0;
            }
            if (i == Integer.MIN_VALUE) {
                if (isAfMusicActiveRecently(5000)) {
                    return 3;
                }
                if (getMode() == 5) {
                    return 10;
                }
                return this.mMediaFocusControl.checkUpdateRemoteStateIfActive(3) ? -200 : 2;
            }
            if (isAfMusicActiveRecently(0)) {
                return 3;
            }
            return i;
        }
        if (isInCommunication()) {
            return AudioSystem.getForceUse(0) == 3 ? 6 : 0;
        }
        if (AudioSystem.isStreamActive(5, 5000) || AudioSystem.isStreamActive(2, 5000)) {
            return 5;
        }
        return i == Integer.MIN_VALUE ? (!isAfMusicActiveRecently(5000) && this.mMediaFocusControl.checkUpdateRemoteStateIfActive(3)) ? -200 : 3 : i;
    }

    private void broadcastRingerMode(int i) {
        Intent intent = new Intent(AudioManager.RINGER_MODE_CHANGED_ACTION);
        intent.putExtra(AudioManager.EXTRA_RINGER_MODE, i);
        intent.addFlags(603979776);
        sendStickyBroadcastToAll(intent);
    }

    private void broadcastVibrateSetting(int i) {
        if (ActivityManagerNative.isSystemReady()) {
            Intent intent = new Intent(AudioManager.VIBRATE_SETTING_CHANGED_ACTION);
            intent.putExtra(AudioManager.EXTRA_VIBRATE_TYPE, i);
            intent.putExtra(AudioManager.EXTRA_VIBRATE_SETTING, getVibrateSetting(i));
            sendBroadcastToAll(intent);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void queueMsgUnderWakeLock(Handler handler, int i, int i2, int i3, Object obj, int i4) {
        long jClearCallingIdentity = Binder.clearCallingIdentity();
        this.mAudioEventWakeLock.acquire();
        Binder.restoreCallingIdentity(jClearCallingIdentity);
        sendMsg(handler, i, 2, i2, i3, obj, i4);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void sendMsg(Handler handler, int i, int i2, int i3, int i4, Object obj, int i5) {
        if (i2 == 0) {
            handler.removeMessages(i);
        } else if (i2 == 1 && handler.hasMessages(i)) {
            return;
        }
        handler.sendMessageDelayed(handler.obtainMessage(i, i3, i4, obj), i5);
    }

    boolean checkAudioSettingsPermission(String str) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.MODIFY_AUDIO_SETTINGS) == 0) {
            return true;
        }
        Log.w(TAG, "Audio Settings Permission Denial: " + str + " from pid=" + Binder.getCallingPid() + ", uid=" + Binder.getCallingUid());
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int getDeviceForStream(int i) {
        int devicesForStream = AudioSystem.getDevicesForStream(i);
        if (((devicesForStream - 1) & devicesForStream) == 0) {
            return devicesForStream;
        }
        if ((devicesForStream & 2) != 0) {
            return 2;
        }
        return devicesForStream & AudioSystem.DEVICE_OUT_ALL_A2DP;
    }

    @Override // android.media.IAudioService
    public void setWiredDeviceConnectionState(int i, int i2, String str) {
        synchronized (this.mConnectedDevices) {
            queueMsgUnderWakeLock(this.mAudioHandler, 100, i, i2, str, checkSendBecomingNoisyIntent(i, i2));
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:19:0x0035  */
    /* JADX WARN: Removed duplicated region for block: B:20:0x0038  */
    @Override // android.media.IAudioService
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public int setBluetoothA2dpDeviceConnectionState(android.bluetooth.BluetoothDevice r11, int r12, int r13) {
        /*
            r10 = this;
            r0 = 2
            if (r13 == r0) goto L21
            r1 = 10
            if (r13 != r1) goto L8
            goto L21
        L8:
            java.lang.IllegalArgumentException r11 = new java.lang.IllegalArgumentException
            java.lang.StringBuilder r12 = new java.lang.StringBuilder
            r12.<init>()
            java.lang.String r0 = "invalid profile "
            java.lang.StringBuilder r12 = r12.append(r0)
            java.lang.StringBuilder r12 = r12.append(r13)
            java.lang.String r12 = r12.toString()
            r11.<init>(r12)
            throw r11
        L21:
            java.util.HashMap<java.lang.Integer, java.lang.String> r1 = r10.mConnectedDevices
            monitor-enter(r1)
            r2 = 0
            if (r13 != r0) goto L30
            r3 = 128(0x80, float:1.794E-43)
            if (r12 != r0) goto L2c
            r2 = 1
        L2c:
            int r2 = r10.checkSendBecomingNoisyIntent(r3, r2)     // Catch: java.lang.Throwable -> L45
        L30:
            r9 = r2
            android.media.AudioService$AudioHandler r3 = r10.mAudioHandler     // Catch: java.lang.Throwable -> L45
            if (r13 != r0) goto L38
            r13 = 102(0x66, float:1.43E-43)
            goto L3a
        L38:
            r13 = 101(0x65, float:1.42E-43)
        L3a:
            r4 = r13
            r6 = 0
            r2 = r10
            r5 = r12
            r7 = r11
            r8 = r9
            r2.queueMsgUnderWakeLock(r3, r4, r5, r6, r7, r8)     // Catch: java.lang.Throwable -> L45
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L45
            return r9
        L45:
            r11 = move-exception
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L45
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.AudioService.setBluetoothA2dpDeviceConnectionState(android.bluetooth.BluetoothDevice, int, int):int");
    }

    public class VolumeStreamState {
        private ArrayList<VolumeDeathHandler> mDeathHandlers;
        private final ConcurrentHashMap<Integer, Integer> mIndex;
        private int mIndexMax;
        private final int mStreamType;
        private String mVolumeIndexSettingName;

        private VolumeStreamState(String str, int i) {
            this.mIndex = new ConcurrentHashMap<>(8, 0.75f, 4);
            this.mVolumeIndexSettingName = str;
            this.mStreamType = i;
            int i2 = AudioService.MAX_STREAM_VOLUME[i];
            this.mIndexMax = i2;
            AudioSystem.initStreamVolume(i, 0, i2);
            this.mIndexMax *= 10;
            this.mDeathHandlers = new ArrayList<>();
            readSettings();
        }

        public String getSettingNameForDevice(int i) {
            String str = this.mVolumeIndexSettingName;
            String deviceName = AudioSystem.getDeviceName(i);
            return deviceName.isEmpty() ? str : str + "_" + deviceName;
        }

        public synchronized void readSettings() {
            if (AudioService.this.mUseFixedVolume) {
                this.mIndex.put(1073741824, Integer.valueOf(this.mIndexMax));
                return;
            }
            int i = this.mStreamType;
            if (i != 1 && i != 7) {
                int i2 = AudioSystem.DEVICE_OUT_ALL;
                int i3 = 0;
                while (i2 != 0) {
                    int i4 = 1 << i3;
                    if ((i4 & i2) != 0) {
                        i2 &= ~i4;
                        int intForUser = Settings.System.getIntForUser(AudioService.this.mContentResolver, getSettingNameForDevice(i4), i4 == 1073741824 ? AudioManager.DEFAULT_STREAM_VOLUME[this.mStreamType] : -1, -2);
                        if (intForUser != -1) {
                            if (AudioService.this.mStreamVolumeAlias[this.mStreamType] == 3 && (i4 & 30720) != 0) {
                                this.mIndex.put(Integer.valueOf(i4), Integer.valueOf(intForUser != 0 ? this.mIndexMax : 0));
                            } else {
                                this.mIndex.put(Integer.valueOf(i4), Integer.valueOf(getValidIndex(intForUser * 10)));
                            }
                        }
                    }
                    i3++;
                }
                return;
            }
            int i5 = AudioManager.DEFAULT_STREAM_VOLUME[this.mStreamType] * 10;
            synchronized (AudioService.this.mCameraSoundForced) {
                if (AudioService.this.mCameraSoundForced.booleanValue()) {
                    i5 = this.mIndexMax;
                }
            }
            this.mIndex.put(1073741824, Integer.valueOf(i5));
        }

        public void applyDeviceVolume(int i) {
            int index;
            if (isMuted()) {
                index = 0;
            } else if (AudioService.this.mStreamVolumeAlias[this.mStreamType] == 3 && (i & AudioSystem.DEVICE_OUT_ALL_A2DP) != 0 && AudioService.this.mAvrcpAbsVolSupported) {
                index = (this.mIndexMax + 5) / 10;
            } else {
                index = (getIndex(i) + 5) / 10;
            }
            AudioSystem.setStreamVolumeIndex(this.mStreamType, index, i);
        }

        public synchronized void applyAllVolumes() {
            AudioSystem.setStreamVolumeIndex(this.mStreamType, isMuted() ? 0 : (getIndex(1073741824) + 5) / 10, 1073741824);
            for (Map.Entry<Integer, Integer> entry : this.mIndex.entrySet()) {
                int iIntValue = entry.getKey().intValue();
                if (iIntValue != 1073741824) {
                    AudioSystem.setStreamVolumeIndex(this.mStreamType, isMuted() ? 0 : (entry.getValue().intValue() + 5) / 10, iIntValue);
                }
            }
        }

        public boolean adjustIndex(int i, int i2) {
            return setIndex(getIndex(i2) + i, i2);
        }

        public synchronized boolean setIndex(int i, int i2) {
            int index = getIndex(i2);
            int validIndex = getValidIndex(i);
            synchronized (AudioService.this.mCameraSoundForced) {
                if (this.mStreamType == 7 && AudioService.this.mCameraSoundForced.booleanValue()) {
                    validIndex = this.mIndexMax;
                }
            }
            this.mIndex.put(Integer.valueOf(i2), Integer.valueOf(validIndex));
            if (index == validIndex) {
                return false;
            }
            boolean z = i2 == AudioService.this.getDeviceForStream(this.mStreamType);
            for (int numStreamTypes = AudioSystem.getNumStreamTypes() - 1; numStreamTypes >= 0; numStreamTypes--) {
                if (numStreamTypes != this.mStreamType) {
                    int i3 = AudioService.this.mStreamVolumeAlias[numStreamTypes];
                    int i4 = this.mStreamType;
                    if (i3 == i4) {
                        int iRescaleIndex = AudioService.this.rescaleIndex(validIndex, i4, numStreamTypes);
                        AudioService.this.mStreamStates[numStreamTypes].setIndex(iRescaleIndex, i2);
                        if (z) {
                            AudioService.this.mStreamStates[numStreamTypes].setIndex(iRescaleIndex, AudioService.this.getDeviceForStream(numStreamTypes));
                        }
                    }
                }
            }
            return true;
        }

        public synchronized int getIndex(int i) {
            Integer num;
            num = this.mIndex.get(Integer.valueOf(i));
            if (num == null) {
                num = this.mIndex.get(1073741824);
            }
            return num.intValue();
        }

        public int getMaxIndex() {
            return this.mIndexMax;
        }

        public synchronized void setAllIndexes(VolumeStreamState volumeStreamState) {
            int streamType = volumeStreamState.getStreamType();
            int iRescaleIndex = AudioService.this.rescaleIndex(volumeStreamState.getIndex(1073741824), streamType, this.mStreamType);
            Iterator<Map.Entry<Integer, Integer>> it = this.mIndex.entrySet().iterator();
            while (it.hasNext()) {
                it.next().setValue(Integer.valueOf(iRescaleIndex));
            }
            for (Map.Entry<Integer, Integer> entry : volumeStreamState.mIndex.entrySet()) {
                setIndex(AudioService.this.rescaleIndex(entry.getValue().intValue(), streamType, this.mStreamType), entry.getKey().intValue());
            }
        }

        public synchronized void setAllIndexesToMax() {
            Iterator<Map.Entry<Integer, Integer>> it = this.mIndex.entrySet().iterator();
            while (it.hasNext()) {
                it.next().setValue(Integer.valueOf(this.mIndexMax));
            }
        }

        public synchronized void mute(IBinder iBinder, boolean z) {
            VolumeDeathHandler deathHandler = getDeathHandler(iBinder, z);
            if (deathHandler == null) {
                Log.e(AudioService.TAG, "Could not get client death handler for stream: " + this.mStreamType);
            } else {
                deathHandler.mute(z);
            }
        }

        public int getStreamType() {
            return this.mStreamType;
        }

        private int getValidIndex(int i) {
            if (i < 0) {
                return 0;
            }
            return (AudioService.this.mUseFixedVolume || i > this.mIndexMax) ? this.mIndexMax : i;
        }

        private class VolumeDeathHandler implements IBinder.DeathRecipient {
            private IBinder mICallback;
            private int mMuteCount;

            VolumeDeathHandler(IBinder iBinder) {
                this.mICallback = iBinder;
            }

            public void mute(boolean z) {
                boolean z2 = false;
                if (z) {
                    if (this.mMuteCount != 0) {
                        Log.w(AudioService.TAG, "stream: " + VolumeStreamState.this.mStreamType + " was already muted by this client");
                    } else {
                        try {
                            IBinder iBinder = this.mICallback;
                            if (iBinder != null) {
                                iBinder.linkToDeath(this, 0);
                            }
                            VolumeStreamState.this.mDeathHandlers.add(this);
                            z2 = !VolumeStreamState.this.isMuted();
                        } catch (RemoteException unused) {
                            binderDied();
                            return;
                        }
                    }
                    this.mMuteCount++;
                } else {
                    int i = this.mMuteCount;
                    if (i == 0) {
                        Log.e(AudioService.TAG, "unexpected unmute for stream: " + VolumeStreamState.this.mStreamType);
                    } else {
                        int i2 = i - 1;
                        this.mMuteCount = i2;
                        if (i2 == 0) {
                            VolumeStreamState.this.mDeathHandlers.remove(this);
                            IBinder iBinder2 = this.mICallback;
                            if (iBinder2 != null) {
                                iBinder2.unlinkToDeath(this, 0);
                            }
                            if (!VolumeStreamState.this.isMuted()) {
                                z2 = true;
                            }
                        }
                    }
                }
                if (z2) {
                    AudioService.sendMsg(AudioService.this.mAudioHandler, 10, 2, 0, 0, VolumeStreamState.this, 0);
                }
            }

            @Override // android.os.IBinder.DeathRecipient
            public void binderDied() {
                Log.w(AudioService.TAG, "Volume service client died for stream: " + VolumeStreamState.this.mStreamType);
                if (this.mMuteCount != 0) {
                    this.mMuteCount = 1;
                    mute(false);
                }
            }
        }

        private synchronized int muteCount() {
            int i;
            int size = this.mDeathHandlers.size();
            i = 0;
            for (int i2 = 0; i2 < size; i2++) {
                i += this.mDeathHandlers.get(i2).mMuteCount;
            }
            return i;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public synchronized boolean isMuted() {
            return muteCount() != 0;
        }

        private VolumeDeathHandler getDeathHandler(IBinder iBinder, boolean z) {
            int size = this.mDeathHandlers.size();
            for (int i = 0; i < size; i++) {
                VolumeDeathHandler volumeDeathHandler = this.mDeathHandlers.get(i);
                if (iBinder == volumeDeathHandler.mICallback) {
                    return volumeDeathHandler;
                }
            }
            if (z) {
                return new VolumeDeathHandler(iBinder);
            }
            Log.w(AudioService.TAG, "stream was not muted by this client");
            return null;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void dump(PrintWriter printWriter) {
            printWriter.print("   Mute count: ");
            printWriter.println(muteCount());
            printWriter.print("   Current: ");
            for (Map.Entry<Integer, Integer> entry : this.mIndex.entrySet()) {
                printWriter.print(Integer.toHexString(entry.getKey().intValue()) + ": " + ((entry.getValue().intValue() + 5) / 10) + ", ");
            }
        }
    }

    private class AudioSystemThread extends Thread {
        AudioSystemThread() {
            super(AudioService.TAG);
        }

        @Override // java.lang.Thread, java.lang.Runnable
        public void run() {
            Looper.prepare();
            synchronized (AudioService.this) {
                AudioService.this.mAudioHandler = new AudioHandler();
                AudioService.this.notify();
            }
            Looper.loop();
        }
    }

    private class AudioHandler extends Handler {
        private AudioHandler() {
        }

        private void setDeviceVolume(VolumeStreamState volumeStreamState, int i) {
            volumeStreamState.applyDeviceVolume(i);
            for (int numStreamTypes = AudioSystem.getNumStreamTypes() - 1; numStreamTypes >= 0; numStreamTypes--) {
                if (numStreamTypes != volumeStreamState.mStreamType && AudioService.this.mStreamVolumeAlias[numStreamTypes] == volumeStreamState.mStreamType) {
                    int deviceForStream = AudioService.this.getDeviceForStream(numStreamTypes);
                    if (i != deviceForStream && AudioService.this.mAvrcpAbsVolSupported && (i & AudioSystem.DEVICE_OUT_ALL_A2DP) != 0) {
                        AudioService.this.mStreamStates[numStreamTypes].applyDeviceVolume(i);
                    }
                    AudioService.this.mStreamStates[numStreamTypes].applyDeviceVolume(deviceForStream);
                }
            }
            AudioService.sendMsg(AudioService.this.mAudioHandler, 1, 2, i, 0, volumeStreamState, 500);
        }

        private void setAllVolumes(VolumeStreamState volumeStreamState) {
            volumeStreamState.applyAllVolumes();
            for (int numStreamTypes = AudioSystem.getNumStreamTypes() - 1; numStreamTypes >= 0; numStreamTypes--) {
                if (numStreamTypes != volumeStreamState.mStreamType && AudioService.this.mStreamVolumeAlias[numStreamTypes] == volumeStreamState.mStreamType) {
                    AudioService.this.mStreamStates[numStreamTypes].applyAllVolumes();
                }
            }
        }

        private void persistVolume(VolumeStreamState volumeStreamState, int i) {
            if (AudioService.this.mUseFixedVolume) {
                return;
            }
            Settings.System.putIntForUser(AudioService.this.mContentResolver, volumeStreamState.getSettingNameForDevice(i), (volumeStreamState.getIndex(i) + 5) / 10, -2);
        }

        private void persistRingerMode(int i) {
            if (AudioService.this.mUseFixedVolume) {
                return;
            }
            Settings.Global.putInt(AudioService.this.mContentResolver, "mode_ringer", i);
        }

        private boolean onLoadSoundEffects() {
            int iStatus;
            synchronized (AudioService.this.mSoundEffectsLock) {
                if (AudioService.this.mBootCompleted) {
                    if (AudioService.this.mSoundPool != null) {
                        return true;
                    }
                    AudioService.this.loadTouchSoundAssets();
                    AudioService.this.mSoundPool = new SoundPool(4, 1, 0);
                    AudioService.this.mSoundPoolCallBack = null;
                    AudioService.this.mSoundPoolListenerThread = AudioService.this.new SoundPoolListenerThread();
                    AudioService.this.mSoundPoolListenerThread.start();
                    int i = 3;
                    int i2 = 3;
                    while (AudioService.this.mSoundPoolCallBack == null) {
                        int i3 = i2 - 1;
                        if (i2 <= 0) {
                            break;
                        }
                        try {
                            AudioService.this.mSoundEffectsLock.wait(TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
                        } catch (InterruptedException unused) {
                            Log.w(AudioService.TAG, "Interrupted while waiting sound pool listener thread.");
                        }
                        i2 = i3;
                    }
                    if (AudioService.this.mSoundPoolCallBack != null) {
                        int[] iArr = new int[AudioService.SOUND_EFFECT_FILES.size()];
                        for (int i4 = 0; i4 < AudioService.SOUND_EFFECT_FILES.size(); i4++) {
                            iArr[i4] = -1;
                        }
                        int i5 = 0;
                        for (int i6 = 0; i6 < 10; i6++) {
                            if (AudioService.this.SOUND_EFFECT_FILES_MAP[i6][1] != 0) {
                                if (iArr[AudioService.this.SOUND_EFFECT_FILES_MAP[i6][0]] == -1) {
                                    String str = Environment.getRootDirectory() + AudioService.SOUND_EFFECTS_PATH + ((String) AudioService.SOUND_EFFECT_FILES.get(AudioService.this.SOUND_EFFECT_FILES_MAP[i6][0]));
                                    int iLoad = AudioService.this.mSoundPool.load(str, 0);
                                    if (iLoad > 0) {
                                        AudioService.this.SOUND_EFFECT_FILES_MAP[i6][1] = iLoad;
                                        iArr[AudioService.this.SOUND_EFFECT_FILES_MAP[i6][0]] = iLoad;
                                        i5++;
                                    } else {
                                        Log.w(AudioService.TAG, "Soundpool could not load file: " + str);
                                    }
                                } else {
                                    AudioService.this.SOUND_EFFECT_FILES_MAP[i6][1] = iArr[AudioService.this.SOUND_EFFECT_FILES_MAP[i6][0]];
                                }
                            }
                        }
                        if (i5 > 0) {
                            AudioService.this.mSoundPoolCallBack.setSamples(iArr);
                            iStatus = 1;
                            while (iStatus == 1) {
                                int i7 = i - 1;
                                if (i <= 0) {
                                    break;
                                }
                                try {
                                    AudioService.this.mSoundEffectsLock.wait(TimedRemoteCaller.DEFAULT_CALL_TIMEOUT_MILLIS);
                                    iStatus = AudioService.this.mSoundPoolCallBack.status();
                                } catch (InterruptedException unused2) {
                                    Log.w(AudioService.TAG, "Interrupted while waiting sound pool callback.");
                                }
                                i = i7;
                            }
                        } else {
                            iStatus = -1;
                        }
                        if (AudioService.this.mSoundPoolLooper != null) {
                            AudioService.this.mSoundPoolLooper.quit();
                            AudioService.this.mSoundPoolLooper = null;
                        }
                        AudioService.this.mSoundPoolListenerThread = null;
                        if (iStatus != 0) {
                            Log.w(AudioService.TAG, "onLoadSoundEffects(), Error " + iStatus + " while loading samples");
                            for (int i8 = 0; i8 < 10; i8++) {
                                if (AudioService.this.SOUND_EFFECT_FILES_MAP[i8][1] > 0) {
                                    AudioService.this.SOUND_EFFECT_FILES_MAP[i8][1] = -1;
                                }
                            }
                            AudioService.this.mSoundPool.release();
                            AudioService.this.mSoundPool = null;
                        }
                        return iStatus == 0;
                    }
                    Log.w(AudioService.TAG, "onLoadSoundEffects() SoundPool listener or thread creation error");
                    if (AudioService.this.mSoundPoolLooper != null) {
                        AudioService.this.mSoundPoolLooper.quit();
                        AudioService.this.mSoundPoolLooper = null;
                    }
                    AudioService.this.mSoundPoolListenerThread = null;
                    AudioService.this.mSoundPool.release();
                    AudioService.this.mSoundPool = null;
                    return false;
                }
                Log.w(AudioService.TAG, "onLoadSoundEffects() called before boot complete");
                return false;
            }
        }

        private void onUnloadSoundEffects() {
            synchronized (AudioService.this.mSoundEffectsLock) {
                if (AudioService.this.mSoundPool == null) {
                    return;
                }
                int[] iArr = new int[AudioService.SOUND_EFFECT_FILES.size()];
                for (int i = 0; i < AudioService.SOUND_EFFECT_FILES.size(); i++) {
                    iArr[i] = 0;
                }
                for (int i2 = 0; i2 < 10; i2++) {
                    if (AudioService.this.SOUND_EFFECT_FILES_MAP[i2][1] > 0 && iArr[AudioService.this.SOUND_EFFECT_FILES_MAP[i2][0]] == 0) {
                        AudioService.this.mSoundPool.unload(AudioService.this.SOUND_EFFECT_FILES_MAP[i2][1]);
                        AudioService.this.SOUND_EFFECT_FILES_MAP[i2][1] = -1;
                        iArr[AudioService.this.SOUND_EFFECT_FILES_MAP[i2][0]] = -1;
                    }
                }
                AudioService.this.mSoundPool.release();
                AudioService.this.mSoundPool = null;
            }
        }

        private void onPlaySoundEffect(int i, int i2) {
            synchronized (AudioService.this.mSoundEffectsLock) {
                onLoadSoundEffects();
                if (AudioService.this.mSoundPool == null) {
                    return;
                }
                float fPow = i2 < 0 ? (float) Math.pow(10.0d, AudioService.sSoundEffectVolumeDb / 20.0f) : i2 / 1000.0f;
                if (AudioService.this.SOUND_EFFECT_FILES_MAP[i][1] > 0) {
                    AudioService.this.mSoundPool.play(AudioService.this.SOUND_EFFECT_FILES_MAP[i][1], fPow, fPow, 0, 0, 1.0f);
                } else {
                    MediaPlayer mediaPlayer = new MediaPlayer();
                    try {
                        try {
                            mediaPlayer.setDataSource(Environment.getRootDirectory() + AudioService.SOUND_EFFECTS_PATH + ((String) AudioService.SOUND_EFFECT_FILES.get(AudioService.this.SOUND_EFFECT_FILES_MAP[i][0])));
                            mediaPlayer.setAudioStreamType(1);
                            mediaPlayer.prepare();
                            mediaPlayer.setVolume(fPow);
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() { // from class: android.media.AudioService.AudioHandler.1
                                @Override // android.media.MediaPlayer.OnCompletionListener
                                public void onCompletion(MediaPlayer mediaPlayer2) {
                                    AudioHandler.this.cleanupPlayer(mediaPlayer2);
                                }
                            });
                            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() { // from class: android.media.AudioService.AudioHandler.2
                                @Override // android.media.MediaPlayer.OnErrorListener
                                public boolean onError(MediaPlayer mediaPlayer2, int i3, int i4) {
                                    AudioHandler.this.cleanupPlayer(mediaPlayer2);
                                    return true;
                                }
                            });
                            mediaPlayer.start();
                        } catch (IOException e) {
                            Log.w(AudioService.TAG, "MediaPlayer IOException: " + e);
                        }
                    } catch (IllegalArgumentException e2) {
                        Log.w(AudioService.TAG, "MediaPlayer IllegalArgumentException: " + e2);
                    } catch (IllegalStateException e3) {
                        Log.w(AudioService.TAG, "MediaPlayer IllegalStateException: " + e3);
                    }
                }
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void cleanupPlayer(MediaPlayer mediaPlayer) {
            if (mediaPlayer != null) {
                try {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                } catch (IllegalStateException e) {
                    Log.w(AudioService.TAG, "MediaPlayer IllegalStateException: " + e);
                }
            }
        }

        private void setForceUse(int i, int i2) {
            AudioSystem.setForceUse(i, i2);
        }

        private void onPersistSafeVolumeState(int i) {
            Settings.Global.putInt(AudioService.this.mContentResolver, Settings.Global.AUDIO_SAFE_VOLUME_STATE, i);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            AudioRoutesInfo audioRoutesInfo;
            int i = message.what;
            switch (i) {
                case 0:
                    setDeviceVolume((VolumeStreamState) message.obj, message.arg1);
                    return;
                case 1:
                    persistVolume((VolumeStreamState) message.obj, message.arg1);
                    return;
                case 2:
                    if (AudioService.this.mUseFixedVolume) {
                        return;
                    }
                    Settings.System.putFloatForUser(AudioService.this.mContentResolver, Settings.System.VOLUME_MASTER, message.arg1 / 1000.0f, -2);
                    return;
                case 3:
                    persistRingerMode(AudioService.this.getRingerMode());
                    return;
                case 4:
                    if (AudioSystem.checkAudioFlinger() != 0) {
                        Log.e(AudioService.TAG, "Media server died.");
                        AudioService.sendMsg(AudioService.this.mAudioHandler, 4, 1, 0, 0, null, 500);
                        return;
                    }
                    Log.e(AudioService.TAG, "Media server started.");
                    AudioSystem.setParameters("restarting=true");
                    AudioService.readAndSetLowRamDevice();
                    synchronized (AudioService.this.mConnectedDevices) {
                        for (Map.Entry entry : AudioService.this.mConnectedDevices.entrySet()) {
                            AudioSystem.setDeviceConnectionState(((Integer) entry.getKey()).intValue(), 1, (String) entry.getValue());
                        }
                        break;
                    }
                    AudioSystem.setPhoneState(AudioService.this.mMode);
                    AudioSystem.setForceUse(0, AudioService.this.mForcedUseForComm);
                    AudioSystem.setForceUse(2, AudioService.this.mForcedUseForComm);
                    AudioSystem.setForceUse(4, AudioService.this.mCameraSoundForced.booleanValue() ? 11 : 0);
                    int numStreamTypes = AudioSystem.getNumStreamTypes() - 1;
                    while (true) {
                        if (numStreamTypes >= 0) {
                            VolumeStreamState volumeStreamState = AudioService.this.mStreamStates[numStreamTypes];
                            AudioSystem.initStreamVolume(numStreamTypes, 0, (volumeStreamState.mIndexMax + 5) / 10);
                            volumeStreamState.applyAllVolumes();
                            numStreamTypes--;
                        } else {
                            AudioService audioService = AudioService.this;
                            audioService.setRingerModeInt(audioService.getRingerMode(), false);
                            AudioService.this.restoreMasterVolume();
                            if (AudioService.this.mMonitorOrientation) {
                                AudioService.this.setOrientationForAudioSystem();
                            }
                            if (AudioService.this.mMonitorRotation) {
                                AudioService.this.setRotationForAudioSystem();
                            }
                            synchronized (AudioService.this.mBluetoothA2dpEnabledLock) {
                                AudioSystem.setForceUse(1, AudioService.this.mBluetoothA2dpEnabled ? 0 : 10);
                                break;
                            }
                            synchronized (AudioService.this.mSettingsLock) {
                                AudioSystem.setForceUse(3, AudioService.this.mDockAudioMediaEnabled ? 8 : 0);
                                break;
                            }
                            AudioSystem.setParameters("restarting=false");
                            return;
                        }
                    }
                    break;
                case 5:
                    onPlaySoundEffect(message.arg1, message.arg2);
                    return;
                case 6:
                    synchronized (AudioService.this.mConnectedDevices) {
                        AudioService.this.makeA2dpDeviceUnavailableNow((String) message.obj);
                        break;
                    }
                    return;
                case 7:
                    boolean zOnLoadSoundEffects = onLoadSoundEffects();
                    if (message.obj != null) {
                        LoadSoundEffectReply loadSoundEffectReply = (LoadSoundEffectReply) message.obj;
                        synchronized (loadSoundEffectReply) {
                            loadSoundEffectReply.mStatus = zOnLoadSoundEffects ? 0 : -1;
                            loadSoundEffectReply.notify();
                            break;
                        }
                        return;
                    }
                    return;
                case 8:
                case 13:
                    setForceUse(message.arg1, message.arg2);
                    return;
                case 9:
                    AudioService.this.resetBluetoothSco();
                    return;
                case 10:
                    setAllVolumes((VolumeStreamState) message.obj);
                    return;
                case 11:
                    if (AudioService.this.mUseFixedVolume) {
                        return;
                    }
                    Settings.System.putIntForUser(AudioService.this.mContentResolver, Settings.System.VOLUME_MASTER_MUTE, message.arg1, -2);
                    return;
                case 12:
                    int iBeginBroadcast = AudioService.this.mRoutesObservers.beginBroadcast();
                    if (iBeginBroadcast > 0) {
                        synchronized (AudioService.this.mCurAudioRoutes) {
                            audioRoutesInfo = new AudioRoutesInfo(AudioService.this.mCurAudioRoutes);
                            break;
                        }
                        while (iBeginBroadcast > 0) {
                            iBeginBroadcast--;
                            try {
                                ((IAudioRoutesObserver) AudioService.this.mRoutesObservers.getBroadcastItem(iBeginBroadcast)).dispatchAudioRoutesChanged(audioRoutesInfo);
                            } catch (RemoteException unused) {
                            }
                        }
                    }
                    AudioService.this.mRoutesObservers.finishBroadcast();
                    return;
                case 14:
                    AudioService.this.onCheckMusicActive();
                    return;
                case 15:
                    AudioService.this.onSendBecomingNoisyIntent();
                    return;
                case 16:
                case 17:
                    AudioService.this.onConfigureSafeVolume(message.what == 17);
                    return;
                case 18:
                    onPersistSafeVolumeState(message.arg1);
                    return;
                case 19:
                    AudioService.this.onBroadcastScoConnectionState(message.arg1);
                    return;
                case 20:
                    onUnloadSoundEffects();
                    return;
                default:
                    switch (i) {
                        case 100:
                            AudioService.this.onSetWiredDeviceConnectionState(message.arg1, message.arg2, (String) message.obj);
                            AudioService.this.mAudioEventWakeLock.release();
                            return;
                        case 101:
                            AudioService.this.onSetA2dpSourceConnectionState((BluetoothDevice) message.obj, message.arg1);
                            AudioService.this.mAudioEventWakeLock.release();
                            return;
                        case 102:
                            AudioService.this.onSetA2dpSinkConnectionState((BluetoothDevice) message.obj, message.arg1);
                            AudioService.this.mAudioEventWakeLock.release();
                            return;
                        default:
                            return;
                    }
            }
        }
    }

    private class SettingsObserver extends ContentObserver {
        SettingsObserver() {
            super(new Handler());
            AudioService.this.mContentResolver.registerContentObserver(Settings.System.getUriFor(Settings.System.MODE_RINGER_STREAMS_AFFECTED), false, this);
            AudioService.this.mContentResolver.registerContentObserver(Settings.Global.getUriFor(Settings.Global.DOCK_AUDIO_MEDIA_ENABLED), false, this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z) {
            super.onChange(z);
            synchronized (AudioService.this.mSettingsLock) {
                if (AudioService.this.updateRingerModeAffectedStreams()) {
                    AudioService audioService = AudioService.this;
                    audioService.setRingerModeInt(audioService.getRingerMode(), false);
                }
                AudioService audioService2 = AudioService.this;
                audioService2.readDockAudioSettings(audioService2.mContentResolver);
            }
        }
    }

    private void makeA2dpDeviceAvailable(String str) {
        sendMsg(this.mAudioHandler, 0, 2, 128, 0, this.mStreamStates[3], 0);
        setBluetoothA2dpOnInt(true);
        AudioSystem.setDeviceConnectionState(128, 1, str);
        AudioSystem.setParameters("A2dpSuspended=false");
        this.mConnectedDevices.put(new Integer(128), str);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSendBecomingNoisyIntent() {
        sendBroadcastToAll(new Intent(AudioManager.ACTION_AUDIO_BECOMING_NOISY));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void makeA2dpDeviceUnavailableNow(String str) {
        synchronized (this.mA2dpAvrcpLock) {
            this.mAvrcpAbsVolSupported = false;
        }
        AudioSystem.setDeviceConnectionState(128, 0, str);
        this.mConnectedDevices.remove(128);
    }

    private void makeA2dpDeviceUnavailableLater(String str) {
        AudioSystem.setParameters("A2dpSuspended=true");
        this.mConnectedDevices.remove(128);
        this.mAudioHandler.sendMessageDelayed(this.mAudioHandler.obtainMessage(6, str), 8000L);
    }

    private void makeA2dpSrcAvailable(String str) {
        AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_IN_BLUETOOTH_A2DP, 1, str);
        this.mConnectedDevices.put(new Integer(AudioSystem.DEVICE_IN_BLUETOOTH_A2DP), str);
    }

    private void makeA2dpSrcUnavailable(String str) {
        AudioSystem.setDeviceConnectionState(AudioSystem.DEVICE_IN_BLUETOOTH_A2DP, 0, str);
        this.mConnectedDevices.remove(Integer.valueOf(AudioSystem.DEVICE_IN_BLUETOOTH_A2DP));
    }

    private void cancelA2dpDeviceTimeout() {
        this.mAudioHandler.removeMessages(6);
    }

    private boolean hasScheduledA2dpDockTimeout() {
        return this.mAudioHandler.hasMessages(6);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSetA2dpSinkConnectionState(BluetoothDevice bluetoothDevice, int i) {
        if (bluetoothDevice == null) {
            return;
        }
        String address = bluetoothDevice.getAddress();
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            address = "";
        }
        synchronized (this.mConnectedDevices) {
            boolean z = this.mConnectedDevices.containsKey(128) && this.mConnectedDevices.get(128).equals(address);
            if (z && i != 2) {
                if (!bluetoothDevice.isBluetoothDock()) {
                    makeA2dpDeviceUnavailableNow(address);
                } else if (i == 0) {
                    makeA2dpDeviceUnavailableLater(address);
                }
                synchronized (this.mCurAudioRoutes) {
                    if (this.mCurAudioRoutes.mBluetoothName != null) {
                        this.mCurAudioRoutes.mBluetoothName = null;
                        sendMsg(this.mAudioHandler, 12, 1, 0, 0, null, 0);
                    }
                }
            } else if (!z && i == 2) {
                if (bluetoothDevice.isBluetoothDock()) {
                    cancelA2dpDeviceTimeout();
                    this.mDockAddress = address;
                } else if (hasScheduledA2dpDockTimeout()) {
                    cancelA2dpDeviceTimeout();
                    makeA2dpDeviceUnavailableNow(this.mDockAddress);
                }
                makeA2dpDeviceAvailable(address);
                synchronized (this.mCurAudioRoutes) {
                    String aliasName = bluetoothDevice.getAliasName();
                    if (!TextUtils.equals(this.mCurAudioRoutes.mBluetoothName, aliasName)) {
                        this.mCurAudioRoutes.mBluetoothName = aliasName;
                        sendMsg(this.mAudioHandler, 12, 1, 0, 0, null, 0);
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSetA2dpSourceConnectionState(BluetoothDevice bluetoothDevice, int i) {
        if (bluetoothDevice == null) {
            return;
        }
        String address = bluetoothDevice.getAddress();
        if (!BluetoothAdapter.checkBluetoothAddress(address)) {
            address = "";
        }
        synchronized (this.mConnectedDevices) {
            boolean z = this.mConnectedDevices.containsKey(Integer.valueOf(AudioSystem.DEVICE_IN_BLUETOOTH_A2DP)) && this.mConnectedDevices.get(Integer.valueOf(AudioSystem.DEVICE_IN_BLUETOOTH_A2DP)).equals(address);
            if (z && i != 2) {
                makeA2dpSrcUnavailable(address);
            } else if (!z && i == 2) {
                makeA2dpSrcAvailable(address);
            }
        }
    }

    @Override // android.media.IAudioService
    public void avrcpSupportsAbsoluteVolume(String str, boolean z) {
        synchronized (this.mA2dpAvrcpLock) {
            this.mAvrcpAbsVolSupported = z;
            sendMsg(this.mAudioHandler, 0, 2, 128, 0, this.mStreamStates[3], 0);
            sendMsg(this.mAudioHandler, 0, 2, 128, 0, this.mStreamStates[2], 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean handleDeviceConnection(boolean z, int i, String str) {
        synchronized (this.mConnectedDevices) {
            boolean z2 = this.mConnectedDevices.containsKey(Integer.valueOf(i)) && (str.isEmpty() || this.mConnectedDevices.get(Integer.valueOf(i)).equals(str));
            if (z2 && !z) {
                AudioSystem.setDeviceConnectionState(i, 0, this.mConnectedDevices.get(Integer.valueOf(i)));
                this.mConnectedDevices.remove(Integer.valueOf(i));
                return true;
            }
            if (z2 || !z) {
                return false;
            }
            AudioSystem.setDeviceConnectionState(i, 1, str);
            this.mConnectedDevices.put(new Integer(i), str);
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public int checkSendBecomingNoisyIntent(int i, int i2) {
        int i3 = 0;
        if (i2 == 0 && (this.mBecomingNoisyIntentDevices & i) != 0) {
            Iterator<Integer> it = this.mConnectedDevices.keySet().iterator();
            int i4 = 0;
            while (it.hasNext()) {
                int iIntValue = it.next().intValue();
                if ((this.mBecomingNoisyIntentDevices & iIntValue) != 0) {
                    i4 |= iIntValue;
                }
            }
            if (i4 == i) {
                sendMsg(this.mAudioHandler, 15, 0, 0, 0, null, 0);
                i3 = 1000;
            }
        }
        if (this.mAudioHandler.hasMessages(101) || this.mAudioHandler.hasMessages(102) || this.mAudioHandler.hasMessages(100)) {
            return 1000;
        }
        return i3;
    }

    private void sendDeviceConnectionIntent(int i, int i2, String str) {
        Intent intent = new Intent();
        intent.putExtra(UsbManager.EXTRA_DEVICE, i);
        intent.putExtra("state", i2);
        intent.putExtra("name", str);
        intent.addFlags(1073741824);
        int i3 = 1;
        if (i == 4) {
            intent.setAction(Intent.ACTION_HEADSET_PLUG);
            intent.putExtra("microphone", 1);
        } else if (i == 8) {
            i3 = 2;
            intent.setAction(Intent.ACTION_HEADSET_PLUG);
            intent.putExtra("microphone", 0);
        } else {
            if (i == 2048) {
                intent.setAction(Intent.ACTION_ANALOG_AUDIO_DOCK_PLUG);
            } else if (i == 4096) {
                intent.setAction(Intent.ACTION_DIGITAL_AUDIO_DOCK_PLUG);
            } else if (i == 1024) {
                intent.setAction(Intent.ACTION_HDMI_AUDIO_PLUG);
                i3 = 8;
            } else {
                i3 = 0;
            }
            i3 = 4;
        }
        synchronized (this.mCurAudioRoutes) {
            if (i3 != 0) {
                int i4 = this.mCurAudioRoutes.mMainType;
                int i5 = i2 != 0 ? i4 | i3 : (~i3) & i4;
                if (i5 != this.mCurAudioRoutes.mMainType) {
                    this.mCurAudioRoutes.mMainType = i5;
                    sendMsg(this.mAudioHandler, 12, 1, 0, 0, null, 0);
                }
            }
        }
        long jClearCallingIdentity = Binder.clearCallingIdentity();
        try {
            ActivityManagerNative.broadcastStickyIntent(intent, null, -1);
        } finally {
            Binder.restoreCallingIdentity(jClearCallingIdentity);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSetWiredDeviceConnectionState(int i, int i2, String str) {
        synchronized (this.mConnectedDevices) {
            boolean z = true;
            if (i2 == 0 && (i == 4 || i == 8)) {
                try {
                    setBluetoothA2dpOnInt(true);
                } catch (Throwable th) {
                    throw th;
                }
            }
            boolean z2 = (i & AudioSystem.DEVICE_OUT_ALL_USB) != 0;
            if (i2 != 1) {
                z = false;
            }
            handleDeviceConnection(z, i, z2 ? str : "");
            if (i2 != 0) {
                if (i == 4 || i == 8) {
                    setBluetoothA2dpOnInt(false);
                }
                if ((i & 12) != 0) {
                    sendMsg(this.mAudioHandler, 14, 0, 0, 0, null, MUSIC_ACTIVE_POLL_PERIOD_MS);
                }
            }
            if (!z2) {
                sendDeviceConnectionIntent(i, i2, str);
            }
        }
    }

    private class AudioServiceBroadcastReceiver extends BroadcastReceiver {
        private AudioServiceBroadcastReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            int i = 1;
            i = 1;
            i = 1;
            i = 1;
            int i2 = 0;
            if (action.equals(Intent.ACTION_DOCK_EVENT)) {
                int intExtra = intent.getIntExtra(Intent.EXTRA_DOCK_STATE, 0);
                if (intExtra == 1) {
                    i2 = 7;
                } else if (intExtra == 2) {
                    i2 = 6;
                } else if (intExtra == 3) {
                    i2 = 8;
                } else if (intExtra == 4) {
                    i2 = 9;
                }
                if (intExtra != 3 && (intExtra != 0 || AudioService.this.mDockState != 3)) {
                    AudioSystem.setForceUse(3, i2);
                }
                AudioService.this.mDockState = intExtra;
                return;
            }
            if (action.equals(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)) {
                int intExtra2 = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, 0);
                int i3 = 16;
                BluetoothDevice bluetoothDevice = (BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (bluetoothDevice == null) {
                    return;
                }
                String address = bluetoothDevice.getAddress();
                BluetoothClass bluetoothClass = bluetoothDevice.getBluetoothClass();
                if (bluetoothClass != null) {
                    int deviceClass = bluetoothClass.getDeviceClass();
                    if (deviceClass == 1028 || deviceClass == 1032) {
                        i3 = 32;
                    } else if (deviceClass == 1056) {
                        i3 = 64;
                    }
                }
                if (!BluetoothAdapter.checkBluetoothAddress(address)) {
                    address = "";
                }
                boolean z = intExtra2 == 2;
                if (AudioService.this.handleDeviceConnection(z, i3, address)) {
                    synchronized (AudioService.this.mScoClients) {
                        if (z) {
                            AudioService.this.mBluetoothHeadsetDevice = bluetoothDevice;
                        } else {
                            AudioService.this.mBluetoothHeadsetDevice = null;
                            AudioService.this.resetBluetoothSco();
                        }
                    }
                    return;
                }
                return;
            }
            if (action.equals(Intent.ACTION_USB_AUDIO_ACCESSORY_PLUG) || action.equals(Intent.ACTION_USB_AUDIO_DEVICE_PLUG)) {
                int intExtra3 = intent.getIntExtra("state", 0);
                int intExtra4 = intent.getIntExtra("card", -1);
                int intExtra5 = intent.getIntExtra(UsbManager.EXTRA_DEVICE, -1);
                String str = (intExtra4 == -1 && intExtra5 == -1) ? "" : "card=" + intExtra4 + ";device=" + intExtra5;
                int i4 = action.equals(Intent.ACTION_USB_AUDIO_ACCESSORY_PLUG) ? 8192 : 16384;
                Log.v(AudioService.TAG, "Broadcast Receiver: Got " + (action.equals(Intent.ACTION_USB_AUDIO_ACCESSORY_PLUG) ? "ACTION_USB_AUDIO_ACCESSORY_PLUG" : "ACTION_USB_AUDIO_DEVICE_PLUG") + ", state = " + intExtra3 + ", card: " + intExtra4 + ", device: " + intExtra5);
                AudioService.this.setWiredDeviceConnectionState(i4, intExtra3, str);
                return;
            }
            if (action.equals(BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED)) {
                synchronized (AudioService.this.mScoClients) {
                    int intExtra6 = intent.getIntExtra(BluetoothProfile.EXTRA_STATE, -1);
                    int i5 = (AudioService.this.mScoClients.isEmpty() || !(AudioService.this.mScoAudioState == 3 || AudioService.this.mScoAudioState == 1 || AudioService.this.mScoAudioState == 5)) ? 0 : 1;
                    switch (intExtra6) {
                        case 10:
                            AudioService.this.mScoAudioState = 0;
                            AudioService.this.clearAllScoClients(0, false);
                            i = 0;
                            break;
                        case 11:
                            if (AudioService.this.mScoAudioState != 3 && AudioService.this.mScoAudioState != 5 && AudioService.this.mScoAudioState != 4) {
                                AudioService.this.mScoAudioState = 2;
                            }
                            i = -1;
                            break;
                        case 12:
                            if (AudioService.this.mScoAudioState != 3 && AudioService.this.mScoAudioState != 5 && AudioService.this.mScoAudioState != 4) {
                                AudioService.this.mScoAudioState = 2;
                            }
                            break;
                        default:
                            i = -1;
                            break;
                    }
                    i2 = i5;
                }
                if (i2 != 0) {
                    AudioService.this.broadcastScoConnectionState(i);
                    Intent intent2 = new Intent(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED);
                    intent2.putExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, i);
                    AudioService.this.sendStickyBroadcastToAll(intent2);
                    return;
                }
                return;
            }
            if (action.equals(Intent.ACTION_BOOT_COMPLETED) || action.equals(Intent.ACTION_UI_BOOT_FINISH)) {
                if (AudioService.this.mBootCompleted) {
                    return;
                }
                Log.d(AudioService.TAG, "ACTION_BOOT_COMPLETED in AudioService.java");
                AudioService.this.mBootCompleted = true;
                AudioService.sendMsg(AudioService.this.mAudioHandler, 7, 2, 0, 0, null, 0);
                AudioService audioService = AudioService.this;
                audioService.mKeyguardManager = (KeyguardManager) audioService.mContext.getSystemService(Context.KEYGUARD_SERVICE);
                AudioService.this.mScoConnectionState = -1;
                AudioService.this.resetBluetoothSco();
                AudioService.this.getBluetoothHeadset();
                Intent intent3 = new Intent(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED);
                intent3.putExtra(AudioManager.EXTRA_SCO_AUDIO_STATE, 0);
                AudioService.this.sendStickyBroadcastToAll(intent3);
                BluetoothAdapter defaultAdapter = BluetoothAdapter.getDefaultAdapter();
                if (defaultAdapter != null) {
                    defaultAdapter.getProfileProxy(AudioService.this.mContext, AudioService.this.mBluetoothProfileServiceListener, 2);
                }
                AudioService.sendMsg(AudioService.this.mAudioHandler, 17, 0, 0, 0, null, 30000);
                return;
            }
            if (action.equals(Intent.ACTION_SCREEN_ON)) {
                AudioSystem.setParameters("screen_state=on");
                return;
            }
            if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                AudioSystem.setParameters("screen_state=off");
                return;
            }
            if (action.equals(Intent.ACTION_CONFIGURATION_CHANGED)) {
                AudioService.this.handleConfigurationChanged(context);
            } else if (action.equals(Intent.ACTION_USER_SWITCHED)) {
                AudioService.sendMsg(AudioService.this.mAudioHandler, 15, 0, 0, 0, null, 0);
                AudioService.this.mMediaFocusControl.discardAudioFocusOwner();
                AudioService.this.readAudioSettings(true);
                AudioService.sendMsg(AudioService.this.mAudioHandler, 10, 2, 0, 0, AudioService.this.mStreamStates[3], 0);
            }
        }
    }

    @Override // android.media.IAudioService
    public boolean registerRemoteController(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2, ComponentName componentName) {
        return this.mMediaFocusControl.registerRemoteController(iRemoteControlDisplay, i, i2, componentName);
    }

    @Override // android.media.IAudioService
    public boolean registerRemoteControlDisplay(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2) {
        return this.mMediaFocusControl.registerRemoteControlDisplay(iRemoteControlDisplay, i, i2);
    }

    @Override // android.media.IAudioService
    public void unregisterRemoteControlDisplay(IRemoteControlDisplay iRemoteControlDisplay) {
        this.mMediaFocusControl.unregisterRemoteControlDisplay(iRemoteControlDisplay);
    }

    @Override // android.media.IAudioService
    public void remoteControlDisplayUsesBitmapSize(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2) {
        this.mMediaFocusControl.remoteControlDisplayUsesBitmapSize(iRemoteControlDisplay, i, i2);
    }

    @Override // android.media.IAudioService
    public void remoteControlDisplayWantsPlaybackPositionSync(IRemoteControlDisplay iRemoteControlDisplay, boolean z) {
        this.mMediaFocusControl.remoteControlDisplayWantsPlaybackPositionSync(iRemoteControlDisplay, z);
    }

    @Override // android.media.IAudioService
    public void registerMediaButtonEventReceiverForCalls(ComponentName componentName) {
        this.mMediaFocusControl.registerMediaButtonEventReceiverForCalls(componentName);
    }

    @Override // android.media.IAudioService
    public void unregisterMediaButtonEventReceiverForCalls() {
        this.mMediaFocusControl.unregisterMediaButtonEventReceiverForCalls();
    }

    @Override // android.media.IAudioService
    public void registerMediaButtonIntent(PendingIntent pendingIntent, ComponentName componentName, IBinder iBinder) {
        this.mMediaFocusControl.registerMediaButtonIntent(pendingIntent, componentName, iBinder);
    }

    @Override // android.media.IAudioService
    public void unregisterMediaButtonIntent(PendingIntent pendingIntent) {
        this.mMediaFocusControl.unregisterMediaButtonIntent(pendingIntent);
    }

    @Override // android.media.IAudioService
    public int registerRemoteControlClient(PendingIntent pendingIntent, IRemoteControlClient iRemoteControlClient, String str) {
        return this.mMediaFocusControl.registerRemoteControlClient(pendingIntent, iRemoteControlClient, str);
    }

    @Override // android.media.IAudioService
    public void unregisterRemoteControlClient(PendingIntent pendingIntent, IRemoteControlClient iRemoteControlClient) {
        this.mMediaFocusControl.unregisterRemoteControlClient(pendingIntent, iRemoteControlClient);
    }

    @Override // android.media.IAudioService
    public void setRemoteControlClientPlaybackPosition(int i, long j) {
        this.mMediaFocusControl.setRemoteControlClientPlaybackPosition(i, j);
    }

    @Override // android.media.IAudioService
    public void updateRemoteControlClientMetadata(int i, int i2, Rating rating) {
        this.mMediaFocusControl.updateRemoteControlClientMetadata(i, i2, rating);
    }

    @Override // android.media.IAudioService
    public void registerRemoteVolumeObserverForRcc(int i, IRemoteVolumeObserver iRemoteVolumeObserver) {
        this.mMediaFocusControl.registerRemoteVolumeObserverForRcc(i, iRemoteVolumeObserver);
    }

    @Override // android.media.IAudioService
    public int getRemoteStreamVolume() {
        return this.mMediaFocusControl.getRemoteStreamVolume();
    }

    @Override // android.media.IAudioService
    public int getRemoteStreamMaxVolume() {
        return this.mMediaFocusControl.getRemoteStreamMaxVolume();
    }

    @Override // android.media.IAudioService
    public void setRemoteStreamVolume(int i) {
        this.mMediaFocusControl.setRemoteStreamVolume(i);
    }

    @Override // android.media.IAudioService
    public void setPlaybackStateForRcc(int i, int i2, long j, float f) {
        this.mMediaFocusControl.setPlaybackStateForRcc(i, i2, j, f);
    }

    @Override // android.media.IAudioService
    public void setPlaybackInfoForRcc(int i, int i2, int i3) {
        this.mMediaFocusControl.setPlaybackInfoForRcc(i, i2, i3);
    }

    @Override // android.media.IAudioService
    public void dispatchMediaKeyEvent(KeyEvent keyEvent) {
        this.mMediaFocusControl.dispatchMediaKeyEvent(keyEvent);
    }

    @Override // android.media.IAudioService
    public void dispatchMediaKeyEventUnderWakelock(KeyEvent keyEvent) {
        this.mMediaFocusControl.dispatchMediaKeyEventUnderWakelock(keyEvent);
    }

    @Override // android.media.IAudioService
    public int requestAudioFocus(int i, int i2, IBinder iBinder, IAudioFocusDispatcher iAudioFocusDispatcher, String str, String str2) {
        return this.mMediaFocusControl.requestAudioFocus(i, i2, iBinder, iAudioFocusDispatcher, str, str2);
    }

    @Override // android.media.IAudioService
    public int abandonAudioFocus(IAudioFocusDispatcher iAudioFocusDispatcher, String str) {
        return this.mMediaFocusControl.abandonAudioFocus(iAudioFocusDispatcher, str);
    }

    @Override // android.media.IAudioService
    public void unregisterAudioFocusClient(String str) {
        this.mMediaFocusControl.unregisterAudioFocusClient(str);
    }

    @Override // android.media.IAudioService
    public int getCurrentAudioFocus() {
        return this.mMediaFocusControl.getCurrentAudioFocus();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleConfigurationChanged(Context context) {
        int rotation;
        int i;
        try {
            Configuration configuration = context.getResources().getConfiguration();
            if (this.mMonitorOrientation && (i = configuration.orientation) != this.mDeviceOrientation) {
                this.mDeviceOrientation = i;
                setOrientationForAudioSystem();
            }
            if (this.mMonitorRotation && (rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation()) != this.mDeviceRotation) {
                this.mDeviceRotation = rotation;
                setRotationForAudioSystem();
            }
            sendMsg(this.mAudioHandler, 16, 0, 0, 0, null, 0);
            boolean z = this.mContext.getResources().getBoolean(17891411);
            synchronized (this.mSettingsLock) {
                synchronized (this.mCameraSoundForced) {
                    if (z != this.mCameraSoundForced.booleanValue()) {
                        this.mCameraSoundForced = Boolean.valueOf(z);
                        VolumeStreamState[] volumeStreamStateArr = this.mStreamStates;
                        VolumeStreamState volumeStreamState = volumeStreamStateArr[7];
                        if (z) {
                            volumeStreamState.setAllIndexesToMax();
                            this.mRingerModeAffectedStreams &= -129;
                        } else {
                            volumeStreamState.setAllIndexes(volumeStreamStateArr[1]);
                            this.mRingerModeAffectedStreams |= 128;
                        }
                        setRingerModeInt(getRingerMode(), false);
                        sendMsg(this.mAudioHandler, 8, 2, 4, z ? 11 : 0, null, 0);
                        sendMsg(this.mAudioHandler, 10, 2, 0, 0, this.mStreamStates[7], 0);
                    }
                }
            }
            this.mVolumePanel.setLayoutDirection(configuration.getLayoutDirection());
        } catch (Exception e) {
            Log.e(TAG, "Error handling configuration change: ", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setOrientationForAudioSystem() {
        int i = this.mDeviceOrientation;
        if (i == 0) {
            AudioSystem.setParameters("orientation=undefined");
            return;
        }
        if (i == 1) {
            AudioSystem.setParameters("orientation=portrait");
            return;
        }
        if (i == 2) {
            AudioSystem.setParameters("orientation=landscape");
        } else if (i == 3) {
            AudioSystem.setParameters("orientation=square");
        } else {
            Log.e(TAG, "Unknown orientation");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setRotationForAudioSystem() {
        int i = this.mDeviceRotation;
        if (i == 0) {
            AudioSystem.setParameters("rotation=0");
            return;
        }
        if (i == 1) {
            AudioSystem.setParameters("rotation=90");
            return;
        }
        if (i == 2) {
            AudioSystem.setParameters("rotation=180");
        } else if (i == 3) {
            AudioSystem.setParameters("rotation=270");
        } else {
            Log.e(TAG, "Unknown device rotation");
        }
    }

    public void setBluetoothA2dpOnInt(boolean z) {
        synchronized (this.mBluetoothA2dpEnabledLock) {
            this.mBluetoothA2dpEnabled = z;
            this.mAudioHandler.removeMessages(13);
            AudioSystem.setForceUse(1, this.mBluetoothA2dpEnabled ? 0 : 10);
        }
    }

    @Override // android.media.IAudioService
    public void setRingtonePlayer(IRingtonePlayer iRingtonePlayer) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.REMOTE_AUDIO_PLAYBACK, null);
        this.mRingtonePlayer = iRingtonePlayer;
    }

    @Override // android.media.IAudioService
    public IRingtonePlayer getRingtonePlayer() {
        return this.mRingtonePlayer;
    }

    @Override // android.media.IAudioService
    public AudioRoutesInfo startWatchingRoutes(IAudioRoutesObserver iAudioRoutesObserver) {
        AudioRoutesInfo audioRoutesInfo;
        synchronized (this.mCurAudioRoutes) {
            audioRoutesInfo = new AudioRoutesInfo(this.mCurAudioRoutes);
            this.mRoutesObservers.register(iAudioRoutesObserver);
        }
        return audioRoutesInfo;
    }

    private void setSafeMediaVolumeEnabled(boolean z) {
        synchronized (this.mSafeMediaVolumeState) {
            if (this.mSafeMediaVolumeState.intValue() != 0 && this.mSafeMediaVolumeState.intValue() != 1) {
                if (z && this.mSafeMediaVolumeState.intValue() == 2) {
                    this.mSafeMediaVolumeState = 3;
                    enforceSafeMediaVolume();
                } else if (!z && this.mSafeMediaVolumeState.intValue() == 3) {
                    this.mSafeMediaVolumeState = 2;
                    this.mMusicActiveMs = 0;
                    sendMsg(this.mAudioHandler, 14, 0, 0, 0, null, MUSIC_ACTIVE_POLL_PERIOD_MS);
                }
            }
        }
    }

    private void enforceSafeMediaVolume() {
        VolumeStreamState volumeStreamState = this.mStreamStates[3];
        int i = 12;
        int i2 = 0;
        while (i != 0) {
            int i3 = i2 + 1;
            int i4 = 1 << i2;
            if ((i4 & i) != 0) {
                int index = volumeStreamState.getIndex(i4);
                int i5 = this.mSafeMediaVolumeIndex;
                if (index > i5) {
                    volumeStreamState.setIndex(i5, i4);
                    sendMsg(this.mAudioHandler, 0, 2, i4, 0, volumeStreamState, 0);
                }
                i &= ~i4;
            }
            i2 = i3;
        }
    }

    private boolean checkSafeMediaVolume(int i, int i2, int i3) {
        synchronized (this.mSafeMediaVolumeState) {
            return this.mSafeMediaVolumeState.intValue() != 3 || this.mStreamVolumeAlias[i] != 3 || (i3 & 12) == 0 || i2 <= this.mSafeMediaVolumeIndex;
        }
    }

    public void disableSafeMediaVolume() {
        synchronized (this.mSafeMediaVolumeState) {
            setSafeMediaVolumeEnabled(false);
            StreamVolumeCommand streamVolumeCommand = this.mPendingVolumeCommand;
            if (streamVolumeCommand != null) {
                onSetStreamVolume(streamVolumeCommand.mStreamType, this.mPendingVolumeCommand.mIndex, this.mPendingVolumeCommand.mFlags, this.mPendingVolumeCommand.mDevice);
                this.mPendingVolumeCommand = null;
            }
        }
    }

    @Override // android.media.IAudioService
    public boolean isCameraSoundForced() {
        boolean zBooleanValue;
        synchronized (this.mCameraSoundForced) {
            zBooleanValue = this.mCameraSoundForced.booleanValue();
        }
        return zBooleanValue;
    }

    private void dumpRingerMode(PrintWriter printWriter) {
        printWriter.println("\nRinger mode: ");
        printWriter.println("- mode: " + RINGER_MODE_NAMES[this.mRingerMode]);
        printWriter.print("- ringer mode affected streams = 0x");
        printWriter.println(Integer.toHexString(this.mRingerModeAffectedStreams));
        printWriter.print("- ringer mode muted streams = 0x");
        printWriter.println(Integer.toHexString(this.mRingerModeMutedStreams));
    }

    @Override // android.os.Binder
    protected void dump(FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        this.mContext.enforceCallingOrSelfPermission(Manifest.permission.DUMP, TAG);
        this.mMediaFocusControl.dump(printWriter);
        dumpStreamStates(printWriter);
        dumpRingerMode(printWriter);
        printWriter.println("\nAudio routes:");
        printWriter.print("  mMainType=0x");
        printWriter.println(Integer.toHexString(this.mCurAudioRoutes.mMainType));
        printWriter.print("  mBluetoothName=");
        printWriter.println(this.mCurAudioRoutes.mBluetoothName);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void readAndSetLowRamDevice() {
        int lowRamDevice = AudioSystem.setLowRamDevice(ActivityManager.isLowRamDeviceStatic());
        if (lowRamDevice != 0) {
            Log.w(TAG, "AudioFlinger informed of device's low RAM attribute; status " + lowRamDevice);
        }
    }
}
