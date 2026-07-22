package android.media.videoeditor;

import android.graphics.Bitmap;
import android.media.videoeditor.MediaArtistNativeHelper;
import android.media.videoeditor.MediaItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;

/* JADX INFO: loaded from: classes.dex */
public class MediaVideoItem extends MediaItem {
    private final int mAspectRatio;
    private final int mAudioBitrate;
    private final int mAudioChannels;
    private final int mAudioSamplingFrequency;
    private final int mAudioType;
    private String mAudioWaveformFilename;
    private long mBeginBoundaryTimeMs;
    private final long mDurationMs;
    private long mEndBoundaryTimeMs;
    private final int mFileType;
    private final int mFps;
    private final int mHeight;
    private MediaArtistNativeHelper mMANativeHelper;
    private boolean mMuted;
    private final int mVideoBitrate;
    private VideoEditorImpl mVideoEditor;
    private final int mVideoLevel;
    private final int mVideoProfile;
    private final int mVideoRotationDegree;
    private final int mVideoType;
    private int mVolumePercentage;
    private SoftReference<WaveformData> mWaveformData;
    private final int mWidth;

    private MediaVideoItem() throws IOException {
        this(null, null, null, 0);
    }

    public MediaVideoItem(VideoEditor videoEditor, String str, String str2, int i) throws IOException {
        this(videoEditor, str, str2, i, 0L, -1L, 100, false, null);
    }

    MediaVideoItem(VideoEditor videoEditor, String str, String str2, int i, long j, long j2, int i2, boolean z, String str3) throws IOException {
        super(videoEditor, str, str2, i);
        if (videoEditor instanceof VideoEditorImpl) {
            VideoEditorImpl videoEditorImpl = (VideoEditorImpl) videoEditor;
            this.mMANativeHelper = videoEditorImpl.getNativeContext();
            this.mVideoEditor = videoEditorImpl;
        }
        try {
            MediaArtistNativeHelper.Properties mediaProperties = this.mMANativeHelper.getMediaProperties(str2);
            VideoEditorProfile videoEditorProfile = VideoEditorProfile.get();
            if (videoEditorProfile == null) {
                throw new RuntimeException("Can't get the video editor profile");
            }
            int i3 = videoEditorProfile.maxInputVideoFrameWidth;
            int i4 = videoEditorProfile.maxInputVideoFrameHeight;
            if (mediaProperties.width > i3 || mediaProperties.height > i4) {
                throw new IllegalArgumentException("Unsupported import resolution. Supported maximum width:" + i3 + " height:" + i4 + ", current width:" + mediaProperties.width + " height:" + mediaProperties.height);
            }
            if (!mediaProperties.profileSupported) {
                throw new IllegalArgumentException("Unsupported video profile " + mediaProperties.profile);
            }
            if (!mediaProperties.levelSupported) {
                throw new IllegalArgumentException("Unsupported video level " + mediaProperties.level);
            }
            int fileType = this.mMANativeHelper.getFileType(mediaProperties.fileType);
            if (fileType != 0 && fileType != 1 && fileType != 10) {
                throw new IllegalArgumentException("Unsupported Input File Type");
            }
            int videoCodecType = this.mMANativeHelper.getVideoCodecType(mediaProperties.videoFormat);
            if (videoCodecType != 1 && videoCodecType != 2 && videoCodecType != 3) {
                throw new IllegalArgumentException("Unsupported Video Codec Format in Input File");
            }
            this.mWidth = mediaProperties.width;
            this.mHeight = mediaProperties.height;
            this.mAspectRatio = this.mMANativeHelper.getAspectRatio(mediaProperties.width, mediaProperties.height);
            this.mFileType = this.mMANativeHelper.getFileType(mediaProperties.fileType);
            this.mVideoType = this.mMANativeHelper.getVideoCodecType(mediaProperties.videoFormat);
            this.mVideoProfile = mediaProperties.profile;
            this.mVideoLevel = mediaProperties.level;
            long j3 = mediaProperties.videoDuration;
            this.mDurationMs = j3;
            this.mVideoBitrate = mediaProperties.videoBitrate;
            this.mAudioBitrate = mediaProperties.audioBitrate;
            this.mFps = (int) mediaProperties.averageFrameRate;
            this.mAudioType = this.mMANativeHelper.getAudioCodecType(mediaProperties.audioFormat);
            this.mAudioChannels = mediaProperties.audioChannels;
            this.mAudioSamplingFrequency = mediaProperties.audioSamplingFrequency;
            this.mBeginBoundaryTimeMs = j;
            this.mEndBoundaryTimeMs = j2 == -1 ? j3 : j2;
            this.mVolumePercentage = i2;
            this.mMuted = z;
            this.mAudioWaveformFilename = str3;
            if (str3 != null) {
                this.mWaveformData = new SoftReference<>(new WaveformData(str3));
            } else {
                this.mWaveformData = null;
            }
            this.mVideoRotationDegree = mediaProperties.videoRotation;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage() + " : " + str2);
        }
    }

    public void setExtractBoundaries(long j, long j2) {
        long j3 = this.mDurationMs;
        if (j > j3) {
            throw new IllegalArgumentException("setExtractBoundaries: Invalid start time");
        }
        if (j2 > j3) {
            throw new IllegalArgumentException("setExtractBoundaries: Invalid end time");
        }
        if (j2 != -1 && j >= j2) {
            throw new IllegalArgumentException("setExtractBoundaries: Start time is greater than end time");
        }
        if (j < 0 || (j2 != -1 && j2 < 0)) {
            throw new IllegalArgumentException("setExtractBoundaries: Start time or end time is negative");
        }
        this.mMANativeHelper.setGeneratePreview(true);
        if (j != this.mBeginBoundaryTimeMs && this.mBeginTransition != null) {
            this.mBeginTransition.invalidate();
        }
        if (j2 != this.mEndBoundaryTimeMs && this.mEndTransition != null) {
            this.mEndTransition.invalidate();
        }
        this.mBeginBoundaryTimeMs = j;
        this.mEndBoundaryTimeMs = j2;
        adjustTransitions();
        this.mVideoEditor.updateTimelineDuration();
    }

    public long getBoundaryBeginTime() {
        return this.mBeginBoundaryTimeMs;
    }

    public long getBoundaryEndTime() {
        return this.mEndBoundaryTimeMs;
    }

    @Override // android.media.videoeditor.MediaItem
    public void addEffect(Effect effect) {
        if (effect instanceof EffectKenBurns) {
            throw new IllegalArgumentException("Ken Burns effects cannot be applied to MediaVideoItem");
        }
        super.addEffect(effect);
    }

    @Override // android.media.videoeditor.MediaItem
    public Bitmap getThumbnail(int i, int i2, long j) {
        int i3;
        int i4;
        if (j > this.mDurationMs) {
            throw new IllegalArgumentException("Time Exceeds duration");
        }
        if (j < 0) {
            throw new IllegalArgumentException("Invalid Time duration");
        }
        if (i <= 0 || i2 <= 0) {
            throw new IllegalArgumentException("Invalid Dimensions");
        }
        int i5 = this.mVideoRotationDegree;
        if (i5 == 90 || i5 == 270) {
            i3 = i;
            i4 = i2;
        } else {
            i4 = i;
            i3 = i2;
        }
        return this.mMANativeHelper.getPixels(getFilename(), i4, i3, j, this.mVideoRotationDegree);
    }

    @Override // android.media.videoeditor.MediaItem
    public void getThumbnailList(int i, int i2, long j, long j2, int i3, int[] iArr, MediaItem.GetThumbnailListCallback getThumbnailListCallback) throws IOException {
        int i4;
        int i5;
        if (j > j2) {
            throw new IllegalArgumentException("Start time is greater than end time");
        }
        if (j2 > this.mDurationMs) {
            throw new IllegalArgumentException("End time is greater than file duration");
        }
        if (i2 <= 0 || i <= 0) {
            throw new IllegalArgumentException("Invalid dimension");
        }
        int i6 = this.mVideoRotationDegree;
        if (i6 == 90 || i6 == 270) {
            i4 = i;
            i5 = i2;
        } else {
            i5 = i;
            i4 = i2;
        }
        this.mMANativeHelper.getPixelsList(getFilename(), i5, i4, j, j2, i3, iArr, getThumbnailListCallback, this.mVideoRotationDegree);
    }

    @Override // android.media.videoeditor.MediaItem
    void invalidateTransitions(long j, long j2) {
        if (this.mBeginTransition != null && isOverlapping(j, j2, this.mBeginBoundaryTimeMs, this.mBeginTransition.getDuration())) {
            this.mBeginTransition.invalidate();
        }
        if (this.mEndTransition != null) {
            long duration = this.mEndTransition.getDuration();
            if (isOverlapping(j, j2, this.mEndBoundaryTimeMs - duration, duration)) {
                this.mEndTransition.invalidate();
            }
        }
    }

    @Override // android.media.videoeditor.MediaItem
    void invalidateTransitions(long j, long j2, long j3, long j4) {
        if (this.mBeginTransition != null) {
            long duration = this.mBeginTransition.getDuration();
            boolean zIsOverlapping = isOverlapping(j, j2, this.mBeginBoundaryTimeMs, duration);
            boolean zIsOverlapping2 = isOverlapping(j3, j4, this.mBeginBoundaryTimeMs, duration);
            if (zIsOverlapping2 != zIsOverlapping) {
                this.mBeginTransition.invalidate();
            } else if (zIsOverlapping2 && (j != j3 || j + j2 <= duration || j3 + j4 <= duration)) {
                this.mBeginTransition.invalidate();
            }
        }
        if (this.mEndTransition != null) {
            long duration2 = this.mEndTransition.getDuration();
            boolean zIsOverlapping3 = isOverlapping(j, j2, this.mEndBoundaryTimeMs - duration2, duration2);
            boolean zIsOverlapping4 = isOverlapping(j3, j4, this.mEndBoundaryTimeMs - duration2, duration2);
            if (zIsOverlapping4 != zIsOverlapping3) {
                this.mEndTransition.invalidate();
                return;
            }
            if (zIsOverlapping4) {
                if (j + j2 == j3 + j4) {
                    long j5 = this.mEndBoundaryTimeMs;
                    if (j <= j5 - duration2 && j3 <= j5 - duration2) {
                        return;
                    }
                }
                this.mEndTransition.invalidate();
            }
        }
    }

    @Override // android.media.videoeditor.MediaItem
    public int getAspectRatio() {
        return this.mAspectRatio;
    }

    @Override // android.media.videoeditor.MediaItem
    public int getFileType() {
        return this.mFileType;
    }

    @Override // android.media.videoeditor.MediaItem
    public int getWidth() {
        int i = this.mVideoRotationDegree;
        if (i == 90 || i == 270) {
            return this.mHeight;
        }
        return this.mWidth;
    }

    @Override // android.media.videoeditor.MediaItem
    public int getHeight() {
        int i = this.mVideoRotationDegree;
        if (i == 90 || i == 270) {
            return this.mWidth;
        }
        return this.mHeight;
    }

    @Override // android.media.videoeditor.MediaItem
    public long getDuration() {
        return this.mDurationMs;
    }

    @Override // android.media.videoeditor.MediaItem
    public long getTimelineDuration() {
        return this.mEndBoundaryTimeMs - this.mBeginBoundaryTimeMs;
    }

    public long renderFrame(SurfaceHolder surfaceHolder, long j) {
        if (surfaceHolder == null) {
            throw new IllegalArgumentException("Surface Holder is null");
        }
        if (j > this.mDurationMs || j < 0) {
            throw new IllegalArgumentException("requested time not correct");
        }
        Surface surface = surfaceHolder.getSurface();
        if (surface == null) {
            throw new RuntimeException("Surface could not be retrieved from Surface holder");
        }
        if (this.mFilename != null) {
            return this.mMANativeHelper.renderMediaItemPreviewFrame(surface, this.mFilename, j, this.mWidth, this.mHeight);
        }
        return 0L;
    }

    public void extractAudioWaveform(ExtractAudioWaveformProgressListener extractAudioWaveformProgressListener) throws IOException {
        int i;
        int i2;
        int i3;
        int i4;
        String projectPath = this.mMANativeHelper.getProjectPath();
        if (this.mAudioWaveformFilename == null) {
            String str = String.format(projectPath + "/audioWaveformFile-" + getId() + ".dat", new Object[0]);
            if (this.mMANativeHelper.getAudioCodecType(this.mAudioType) == 1) {
                i3 = 5;
                i4 = 160;
            } else if (this.mMANativeHelper.getAudioCodecType(this.mAudioType) == 8) {
                i3 = 10;
                i4 = 320;
            } else if (this.mMANativeHelper.getAudioCodecType(this.mAudioType) == 2) {
                i3 = 32;
                i4 = 1024;
            } else {
                i = 0;
                i2 = 0;
                this.mMANativeHelper.generateAudioGraph(getId(), this.mFilename, str, i, 2, i2, extractAudioWaveformProgressListener, true);
                this.mAudioWaveformFilename = str;
            }
            i = i3;
            i2 = i4;
            this.mMANativeHelper.generateAudioGraph(getId(), this.mFilename, str, i, 2, i2, extractAudioWaveformProgressListener, true);
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

    public void setVolume(int i) {
        if (i < 0 || i > 100) {
            throw new IllegalArgumentException("Invalid volume");
        }
        this.mVolumePercentage = i;
    }

    public int getVolume() {
        return this.mVolumePercentage;
    }

    public void setMute(boolean z) {
        this.mMANativeHelper.setGeneratePreview(true);
        this.mMuted = z;
        if (this.mBeginTransition != null) {
            this.mBeginTransition.invalidate();
        }
        if (this.mEndTransition != null) {
            this.mEndTransition.invalidate();
        }
    }

    public boolean isMuted() {
        return this.mMuted;
    }

    public int getVideoType() {
        return this.mVideoType;
    }

    public int getVideoProfile() {
        return this.mVideoProfile;
    }

    public int getVideoLevel() {
        return this.mVideoLevel;
    }

    public int getVideoBitrate() {
        return this.mVideoBitrate;
    }

    public int getAudioBitrate() {
        return this.mAudioBitrate;
    }

    public int getFps() {
        return this.mFps;
    }

    public int getAudioType() {
        return this.mAudioType;
    }

    public int getAudioChannels() {
        return this.mAudioChannels;
    }

    public int getAudioSamplingFrequency() {
        return this.mAudioSamplingFrequency;
    }

    MediaArtistNativeHelper.ClipSettings getVideoClipProperties() {
        MediaArtistNativeHelper.ClipSettings clipSettings = new MediaArtistNativeHelper.ClipSettings();
        clipSettings.clipPath = getFilename();
        clipSettings.fileType = this.mMANativeHelper.getMediaItemFileType(getFileType());
        clipSettings.beginCutTime = (int) getBoundaryBeginTime();
        clipSettings.endCutTime = (int) getBoundaryEndTime();
        clipSettings.mediaRendering = this.mMANativeHelper.getMediaItemRenderingMode(getRenderingMode());
        clipSettings.rotationDegree = this.mVideoRotationDegree;
        return clipSettings;
    }
}
