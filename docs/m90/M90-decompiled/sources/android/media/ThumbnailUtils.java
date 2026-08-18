package android.media;

import android.app.backup.FullBackup;
import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.MediaFile;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;

/* JADX INFO: loaded from: classes.dex */
public class ThumbnailUtils {
    private static final int MAX_NUM_PIXELS_MICRO_THUMBNAIL = 19200;
    private static final int MAX_NUM_PIXELS_THUMBNAIL = 196608;
    private static final int OPTIONS_NONE = 0;
    public static final int OPTIONS_RECYCLE_INPUT = 2;
    private static final int OPTIONS_SCALE_UP = 1;
    private static final String TAG = "ThumbnailUtils";
    public static final int TARGET_SIZE_MICRO_THUMBNAIL = 96;
    public static final int TARGET_SIZE_MINI_THUMBNAIL = 320;
    private static final int UNCONSTRAINED = -1;

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r8v0, types: [android.media.ThumbnailUtils$1] */
    /* JADX WARN: Type inference failed for: r8v1 */
    /* JADX WARN: Type inference failed for: r8v10 */
    /* JADX WARN: Type inference failed for: r8v13 */
    /* JADX WARN: Type inference failed for: r8v14 */
    /* JADX WARN: Type inference failed for: r8v15 */
    /* JADX WARN: Type inference failed for: r8v16 */
    /* JADX WARN: Type inference failed for: r8v17 */
    /* JADX WARN: Type inference failed for: r8v18 */
    /* JADX WARN: Type inference failed for: r8v2 */
    /* JADX WARN: Type inference failed for: r8v3, types: [java.io.FileInputStream] */
    /* JADX WARN: Type inference failed for: r8v4 */
    /* JADX WARN: Type inference failed for: r8v7 */
    /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:32:0x006f -> B:70:0x00bf). Please report as a decompilation issue!!! */
    public static Bitmap createImageThumbnail(String str, int i) throws Throwable {
        Bitmap bitmapDecodeFileDescriptor;
        FileInputStream fileInputStream;
        FileDescriptor fd;
        BitmapFactory.Options options;
        boolean z = i == 1;
        int i2 = z ? 320 : 96;
        int i3 = z ? 196608 : MAX_NUM_PIXELS_MICRO_THUMBNAIL;
        ?? r8 = 0;
        r8 = 0;
        FileInputStream fileInputStream2 = null;
        FileInputStream fileInputStream3 = null;
        r8 = 0;
        SizedThumbnailBitmap sizedThumbnailBitmap = new SizedThumbnailBitmap();
        MediaFile.MediaFileType fileType = MediaFile.getFileType(str);
        if (fileType == null || fileType.fileType != 31) {
            bitmapDecodeFileDescriptor = null;
        } else {
            createThumbnailFromEXIF(str, i2, i3, sizedThumbnailBitmap);
            bitmapDecodeFileDescriptor = sizedThumbnailBitmap.mBitmap;
        }
        try {
            try {
            } catch (Throwable th) {
                th = th;
            }
        } catch (IOException e) {
            Log.e(TAG, "", e);
            r8 = r8;
        }
        if (bitmapDecodeFileDescriptor == null) {
            try {
                fileInputStream = new FileInputStream(str);
            } catch (IOException e2) {
                e = e2;
            } catch (OutOfMemoryError e3) {
                e = e3;
            }
            try {
                fd = fileInputStream.getFD();
                options = new BitmapFactory.Options();
                options.inSampleSize = 1;
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(fd, null, options);
            } catch (IOException e4) {
                e = e4;
                fileInputStream2 = fileInputStream;
                Log.e(TAG, "", e);
                r8 = fileInputStream2;
                if (fileInputStream2 != null) {
                    fileInputStream2.close();
                    r8 = fileInputStream2;
                }
            } catch (OutOfMemoryError e5) {
                e = e5;
                fileInputStream3 = fileInputStream;
                Log.e(TAG, "Unable to decode file " + str + ". OutOfMemoryError.", e);
                r8 = fileInputStream3;
                if (fileInputStream3 != null) {
                    fileInputStream3.close();
                    r8 = fileInputStream3;
                }
            } catch (Throwable th2) {
                th = th2;
                r8 = fileInputStream;
                if (r8 != 0) {
                    try {
                        r8.close();
                    } catch (IOException e6) {
                        Log.e(TAG, "", e6);
                    }
                }
                throw th;
            }
            if (!options.mCancel && options.outWidth != -1 && options.outHeight != -1) {
                options.inSampleSize = computeSampleSize(options, i2, i3);
                options.inJustDecodeBounds = false;
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bitmapDecodeFileDescriptor = BitmapFactory.decodeFileDescriptor(fd, null, options);
                fileInputStream.close();
            }
            try {
                fileInputStream.close();
            } catch (IOException e7) {
                Log.e(TAG, "", e7);
            }
            return null;
        }
        return i == 3 ? extractThumbnail(bitmapDecodeFileDescriptor, 96, 96, 2) : bitmapDecodeFileDescriptor;
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x001e A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:13:0x001f  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public static android.graphics.Bitmap createVideoThumbnail(java.lang.String r4, int r5) {
        /*
            android.media.MediaMetadataRetriever r0 = new android.media.MediaMetadataRetriever
            r0.<init>()
            r1 = 0
            r0.setDataSource(r4)     // Catch: java.lang.Throwable -> L13 java.lang.Throwable -> L18
            r2 = -1
            android.graphics.Bitmap r4 = r0.getFrameAtTime(r2)     // Catch: java.lang.Throwable -> L13 java.lang.Throwable -> L18
            r0.release()     // Catch: java.lang.RuntimeException -> L1c
            goto L1c
        L13:
            r4 = move-exception
            r0.release()     // Catch: java.lang.RuntimeException -> L17
        L17:
            throw r4
        L18:
            r0.release()     // Catch: java.lang.RuntimeException -> L1b
        L1b:
            r4 = r1
        L1c:
            if (r4 != 0) goto L1f
            return r1
        L1f:
            r0 = 1
            if (r5 != r0) goto L47
            int r5 = r4.getWidth()
            int r1 = r4.getHeight()
            int r2 = java.lang.Math.max(r5, r1)
            r3 = 512(0x200, float:7.175E-43)
            if (r2 <= r3) goto L51
            r3 = 1140850688(0x44000000, float:512.0)
            float r2 = (float) r2
            float r3 = r3 / r2
            float r5 = (float) r5
            float r5 = r5 * r3
            int r5 = java.lang.Math.round(r5)
            float r1 = (float) r1
            float r3 = r3 * r1
            int r1 = java.lang.Math.round(r3)
            android.graphics.Bitmap r4 = android.graphics.Bitmap.createScaledBitmap(r4, r5, r1, r0)
            goto L51
        L47:
            r0 = 3
            if (r5 != r0) goto L51
            r5 = 2
            r0 = 96
            android.graphics.Bitmap r4 = extractThumbnail(r4, r0, r0, r5)
        L51:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.ThumbnailUtils.createVideoThumbnail(java.lang.String, int):android.graphics.Bitmap");
    }

    public static Bitmap extractThumbnail(Bitmap bitmap, int i, int i2) {
        return extractThumbnail(bitmap, i, i2, 0);
    }

    public static Bitmap extractThumbnail(Bitmap bitmap, int i, int i2, int i3) {
        float f;
        int height;
        if (bitmap == null) {
            return null;
        }
        if (bitmap.getWidth() < bitmap.getHeight()) {
            f = i;
            height = bitmap.getWidth();
        } else {
            f = i2;
            height = bitmap.getHeight();
        }
        float f2 = f / height;
        Matrix matrix = new Matrix();
        matrix.setScale(f2, f2);
        return transform(matrix, bitmap, i, i2, i3 | 1);
    }

    private static int computeSampleSize(BitmapFactory.Options options, int i, int i2) {
        int iComputeInitialSampleSize = computeInitialSampleSize(options, i, i2);
        if (iComputeInitialSampleSize > 8) {
            return 8 * ((iComputeInitialSampleSize + 7) / 8);
        }
        int i3 = 1;
        while (i3 < iComputeInitialSampleSize) {
            i3 <<= 1;
        }
        return i3;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int i, int i2) {
        int iMin;
        double d = options.outWidth;
        double d2 = options.outHeight;
        int iCeil = i2 == -1 ? 1 : (int) Math.ceil(Math.sqrt((d * d2) / ((double) i2)));
        if (i == -1) {
            iMin = 128;
        } else {
            double d3 = i;
            iMin = (int) Math.min(Math.floor(d / d3), Math.floor(d2 / d3));
        }
        if (iMin < iCeil) {
            return iCeil;
        }
        if (i2 == -1 && i == -1) {
            return 1;
        }
        return i == -1 ? iCeil : iMin;
    }

    private static Bitmap makeBitmap(int i, int i2, Uri uri, ContentResolver contentResolver, ParcelFileDescriptor parcelFileDescriptor, BitmapFactory.Options options) {
        if (parcelFileDescriptor == null) {
            try {
                parcelFileDescriptor = makeInputStream(uri, contentResolver);
            } catch (OutOfMemoryError e) {
                Log.e(TAG, "Got oom exception ", e);
                return null;
            } finally {
                closeSilently(parcelFileDescriptor);
            }
        }
        if (parcelFileDescriptor == null) {
            return null;
        }
        if (options == null) {
            options = new BitmapFactory.Options();
        }
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        options.inSampleSize = 1;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        if (!options.mCancel && options.outWidth != -1 && options.outHeight != -1) {
            options.inSampleSize = computeSampleSize(options, i, i2);
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        }
        return null;
    }

    private static void closeSilently(ParcelFileDescriptor parcelFileDescriptor) {
        if (parcelFileDescriptor == null) {
            return;
        }
        try {
            parcelFileDescriptor.close();
        } catch (Throwable unused) {
        }
    }

    private static ParcelFileDescriptor makeInputStream(Uri uri, ContentResolver contentResolver) {
        try {
            return contentResolver.openFileDescriptor(uri, FullBackup.ROOT_TREE_TOKEN);
        } catch (IOException unused) {
            return null;
        }
    }

    /* JADX WARN: Removed duplicated region for block: B:37:0x00b3  */
    /* JADX WARN: Removed duplicated region for block: B:38:0x00c4  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static android.graphics.Bitmap transform(android.graphics.Matrix r14, android.graphics.Bitmap r15, int r16, int r17, int r18) {
        /*
            Method dump skipped, instruction units count: 240
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.media.ThumbnailUtils.transform(android.graphics.Matrix, android.graphics.Bitmap, int, int, int):android.graphics.Bitmap");
    }

    private static class SizedThumbnailBitmap {
        public Bitmap mBitmap;
        public byte[] mThumbnailData;
        public int mThumbnailHeight;
        public int mThumbnailWidth;

        private SizedThumbnailBitmap() {
        }
    }

    private static void createThumbnailFromEXIF(String str, int i, int i2, SizedThumbnailBitmap sizedThumbnailBitmap) throws Throwable {
        int i3;
        if (str == null) {
            return;
        }
        byte[] thumbnail = null;
        try {
            thumbnail = new ExifInterface(str).getThumbnail();
        } catch (IOException e) {
            Log.w(TAG, e);
        }
        BitmapFactory.Options options = new BitmapFactory.Options();
        BitmapFactory.Options options2 = new BitmapFactory.Options();
        if (thumbnail != null) {
            options2.inJustDecodeBounds = true;
            BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.length, options2);
            options2.inSampleSize = computeSampleSize(options2, i, i2);
            i3 = options2.outWidth / options2.inSampleSize;
        } else {
            i3 = 0;
        }
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(str, options);
        options.inSampleSize = computeSampleSize(options, i, i2);
        int i4 = options.outWidth / options.inSampleSize;
        if (thumbnail != null && i3 >= i4) {
            int i5 = options2.outWidth;
            int i6 = options2.outHeight;
            options2.inJustDecodeBounds = false;
            sizedThumbnailBitmap.mBitmap = BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.length, options2);
            if (sizedThumbnailBitmap.mBitmap != null) {
                sizedThumbnailBitmap.mThumbnailData = thumbnail;
                sizedThumbnailBitmap.mThumbnailWidth = i5;
                sizedThumbnailBitmap.mThumbnailHeight = i6;
                return;
            }
            return;
        }
        options.inJustDecodeBounds = false;
        sizedThumbnailBitmap.mBitmap = BitmapFactory.decodeFile(str, options);
    }
}
