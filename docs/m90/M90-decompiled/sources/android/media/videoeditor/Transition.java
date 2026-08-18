package android.media.videoeditor;

import android.media.videoeditor.MediaArtistNativeHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public abstract class Transition {
    public static final int BEHAVIOR_LINEAR = 2;
    private static final int BEHAVIOR_MAX_VALUE = 4;
    public static final int BEHAVIOR_MIDDLE_FAST = 4;
    public static final int BEHAVIOR_MIDDLE_SLOW = 3;
    private static final int BEHAVIOR_MIN_VALUE = 0;
    public static final int BEHAVIOR_SPEED_DOWN = 1;
    public static final int BEHAVIOR_SPEED_UP = 0;
    private final MediaItem mAfterMediaItem;
    private final MediaItem mBeforeMediaItem;
    protected final int mBehavior;
    protected long mDurationMs;
    protected String mFilename;
    protected MediaArtistNativeHelper mNativeHelper;
    private final String mUniqueId;

    private Transition() {
        this(null, null, null, 0L, 0);
    }

    protected Transition(String str, MediaItem mediaItem, MediaItem mediaItem2, long j, int i) {
        if (i < 0 || i > 4) {
            throw new IllegalArgumentException("Invalid behavior: " + i);
        }
        if (mediaItem == null && mediaItem2 == null) {
            throw new IllegalArgumentException("Null media items");
        }
        this.mUniqueId = str;
        this.mAfterMediaItem = mediaItem;
        this.mBeforeMediaItem = mediaItem2;
        this.mDurationMs = j;
        this.mBehavior = i;
        this.mNativeHelper = null;
        if (j > getMaximumDuration()) {
            throw new IllegalArgumentException("The duration is too large");
        }
        if (mediaItem != null) {
            this.mNativeHelper = mediaItem.getNativeContext();
        } else {
            this.mNativeHelper = mediaItem2.getNativeContext();
        }
    }

    public String getId() {
        return this.mUniqueId;
    }

    public MediaItem getAfterMediaItem() {
        return this.mAfterMediaItem;
    }

    public MediaItem getBeforeMediaItem() {
        return this.mBeforeMediaItem;
    }

    public void setDuration(long j) {
        if (j > getMaximumDuration()) {
            throw new IllegalArgumentException("The duration is too large");
        }
        this.mDurationMs = j;
        invalidate();
        this.mNativeHelper.setGeneratePreview(true);
    }

    public long getDuration() {
        return this.mDurationMs;
    }

    public long getMaximumDuration() {
        MediaItem mediaItem = this.mAfterMediaItem;
        if (mediaItem == null) {
            return this.mBeforeMediaItem.getTimelineDuration() / 2;
        }
        if (this.mBeforeMediaItem == null) {
            return mediaItem.getTimelineDuration() / 2;
        }
        return Math.min(mediaItem.getTimelineDuration(), this.mBeforeMediaItem.getTimelineDuration()) / 2;
    }

    public int getBehavior() {
        return this.mBehavior;
    }

    MediaArtistNativeHelper.TransitionSettings getTransitionSettings() {
        MediaArtistNativeHelper.TransitionSettings transitionSettings = new MediaArtistNativeHelper.TransitionSettings();
        transitionSettings.duration = (int) getDuration();
        if (this instanceof TransitionAlpha) {
            TransitionAlpha transitionAlpha = (TransitionAlpha) this;
            transitionSettings.videoTransitionType = 257;
            transitionSettings.audioTransitionType = 1;
            transitionSettings.transitionBehaviour = this.mNativeHelper.getVideoTransitionBehaviour(transitionAlpha.getBehavior());
            transitionSettings.alphaSettings = new MediaArtistNativeHelper.AlphaMagicSettings();
            transitionSettings.slideSettings = null;
            transitionSettings.alphaSettings.file = transitionAlpha.getPNGMaskFilename();
            transitionSettings.alphaSettings.blendingPercent = transitionAlpha.getBlendingPercent();
            transitionSettings.alphaSettings.invertRotation = transitionAlpha.isInvert();
            transitionSettings.alphaSettings.rgbWidth = transitionAlpha.getRGBFileWidth();
            transitionSettings.alphaSettings.rgbHeight = transitionAlpha.getRGBFileHeight();
        } else if (this instanceof TransitionSliding) {
            TransitionSliding transitionSliding = (TransitionSliding) this;
            transitionSettings.videoTransitionType = 258;
            transitionSettings.audioTransitionType = 1;
            transitionSettings.transitionBehaviour = this.mNativeHelper.getVideoTransitionBehaviour(transitionSliding.getBehavior());
            transitionSettings.alphaSettings = null;
            transitionSettings.slideSettings = new MediaArtistNativeHelper.SlideTransitionSettings();
            transitionSettings.slideSettings.direction = this.mNativeHelper.getSlideSettingsDirection(transitionSliding.getDirection());
        } else if (this instanceof TransitionCrossfade) {
            transitionSettings.videoTransitionType = 1;
            transitionSettings.audioTransitionType = 1;
            transitionSettings.transitionBehaviour = this.mNativeHelper.getVideoTransitionBehaviour(((TransitionCrossfade) this).getBehavior());
            transitionSettings.alphaSettings = null;
            transitionSettings.slideSettings = null;
        } else if (this instanceof TransitionFadeBlack) {
            transitionSettings.videoTransitionType = 259;
            transitionSettings.audioTransitionType = 1;
            transitionSettings.transitionBehaviour = this.mNativeHelper.getVideoTransitionBehaviour(((TransitionFadeBlack) this).getBehavior());
            transitionSettings.alphaSettings = null;
            transitionSettings.slideSettings = null;
        }
        return transitionSettings;
    }

    List<MediaArtistNativeHelper.EffectSettings> isEffectandOverlayOverlapping(MediaItem mediaItem, MediaArtistNativeHelper.ClipSettings clipSettings, int i) {
        ArrayList arrayList = new ArrayList();
        Iterator<Overlay> it = mediaItem.getAllOverlays().iterator();
        while (it.hasNext()) {
            MediaArtistNativeHelper.EffectSettings overlaySettings = this.mNativeHelper.getOverlaySettings((OverlayFrame) it.next());
            this.mNativeHelper.adjustEffectsStartTimeAndDuration(overlaySettings, clipSettings.beginCutTime, clipSettings.endCutTime);
            if (overlaySettings.duration != 0) {
                arrayList.add(overlaySettings);
            }
        }
        for (Effect effect : mediaItem.getAllEffects()) {
            if (effect instanceof EffectColor) {
                MediaArtistNativeHelper.EffectSettings effectSettings = this.mNativeHelper.getEffectSettings((EffectColor) effect);
                this.mNativeHelper.adjustEffectsStartTimeAndDuration(effectSettings, clipSettings.beginCutTime, clipSettings.endCutTime);
                if (effectSettings.duration != 0) {
                    if (mediaItem instanceof MediaVideoItem) {
                        effectSettings.fiftiesFrameRate = this.mNativeHelper.GetClosestVideoFrameRate(((MediaVideoItem) mediaItem).getFps());
                    }
                    arrayList.add(effectSettings);
                }
            }
        }
        return arrayList;
    }

    void generate() {
        MediaItem afterMediaItem = getAfterMediaItem();
        MediaItem beforeMediaItem = getBeforeMediaItem();
        MediaArtistNativeHelper.ClipSettings clipSettings = new MediaArtistNativeHelper.ClipSettings();
        MediaArtistNativeHelper.ClipSettings clipSettings2 = new MediaArtistNativeHelper.ClipSettings();
        MediaArtistNativeHelper.EditSettings editSettings = new MediaArtistNativeHelper.EditSettings();
        if (this.mNativeHelper == null) {
            if (afterMediaItem != null) {
                this.mNativeHelper = afterMediaItem.getNativeContext();
            } else if (beforeMediaItem != null) {
                this.mNativeHelper = beforeMediaItem.getNativeContext();
            }
        }
        MediaArtistNativeHelper.TransitionSettings transitionSettings = getTransitionSettings();
        if (afterMediaItem != null && beforeMediaItem != null) {
            clipSettings = afterMediaItem.getClipSettings();
            clipSettings2 = beforeMediaItem.getClipSettings();
            clipSettings.beginCutTime = (int) (((long) clipSettings.endCutTime) - this.mDurationMs);
            clipSettings2.endCutTime = (int) (((long) clipSettings2.beginCutTime) + this.mDurationMs);
            List<MediaArtistNativeHelper.EffectSettings> listIsEffectandOverlayOverlapping = isEffectandOverlayOverlapping(afterMediaItem, clipSettings, 1);
            List<MediaArtistNativeHelper.EffectSettings> listIsEffectandOverlayOverlapping2 = isEffectandOverlayOverlapping(beforeMediaItem, clipSettings2, 2);
            for (int i = 0; i < listIsEffectandOverlayOverlapping2.size(); i++) {
                MediaArtistNativeHelper.EffectSettings effectSettings = listIsEffectandOverlayOverlapping2.get(i);
                effectSettings.startTime = (int) (((long) effectSettings.startTime) + this.mDurationMs);
            }
            editSettings.effectSettingsArray = new MediaArtistNativeHelper.EffectSettings[listIsEffectandOverlayOverlapping.size() + listIsEffectandOverlayOverlapping2.size()];
            int i2 = 0;
            int i3 = 0;
            while (i2 < listIsEffectandOverlayOverlapping.size()) {
                editSettings.effectSettingsArray[i3] = listIsEffectandOverlayOverlapping.get(i2);
                i2++;
                i3++;
            }
            int i4 = 0;
            while (i4 < listIsEffectandOverlayOverlapping2.size()) {
                editSettings.effectSettingsArray[i3] = listIsEffectandOverlayOverlapping2.get(i4);
                i4++;
                i3++;
            }
        } else if (afterMediaItem == null && beforeMediaItem != null) {
            beforeMediaItem.generateBlankFrame(clipSettings);
            clipSettings2 = beforeMediaItem.getClipSettings();
            clipSettings.endCutTime = (int) (this.mDurationMs + 50);
            clipSettings2.endCutTime = (int) (((long) clipSettings2.beginCutTime) + this.mDurationMs);
            List<MediaArtistNativeHelper.EffectSettings> listIsEffectandOverlayOverlapping3 = isEffectandOverlayOverlapping(beforeMediaItem, clipSettings2, 2);
            for (int i5 = 0; i5 < listIsEffectandOverlayOverlapping3.size(); i5++) {
                MediaArtistNativeHelper.EffectSettings effectSettings2 = listIsEffectandOverlayOverlapping3.get(i5);
                effectSettings2.startTime = (int) (((long) effectSettings2.startTime) + this.mDurationMs);
            }
            editSettings.effectSettingsArray = new MediaArtistNativeHelper.EffectSettings[listIsEffectandOverlayOverlapping3.size()];
            int i6 = 0;
            int i7 = 0;
            while (i6 < listIsEffectandOverlayOverlapping3.size()) {
                editSettings.effectSettingsArray[i7] = listIsEffectandOverlayOverlapping3.get(i6);
                i6++;
                i7++;
            }
        } else if (afterMediaItem != null && beforeMediaItem == null) {
            clipSettings = afterMediaItem.getClipSettings();
            afterMediaItem.generateBlankFrame(clipSettings2);
            clipSettings.beginCutTime = (int) (((long) clipSettings.endCutTime) - this.mDurationMs);
            clipSettings2.endCutTime = (int) (this.mDurationMs + 50);
            List<MediaArtistNativeHelper.EffectSettings> listIsEffectandOverlayOverlapping4 = isEffectandOverlayOverlapping(afterMediaItem, clipSettings, 1);
            editSettings.effectSettingsArray = new MediaArtistNativeHelper.EffectSettings[listIsEffectandOverlayOverlapping4.size()];
            int i8 = 0;
            int i9 = 0;
            while (i8 < listIsEffectandOverlayOverlapping4.size()) {
                editSettings.effectSettingsArray[i9] = listIsEffectandOverlayOverlapping4.get(i8);
                i8++;
                i9++;
            }
        }
        editSettings.clipSettingsArray = new MediaArtistNativeHelper.ClipSettings[2];
        editSettings.clipSettingsArray[0] = clipSettings;
        editSettings.clipSettingsArray[1] = clipSettings2;
        editSettings.backgroundMusicSettings = null;
        editSettings.transitionSettingsArray = new MediaArtistNativeHelper.TransitionSettings[1];
        editSettings.transitionSettingsArray[0] = transitionSettings;
        setFilename(this.mNativeHelper.generateTransitionClip(editSettings, this.mUniqueId, afterMediaItem, beforeMediaItem, this));
    }

    void setFilename(String str) {
        this.mFilename = str;
    }

    String getFilename() {
        return this.mFilename;
    }

    void invalidate() {
        if (this.mFilename != null) {
            new File(this.mFilename).delete();
            this.mFilename = null;
        }
    }

    boolean isGenerated() {
        return this.mFilename != null;
    }

    public boolean equals(Object obj) {
        if (obj instanceof Transition) {
            return this.mUniqueId.equals(((Transition) obj).mUniqueId);
        }
        return false;
    }

    public int hashCode() {
        return this.mUniqueId.hashCode();
    }
}
