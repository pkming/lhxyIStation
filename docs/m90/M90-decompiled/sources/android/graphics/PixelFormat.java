package android.graphics;

/* JADX INFO: loaded from: classes.dex */
public class PixelFormat {

    @Deprecated
    public static final int A_8 = 8;

    @Deprecated
    public static final int JPEG = 256;

    @Deprecated
    public static final int LA_88 = 10;

    @Deprecated
    public static final int L_8 = 9;
    public static final int OPAQUE = -1;

    @Deprecated
    public static final int RGBA_4444 = 7;

    @Deprecated
    public static final int RGBA_5551 = 6;
    public static final int RGBA_8888 = 1;
    public static final int RGBX_8888 = 2;

    @Deprecated
    public static final int RGB_332 = 11;
    public static final int RGB_565 = 4;
    public static final int RGB_888 = 3;
    public static final int TRANSLUCENT = -3;
    public static final int TRANSPARENT = -2;
    public static final int UNKNOWN = 0;

    @Deprecated
    public static final int YCbCr_420_SP = 17;

    @Deprecated
    public static final int YCbCr_422_I = 20;

    @Deprecated
    public static final int YCbCr_422_SP = 16;
    public int bitsPerPixel;
    public int bytesPerPixel;

    public static boolean formatHasAlpha(int i) {
        return i == -3 || i == -2 || i == 1 || i == 10 || i == 6 || i == 7 || i == 8;
    }

    public static void getPixelFormatInfo(int i, PixelFormat pixelFormat) {
        if (i == 1 || i == 2) {
            pixelFormat.bitsPerPixel = 32;
            pixelFormat.bytesPerPixel = 4;
            return;
        }
        if (i == 3) {
            pixelFormat.bitsPerPixel = 24;
            pixelFormat.bytesPerPixel = 3;
            return;
        }
        if (i != 4) {
            if (i != 16) {
                if (i == 17) {
                    pixelFormat.bitsPerPixel = 12;
                    pixelFormat.bytesPerPixel = 1;
                    return;
                } else if (i != 20) {
                    switch (i) {
                        case 6:
                        case 7:
                        case 10:
                            break;
                        case 8:
                        case 9:
                        case 11:
                            pixelFormat.bitsPerPixel = 8;
                            pixelFormat.bytesPerPixel = 1;
                            return;
                        default:
                            throw new IllegalArgumentException("unkonwon pixel format " + i);
                    }
                }
            }
            pixelFormat.bitsPerPixel = 16;
            pixelFormat.bytesPerPixel = 1;
            return;
        }
        pixelFormat.bitsPerPixel = 16;
        pixelFormat.bytesPerPixel = 2;
    }
}
