package android.graphics;

import android.hardware.Camera;
import android.util.MathUtils;
import com.android.internal.util.XmlUtils;
import java.util.HashMap;
import java.util.Locale;

/* JADX INFO: loaded from: classes.dex */
public class Color {
    public static final int BLACK = -16777216;
    public static final int BLUE = -16776961;
    public static final int CYAN = -16711681;
    public static final int DKGRAY = -12303292;
    public static final int GRAY = -7829368;
    public static final int GREEN = -16711936;
    public static final int LTGRAY = -3355444;
    public static final int MAGENTA = -65281;
    public static final int RED = -65536;
    public static final int TRANSPARENT = 0;
    public static final int WHITE = -1;
    public static final int YELLOW = -256;
    private static final HashMap<String, Integer> sColorNameMap;

    public static int alpha(int i) {
        return i >>> 24;
    }

    public static int argb(int i, int i2, int i3, int i4) {
        return (i << 24) | (i2 << 16) | (i3 << 8) | i4;
    }

    public static int blue(int i) {
        return i & 255;
    }

    public static int green(int i) {
        return (i >> 8) & 255;
    }

    private static native int nativeHSVToColor(int i, float[] fArr);

    private static native void nativeRGBToHSV(int i, int i2, int i3, float[] fArr);

    public static int red(int i) {
        return (i >> 16) & 255;
    }

    public static int rgb(int i, int i2, int i3) {
        return (i << 16) | (-16777216) | (i2 << 8) | i3;
    }

    public static float hue(int i) {
        int i2 = (i >> 16) & 255;
        int i3 = (i >> 8) & 255;
        int i4 = i & 255;
        int iMax = Math.max(i4, Math.max(i2, i3));
        int iMin = Math.min(i4, Math.min(i2, i3));
        if (iMax == iMin) {
            return 0.0f;
        }
        float f = iMax - iMin;
        float f2 = (iMax - i2) / f;
        float f3 = (iMax - i3) / f;
        float f4 = (iMax - i4) / f;
        float f5 = (i2 == iMax ? f4 - f3 : i3 == iMax ? (f2 + 2.0f) - f4 : (f3 + 4.0f) - f2) / 6.0f;
        return f5 < 0.0f ? f5 + 1.0f : f5;
    }

    public static float saturation(int i) {
        int i2 = (i >> 16) & 255;
        int i3 = (i >> 8) & 255;
        int i4 = i & 255;
        int iMax = Math.max(i4, Math.max(i2, i3));
        if (iMax == Math.min(i4, Math.min(i2, i3))) {
            return 0.0f;
        }
        return (iMax - r3) / iMax;
    }

    public static float brightness(int i) {
        return Math.max(i & 255, Math.max((i >> 16) & 255, (i >> 8) & 255)) / 255.0f;
    }

    public static int parseColor(String str) {
        if (str.charAt(0) == '#') {
            long j = Long.parseLong(str.substring(1), 16);
            if (str.length() == 7) {
                j |= -16777216;
            } else if (str.length() != 9) {
                throw new IllegalArgumentException("Unknown color");
            }
            return (int) j;
        }
        Integer num = sColorNameMap.get(str.toLowerCase(Locale.ROOT));
        if (num != null) {
            return num.intValue();
        }
        throw new IllegalArgumentException("Unknown color");
    }

    public static int HSBtoColor(float[] fArr) {
        return HSBtoColor(fArr[0], fArr[1], fArr[2]);
    }

    public static int HSBtoColor(float f, float f2, float f3) {
        float f4 = 0.0f;
        float fConstrain = MathUtils.constrain(f, 0.0f, 1.0f);
        float fConstrain2 = MathUtils.constrain(f2, 0.0f, 1.0f);
        float fConstrain3 = MathUtils.constrain(f3, 0.0f, 1.0f);
        float f5 = (fConstrain - ((int) fConstrain)) * 6.0f;
        int i = (int) f5;
        float f6 = f5 - i;
        float f7 = (1.0f - fConstrain2) * fConstrain3;
        float f8 = (1.0f - (fConstrain2 * f6)) * fConstrain3;
        float f9 = (1.0f - (fConstrain2 * (1.0f - f6))) * fConstrain3;
        if (i == 0) {
            f4 = fConstrain3;
            fConstrain3 = f9;
        } else if (i == 1) {
            f4 = f8;
        } else if (i == 2) {
            f4 = f7;
            f7 = f9;
        } else if (i == 3) {
            f4 = f7;
            f7 = fConstrain3;
            fConstrain3 = f8;
        } else if (i == 4) {
            f4 = f9;
            f7 = fConstrain3;
            fConstrain3 = f7;
        } else if (i != 5) {
            fConstrain3 = 0.0f;
            f7 = 0.0f;
        } else {
            f4 = fConstrain3;
            fConstrain3 = f7;
            f7 = f8;
        }
        return (-16777216) | (((int) (f4 * 255.0f)) << 16) | (((int) (fConstrain3 * 255.0f)) << 8) | ((int) (f7 * 255.0f));
    }

    public static void RGBToHSV(int i, int i2, int i3, float[] fArr) {
        if (fArr.length < 3) {
            throw new RuntimeException("3 components required for hsv");
        }
        nativeRGBToHSV(i, i2, i3, fArr);
    }

    public static void colorToHSV(int i, float[] fArr) {
        RGBToHSV((i >> 16) & 255, (i >> 8) & 255, i & 255, fArr);
    }

    public static int HSVToColor(float[] fArr) {
        return HSVToColor(255, fArr);
    }

    public static int HSVToColor(int i, float[] fArr) {
        if (fArr.length < 3) {
            throw new RuntimeException("3 components required for hsv");
        }
        return nativeHSVToColor(i, fArr);
    }

    public static int getHtmlColor(String str) {
        Integer num = sColorNameMap.get(str.toLowerCase(Locale.ROOT));
        if (num != null) {
            return num.intValue();
        }
        try {
            return XmlUtils.convertValueToInt(str, -1);
        } catch (NumberFormatException unused) {
            return -1;
        }
    }

    static {
        HashMap<String, Integer> map = new HashMap<>();
        sColorNameMap = map;
        map.put("black", -16777216);
        Integer numValueOf = Integer.valueOf(DKGRAY);
        map.put("darkgray", numValueOf);
        Integer numValueOf2 = Integer.valueOf(GRAY);
        map.put("gray", numValueOf2);
        Integer numValueOf3 = Integer.valueOf(LTGRAY);
        map.put("lightgray", numValueOf3);
        map.put("white", -1);
        map.put("red", -65536);
        Integer numValueOf4 = Integer.valueOf(GREEN);
        map.put("green", numValueOf4);
        map.put("blue", Integer.valueOf(BLUE));
        map.put("yellow", -256);
        Integer numValueOf5 = Integer.valueOf(CYAN);
        map.put("cyan", numValueOf5);
        Integer numValueOf6 = Integer.valueOf(MAGENTA);
        map.put("magenta", numValueOf6);
        map.put(Camera.Parameters.EFFECT_AQUA, numValueOf5);
        map.put("fuchsia", numValueOf6);
        map.put("darkgrey", numValueOf);
        map.put("grey", numValueOf2);
        map.put("lightgrey", numValueOf3);
        map.put("lime", numValueOf4);
        map.put("maroon", -8388608);
        map.put("navy", -16777088);
        map.put("olive", -8355840);
        map.put("purple", -8388480);
        map.put("silver", -4144960);
        map.put("teal", -16744320);
    }
}
