package android.media;

import android.media.Image;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Surface;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/* JADX INFO: loaded from: classes.dex */
public class ImageReader implements AutoCloseable {
    private static final int ACQUIRE_MAX_IMAGES = 2;
    private static final int ACQUIRE_NO_BUFS = 1;
    private static final int ACQUIRE_SUCCESS = 0;
    private final int mFormat;
    private final int mHeight;
    private OnImageAvailableListener mListener;
    private ListenerHandler mListenerHandler;
    private final Object mListenerLock = new Object();
    private final int mMaxImages;
    private long mNativeContext;
    private final int mNumPlanes;
    private final Surface mSurface;
    private final int mWidth;

    public interface OnImageAvailableListener {
        void onImageAvailable(ImageReader imageReader);
    }

    private static native void nativeClassInit();

    private native synchronized void nativeClose();

    private native synchronized Surface nativeGetSurface();

    private native synchronized int nativeImageSetup(Image image);

    private native synchronized void nativeInit(Object obj, int i, int i2, int i3, int i4);

    private native synchronized void nativeReleaseImage(Image image);

    public static ImageReader newInstance(int i, int i2, int i3, int i4) {
        return new ImageReader(i, i2, i3, i4);
    }

    protected ImageReader(int i, int i2, int i3, int i4) {
        this.mWidth = i;
        this.mHeight = i2;
        this.mFormat = i3;
        this.mMaxImages = i4;
        if (i < 1 || i2 < 1) {
            throw new IllegalArgumentException("The image dimensions must be positive");
        }
        if (i4 < 1) {
            throw new IllegalArgumentException("Maximum outstanding image count must be at least 1");
        }
        if (i3 == 17) {
            throw new IllegalArgumentException("NV21 format is not supported");
        }
        this.mNumPlanes = getNumPlanesFromFormat();
        nativeInit(new WeakReference(this), i, i2, i3, i4);
        this.mSurface = nativeGetSurface();
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    public int getImageFormat() {
        return this.mFormat;
    }

    public int getMaxImages() {
        return this.mMaxImages;
    }

    public Surface getSurface() {
        return this.mSurface;
    }

    public Image acquireLatestImage() {
        Image imageAcquireNextImage = acquireNextImage();
        if (imageAcquireNextImage == null) {
            return null;
        }
        while (true) {
            try {
                Image imageAcquireNextImageNoThrowISE = acquireNextImageNoThrowISE();
                if (imageAcquireNextImageNoThrowISE == null) {
                    return imageAcquireNextImage;
                }
                imageAcquireNextImage.close();
                imageAcquireNextImage = imageAcquireNextImageNoThrowISE;
            } catch (Throwable th) {
                if (imageAcquireNextImage != null) {
                    imageAcquireNextImage.close();
                }
                throw th;
            }
        }
    }

    public Image acquireNextImageNoThrowISE() {
        SurfaceImage surfaceImage = new SurfaceImage();
        if (acquireNextSurfaceImage(surfaceImage) == 0) {
            return surfaceImage;
        }
        return null;
    }

    private int acquireNextSurfaceImage(SurfaceImage surfaceImage) {
        int iNativeImageSetup = nativeImageSetup(surfaceImage);
        if (iNativeImageSetup == 0) {
            surfaceImage.createSurfacePlanes();
            surfaceImage.setImageValid(true);
        } else if (iNativeImageSetup != 1 && iNativeImageSetup != 2) {
            throw new AssertionError("Unknown nativeImageSetup return code " + iNativeImageSetup);
        }
        return iNativeImageSetup;
    }

    public Image acquireNextImage() {
        SurfaceImage surfaceImage = new SurfaceImage();
        int iAcquireNextSurfaceImage = acquireNextSurfaceImage(surfaceImage);
        if (iAcquireNextSurfaceImage == 0) {
            return surfaceImage;
        }
        if (iAcquireNextSurfaceImage == 1) {
            return null;
        }
        if (iAcquireNextSurfaceImage == 2) {
            throw new IllegalStateException(String.format("maxImages (%d) has already been acquired, call #close before acquiring more.", Integer.valueOf(this.mMaxImages)));
        }
        throw new AssertionError("Unknown nativeImageSetup return code " + iAcquireNextSurfaceImage);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void releaseImage(Image image) {
        if (!(image instanceof SurfaceImage)) {
            throw new IllegalArgumentException("This image was not produced by an ImageReader");
        }
        SurfaceImage surfaceImage = (SurfaceImage) image;
        if (surfaceImage.getReader() != this) {
            throw new IllegalArgumentException("This image was not produced by this ImageReader");
        }
        surfaceImage.clearSurfacePlanes();
        nativeReleaseImage(image);
        surfaceImage.setImageValid(false);
    }

    public void setOnImageAvailableListener(OnImageAvailableListener onImageAvailableListener, Handler handler) {
        synchronized (this.mListenerLock) {
            if (onImageAvailableListener != null) {
                Looper looper = handler != null ? handler.getLooper() : Looper.myLooper();
                if (looper == null) {
                    throw new IllegalArgumentException("handler is null but the current thread is not a looper");
                }
                ListenerHandler listenerHandler = this.mListenerHandler;
                if (listenerHandler == null || listenerHandler.getLooper() != looper) {
                    this.mListenerHandler = new ListenerHandler(looper);
                }
                this.mListener = onImageAvailableListener;
            } else {
                this.mListener = null;
                this.mListenerHandler = null;
            }
        }
    }

    @Override // java.lang.AutoCloseable
    public void close() {
        setOnImageAvailableListener(null, null);
        nativeClose();
    }

    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    private int getNumPlanesFromFormat() {
        int i = this.mFormat;
        if (i != 1 && i != 2 && i != 3 && i != 4) {
            if (i == 16) {
                return 2;
            }
            if (i != 17) {
                if (i != 20 && i != 32) {
                    if (i != 35) {
                        if (i != 256 && i != 538982489 && i != 540422489) {
                            if (i != 842094169) {
                                throw new UnsupportedOperationException(String.format("Invalid format specified %d", Integer.valueOf(this.mFormat)));
                            }
                        }
                    }
                }
            }
            return 3;
        }
        return 1;
    }

    private static void postEventFromNative(Object obj) {
        ListenerHandler listenerHandler;
        ImageReader imageReader = (ImageReader) ((WeakReference) obj).get();
        if (imageReader == null) {
            return;
        }
        synchronized (imageReader.mListenerLock) {
            listenerHandler = imageReader.mListenerHandler;
        }
        if (listenerHandler != null) {
            listenerHandler.sendEmptyMessage(0);
        }
    }

    private final class ListenerHandler extends Handler {
        public ListenerHandler(Looper looper) {
            super(looper, null, true);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            OnImageAvailableListener onImageAvailableListener;
            synchronized (ImageReader.this.mListenerLock) {
                onImageAvailableListener = ImageReader.this.mListener;
            }
            if (onImageAvailableListener != null) {
                onImageAvailableListener.onImageAvailable(ImageReader.this);
            }
        }
    }

    private class SurfaceImage extends Image {
        private boolean mIsImageValid = false;
        private long mLockedBuffer;
        private SurfacePlane[] mPlanes;
        private long mTimestamp;

        private native synchronized SurfacePlane nativeCreatePlane(int i);

        /* JADX INFO: Access modifiers changed from: private */
        public native synchronized ByteBuffer nativeImageGetBuffer(int i);

        public SurfaceImage() {
        }

        @Override // android.media.Image, java.lang.AutoCloseable
        public void close() {
            if (this.mIsImageValid) {
                ImageReader.this.releaseImage(this);
            }
        }

        public ImageReader getReader() {
            return ImageReader.this;
        }

        @Override // android.media.Image
        public int getFormat() {
            if (this.mIsImageValid) {
                return ImageReader.this.mFormat;
            }
            throw new IllegalStateException("Image is already released");
        }

        @Override // android.media.Image
        public int getWidth() {
            if (this.mIsImageValid) {
                return ImageReader.this.mWidth;
            }
            throw new IllegalStateException("Image is already released");
        }

        @Override // android.media.Image
        public int getHeight() {
            if (this.mIsImageValid) {
                return ImageReader.this.mHeight;
            }
            throw new IllegalStateException("Image is already released");
        }

        @Override // android.media.Image
        public long getTimestamp() {
            if (this.mIsImageValid) {
                return this.mTimestamp;
            }
            throw new IllegalStateException("Image is already released");
        }

        @Override // android.media.Image
        public Image.Plane[] getPlanes() {
            if (this.mIsImageValid) {
                return (Image.Plane[]) this.mPlanes.clone();
            }
            throw new IllegalStateException("Image is already released");
        }

        protected final void finalize() throws Throwable {
            try {
                close();
            } finally {
                super.finalize();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void setImageValid(boolean z) {
            this.mIsImageValid = z;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public boolean isImageValid() {
            return this.mIsImageValid;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void clearSurfacePlanes() {
            if (!this.mIsImageValid) {
                return;
            }
            int i = 0;
            while (true) {
                SurfacePlane[] surfacePlaneArr = this.mPlanes;
                if (i >= surfacePlaneArr.length) {
                    return;
                }
                if (surfacePlaneArr[i] != null) {
                    surfacePlaneArr[i].clearBuffer();
                    this.mPlanes[i] = null;
                }
                i++;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void createSurfacePlanes() {
            this.mPlanes = new SurfacePlane[ImageReader.this.mNumPlanes];
            for (int i = 0; i < ImageReader.this.mNumPlanes; i++) {
                this.mPlanes[i] = nativeCreatePlane(i);
            }
        }

        private class SurfacePlane extends Image.Plane {
            private ByteBuffer mBuffer;
            private final int mIndex;
            private final int mPixelStride;
            private final int mRowStride;

            private SurfacePlane(int i, int i2, int i3) {
                this.mIndex = i;
                this.mRowStride = i2;
                this.mPixelStride = i3;
            }

            @Override // android.media.Image.Plane
            public ByteBuffer getBuffer() {
                if (!SurfaceImage.this.isImageValid()) {
                    throw new IllegalStateException("Image is already released");
                }
                ByteBuffer byteBuffer = this.mBuffer;
                if (byteBuffer != null) {
                    return byteBuffer;
                }
                ByteBuffer byteBufferNativeImageGetBuffer = SurfaceImage.this.nativeImageGetBuffer(this.mIndex);
                this.mBuffer = byteBufferNativeImageGetBuffer;
                return byteBufferNativeImageGetBuffer.order(ByteOrder.nativeOrder());
            }

            @Override // android.media.Image.Plane
            public int getPixelStride() {
                if (SurfaceImage.this.isImageValid()) {
                    return this.mPixelStride;
                }
                throw new IllegalStateException("Image is already released");
            }

            @Override // android.media.Image.Plane
            public int getRowStride() {
                if (SurfaceImage.this.isImageValid()) {
                    return this.mRowStride;
                }
                throw new IllegalStateException("Image is already released");
            }

            /* JADX INFO: Access modifiers changed from: private */
            public void clearBuffer() {
                this.mBuffer = null;
            }
        }
    }

    static {
        System.loadLibrary("media_jni");
        nativeClassInit();
    }
}
