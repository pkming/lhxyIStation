package android.media;

import android.media.SoundPool;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
public class MediaActionSound {
    public static final int FOCUS_COMPLETE = 1;
    private static final int NUM_MEDIA_SOUND_STREAMS = 1;
    public static final int SHUTTER_CLICK = 0;
    private static final String[] SOUND_FILES = {"/system/media/audio/ui/camera_click.ogg", "/system/media/audio/ui/camera_focus.ogg", "/system/media/audio/ui/VideoRecord.ogg", "/system/media/audio/ui/VideoRecord.ogg"};
    private static final int SOUND_NOT_LOADED = -1;
    public static final int START_VIDEO_RECORDING = 2;
    public static final int STOP_VIDEO_RECORDING = 3;
    private static final String TAG = "MediaActionSound";
    private SoundPool.OnLoadCompleteListener mLoadCompleteListener = new SoundPool.OnLoadCompleteListener() { // from class: android.media.MediaActionSound.1
        @Override // android.media.SoundPool.OnLoadCompleteListener
        public void onLoadComplete(SoundPool soundPool, int i, int i2) {
            if (i2 == 0) {
                if (MediaActionSound.this.mSoundIdToPlay == i) {
                    soundPool.play(i, 1.0f, 1.0f, 0, 0, 1.0f);
                    MediaActionSound.this.mSoundIdToPlay = -1;
                    return;
                }
                return;
            }
            Log.e(MediaActionSound.TAG, "Unable to load sound for playback (status: " + i2 + ")");
        }
    };
    private int mSoundIdToPlay;
    private int[] mSoundIds;
    private SoundPool mSoundPool;

    public MediaActionSound() {
        int i = 0;
        SoundPool soundPool = new SoundPool(1, 7, 0);
        this.mSoundPool = soundPool;
        soundPool.setOnLoadCompleteListener(this.mLoadCompleteListener);
        this.mSoundIds = new int[SOUND_FILES.length];
        while (true) {
            int[] iArr = this.mSoundIds;
            if (i < iArr.length) {
                iArr[i] = -1;
                i++;
            } else {
                this.mSoundIdToPlay = -1;
                return;
            }
        }
    }

    public synchronized void load(int i) {
        if (i >= 0) {
            String[] strArr = SOUND_FILES;
            if (i < strArr.length) {
                int[] iArr = this.mSoundIds;
                if (iArr[i] == -1) {
                    iArr[i] = this.mSoundPool.load(strArr[i], 1);
                }
            }
        }
        throw new RuntimeException("Unknown sound requested: " + i);
    }

    public synchronized void play(int i) {
        if (i >= 0) {
            String[] strArr = SOUND_FILES;
            if (i < strArr.length) {
                int[] iArr = this.mSoundIds;
                if (iArr[i] == -1) {
                    int iLoad = this.mSoundPool.load(strArr[i], 1);
                    this.mSoundIdToPlay = iLoad;
                    this.mSoundIds[i] = iLoad;
                } else {
                    this.mSoundPool.play(iArr[i], 1.0f, 1.0f, 0, 0, 1.0f);
                }
            }
        }
        throw new RuntimeException("Unknown sound requested: " + i);
    }

    public void release() {
        SoundPool soundPool = this.mSoundPool;
        if (soundPool != null) {
            soundPool.release();
            this.mSoundPool = null;
        }
    }
}
