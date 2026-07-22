package android.media.videoeditor;

import android.media.videoeditor.MediaArtistNativeHelper;
import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;

/* JADX INFO: loaded from: classes.dex */
public class AudioTrack {
    private final int mAudioBitrate;
    private final int mAudioChannels;
    private final int mAudioSamplingFrequency;
    private final int mAudioType;
    private String mAudioWaveformFilename;
    private long mBeginBoundaryTimeMs;
    private int mDuckedTrackVolume;
    private int mDuckingThreshold;
    private final long mDurationMs;
    private long mEndBoundaryTimeMs;
    private final String mFilename;
    private boolean mIsDuckingEnabled;
    private boolean mLoop;
    private final MediaArtistNativeHelper mMANativeHelper;
    private boolean mMuted;
    private long mStartTimeMs;
    private long mTimelineDurationMs;
    private final String mUniqueId;
    private int mVolumePercent;
    private SoftReference<WaveformData> mWaveformData;

    private AudioTrack() throws IOException {
        this(null, null, null);
    }

    public AudioTrack(VideoEditor videoEditor, String str, String str2) throws IOException {
        this(videoEditor, str, str2, 0L, 0L, -1L, false, 100, false, false, 0, 0, null);
    }

    AudioTrack(VideoEditor videoEditor, String str, String str2, long j, long j2, long j3, boolean z, int i, boolean z2, boolean z3, int i2, int i3, String str3) throws IOException {
        String str4;
        long j4;
        File file = new File(str2);
        if (!file.exists()) {
            throw new IOException(str2 + " not found ! ");
        }
        if (VideoEditor.MAX_SUPPORTED_FILE_SIZE <= file.length()) {
            throw new IllegalArgumentException("File size is more than 2GB");
        }
        if (videoEditor instanceof VideoEditorImpl) {
            MediaArtistNativeHelper nativeContext = ((VideoEditorImpl) videoEditor).getNativeContext();
            this.mMANativeHelper = nativeContext;
            try {
                MediaArtistNativeHelper.Properties mediaProperties = nativeContext.getMediaProperties(str2);
                int fileType = nativeContext.getFileType(mediaProperties.fileType);
                if (fileType != 0 && fileType != 1 && fileType != 2 && fileType != 3) {
                    throw new IllegalArgumentException("Unsupported input file type: " + fileType);
                }
                int audioCodecType = nativeContext.getAudioCodecType(mediaProperties.audioFormat);
                if (audioCodecType != 1 && audioCodecType != 2 && audioCodecType != 5 && audioCodecType != 8) {
                    throw new IllegalArgumentException("Unsupported Audio Codec Format in Input File");
                }
                if (j3 == -1) {
                    j4 = mediaProperties.audioDuration;
                    str4 = str;
                } else {
                    str4 = str;
                    j4 = j3;
                }
                this.mUniqueId = str4;
                this.mFilename = str2;
                this.mStartTimeMs = j;
                this.mDurationMs = mediaProperties.audioDuration;
                this.mAudioChannels = mediaProperties.audioChannels;
                this.mAudioBitrate = mediaProperties.audioBitrate;
                this.mAudioSamplingFrequency = mediaProperties.audioSamplingFrequency;
                this.mAudioType = mediaProperties.audioFormat;
                this.mTimelineDurationMs = j4 - j2;
                this.mVolumePercent = i;
                this.mBeginBoundaryTimeMs = j2;
                this.mEndBoundaryTimeMs = j4;
                this.mLoop = z;
                this.mMuted = z2;
                this.mIsDuckingEnabled = z3;
                this.mDuckingThreshold = i2;
                this.mDuckedTrackVolume = i3;
                this.mAudioWaveformFilename = str3;
                if (str3 != null) {
                    this.mWaveformData = new SoftReference<>(new WaveformData(str3));
                    return;
                } else {
                    this.mWaveformData = null;
                    return;
                }
            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage() + " : " + str2);
            }
        }
        throw new IllegalArgumentException("editor is not of type VideoEditorImpl");
    }

    public String getId() {
        return this.mUniqueId;
    }

    public String getFilename() {
        return this.mFilename;
    }

    public int getAudioChannels() {
        return this.mAudioChannels;
    }

    public int getAudioType() {
        return this.mAudioType;
    }

    public int getAudioSamplingFrequency() {
        return this.mAudioSamplingFrequency;
    }

    public int getAudioBitrate() {
        return this.mAudioBitrate;
    }

    public void setVolume(int i) {
        if (i > 100) {
            throw new IllegalArgumentException("Volume set exceeds maximum allowed value");
        }
        if (i < 0) {
            throw new IllegalArgumentException("Invalid Volume ");
        }
        this.mMANativeHelper.setGeneratePreview(true);
        this.mVolumePercent = i;
    }

    public int getVolume() {
        return this.mVolumePercent;
    }

    public void setMute(boolean z) {
        this.mMANativeHelper.setGeneratePreview(true);
        this.mMuted = z;
    }

    public boolean isMuted() {
        return this.mMuted;
    }

    public long getStartTime() {
        return this.mStartTimeMs;
    }

    public long getDuration() {
        return this.mDurationMs;
    }

    public long getTimelineDuration() {
        return this.mTimelineDurationMs;
    }

    public void setExtractBoundaries(long j, long j2) {
        long j3 = this.mDurationMs;
        if (j > j3) {
            throw new IllegalArgumentException("Invalid start time");
        }
        if (j2 > j3) {
            throw new IllegalArgumentException("Invalid end time");
        }
        if (j < 0) {
            throw new IllegalArgumentException("Invalid start time; is < 0");
        }
        if (j2 < 0) {
            throw new IllegalArgumentException("Invalid end time; is < 0");
        }
        this.mMANativeHelper.setGeneratePreview(true);
        this.mBeginBoundaryTimeMs = j;
        this.mEndBoundaryTimeMs = j2;
        this.mTimelineDurationMs = j2 - j;
    }

    public long getBoundaryBeginTime() {
        return this.mBeginBoundaryTimeMs;
    }

    public long getBoundaryEndTime() {
        return this.mEndBoundaryTimeMs;
    }

    public void enableLoop() {
        if (this.mLoop) {
            return;
        }
        this.mMANativeHelper.setGeneratePreview(true);
        this.mLoop = true;
    }

    public void disableLoop() {
        if (this.mLoop) {
            this.mMANativeHelper.setGeneratePreview(true);
            this.mLoop = false;
        }
    }

    public boolean isLooping() {
        return this.mLoop;
    }

    public void disableDucking() {
        if (this.mIsDuckingEnabled) {
            this.mMANativeHelper.setGeneratePreview(true);
            this.mIsDuckingEnabled = false;
        }
    }

    public void enableDucking(int i, int i2) {
        if (i < 0 || i > 90) {
            throw new IllegalArgumentException("Invalid threshold value: " + i);
        }
        if (i2 < 0 || i2 > 100) {
            throw new IllegalArgumentException("Invalid duckedTrackVolume value: " + i2);
        }
        this.mMANativeHelper.setGeneratePreview(true);
        this.mDuckingThreshold = i;
        this.mDuckedTrackVolume = i2;
        this.mIsDuckingEnabled = true;
    }

    public boolean isDuckingEnabled() {
        return this.mIsDuckingEnabled;
    }

    public int getDuckingThreshhold() {
        return this.mDuckingThreshold;
    }

    public int getDuckedTrackVolume() {
        return this.mDuckedTrackVolume;
    }

    public void extractAudioWaveform(ExtractAudioWaveformProgressListener extractAudioWaveformProgressListener) throws IOException {
        int i;
        int i2;
        int i3;
        int i4;
        if (this.mAudioWaveformFilename == null) {
            String str = String.format(this.mMANativeHelper.getProjectPath() + "/audioWaveformFile-" + getId() + ".dat", new Object[0]);
            int audioCodecType = this.mMANativeHelper.getAudioCodecType(this.mAudioType);
            if (audioCodecType != 1) {
                if (audioCodecType == 2) {
                    i3 = 32;
                    i4 = 1024;
                } else if (audioCodecType == 5) {
                    i3 = 36;
                    i4 = MediaProperties.SAMPLES_PER_FRAME_MP3;
                } else {
                    if (audioCodecType != 8) {
                        throw new IllegalStateException("Unsupported codec type: " + audioCodecType);
                    }
                    i3 = 10;
                    i4 = 320;
                }
                i2 = i3;
                i = i4;
            } else {
                i = 160;
                i2 = 5;
            }
            this.mMANativeHelper.generateAudioGraph(this.mUniqueId, this.mFilename, str, i2, 2, i, extractAudioWaveformProgressListener, false);
            this.mAudioWaveformFilename = str;
        }
        this.mWaveformData = new SoftReference<>(new WaveformData(this.mAudioWaveformFilename));
    }

    String getAudioWaveformFilename() {
        return this.mAudioWaveformFilename;
    }

    void invalidate() {
        if (this.mAudioWaveformFilename != null) {
            new File(this.mAudioWaveformFilename).delete();
            this.mAudioWaveformFilename = null;
            this.mWaveformData = null;
        }
    }

    public WaveformData getWaveformData() throws IOException {
        SoftReference<WaveformData> softReference = this.mWaveformData;
        if (softReference == null) {
            return null;
        }
        WaveformData waveformData = softReference.get();
        if (waveformData != null) {
            return waveformData;
        }
        if (this.mAudioWaveformFilename == null) {
            return null;
        }
        try {
            WaveformData waveformData2 = new WaveformData(this.mAudioWaveformFilename);
            this.mWaveformData = new SoftReference<>(waveformData2);
            return waveformData2;
        } catch (IOException e) {
            throw e;
        }
    }

    public boolean equals(Object obj) {
        if (obj instanceof AudioTrack) {
            return this.mUniqueId.equals(((AudioTrack) obj).mUniqueId);
        }
        return false;
    }

    public int hashCode() {
        return this.mUniqueId.hashCode();
    }
}
