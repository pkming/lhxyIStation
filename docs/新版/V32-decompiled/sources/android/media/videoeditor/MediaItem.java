package android.media.videoeditor;

import android.graphics.Bitmap;
import android.media.videoeditor.MediaArtistNativeHelper;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public abstract class MediaItem {
    public static final int END_OF_FILE = -1;
    public static final int RENDERING_MODE_BLACK_BORDER = 0;
    public static final int RENDERING_MODE_CROPPING = 2;
    public static final int RENDERING_MODE_STRETCH = 1;
    protected Transition mBeginTransition;
    private final List<Effect> mEffects;
    protected Transition mEndTransition;
    protected final String mFilename;
    protected String mGeneratedImageClip;
    private final MediaArtistNativeHelper mMANativeHelper;
    private final List<Overlay> mOverlays;
    private final String mProjectPath;
    protected boolean mRegenerateClip;
    private int mRenderingMode;
    private final String mUniqueId;
    private boolean mBlankFrameGenerated = false;
    private String mBlankFrameFilename = null;

    public interface GetThumbnailListCallback {
        void onThumbnail(Bitmap bitmap, int i);
    }

    public abstract int getAspectRatio();

    public abstract long getDuration();

    public abstract int getFileType();

    public abstract int getHeight();

    public abstract Bitmap getThumbnail(int i, int i2, long j) throws IOException;

    public abstract void getThumbnailList(int i, int i2, long j, long j2, int i3, int[] iArr, GetThumbnailListCallback getThumbnailListCallback) throws IOException;

    public abstract long getTimelineDuration();

    public abstract int getWidth();

    abstract void invalidateTransitions(long j, long j2);

    abstract void invalidateTransitions(long j, long j2, long j3, long j4);

    protected boolean isOverlapping(long j, long j2, long j3, long j4) {
        return j2 + j > j3 && j < j3 + j4;
    }

    protected MediaItem(VideoEditor videoEditor, String str, String str2, int i) throws IOException {
        if (str2 == null) {
            throw new IllegalArgumentException("MediaItem : filename is null");
        }
        File file = new File(str2);
        if (!file.exists()) {
            throw new IOException(str2 + " not found ! ");
        }
        if (VideoEditor.MAX_SUPPORTED_FILE_SIZE <= file.length()) {
            throw new IllegalArgumentException("File size is more than 2GB");
        }
        this.mUniqueId = str;
        this.mFilename = str2;
        this.mRenderingMode = i;
        this.mEffects = new ArrayList();
        this.mOverlays = new ArrayList();
        this.mBeginTransition = null;
        this.mEndTransition = null;
        this.mMANativeHelper = ((VideoEditorImpl) videoEditor).getNativeContext();
        this.mProjectPath = videoEditor.getPath();
        this.mRegenerateClip = false;
        this.mGeneratedImageClip = null;
    }

    public String getId() {
        return this.mUniqueId;
    }

    public String getFilename() {
        return this.mFilename;
    }

    public void setRenderingMode(int i) {
        if (i != 0 && i != 1 && i != 2) {
            throw new IllegalArgumentException("Invalid Rendering Mode");
        }
        this.mMANativeHelper.setGeneratePreview(true);
        this.mRenderingMode = i;
        Transition transition = this.mBeginTransition;
        if (transition != null) {
            transition.invalidate();
        }
        Transition transition2 = this.mEndTransition;
        if (transition2 != null) {
            transition2.invalidate();
        }
        Iterator<Overlay> it = this.mOverlays.iterator();
        while (it.hasNext()) {
            ((OverlayFrame) it.next()).invalidateGeneratedFiles();
        }
    }

    public int getRenderingMode() {
        return this.mRenderingMode;
    }

    void setBeginTransition(Transition transition) {
        this.mBeginTransition = transition;
    }

    public Transition getBeginTransition() {
        return this.mBeginTransition;
    }

    void setEndTransition(Transition transition) {
        this.mEndTransition = transition;
    }

    public Transition getEndTransition() {
        return this.mEndTransition;
    }

    public void addEffect(Effect effect) {
        if (effect == null) {
            throw new IllegalArgumentException("NULL effect cannot be applied");
        }
        if (effect.getMediaItem() != this) {
            throw new IllegalArgumentException("Media item mismatch");
        }
        if (this.mEffects.contains(effect)) {
            throw new IllegalArgumentException("Effect already exists: " + effect.getId());
        }
        if (effect.getStartTime() + effect.getDuration() > getDuration()) {
            throw new IllegalArgumentException("Effect start time + effect duration > media clip duration");
        }
        this.mMANativeHelper.setGeneratePreview(true);
        this.mEffects.add(effect);
        invalidateTransitions(effect.getStartTime(), effect.getDuration());
        if (effect instanceof EffectKenBurns) {
            this.mRegenerateClip = true;
        }
    }

    public Effect removeEffect(String str) {
        for (Effect effect : this.mEffects) {
            if (effect.getId().equals(str)) {
                this.mMANativeHelper.setGeneratePreview(true);
                this.mEffects.remove(effect);
                invalidateTransitions(effect.getStartTime(), effect.getDuration());
                if (effect instanceof EffectKenBurns) {
                    if (this.mGeneratedImageClip != null) {
                        new File(this.mGeneratedImageClip).delete();
                        this.mGeneratedImageClip = null;
                    }
                    this.mRegenerateClip = false;
                }
                return effect;
            }
        }
        return null;
    }

    void setGeneratedImageClip(String str) {
        this.mGeneratedImageClip = str;
    }

    String getGeneratedImageClip() {
        return this.mGeneratedImageClip;
    }

    public Effect getEffect(String str) {
        for (Effect effect : this.mEffects) {
            if (effect.getId().equals(str)) {
                return effect;
            }
        }
        return null;
    }

    public List<Effect> getAllEffects() {
        return this.mEffects;
    }

    public void addOverlay(Overlay overlay) throws IOException {
        int scaledHeight;
        int width;
        if (overlay == null) {
            throw new IllegalArgumentException("NULL Overlay cannot be applied");
        }
        if (overlay.getMediaItem() != this) {
            throw new IllegalArgumentException("Media item mismatch");
        }
        if (this.mOverlays.contains(overlay)) {
            throw new IllegalArgumentException("Overlay already exists: " + overlay.getId());
        }
        if (overlay.getStartTime() + overlay.getDuration() > getDuration()) {
            throw new IllegalArgumentException("Overlay start time + overlay duration > media clip duration");
        }
        if (overlay instanceof OverlayFrame) {
            OverlayFrame overlayFrame = (OverlayFrame) overlay;
            Bitmap bitmap = overlayFrame.getBitmap();
            if (bitmap == null) {
                throw new IllegalArgumentException("Overlay bitmap not specified");
            }
            if (this instanceof MediaVideoItem) {
                width = getWidth();
                scaledHeight = getHeight();
            } else {
                MediaImageItem mediaImageItem = (MediaImageItem) this;
                int scaledWidth = mediaImageItem.getScaledWidth();
                scaledHeight = mediaImageItem.getScaledHeight();
                width = scaledWidth;
            }
            if (bitmap.getWidth() != width || bitmap.getHeight() != scaledHeight) {
                throw new IllegalArgumentException("Bitmap dimensions must match media item dimensions");
            }
            this.mMANativeHelper.setGeneratePreview(true);
            overlayFrame.save(this.mProjectPath);
            this.mOverlays.add(overlay);
            invalidateTransitions(overlay.getStartTime(), overlay.getDuration());
            return;
        }
        throw new IllegalArgumentException("Overlay not supported");
    }

    void setRegenerateClip(boolean z) {
        this.mRegenerateClip = z;
    }

    boolean getRegenerateClip() {
        return this.mRegenerateClip;
    }

    public Overlay removeOverlay(String str) {
        for (Overlay overlay : this.mOverlays) {
            if (overlay.getId().equals(str)) {
                this.mMANativeHelper.setGeneratePreview(true);
                this.mOverlays.remove(overlay);
                if (overlay instanceof OverlayFrame) {
                    ((OverlayFrame) overlay).invalidate();
                }
                invalidateTransitions(overlay.getStartTime(), overlay.getDuration());
                return overlay;
            }
        }
        return null;
    }

    public Overlay getOverlay(String str) {
        for (Overlay overlay : this.mOverlays) {
            if (overlay.getId().equals(str)) {
                return overlay;
            }
        }
        return null;
    }

    public List<Overlay> getAllOverlays() {
        return this.mOverlays;
    }

    public Bitmap[] getThumbnailList(int i, int i2, long j, long j2, int i3) throws IOException {
        final Bitmap[] bitmapArr = new Bitmap[i3];
        int[] iArr = new int[i3];
        for (int i4 = 0; i4 < i3; i4++) {
            iArr[i4] = i4;
        }
        getThumbnailList(i, i2, j, j2, i3, iArr, new GetThumbnailListCallback() { // from class: android.media.videoeditor.MediaItem.1
            @Override // android.media.videoeditor.MediaItem.GetThumbnailListCallback
            public void onThumbnail(Bitmap bitmap, int i5) {
                bitmapArr[i5] = bitmap;
            }
        });
        return bitmapArr;
    }

    public boolean equals(Object obj) {
        if (obj instanceof MediaItem) {
            return this.mUniqueId.equals(((MediaItem) obj).mUniqueId);
        }
        return false;
    }

    public int hashCode() {
        return this.mUniqueId.hashCode();
    }

    protected void adjustTransitions() {
        Transition transition = this.mBeginTransition;
        if (transition != null) {
            long maximumDuration = transition.getMaximumDuration();
            if (this.mBeginTransition.getDuration() > maximumDuration) {
                this.mBeginTransition.setDuration(maximumDuration);
            }
        }
        Transition transition2 = this.mEndTransition;
        if (transition2 != null) {
            long maximumDuration2 = transition2.getMaximumDuration();
            if (this.mEndTransition.getDuration() > maximumDuration2) {
                this.mEndTransition.setDuration(maximumDuration2);
            }
        }
    }

    MediaArtistNativeHelper getNativeContext() {
        return this.mMANativeHelper;
    }

    void initClipSettings(MediaArtistNativeHelper.ClipSettings clipSettings) {
        clipSettings.clipPath = null;
        clipSettings.clipDecodedPath = null;
        clipSettings.clipOriginalPath = null;
        clipSettings.fileType = 0;
        clipSettings.endCutTime = 0;
        clipSettings.beginCutTime = 0;
        clipSettings.beginCutPercent = 0;
        clipSettings.endCutPercent = 0;
        clipSettings.panZoomEnabled = false;
        clipSettings.panZoomPercentStart = 0;
        clipSettings.panZoomTopLeftXStart = 0;
        clipSettings.panZoomTopLeftYStart = 0;
        clipSettings.panZoomPercentEnd = 0;
        clipSettings.panZoomTopLeftXEnd = 0;
        clipSettings.panZoomTopLeftYEnd = 0;
        clipSettings.mediaRendering = 0;
        clipSettings.rgbWidth = 0;
        clipSettings.rgbHeight = 0;
    }

    MediaArtistNativeHelper.ClipSettings getClipSettings() {
        MediaArtistNativeHelper.ClipSettings clipSettings = new MediaArtistNativeHelper.ClipSettings();
        initClipSettings(clipSettings);
        if (!(this instanceof MediaVideoItem)) {
            return this instanceof MediaImageItem ? ((MediaImageItem) this).getImageClipProperties() : clipSettings;
        }
        MediaVideoItem mediaVideoItem = (MediaVideoItem) this;
        clipSettings.clipPath = mediaVideoItem.getFilename();
        clipSettings.fileType = this.mMANativeHelper.getMediaItemFileType(mediaVideoItem.getFileType());
        clipSettings.beginCutTime = (int) mediaVideoItem.getBoundaryBeginTime();
        clipSettings.endCutTime = (int) mediaVideoItem.getBoundaryEndTime();
        clipSettings.mediaRendering = this.mMANativeHelper.getMediaItemRenderingMode(mediaVideoItem.getRenderingMode());
        return clipSettings;
    }

    void generateBlankFrame(MediaArtistNativeHelper.ClipSettings clipSettings) {
        if (!this.mBlankFrameGenerated) {
            this.mBlankFrameFilename = String.format(this.mProjectPath + "/ghost.rgb", new Object[0]);
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(this.mBlankFrameFilename);
            } catch (IOException unused) {
            }
            DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
            int[] iArr = new int[64];
            ByteBuffer byteBufferAllocate = ByteBuffer.allocate(256);
            byte[] bArrArray = byteBufferAllocate.array();
            for (int i = 0; i < 64; i++) {
                byteBufferAllocate.asIntBuffer().put(iArr, 0, 64);
                try {
                    dataOutputStream.write(bArrArray);
                } catch (IOException unused2) {
                }
            }
            try {
                fileOutputStream.close();
            } catch (IOException unused3) {
            }
            this.mBlankFrameGenerated = true;
        }
        clipSettings.clipPath = this.mBlankFrameFilename;
        clipSettings.fileType = 5;
        clipSettings.beginCutTime = 0;
        clipSettings.endCutTime = 0;
        clipSettings.mediaRendering = 0;
        clipSettings.rgbWidth = 64;
        clipSettings.rgbHeight = 64;
    }

    void invalidateBlankFrame() {
        if (this.mBlankFrameFilename == null || !new File(this.mBlankFrameFilename).exists()) {
            return;
        }
        new File(this.mBlankFrameFilename).delete();
        this.mBlankFrameFilename = null;
    }
}
