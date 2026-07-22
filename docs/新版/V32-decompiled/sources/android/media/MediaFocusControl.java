package android.media;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.KeyguardManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Slog;
import android.view.KeyEvent;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Stack;

/* JADX INFO: loaded from: classes.dex */
public class MediaFocusControl implements PendingIntent.OnFinished {
    protected static final boolean DEBUG_RC = false;
    protected static final boolean DEBUG_VOL = false;
    private static final String EXTRA_WAKELOCK_ACQUIRED = "android.media.AudioService.WAKELOCK_ACQUIRED";
    protected static final String IN_VOICE_COMM_FOCUS_ID = "AudioFocus_For_Phone_Ring_And_Calls";
    private static final int MSG_PERSIST_MEDIABUTTONRECEIVER = 0;
    private static final int MSG_PROMOTE_RCC = 6;
    private static final int MSG_RCC_NEW_PLAYBACK_INFO = 4;
    private static final int MSG_RCC_NEW_PLAYBACK_STATE = 7;
    private static final int MSG_RCC_NEW_VOLUME_OBS = 5;
    private static final int MSG_RCC_SEEK_REQUEST = 8;
    private static final int MSG_RCC_UPDATE_METADATA = 9;
    private static final int MSG_RCDISPLAY_CLEAR = 1;
    private static final int MSG_RCDISPLAY_INIT_INFO = 10;
    private static final int MSG_RCDISPLAY_UPDATE = 2;
    private static final int MSG_REEVALUATE_RCD = 11;
    private static final int MSG_REEVALUATE_REMOTE = 3;
    private static final int RCD_REG_FAILURE = 0;
    private static final int RCD_REG_SUCCESS_ENABLED_NOTIF = 2;
    private static final int RCD_REG_SUCCESS_PERMISSION = 1;
    private static final int RC_INFO_ALL = 15;
    private static final int RC_INFO_NONE = 0;
    private static final int SENDMSG_NOOP = 1;
    private static final int SENDMSG_QUEUE = 2;
    private static final int SENDMSG_REPLACE = 0;
    private static final String TAG = "MediaFocusControl";
    private static final int VOICEBUTTON_ACTION_DISCARD_CURRENT_KEY_PRESS = 1;
    private static final int VOICEBUTTON_ACTION_SIMULATE_KEY_PRESS = 3;
    private static final int VOICEBUTTON_ACTION_START_VOICE_INPUT = 2;
    private static final int WAKELOCK_RELEASE_ON_FINISHED = 1980;
    private final AppOpsManager mAppOps;
    private final AudioService mAudioService;
    private final ContentResolver mContentResolver;
    private final Context mContext;
    private IRemoteControlClient mCurrentRcClient;
    private int mCurrentRcClientGen;
    private PendingIntent mCurrentRcClientIntent;
    private final Object mCurrentRcLock;
    private final MediaEventHandler mEventHandler;
    private final Stack<FocusRequester> mFocusStack;
    private boolean mHasRemotePlayback;
    private boolean mIsRinging = false;
    BroadcastReceiver mKeyEventDone;
    private final KeyguardManager mKeyguardManager;
    private RemotePlaybackState mMainRemote;
    private boolean mMainRemoteIsActive;
    private final PowerManager.WakeLock mMediaEventWakeLock;
    private ComponentName mMediaReceiverForCalls;
    private final NotificationListenerObserver mNotifListenerObserver;
    private PhoneStateListener mPhoneStateListener;
    private final Stack<RemoteControlStackEntry> mRCStack;
    private ArrayList<DisplayInfoForServer> mRcDisplays;
    private final BroadcastReceiver mReceiver;
    private boolean mVoiceButtonDown;
    private boolean mVoiceButtonHandled;
    private final Object mVoiceEventLock;
    private final VolumeController mVolumeController;
    private static final Uri ENABLED_NOTIFICATION_LISTENERS_URI = Settings.Secure.getUriFor(Settings.Secure.ENABLED_NOTIFICATION_LISTENERS);
    private static final Object mAudioFocusLock = new Object();
    private static final Object mRingingLock = new Object();
    private static int sLastRccId = 0;

    protected static boolean isMediaKeyCode(int i) {
        if (i == 79 || i == 222) {
            return true;
        }
        switch (i) {
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 91:
                return true;
            default:
                switch (i) {
                    case 126:
                    case 127:
                    case 128:
                    case 129:
                    case 130:
                        return true;
                    default:
                        return false;
                }
        }
    }

    private static boolean isPlaystateActive(int i) {
        switch (i) {
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                return true;
            default:
                return false;
        }
    }

    private static boolean isValidVoiceInputKeyCode(int i) {
        return i == 79;
    }

    static /* synthetic */ int access$3004() {
        int i = sLastRccId + 1;
        sLastRccId = i;
        return i;
    }

    protected MediaFocusControl(Looper looper, Context context, VolumeController volumeController, AudioService audioService) {
        PackageIntentsReceiver packageIntentsReceiver = new PackageIntentsReceiver();
        this.mReceiver = packageIntentsReceiver;
        this.mPhoneStateListener = new PhoneStateListener() { // from class: android.media.MediaFocusControl.1
            @Override // android.telephony.PhoneStateListener
            public void onCallStateChanged(int i, String str) {
                if (i == 1) {
                    synchronized (MediaFocusControl.mRingingLock) {
                        MediaFocusControl.this.mIsRinging = true;
                    }
                } else if (i == 2 || i == 0) {
                    synchronized (MediaFocusControl.mRingingLock) {
                        MediaFocusControl.this.mIsRinging = false;
                    }
                }
            }
        };
        this.mFocusStack = new Stack<>();
        this.mVoiceEventLock = new Object();
        this.mKeyEventDone = new BroadcastReceiver() { // from class: android.media.MediaFocusControl.2
            @Override // android.content.BroadcastReceiver
            public void onReceive(Context context2, Intent intent) {
                Bundle extras;
                if (intent == null || (extras = intent.getExtras()) == null || !extras.containsKey(MediaFocusControl.EXTRA_WAKELOCK_ACQUIRED)) {
                    return;
                }
                MediaFocusControl.this.mMediaEventWakeLock.release();
            }
        };
        this.mCurrentRcLock = new Object();
        this.mCurrentRcClient = null;
        this.mCurrentRcClientIntent = null;
        this.mCurrentRcClientGen = 0;
        this.mRCStack = new Stack<>();
        this.mMediaReceiverForCalls = null;
        this.mRcDisplays = new ArrayList<>(1);
        this.mEventHandler = new MediaEventHandler(looper);
        this.mContext = context;
        this.mContentResolver = context.getContentResolver();
        this.mVolumeController = volumeController;
        this.mAudioService = audioService;
        this.mMediaEventWakeLock = ((PowerManager) context.getSystemService(Context.POWER_SERVICE)).newWakeLock(1, "handleMediaEvent");
        this.mMainRemote = new RemotePlaybackState(-1, AudioService.getMaxStreamVolume(3), AudioService.getMaxStreamVolume(3));
        ((TelephonyManager) context.getSystemService("phone")).listen(this.mPhoneStateListener, 32);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        intentFilter.addAction(Intent.ACTION_PACKAGE_DATA_CLEARED);
        intentFilter.addDataScheme("package");
        context.registerReceiver(packageIntentsReceiver, intentFilter);
        this.mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        this.mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        this.mNotifListenerObserver = new NotificationListenerObserver();
        this.mHasRemotePlayback = false;
        this.mMainRemoteIsActive = false;
        postReevaluateRemote();
    }

    protected void dump(PrintWriter printWriter) {
        dumpFocusStack(printWriter);
        dumpRCStack(printWriter);
        dumpRCCStack(printWriter);
        dumpRCDList(printWriter);
    }

    private class NotificationListenerObserver extends ContentObserver {
        NotificationListenerObserver() {
            super(MediaFocusControl.this.mEventHandler);
            MediaFocusControl.this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.ENABLED_NOTIFICATION_LISTENERS), false, this);
        }

        @Override // android.database.ContentObserver
        public void onChange(boolean z, Uri uri) {
            if (!MediaFocusControl.ENABLED_NOTIFICATION_LISTENERS_URI.equals(uri) || z) {
                return;
            }
            MediaFocusControl.this.postReevaluateRemoteControlDisplays();
        }
    }

    private int checkRcdRegistrationAuthorization(ComponentName componentName) {
        if (this.mContext.checkCallingOrSelfPermission(Manifest.permission.MEDIA_CONTENT_CONTROL) == 0) {
            return 1;
        }
        if (componentName != null) {
            long jClearCallingIdentity = Binder.clearCallingIdentity();
            try {
                String stringForUser = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), Settings.Secure.ENABLED_NOTIFICATION_LISTENERS, ActivityManager.getCurrentUser());
                if (stringForUser != null) {
                    for (String str : stringForUser.split(":")) {
                        ComponentName componentNameUnflattenFromString = ComponentName.unflattenFromString(str);
                        if (componentNameUnflattenFromString != null && componentName.equals(componentNameUnflattenFromString)) {
                            return 2;
                        }
                    }
                }
            } finally {
                Binder.restoreCallingIdentity(jClearCallingIdentity);
            }
        }
        return 0;
    }

    protected boolean registerRemoteController(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2, ComponentName componentName) {
        if (checkRcdRegistrationAuthorization(componentName) != 0) {
            registerRemoteControlDisplay_int(iRemoteControlDisplay, i, i2, componentName);
            return true;
        }
        Slog.w(TAG, "Access denied to process: " + Binder.getCallingPid() + ", must have permission " + Manifest.permission.MEDIA_CONTENT_CONTROL + " or be an enabled NotificationListenerService for registerRemoteController");
        return false;
    }

    protected boolean registerRemoteControlDisplay(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2) {
        if (checkRcdRegistrationAuthorization(null) != 0) {
            registerRemoteControlDisplay_int(iRemoteControlDisplay, i, i2, null);
            return true;
        }
        Slog.w(TAG, "Access denied to process: " + Binder.getCallingPid() + ", must have permission " + Manifest.permission.MEDIA_CONTENT_CONTROL + " to register IRemoteControlDisplay");
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postReevaluateRemoteControlDisplays() {
        sendMsg(this.mEventHandler, 11, 2, 0, 0, null, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onReevaluateRemoteControlDisplays() {
        String stringForUser = Settings.Secure.getStringForUser(this.mContext.getContentResolver(), Settings.Secure.ENABLED_NOTIFICATION_LISTENERS, ActivityManager.getCurrentUser());
        synchronized (mAudioFocusLock) {
            synchronized (this.mRCStack) {
                String[] strArrSplit = stringForUser == null ? null : stringForUser.split(":");
                for (DisplayInfoForServer displayInfoForServer : this.mRcDisplays) {
                    if (displayInfoForServer.mClientNotifListComp != null) {
                        boolean z = displayInfoForServer.mEnabled;
                        displayInfoForServer.mEnabled = isComponentInStringArray(displayInfoForServer.mClientNotifListComp, strArrSplit);
                        if (z != displayInfoForServer.mEnabled) {
                            try {
                                displayInfoForServer.mRcDisplay.setEnabled(displayInfoForServer.mEnabled);
                                enableRemoteControlDisplayForClient_syncRcStack(displayInfoForServer.mRcDisplay, displayInfoForServer.mEnabled);
                                if (displayInfoForServer.mEnabled) {
                                    sendMsg(this.mEventHandler, 10, 2, displayInfoForServer.mArtworkExpectedWidth, displayInfoForServer.mArtworkExpectedHeight, displayInfoForServer.mRcDisplay, 0);
                                }
                            } catch (RemoteException e) {
                                Log.e(TAG, "Error en/disabling RCD: ", e);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isComponentInStringArray(ComponentName componentName, String[] strArr) {
        if (strArr != null && strArr.length != 0) {
            String strFlattenToString = componentName.flattenToString();
            for (String str : strArr) {
                if (strFlattenToString.equals(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static void sendMsg(Handler handler, int i, int i2, int i3, int i4, Object obj, int i5) {
        if (i2 == 0) {
            handler.removeMessages(i);
        } else if (i2 == 1 && handler.hasMessages(i)) {
            return;
        }
        handler.sendMessageDelayed(handler.obtainMessage(i, i3, i4, obj), i5);
    }

    private class MediaEventHandler extends Handler {
        MediaEventHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 0:
                    MediaFocusControl.this.onHandlePersistMediaButtonReceiver((ComponentName) message.obj);
                    break;
                case 1:
                    MediaFocusControl.this.onRcDisplayClear();
                    break;
                case 2:
                    MediaFocusControl.this.onRcDisplayUpdate((RemoteControlStackEntry) message.obj, message.arg1);
                    break;
                case 3:
                    MediaFocusControl.this.onReevaluateRemote();
                    break;
                case 4:
                    MediaFocusControl.this.onNewPlaybackInfoForRcc(message.arg1, message.arg2, ((Integer) message.obj).intValue());
                    break;
                case 5:
                    MediaFocusControl.this.onRegisterVolumeObserverForRcc(message.arg1, (IRemoteVolumeObserver) message.obj);
                    break;
                case 6:
                    MediaFocusControl.this.onPromoteRcc(message.arg1);
                    break;
                case 7:
                    MediaFocusControl.this.onNewPlaybackStateForRcc(message.arg1, message.arg2, (RccPlaybackState) message.obj);
                    break;
                case 8:
                    MediaFocusControl.this.onSetRemoteControlClientPlaybackPosition(message.arg1, ((Long) message.obj).longValue());
                    break;
                case 9:
                    MediaFocusControl.this.onUpdateRemoteControlClientMetadata(message.arg1, message.arg2, (Rating) message.obj);
                    break;
                case 10:
                    MediaFocusControl.this.onRcDisplayInitInfo((IRemoteControlDisplay) message.obj, message.arg1, message.arg2);
                    break;
                case 11:
                    MediaFocusControl.this.onReevaluateRemoteControlDisplays();
                    break;
            }
        }
    }

    protected void discardAudioFocusOwner() {
        synchronized (mAudioFocusLock) {
            if (!this.mFocusStack.empty()) {
                FocusRequester focusRequesterPop = this.mFocusStack.pop();
                focusRequesterPop.handleFocusLoss(-1);
                focusRequesterPop.release();
                synchronized (this.mRCStack) {
                    clearRemoteControlDisplay_syncAfRcs();
                }
            }
        }
    }

    private void notifyTopOfAudioFocusStack() {
        if (this.mFocusStack.empty() || !canReassignAudioFocus()) {
            return;
        }
        this.mFocusStack.peek().handleFocusGain(1);
    }

    private void propagateFocusLossFromGain_syncAf(int i) {
        Iterator<FocusRequester> it = this.mFocusStack.iterator();
        while (it.hasNext()) {
            it.next().handleExternalFocusGain(i);
        }
    }

    private void dumpFocusStack(PrintWriter printWriter) {
        printWriter.println("\nAudio Focus stack entries (last is top of stack):");
        synchronized (mAudioFocusLock) {
            Iterator<FocusRequester> it = this.mFocusStack.iterator();
            while (it.hasNext()) {
                it.next().dump(printWriter);
            }
        }
    }

    private void removeFocusStackEntry(String str, boolean z) {
        if (!this.mFocusStack.empty() && this.mFocusStack.peek().hasSameClient(str)) {
            this.mFocusStack.pop().release();
            if (z) {
                notifyTopOfAudioFocusStack();
                synchronized (this.mRCStack) {
                    checkUpdateRemoteControlDisplay_syncAfRcs(15);
                }
                return;
            }
            return;
        }
        Iterator<FocusRequester> it = this.mFocusStack.iterator();
        while (it.hasNext()) {
            FocusRequester next = it.next();
            if (next.hasSameClient(str)) {
                Log.i(TAG, "AudioFocus  removeFocusStackEntry(): removing entry for " + str);
                it.remove();
                next.release();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeFocusStackEntryForClient(IBinder iBinder) {
        boolean z = !this.mFocusStack.isEmpty() && this.mFocusStack.peek().hasSameBinder(iBinder);
        Iterator<FocusRequester> it = this.mFocusStack.iterator();
        while (it.hasNext()) {
            if (it.next().hasSameBinder(iBinder)) {
                Log.i(TAG, "AudioFocus  removeFocusStackEntry(): removing entry for " + iBinder);
                it.remove();
            }
        }
        if (z) {
            notifyTopOfAudioFocusStack();
            synchronized (this.mRCStack) {
                checkUpdateRemoteControlDisplay_syncAfRcs(15);
            }
        }
    }

    private boolean canReassignAudioFocus() {
        return this.mFocusStack.isEmpty() || !this.mFocusStack.peek().hasSameClient(IN_VOICE_COMM_FOCUS_ID);
    }

    protected class AudioFocusDeathHandler implements IBinder.DeathRecipient {
        private IBinder mCb;

        AudioFocusDeathHandler(IBinder iBinder) {
            this.mCb = iBinder;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (MediaFocusControl.mAudioFocusLock) {
                Log.w(MediaFocusControl.TAG, "  AudioFocus   audio focus client died");
                MediaFocusControl.this.removeFocusStackEntryForClient(this.mCb);
            }
        }

        public IBinder getBinder() {
            return this.mCb;
        }
    }

    protected int getCurrentAudioFocus() {
        synchronized (mAudioFocusLock) {
            if (this.mFocusStack.empty()) {
                return 0;
            }
            return this.mFocusStack.peek().getGainRequest();
        }
    }

    protected int requestAudioFocus(int i, int i2, IBinder iBinder, IAudioFocusDispatcher iAudioFocusDispatcher, String str, String str2) {
        Log.i(TAG, " AudioFocus  requestAudioFocus() from " + str);
        if (!iBinder.pingBinder()) {
            Log.e(TAG, " AudioFocus DOA client for requestAudioFocus(), aborting.");
            return 0;
        }
        if (this.mAppOps.noteOp(32, Binder.getCallingUid(), str2) != 0) {
            return 0;
        }
        synchronized (mAudioFocusLock) {
            if (!canReassignAudioFocus()) {
                return 0;
            }
            AudioFocusDeathHandler audioFocusDeathHandler = new AudioFocusDeathHandler(iBinder);
            try {
                iBinder.linkToDeath(audioFocusDeathHandler, 0);
                if (!this.mFocusStack.empty() && this.mFocusStack.peek().hasSameClient(str)) {
                    if (this.mFocusStack.peek().getGainRequest() == i2) {
                        iBinder.unlinkToDeath(audioFocusDeathHandler, 0);
                        return 1;
                    }
                    this.mFocusStack.pop().release();
                }
                removeFocusStackEntry(str, false);
                if (!this.mFocusStack.empty()) {
                    propagateFocusLossFromGain_syncAf(i2);
                }
                this.mFocusStack.push(new FocusRequester(i, i2, iAudioFocusDispatcher, iBinder, str, audioFocusDeathHandler, str2, Binder.getCallingUid()));
                synchronized (this.mRCStack) {
                    checkUpdateRemoteControlDisplay_syncAfRcs(15);
                }
                return 1;
            } catch (RemoteException unused) {
                Log.w(TAG, "AudioFocus  requestAudioFocus() could not link to " + iBinder + " binder death");
                return 0;
            }
        }
    }

    protected int abandonAudioFocus(IAudioFocusDispatcher iAudioFocusDispatcher, String str) {
        Log.i(TAG, " AudioFocus  abandonAudioFocus() from " + str);
        try {
            synchronized (mAudioFocusLock) {
                removeFocusStackEntry(str, true);
            }
        } catch (ConcurrentModificationException e) {
            Log.e(TAG, "FATAL EXCEPTION AudioFocus  abandonAudioFocus() caused " + e);
            e.printStackTrace();
        }
        return 1;
    }

    protected void unregisterAudioFocusClient(String str) {
        synchronized (mAudioFocusLock) {
            removeFocusStackEntry(str, false);
        }
    }

    protected void dispatchMediaKeyEvent(KeyEvent keyEvent) {
        filterMediaKeyEvent(keyEvent, false);
    }

    protected void dispatchMediaKeyEventUnderWakelock(KeyEvent keyEvent) {
        filterMediaKeyEvent(keyEvent, true);
    }

    private void filterMediaKeyEvent(KeyEvent keyEvent, boolean z) {
        if (!isValidMediaKeyEvent(keyEvent)) {
            Log.e(TAG, "not dispatching invalid media key event " + keyEvent);
            return;
        }
        synchronized (mRingingLock) {
            synchronized (this.mRCStack) {
                if (this.mMediaReceiverForCalls != null && (this.mIsRinging || this.mAudioService.getMode() == 2)) {
                    dispatchMediaKeyEventForCalls(keyEvent, z);
                } else if (isValidVoiceInputKeyCode(keyEvent.getKeyCode())) {
                    filterVoiceInputKeyEvent(keyEvent, z);
                } else {
                    dispatchMediaKeyEvent(keyEvent, z);
                }
            }
        }
    }

    private void dispatchMediaKeyEventForCalls(KeyEvent keyEvent, boolean z) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON, (Uri) null);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
        intent.setPackage(this.mMediaReceiverForCalls.getPackageName());
        if (z) {
            this.mMediaEventWakeLock.acquire();
            intent.putExtra(EXTRA_WAKELOCK_ACQUIRED, WAKELOCK_RELEASE_ON_FINISHED);
        }
        long jClearCallingIdentity = Binder.clearCallingIdentity();
        try {
            this.mContext.sendOrderedBroadcastAsUser(intent, UserHandle.ALL, null, this.mKeyEventDone, this.mEventHandler, -1, null, null);
        } finally {
            Binder.restoreCallingIdentity(jClearCallingIdentity);
        }
    }

    private void dispatchMediaKeyEvent(KeyEvent keyEvent, boolean z) {
        if (z) {
            this.mMediaEventWakeLock.acquire();
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON, (Uri) null);
        intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
        synchronized (this.mRCStack) {
            if (!this.mRCStack.empty()) {
                try {
                    this.mRCStack.peek().mMediaIntent.send(this.mContext, z ? WAKELOCK_RELEASE_ON_FINISHED : 0, intent, this, this.mEventHandler);
                } catch (PendingIntent.CanceledException e) {
                    Log.e(TAG, "Error sending pending intent " + this.mRCStack.peek());
                    e.printStackTrace();
                }
            } else {
                if (z) {
                    intent.putExtra(EXTRA_WAKELOCK_ACQUIRED, WAKELOCK_RELEASE_ON_FINISHED);
                }
                long jClearCallingIdentity = Binder.clearCallingIdentity();
                try {
                    this.mContext.sendOrderedBroadcastAsUser(intent, UserHandle.ALL, null, this.mKeyEventDone, this.mEventHandler, -1, null, null);
                } finally {
                    Binder.restoreCallingIdentity(jClearCallingIdentity);
                }
            }
        }
    }

    private void filterVoiceInputKeyEvent(KeyEvent keyEvent, boolean z) {
        char c;
        int action = keyEvent.getAction();
        synchronized (this.mVoiceEventLock) {
            c = 1;
            try {
                if (action == 0) {
                    if (keyEvent.getRepeatCount() == 0) {
                        this.mVoiceButtonDown = true;
                        this.mVoiceButtonHandled = false;
                    } else if (this.mVoiceButtonDown && !this.mVoiceButtonHandled && (keyEvent.getFlags() & 128) != 0) {
                        this.mVoiceButtonHandled = true;
                        c = 2;
                    }
                } else if (action == 1 && this.mVoiceButtonDown) {
                    this.mVoiceButtonDown = false;
                    if (!this.mVoiceButtonHandled && !keyEvent.isCanceled()) {
                        c = 3;
                    }
                }
            } catch (Throwable th) {
                throw th;
            }
        }
        if (c == 2) {
            startVoiceBasedInteractions(z);
        } else {
            if (c != 3) {
                return;
            }
            sendSimulatedMediaButtonEvent(keyEvent, z);
        }
    }

    private void sendSimulatedMediaButtonEvent(KeyEvent keyEvent, boolean z) {
        dispatchMediaKeyEvent(KeyEvent.changeAction(keyEvent, 0), z);
        dispatchMediaKeyEvent(KeyEvent.changeAction(keyEvent, 1), z);
    }

    private class PackageIntentsReceiver extends BroadcastReceiver {
        private PackageIntentsReceiver() {
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String schemeSpecificPart;
            String schemeSpecificPart2;
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_PACKAGE_REMOVED) || action.equals(Intent.ACTION_PACKAGE_DATA_CLEARED)) {
                if (intent.getBooleanExtra(Intent.EXTRA_REPLACING, false) || (schemeSpecificPart = intent.getData().getSchemeSpecificPart()) == null) {
                    return;
                }
                MediaFocusControl.this.cleanupMediaButtonReceiverForPackage(schemeSpecificPart, true);
                return;
            }
            if ((action.equals(Intent.ACTION_PACKAGE_ADDED) || action.equals(Intent.ACTION_PACKAGE_CHANGED)) && (schemeSpecificPart2 = intent.getData().getSchemeSpecificPart()) != null) {
                MediaFocusControl.this.cleanupMediaButtonReceiverForPackage(schemeSpecificPart2, false);
            }
        }
    }

    private static boolean isValidMediaKeyEvent(KeyEvent keyEvent) {
        if (keyEvent == null) {
            return false;
        }
        return isMediaKeyCode(keyEvent.getKeyCode());
    }

    private void startVoiceBasedInteractions(boolean z) {
        Intent intent;
        PowerManager powerManager = (PowerManager) this.mContext.getSystemService(Context.POWER_SERVICE);
        KeyguardManager keyguardManager = this.mKeyguardManager;
        boolean z2 = keyguardManager != null && keyguardManager.isKeyguardLocked();
        if (!z2 && powerManager.isScreenOn()) {
            intent = new Intent(RecognizerIntent.ACTION_WEB_SEARCH);
            Log.i(TAG, "voice-based interactions: about to use ACTION_WEB_SEARCH");
        } else {
            intent = new Intent(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE);
            intent.putExtra(RecognizerIntent.EXTRA_SECURE, z2 && this.mKeyguardManager.isKeyguardSecure());
            Log.i(TAG, "voice-based interactions: about to use ACTION_VOICE_SEARCH_HANDS_FREE");
        }
        if (z) {
            this.mMediaEventWakeLock.acquire();
        }
        long jClearCallingIdentity = Binder.clearCallingIdentity();
        try {
            try {
                intent.setFlags(276824064);
                this.mContext.startActivityAsUser(intent, UserHandle.CURRENT);
                Binder.restoreCallingIdentity(jClearCallingIdentity);
                if (!z) {
                    return;
                }
            } catch (ActivityNotFoundException e) {
                Log.w(TAG, "No activity for search: " + e);
                Binder.restoreCallingIdentity(jClearCallingIdentity);
                if (!z) {
                    return;
                }
            }
            this.mMediaEventWakeLock.release();
        } catch (Throwable th) {
            Binder.restoreCallingIdentity(jClearCallingIdentity);
            if (z) {
                this.mMediaEventWakeLock.release();
            }
            throw th;
        }
    }

    @Override // android.app.PendingIntent.OnFinished
    public void onSendFinished(PendingIntent pendingIntent, Intent intent, int i, String str, Bundle bundle) {
        if (i == WAKELOCK_RELEASE_ON_FINISHED) {
            this.mMediaEventWakeLock.release();
        }
    }

    private class RcClientDeathHandler implements IBinder.DeathRecipient {
        private final IBinder mCb;
        private final PendingIntent mMediaIntent;

        RcClientDeathHandler(IBinder iBinder, PendingIntent pendingIntent) {
            this.mCb = iBinder;
            this.mMediaIntent = pendingIntent;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            Log.w(MediaFocusControl.TAG, "  RemoteControlClient died");
            MediaFocusControl.this.registerRemoteControlClient(this.mMediaIntent, null, null);
            MediaFocusControl.this.postReevaluateRemote();
        }

        public IBinder getBinder() {
            return this.mCb;
        }
    }

    private class RemotePlaybackState {
        int mRccId;
        int mVolume;
        int mVolumeHandling;
        int mVolumeMax;

        private RemotePlaybackState(int i, int i2, int i3) {
            this.mRccId = i;
            this.mVolume = i2;
            this.mVolumeMax = i3;
            this.mVolumeHandling = 1;
        }
    }

    private static class RccPlaybackState {
        public long mPositionMs;
        public float mSpeed;
        public int mState;

        public RccPlaybackState(int i, long j, float f) {
            this.mState = i;
            this.mPositionMs = j;
            this.mSpeed = f;
        }

        public void reset() {
            this.mState = 1;
            this.mPositionMs = -1L;
            this.mSpeed = 1.0f;
        }

        public String toString() {
            return stateToString() + ", " + posToString() + ", " + this.mSpeed + "X";
        }

        private String posToString() {
            long j = this.mPositionMs;
            return j == -1 ? "PLAYBACK_POSITION_INVALID" : j == RemoteControlClient.PLAYBACK_POSITION_ALWAYS_UNKNOWN ? "PLAYBACK_POSITION_ALWAYS_UNKNOWN" : String.valueOf(this.mPositionMs) + "ms";
        }

        private String stateToString() {
            switch (this.mState) {
                case 0:
                    return "PLAYSTATE_NONE";
                case 1:
                    return "PLAYSTATE_STOPPED";
                case 2:
                    return "PLAYSTATE_PAUSED";
                case 3:
                    return "PLAYSTATE_PLAYING";
                case 4:
                    return "PLAYSTATE_FAST_FORWARDING";
                case 5:
                    return "PLAYSTATE_REWINDING";
                case 6:
                    return "PLAYSTATE_SKIPPING_FORWARDS";
                case 7:
                    return "PLAYSTATE_SKIPPING_BACKWARDS";
                case 8:
                    return "PLAYSTATE_BUFFERING";
                case 9:
                    return "PLAYSTATE_ERROR";
                default:
                    return "[invalid playstate]";
            }
        }
    }

    protected static class RemoteControlStackEntry implements IBinder.DeathRecipient {
        public String mCallingPackageName;
        public final MediaFocusControl mController;
        public final PendingIntent mMediaIntent;
        public int mPlaybackStream;
        public int mPlaybackType;
        public int mPlaybackVolume;
        public int mPlaybackVolumeHandling;
        public int mPlaybackVolumeMax;
        public RcClientDeathHandler mRcClientDeathHandler;
        public int mRccId;
        public final ComponentName mReceiverComponent;
        public IRemoteVolumeObserver mRemoteVolumeObs;
        public IBinder mToken;
        public int mCallingUid = -1;
        public IRemoteControlClient mRcClient = null;
        public RccPlaybackState mPlaybackState = new RccPlaybackState(1, -1, 1.0f);

        public void resetPlaybackInfo() {
            this.mPlaybackType = 0;
            this.mPlaybackVolume = 15;
            this.mPlaybackVolumeMax = 15;
            this.mPlaybackVolumeHandling = 1;
            this.mPlaybackStream = 3;
            this.mPlaybackState.reset();
            this.mRemoteVolumeObs = null;
        }

        public RemoteControlStackEntry(MediaFocusControl mediaFocusControl, PendingIntent pendingIntent, ComponentName componentName, IBinder iBinder) {
            this.mRccId = -1;
            this.mController = mediaFocusControl;
            this.mMediaIntent = pendingIntent;
            this.mReceiverComponent = componentName;
            this.mToken = iBinder;
            this.mRccId = MediaFocusControl.access$3004();
            resetPlaybackInfo();
            IBinder iBinder2 = this.mToken;
            if (iBinder2 != null) {
                try {
                    iBinder2.linkToDeath(this, 0);
                } catch (RemoteException unused) {
                    this.mController.mEventHandler.post(new Runnable() { // from class: android.media.MediaFocusControl.RemoteControlStackEntry.1
                        @Override // java.lang.Runnable
                        public void run() {
                            RemoteControlStackEntry.this.mController.unregisterMediaButtonIntent(RemoteControlStackEntry.this.mMediaIntent);
                        }
                    });
                }
            }
        }

        public void unlinkToRcClientDeath() {
            RcClientDeathHandler rcClientDeathHandler = this.mRcClientDeathHandler;
            if (rcClientDeathHandler == null || rcClientDeathHandler.mCb == null) {
                return;
            }
            try {
                this.mRcClientDeathHandler.mCb.unlinkToDeath(this.mRcClientDeathHandler, 0);
                this.mRcClientDeathHandler = null;
            } catch (NoSuchElementException e) {
                Log.e(MediaFocusControl.TAG, "Encountered " + e + " in unlinkToRcClientDeath()");
                e.printStackTrace();
            }
        }

        public void destroy() {
            unlinkToRcClientDeath();
            IBinder iBinder = this.mToken;
            if (iBinder != null) {
                iBinder.unlinkToDeath(this, 0);
                this.mToken = null;
            }
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            this.mController.unregisterMediaButtonIntent(this.mMediaIntent);
        }

        protected void finalize() throws Throwable {
            destroy();
            super.finalize();
        }
    }

    private void dumpRCStack(PrintWriter printWriter) {
        printWriter.println("\nRemote Control stack entries (last is top of stack):");
        synchronized (this.mRCStack) {
            for (RemoteControlStackEntry remoteControlStackEntry : this.mRCStack) {
                printWriter.println("  pi: " + remoteControlStackEntry.mMediaIntent + " -- pack: " + remoteControlStackEntry.mCallingPackageName + "  -- ercvr: " + remoteControlStackEntry.mReceiverComponent + "  -- client: " + remoteControlStackEntry.mRcClient + "  -- uid: " + remoteControlStackEntry.mCallingUid + "  -- type: " + remoteControlStackEntry.mPlaybackType + "  state: " + remoteControlStackEntry.mPlaybackState);
            }
        }
    }

    private void dumpRCCStack(PrintWriter printWriter) {
        printWriter.println("\nRemote Control Client stack entries (last is top of stack):");
        synchronized (this.mRCStack) {
            for (RemoteControlStackEntry remoteControlStackEntry : this.mRCStack) {
                printWriter.println("  uid: " + remoteControlStackEntry.mCallingUid + "  -- id: " + remoteControlStackEntry.mRccId + "  -- type: " + remoteControlStackEntry.mPlaybackType + "  -- state: " + remoteControlStackEntry.mPlaybackState + "  -- vol handling: " + remoteControlStackEntry.mPlaybackVolumeHandling + "  -- vol: " + remoteControlStackEntry.mPlaybackVolume + "  -- volMax: " + remoteControlStackEntry.mPlaybackVolumeMax + "  -- volObs: " + remoteControlStackEntry.mRemoteVolumeObs);
            }
            synchronized (this.mCurrentRcLock) {
                printWriter.println("\nCurrent remote control generation ID = " + this.mCurrentRcClientGen);
            }
        }
        synchronized (this.mMainRemote) {
            printWriter.println("\nRemote Volume State:");
            printWriter.println("  has remote: " + this.mHasRemotePlayback);
            printWriter.println("  is remote active: " + this.mMainRemoteIsActive);
            printWriter.println("  rccId: " + this.mMainRemote.mRccId);
            printWriter.println("  volume handling: " + (this.mMainRemote.mVolumeHandling == 0 ? "PLAYBACK_VOLUME_FIXED(0)" : "PLAYBACK_VOLUME_VARIABLE(1)"));
            printWriter.println("  volume: " + this.mMainRemote.mVolume);
            printWriter.println("  volume steps: " + this.mMainRemote.mVolumeMax);
        }
    }

    private void dumpRCDList(PrintWriter printWriter) {
        printWriter.println("\nRemote Control Display list entries:");
        synchronized (this.mRCStack) {
            for (DisplayInfoForServer displayInfoForServer : this.mRcDisplays) {
                printWriter.println("  IRCD: " + displayInfoForServer.mRcDisplay + "  -- w:" + displayInfoForServer.mArtworkExpectedWidth + "  -- h:" + displayInfoForServer.mArtworkExpectedHeight + "  -- wantsPosSync:" + displayInfoForServer.mWantsPositionSync + "  -- " + (displayInfoForServer.mEnabled ? "enabled" : "disabled"));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void cleanupMediaButtonReceiverForPackage(String str, boolean z) {
        synchronized (this.mRCStack) {
            if (this.mRCStack.empty()) {
                return;
            }
            PackageManager packageManager = this.mContext.getPackageManager();
            RemoteControlStackEntry remoteControlStackEntryPeek = this.mRCStack.peek();
            Iterator<RemoteControlStackEntry> it = this.mRCStack.iterator();
            while (it.hasNext()) {
                RemoteControlStackEntry next = it.next();
                if (z && str.equals(next.mMediaIntent.getCreatorPackage())) {
                    it.remove();
                    next.destroy();
                } else if (next.mReceiverComponent != null) {
                    try {
                        packageManager.getReceiverInfo(next.mReceiverComponent, 0);
                    } catch (PackageManager.NameNotFoundException unused) {
                        it.remove();
                        next.destroy();
                    }
                }
            }
            if (this.mRCStack.empty()) {
                MediaEventHandler mediaEventHandler = this.mEventHandler;
                mediaEventHandler.sendMessage(mediaEventHandler.obtainMessage(0, 0, 0, null));
            } else if (remoteControlStackEntryPeek != this.mRCStack.peek()) {
                RemoteControlStackEntry remoteControlStackEntryPeek2 = this.mRCStack.peek();
                if (remoteControlStackEntryPeek2.mReceiverComponent != null) {
                    MediaEventHandler mediaEventHandler2 = this.mEventHandler;
                    mediaEventHandler2.sendMessage(mediaEventHandler2.obtainMessage(0, 0, 0, remoteControlStackEntryPeek2.mReceiverComponent));
                }
            }
        }
    }

    protected void restoreMediaButtonReceiver() {
        ComponentName componentNameUnflattenFromString;
        String stringForUser = Settings.System.getStringForUser(this.mContentResolver, Settings.System.MEDIA_BUTTON_RECEIVER, -2);
        if (stringForUser == null || stringForUser.isEmpty() || (componentNameUnflattenFromString = ComponentName.unflattenFromString(stringForUser)) == null) {
            return;
        }
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        intent.setComponent(componentNameUnflattenFromString);
        registerMediaButtonIntent(PendingIntent.getBroadcast(this.mContext, 0, intent, 0), componentNameUnflattenFromString, null);
    }

    private boolean pushMediaButtonReceiver_syncAfRcs(PendingIntent pendingIntent, ComponentName componentName, IBinder iBinder) {
        RemoteControlStackEntry remoteControlStackEntryElementAt;
        ArrayIndexOutOfBoundsException e;
        boolean z;
        int size;
        if ((!this.mRCStack.empty() && this.mRCStack.peek().mMediaIntent.equals(pendingIntent)) || this.mAppOps.noteOp(31, Binder.getCallingUid(), pendingIntent.getCreatorPackage()) != 0) {
            return false;
        }
        RemoteControlStackEntry remoteControlStackEntry = null;
        try {
            size = this.mRCStack.size() - 1;
        } catch (ArrayIndexOutOfBoundsException e2) {
            remoteControlStackEntryElementAt = remoteControlStackEntry;
            e = e2;
        }
        while (size >= 0) {
            remoteControlStackEntryElementAt = this.mRCStack.elementAt(size);
            try {
                if (remoteControlStackEntryElementAt.mMediaIntent.equals(pendingIntent)) {
                    this.mRCStack.removeElementAt(size);
                    z = true;
                } else {
                    size--;
                    remoteControlStackEntry = remoteControlStackEntryElementAt;
                }
            } catch (ArrayIndexOutOfBoundsException e3) {
                e = e3;
                Log.e(TAG, "Wrong index accessing media button stack, lock error? ", e);
                z = false;
            }
            remoteControlStackEntry = remoteControlStackEntryElementAt;
            break;
        }
        z = false;
        if (!z) {
            remoteControlStackEntry = new RemoteControlStackEntry(this, pendingIntent, componentName, iBinder);
        }
        this.mRCStack.push(remoteControlStackEntry);
        if (componentName != null) {
            MediaEventHandler mediaEventHandler = this.mEventHandler;
            mediaEventHandler.sendMessage(mediaEventHandler.obtainMessage(0, 0, 0, componentName));
        }
        return true;
    }

    private void removeMediaButtonReceiver_syncAfRcs(PendingIntent pendingIntent) {
        try {
            for (int size = this.mRCStack.size() - 1; size >= 0; size--) {
                RemoteControlStackEntry remoteControlStackEntryElementAt = this.mRCStack.elementAt(size);
                if (remoteControlStackEntryElementAt.mMediaIntent.equals(pendingIntent)) {
                    remoteControlStackEntryElementAt.destroy();
                    this.mRCStack.removeElementAt(size);
                    return;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e(TAG, "Wrong index accessing media button stack, lock error? ", e);
        }
    }

    private boolean isCurrentRcController(PendingIntent pendingIntent) {
        return !this.mRCStack.empty() && this.mRCStack.peek().mMediaIntent.equals(pendingIntent);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onHandlePersistMediaButtonReceiver(ComponentName componentName) {
        Settings.System.putStringForUser(this.mContentResolver, Settings.System.MEDIA_BUTTON_RECEIVER, componentName == null ? "" : componentName.flattenToString(), -2);
    }

    private void setNewRcClientOnDisplays_syncRcsCurrc(int i, PendingIntent pendingIntent, boolean z) {
        synchronized (this.mRCStack) {
            if (this.mRcDisplays.size() > 0) {
                Iterator<DisplayInfoForServer> it = this.mRcDisplays.iterator();
                while (it.hasNext()) {
                    DisplayInfoForServer next = it.next();
                    try {
                        next.mRcDisplay.setCurrentClientId(i, pendingIntent, z);
                    } catch (RemoteException e) {
                        Log.e(TAG, "Dead display in setNewRcClientOnDisplays_syncRcsCurrc()", e);
                        next.release();
                        it.remove();
                    }
                }
            }
        }
    }

    private void setNewRcClientGenerationOnClients_syncRcsCurrc(int i) {
        Iterator<RemoteControlStackEntry> it = this.mRCStack.iterator();
        while (it.hasNext()) {
            RemoteControlStackEntry next = it.next();
            if (next != null && next.mRcClient != null) {
                try {
                    next.mRcClient.setCurrentClientGenerationId(i);
                } catch (RemoteException e) {
                    Log.w(TAG, "Dead client in setNewRcClientGenerationOnClients_syncRcsCurrc()", e);
                    it.remove();
                    next.unlinkToRcClientDeath();
                }
            }
        }
    }

    private void setNewRcClient_syncRcsCurrc(int i, PendingIntent pendingIntent, boolean z) {
        setNewRcClientOnDisplays_syncRcsCurrc(i, pendingIntent, z);
        setNewRcClientGenerationOnClients_syncRcsCurrc(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onRcDisplayClear() {
        synchronized (this.mRCStack) {
            synchronized (this.mCurrentRcLock) {
                int i = this.mCurrentRcClientGen + 1;
                this.mCurrentRcClientGen = i;
                setNewRcClient_syncRcsCurrc(i, null, true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onRcDisplayUpdate(RemoteControlStackEntry remoteControlStackEntry, int i) {
        synchronized (this.mRCStack) {
            synchronized (this.mCurrentRcLock) {
                IRemoteControlClient iRemoteControlClient = this.mCurrentRcClient;
                if (iRemoteControlClient != null && iRemoteControlClient.equals(remoteControlStackEntry.mRcClient)) {
                    int i2 = this.mCurrentRcClientGen + 1;
                    this.mCurrentRcClientGen = i2;
                    setNewRcClient_syncRcsCurrc(i2, remoteControlStackEntry.mMediaIntent, false);
                    try {
                        this.mCurrentRcClient.onInformationRequested(this.mCurrentRcClientGen, i);
                    } catch (RemoteException e) {
                        Log.e(TAG, "Current valid remote client is dead: " + e);
                        this.mCurrentRcClient = null;
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onRcDisplayInitInfo(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2) {
        synchronized (this.mRCStack) {
            synchronized (this.mCurrentRcLock) {
                if (this.mCurrentRcClient != null) {
                    try {
                        iRemoteControlDisplay.setCurrentClientId(this.mCurrentRcClientGen, this.mCurrentRcClientIntent, false);
                        try {
                            this.mCurrentRcClient.informationRequestForDisplay(iRemoteControlDisplay, i, i2);
                        } catch (RemoteException e) {
                            Log.e(TAG, "Current valid remote client is dead: ", e);
                            this.mCurrentRcClient = null;
                        }
                    } catch (RemoteException e2) {
                        Log.e(TAG, "Dead display in onRcDisplayInitInfo()", e2);
                    }
                }
            }
        }
    }

    private void clearRemoteControlDisplay_syncAfRcs() {
        synchronized (this.mCurrentRcLock) {
            this.mCurrentRcClient = null;
        }
        MediaEventHandler mediaEventHandler = this.mEventHandler;
        mediaEventHandler.sendMessage(mediaEventHandler.obtainMessage(1));
    }

    private void updateRemoteControlDisplay_syncAfRcs(int i) {
        RemoteControlStackEntry remoteControlStackEntryPeek = this.mRCStack.peek();
        if (remoteControlStackEntryPeek.mRcClient == null) {
            clearRemoteControlDisplay_syncAfRcs();
            return;
        }
        synchronized (this.mCurrentRcLock) {
            if (!remoteControlStackEntryPeek.mRcClient.equals(this.mCurrentRcClient)) {
                i = 15;
            }
            this.mCurrentRcClient = remoteControlStackEntryPeek.mRcClient;
            this.mCurrentRcClientIntent = remoteControlStackEntryPeek.mMediaIntent;
        }
        MediaEventHandler mediaEventHandler = this.mEventHandler;
        mediaEventHandler.sendMessage(mediaEventHandler.obtainMessage(2, i, 0, remoteControlStackEntryPeek));
    }

    private void checkUpdateRemoteControlDisplay_syncAfRcs(int i) {
        if (this.mRCStack.isEmpty() || this.mFocusStack.isEmpty()) {
            clearRemoteControlDisplay_syncAfRcs();
            return;
        }
        FocusRequester focusRequester = null;
        try {
            for (int size = this.mFocusStack.size() - 1; size >= 0; size--) {
                FocusRequester focusRequesterElementAt = this.mFocusStack.elementAt(size);
                if (focusRequesterElementAt.getStreamType() == 3 || focusRequesterElementAt.getGainRequest() == 1) {
                    focusRequester = focusRequesterElementAt;
                    break;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e(TAG, "Wrong index accessing audio focus stack when updating RCD: " + e);
        }
        if (focusRequester == null) {
            clearRemoteControlDisplay_syncAfRcs();
            return;
        }
        if (!focusRequester.hasSamePackage(this.mRCStack.peek().mCallingPackageName)) {
            clearRemoteControlDisplay_syncAfRcs();
        } else if (!focusRequester.hasSameUid(this.mRCStack.peek().mCallingUid)) {
            clearRemoteControlDisplay_syncAfRcs();
        } else {
            updateRemoteControlDisplay_syncAfRcs(i);
        }
    }

    private void postPromoteRcc(int i) {
        sendMsg(this.mEventHandler, 6, 0, i, 0, null, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPromoteRcc(int i) {
        synchronized (mAudioFocusLock) {
            synchronized (this.mRCStack) {
                if (this.mRCStack.isEmpty() || this.mRCStack.peek().mRccId != i) {
                    try {
                        int size = this.mRCStack.size() - 1;
                        while (true) {
                            if (size < 0) {
                                size = -1;
                                break;
                            } else if (this.mRCStack.elementAt(size).mRccId == i) {
                                break;
                            } else {
                                size--;
                            }
                        }
                        if (size >= 0) {
                            this.mRCStack.push(this.mRCStack.remove(size));
                            checkUpdateRemoteControlDisplay_syncAfRcs(15);
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        Log.e(TAG, "Wrong index accessing RC stack, lock error? ", e);
                    }
                }
            }
        }
    }

    protected void registerMediaButtonIntent(PendingIntent pendingIntent, ComponentName componentName, IBinder iBinder) {
        Log.i(TAG, "  Remote Control   registerMediaButtonIntent() for " + pendingIntent);
        synchronized (mAudioFocusLock) {
            synchronized (this.mRCStack) {
                if (pushMediaButtonReceiver_syncAfRcs(pendingIntent, componentName, iBinder)) {
                    checkUpdateRemoteControlDisplay_syncAfRcs(15);
                }
            }
        }
    }

    protected void unregisterMediaButtonIntent(PendingIntent pendingIntent) {
        Log.i(TAG, "  Remote Control   unregisterMediaButtonIntent() for " + pendingIntent);
        synchronized (mAudioFocusLock) {
            synchronized (this.mRCStack) {
                boolean zIsCurrentRcController = isCurrentRcController(pendingIntent);
                removeMediaButtonReceiver_syncAfRcs(pendingIntent);
                if (zIsCurrentRcController) {
                    checkUpdateRemoteControlDisplay_syncAfRcs(15);
                }
            }
        }
    }

    protected void registerMediaButtonEventReceiverForCalls(ComponentName componentName) {
        if (this.mContext.checkCallingPermission(Manifest.permission.MODIFY_PHONE_STATE) != 0) {
            Log.e(TAG, "Invalid permissions to register media button receiver for calls");
            return;
        }
        synchronized (this.mRCStack) {
            this.mMediaReceiverForCalls = componentName;
        }
    }

    protected void unregisterMediaButtonEventReceiverForCalls() {
        if (this.mContext.checkCallingPermission(Manifest.permission.MODIFY_PHONE_STATE) != 0) {
            Log.e(TAG, "Invalid permissions to unregister media button receiver for calls");
            return;
        }
        synchronized (this.mRCStack) {
            this.mMediaReceiverForCalls = null;
        }
    }

    protected int registerRemoteControlClient(PendingIntent pendingIntent, IRemoteControlClient iRemoteControlClient, String str) {
        int i;
        synchronized (mAudioFocusLock) {
            synchronized (this.mRCStack) {
                i = -1;
                try {
                    int size = this.mRCStack.size() - 1;
                    while (true) {
                        if (size < 0) {
                            break;
                        }
                        RemoteControlStackEntry remoteControlStackEntryElementAt = this.mRCStack.elementAt(size);
                        if (remoteControlStackEntryElementAt.mMediaIntent.equals(pendingIntent)) {
                            if (remoteControlStackEntryElementAt.mRcClientDeathHandler != null) {
                                remoteControlStackEntryElementAt.unlinkToRcClientDeath();
                            }
                            remoteControlStackEntryElementAt.mRcClient = iRemoteControlClient;
                            remoteControlStackEntryElementAt.mCallingPackageName = str;
                            remoteControlStackEntryElementAt.mCallingUid = Binder.getCallingUid();
                            if (iRemoteControlClient == null) {
                                remoteControlStackEntryElementAt.resetPlaybackInfo();
                            } else {
                                i = remoteControlStackEntryElementAt.mRccId;
                                if (this.mRcDisplays.size() > 0) {
                                    plugRemoteControlDisplaysIntoClient_syncRcStack(remoteControlStackEntryElementAt.mRcClient);
                                }
                                IBinder iBinderAsBinder = remoteControlStackEntryElementAt.mRcClient.asBinder();
                                RcClientDeathHandler rcClientDeathHandler = new RcClientDeathHandler(iBinderAsBinder, remoteControlStackEntryElementAt.mMediaIntent);
                                try {
                                    iBinderAsBinder.linkToDeath(rcClientDeathHandler, 0);
                                } catch (RemoteException unused) {
                                    Log.w(TAG, "registerRemoteControlClient() has a dead client " + iBinderAsBinder);
                                    remoteControlStackEntryElementAt.mRcClient = null;
                                }
                                remoteControlStackEntryElementAt.mRcClientDeathHandler = rcClientDeathHandler;
                            }
                        } else {
                            size--;
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.e(TAG, "Wrong index accessing RC stack, lock error? ", e);
                }
                if (isCurrentRcController(pendingIntent)) {
                    checkUpdateRemoteControlDisplay_syncAfRcs(15);
                }
            }
        }
        return i;
    }

    /* JADX WARN: Code restructure failed: missing block: B:13:0x0029, code lost:
    
        r5.unlinkToRcClientDeath();
        r5.mRcClient = null;
        r5.mCallingPackageName = null;
     */
    /* JADX WARN: Code restructure failed: missing block: B:15:0x0038, code lost:
    
        if (r3 != (r7.mRCStack.size() - 1)) goto L22;
     */
    /* JADX WARN: Code restructure failed: missing block: B:16:0x003a, code lost:
    
        r2 = true;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void unregisterRemoteControlClient(android.app.PendingIntent r8, android.media.IRemoteControlClient r9) {
        /*
            r7 = this;
            java.lang.Object r0 = android.media.MediaFocusControl.mAudioFocusLock
            monitor-enter(r0)
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r1 = r7.mRCStack     // Catch: java.lang.Throwable -> L55
            monitor-enter(r1)     // Catch: java.lang.Throwable -> L55
            r2 = 0
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r3 = r7.mRCStack     // Catch: java.lang.Throwable -> L3f java.lang.ArrayIndexOutOfBoundsException -> L41
            int r3 = r3.size()     // Catch: java.lang.Throwable -> L3f java.lang.ArrayIndexOutOfBoundsException -> L41
            r4 = 1
            int r3 = r3 - r4
        Lf:
            if (r3 < 0) goto L49
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r5 = r7.mRCStack     // Catch: java.lang.Throwable -> L3f java.lang.ArrayIndexOutOfBoundsException -> L41
            java.lang.Object r5 = r5.elementAt(r3)     // Catch: java.lang.Throwable -> L3f java.lang.ArrayIndexOutOfBoundsException -> L41
            android.media.MediaFocusControl$RemoteControlStackEntry r5 = (android.media.MediaFocusControl.RemoteControlStackEntry) r5     // Catch: java.lang.Throwable -> L3f java.lang.ArrayIndexOutOfBoundsException -> L41
            android.app.PendingIntent r6 = r5.mMediaIntent     // Catch: java.lang.Throwable -> L3f java.lang.ArrayIndexOutOfBoundsException -> L41
            boolean r6 = r6.equals(r8)     // Catch: java.lang.Throwable -> L3f java.lang.ArrayIndexOutOfBoundsException -> L41
            if (r6 == 0) goto L3c
            android.media.IRemoteControlClient r6 = r5.mRcClient     // Catch: java.lang.Throwable -> L3f java.lang.ArrayIndexOutOfBoundsException -> L41
            boolean r6 = r9.equals(r6)     // Catch: java.lang.Throwable -> L3f java.lang.ArrayIndexOutOfBoundsException -> L41
            if (r6 == 0) goto L3c
            r5.unlinkToRcClientDeath()     // Catch: java.lang.Throwable -> L3f java.lang.ArrayIndexOutOfBoundsException -> L41
            r8 = 0
            r5.mRcClient = r8     // Catch: java.lang.Throwable -> L3f java.lang.ArrayIndexOutOfBoundsException -> L41
            r5.mCallingPackageName = r8     // Catch: java.lang.Throwable -> L3f java.lang.ArrayIndexOutOfBoundsException -> L41
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r8 = r7.mRCStack     // Catch: java.lang.Throwable -> L3f java.lang.ArrayIndexOutOfBoundsException -> L41
            int r8 = r8.size()     // Catch: java.lang.Throwable -> L3f java.lang.ArrayIndexOutOfBoundsException -> L41
            int r8 = r8 - r4
            if (r3 != r8) goto L49
            r2 = r4
            goto L49
        L3c:
            int r3 = r3 + (-1)
            goto Lf
        L3f:
            r8 = move-exception
            goto L53
        L41:
            r8 = move-exception
            java.lang.String r9 = "MediaFocusControl"
            java.lang.String r3 = "Wrong index accessing RC stack, lock error? "
            android.util.Log.e(r9, r3, r8)     // Catch: java.lang.Throwable -> L3f
        L49:
            if (r2 == 0) goto L50
            r8 = 15
            r7.checkUpdateRemoteControlDisplay_syncAfRcs(r8)     // Catch: java.lang.Throwable -> L3f
        L50:
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L3f
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L55
            return
        L53:
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L3f
            throw r8     // Catch: java.lang.Throwable -> L55
        L55:
            r8 = move-exception
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L55
            throw r8
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.MediaFocusControl.unregisterRemoteControlClient(android.app.PendingIntent, android.media.IRemoteControlClient):void");
    }

    private class DisplayInfoForServer implements IBinder.DeathRecipient {
        private int mArtworkExpectedHeight;
        private int mArtworkExpectedWidth;
        private ComponentName mClientNotifListComp;
        private final IRemoteControlDisplay mRcDisplay;
        private final IBinder mRcDisplayBinder;
        private boolean mWantsPositionSync = false;
        private boolean mEnabled = true;

        public DisplayInfoForServer(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2) {
            this.mArtworkExpectedWidth = -1;
            this.mArtworkExpectedHeight = -1;
            this.mRcDisplay = iRemoteControlDisplay;
            this.mRcDisplayBinder = iRemoteControlDisplay.asBinder();
            this.mArtworkExpectedWidth = i;
            this.mArtworkExpectedHeight = i2;
        }

        public boolean init() {
            try {
                this.mRcDisplayBinder.linkToDeath(this, 0);
                return true;
            } catch (RemoteException unused) {
                Log.w(MediaFocusControl.TAG, "registerRemoteControlDisplay() has a dead client " + this.mRcDisplayBinder);
                return false;
            }
        }

        public void release() {
            try {
                this.mRcDisplayBinder.unlinkToDeath(this, 0);
            } catch (NoSuchElementException e) {
                Log.e(MediaFocusControl.TAG, "Error in DisplaInfoForServer.relase()", e);
            }
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            synchronized (MediaFocusControl.this.mRCStack) {
                Log.w(MediaFocusControl.TAG, "RemoteControl: display " + this.mRcDisplay + " died");
                Iterator it = MediaFocusControl.this.mRcDisplays.iterator();
                while (it.hasNext()) {
                    if (((DisplayInfoForServer) it.next()).mRcDisplay == this.mRcDisplay) {
                        it.remove();
                        return;
                    }
                }
            }
        }
    }

    private void plugRemoteControlDisplaysIntoClient_syncRcStack(IRemoteControlClient iRemoteControlClient) {
        for (DisplayInfoForServer displayInfoForServer : this.mRcDisplays) {
            try {
                iRemoteControlClient.plugRemoteControlDisplay(displayInfoForServer.mRcDisplay, displayInfoForServer.mArtworkExpectedWidth, displayInfoForServer.mArtworkExpectedHeight);
                if (displayInfoForServer.mWantsPositionSync) {
                    iRemoteControlClient.setWantsSyncForDisplay(displayInfoForServer.mRcDisplay, true);
                }
            } catch (RemoteException e) {
                Log.e(TAG, "Error connecting RCD to RCC in RCC registration", e);
            }
        }
    }

    private void enableRemoteControlDisplayForClient_syncRcStack(IRemoteControlDisplay iRemoteControlDisplay, boolean z) {
        for (RemoteControlStackEntry remoteControlStackEntry : this.mRCStack) {
            if (remoteControlStackEntry.mRcClient != null) {
                try {
                    remoteControlStackEntry.mRcClient.enableRemoteControlDisplay(iRemoteControlDisplay, z);
                } catch (RemoteException e) {
                    Log.e(TAG, "Error connecting RCD to client: ", e);
                }
            }
        }
    }

    private boolean rcDisplayIsPluggedIn_syncRcStack(IRemoteControlDisplay iRemoteControlDisplay) {
        Iterator<DisplayInfoForServer> it = this.mRcDisplays.iterator();
        while (it.hasNext()) {
            if (it.next().mRcDisplay.asBinder().equals(iRemoteControlDisplay.asBinder())) {
                return true;
            }
        }
        return false;
    }

    private void registerRemoteControlDisplay_int(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2, ComponentName componentName) {
        synchronized (mAudioFocusLock) {
            synchronized (this.mRCStack) {
                if (iRemoteControlDisplay != null) {
                    if (!rcDisplayIsPluggedIn_syncRcStack(iRemoteControlDisplay)) {
                        DisplayInfoForServer displayInfoForServer = new DisplayInfoForServer(iRemoteControlDisplay, i, i2);
                        displayInfoForServer.mEnabled = true;
                        displayInfoForServer.mClientNotifListComp = componentName;
                        if (displayInfoForServer.init()) {
                            this.mRcDisplays.add(displayInfoForServer);
                            for (RemoteControlStackEntry remoteControlStackEntry : this.mRCStack) {
                                if (remoteControlStackEntry.mRcClient != null) {
                                    try {
                                        remoteControlStackEntry.mRcClient.plugRemoteControlDisplay(iRemoteControlDisplay, i, i2);
                                    } catch (RemoteException e) {
                                        Log.e(TAG, "Error connecting RCD to client: ", e);
                                    }
                                }
                            }
                            sendMsg(this.mEventHandler, 10, 2, i, i2, iRemoteControlDisplay, 0);
                        }
                    }
                }
            }
        }
    }

    protected void unregisterRemoteControlDisplay(IRemoteControlDisplay iRemoteControlDisplay) {
        synchronized (this.mRCStack) {
            if (iRemoteControlDisplay == null) {
                return;
            }
            boolean z = false;
            Iterator<DisplayInfoForServer> it = this.mRcDisplays.iterator();
            while (it.hasNext() && !z) {
                DisplayInfoForServer next = it.next();
                if (next.mRcDisplay.asBinder().equals(iRemoteControlDisplay.asBinder())) {
                    z = true;
                    next.release();
                    it.remove();
                }
            }
            if (z) {
                for (RemoteControlStackEntry remoteControlStackEntry : this.mRCStack) {
                    if (remoteControlStackEntry.mRcClient != null) {
                        try {
                            remoteControlStackEntry.mRcClient.unplugRemoteControlDisplay(iRemoteControlDisplay);
                        } catch (RemoteException e) {
                            Log.e(TAG, "Error disconnecting remote control display to client: ", e);
                        }
                    }
                }
            }
        }
    }

    protected void remoteControlDisplayUsesBitmapSize(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2) {
        synchronized (this.mRCStack) {
            Iterator<DisplayInfoForServer> it = this.mRcDisplays.iterator();
            boolean z = false;
            while (it.hasNext() && !z) {
                DisplayInfoForServer next = it.next();
                if (next.mRcDisplay.asBinder().equals(iRemoteControlDisplay.asBinder()) && (next.mArtworkExpectedWidth != i || next.mArtworkExpectedHeight != i2)) {
                    next.mArtworkExpectedWidth = i;
                    next.mArtworkExpectedHeight = i2;
                    z = true;
                }
            }
            if (z) {
                for (RemoteControlStackEntry remoteControlStackEntry : this.mRCStack) {
                    if (remoteControlStackEntry.mRcClient != null) {
                        try {
                            remoteControlStackEntry.mRcClient.setBitmapSizeForDisplay(iRemoteControlDisplay, i, i2);
                        } catch (RemoteException e) {
                            Log.e(TAG, "Error setting bitmap size for RCD on RCC: ", e);
                        }
                    }
                }
            }
        }
    }

    protected void remoteControlDisplayWantsPlaybackPositionSync(IRemoteControlDisplay iRemoteControlDisplay, boolean z) {
        synchronized (this.mRCStack) {
            boolean z2 = false;
            Iterator<DisplayInfoForServer> it = this.mRcDisplays.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                DisplayInfoForServer next = it.next();
                if (next.mRcDisplay.asBinder().equals(iRemoteControlDisplay.asBinder())) {
                    next.mWantsPositionSync = z;
                    z2 = true;
                    break;
                }
            }
            if (z2) {
                for (RemoteControlStackEntry remoteControlStackEntry : this.mRCStack) {
                    if (remoteControlStackEntry.mRcClient != null) {
                        try {
                            remoteControlStackEntry.mRcClient.setWantsSyncForDisplay(iRemoteControlDisplay, z);
                        } catch (RemoteException e) {
                            Log.e(TAG, "Error setting position sync flag for RCD on RCC: ", e);
                        }
                    }
                }
            }
        }
    }

    protected void setRemoteControlClientPlaybackPosition(int i, long j) {
        synchronized (this.mRCStack) {
            synchronized (this.mCurrentRcLock) {
                if (this.mCurrentRcClientGen != i) {
                    return;
                }
                sendMsg(this.mEventHandler, 8, 0, i, 0, new Long(j), 0);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSetRemoteControlClientPlaybackPosition(int i, long j) {
        synchronized (this.mRCStack) {
            synchronized (this.mCurrentRcLock) {
                IRemoteControlClient iRemoteControlClient = this.mCurrentRcClient;
                if (iRemoteControlClient != null && this.mCurrentRcClientGen == i) {
                    try {
                        iRemoteControlClient.seekTo(i, j);
                    } catch (RemoteException e) {
                        Log.e(TAG, "Current valid remote client is dead: " + e);
                        this.mCurrentRcClient = null;
                    }
                }
            }
        }
    }

    protected void updateRemoteControlClientMetadata(int i, int i2, Rating rating) {
        sendMsg(this.mEventHandler, 9, 2, i, i2, rating, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUpdateRemoteControlClientMetadata(int i, int i2, Rating rating) {
        synchronized (this.mRCStack) {
            synchronized (this.mCurrentRcLock) {
                IRemoteControlClient iRemoteControlClient = this.mCurrentRcClient;
                if (iRemoteControlClient != null && this.mCurrentRcClientGen == i) {
                    try {
                        if (i2 == 268435457) {
                            iRemoteControlClient.updateMetadata(i, i2, rating);
                        } else {
                            Log.e(TAG, "unhandled metadata key " + i2 + " update for RCC " + i);
                        }
                    } catch (RemoteException e) {
                        Log.e(TAG, "Current valid remote client is dead", e);
                        this.mCurrentRcClient = null;
                    }
                }
            }
        }
    }

    protected void setPlaybackInfoForRcc(int i, int i2, int i3) {
        sendMsg(this.mEventHandler, 4, 2, i, i2, Integer.valueOf(i3), 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNewPlaybackInfoForRcc(int i, int i2, int i3) {
        synchronized (this.mRCStack) {
            try {
                try {
                    for (int size = this.mRCStack.size() - 1; size >= 0; size--) {
                        RemoteControlStackEntry remoteControlStackEntryElementAt = this.mRCStack.elementAt(size);
                        if (remoteControlStackEntryElementAt.mRccId == i) {
                            if (i2 == 1) {
                                remoteControlStackEntryElementAt.mPlaybackType = i3;
                                postReevaluateRemote();
                            } else if (i2 == 2) {
                                remoteControlStackEntryElementAt.mPlaybackVolume = i3;
                                synchronized (this.mMainRemote) {
                                    if (i == this.mMainRemote.mRccId) {
                                        this.mMainRemote.mVolume = i3;
                                        this.mVolumeController.postHasNewRemotePlaybackInfo();
                                    }
                                }
                            } else if (i2 == 3) {
                                remoteControlStackEntryElementAt.mPlaybackVolumeMax = i3;
                                synchronized (this.mMainRemote) {
                                    if (i == this.mMainRemote.mRccId) {
                                        this.mMainRemote.mVolumeMax = i3;
                                        this.mVolumeController.postHasNewRemotePlaybackInfo();
                                    }
                                }
                            } else if (i2 == 4) {
                                remoteControlStackEntryElementAt.mPlaybackVolumeHandling = i3;
                                synchronized (this.mMainRemote) {
                                    if (i == this.mMainRemote.mRccId) {
                                        this.mMainRemote.mVolumeHandling = i3;
                                        this.mVolumeController.postHasNewRemotePlaybackInfo();
                                    }
                                }
                            } else if (i2 == 5) {
                                remoteControlStackEntryElementAt.mPlaybackStream = i3;
                            } else {
                                Log.e(TAG, "unhandled key " + i2 + " for RCC " + i);
                            }
                            return;
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    Log.e(TAG, "Wrong index mRCStack on onNewPlaybackInfoForRcc, lock error? ", e);
                }
            } finally {
            }
        }
    }

    protected void setPlaybackStateForRcc(int i, int i2, long j, float f) {
        sendMsg(this.mEventHandler, 7, 2, i, i2, new RccPlaybackState(i2, j, f), 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNewPlaybackStateForRcc(int i, int i2, RccPlaybackState rccPlaybackState) {
        synchronized (this.mRCStack) {
            try {
                for (int size = this.mRCStack.size() - 1; size >= 0; size--) {
                    RemoteControlStackEntry remoteControlStackEntryElementAt = this.mRCStack.elementAt(size);
                    if (remoteControlStackEntryElementAt.mRccId == i) {
                        remoteControlStackEntryElementAt.mPlaybackState = rccPlaybackState;
                        synchronized (this.mMainRemote) {
                            if (i == this.mMainRemote.mRccId) {
                                this.mMainRemoteIsActive = isPlaystateActive(i2);
                                postReevaluateRemote();
                            }
                        }
                        if (isPlaystateActive(i2)) {
                            postPromoteRcc(i);
                        }
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e(TAG, "Wrong index on mRCStack in onNewPlaybackStateForRcc, lock error? ", e);
            }
        }
    }

    protected void registerRemoteVolumeObserverForRcc(int i, IRemoteVolumeObserver iRemoteVolumeObserver) {
        sendMsg(this.mEventHandler, 5, 2, i, 0, iRemoteVolumeObserver, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Code restructure failed: missing block: B:8:0x0019, code lost:
    
        r2.mRemoteVolumeObs = r6;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void onRegisterVolumeObserverForRcc(int r5, android.media.IRemoteVolumeObserver r6) {
        /*
            r4 = this;
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r0 = r4.mRCStack
            monitor-enter(r0)
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r1 = r4.mRCStack     // Catch: java.lang.Throwable -> L1f java.lang.ArrayIndexOutOfBoundsException -> L21
            int r1 = r1.size()     // Catch: java.lang.Throwable -> L1f java.lang.ArrayIndexOutOfBoundsException -> L21
            int r1 = r1 + (-1)
        Lb:
            if (r1 < 0) goto L29
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r2 = r4.mRCStack     // Catch: java.lang.Throwable -> L1f java.lang.ArrayIndexOutOfBoundsException -> L21
            java.lang.Object r2 = r2.elementAt(r1)     // Catch: java.lang.Throwable -> L1f java.lang.ArrayIndexOutOfBoundsException -> L21
            android.media.MediaFocusControl$RemoteControlStackEntry r2 = (android.media.MediaFocusControl.RemoteControlStackEntry) r2     // Catch: java.lang.Throwable -> L1f java.lang.ArrayIndexOutOfBoundsException -> L21
            int r3 = r2.mRccId     // Catch: java.lang.Throwable -> L1f java.lang.ArrayIndexOutOfBoundsException -> L21
            if (r3 != r5) goto L1c
            r2.mRemoteVolumeObs = r6     // Catch: java.lang.Throwable -> L1f java.lang.ArrayIndexOutOfBoundsException -> L21
            goto L29
        L1c:
            int r1 = r1 + (-1)
            goto Lb
        L1f:
            r5 = move-exception
            goto L2b
        L21:
            r5 = move-exception
            java.lang.String r6 = "MediaFocusControl"
            java.lang.String r1 = "Wrong index accessing media button stack, lock error? "
            android.util.Log.e(r6, r1, r5)     // Catch: java.lang.Throwable -> L1f
        L29:
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L1f
            return
        L2b:
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L1f
            throw r5
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.MediaFocusControl.onRegisterVolumeObserverForRcc(int, android.media.IRemoteVolumeObserver):void");
    }

    protected boolean checkUpdateRemoteStateIfActive(int i) {
        synchronized (this.mRCStack) {
            try {
                for (int size = this.mRCStack.size() - 1; size >= 0; size--) {
                    RemoteControlStackEntry remoteControlStackEntryElementAt = this.mRCStack.elementAt(size);
                    if (remoteControlStackEntryElementAt.mPlaybackType == 1 && isPlaystateActive(remoteControlStackEntryElementAt.mPlaybackState.mState) && remoteControlStackEntryElementAt.mPlaybackStream == i) {
                        synchronized (this.mMainRemote) {
                            this.mMainRemote.mRccId = remoteControlStackEntryElementAt.mRccId;
                            this.mMainRemote.mVolume = remoteControlStackEntryElementAt.mPlaybackVolume;
                            this.mMainRemote.mVolumeMax = remoteControlStackEntryElementAt.mPlaybackVolumeMax;
                            this.mMainRemote.mVolumeHandling = remoteControlStackEntryElementAt.mPlaybackVolumeHandling;
                            this.mMainRemoteIsActive = true;
                        }
                        return true;
                    }
                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Log.e(TAG, "Wrong index accessing RC stack, lock error? ", e);
            }
            synchronized (this.mMainRemote) {
                this.mMainRemoteIsActive = false;
            }
            return false;
        }
    }

    protected void adjustRemoteVolume(int i, int i2, int i3) {
        synchronized (this.mMainRemote) {
            if (this.mMainRemoteIsActive) {
                int i4 = this.mMainRemote.mRccId;
                boolean z = this.mMainRemote.mVolumeHandling == 0;
                if (!z) {
                    sendVolumeUpdateToRemote(i4, i2);
                }
                this.mVolumeController.postRemoteVolumeChanged(i, i3);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:11:0x001f, code lost:
    
        r0 = r3.mRemoteVolumeObs;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void sendVolumeUpdateToRemote(int r6, int r7) {
        /*
            r5 = this;
            if (r7 != 0) goto L3
            return
        L3:
            r0 = 0
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r1 = r5.mRCStack
            monitor-enter(r1)
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r2 = r5.mRCStack     // Catch: java.lang.Throwable -> L24 java.lang.ArrayIndexOutOfBoundsException -> L26
            int r2 = r2.size()     // Catch: java.lang.Throwable -> L24 java.lang.ArrayIndexOutOfBoundsException -> L26
            int r2 = r2 + (-1)
        Lf:
            if (r2 < 0) goto L2e
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r3 = r5.mRCStack     // Catch: java.lang.Throwable -> L24 java.lang.ArrayIndexOutOfBoundsException -> L26
            java.lang.Object r3 = r3.elementAt(r2)     // Catch: java.lang.Throwable -> L24 java.lang.ArrayIndexOutOfBoundsException -> L26
            android.media.MediaFocusControl$RemoteControlStackEntry r3 = (android.media.MediaFocusControl.RemoteControlStackEntry) r3     // Catch: java.lang.Throwable -> L24 java.lang.ArrayIndexOutOfBoundsException -> L26
            int r4 = r3.mRccId     // Catch: java.lang.Throwable -> L24 java.lang.ArrayIndexOutOfBoundsException -> L26
            if (r4 != r6) goto L21
            android.media.IRemoteVolumeObserver r6 = r3.mRemoteVolumeObs     // Catch: java.lang.Throwable -> L24 java.lang.ArrayIndexOutOfBoundsException -> L26
            r0 = r6
            goto L2e
        L21:
            int r2 = r2 + (-1)
            goto Lf
        L24:
            r6 = move-exception
            goto L3f
        L26:
            r6 = move-exception
            java.lang.String r2 = "MediaFocusControl"
            java.lang.String r3 = "Wrong index accessing media button stack, lock error? "
            android.util.Log.e(r2, r3, r6)     // Catch: java.lang.Throwable -> L24
        L2e:
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L24
            if (r0 == 0) goto L3e
            r6 = -1
            r0.dispatchRemoteVolumeUpdate(r7, r6)     // Catch: android.os.RemoteException -> L36
            goto L3e
        L36:
            r6 = move-exception
            java.lang.String r7 = "MediaFocusControl"
            java.lang.String r0 = "Error dispatching relative volume update"
            android.util.Log.e(r7, r0, r6)
        L3e:
            return
        L3f:
            monitor-exit(r1)     // Catch: java.lang.Throwable -> L24
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.MediaFocusControl.sendVolumeUpdateToRemote(int, int):void");
    }

    protected int getRemoteStreamMaxVolume() {
        synchronized (this.mMainRemote) {
            if (this.mMainRemote.mRccId == -1) {
                return 0;
            }
            return this.mMainRemote.mVolumeMax;
        }
    }

    protected int getRemoteStreamVolume() {
        synchronized (this.mMainRemote) {
            if (this.mMainRemote.mRccId == -1) {
                return 0;
            }
            return this.mMainRemote.mVolume;
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:16:0x002b, code lost:
    
        r0 = r4.mRemoteVolumeObs;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void setRemoteStreamVolume(int r7) {
        /*
            r6 = this;
            android.media.MediaFocusControl$RemotePlaybackState r0 = r6.mMainRemote
            monitor-enter(r0)
            android.media.MediaFocusControl$RemotePlaybackState r1 = r6.mMainRemote     // Catch: java.lang.Throwable -> L4e
            int r1 = r1.mRccId     // Catch: java.lang.Throwable -> L4e
            r2 = -1
            if (r1 != r2) goto Lc
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L4e
            return
        Lc:
            android.media.MediaFocusControl$RemotePlaybackState r1 = r6.mMainRemote     // Catch: java.lang.Throwable -> L4e
            int r1 = r1.mRccId     // Catch: java.lang.Throwable -> L4e
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L4e
            r0 = 0
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r2 = r6.mRCStack
            monitor-enter(r2)
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r3 = r6.mRCStack     // Catch: java.lang.Throwable -> L31 java.lang.ArrayIndexOutOfBoundsException -> L33
            int r3 = r3.size()     // Catch: java.lang.Throwable -> L31 java.lang.ArrayIndexOutOfBoundsException -> L33
            int r3 = r3 + (-1)
        L1d:
            if (r3 < 0) goto L3b
            java.util.Stack<android.media.MediaFocusControl$RemoteControlStackEntry> r4 = r6.mRCStack     // Catch: java.lang.Throwable -> L31 java.lang.ArrayIndexOutOfBoundsException -> L33
            java.lang.Object r4 = r4.elementAt(r3)     // Catch: java.lang.Throwable -> L31 java.lang.ArrayIndexOutOfBoundsException -> L33
            android.media.MediaFocusControl$RemoteControlStackEntry r4 = (android.media.MediaFocusControl.RemoteControlStackEntry) r4     // Catch: java.lang.Throwable -> L31 java.lang.ArrayIndexOutOfBoundsException -> L33
            int r5 = r4.mRccId     // Catch: java.lang.Throwable -> L31 java.lang.ArrayIndexOutOfBoundsException -> L33
            if (r5 != r1) goto L2e
            android.media.IRemoteVolumeObserver r0 = r4.mRemoteVolumeObs     // Catch: java.lang.Throwable -> L31 java.lang.ArrayIndexOutOfBoundsException -> L33
            goto L3b
        L2e:
            int r3 = r3 + (-1)
            goto L1d
        L31:
            r7 = move-exception
            goto L4c
        L33:
            r1 = move-exception
            java.lang.String r3 = "MediaFocusControl"
            java.lang.String r4 = "Wrong index accessing media button stack, lock error? "
            android.util.Log.e(r3, r4, r1)     // Catch: java.lang.Throwable -> L31
        L3b:
            monitor-exit(r2)     // Catch: java.lang.Throwable -> L31
            if (r0 == 0) goto L4b
            r1 = 0
            r0.dispatchRemoteVolumeUpdate(r1, r7)     // Catch: android.os.RemoteException -> L43
            goto L4b
        L43:
            r7 = move-exception
            java.lang.String r0 = "MediaFocusControl"
            java.lang.String r1 = "Error dispatching absolute volume update"
            android.util.Log.e(r0, r1, r7)
        L4b:
            return
        L4c:
            monitor-exit(r2)     // Catch: java.lang.Throwable -> L31
            throw r7
        L4e:
            r7 = move-exception
            monitor-exit(r0)     // Catch: java.lang.Throwable -> L4e
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.MediaFocusControl.setRemoteStreamVolume(int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void postReevaluateRemote() {
        sendMsg(this.mEventHandler, 3, 2, 0, 0, null, 0);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onReevaluateRemote() {
        boolean z;
        synchronized (this.mRCStack) {
            Iterator<RemoteControlStackEntry> it = this.mRCStack.iterator();
            while (true) {
                z = true;
                if (!it.hasNext()) {
                    z = false;
                    break;
                } else if (it.next().mPlaybackType == 1) {
                    break;
                }
            }
        }
        synchronized (this.mMainRemote) {
            if (this.mHasRemotePlayback != z) {
                this.mHasRemotePlayback = z;
                this.mVolumeController.postRemoteSliderVisibility(z);
            }
        }
    }
}
