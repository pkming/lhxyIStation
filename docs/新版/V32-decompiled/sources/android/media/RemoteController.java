package android.media;

import android.app.ActivityManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.IRemoteControlDisplay;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import java.lang.ref.WeakReference;

/* JADX INFO: loaded from: classes.dex */
public final class RemoteController {
    private static final boolean DEBUG = false;
    private static final int MAX_BITMAP_DIMENSION = 512;
    private static final int MSG_CLIENT_CHANGE = 4;
    private static final int MSG_DISPLAY_ENABLE = 5;
    private static final int MSG_NEW_METADATA = 3;
    private static final int MSG_NEW_PENDING_INTENT = 0;
    private static final int MSG_NEW_PLAYBACK_INFO = 1;
    private static final int MSG_NEW_TRANSPORT_INFO = 2;
    public static final int POSITION_SYNCHRONIZATION_CHECK = 1;
    public static final int POSITION_SYNCHRONIZATION_NONE = 0;
    private static final int SENDMSG_NOOP = 1;
    private static final int SENDMSG_QUEUE = 2;
    private static final int SENDMSG_REPLACE = 0;
    private static final String TAG = "RemoteController";
    private static final int TRANSPORT_UNKNOWN = 0;
    private static final Object mGenLock = new Object();
    private static final Object mInfoLock = new Object();
    private int mArtworkHeight;
    private int mArtworkWidth;
    private final AudioManager mAudioManager;
    private int mClientGenerationIdCurrent;
    private PendingIntent mClientPendingIntentCurrent;
    private final Context mContext;
    private boolean mEnabled;
    private final EventHandler mEventHandler;
    private boolean mIsRegistered;
    private PlaybackInfo mLastPlaybackInfo;
    private final int mMaxBitmapDimension;
    private MetadataEditor mMetadataEditor;
    private OnClientUpdateListener mOnClientUpdateListener;
    private final RcDisplay mRcd;

    public interface OnClientUpdateListener {
        void onClientChange(boolean z);

        void onClientMetadataUpdate(MetadataEditor metadataEditor);

        void onClientPlaybackStateUpdate(int i);

        void onClientPlaybackStateUpdate(int i, long j, long j2, float f);

        void onClientTransportControlUpdate(int i);
    }

    public RemoteController(Context context, OnClientUpdateListener onClientUpdateListener) throws IllegalArgumentException {
        this(context, onClientUpdateListener, null);
    }

    public RemoteController(Context context, OnClientUpdateListener onClientUpdateListener, Looper looper) throws IllegalArgumentException {
        this.mClientGenerationIdCurrent = 0;
        this.mIsRegistered = false;
        this.mArtworkWidth = -1;
        this.mArtworkHeight = -1;
        this.mEnabled = true;
        if (context == null) {
            throw new IllegalArgumentException("Invalid null Context");
        }
        if (onClientUpdateListener == null) {
            throw new IllegalArgumentException("Invalid null OnClientUpdateListener");
        }
        if (looper != null) {
            this.mEventHandler = new EventHandler(this, looper);
        } else {
            Looper looperMyLooper = Looper.myLooper();
            if (looperMyLooper != null) {
                this.mEventHandler = new EventHandler(this, looperMyLooper);
            } else {
                throw new IllegalArgumentException("Calling thread not associated with a looper");
            }
        }
        this.mOnClientUpdateListener = onClientUpdateListener;
        this.mContext = context;
        this.mRcd = new RcDisplay(this);
        this.mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (ActivityManager.isLowRamDeviceStatic()) {
            this.mMaxBitmapDimension = 512;
        } else {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            this.mMaxBitmapDimension = Math.max(displayMetrics.widthPixels, displayMetrics.heightPixels);
        }
    }

    public String getRemoteControlClientPackageName() {
        PendingIntent pendingIntent = this.mClientPendingIntentCurrent;
        if (pendingIntent != null) {
            return pendingIntent.getCreatorPackage();
        }
        return null;
    }

    public long getEstimatedMediaPosition() {
        PlaybackInfo playbackInfo = this.mLastPlaybackInfo;
        if (playbackInfo == null) {
            return -1L;
        }
        if (!RemoteControlClient.playbackPositionShouldMove(playbackInfo.mState)) {
            return this.mLastPlaybackInfo.mCurrentPosMs;
        }
        long j = this.mLastPlaybackInfo.mCurrentPosMs;
        if (j < 0) {
            return -1L;
        }
        return j + ((long) ((SystemClock.elapsedRealtime() - this.mLastPlaybackInfo.mStateChangeTimeMs) * this.mLastPlaybackInfo.mSpeed));
    }

    public boolean sendMediaKeyEvent(KeyEvent keyEvent) throws IllegalArgumentException {
        if (!MediaFocusControl.isMediaKeyCode(keyEvent.getKeyCode())) {
            throw new IllegalArgumentException("not a media key event");
        }
        synchronized (mInfoLock) {
            if (!this.mIsRegistered) {
                Log.e(TAG, "Cannot use sendMediaKeyEvent() from an unregistered RemoteController");
                return false;
            }
            if (!this.mEnabled) {
                Log.e(TAG, "Cannot use sendMediaKeyEvent() from a disabled RemoteController");
                return false;
            }
            PendingIntent pendingIntent = this.mClientPendingIntentCurrent;
            if (pendingIntent != null) {
                Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
                intent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
                try {
                    pendingIntent.send(this.mContext, 0, intent);
                    return true;
                } catch (PendingIntent.CanceledException e) {
                    Log.e(TAG, "Error sending intent for media button down: ", e);
                    return false;
                }
            }
            Log.i(TAG, "No-op when sending key click, no receiver right now");
            return false;
        }
    }

    public boolean seekTo(long j) throws IllegalArgumentException {
        int i;
        if (!this.mEnabled) {
            Log.e(TAG, "Cannot use seekTo() from a disabled RemoteController");
            return false;
        }
        if (j < 0) {
            throw new IllegalArgumentException("illegal negative time value");
        }
        synchronized (mGenLock) {
            i = this.mClientGenerationIdCurrent;
        }
        this.mAudioManager.setRemoteControlClientPlaybackPosition(i, j);
        return true;
    }

    public boolean setArtworkConfiguration(boolean z, int i, int i2) throws IllegalArgumentException {
        synchronized (mInfoLock) {
            if (!z) {
                this.mArtworkWidth = -1;
                this.mArtworkHeight = -1;
            } else if (i > 0 && i2 > 0) {
                int i3 = this.mMaxBitmapDimension;
                if (i > i3) {
                    i = i3;
                }
                if (i2 > i3) {
                    i2 = i3;
                }
                this.mArtworkWidth = i;
                this.mArtworkHeight = i2;
            } else {
                throw new IllegalArgumentException("Invalid dimensions");
            }
            if (this.mIsRegistered) {
                this.mAudioManager.remoteControlDisplayUsesBitmapSize(this.mRcd, this.mArtworkWidth, this.mArtworkHeight);
            }
        }
        return true;
    }

    public boolean setArtworkConfiguration(int i, int i2) throws IllegalArgumentException {
        return setArtworkConfiguration(true, i, i2);
    }

    public boolean clearArtworkConfiguration() {
        return setArtworkConfiguration(false, -1, -1);
    }

    public boolean setSynchronizationMode(int i) throws IllegalArgumentException {
        if (i != 0 || i != 1) {
            throw new IllegalArgumentException("Unknown synchronization mode " + i);
        }
        if (!this.mIsRegistered) {
            Log.e(TAG, "Cannot set synchronization mode on an unregistered RemoteController");
            return false;
        }
        this.mAudioManager.remoteControlDisplayWantsPlaybackPositionSync(this.mRcd, 1 == i);
        return true;
    }

    public MetadataEditor editMetadata() {
        MetadataEditor metadataEditor = new MetadataEditor();
        metadataEditor.mEditorMetadata = new Bundle();
        metadataEditor.mEditorArtwork = null;
        metadataEditor.mMetadataChanged = true;
        metadataEditor.mArtworkChanged = true;
        metadataEditor.mEditableKeys = 0L;
        return metadataEditor;
    }

    public class MetadataEditor extends MediaMetadataEditor {
        protected MetadataEditor() {
        }

        protected MetadataEditor(Bundle bundle, long j) {
            this.mEditorMetadata = bundle;
            this.mEditableKeys = j;
            this.mEditorArtwork = (Bitmap) bundle.getParcelable(String.valueOf(100));
            if (this.mEditorArtwork != null) {
                cleanupBitmapFromBundle(100);
            }
            this.mMetadataChanged = true;
            this.mArtworkChanged = true;
            this.mApplied = false;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void cleanupBitmapFromBundle(int i) {
            if (METADATA_KEYS_TYPE.get(i, -1) == 2) {
                this.mEditorMetadata.remove(String.valueOf(i));
            }
        }

        @Override // android.media.MediaMetadataEditor
        public synchronized void apply() {
            int i;
            if (this.mMetadataChanged) {
                synchronized (RemoteController.mGenLock) {
                    i = RemoteController.this.mClientGenerationIdCurrent;
                }
                synchronized (RemoteController.mInfoLock) {
                    if (this.mEditorMetadata.containsKey(String.valueOf(MediaMetadataEditor.RATING_KEY_BY_USER))) {
                        RemoteController.this.mAudioManager.updateRemoteControlClientMetadata(i, MediaMetadataEditor.RATING_KEY_BY_USER, (Rating) getObject(MediaMetadataEditor.RATING_KEY_BY_USER, null));
                    } else {
                        Log.e(RemoteController.TAG, "no metadata to apply");
                    }
                    this.mApplied = false;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    static class RcDisplay extends IRemoteControlDisplay.Stub {
        private final WeakReference<RemoteController> mController;

        RcDisplay(RemoteController remoteController) {
            this.mController = new WeakReference<>(remoteController);
        }

        @Override // android.media.IRemoteControlDisplay
        public void setCurrentClientId(int i, PendingIntent pendingIntent, boolean z) {
            boolean z2;
            RemoteController remoteController = this.mController.get();
            if (remoteController == null) {
                return;
            }
            boolean z3 = false;
            synchronized (RemoteController.mGenLock) {
                if (remoteController.mClientGenerationIdCurrent != i) {
                    remoteController.mClientGenerationIdCurrent = i;
                    z3 = true;
                }
                z2 = z3;
            }
            if (pendingIntent != null) {
                RemoteController.sendMsg(remoteController.mEventHandler, 0, 0, i, 0, pendingIntent, 0);
            }
            if (z2 || z) {
                RemoteController.sendMsg(remoteController.mEventHandler, 4, 0, i, z ? 1 : 0, null, 0);
            }
        }

        @Override // android.media.IRemoteControlDisplay
        public void setEnabled(boolean z) {
            RemoteController remoteController = this.mController.get();
            if (remoteController == null) {
                return;
            }
            RemoteController.sendMsg(remoteController.mEventHandler, 5, 0, z ? 1 : 0, 0, null, 0);
        }

        @Override // android.media.IRemoteControlDisplay
        public void setPlaybackState(int i, int i2, long j, long j2, float f) {
            RemoteController remoteController = this.mController.get();
            if (remoteController == null) {
                return;
            }
            synchronized (RemoteController.mGenLock) {
                if (remoteController.mClientGenerationIdCurrent != i) {
                    return;
                }
                RemoteController.sendMsg(remoteController.mEventHandler, 1, 0, i, 0, new PlaybackInfo(i2, j, j2, f), 0);
            }
        }

        @Override // android.media.IRemoteControlDisplay
        public void setTransportControlInfo(int i, int i2, int i3) {
            RemoteController remoteController = this.mController.get();
            if (remoteController == null) {
                return;
            }
            synchronized (RemoteController.mGenLock) {
                if (remoteController.mClientGenerationIdCurrent != i) {
                    return;
                }
                RemoteController.sendMsg(remoteController.mEventHandler, 2, 0, i, i2, null, 0);
            }
        }

        @Override // android.media.IRemoteControlDisplay
        public void setMetadata(int i, Bundle bundle) {
            RemoteController remoteController = this.mController.get();
            if (remoteController == null || bundle == null) {
                return;
            }
            synchronized (RemoteController.mGenLock) {
                if (remoteController.mClientGenerationIdCurrent != i) {
                    return;
                }
                RemoteController.sendMsg(remoteController.mEventHandler, 3, 2, i, 0, bundle, 0);
            }
        }

        @Override // android.media.IRemoteControlDisplay
        public void setArtwork(int i, Bitmap bitmap) {
            RemoteController remoteController = this.mController.get();
            if (remoteController == null) {
                return;
            }
            synchronized (RemoteController.mGenLock) {
                if (remoteController.mClientGenerationIdCurrent != i) {
                    return;
                }
                Bundle bundle = new Bundle(1);
                bundle.putParcelable(String.valueOf(100), bitmap);
                RemoteController.sendMsg(remoteController.mEventHandler, 3, 2, i, 0, bundle, 0);
            }
        }

        @Override // android.media.IRemoteControlDisplay
        public void setAllMetadata(int i, Bundle bundle, Bitmap bitmap) {
            RemoteController remoteController = this.mController.get();
            if (remoteController == null) {
                return;
            }
            if (bundle == null && bitmap == null) {
                return;
            }
            synchronized (RemoteController.mGenLock) {
                if (remoteController.mClientGenerationIdCurrent != i) {
                    return;
                }
                if (bundle == null) {
                    bundle = new Bundle(1);
                }
                Bundle bundle2 = bundle;
                if (bitmap != null) {
                    bundle2.putParcelable(String.valueOf(100), bitmap);
                }
                RemoteController.sendMsg(remoteController.mEventHandler, 3, 2, i, 0, bundle2, 0);
            }
        }
    }

    private class EventHandler extends Handler {
        public EventHandler(RemoteController remoteController, Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            int i = message.what;
            if (i == 0) {
                RemoteController.this.onNewPendingIntent(message.arg1, (PendingIntent) message.obj);
                return;
            }
            if (i == 1) {
                RemoteController.this.onNewPlaybackInfo(message.arg1, (PlaybackInfo) message.obj);
                return;
            }
            if (i == 2) {
                RemoteController.this.onNewTransportInfo(message.arg1, message.arg2);
                return;
            }
            if (i == 3) {
                RemoteController.this.onNewMetadata(message.arg1, (Bundle) message.obj);
                return;
            }
            if (i == 4) {
                RemoteController.this.onClientChange(message.arg1, message.arg2 == 1);
            } else if (i == 5) {
                RemoteController.this.onDisplayEnable(message.arg1 == 1);
            } else {
                Log.e(RemoteController.TAG, "unknown event " + message.what);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void sendMsg(Handler handler, int i, int i2, int i3, int i4, Object obj, int i5) {
        if (handler == null) {
            Log.e(TAG, "null event handler, will not deliver message " + i);
            return;
        }
        if (i2 == 0) {
            handler.removeMessages(i);
        } else if (i2 == 1 && handler.hasMessages(i)) {
            return;
        }
        handler.sendMessageDelayed(handler.obtainMessage(i, i3, i4, obj), i5);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNewPendingIntent(int i, PendingIntent pendingIntent) {
        synchronized (mGenLock) {
            if (this.mClientGenerationIdCurrent != i) {
                return;
            }
            synchronized (mInfoLock) {
                this.mClientPendingIntentCurrent = pendingIntent;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNewPlaybackInfo(int i, PlaybackInfo playbackInfo) {
        OnClientUpdateListener onClientUpdateListener;
        synchronized (mGenLock) {
            if (this.mClientGenerationIdCurrent != i) {
                return;
            }
            synchronized (mInfoLock) {
                onClientUpdateListener = this.mOnClientUpdateListener;
                this.mLastPlaybackInfo = playbackInfo;
            }
            if (onClientUpdateListener != null) {
                if (playbackInfo.mCurrentPosMs == RemoteControlClient.PLAYBACK_POSITION_ALWAYS_UNKNOWN) {
                    onClientUpdateListener.onClientPlaybackStateUpdate(playbackInfo.mState);
                } else {
                    onClientUpdateListener.onClientPlaybackStateUpdate(playbackInfo.mState, playbackInfo.mStateChangeTimeMs, playbackInfo.mCurrentPosMs, playbackInfo.mSpeed);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNewTransportInfo(int i, int i2) {
        OnClientUpdateListener onClientUpdateListener;
        synchronized (mGenLock) {
            if (this.mClientGenerationIdCurrent != i) {
                return;
            }
            synchronized (mInfoLock) {
                onClientUpdateListener = this.mOnClientUpdateListener;
            }
            if (onClientUpdateListener != null) {
                onClientUpdateListener.onClientTransportControlUpdate(i2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onNewMetadata(int i, Bundle bundle) {
        OnClientUpdateListener onClientUpdateListener;
        MetadataEditor metadataEditor;
        synchronized (mGenLock) {
            if (this.mClientGenerationIdCurrent != i) {
                return;
            }
            long j = bundle.getLong(String.valueOf(MediaMetadataEditor.KEY_EDITABLE_MASK), 0L);
            if (j != 0) {
                bundle.remove(String.valueOf(MediaMetadataEditor.KEY_EDITABLE_MASK));
            }
            synchronized (mInfoLock) {
                onClientUpdateListener = this.mOnClientUpdateListener;
                MetadataEditor metadataEditor2 = this.mMetadataEditor;
                if (metadataEditor2 != null && metadataEditor2.mEditorMetadata != null) {
                    if (this.mMetadataEditor.mEditorMetadata != bundle) {
                        this.mMetadataEditor.mEditorMetadata.putAll(bundle);
                    }
                    this.mMetadataEditor.putBitmap(100, (Bitmap) bundle.getParcelable(String.valueOf(100)));
                    this.mMetadataEditor.cleanupBitmapFromBundle(100);
                } else {
                    this.mMetadataEditor = new MetadataEditor(bundle, j);
                }
                metadataEditor = this.mMetadataEditor;
            }
            if (onClientUpdateListener != null) {
                onClientUpdateListener.onClientMetadataUpdate(metadataEditor);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onClientChange(int i, boolean z) {
        OnClientUpdateListener onClientUpdateListener;
        synchronized (mGenLock) {
            if (this.mClientGenerationIdCurrent != i) {
                return;
            }
            synchronized (mInfoLock) {
                onClientUpdateListener = this.mOnClientUpdateListener;
                this.mMetadataEditor = null;
            }
            if (onClientUpdateListener != null) {
                onClientUpdateListener.onClientChange(z);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDisplayEnable(boolean z) {
        int i;
        synchronized (mInfoLock) {
            this.mEnabled = z;
        }
        if (z) {
            return;
        }
        synchronized (mGenLock) {
            i = this.mClientGenerationIdCurrent;
        }
        sendMsg(this.mEventHandler, 1, 0, i, 0, new PlaybackInfo(1, SystemClock.elapsedRealtime(), 0L, 0.0f), 0);
        sendMsg(this.mEventHandler, 2, 0, i, 0, null, 0);
        Bundle bundle = new Bundle(3);
        bundle.putString(String.valueOf(7), "");
        bundle.putString(String.valueOf(2), "");
        bundle.putLong(String.valueOf(9), 0L);
        sendMsg(this.mEventHandler, 3, 2, i, 0, bundle, 0);
    }

    private static class PlaybackInfo {
        long mCurrentPosMs;
        float mSpeed;
        int mState;
        long mStateChangeTimeMs;

        PlaybackInfo(int i, long j, long j2, float f) {
            this.mState = i;
            this.mStateChangeTimeMs = j;
            this.mCurrentPosMs = j2;
            this.mSpeed = f;
        }
    }

    void setIsRegistered(boolean z) {
        synchronized (mInfoLock) {
            this.mIsRegistered = z;
        }
    }

    RcDisplay getRcDisplay() {
        return this.mRcd;
    }

    int[] getArtworkSize() {
        int[] iArr;
        synchronized (mInfoLock) {
            iArr = new int[]{this.mArtworkWidth, this.mArtworkHeight};
        }
        return iArr;
    }

    OnClientUpdateListener getUpdateListener() {
        return this.mOnClientUpdateListener;
    }
}
