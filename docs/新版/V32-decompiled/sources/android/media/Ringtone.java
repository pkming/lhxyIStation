package android.media;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.util.Log;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class Ringtone {
    private static final boolean LOGD = true;
    private static final String[] MEDIA_COLUMNS = {"_id", "_data", "title"};
    private static final String TAG = "Ringtone";
    private final boolean mAllowRemote;
    private final AudioManager mAudioManager;
    private final Context mContext;
    private MediaPlayer mLocalPlayer;
    private final IRingtonePlayer mRemotePlayer;
    private final Binder mRemoteToken;
    private int mStreamType = 2;
    private String mTitle;
    private Uri mUri;

    public Ringtone(Context context, boolean z) {
        this.mContext = context;
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.mAudioManager = audioManager;
        this.mAllowRemote = z;
        this.mRemotePlayer = z ? audioManager.getRingtonePlayer() : null;
        this.mRemoteToken = z ? new Binder() : null;
    }

    public void setStreamType(int i) {
        this.mStreamType = i;
        setUri(this.mUri);
    }

    public int getStreamType() {
        return this.mStreamType;
    }

    public String getTitle(Context context) {
        String str = this.mTitle;
        if (str != null) {
            return str;
        }
        String title = getTitle(context, this.mUri, true);
        this.mTitle = title;
        return title;
    }

    private static String getTitle(Context context, Uri uri, boolean z) {
        ContentResolver contentResolver = context.getContentResolver();
        String string = null;
        string = null;
        cursorQuery = null;
        Cursor cursorQuery = null;
        if (uri != null) {
            String authority = uri.getAuthority();
            if (!"settings".equals(authority)) {
                try {
                    if (MediaStore.AUTHORITY.equals(authority)) {
                        cursorQuery = contentResolver.query(uri, MEDIA_COLUMNS, null, null, null);
                    }
                } catch (SecurityException unused) {
                }
                if (cursorQuery != null) {
                    try {
                        if (cursorQuery.getCount() == 1) {
                            cursorQuery.moveToFirst();
                            return cursorQuery.getString(2);
                        }
                    } finally {
                        if (cursorQuery != null) {
                            cursorQuery.close();
                        }
                    }
                }
                String lastPathSegment = uri.getLastPathSegment();
                if (cursorQuery != null) {
                    cursorQuery.close();
                }
                string = lastPathSegment;
            } else if (z) {
                string = context.getString(17040430, getTitle(context, RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.getDefaultType(uri)), false));
            }
        }
        if (string != null) {
            return string;
        }
        String string2 = context.getString(17040433);
        return string2 == null ? "" : string2;
    }

    public void setUri(Uri uri) {
        destroyLocalPlayer();
        this.mUri = uri;
        if (uri == null) {
            return;
        }
        MediaPlayer mediaPlayer = new MediaPlayer();
        this.mLocalPlayer = mediaPlayer;
        try {
            mediaPlayer.setDataSource(this.mContext, this.mUri);
            this.mLocalPlayer.setAudioStreamType(this.mStreamType);
            this.mLocalPlayer.prepare();
        } catch (IOException e) {
            destroyLocalPlayer();
            if (!this.mAllowRemote) {
                Log.w(TAG, "Remote playback not allowed: " + e);
            }
        } catch (SecurityException e2) {
            destroyLocalPlayer();
            if (!this.mAllowRemote) {
                Log.w(TAG, "Remote playback not allowed: " + e2);
            }
        }
        if (this.mLocalPlayer != null) {
            Log.d(TAG, "Successfully created local player");
        } else {
            Log.d(TAG, "Problem opening; delegating to remote player");
        }
    }

    public Uri getUri() {
        return this.mUri;
    }

    public void play() {
        if (this.mLocalPlayer != null) {
            if (this.mAudioManager.getStreamVolume(this.mStreamType) != 0) {
                this.mLocalPlayer.start();
            }
        } else {
            if (this.mAllowRemote) {
                try {
                    this.mRemotePlayer.play(this.mRemoteToken, this.mUri.getCanonicalUri(), this.mStreamType);
                    return;
                } catch (RemoteException e) {
                    if (playFallbackRingtone()) {
                        return;
                    }
                    Log.w(TAG, "Problem playing ringtone: " + e);
                    return;
                }
            }
            if (playFallbackRingtone()) {
                return;
            }
            Log.w(TAG, "Neither local nor remote playback available");
        }
    }

    public void stop() {
        if (this.mLocalPlayer != null) {
            destroyLocalPlayer();
        } else if (this.mAllowRemote) {
            try {
                this.mRemotePlayer.stop(this.mRemoteToken);
            } catch (RemoteException e) {
                Log.w(TAG, "Problem stopping ringtone: " + e);
            }
        }
    }

    private void destroyLocalPlayer() {
        MediaPlayer mediaPlayer = this.mLocalPlayer;
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            this.mLocalPlayer.release();
            this.mLocalPlayer = null;
        }
    }

    public boolean isPlaying() {
        MediaPlayer mediaPlayer = this.mLocalPlayer;
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        if (this.mAllowRemote) {
            try {
                return this.mRemotePlayer.isPlaying(this.mRemoteToken);
            } catch (RemoteException e) {
                Log.w(TAG, "Problem checking ringtone: " + e);
                return false;
            }
        }
        Log.w(TAG, "Neither local nor remote playback available");
        return false;
    }

    private boolean playFallbackRingtone() {
        if (this.mAudioManager.getStreamVolume(this.mStreamType) == 0) {
            return false;
        }
        int defaultType = RingtoneManager.getDefaultType(this.mUri);
        if (defaultType == -1 || RingtoneManager.getActualDefaultRingtoneUri(this.mContext, defaultType) != null) {
            try {
                AssetFileDescriptor assetFileDescriptorOpenRawResourceFd = this.mContext.getResources().openRawResourceFd(17825793);
                if (assetFileDescriptorOpenRawResourceFd != null) {
                    this.mLocalPlayer = new MediaPlayer();
                    if (assetFileDescriptorOpenRawResourceFd.getDeclaredLength() < 0) {
                        this.mLocalPlayer.setDataSource(assetFileDescriptorOpenRawResourceFd.getFileDescriptor());
                    } else {
                        this.mLocalPlayer.setDataSource(assetFileDescriptorOpenRawResourceFd.getFileDescriptor(), assetFileDescriptorOpenRawResourceFd.getStartOffset(), assetFileDescriptorOpenRawResourceFd.getDeclaredLength());
                    }
                    this.mLocalPlayer.setAudioStreamType(this.mStreamType);
                    this.mLocalPlayer.prepare();
                    this.mLocalPlayer.start();
                    assetFileDescriptorOpenRawResourceFd.close();
                    return true;
                }
                Log.e(TAG, "Could not load fallback ringtone");
                return false;
            } catch (Resources.NotFoundException unused) {
                Log.e(TAG, "Fallback ringtone does not exist");
                return false;
            } catch (IOException unused2) {
                destroyLocalPlayer();
                Log.e(TAG, "Failed to open fallback ringtone");
                return false;
            }
        }
        Log.w(TAG, "not playing fallback for " + this.mUri);
        return false;
    }

    void setTitle(String str) {
        this.mTitle = str;
    }
}
