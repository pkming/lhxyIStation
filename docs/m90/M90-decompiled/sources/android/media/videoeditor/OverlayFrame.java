package android.media.videoeditor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Pair;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

/* JADX INFO: loaded from: classes.dex */
public class OverlayFrame extends Overlay {
    private static final Paint sResizePaint = new Paint(2);
    private Bitmap mBitmap;
    private String mBitmapFileName;
    private String mFilename;
    private int mOFHeight;
    private int mOFWidth;
    private int mResizedRGBHeight;
    private int mResizedRGBWidth;

    private OverlayFrame() {
        this((MediaItem) null, (String) null, (String) null, 0L, 0L);
    }

    public OverlayFrame(MediaItem mediaItem, String str, Bitmap bitmap, long j, long j2) {
        super(mediaItem, str, j, j2);
        this.mBitmap = bitmap;
        this.mFilename = null;
        this.mBitmapFileName = null;
        this.mResizedRGBWidth = 0;
        this.mResizedRGBHeight = 0;
    }

    OverlayFrame(MediaItem mediaItem, String str, String str2, long j, long j2) {
        super(mediaItem, str, j, j2);
        this.mBitmapFileName = str2;
        this.mBitmap = BitmapFactory.decodeFile(str2);
        this.mFilename = null;
        this.mResizedRGBWidth = 0;
        this.mResizedRGBHeight = 0;
    }

    public Bitmap getBitmap() {
        return this.mBitmap;
    }

    String getBitmapImageFileName() {
        return this.mBitmapFileName;
    }

    public void setBitmap(Bitmap bitmap) {
        getMediaItem().getNativeContext().setGeneratePreview(true);
        invalidate();
        this.mBitmap = bitmap;
        if (this.mFilename != null) {
            new File(this.mFilename).delete();
            this.mFilename = null;
        }
        getMediaItem().invalidateTransitions(this.mStartTimeMs, this.mDurationMs);
    }

    String getFilename() {
        return this.mFilename;
    }

    void setFilename(String str) {
        this.mFilename = str;
    }

    String save(String str) throws IOException {
        String str2 = this.mFilename;
        if (str2 != null) {
            return str2;
        }
        this.mBitmapFileName = str + "/Overlay" + getId() + ".png";
        if (!new File(this.mBitmapFileName).exists()) {
            FileOutputStream fileOutputStream = new FileOutputStream(this.mBitmapFileName);
            this.mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        }
        this.mOFWidth = this.mBitmap.getWidth();
        this.mOFHeight = this.mBitmap.getHeight();
        this.mFilename = str + "/Overlay" + getId() + ".rgb";
        Pair<Integer, Integer> pair = MediaProperties.getSupportedResolutions(super.getMediaItem().getNativeContext().nativeHelperGetAspectRatio())[r7.length - 1];
        generateOverlayWithRenderingMode(super.getMediaItem(), this, pair.second.intValue(), pair.first.intValue());
        return this.mFilename;
    }

    int getOverlayFrameHeight() {
        return this.mOFHeight;
    }

    int getOverlayFrameWidth() {
        return this.mOFWidth;
    }

    void setOverlayFrameHeight(int i) {
        this.mOFHeight = i;
    }

    void setOverlayFrameWidth(int i) {
        this.mOFWidth = i;
    }

    void setResizedRGBSize(int i, int i2) {
        this.mResizedRGBWidth = i;
        this.mResizedRGBHeight = i2;
    }

    int getResizedRGBSizeHeight() {
        return this.mResizedRGBHeight;
    }

    int getResizedRGBSizeWidth() {
        return this.mResizedRGBWidth;
    }

    void invalidate() {
        Bitmap bitmap = this.mBitmap;
        if (bitmap != null) {
            bitmap.recycle();
            this.mBitmap = null;
        }
        if (this.mFilename != null) {
            new File(this.mFilename).delete();
            this.mFilename = null;
        }
        if (this.mBitmapFileName != null) {
            new File(this.mBitmapFileName).delete();
            this.mBitmapFileName = null;
        }
    }

    void invalidateGeneratedFiles() {
        if (this.mFilename != null) {
            new File(this.mFilename).delete();
            this.mFilename = null;
        }
        if (this.mBitmapFileName != null) {
            new File(this.mBitmapFileName).delete();
            this.mBitmapFileName = null;
        }
    }

    void generateOverlayWithRenderingMode(MediaItem mediaItem, OverlayFrame overlayFrame, int i, int i2) throws IOException {
        int width;
        int width2;
        int height;
        int i3;
        Rect rect;
        Rect rect2;
        int width3;
        int width4;
        int height2;
        int i4;
        int renderingMode = mediaItem.getRenderingMode();
        Bitmap bitmap = overlayFrame.getBitmap();
        int resizedRGBSizeHeight = overlayFrame.getResizedRGBSizeHeight();
        int resizedRGBSizeWidth = overlayFrame.getResizedRGBSizeWidth();
        if (resizedRGBSizeWidth == 0) {
            resizedRGBSizeWidth = bitmap.getWidth();
        }
        if (resizedRGBSizeHeight == 0) {
            resizedRGBSizeHeight = bitmap.getHeight();
        }
        if (resizedRGBSizeWidth == i2 && resizedRGBSizeHeight == i && new File(overlayFrame.getFilename()).exists()) {
            return;
        }
        Bitmap bitmapCreateBitmap = Bitmap.createBitmap(i2, i, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapCreateBitmap);
        if (renderingMode == 0) {
            if (bitmap.getWidth() / bitmap.getHeight() > canvas.getWidth() / canvas.getHeight()) {
                int width5 = (canvas.getWidth() * bitmap.getHeight()) / bitmap.getWidth();
                int height3 = (canvas.getHeight() - width5) / 2;
                width2 = canvas.getWidth();
                height = width5 + height3;
                i3 = height3;
                width = 0;
            } else {
                int height4 = (canvas.getHeight() * bitmap.getWidth()) / bitmap.getHeight();
                width = (canvas.getWidth() - height4) / 2;
                width2 = width + height4;
                height = canvas.getHeight();
                i3 = 0;
            }
            Rect rect3 = new Rect(width, i3, width2, height);
            rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            rect2 = rect3;
        } else if (renderingMode == 1) {
            rect2 = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
            rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        } else if (renderingMode == 2) {
            if (bitmap.getWidth() / bitmap.getHeight() < canvas.getWidth() / canvas.getHeight()) {
                int width6 = (bitmap.getWidth() * canvas.getHeight()) / canvas.getWidth();
                int height5 = (bitmap.getHeight() - width6) / 2;
                width4 = bitmap.getWidth();
                height2 = width6 + height5;
                i4 = height5;
                width3 = 0;
            } else {
                int height6 = (bitmap.getHeight() * canvas.getWidth()) / canvas.getHeight();
                width3 = (bitmap.getWidth() - height6) / 2;
                width4 = width3 + height6;
                height2 = bitmap.getHeight();
                i4 = 0;
            }
            rect = new Rect(width3, i4, width4, height2);
            rect2 = new Rect(0, 0, canvas.getWidth(), canvas.getHeight());
        } else {
            throw new IllegalStateException("Rendering mode: " + renderingMode);
        }
        canvas.drawBitmap(bitmap, rect, rect2, sResizePaint);
        canvas.setBitmap(null);
        String filename = overlayFrame.getFilename();
        if (filename != null) {
            new File(filename).delete();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(filename);
        DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
        int[] iArr = new int[i2];
        ByteBuffer byteBufferAllocate = ByteBuffer.allocate(i2 * 4);
        byte[] bArrArray = byteBufferAllocate.array();
        int i5 = 0;
        while (i5 < i) {
            byte[] bArr = bArrArray;
            bitmapCreateBitmap.getPixels(iArr, 0, i2, 0, i5, i2, 1);
            byteBufferAllocate.asIntBuffer().put(iArr, 0, i2);
            dataOutputStream.write(bArr);
            i5++;
            bArrArray = bArr;
        }
        fileOutputStream.flush();
        fileOutputStream.close();
        overlayFrame.setResizedRGBSize(i2, i);
    }
}
