package android.media.videoeditor;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import java.io.IOException;
import java.util.List;

/* JADX INFO: loaded from: classes.dex */
public interface VideoEditor {
    public static final int DURATION_OF_STORYBOARD = -1;
    public static final long MAX_SUPPORTED_FILE_SIZE = 2147483648L;
    public static final String THUMBNAIL_FILENAME = "thumbnail.jpg";

    public interface ExportProgressListener {
        void onProgress(VideoEditor videoEditor, String str, int i);
    }

    public interface MediaProcessingProgressListener {
        public static final int ACTION_DECODE = 2;
        public static final int ACTION_ENCODE = 1;

        void onProgress(Object obj, int i, int i2);
    }

    public interface PreviewProgressListener {
        void onError(VideoEditor videoEditor, int i);

        void onProgress(VideoEditor videoEditor, long j, OverlayData overlayData);

        void onStart(VideoEditor videoEditor);

        void onStop(VideoEditor videoEditor);
    }

    void addAudioTrack(AudioTrack audioTrack);

    void addMediaItem(MediaItem mediaItem);

    void addTransition(Transition transition);

    void cancelExport(String str);

    void clearSurface(SurfaceHolder surfaceHolder);

    void export(String str, int i, int i2, int i3, int i4, ExportProgressListener exportProgressListener) throws IOException;

    void export(String str, int i, int i2, ExportProgressListener exportProgressListener) throws IOException;

    void generatePreview(MediaProcessingProgressListener mediaProcessingProgressListener);

    List<AudioTrack> getAllAudioTracks();

    List<MediaItem> getAllMediaItems();

    List<Transition> getAllTransitions();

    int getAspectRatio();

    AudioTrack getAudioTrack(String str);

    long getDuration();

    MediaItem getMediaItem(String str);

    String getPath();

    Transition getTransition(String str);

    void insertAudioTrack(AudioTrack audioTrack, String str);

    void insertMediaItem(MediaItem mediaItem, String str);

    void moveAudioTrack(String str, String str2);

    void moveMediaItem(String str, String str2);

    void release();

    void removeAllMediaItems();

    AudioTrack removeAudioTrack(String str);

    MediaItem removeMediaItem(String str);

    Transition removeTransition(String str);

    long renderPreviewFrame(SurfaceHolder surfaceHolder, long j, OverlayData overlayData);

    void save() throws IOException;

    void setAspectRatio(int i);

    void startPreview(SurfaceHolder surfaceHolder, long j, long j2, boolean z, int i, PreviewProgressListener previewProgressListener);

    long stopPreview();

    public static final class OverlayData {
        private static final Paint sResizePaint = new Paint(2);
        private Bitmap mOverlayBitmap = null;
        private int mRenderingMode = 2;
        private boolean mClear = false;

        public void release() {
            Bitmap bitmap = this.mOverlayBitmap;
            if (bitmap != null) {
                bitmap.recycle();
                this.mOverlayBitmap = null;
            }
        }

        public boolean needsRendering() {
            return this.mClear || this.mOverlayBitmap != null;
        }

        void set(Bitmap bitmap, int i) {
            this.mOverlayBitmap = bitmap;
            this.mRenderingMode = i;
            this.mClear = false;
        }

        void setClear() {
            this.mClear = true;
        }

        public void renderOverlay(Bitmap bitmap) {
            Rect rect;
            Rect rect2;
            int width;
            int height;
            int width2;
            int i;
            int width3;
            int height2;
            int width4;
            int i2;
            if (this.mClear) {
                bitmap.eraseColor(0);
                return;
            }
            if (this.mOverlayBitmap != null) {
                Canvas canvas = new Canvas(bitmap);
                int i3 = this.mRenderingMode;
                if (i3 == 0) {
                    rect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
                    rect2 = new Rect(0, 0, this.mOverlayBitmap.getWidth(), this.mOverlayBitmap.getHeight());
                } else if (i3 == 1) {
                    if (this.mOverlayBitmap.getWidth() / this.mOverlayBitmap.getHeight() < canvas.getWidth() / canvas.getHeight()) {
                        int width5 = (this.mOverlayBitmap.getWidth() * canvas.getHeight()) / canvas.getWidth();
                        int height3 = (this.mOverlayBitmap.getHeight() - width5) / 2;
                        height = width5 + height3;
                        width2 = this.mOverlayBitmap.getWidth();
                        i = height3;
                        width = 0;
                    } else {
                        int height4 = (this.mOverlayBitmap.getHeight() * canvas.getWidth()) / canvas.getHeight();
                        width = (this.mOverlayBitmap.getWidth() - height4) / 2;
                        int i4 = width + height4;
                        height = this.mOverlayBitmap.getHeight();
                        width2 = i4;
                        i = 0;
                    }
                    Rect rect3 = new Rect(width, i, width2, height);
                    rect = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
                    rect2 = rect3;
                } else if (i3 == 2) {
                    if (this.mOverlayBitmap.getWidth() / this.mOverlayBitmap.getHeight() > canvas.getWidth() / canvas.getHeight()) {
                        int width6 = (canvas.getWidth() * this.mOverlayBitmap.getHeight()) / this.mOverlayBitmap.getWidth();
                        int height5 = (canvas.getHeight() - width6) / 2;
                        height2 = width6 + height5;
                        width4 = canvas.getWidth();
                        i2 = height5;
                        width3 = 0;
                    } else {
                        int height6 = (canvas.getHeight() * this.mOverlayBitmap.getWidth()) / this.mOverlayBitmap.getHeight();
                        width3 = (canvas.getWidth() - height6) / 2;
                        int i5 = width3 + height6;
                        height2 = canvas.getHeight();
                        width4 = i5;
                        i2 = 0;
                    }
                    rect = new Rect(width3, i2, width4, height2);
                    rect2 = new Rect(0, 0, this.mOverlayBitmap.getWidth(), this.mOverlayBitmap.getHeight());
                } else {
                    throw new IllegalStateException("Rendering mode: " + this.mRenderingMode);
                }
                bitmap.eraseColor(0);
                canvas.drawBitmap(this.mOverlayBitmap, rect2, rect, sResizePaint);
                this.mOverlayBitmap.recycle();
            }
        }
    }
}
