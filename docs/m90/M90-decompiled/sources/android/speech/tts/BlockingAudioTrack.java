package android.speech.tts;

import android.media.AudioTrack;
import android.util.Log;

/* JADX INFO: loaded from: classes.dex */
class BlockingAudioTrack {
    private static final boolean DBG = false;
    private static final long MAX_PROGRESS_WAIT_MS = 2500;
    private static final long MAX_SLEEP_TIME_MS = 2500;
    private static final int MIN_AUDIO_BUFFER_SIZE = 8192;
    private static final long MIN_SLEEP_TIME_MS = 20;
    private static final String TAG = "TTS.BlockingAudioTrack";
    private final int mAudioFormat;
    private final int mBytesPerFrame;
    private int mBytesWritten;
    private final int mChannelCount;
    private final float mPan;
    private final int mSampleRateInHz;
    private final int mStreamType;
    private final float mVolume;
    private Object mAudioTrackLock = new Object();
    private boolean mIsShortUtterance = false;
    private int mAudioBufferSize = 0;
    private AudioTrack mAudioTrack = null;
    private volatile boolean mStopped = false;

    private static float clip(float f, float f2, float f3) {
        return f > f3 ? f3 : f < f2 ? f2 : f;
    }

    private static final long clip(long j, long j2, long j3) {
        return j < j2 ? j2 : j > j3 ? j3 : j;
    }

    private static int getBytesPerFrame(int i) {
        if (i == 3) {
            return 1;
        }
        return i == 2 ? 2 : -1;
    }

    static int getChannelConfig(int i) {
        if (i == 1) {
            return 4;
        }
        return i == 2 ? 12 : 0;
    }

    BlockingAudioTrack(int i, int i2, int i3, int i4, float f, float f2) {
        this.mBytesWritten = 0;
        this.mStreamType = i;
        this.mSampleRateInHz = i2;
        this.mAudioFormat = i3;
        this.mChannelCount = i4;
        this.mVolume = f;
        this.mPan = f2;
        this.mBytesPerFrame = getBytesPerFrame(i3) * i4;
        this.mBytesWritten = 0;
    }

    public boolean init() {
        AudioTrack audioTrackCreateStreamingAudioTrack = createStreamingAudioTrack();
        synchronized (this.mAudioTrackLock) {
            this.mAudioTrack = audioTrackCreateStreamingAudioTrack;
        }
        return audioTrackCreateStreamingAudioTrack != null;
    }

    public void stop() {
        synchronized (this.mAudioTrackLock) {
            AudioTrack audioTrack = this.mAudioTrack;
            if (audioTrack != null) {
                audioTrack.stop();
            }
            this.mStopped = true;
        }
    }

    public int write(byte[] bArr) {
        AudioTrack audioTrack;
        synchronized (this.mAudioTrackLock) {
            audioTrack = this.mAudioTrack;
        }
        if (audioTrack == null || this.mStopped) {
            return -1;
        }
        int iWriteToAudioTrack = writeToAudioTrack(audioTrack, bArr);
        this.mBytesWritten += iWriteToAudioTrack;
        return iWriteToAudioTrack;
    }

    public void waitAndRelease() {
        AudioTrack audioTrack;
        synchronized (this.mAudioTrackLock) {
            audioTrack = this.mAudioTrack;
        }
        if (audioTrack == null) {
            return;
        }
        if (this.mBytesWritten < this.mAudioBufferSize && !this.mStopped) {
            this.mIsShortUtterance = true;
            audioTrack.stop();
        }
        if (!this.mStopped) {
            blockUntilDone(this.mAudioTrack);
        }
        synchronized (this.mAudioTrackLock) {
            this.mAudioTrack = null;
        }
        audioTrack.release();
    }

    long getAudioLengthMs(int i) {
        return ((i / this.mBytesPerFrame) * 1000) / this.mSampleRateInHz;
    }

    private static int writeToAudioTrack(AudioTrack audioTrack, byte[] bArr) {
        int iWrite;
        if (audioTrack.getPlayState() != 3) {
            audioTrack.play();
        }
        int i = 0;
        while (i < bArr.length && (iWrite = audioTrack.write(bArr, i, bArr.length)) > 0) {
            i += iWrite;
        }
        return i;
    }

    private AudioTrack createStreamingAudioTrack() {
        int channelConfig = getChannelConfig(this.mChannelCount);
        int iMax = Math.max(8192, AudioTrack.getMinBufferSize(this.mSampleRateInHz, channelConfig, this.mAudioFormat));
        AudioTrack audioTrack = new AudioTrack(this.mStreamType, this.mSampleRateInHz, channelConfig, this.mAudioFormat, iMax, 1);
        if (audioTrack.getState() != 1) {
            Log.w(TAG, "Unable to create audio track.");
            audioTrack.release();
            return null;
        }
        this.mAudioBufferSize = iMax;
        setupVolume(audioTrack, this.mVolume, this.mPan);
        return audioTrack;
    }

    private void blockUntilDone(AudioTrack audioTrack) {
        if (this.mBytesWritten <= 0) {
            return;
        }
        if (this.mIsShortUtterance) {
            blockUntilEstimatedCompletion();
        } else {
            blockUntilCompletion(audioTrack);
        }
    }

    private void blockUntilEstimatedCompletion() {
        try {
            Thread.sleep(((this.mBytesWritten / this.mBytesPerFrame) * 1000) / this.mSampleRateInHz);
        } catch (InterruptedException unused) {
        }
    }

    private void blockUntilCompletion(AudioTrack audioTrack) {
        int i = this.mBytesWritten / this.mBytesPerFrame;
        int i2 = -1;
        long j = 0;
        while (true) {
            int playbackHeadPosition = audioTrack.getPlaybackHeadPosition();
            if (playbackHeadPosition >= i || audioTrack.getPlayState() != 3 || this.mStopped) {
                return;
            }
            long jClip = clip(((i - playbackHeadPosition) * 1000) / audioTrack.getSampleRate(), MIN_SLEEP_TIME_MS, 2500L);
            if (playbackHeadPosition == i2) {
                j += jClip;
                if (j > 2500) {
                    Log.w(TAG, "Waited unsuccessfully for 2500ms for AudioTrack to make progress, Aborting");
                    return;
                }
            } else {
                j = 0;
            }
            try {
                Thread.sleep(jClip);
                i2 = playbackHeadPosition;
            } catch (InterruptedException unused) {
                return;
            }
        }
    }

    private static void setupVolume(AudioTrack audioTrack, float f, float f2) {
        float f3;
        float fClip = clip(f, 0.0f, 1.0f);
        float fClip2 = clip(f2, -1.0f, 1.0f);
        if (fClip2 > 0.0f) {
            float f4 = (1.0f - fClip2) * fClip;
            f3 = fClip;
            fClip = f4;
        } else {
            f3 = fClip2 < 0.0f ? (fClip2 + 1.0f) * fClip : fClip;
        }
        if (audioTrack.setStereoVolume(fClip, f3) != 0) {
            Log.e(TAG, "Failed to set volume");
        }
    }
}
