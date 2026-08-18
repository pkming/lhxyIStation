package com.autonavi.base.ae.gmap.glyph;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import com.amap.api.col.p0003sl.ab;
import com.autonavi.base.amap.mapcore.tools.GLConvertUtil;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes2.dex */
public class GlyphLoader {
    private static Map<String, Typeface> FontFaceMap = new HashMap();

    private static native long nativeCreateGlyphLoader();

    private static native void nativeDestroyGlyphLoader(long j);

    private static GlyphMetrics loadGlyphMetrics(String str, FontStyle fontStyle, float f, String str2, boolean z, float f2, boolean z2, boolean z3) {
        GlyphMetrics glyphMetrics = new GlyphMetrics();
        if (fontStyle == null || TextUtils.isEmpty(str)) {
            return glyphMetrics;
        }
        try {
            if (!z2) {
                TextPaint textPaintNewTextPaint = newTextPaint(fontStyle, f, str2, z, f2);
                Rect rect = new Rect();
                textPaintNewTextPaint.getTextBounds(str, 0, str.length(), rect);
                if (rect.width() == 0 && rect.height() == 0) {
                    float fMeasureText = textPaintNewTextPaint.measureText(" ", 0, 1);
                    float fAbs = Math.abs(textPaintNewTextPaint.getFontMetrics().ascent) + Math.abs(textPaintNewTextPaint.getFontMetrics().descent);
                    rect.top = 0;
                    rect.left = 0;
                    rect.right = (int) fMeasureText;
                    rect.bottom = (int) fAbs;
                }
                if (z && f2 > 0.0f) {
                    float f3 = f2 / 2.0f;
                    rect.top = (int) (rect.top - f3);
                    rect.left = (int) (rect.left - f3);
                    rect.right = (int) (rect.right + f3);
                    rect.bottom = (int) (rect.bottom + f3);
                }
                glyphMetrics.bSuccess = true;
                glyphMetrics.fLeft = rect.left;
                glyphMetrics.fTop = Math.abs(textPaintNewTextPaint.getFontMetrics().ascent) - Math.abs(rect.top);
                glyphMetrics.nWidth = rect.width();
                glyphMetrics.nHeight = rect.height();
                glyphMetrics.fAdvance = textPaintNewTextPaint.measureText(str);
                textPaintNewTextPaint.setTypeface(null);
            } else {
                glyphMetrics.bSuccess = true;
                glyphMetrics.fLeft = 0.0f;
                glyphMetrics.fTop = 0.0f;
                int i = (int) f;
                glyphMetrics.nWidth = i;
                glyphMetrics.nHeight = i;
                glyphMetrics.fAdvance = f;
            }
        } catch (Exception unused) {
            glyphMetrics.bSuccess = false;
        }
        return glyphMetrics;
    }

    private static GlyphRaster loadGlyphRaster(String str, FontStyle fontStyle, float f, String str2, boolean z, float f2, boolean z2, boolean z3) {
        int i;
        Context context;
        GlyphRaster glyphRaster = new GlyphRaster();
        if (fontStyle == null || TextUtils.isEmpty(str)) {
            return glyphRaster;
        }
        try {
            context = ab.a;
        } catch (Throwable th) {
            Log.e("HighText", "highText exception:" + th.toString());
        }
        boolean zBooleanValue = context != null ? ((Boolean) ReflectUtil.invoke("android.view.accessibility.AccessibilityManager", (AccessibilityManager) context.getApplicationContext().getSystemService(Context.ACCESSIBILITY_SERVICE), "isHighTextContrastEnabled", null)).booleanValue() : false;
        try {
            TextPaint textPaintNewTextPaint = newTextPaint(fontStyle, f, str2, z, f2);
            Rect rect = new Rect();
            textPaintNewTextPaint.getTextBounds(str, 0, str.length(), rect);
            if (rect.width() == 0 && rect.height() == 0) {
                float fMeasureText = textPaintNewTextPaint.measureText(" ", 0, 1);
                float fAbs = Math.abs(textPaintNewTextPaint.getFontMetrics().ascent) + Math.abs(textPaintNewTextPaint.getFontMetrics().descent);
                rect.right = (int) fMeasureText;
                rect.bottom = (int) fAbs;
                rect.left = 0;
                rect.top = 0;
            }
            if (z && f2 > 0.0f) {
                float f3 = f2 / 2.0f;
                rect.top = (int) (rect.top - f3);
                rect.left = (int) (rect.left - f3);
                rect.right = (int) (rect.right + f3);
                rect.bottom = (int) (rect.bottom + f3);
            }
            if (!rect.isEmpty()) {
                Bitmap.Config config = Bitmap.Config.ALPHA_8;
                if (z2 || zBooleanValue) {
                    config = Bitmap.Config.ARGB_8888;
                    i = 4;
                } else {
                    i = 1;
                }
                int i2 = (z3 ? 3 : 0) * 2;
                Bitmap bitmapCreateBitmap = Bitmap.createBitmap(rect.width() + i2, rect.height() + i2, config);
                new Canvas(bitmapCreateBitmap).drawText(str, (0 - rect.left) + r13, (0 - rect.top) + r13, textPaintNewTextPaint);
                int iWidth = rect.width() + i2;
                int iHeight = rect.height() + i2;
                int i3 = iWidth * iHeight;
                byte[] bArr = new byte[i3 * i];
                bitmapCreateBitmap.copyPixelsToBuffer(ByteBuffer.wrap(bArr));
                if (zBooleanValue) {
                    byte[] bArr2 = new byte[i3];
                    for (int i4 = 0; i4 < iHeight; i4++) {
                        for (int i5 = 0; i5 < iWidth * i; i5 += i) {
                            int i6 = i4 * iWidth;
                            bArr2[(i5 / i) + i6] = bArr[(i6 * i) + i5];
                        }
                    }
                    bArr = bArr2;
                }
                glyphRaster.bitmapWidth = iWidth;
                glyphRaster.bitmapHeight = iHeight;
                if (z2) {
                    glyphRaster.bitmapPixelMode = 1;
                } else {
                    glyphRaster.bitmapPixelMode = 0;
                }
                glyphRaster.bitmapSize = bArr.length;
                glyphRaster.bitmapBuffer = bArr;
                bitmapCreateBitmap.recycle();
                glyphRaster.bSuccess = true;
            }
            textPaintNewTextPaint.setTypeface(null);
        } catch (Exception unused) {
            glyphRaster.bSuccess = false;
        }
        return glyphRaster;
    }

    private static GlyphRequestParam genGlyphRequestParam(byte[] bArr) {
        GlyphRequestParam glyphRequestParam = new GlyphRequestParam();
        int i = GLConvertUtil.getInt(bArr, 0);
        glyphRequestParam.strBuffer = new String(bArr, 4, i);
        int i2 = i + 4;
        Font font = new Font();
        font.nFontStyleCode = GLConvertUtil.getInt(bArr, i2);
        int i3 = i2 + 4;
        font.nFontSize = GLConvertUtil.getInt(bArr, i3);
        int i4 = i3 + 4;
        int i5 = GLConvertUtil.getInt(bArr, i4);
        int i6 = i4 + 4;
        font.strName = new String(bArr, i6, i5);
        int i7 = i6 + i5;
        FontMetrics fontMetrics = new FontMetrics();
        int i8 = GLConvertUtil.getInt(bArr, i7);
        int i9 = i7 + 4;
        fontMetrics.fAscent = i8 * 0.001f;
        int i10 = GLConvertUtil.getInt(bArr, i9);
        int i11 = i9 + 4;
        fontMetrics.fDescent = i10 * 0.001f;
        int i12 = GLConvertUtil.getInt(bArr, i11);
        int i13 = i11 + 4;
        fontMetrics.fLeading = i12 * 0.001f;
        int i14 = GLConvertUtil.getInt(bArr, i13);
        int i15 = i13 + 4;
        fontMetrics.fHeight = i14 * 0.001f;
        font.fontMetrics = fontMetrics;
        glyphRequestParam.font = font;
        glyphRequestParam.drawingMode = GLConvertUtil.getInt(bArr, i15);
        int i16 = i15 + 4;
        int i17 = GLConvertUtil.getInt(bArr, i16);
        int i18 = i16 + 4;
        glyphRequestParam.strokeWidth = i17 * 0.001f;
        int i19 = GLConvertUtil.getInt(bArr, i18);
        int i20 = i18 + 4;
        glyphRequestParam.languageArr = new String(bArr, i20, i19);
        glyphRequestParam.isEmoji = GLConvertUtil.getInt(bArr, i20);
        int i21 = i20 + 4;
        glyphRequestParam.isSDF = GLConvertUtil.getInt(bArr, i21);
        int i22 = i21 + 4;
        int i23 = GLConvertUtil.getInt(bArr, i22);
        int i24 = i22 + 4;
        if (1 == i23) {
            GlyphMetrics glyphMetrics = new GlyphMetrics();
            glyphMetrics.nWidth = GLConvertUtil.getInt(bArr, i24);
            int i25 = i24 + 4;
            glyphMetrics.nHeight = GLConvertUtil.getInt(bArr, i25);
            int i26 = i25 + 4;
            int i27 = GLConvertUtil.getInt(bArr, i26);
            int i28 = i26 + 4;
            glyphMetrics.fLeft = i27 * 0.001f;
            glyphMetrics.fTop = GLConvertUtil.getInt(bArr, i28) * 0.001f;
            glyphMetrics.fAdvance = GLConvertUtil.getInt(bArr, i28 + 4) * 0.001f;
            glyphRequestParam.fGlyphMetrics = glyphMetrics;
        }
        return glyphRequestParam;
    }

    private static FontMetricsRequestParam genFontMetricsParam(byte[] bArr) {
        FontMetricsRequestParam fontMetricsRequestParam = new FontMetricsRequestParam();
        fontMetricsRequestParam.fFontSize = GLConvertUtil.getInt(bArr, 0) * 0.001f;
        fontMetricsRequestParam.nFontStyleCode = GLConvertUtil.getInt(bArr, 4);
        int i = 12;
        if (1 == GLConvertUtil.getInt(bArr, 8)) {
            int i2 = GLConvertUtil.getInt(bArr, 12);
            fontMetricsRequestParam.strName = new String(bArr, 16, i2);
            i = i2 + 16;
        }
        fontMetricsRequestParam.languageArr = new String(bArr, i + 4, GLConvertUtil.getInt(bArr, i));
        return fontMetricsRequestParam;
    }

    private static GlyphMetrics getGlyphMetrics(byte[] bArr) {
        GlyphRequestParam glyphRequestParamGenGlyphRequestParam = genGlyphRequestParam(bArr);
        return loadGlyphMetrics(glyphRequestParamGenGlyphRequestParam.strBuffer, new FontStyle(glyphRequestParamGenGlyphRequestParam.font.nFontStyleCode), glyphRequestParamGenGlyphRequestParam.font.nFontSize, glyphRequestParamGenGlyphRequestParam.font.strName, glyphRequestParamGenGlyphRequestParam.drawingMode != 0, glyphRequestParamGenGlyphRequestParam.strokeWidth, glyphRequestParamGenGlyphRequestParam.isEmoji > 0, glyphRequestParamGenGlyphRequestParam.isSDF > 0);
    }

    private static GlyphRaster getGlyphRaster(byte[] bArr) {
        GlyphRequestParam glyphRequestParamGenGlyphRequestParam = genGlyphRequestParam(bArr);
        FontStyle fontStyle = new FontStyle(glyphRequestParamGenGlyphRequestParam.font.nFontStyleCode);
        boolean z = glyphRequestParamGenGlyphRequestParam.drawingMode != 0;
        if (glyphRequestParamGenGlyphRequestParam.drawingMode == 3) {
            return loadPathRaster(glyphRequestParamGenGlyphRequestParam.strBuffer, fontStyle, glyphRequestParamGenGlyphRequestParam.font.nFontSize, glyphRequestParamGenGlyphRequestParam.font.strName, z, 2.0f * glyphRequestParamGenGlyphRequestParam.strokeWidth);
        }
        return loadGlyphRaster(glyphRequestParamGenGlyphRequestParam.strBuffer, fontStyle, glyphRequestParamGenGlyphRequestParam.font.nFontSize, glyphRequestParamGenGlyphRequestParam.font.strName, z, glyphRequestParamGenGlyphRequestParam.strokeWidth, glyphRequestParamGenGlyphRequestParam.isEmoji > 0, glyphRequestParamGenGlyphRequestParam.isSDF > 0);
    }

    public static GlyphRaster loadPathRaster(String str, FontStyle fontStyle, float f, String str2, boolean z, float f2) {
        GlyphRaster glyphRaster = new GlyphRaster();
        if (fontStyle == null || TextUtils.isEmpty(str)) {
            return glyphRaster;
        }
        try {
            TextPaint textPaintNewTextPaint = newTextPaint(fontStyle, f, str2, false, 0.0f);
            Rect rect = new Rect();
            textPaintNewTextPaint.getTextBounds(str, 0, str.length(), rect);
            new Canvas(Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ALPHA_8)).drawText(str, 0 - rect.left, 0 - rect.top, textPaintNewTextPaint);
            TextPaint textPaintNewTextPaint2 = newTextPaint(fontStyle, f, str2, z, f2);
            Rect rect2 = new Rect();
            textPaintNewTextPaint2.getTextBounds(str, 0, str.length(), rect2);
            if (z && f2 > 0.0f) {
                float f3 = 0.5f * f2;
                rect2.top = (int) (rect2.top - f3);
                rect2.left = (int) (rect2.left - f3);
                rect2.right = (int) (rect2.right + f3);
                rect2.bottom = (int) (rect2.bottom + f3);
            }
            if (!rect2.isEmpty()) {
                Bitmap bitmapCreateBitmap = Bitmap.createBitmap(rect2.width(), rect2.height(), Bitmap.Config.ALPHA_8);
                Canvas canvas = new Canvas(bitmapCreateBitmap);
                float f4 = 0 - rect2.left;
                float f5 = 0 - rect2.top;
                Path path = new Path();
                textPaintNewTextPaint.getTextPath(str, 0, str.length(), f4, f5, path);
                canvas.drawPath(path, textPaintNewTextPaint2);
                int iWidth = rect2.width() * rect2.height();
                byte[] bArr = new byte[iWidth];
                ByteBuffer byteBufferWrap = ByteBuffer.wrap(bArr);
                glyphRaster.bitmapWidth = rect2.width();
                glyphRaster.bitmapHeight = rect2.height();
                glyphRaster.bitmapPixelMode = 0;
                glyphRaster.bitmapSize = iWidth;
                bitmapCreateBitmap.copyPixelsToBuffer(byteBufferWrap);
                bitmapCreateBitmap.recycle();
                glyphRaster.bitmapBuffer = bArr;
                glyphRaster.bSuccess = true;
            }
            textPaintNewTextPaint.setTypeface(null);
            textPaintNewTextPaint2.setTypeface(null);
        } catch (Exception unused) {
            glyphRaster.bSuccess = false;
        }
        return glyphRaster;
    }

    private static FontMetrics getFontMetrics(byte[] bArr) {
        FontMetricsRequestParam fontMetricsRequestParamGenFontMetricsParam = genFontMetricsParam(bArr);
        TextPaint textPaintNewTextPaint = newTextPaint(new FontStyle(fontMetricsRequestParamGenFontMetricsParam.nFontStyleCode), fontMetricsRequestParamGenFontMetricsParam.fFontSize, fontMetricsRequestParamGenFontMetricsParam.strName, false, 0.0f);
        Paint.FontMetrics fontMetrics = textPaintNewTextPaint.getFontMetrics();
        FontMetrics fontMetrics2 = new FontMetrics();
        fontMetrics2.bSuccess = true;
        fontMetrics2.fAscent = Math.abs(fontMetrics.ascent);
        fontMetrics2.fDescent = Math.abs(fontMetrics.descent);
        fontMetrics2.fLeading = Math.abs(fontMetrics.leading);
        fontMetrics2.fHeight = Math.abs(fontMetrics.ascent) + Math.abs(fontMetrics.descent);
        textPaintNewTextPaint.setTypeface(null);
        return fontMetrics2;
    }

    private static String decodeUnicode(short s) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append((char) s);
        return stringBuffer.toString();
    }

    private static String decodeUnicode(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(str);
        return stringBuffer.toString();
    }

    /* JADX WARN: Can't wrap try/catch for region: R(16:5|(1:7)(1:8)|9|(1:15)(1:14)|16|SW:17|(9:(0)|54|29|30|(1:32)|33|(1:48)(4:56|37|6a|43)|49|50)(1:25)|28|54|29|30|(0)|33|(1:48)(0)|49|50) */
    /* JADX WARN: Removed duplicated region for block: B:32:0x005e  */
    /* JADX WARN: Removed duplicated region for block: B:48:0x008a  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static android.text.TextPaint newTextPaint(com.autonavi.base.ae.gmap.glyph.FontStyle r2, float r3, java.lang.String r4, boolean r5, float r6) {
        /*
            android.text.TextPaint r0 = new android.text.TextPaint
            r0.<init>()
            if (r2 != 0) goto L8
            return r0
        L8:
            r1 = -1
            r0.setColor(r1)
            r1 = 1
            r0.setAntiAlias(r1)
            r0.setFilterBitmap(r1)
            r0.setTextSize(r3)
            android.graphics.Paint$Align r3 = android.graphics.Paint.Align.LEFT
            r0.setTextAlign(r3)
            if (r5 == 0) goto L26
            android.graphics.Paint$Style r3 = android.graphics.Paint.Style.STROKE
            r0.setStyle(r3)
            r0.setStrokeWidth(r6)
            goto L2b
        L26:
            android.graphics.Paint$Style r3 = android.graphics.Paint.Style.FILL
            r0.setStyle(r3)
        L2b:
            int r3 = r2.getSlant()
            r5 = 2
            r6 = 0
            if (r3 == 0) goto L3a
            if (r3 == r1) goto L38
            if (r3 == r5) goto L38
            goto L3a
        L38:
            r3 = r1
            goto L3b
        L3a:
            r3 = r6
        L3b:
            int r2 = r2.getWeight()
            switch(r2) {
                case 0: goto L45;
                case 100: goto L45;
                case 200: goto L45;
                case 300: goto L45;
                case 400: goto L45;
                case 500: goto L43;
                case 600: goto L43;
                case 700: goto L43;
                case 800: goto L43;
                case 900: goto L43;
                case 1000: goto L43;
                default: goto L42;
            }
        L42:
            goto L45
        L43:
            r2 = r1
            goto L46
        L45:
            r2 = r6
        L46:
            if (r2 == 0) goto L4e
            if (r3 == 0) goto L4e
            r0.setFakeBoldText(r1)
            goto L57
        L4e:
            if (r2 == 0) goto L54
            r0.setFakeBoldText(r1)
            goto L57
        L54:
            if (r3 == 0) goto L57
            goto L58
        L57:
            r5 = r6
        L58:
            int r2 = android.os.Build.VERSION.SDK_INT     // Catch: java.lang.NumberFormatException -> L5f
            r3 = 23
            if (r2 < r3) goto L5f
            r1 = r6
        L5f:
            boolean r2 = r4.isEmpty()
            if (r2 != 0) goto L8a
            if (r1 == 0) goto L68
            goto L8a
        L68:
            java.util.Map<java.lang.String, android.graphics.Typeface> r2 = com.autonavi.base.ae.gmap.glyph.GlyphLoader.FontFaceMap     // Catch: java.lang.Exception -> L83
            monitor-enter(r2)     // Catch: java.lang.Exception -> L83
            java.util.Map<java.lang.String, android.graphics.Typeface> r3 = com.autonavi.base.ae.gmap.glyph.GlyphLoader.FontFaceMap     // Catch: java.lang.Throwable -> L80
            java.lang.Object r3 = r3.get(r4)     // Catch: java.lang.Throwable -> L80
            android.graphics.Typeface r3 = (android.graphics.Typeface) r3     // Catch: java.lang.Throwable -> L80
            if (r3 != 0) goto L7e
            android.graphics.Typeface r3 = android.graphics.Typeface.createFromFile(r4)     // Catch: java.lang.Throwable -> L80
            java.util.Map<java.lang.String, android.graphics.Typeface> r6 = com.autonavi.base.ae.gmap.glyph.GlyphLoader.FontFaceMap     // Catch: java.lang.Throwable -> L80
            r6.put(r4, r3)     // Catch: java.lang.Throwable -> L80
        L7e:
            monitor-exit(r2)     // Catch: java.lang.Throwable -> L80
            goto L90
        L80:
            r3 = move-exception
            monitor-exit(r2)     // Catch: java.lang.Throwable -> L80
            throw r3     // Catch: java.lang.Exception -> L83
        L83:
            android.graphics.Typeface r2 = android.graphics.Typeface.DEFAULT
            android.graphics.Typeface r3 = android.graphics.Typeface.create(r2, r5)
            goto L90
        L8a:
            android.graphics.Typeface r2 = android.graphics.Typeface.DEFAULT
            android.graphics.Typeface r3 = android.graphics.Typeface.create(r2, r5)
        L90:
            r0.setTypeface(r3)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.autonavi.base.ae.gmap.glyph.GlyphLoader.newTextPaint(com.autonavi.base.ae.gmap.glyph.FontStyle, float, java.lang.String, boolean, float):android.text.TextPaint");
    }

    public static long createGlyphLoader() {
        return nativeCreateGlyphLoader();
    }

    public static void destroyGlyphLoader(long j) {
        nativeDestroyGlyphLoader(j);
    }
}
