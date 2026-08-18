package android.media;

import android.app.backup.FullBackup;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public class MediaMetadataRetriever {
    private static final int EMBEDDED_PICTURE_TYPE_ANY = 65535;
    public static final int METADATA_KEY_ALBUM = 1;
    public static final int METADATA_KEY_ALBUMARTIST = 13;
    public static final int METADATA_KEY_ARTIST = 2;
    public static final int METADATA_KEY_AUTHOR = 3;
    public static final int METADATA_KEY_BITRATE = 20;
    public static final int METADATA_KEY_CD_TRACK_NUMBER = 0;
    public static final int METADATA_KEY_COMPILATION = 15;
    public static final int METADATA_KEY_COMPOSER = 4;
    public static final int METADATA_KEY_DATE = 5;
    public static final int METADATA_KEY_DISC_NUMBER = 14;
    public static final int METADATA_KEY_DURATION = 9;
    public static final int METADATA_KEY_GENRE = 6;
    public static final int METADATA_KEY_HAS_AUDIO = 16;
    public static final int METADATA_KEY_HAS_VIDEO = 17;
    public static final int METADATA_KEY_IS_DRM = 22;
    public static final int METADATA_KEY_LOCATION = 23;
    public static final int METADATA_KEY_MIMETYPE = 12;
    public static final int METADATA_KEY_NUM_TRACKS = 10;
    public static final int METADATA_KEY_TIMED_TEXT_LANGUAGES = 21;
    public static final int METADATA_KEY_TITLE = 7;
    public static final int METADATA_KEY_VIDEO_HEIGHT = 19;
    public static final int METADATA_KEY_VIDEO_ROTATION = 24;
    public static final int METADATA_KEY_VIDEO_WIDTH = 18;
    public static final int METADATA_KEY_WRITER = 11;
    public static final int METADATA_KEY_YEAR = 8;
    public static final int OPTION_CLOSEST = 3;
    public static final int OPTION_CLOSEST_SYNC = 2;
    public static final int OPTION_NEXT_SYNC = 1;
    public static final int OPTION_PREVIOUS_SYNC = 0;
    private int mNativeContext;

    private native Bitmap _getFrameAtTime(long j, int i);

    private native void _setDataSource(String str, String[] strArr, String[] strArr2) throws IllegalArgumentException;

    private native byte[] getEmbeddedPicture(int i);

    private final native void native_finalize();

    private static native void native_init();

    private native void native_setup();

    public native String extractMetadata(int i);

    public native void release();

    public native void setDataSource(FileDescriptor fileDescriptor, long j, long j2) throws IllegalArgumentException;

    static {
        System.loadLibrary("media_jni");
        native_init();
    }

    public MediaMetadataRetriever() {
        native_setup();
    }

    public void setDataSource(String str) throws IllegalArgumentException {
        if (str == null) {
            throw new IllegalArgumentException();
        }
        try {
            FileInputStream fileInputStream = new FileInputStream(str);
            setDataSource(fileInputStream.getFD(), 0L, 576460752303423487L);
            try {
                fileInputStream.close();
            } catch (Exception unused) {
            }
        } catch (FileNotFoundException unused2) {
            throw new IllegalArgumentException();
        } catch (IOException unused3) {
            throw new IllegalArgumentException();
        }
    }

    public void setDataSource(String str, Map<String, String> map) throws IllegalArgumentException {
        String[] strArr = new String[map.size()];
        String[] strArr2 = new String[map.size()];
        int i = 0;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            strArr[i] = entry.getKey();
            strArr2[i] = entry.getValue();
            i++;
        }
        _setDataSource(str, strArr, strArr2);
    }

    public void setDataSource(FileDescriptor fileDescriptor) throws IllegalArgumentException {
        setDataSource(fileDescriptor, 0L, 576460752303423487L);
    }

    public void setDataSource(Context context, Uri uri) throws SecurityException, IllegalArgumentException {
        if (uri == null) {
            throw new IllegalArgumentException();
        }
        String scheme = uri.getScheme();
        if (scheme == null || scheme.equals("file")) {
            setDataSource(uri.getPath());
            return;
        }
        AutoCloseable autoCloseable = null;
        try {
            try {
                AssetFileDescriptor assetFileDescriptorOpenAssetFileDescriptor = context.getContentResolver().openAssetFileDescriptor(uri, FullBackup.ROOT_TREE_TOKEN);
                if (assetFileDescriptorOpenAssetFileDescriptor == null) {
                    throw new IllegalArgumentException();
                }
                FileDescriptor fileDescriptor = assetFileDescriptorOpenAssetFileDescriptor.getFileDescriptor();
                if (!fileDescriptor.valid()) {
                    throw new IllegalArgumentException();
                }
                if (assetFileDescriptorOpenAssetFileDescriptor.getDeclaredLength() < 0) {
                    setDataSource(fileDescriptor);
                } else {
                    setDataSource(fileDescriptor, assetFileDescriptorOpenAssetFileDescriptor.getStartOffset(), assetFileDescriptorOpenAssetFileDescriptor.getDeclaredLength());
                }
                if (assetFileDescriptorOpenAssetFileDescriptor != null) {
                    try {
                        assetFileDescriptorOpenAssetFileDescriptor.close();
                    } catch (IOException unused) {
                    }
                }
            } catch (FileNotFoundException unused2) {
                throw new IllegalArgumentException();
            }
        } catch (SecurityException unused3) {
            if (0 != 0) {
                try {
                    autoCloseable.close();
                } catch (IOException unused4) {
                }
            }
            setDataSource(uri.toString());
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    autoCloseable.close();
                } catch (IOException unused5) {
                }
            }
            throw th;
        }
    }

    public Bitmap getFrameAtTime(long j, int i) {
        if (i < 0 || i > 3) {
            throw new IllegalArgumentException("Unsupported option: " + i);
        }
        return _getFrameAtTime(j, i);
    }

    public Bitmap getFrameAtTime(long j) {
        return getFrameAtTime(j, 2);
    }

    public Bitmap getFrameAtTime() {
        return getFrameAtTime(-1L, 2);
    }

    public byte[] getEmbeddedPicture() {
        return getEmbeddedPicture(65535);
    }

    protected void finalize() throws Throwable {
        try {
            native_finalize();
        } finally {
            super.finalize();
        }
    }
}
