package android.media.videoeditor;

import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public abstract class Overlay {
    protected long mDurationMs;
    private final MediaItem mMediaItem;
    protected long mStartTimeMs;
    private final String mUniqueId;
    private final Map<String, String> mUserAttributes;

    private Overlay() {
        this(null, null, 0L, 0L);
    }

    public Overlay(MediaItem mediaItem, String str, long j, long j2) {
        if (mediaItem == null) {
            throw new IllegalArgumentException("Media item cannot be null");
        }
        if (j < 0 || j2 < 0) {
            throw new IllegalArgumentException("Invalid start time and/OR duration");
        }
        if (j + j2 > mediaItem.getDuration()) {
            throw new IllegalArgumentException("Invalid start time and duration");
        }
        this.mMediaItem = mediaItem;
        this.mUniqueId = str;
        this.mStartTimeMs = j;
        this.mDurationMs = j2;
        this.mUserAttributes = new HashMap();
    }

    public String getId() {
        return this.mUniqueId;
    }

    public long getDuration() {
        return this.mDurationMs;
    }

    public void setDuration(long j) {
        if (j < 0) {
            throw new IllegalArgumentException("Invalid duration");
        }
        if (this.mStartTimeMs + j > this.mMediaItem.getDuration()) {
            throw new IllegalArgumentException("Duration is too large");
        }
        getMediaItem().getNativeContext().setGeneratePreview(true);
        long j2 = this.mDurationMs;
        this.mDurationMs = j;
        MediaItem mediaItem = this.mMediaItem;
        long j3 = this.mStartTimeMs;
        mediaItem.invalidateTransitions(j3, j2, j3, j);
    }

    public long getStartTime() {
        return this.mStartTimeMs;
    }

    public void setStartTime(long j) {
        if (this.mDurationMs + j > this.mMediaItem.getDuration()) {
            throw new IllegalArgumentException("Start time is too large");
        }
        getMediaItem().getNativeContext().setGeneratePreview(true);
        long j2 = this.mStartTimeMs;
        this.mStartTimeMs = j;
        MediaItem mediaItem = this.mMediaItem;
        long j3 = this.mDurationMs;
        mediaItem.invalidateTransitions(j2, j3, j, j3);
    }

    public void setStartTimeAndDuration(long j, long j2) {
        if (j + j2 > this.mMediaItem.getDuration()) {
            throw new IllegalArgumentException("Invalid start time or duration");
        }
        getMediaItem().getNativeContext().setGeneratePreview(true);
        long j3 = this.mStartTimeMs;
        long j4 = this.mDurationMs;
        this.mStartTimeMs = j;
        this.mDurationMs = j2;
        this.mMediaItem.invalidateTransitions(j3, j4, j, j2);
    }

    public MediaItem getMediaItem() {
        return this.mMediaItem;
    }

    public void setUserAttribute(String str, String str2) {
        this.mUserAttributes.put(str, str2);
    }

    public Map<String, String> getUserAttributes() {
        return this.mUserAttributes;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Overlay) {
            return this.mUniqueId.equals(((Overlay) obj).mUniqueId);
        }
        return false;
    }

    public int hashCode() {
        return this.mUniqueId.hashCode();
    }
}
