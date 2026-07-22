package android.media.videoeditor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.videoeditor.MediaArtistNativeHelper;
import android.media.videoeditor.MediaItem;
import android.util.Log;
import android.util.Pair;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public class MediaImageItem extends MediaItem {
    private static final String TAG = "MediaImageItem";
    private static final Paint sResizePaint = new Paint(2);
    private final int mAspectRatio;
    private String mDecodedFilename;
    private long mDurationMs;
    private String mFileName;
    private int mGeneratedClipHeight;
    private int mGeneratedClipWidth;
    private final int mHeight;
    private final MediaArtistNativeHelper mMANativeHelper;
    private String mScaledFilename;
    private int mScaledHeight;
    private int mScaledWidth;
    private final VideoEditorImpl mVideoEditor;
    private final int mWidth;

    public static int nextPowerOf2(int i) {
        int i2 = i - 1;
        int i3 = i2 | (i2 >>> 16);
        int i4 = i3 | (i3 >>> 8);
        int i5 = i4 | (i4 >>> 4);
        int i6 = i5 | (i5 >>> 2);
        return (i6 | (i6 >>> 1)) + 1;
    }

    private MediaImageItem() throws IOException {
        this(null, null, null, 0L, 0);
    }

    public MediaImageItem(VideoEditor videoEditor, String str, String str2, long j, int i) throws Throwable {
        Bitmap bitmapScaleImage;
        super(videoEditor, str, str2, i);
        VideoEditorImpl videoEditorImpl = (VideoEditorImpl) videoEditor;
        MediaArtistNativeHelper nativeContext = videoEditorImpl.getNativeContext();
        this.mMANativeHelper = nativeContext;
        this.mVideoEditor = videoEditorImpl;
        try {
            int fileType = nativeContext.getFileType(nativeContext.getMediaProperties(str2).fileType);
            if (fileType != 5 && fileType != 8) {
                throw new IllegalArgumentException("Unsupported Input File Type");
            }
            this.mFileName = str2;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(str2, options);
            int i2 = options.outWidth;
            this.mWidth = i2;
            int i3 = options.outHeight;
            this.mHeight = i3;
            this.mDurationMs = j;
            this.mDecodedFilename = String.format(nativeContext.getProjectPath() + "/decoded" + getId() + ".rgb", new Object[0]);
            try {
                int aspectRatio = nativeContext.getAspectRatio(i2, i3);
                this.mAspectRatio = aspectRatio;
                this.mGeneratedClipHeight = 0;
                this.mGeneratedClipWidth = 0;
                Pair<Integer, Integer>[] supportedResolutions = MediaProperties.getSupportedResolutions(aspectRatio);
                Pair<Integer, Integer> pair = supportedResolutions[supportedResolutions.length - 1];
                if (i2 > pair.first.intValue() || i3 > pair.second.intValue()) {
                    bitmapScaleImage = scaleImage(str2, pair.first.intValue(), pair.second.intValue());
                    this.mScaledFilename = String.format(nativeContext.getProjectPath() + "/scaled" + getId() + ".JPG", new Object[0]);
                    if (!new File(this.mScaledFilename).exists()) {
                        this.mRegenerateClip = true;
                        FileOutputStream fileOutputStream = new FileOutputStream(this.mScaledFilename);
                        bitmapScaleImage.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);
                        fileOutputStream.close();
                    }
                    this.mScaledWidth = (bitmapScaleImage.getWidth() >> 1) << 1;
                    this.mScaledHeight = (bitmapScaleImage.getHeight() >> 1) << 1;
                } else {
                    this.mScaledFilename = str2;
                    this.mScaledWidth = (i2 >> 1) << 1;
                    this.mScaledHeight = (i3 >> 1) << 1;
                    bitmapScaleImage = BitmapFactory.decodeFile(str2);
                }
                int i4 = this.mScaledWidth;
                int i5 = this.mScaledHeight;
                if (!new File(this.mDecodedFilename).exists()) {
                    FileOutputStream fileOutputStream2 = new FileOutputStream(this.mDecodedFilename);
                    DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream2);
                    int[] iArr = new int[i4];
                    ByteBuffer byteBufferAllocate = ByteBuffer.allocate(i4 * 4);
                    byte[] bArrArray = byteBufferAllocate.array();
                    int i6 = 0;
                    while (i6 < i5) {
                        byte[] bArr = bArrArray;
                        bitmapScaleImage.getPixels(iArr, 0, this.mScaledWidth, 0, i6, i4, 1);
                        byteBufferAllocate.asIntBuffer().put(iArr, 0, i4);
                        dataOutputStream.write(bArr);
                        i6++;
                        bArrArray = bArr;
                    }
                    fileOutputStream2.close();
                }
                bitmapScaleImage.recycle();
            } catch (IllegalArgumentException unused) {
                throw new IllegalArgumentException("Null width and height");
            }
        } catch (Exception unused2) {
            throw new IllegalArgumentException("Unsupported file or file not found: " + str2);
        }
    }

    @Override // android.media.videoeditor.MediaItem
    public int getFileType() {
        if (this.mFilename.endsWith(".jpg") || this.mFilename.endsWith(".jpeg") || this.mFilename.endsWith(".JPG") || this.mFilename.endsWith(".JPEG")) {
            return 5;
        }
        return (this.mFilename.endsWith(".png") || this.mFilename.endsWith(".PNG")) ? 8 : 255;
    }

    String getScaledImageFileName() {
        return this.mScaledFilename;
    }

    int getGeneratedClipHeight() {
        return this.mGeneratedClipHeight;
    }

    int getGeneratedClipWidth() {
        return this.mGeneratedClipWidth;
    }

    String getDecodedImageFileName() {
        return this.mDecodedFilename;
    }

    @Override // android.media.videoeditor.MediaItem
    public int getWidth() {
        return this.mWidth;
    }

    @Override // android.media.videoeditor.MediaItem
    public int getHeight() {
        return this.mHeight;
    }

    public int getScaledWidth() {
        return this.mScaledWidth;
    }

    public int getScaledHeight() {
        return this.mScaledHeight;
    }

    @Override // android.media.videoeditor.MediaItem
    public int getAspectRatio() {
        return this.mAspectRatio;
    }

    public void setDuration(long j) {
        if (j == this.mDurationMs) {
            return;
        }
        this.mMANativeHelper.setGeneratePreview(true);
        invalidateEndTransition();
        this.mDurationMs = j;
        adjustTransitions();
        invalidateBeginTransition(adjustEffects(), adjustOverlays());
        invalidateEndTransition();
        if (getGeneratedImageClip() != null) {
            new File(getGeneratedImageClip()).delete();
            setGeneratedImageClip(null);
            super.setRegenerateClip(true);
        }
        this.mVideoEditor.updateTimelineDuration();
    }

    private void invalidateBeginTransition(List<Effect> list, List<Overlay> list2) {
        if (this.mBeginTransition == null || !this.mBeginTransition.isGenerated()) {
            return;
        }
        long duration = this.mBeginTransition.getDuration();
        Iterator<Effect> it = list.iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            } else if (it.next().getStartTime() < duration) {
                this.mBeginTransition.invalidate();
                break;
            }
        }
        if (this.mBeginTransition.isGenerated()) {
            Iterator<Overlay> it2 = list2.iterator();
            while (it2.hasNext()) {
                if (it2.next().getStartTime() < duration) {
                    this.mBeginTransition.invalidate();
                    return;
                }
            }
        }
    }

    private void invalidateEndTransition() {
        if (this.mEndTransition == null || !this.mEndTransition.isGenerated()) {
            return;
        }
        long duration = this.mEndTransition.getDuration();
        Iterator<Effect> it = getAllEffects().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            Effect next = it.next();
            if (next.getStartTime() + next.getDuration() > this.mDurationMs - duration) {
                this.mEndTransition.invalidate();
                break;
            }
        }
        if (this.mEndTransition.isGenerated()) {
            for (Overlay overlay : getAllOverlays()) {
                if (overlay.getStartTime() + overlay.getDuration() > this.mDurationMs - duration) {
                    this.mEndTransition.invalidate();
                    return;
                }
            }
        }
    }

    private List<Effect> adjustEffects() {
        long duration;
        ArrayList arrayList = new ArrayList();
        for (Effect effect : getAllEffects()) {
            long startTime = effect.getStartTime() > getDuration() ? 0L : effect.getStartTime();
            if (effect.getDuration() + startTime > getDuration()) {
                duration = getDuration() - startTime;
            } else {
                duration = effect.getDuration();
            }
            if (startTime != effect.getStartTime() || duration != effect.getDuration()) {
                effect.setStartTimeAndDuration(startTime, duration);
                arrayList.add(effect);
            }
        }
        return arrayList;
    }

    private List<Overlay> adjustOverlays() {
        long duration;
        ArrayList arrayList = new ArrayList();
        for (Overlay overlay : getAllOverlays()) {
            long startTime = overlay.getStartTime() > getDuration() ? 0L : overlay.getStartTime();
            if (overlay.getDuration() + startTime > getDuration()) {
                duration = getDuration() - startTime;
            } else {
                duration = overlay.getDuration();
            }
            if (startTime != overlay.getStartTime() || duration != overlay.getDuration()) {
                overlay.setStartTimeAndDuration(startTime, duration);
                arrayList.add(overlay);
            }
        }
        return arrayList;
    }

    private int getWidthByAspectRatioAndHeight(int i, int i2) {
        if (i != 1) {
            if (i != 2) {
                if (i == 3) {
                    int i3 = i2 == 480 ? 640 : 0;
                    if (i2 == 720) {
                        return 960;
                    }
                    return i3;
                }
                if (i != 4) {
                    if (i != 5) {
                        throw new IllegalArgumentException("Illegal arguments for aspectRatio");
                    }
                    if (i2 == 144) {
                        return 176;
                    }
                } else if (i2 == 480) {
                    return 800;
                }
            } else {
                if (i2 == 360) {
                    return 640;
                }
                if (i2 == 480) {
                    return 854;
                }
                if (i2 == 720) {
                    return 1280;
                }
                if (i2 == 1080) {
                    return 1920;
                }
            }
        } else {
            if (i2 == 480) {
                return MediaProperties.HEIGHT_720;
            }
            if (i2 == 720) {
                return 1080;
            }
        }
        return 0;
    }

    @Override // android.media.videoeditor.MediaItem
    void setGeneratedImageClip(String str) {
        super.setGeneratedImageClip(str);
        this.mGeneratedClipHeight = getScaledHeight();
        this.mGeneratedClipWidth = getWidthByAspectRatioAndHeight(this.mVideoEditor.getAspectRatio(), this.mGeneratedClipHeight);
    }

    @Override // android.media.videoeditor.MediaItem
    String getGeneratedImageClip() {
        return super.getGeneratedImageClip();
    }

    @Override // android.media.videoeditor.MediaItem
    public long getDuration() {
        return this.mDurationMs;
    }

    @Override // android.media.videoeditor.MediaItem
    public long getTimelineDuration() {
        return this.mDurationMs;
    }

    @Override // android.media.videoeditor.MediaItem
    public Bitmap getThumbnail(int i, int i2, long j) throws IOException {
        if (getGeneratedImageClip() != null) {
            return this.mMANativeHelper.getPixels(getGeneratedImageClip(), i, i2, j, 0);
        }
        return scaleImage(this.mFilename, i, i2);
    }

    @Override // android.media.videoeditor.MediaItem
    public void getThumbnailList(int i, int i2, long j, long j2, int i3, int[] iArr, MediaItem.GetThumbnailListCallback getThumbnailListCallback) throws Throwable {
        if (getGeneratedImageClip() == null) {
            Bitmap bitmapScaleImage = scaleImage(this.mFilename, i, i2);
            for (int i4 : iArr) {
                getThumbnailListCallback.onThumbnail(bitmapScaleImage, i4);
            }
            return;
        }
        if (j > j2) {
            throw new IllegalArgumentException("Start time is greater than end time");
        }
        if (j2 > this.mDurationMs) {
            throw new IllegalArgumentException("End time is greater than file duration");
        }
        this.mMANativeHelper.getPixelsList(getGeneratedImageClip(), i, i2, j, j2, i3, iArr, getThumbnailListCallback, 0);
    }

    @Override // android.media.videoeditor.MediaItem
    void invalidateTransitions(long j, long j2) {
        if (this.mBeginTransition != null && isOverlapping(j, j2, 0L, this.mBeginTransition.getDuration())) {
            this.mBeginTransition.invalidate();
        }
        if (this.mEndTransition != null) {
            long duration = this.mEndTransition.getDuration();
            if (isOverlapping(j, j2, getDuration() - duration, duration)) {
                this.mEndTransition.invalidate();
            }
        }
    }

    @Override // android.media.videoeditor.MediaItem
    void invalidateTransitions(long j, long j2, long j3, long j4) {
        if (this.mBeginTransition != null) {
            long duration = this.mBeginTransition.getDuration();
            boolean zIsOverlapping = isOverlapping(j, j2, 0L, duration);
            boolean zIsOverlapping2 = isOverlapping(j3, j4, 0L, duration);
            if (zIsOverlapping2 != zIsOverlapping) {
                this.mBeginTransition.invalidate();
            } else if (zIsOverlapping2 && (j != j3 || j + j2 <= duration || j3 + j4 <= duration)) {
                this.mBeginTransition.invalidate();
            }
        }
        if (this.mEndTransition != null) {
            long duration2 = this.mEndTransition.getDuration();
            boolean zIsOverlapping3 = isOverlapping(j, j2, this.mDurationMs - duration2, duration2);
            boolean zIsOverlapping4 = isOverlapping(j3, j4, this.mDurationMs - duration2, duration2);
            if (zIsOverlapping4 != zIsOverlapping3) {
                this.mEndTransition.invalidate();
                return;
            }
            if (zIsOverlapping4) {
                if (j + j2 == j3 + j4) {
                    long j5 = this.mDurationMs;
                    if (j <= j5 - duration2 && j3 <= j5 - duration2) {
                        return;
                    }
                }
                this.mEndTransition.invalidate();
            }
        }
    }

    void invalidate() {
        if (getGeneratedImageClip() != null) {
            new File(getGeneratedImageClip()).delete();
            setGeneratedImageClip(null);
            setRegenerateClip(true);
        }
        String str = this.mScaledFilename;
        if (str != null) {
            if (this.mFileName != str) {
                new File(this.mScaledFilename).delete();
            }
            this.mScaledFilename = null;
        }
        if (this.mDecodedFilename != null) {
            new File(this.mDecodedFilename).delete();
            this.mDecodedFilename = null;
        }
    }

    private MediaArtistNativeHelper.ClipSettings getKenBurns(EffectKenBurns effectKenBurns) {
        Rect rect = new Rect();
        Rect rect2 = new Rect();
        MediaArtistNativeHelper.ClipSettings clipSettings = new MediaArtistNativeHelper.ClipSettings();
        effectKenBurns.getKenBurnsSettings(rect, rect2);
        int width = getWidth();
        int height = getHeight();
        if (rect.left < 0 || rect.left > width || rect.right < 0 || rect.right > width || rect.top < 0 || rect.top > height || rect.bottom < 0 || rect.bottom > height || rect2.left < 0 || rect2.left > width || rect2.right < 0 || rect2.right > width || rect2.top < 0 || rect2.top > height || rect2.bottom < 0 || rect2.bottom > height) {
            throw new IllegalArgumentException("Illegal arguments for KebBurns");
        }
        if ((width - (rect.right - rect.left) == 0 || height - (rect.bottom - rect.top) == 0) && (width - (rect2.right - rect2.left) == 0 || height - (rect2.bottom - rect2.top) == 0)) {
            setRegenerateClip(false);
            clipSettings.clipPath = getDecodedImageFileName();
            clipSettings.fileType = 5;
            clipSettings.beginCutTime = 0;
            clipSettings.endCutTime = (int) getTimelineDuration();
            clipSettings.beginCutPercent = 0;
            clipSettings.endCutPercent = 0;
            clipSettings.panZoomEnabled = false;
            clipSettings.panZoomPercentStart = 0;
            clipSettings.panZoomTopLeftXStart = 0;
            clipSettings.panZoomTopLeftYStart = 0;
            clipSettings.panZoomPercentEnd = 0;
            clipSettings.panZoomTopLeftXEnd = 0;
            clipSettings.panZoomTopLeftYEnd = 0;
            clipSettings.mediaRendering = this.mMANativeHelper.getMediaItemRenderingMode(getRenderingMode());
            clipSettings.rgbWidth = getScaledWidth();
            clipSettings.rgbHeight = getScaledHeight();
            return clipSettings;
        }
        int iWidth = (rect.width() * 1000) / width;
        int iWidth2 = (rect2.width() * 1000) / width;
        clipSettings.clipPath = getDecodedImageFileName();
        clipSettings.fileType = this.mMANativeHelper.getMediaItemFileType(getFileType());
        clipSettings.beginCutTime = 0;
        clipSettings.endCutTime = (int) getTimelineDuration();
        clipSettings.beginCutPercent = 0;
        clipSettings.endCutPercent = 0;
        clipSettings.panZoomEnabled = true;
        clipSettings.panZoomPercentStart = iWidth;
        clipSettings.panZoomTopLeftXStart = (rect.left * 1000) / width;
        clipSettings.panZoomTopLeftYStart = (rect.top * 1000) / height;
        clipSettings.panZoomPercentEnd = iWidth2;
        clipSettings.panZoomTopLeftXEnd = (rect2.left * 1000) / width;
        clipSettings.panZoomTopLeftYEnd = (rect2.top * 1000) / height;
        clipSettings.mediaRendering = this.mMANativeHelper.getMediaItemRenderingMode(getRenderingMode());
        clipSettings.rgbWidth = getScaledWidth();
        clipSettings.rgbHeight = getScaledHeight();
        return clipSettings;
    }

    MediaArtistNativeHelper.ClipSettings generateKenburnsClip(EffectKenBurns effectKenBurns) {
        MediaArtistNativeHelper.EditSettings editSettings = new MediaArtistNativeHelper.EditSettings();
        editSettings.clipSettingsArray = new MediaArtistNativeHelper.ClipSettings[1];
        MediaArtistNativeHelper.ClipSettings clipSettings = new MediaArtistNativeHelper.ClipSettings();
        initClipSettings(clipSettings);
        editSettings.clipSettingsArray[0] = getKenBurns(effectKenBurns);
        if (getGeneratedImageClip() == null && getRegenerateClip()) {
            String strGenerateKenBurnsClip = this.mMANativeHelper.generateKenBurnsClip(editSettings, this);
            setGeneratedImageClip(strGenerateKenBurnsClip);
            setRegenerateClip(false);
            clipSettings.clipPath = strGenerateKenBurnsClip;
            clipSettings.fileType = 0;
            this.mGeneratedClipHeight = getScaledHeight();
            this.mGeneratedClipWidth = getWidthByAspectRatioAndHeight(this.mVideoEditor.getAspectRatio(), this.mGeneratedClipHeight);
        } else if (getGeneratedImageClip() == null) {
            clipSettings.clipPath = getDecodedImageFileName();
            clipSettings.fileType = 5;
            clipSettings.rgbWidth = getScaledWidth();
            clipSettings.rgbHeight = getScaledHeight();
        } else {
            clipSettings.clipPath = getGeneratedImageClip();
            clipSettings.fileType = 0;
        }
        clipSettings.mediaRendering = this.mMANativeHelper.getMediaItemRenderingMode(getRenderingMode());
        clipSettings.beginCutTime = 0;
        clipSettings.endCutTime = (int) getTimelineDuration();
        return clipSettings;
    }

    MediaArtistNativeHelper.ClipSettings getImageClipProperties() {
        EffectKenBurns effectKenBurns;
        boolean z;
        MediaArtistNativeHelper.ClipSettings clipSettings = new MediaArtistNativeHelper.ClipSettings();
        Iterator<Effect> it = getAllEffects().iterator();
        while (true) {
            if (!it.hasNext()) {
                effectKenBurns = null;
                z = false;
                break;
            }
            Effect next = it.next();
            if (next instanceof EffectKenBurns) {
                effectKenBurns = (EffectKenBurns) next;
                z = true;
                break;
            }
        }
        if (z) {
            return generateKenburnsClip(effectKenBurns);
        }
        initClipSettings(clipSettings);
        clipSettings.clipPath = getDecodedImageFileName();
        clipSettings.fileType = 5;
        clipSettings.beginCutTime = 0;
        clipSettings.endCutTime = (int) getTimelineDuration();
        clipSettings.mediaRendering = this.mMANativeHelper.getMediaItemRenderingMode(getRenderingMode());
        clipSettings.rgbWidth = getScaledWidth();
        clipSettings.rgbHeight = getScaledHeight();
        return clipSettings;
    }

    private Bitmap scaleImage(String str, int i, int i2) throws Throwable {
        float f;
        float f2;
        double dCeil;
        Bitmap bitmapDecodeFile;
        double dFloor;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(str, options);
        int i3 = options.outWidth;
        int i4 = options.outHeight;
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, "generateThumbnail: Input: " + i3 + "x" + i4 + ", resize to: " + i + "x" + i2);
        }
        if (i3 > i || i4 > i2) {
            float f3 = i3;
            f = i;
            float f4 = f3 / f;
            float f5 = i4;
            f2 = i2;
            float f6 = f5 / f2;
            if (f4 > f6) {
                float f7 = f5 / f4;
                if (f7 < f2) {
                    dFloor = Math.ceil(f7);
                } else {
                    dFloor = Math.floor(f7);
                }
                f2 = (float) dFloor;
            } else {
                float f8 = f3 / f6;
                if (f8 > f) {
                    dCeil = Math.floor(f8);
                } else {
                    dCeil = Math.ceil(f8);
                }
                f = (float) dCeil;
            }
            int iNextPowerOf2 = nextPowerOf2((int) Math.ceil(Math.max(f3 / f, f5 / f2)));
            BitmapFactory.Options options2 = new BitmapFactory.Options();
            options2.inSampleSize = iNextPowerOf2;
            bitmapDecodeFile = BitmapFactory.decodeFile(str, options2);
        } else {
            f = i;
            f2 = i2;
            bitmapDecodeFile = BitmapFactory.decodeFile(str);
        }
        if (bitmapDecodeFile == null) {
            Log.e(TAG, "generateThumbnail: Cannot decode image bytes");
            throw new IOException("Cannot decode file: " + this.mFilename);
        }
        int i5 = (int) f;
        int i6 = (int) f2;
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(i5, i6, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        canvas.drawBitmap(bitmapDecodeFile, new Rect(0, 0, bitmapDecodeFile.getWidth(), bitmapDecodeFile.getHeight()), new Rect(0, 0, i5, i6), sResizePaint);
        canvas.setBitmap(null);
        bitmapDecodeFile.recycle();
        return bitmapCreateBitmap;
    }
}
