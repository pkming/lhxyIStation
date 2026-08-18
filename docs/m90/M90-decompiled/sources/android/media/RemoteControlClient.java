package android.media;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.IAudioService;
import android.media.IRemoteControlClient;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.util.Log;
import java.util.ArrayList;
import java.util.Iterator;

/* JADX INFO: loaded from: classes.dex */
public class RemoteControlClient {
    private static final boolean DEBUG = false;
    public static final int DEFAULT_PLAYBACK_VOLUME = 15;
    public static final int DEFAULT_PLAYBACK_VOLUME_HANDLING = 1;
    public static final int FLAGS_KEY_MEDIA_NONE = 0;
    public static final int FLAG_INFORMATION_REQUEST_ALBUM_ART = 8;
    public static final int FLAG_INFORMATION_REQUEST_KEY_MEDIA = 2;
    public static final int FLAG_INFORMATION_REQUEST_METADATA = 1;
    public static final int FLAG_INFORMATION_REQUEST_PLAYSTATE = 4;
    public static final int FLAG_KEY_MEDIA_FAST_FORWARD = 64;
    public static final int FLAG_KEY_MEDIA_NEXT = 128;
    public static final int FLAG_KEY_MEDIA_PAUSE = 16;
    public static final int FLAG_KEY_MEDIA_PLAY = 4;
    public static final int FLAG_KEY_MEDIA_PLAY_PAUSE = 8;
    public static final int FLAG_KEY_MEDIA_POSITION_UPDATE = 256;
    public static final int FLAG_KEY_MEDIA_PREVIOUS = 1;
    public static final int FLAG_KEY_MEDIA_RATING = 512;
    public static final int FLAG_KEY_MEDIA_REWIND = 2;
    public static final int FLAG_KEY_MEDIA_STOP = 32;
    public static int MEDIA_POSITION_READABLE = 1;
    public static int MEDIA_POSITION_WRITABLE = 2;
    private static final int MSG_DISPLAY_ENABLE = 15;
    private static final int MSG_DISPLAY_WANTS_POS_SYNC = 12;
    private static final int MSG_NEW_CURRENT_CLIENT_GEN = 6;
    private static final int MSG_NEW_INTERNAL_CLIENT_GEN = 5;
    private static final int MSG_PLUG_DISPLAY = 7;
    private static final int MSG_POSITION_DRIFT_CHECK = 11;
    private static final int MSG_REQUEST_ARTWORK = 4;
    private static final int MSG_REQUEST_METADATA = 2;
    private static final int MSG_REQUEST_METADATA_ARTWORK = 14;
    private static final int MSG_REQUEST_PLAYBACK_STATE = 1;
    private static final int MSG_REQUEST_TRANSPORTCONTROL = 3;
    private static final int MSG_SEEK_TO = 10;
    private static final int MSG_UNPLUG_DISPLAY = 8;
    private static final int MSG_UPDATE_DISPLAY_ARTWORK_SIZE = 9;
    private static final int MSG_UPDATE_METADATA = 13;
    public static final int PLAYBACKINFO_INVALID_VALUE = Integer.MIN_VALUE;
    public static final int PLAYBACKINFO_PLAYBACK_TYPE = 1;
    public static final int PLAYBACKINFO_USES_STREAM = 5;
    public static final int PLAYBACKINFO_VOLUME = 2;
    public static final int PLAYBACKINFO_VOLUME_HANDLING = 4;
    public static final int PLAYBACKINFO_VOLUME_MAX = 3;
    public static final long PLAYBACK_POSITION_ALWAYS_UNKNOWN = -9216204211029966080L;
    public static final long PLAYBACK_POSITION_INVALID = -1;
    public static final float PLAYBACK_SPEED_1X = 1.0f;
    public static final int PLAYBACK_TYPE_LOCAL = 0;
    private static final int PLAYBACK_TYPE_MAX = 1;
    private static final int PLAYBACK_TYPE_MIN = 0;
    public static final int PLAYBACK_TYPE_REMOTE = 1;
    public static final int PLAYBACK_VOLUME_FIXED = 0;
    public static final int PLAYBACK_VOLUME_VARIABLE = 1;
    public static final int PLAYSTATE_BUFFERING = 8;
    public static final int PLAYSTATE_ERROR = 9;
    public static final int PLAYSTATE_FAST_FORWARDING = 4;
    public static final int PLAYSTATE_NONE = 0;
    public static final int PLAYSTATE_PAUSED = 2;
    public static final int PLAYSTATE_PLAYING = 3;
    public static final int PLAYSTATE_REWINDING = 5;
    public static final int PLAYSTATE_SKIPPING_BACKWARDS = 7;
    public static final int PLAYSTATE_SKIPPING_FORWARDS = 6;
    public static final int PLAYSTATE_STOPPED = 1;
    private static final long POSITION_DRIFT_MAX_MS = 500;
    private static final long POSITION_REFRESH_PERIOD_MIN_MS = 2000;
    private static final long POSITION_REFRESH_PERIOD_PLAYING_MS = 15000;
    public static final int RCSE_ID_UNREGISTERED = -1;
    private static final String TAG = "RemoteControlClient";
    private static IAudioService sService;
    private EventHandler mEventHandler;
    private OnMetadataUpdateListener mMetadataUpdateListener;
    private Bitmap mOriginalArtwork;
    private OnGetPlaybackPositionListener mPositionProvider;
    private OnPlaybackPositionUpdateListener mPositionUpdateListener;
    private final PendingIntent mRcMediaIntent;
    private int mPlaybackPositionCapabilities = 0;
    private int mPlaybackType = 0;
    private int mPlaybackVolumeMax = 15;
    private int mPlaybackVolume = 15;
    private int mPlaybackVolumeHandling = 1;
    private int mPlaybackStream = 3;
    private final Object mCacheLock = new Object();
    private int mPlaybackState = 0;
    private long mPlaybackStateChangeTimeMs = 0;
    private long mPlaybackPositionMs = -1;
    private float mPlaybackSpeed = 1.0f;
    private int mTransportControlFlags = 0;
    private Bundle mMetadata = new Bundle();
    private int mCurrentClientGenId = -1;
    private int mInternalClientGenId = -2;
    private boolean mNeedsPositionSync = false;
    private ArrayList<DisplayInfoForClient> mRcDisplays = new ArrayList<>(1);
    private final IRemoteControlClient mIRCC = new IRemoteControlClient.Stub() { // from class: android.media.RemoteControlClient.1
        @Override // android.media.IRemoteControlClient
        public void onInformationRequested(int i, int i2) {
            if (RemoteControlClient.this.mEventHandler != null) {
                RemoteControlClient.this.mEventHandler.removeMessages(5);
                RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(5, i, 0));
                RemoteControlClient.this.mEventHandler.removeMessages(1);
                RemoteControlClient.this.mEventHandler.removeMessages(2);
                RemoteControlClient.this.mEventHandler.removeMessages(3);
                RemoteControlClient.this.mEventHandler.removeMessages(4);
                RemoteControlClient.this.mEventHandler.removeMessages(14);
                RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(1, null));
                RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(3, null));
                RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(14, 0, 0, null));
            }
        }

        @Override // android.media.IRemoteControlClient
        public void informationRequestForDisplay(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2) {
            if (RemoteControlClient.this.mEventHandler != null) {
                RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(3, iRemoteControlDisplay));
                RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(1, iRemoteControlDisplay));
                if (i <= 0 || i2 <= 0) {
                    RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(2, iRemoteControlDisplay));
                } else {
                    RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(14, i, i2, iRemoteControlDisplay));
                }
            }
        }

        @Override // android.media.IRemoteControlClient
        public void setCurrentClientGenerationId(int i) {
            if (RemoteControlClient.this.mEventHandler != null) {
                RemoteControlClient.this.mEventHandler.removeMessages(6);
                RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(6, i, 0));
            }
        }

        @Override // android.media.IRemoteControlClient
        public void plugRemoteControlDisplay(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2) {
            if (RemoteControlClient.this.mEventHandler == null || iRemoteControlDisplay == null) {
                return;
            }
            RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(7, i, i2, iRemoteControlDisplay));
        }

        @Override // android.media.IRemoteControlClient
        public void unplugRemoteControlDisplay(IRemoteControlDisplay iRemoteControlDisplay) {
            if (RemoteControlClient.this.mEventHandler == null || iRemoteControlDisplay == null) {
                return;
            }
            RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(8, iRemoteControlDisplay));
        }

        @Override // android.media.IRemoteControlClient
        public void setBitmapSizeForDisplay(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2) {
            if (RemoteControlClient.this.mEventHandler == null || iRemoteControlDisplay == null) {
                return;
            }
            RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(9, i, i2, iRemoteControlDisplay));
        }

        @Override // android.media.IRemoteControlClient
        public void setWantsSyncForDisplay(IRemoteControlDisplay iRemoteControlDisplay, boolean z) {
            if (RemoteControlClient.this.mEventHandler == null || iRemoteControlDisplay == null) {
                return;
            }
            RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(12, z ? 1 : 0, 0, iRemoteControlDisplay));
        }

        @Override // android.media.IRemoteControlClient
        public void enableRemoteControlDisplay(IRemoteControlDisplay iRemoteControlDisplay, boolean z) {
            if (RemoteControlClient.this.mEventHandler == null || iRemoteControlDisplay == null) {
                return;
            }
            RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(15, z ? 1 : 0, 0, iRemoteControlDisplay));
        }

        @Override // android.media.IRemoteControlClient
        public void seekTo(int i, long j) {
            if (RemoteControlClient.this.mEventHandler != null) {
                RemoteControlClient.this.mEventHandler.removeMessages(10);
                RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(10, i, 0, new Long(j)));
            }
        }

        @Override // android.media.IRemoteControlClient
        public void updateMetadata(int i, int i2, Rating rating) {
            if (RemoteControlClient.this.mEventHandler != null) {
                RemoteControlClient.this.mEventHandler.sendMessage(RemoteControlClient.this.mEventHandler.obtainMessage(13, i, i2, rating));
            }
        }
    };
    private int mRcseId = -1;

    public interface OnGetPlaybackPositionListener {
        long onGetPlaybackPosition();
    }

    public interface OnMetadataUpdateListener {
        void onMetadataUpdate(int i, Object obj);
    }

    public interface OnPlaybackPositionUpdateListener {
        void onPlaybackPositionUpdate(long j);
    }

    static boolean playbackPositionShouldMove(int i) {
        if (i == 1 || i == 2) {
            return false;
        }
        switch (i) {
            case 6:
            case 7:
            case 8:
            case 9:
                return false;
            default:
                return true;
        }
    }

    public RemoteControlClient(PendingIntent pendingIntent) {
        this.mRcMediaIntent = pendingIntent;
        Looper looperMyLooper = Looper.myLooper();
        if (looperMyLooper != null) {
            this.mEventHandler = new EventHandler(this, looperMyLooper);
            return;
        }
        Looper mainLooper = Looper.getMainLooper();
        if (mainLooper != null) {
            this.mEventHandler = new EventHandler(this, mainLooper);
        } else {
            this.mEventHandler = null;
            Log.e(TAG, "RemoteControlClient() couldn't find main application thread");
        }
    }

    public RemoteControlClient(PendingIntent pendingIntent, Looper looper) {
        this.mRcMediaIntent = pendingIntent;
        this.mEventHandler = new EventHandler(this, looper);
    }

    public class MetadataEditor extends MediaMetadataEditor {
        public static final int BITMAP_KEY_ARTWORK = 100;
        public static final int METADATA_KEY_ARTWORK = 100;

        private MetadataEditor() {
        }

        public Object clone() throws CloneNotSupportedException {
            throw new CloneNotSupportedException();
        }

        @Override // android.media.MediaMetadataEditor
        public synchronized MetadataEditor putString(int i, String str) throws IllegalArgumentException {
            super.putString(i, str);
            return this;
        }

        @Override // android.media.MediaMetadataEditor
        public synchronized MetadataEditor putLong(int i, long j) throws IllegalArgumentException {
            super.putLong(i, j);
            return this;
        }

        @Override // android.media.MediaMetadataEditor
        public synchronized MetadataEditor putBitmap(int i, Bitmap bitmap) throws IllegalArgumentException {
            super.putBitmap(i, bitmap);
            return this;
        }

        @Override // android.media.MediaMetadataEditor
        public synchronized void clear() {
            super.clear();
        }

        @Override // android.media.MediaMetadataEditor
        public synchronized void apply() {
            if (!this.mApplied) {
                synchronized (RemoteControlClient.this.mCacheLock) {
                    RemoteControlClient.this.mMetadata = new Bundle(this.mEditorMetadata);
                    RemoteControlClient.this.mMetadata.putLong(String.valueOf(MediaMetadataEditor.KEY_EDITABLE_MASK), this.mEditableKeys);
                    if (RemoteControlClient.this.mOriginalArtwork != null && !RemoteControlClient.this.mOriginalArtwork.equals(this.mEditorArtwork)) {
                        RemoteControlClient.this.mOriginalArtwork.recycle();
                    }
                    RemoteControlClient.this.mOriginalArtwork = this.mEditorArtwork;
                    this.mEditorArtwork = null;
                    if (this.mMetadataChanged & this.mArtworkChanged) {
                        RemoteControlClient.this.sendMetadataWithArtwork_syncCacheLock(null, 0, 0);
                    } else if (this.mMetadataChanged) {
                        RemoteControlClient.this.sendMetadata_syncCacheLock(null);
                    } else if (this.mArtworkChanged) {
                        RemoteControlClient.this.sendArtwork_syncCacheLock(null, 0, 0);
                    }
                    this.mApplied = true;
                }
                return;
            }
            Log.e(RemoteControlClient.TAG, "Can't apply a previously applied MetadataEditor");
        }
    }

    public MetadataEditor editMetadata(boolean z) {
        MetadataEditor metadataEditor = new MetadataEditor();
        if (z) {
            metadataEditor.mEditorMetadata = new Bundle();
            metadataEditor.mEditorArtwork = null;
            metadataEditor.mMetadataChanged = true;
            metadataEditor.mArtworkChanged = true;
            metadataEditor.mEditableKeys = 0L;
        } else {
            metadataEditor.mEditorMetadata = new Bundle(this.mMetadata);
            metadataEditor.mEditorArtwork = this.mOriginalArtwork;
            metadataEditor.mMetadataChanged = false;
            metadataEditor.mArtworkChanged = false;
        }
        return metadataEditor;
    }

    public void setPlaybackState(int i) {
        setPlaybackStateInt(i, PLAYBACK_POSITION_ALWAYS_UNKNOWN, 1.0f, false);
    }

    public void setPlaybackState(int i, long j, float f) {
        setPlaybackStateInt(i, j, f, true);
    }

    private void setPlaybackStateInt(int i, long j, float f, boolean z) {
        synchronized (this.mCacheLock) {
            if (this.mPlaybackState != i || this.mPlaybackPositionMs != j || this.mPlaybackSpeed != f) {
                this.mPlaybackState = i;
                if (!z) {
                    this.mPlaybackPositionMs = PLAYBACK_POSITION_ALWAYS_UNKNOWN;
                } else if (j < 0) {
                    this.mPlaybackPositionMs = -1L;
                } else {
                    this.mPlaybackPositionMs = j;
                }
                this.mPlaybackSpeed = f;
                this.mPlaybackStateChangeTimeMs = SystemClock.elapsedRealtime();
                sendPlaybackState_syncCacheLock(null);
                sendAudioServiceNewPlaybackState_syncCacheLock();
                initiateCheckForDrift_syncCacheLock();
            }
        }
    }

    private void initiateCheckForDrift_syncCacheLock() {
        EventHandler eventHandler = this.mEventHandler;
        if (eventHandler == null) {
            return;
        }
        eventHandler.removeMessages(11);
        if (this.mNeedsPositionSync && this.mPlaybackPositionMs >= 0 && playbackPositionShouldMove(this.mPlaybackState)) {
            EventHandler eventHandler2 = this.mEventHandler;
            eventHandler2.sendMessageDelayed(eventHandler2.obtainMessage(11), getCheckPeriodFromSpeed(this.mPlaybackSpeed));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPositionDriftCheck() {
        synchronized (this.mCacheLock) {
            if (this.mEventHandler != null && this.mPositionProvider != null && this.mNeedsPositionSync) {
                long j = this.mPlaybackPositionMs;
                if (j >= 0 && this.mPlaybackSpeed != 0.0f) {
                    long jElapsedRealtime = j + ((long) ((SystemClock.elapsedRealtime() - this.mPlaybackStateChangeTimeMs) / this.mPlaybackSpeed));
                    long jOnGetPlaybackPosition = this.mPositionProvider.onGetPlaybackPosition();
                    if (jOnGetPlaybackPosition < 0) {
                        this.mEventHandler.removeMessages(11);
                    } else if (Math.abs(jElapsedRealtime - jOnGetPlaybackPosition) > 500) {
                        setPlaybackState(this.mPlaybackState, jOnGetPlaybackPosition, this.mPlaybackSpeed);
                    } else {
                        EventHandler eventHandler = this.mEventHandler;
                        eventHandler.sendMessageDelayed(eventHandler.obtainMessage(11), getCheckPeriodFromSpeed(this.mPlaybackSpeed));
                    }
                }
            }
        }
    }

    public void setTransportControlFlags(int i) {
        synchronized (this.mCacheLock) {
            this.mTransportControlFlags = i;
            sendTransportControlInfo_syncCacheLock(null);
        }
    }

    public void setMetadataUpdateListener(OnMetadataUpdateListener onMetadataUpdateListener) {
        synchronized (this.mCacheLock) {
            this.mMetadataUpdateListener = onMetadataUpdateListener;
        }
    }

    public void setPlaybackPositionUpdateListener(OnPlaybackPositionUpdateListener onPlaybackPositionUpdateListener) {
        synchronized (this.mCacheLock) {
            int i = this.mPlaybackPositionCapabilities;
            if (onPlaybackPositionUpdateListener != null) {
                this.mPlaybackPositionCapabilities = MEDIA_POSITION_WRITABLE | i;
            } else {
                this.mPlaybackPositionCapabilities = (~MEDIA_POSITION_WRITABLE) & i;
            }
            this.mPositionUpdateListener = onPlaybackPositionUpdateListener;
            if (i != this.mPlaybackPositionCapabilities) {
                sendTransportControlInfo_syncCacheLock(null);
            }
        }
    }

    public void setOnGetPlaybackPositionListener(OnGetPlaybackPositionListener onGetPlaybackPositionListener) {
        synchronized (this.mCacheLock) {
            int i = this.mPlaybackPositionCapabilities;
            if (onGetPlaybackPositionListener != null) {
                this.mPlaybackPositionCapabilities = MEDIA_POSITION_READABLE | i;
            } else {
                this.mPlaybackPositionCapabilities = (~MEDIA_POSITION_READABLE) & i;
            }
            this.mPositionProvider = onGetPlaybackPositionListener;
            if (i != this.mPlaybackPositionCapabilities) {
                sendTransportControlInfo_syncCacheLock(null);
            }
            if (this.mPositionProvider != null && this.mEventHandler != null && playbackPositionShouldMove(this.mPlaybackState)) {
                EventHandler eventHandler = this.mEventHandler;
                eventHandler.sendMessageDelayed(eventHandler.obtainMessage(11), 0L);
            }
        }
    }

    public void setPlaybackInformation(int i, int i2) {
        synchronized (this.mCacheLock) {
            try {
                if (i != 1) {
                    if (i != 2) {
                        if (i != 3) {
                            if (i != 4) {
                                if (i == 5) {
                                    if (i2 >= 0 && i2 < AudioSystem.getNumStreamTypes()) {
                                        this.mPlaybackStream = i2;
                                    } else {
                                        Log.w(TAG, "using invalid value for PLAYBACKINFO_USES_STREAM");
                                    }
                                } else {
                                    Log.w(TAG, "setPlaybackInformation() ignoring unknown key " + i);
                                }
                            } else if (i2 >= 0 && i2 <= 1) {
                                if (this.mPlaybackVolumeHandling != i2) {
                                    this.mPlaybackVolumeHandling = i2;
                                    sendAudioServiceNewPlaybackInfo_syncCacheLock(i, i2);
                                }
                            } else {
                                Log.w(TAG, "using invalid value for PLAYBACKINFO_VOLUME_HANDLING");
                            }
                        } else if (i2 > 0) {
                            if (this.mPlaybackVolumeMax != i2) {
                                this.mPlaybackVolumeMax = i2;
                                sendAudioServiceNewPlaybackInfo_syncCacheLock(i, i2);
                            }
                        } else {
                            Log.w(TAG, "using invalid value for PLAYBACKINFO_VOLUME_MAX");
                        }
                    } else if (i2 > -1 && i2 <= this.mPlaybackVolumeMax) {
                        if (this.mPlaybackVolume != i2) {
                            this.mPlaybackVolume = i2;
                            sendAudioServiceNewPlaybackInfo_syncCacheLock(i, i2);
                        }
                    } else {
                        Log.w(TAG, "using invalid value for PLAYBACKINFO_VOLUME");
                    }
                } else if (i2 >= 0 && i2 <= 1) {
                    if (this.mPlaybackType != i2) {
                        this.mPlaybackType = i2;
                        sendAudioServiceNewPlaybackInfo_syncCacheLock(i, i2);
                    }
                } else {
                    Log.w(TAG, "using invalid value for PLAYBACKINFO_PLAYBACK_TYPE");
                }
            } catch (Throwable th) {
                throw th;
            }
        }
    }

    public int getIntPlaybackInformation(int i) {
        synchronized (this.mCacheLock) {
            if (i == 1) {
                return this.mPlaybackType;
            }
            if (i == 2) {
                return this.mPlaybackVolume;
            }
            if (i == 3) {
                return this.mPlaybackVolumeMax;
            }
            if (i == 4) {
                return this.mPlaybackVolumeHandling;
            }
            if (i == 5) {
                return this.mPlaybackStream;
            }
            Log.e(TAG, "getIntPlaybackInformation() unknown key " + i);
            return Integer.MIN_VALUE;
        }
    }

    private class DisplayInfoForClient {
        private int mArtworkExpectedHeight;
        private int mArtworkExpectedWidth;
        private IRemoteControlDisplay mRcDisplay;
        private boolean mWantsPositionSync = false;
        private boolean mEnabled = true;

        DisplayInfoForClient(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2) {
            this.mRcDisplay = iRemoteControlDisplay;
            this.mArtworkExpectedWidth = i;
            this.mArtworkExpectedHeight = i2;
        }
    }

    public PendingIntent getRcMediaIntent() {
        return this.mRcMediaIntent;
    }

    public IRemoteControlClient getIRemoteControlClient() {
        return this.mIRCC;
    }

    public void setRcseId(int i) {
        this.mRcseId = i;
    }

    public int getRcseId() {
        return this.mRcseId;
    }

    private class EventHandler extends Handler {
        public EventHandler(RemoteControlClient remoteControlClient, Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            switch (message.what) {
                case 1:
                    synchronized (RemoteControlClient.this.mCacheLock) {
                        RemoteControlClient.this.sendPlaybackState_syncCacheLock((IRemoteControlDisplay) message.obj);
                        break;
                    }
                    return;
                case 2:
                    synchronized (RemoteControlClient.this.mCacheLock) {
                        RemoteControlClient.this.sendMetadata_syncCacheLock((IRemoteControlDisplay) message.obj);
                        break;
                    }
                    return;
                case 3:
                    synchronized (RemoteControlClient.this.mCacheLock) {
                        RemoteControlClient.this.sendTransportControlInfo_syncCacheLock((IRemoteControlDisplay) message.obj);
                        break;
                    }
                    return;
                case 4:
                    synchronized (RemoteControlClient.this.mCacheLock) {
                        RemoteControlClient.this.sendArtwork_syncCacheLock((IRemoteControlDisplay) message.obj, message.arg1, message.arg2);
                        break;
                    }
                    return;
                case 5:
                    RemoteControlClient.this.onNewInternalClientGen(message.arg1);
                    return;
                case 6:
                    RemoteControlClient.this.onNewCurrentClientGen(message.arg1);
                    return;
                case 7:
                    RemoteControlClient.this.onPlugDisplay((IRemoteControlDisplay) message.obj, message.arg1, message.arg2);
                    return;
                case 8:
                    RemoteControlClient.this.onUnplugDisplay((IRemoteControlDisplay) message.obj);
                    return;
                case 9:
                    RemoteControlClient.this.onUpdateDisplayArtworkSize((IRemoteControlDisplay) message.obj, message.arg1, message.arg2);
                    return;
                case 10:
                    RemoteControlClient.this.onSeekTo(message.arg1, ((Long) message.obj).longValue());
                    return;
                case 11:
                    RemoteControlClient.this.onPositionDriftCheck();
                    return;
                case 12:
                    RemoteControlClient.this.onDisplayWantsSync((IRemoteControlDisplay) message.obj, message.arg1 == 1);
                    return;
                case 13:
                    RemoteControlClient.this.onUpdateMetadata(message.arg1, message.arg2, message.obj);
                    return;
                case 14:
                    synchronized (RemoteControlClient.this.mCacheLock) {
                        RemoteControlClient.this.sendMetadataWithArtwork_syncCacheLock((IRemoteControlDisplay) message.obj, message.arg1, message.arg2);
                        break;
                    }
                    return;
                case 15:
                    RemoteControlClient.this.onDisplayEnable((IRemoteControlDisplay) message.obj, message.arg1 == 1);
                    return;
                default:
                    Log.e(RemoteControlClient.TAG, "Unknown event " + message.what + " in RemoteControlClient handler");
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendPlaybackState_syncCacheLock(IRemoteControlDisplay iRemoteControlDisplay) {
        int i = this.mCurrentClientGenId;
        int i2 = this.mInternalClientGenId;
        if (i == i2) {
            if (iRemoteControlDisplay != null) {
                try {
                    iRemoteControlDisplay.setPlaybackState(i2, this.mPlaybackState, this.mPlaybackStateChangeTimeMs, this.mPlaybackPositionMs, this.mPlaybackSpeed);
                    return;
                } catch (RemoteException e) {
                    Log.e(TAG, "Error in setPlaybackState() for dead display " + iRemoteControlDisplay, e);
                    return;
                }
            }
            Iterator<DisplayInfoForClient> it = this.mRcDisplays.iterator();
            while (it.hasNext()) {
                DisplayInfoForClient next = it.next();
                if (next.mEnabled) {
                    try {
                        next.mRcDisplay.setPlaybackState(this.mInternalClientGenId, this.mPlaybackState, this.mPlaybackStateChangeTimeMs, this.mPlaybackPositionMs, this.mPlaybackSpeed);
                    } catch (RemoteException e2) {
                        Log.e(TAG, "Error in setPlaybackState(), dead display " + next.mRcDisplay, e2);
                        it.remove();
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendMetadata_syncCacheLock(IRemoteControlDisplay iRemoteControlDisplay) {
        int i = this.mCurrentClientGenId;
        int i2 = this.mInternalClientGenId;
        if (i == i2) {
            if (iRemoteControlDisplay != null) {
                try {
                    iRemoteControlDisplay.setMetadata(i2, this.mMetadata);
                    return;
                } catch (RemoteException e) {
                    Log.e(TAG, "Error in setMetadata() for dead display " + iRemoteControlDisplay, e);
                    return;
                }
            }
            Iterator<DisplayInfoForClient> it = this.mRcDisplays.iterator();
            while (it.hasNext()) {
                DisplayInfoForClient next = it.next();
                if (next.mEnabled) {
                    try {
                        next.mRcDisplay.setMetadata(this.mInternalClientGenId, this.mMetadata);
                    } catch (RemoteException e2) {
                        Log.e(TAG, "Error in setMetadata(), dead display " + next.mRcDisplay, e2);
                        it.remove();
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendTransportControlInfo_syncCacheLock(IRemoteControlDisplay iRemoteControlDisplay) {
        int i = this.mCurrentClientGenId;
        int i2 = this.mInternalClientGenId;
        if (i == i2) {
            if (iRemoteControlDisplay != null) {
                try {
                    iRemoteControlDisplay.setTransportControlInfo(i2, this.mTransportControlFlags, this.mPlaybackPositionCapabilities);
                    return;
                } catch (RemoteException e) {
                    Log.e(TAG, "Error in setTransportControlFlags() for dead display " + iRemoteControlDisplay, e);
                    return;
                }
            }
            Iterator<DisplayInfoForClient> it = this.mRcDisplays.iterator();
            while (it.hasNext()) {
                DisplayInfoForClient next = it.next();
                if (next.mEnabled) {
                    try {
                        next.mRcDisplay.setTransportControlInfo(this.mInternalClientGenId, this.mTransportControlFlags, this.mPlaybackPositionCapabilities);
                    } catch (RemoteException e2) {
                        Log.e(TAG, "Error in setTransportControlFlags(), dead display " + next.mRcDisplay, e2);
                        it.remove();
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendArtwork_syncCacheLock(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2) {
        if (this.mCurrentClientGenId == this.mInternalClientGenId) {
            if (iRemoteControlDisplay != null) {
                sendArtworkToDisplay(new DisplayInfoForClient(iRemoteControlDisplay, i, i2));
                return;
            }
            Iterator<DisplayInfoForClient> it = this.mRcDisplays.iterator();
            while (it.hasNext()) {
                if (!sendArtworkToDisplay(it.next())) {
                    it.remove();
                }
            }
        }
    }

    private boolean sendArtworkToDisplay(DisplayInfoForClient displayInfoForClient) {
        if (displayInfoForClient.mArtworkExpectedWidth <= 0 || displayInfoForClient.mArtworkExpectedHeight <= 0) {
            return true;
        }
        try {
            displayInfoForClient.mRcDisplay.setArtwork(this.mInternalClientGenId, scaleBitmapIfTooBig(this.mOriginalArtwork, displayInfoForClient.mArtworkExpectedWidth, displayInfoForClient.mArtworkExpectedHeight));
            return true;
        } catch (RemoteException e) {
            Log.e(TAG, "Error in sendArtworkToDisplay(), dead display " + displayInfoForClient.mRcDisplay, e);
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void sendMetadataWithArtwork_syncCacheLock(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2) {
        int i3 = this.mCurrentClientGenId;
        int i4 = this.mInternalClientGenId;
        if (i3 == i4) {
            if (iRemoteControlDisplay != null) {
                try {
                    if (i > 0 && i2 > 0) {
                        iRemoteControlDisplay.setAllMetadata(this.mInternalClientGenId, this.mMetadata, scaleBitmapIfTooBig(this.mOriginalArtwork, i, i2));
                    } else {
                        iRemoteControlDisplay.setMetadata(i4, this.mMetadata);
                    }
                    return;
                } catch (RemoteException e) {
                    Log.e(TAG, "Error in set(All)Metadata() for dead display " + iRemoteControlDisplay, e);
                    return;
                }
            }
            Iterator<DisplayInfoForClient> it = this.mRcDisplays.iterator();
            while (it.hasNext()) {
                DisplayInfoForClient next = it.next();
                try {
                    if (next.mEnabled) {
                        if (next.mArtworkExpectedWidth <= 0 || next.mArtworkExpectedHeight <= 0) {
                            next.mRcDisplay.setMetadata(this.mInternalClientGenId, this.mMetadata);
                        } else {
                            next.mRcDisplay.setAllMetadata(this.mInternalClientGenId, this.mMetadata, scaleBitmapIfTooBig(this.mOriginalArtwork, next.mArtworkExpectedWidth, next.mArtworkExpectedHeight));
                        }
                    }
                } catch (RemoteException e2) {
                    Log.e(TAG, "Error when setting metadata, dead display " + next.mRcDisplay, e2);
                    it.remove();
                }
            }
        }
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

    private void sendAudioServiceNewPlaybackInfo_syncCacheLock(int i, int i2) {
        if (this.mRcseId == -1) {
            return;
        }
        try {
            getService().setPlaybackInfoForRcc(this.mRcseId, i, i2);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setPlaybackInfoForRcc", e);
        }
    }

    private void sendAudioServiceNewPlaybackState_syncCacheLock() {
        if (this.mRcseId == -1) {
            return;
        }
        try {
            getService().setPlaybackStateForRcc(this.mRcseId, this.mPlaybackState, this.mPlaybackPositionMs, this.mPlaybackSpeed);
        } catch (RemoteException e) {
            Log.e(TAG, "Dead object in setPlaybackStateForRcc", e);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNewInternalClientGen(int i) {
        synchronized (this.mCacheLock) {
            this.mInternalClientGenId = i;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNewCurrentClientGen(int i) {
        synchronized (this.mCacheLock) {
            this.mCurrentClientGenId = i;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPlugDisplay(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2) {
        synchronized (this.mCacheLock) {
            boolean z = false;
            Iterator<DisplayInfoForClient> it = this.mRcDisplays.iterator();
            while (it.hasNext() && !z) {
                DisplayInfoForClient next = it.next();
                boolean zEquals = next.mRcDisplay.asBinder().equals(iRemoteControlDisplay.asBinder());
                if (zEquals && (next.mArtworkExpectedWidth != i || next.mArtworkExpectedHeight != i2)) {
                    next.mArtworkExpectedWidth = i;
                    next.mArtworkExpectedHeight = i2;
                    if (!sendArtworkToDisplay(next)) {
                        it.remove();
                    }
                }
                z = zEquals;
            }
            if (!z) {
                this.mRcDisplays.add(new DisplayInfoForClient(iRemoteControlDisplay, i, i2));
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUnplugDisplay(IRemoteControlDisplay iRemoteControlDisplay) {
        synchronized (this.mCacheLock) {
            Iterator<DisplayInfoForClient> it = this.mRcDisplays.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                } else if (it.next().mRcDisplay.asBinder().equals(iRemoteControlDisplay.asBinder())) {
                    it.remove();
                    break;
                }
            }
            boolean z = this.mNeedsPositionSync;
            boolean z2 = false;
            Iterator<DisplayInfoForClient> it2 = this.mRcDisplays.iterator();
            while (true) {
                if (!it2.hasNext()) {
                    break;
                } else if (it2.next().mWantsPositionSync) {
                    z2 = true;
                    break;
                }
            }
            this.mNeedsPositionSync = z2;
            if (z != z2) {
                initiateCheckForDrift_syncCacheLock();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUpdateDisplayArtworkSize(IRemoteControlDisplay iRemoteControlDisplay, int i, int i2) {
        synchronized (this.mCacheLock) {
            Iterator<DisplayInfoForClient> it = this.mRcDisplays.iterator();
            while (it.hasNext()) {
                DisplayInfoForClient next = it.next();
                if (next.mRcDisplay.asBinder().equals(iRemoteControlDisplay.asBinder()) && (next.mArtworkExpectedWidth != i || next.mArtworkExpectedHeight != i2)) {
                    next.mArtworkExpectedWidth = i;
                    next.mArtworkExpectedHeight = i2;
                    if (next.mEnabled && !sendArtworkToDisplay(next)) {
                        it.remove();
                    }
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDisplayWantsSync(IRemoteControlDisplay iRemoteControlDisplay, boolean z) {
        synchronized (this.mCacheLock) {
            boolean z2 = this.mNeedsPositionSync;
            boolean z3 = false;
            for (DisplayInfoForClient displayInfoForClient : this.mRcDisplays) {
                if (displayInfoForClient.mEnabled) {
                    if (displayInfoForClient.mRcDisplay.asBinder().equals(iRemoteControlDisplay.asBinder())) {
                        displayInfoForClient.mWantsPositionSync = z;
                    }
                    if (displayInfoForClient.mWantsPositionSync) {
                        z3 = true;
                    }
                }
            }
            this.mNeedsPositionSync = z3;
            if (z2 != z3) {
                initiateCheckForDrift_syncCacheLock();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDisplayEnable(IRemoteControlDisplay iRemoteControlDisplay, boolean z) {
        synchronized (this.mCacheLock) {
            for (DisplayInfoForClient displayInfoForClient : this.mRcDisplays) {
                if (displayInfoForClient.mRcDisplay.asBinder().equals(iRemoteControlDisplay.asBinder())) {
                    displayInfoForClient.mEnabled = z;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onSeekTo(int i, long j) {
        OnPlaybackPositionUpdateListener onPlaybackPositionUpdateListener;
        synchronized (this.mCacheLock) {
            if (this.mCurrentClientGenId == i && (onPlaybackPositionUpdateListener = this.mPositionUpdateListener) != null) {
                onPlaybackPositionUpdateListener.onPlaybackPositionUpdate(j);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onUpdateMetadata(int i, int i2, Object obj) {
        OnMetadataUpdateListener onMetadataUpdateListener;
        synchronized (this.mCacheLock) {
            if (this.mCurrentClientGenId == i && (onMetadataUpdateListener = this.mMetadataUpdateListener) != null) {
                onMetadataUpdateListener.onMetadataUpdate(i2, obj);
            }
        }
    }

    private Bitmap scaleBitmapIfTooBig(Bitmap bitmap, int i, int i2) {
        if (bitmap == null) {
            return bitmap;
        }
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (width <= i && height <= i2) {
            return bitmap;
        }
        float f = width;
        float f2 = height;
        float fMin = Math.min(i / f, i2 / f2);
        int iRound = Math.round(f * fMin);
        int iRound2 = Math.round(fMin * f2);
        Bitmap.Config config = bitmap.getConfig();
        if (config == null) {
            config = Bitmap.Config.ARGB_8888;
        }
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(iRound, iRound2, config);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        canvas.drawBitmap(bitmap, (Rect) null, new RectF(0.0f, 0.0f, bitmapCreateBitmap.getWidth(), bitmapCreateBitmap.getHeight()), paint);
        return bitmapCreateBitmap;
    }

    private static long getCheckPeriodFromSpeed(float f) {
        return Math.abs(f) <= 1.0f ? POSITION_REFRESH_PERIOD_PLAYING_MS : Math.max((long) (15000.0f / Math.abs(f)), 2000L);
    }
}
