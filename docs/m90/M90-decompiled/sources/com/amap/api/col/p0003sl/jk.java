package com.amap.api.col.p0003sl;

import android.media.MediaPlayer;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import org.apache.poi.hssf.record.PaletteRecord;
import org.apache.poi.hssf.record.formula.Area3DPtg;
import org.apache.poi.hssf.record.formula.AttrPtg;
import org.apache.poi.hssf.record.formula.GreaterThanPtg;
import org.apache.poi.hssf.record.formula.IntPtg;
import org.apache.poi.hssf.record.formula.MemFuncPtg;
import org.apache.poi.hssf.record.formula.MissingArgPtg;
import org.apache.poi.hssf.record.formula.NotEqualPtg;
import org.apache.poi.hssf.record.formula.NumberPtg;
import org.apache.poi.hssf.record.formula.ParenthesisPtg;
import org.apache.poi.hssf.record.formula.Ref3DPtg;
import org.apache.poi.hssf.record.formula.UnaryMinusPtg;
import org.apache.poi.hssf.record.formula.UnaryPlusPtg;
import org.apache.poi.hssf.usermodel.HSSFErrorConstants;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: compiled from: Base64Util.java */
/* JADX INFO: loaded from: classes2.dex */
public class jk {
    static final /* synthetic */ boolean a = true;
    private static final byte[] b = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, TarConstants.LF_GNUTYPE_LONGLINK, TarConstants.LF_GNUTYPE_LONGNAME, 77, 78, 79, 80, 81, 82, TarConstants.LF_GNUTYPE_SPARSE, 84, 85, 86, 87, TarConstants.LF_PAX_EXTENDED_HEADER_UC, 89, 90, 97, 98, 99, 100, 101, 102, TarConstants.LF_PAX_GLOBAL_EXTENDED_HEADER, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, TarConstants.LF_PAX_EXTENDED_HEADER_LC, 121, 122, TarConstants.LF_NORMAL, TarConstants.LF_LINK, TarConstants.LF_SYMLINK, TarConstants.LF_CHR, TarConstants.LF_BLK, TarConstants.LF_DIR, TarConstants.LF_FIFO, TarConstants.LF_CONTIG, PaletteRecord.STANDARD_PALETTE_SIZE, 57, 43, 47};
    private static final byte[] c = {-9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, -9, 63, TarConstants.LF_BLK, TarConstants.LF_DIR, TarConstants.LF_FIFO, TarConstants.LF_CONTIG, PaletteRecord.STANDARD_PALETTE_SIZE, 57, Ref3DPtg.sid, Area3DPtg.sid, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, GreaterThanPtg.sid, NotEqualPtg.sid, HSSFErrorConstants.ERROR_VALUE, 16, 17, UnaryPlusPtg.sid, UnaryMinusPtg.sid, 20, ParenthesisPtg.sid, MissingArgPtg.sid, 23, 24, AttrPtg.sid, -9, -9, -9, -9, -9, -9, 26, 27, 28, 29, IntPtg.sid, NumberPtg.sid, 32, 33, 34, 35, 36, 37, 38, 39, 40, MemFuncPtg.sid, HSSFErrorConstants.ERROR_NA, 43, 44, 45, 46, 47, TarConstants.LF_NORMAL, TarConstants.LF_LINK, TarConstants.LF_SYMLINK, TarConstants.LF_CHR, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9};
    private static final byte[] d = {65, 66, 67, 68, 69, 70, 71, 72, 73, 74, TarConstants.LF_GNUTYPE_LONGLINK, TarConstants.LF_GNUTYPE_LONGNAME, 77, 78, 79, 80, 81, 82, TarConstants.LF_GNUTYPE_SPARSE, 84, 85, 86, 87, TarConstants.LF_PAX_EXTENDED_HEADER_UC, 89, 90, 97, 98, 99, 100, 101, 102, TarConstants.LF_PAX_GLOBAL_EXTENDED_HEADER, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, TarConstants.LF_PAX_EXTENDED_HEADER_LC, 121, 122, TarConstants.LF_NORMAL, TarConstants.LF_LINK, TarConstants.LF_SYMLINK, TarConstants.LF_CHR, TarConstants.LF_BLK, TarConstants.LF_DIR, TarConstants.LF_FIFO, TarConstants.LF_CONTIG, PaletteRecord.STANDARD_PALETTE_SIZE, 57, 45, 95};
    private static final byte[] e = {-9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 62, -9, -9, TarConstants.LF_BLK, TarConstants.LF_DIR, TarConstants.LF_FIFO, TarConstants.LF_CONTIG, PaletteRecord.STANDARD_PALETTE_SIZE, 57, Ref3DPtg.sid, Area3DPtg.sid, 60, 61, -9, -9, -9, -1, -9, -9, -9, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, GreaterThanPtg.sid, NotEqualPtg.sid, HSSFErrorConstants.ERROR_VALUE, 16, 17, UnaryPlusPtg.sid, UnaryMinusPtg.sid, 20, ParenthesisPtg.sid, MissingArgPtg.sid, 23, 24, AttrPtg.sid, -9, -9, -9, -9, 63, -9, 26, 27, 28, 29, IntPtg.sid, NumberPtg.sid, 32, 33, 34, 35, 36, 37, 38, 39, 40, MemFuncPtg.sid, HSSFErrorConstants.ERROR_NA, 43, 44, 45, 46, 47, TarConstants.LF_NORMAL, TarConstants.LF_LINK, TarConstants.LF_SYMLINK, TarConstants.LF_CHR, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9};
    private static final byte[] f = {45, TarConstants.LF_NORMAL, TarConstants.LF_LINK, TarConstants.LF_SYMLINK, TarConstants.LF_CHR, TarConstants.LF_BLK, TarConstants.LF_DIR, TarConstants.LF_FIFO, TarConstants.LF_CONTIG, PaletteRecord.STANDARD_PALETTE_SIZE, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, TarConstants.LF_GNUTYPE_LONGLINK, TarConstants.LF_GNUTYPE_LONGNAME, 77, 78, 79, 80, 81, 82, TarConstants.LF_GNUTYPE_SPARSE, 84, 85, 86, 87, TarConstants.LF_PAX_EXTENDED_HEADER_UC, 89, 90, 95, 97, 98, 99, 100, 101, 102, TarConstants.LF_PAX_GLOBAL_EXTENDED_HEADER, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, TarConstants.LF_PAX_EXTENDED_HEADER_LC, 121, 122};
    private static final byte[] g = {-9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -5, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -5, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, 0, -9, -9, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, -9, -9, -9, -1, -9, -9, -9, 11, 12, GreaterThanPtg.sid, NotEqualPtg.sid, HSSFErrorConstants.ERROR_VALUE, 16, 17, UnaryPlusPtg.sid, UnaryMinusPtg.sid, 20, ParenthesisPtg.sid, MissingArgPtg.sid, 23, 24, AttrPtg.sid, 26, 27, 28, 29, IntPtg.sid, NumberPtg.sid, 32, 33, 34, 35, 36, -9, -9, -9, -9, 37, -9, 38, 39, 40, MemFuncPtg.sid, HSSFErrorConstants.ERROR_NA, 43, 44, 45, 46, 47, TarConstants.LF_NORMAL, TarConstants.LF_LINK, TarConstants.LF_SYMLINK, TarConstants.LF_CHR, TarConstants.LF_BLK, TarConstants.LF_DIR, TarConstants.LF_FIFO, TarConstants.LF_CONTIG, PaletteRecord.STANDARD_PALETTE_SIZE, 57, Ref3DPtg.sid, Area3DPtg.sid, 60, 61, 62, 63, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9, -9};

    private jk() {
    }

    public static String a(byte[] bArr) {
        String strA;
        try {
            strA = a(bArr, bArr.length);
        } catch (IOException e2) {
            if (!a) {
                throw new AssertionError(e2.getMessage());
            }
            strA = null;
        }
        if (a || strA != null) {
            return strA;
        }
        throw new AssertionError();
    }

    private static byte[] b(byte[] bArr, int i) throws IOException {
        int i2;
        Objects.requireNonNull(bArr, "Cannot decode null source array.");
        int i3 = i + 0;
        int i4 = 1;
        if (i3 > bArr.length) {
            throw new IllegalArgumentException(String.format("Source array with length %d cannot have offset of %d and process %d bytes.", Integer.valueOf(bArr.length), 0, Integer.valueOf(i)));
        }
        if (i == 0) {
            return new byte[0];
        }
        if (i < 4) {
            throw new IllegalArgumentException("Base64Util-encoded string must have at least four characters, but length specified was ".concat(String.valueOf(i)));
        }
        byte[] bArr2 = c;
        int i5 = (i * 3) / 4;
        byte[] bArr3 = new byte[i5];
        byte[] bArr4 = new byte[4];
        int i6 = 0;
        int i7 = 0;
        int i8 = 0;
        while (i6 < i3) {
            byte b2 = bArr2[bArr[i6] & 255];
            if (b2 < -5) {
                throw new IOException(String.format("Bad Base64Util input character decimal %d in array position %d", Integer.valueOf(bArr[i6] & 255), Integer.valueOf(i6)));
            }
            if (b2 >= -1) {
                int i9 = i7 + 1;
                bArr4[i7] = bArr[i6];
                if (i9 <= 3) {
                    i7 = i9;
                } else {
                    if (i8 < 0 || (i2 = i8 + 2) >= i5) {
                        throw new IllegalArgumentException(String.format("Destination array with length %d cannot have offset of %d and still store three bytes.", Integer.valueOf(i5), Integer.valueOf(i8)));
                    }
                    byte[] bArr5 = c;
                    if (bArr4[2] == 61) {
                        bArr3[i8] = (byte) ((((bArr5[bArr4[0]] & 255) << 18) | ((bArr5[bArr4[i4]] & 255) << 12)) >>> 16);
                    } else if (bArr4[3] == 61) {
                        int i10 = ((bArr5[bArr4[0]] & 255) << 18) | ((bArr5[bArr4[i4]] & 255) << 12) | ((bArr5[bArr4[2]] & 255) << 6);
                        bArr3[i8] = (byte) (i10 >>> 16);
                        bArr3[i8 + 1] = (byte) (i10 >>> 8);
                        i4 = 2;
                    } else {
                        int i11 = ((bArr5[bArr4[i4]] & 255) << 12) | ((bArr5[bArr4[0]] & 255) << 18) | ((bArr5[bArr4[2]] & 255) << 6) | (bArr5[bArr4[3]] & 255);
                        bArr3[i8] = (byte) (i11 >> 16);
                        bArr3[i8 + 1] = (byte) (i11 >> 8);
                        bArr3[i2] = (byte) i11;
                        i4 = 3;
                    }
                    i8 += i4;
                    if (bArr[i6] == 61) {
                        break;
                    }
                    i7 = 0;
                }
            }
            i6++;
            i4 = 1;
        }
        byte[] bArr6 = new byte[i8];
        System.arraycopy(bArr3, 0, bArr6, 0, i8);
        return bArr6;
    }

    public static byte[] a(String str) throws IOException {
        return b(str);
    }

    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Can't find top splitter block for handler:B:49:0x0052
        	at jadx.core.utils.BlockUtils.getTopSplitterForHandler(BlockUtils.java:1182)
        	at jadx.core.dex.visitors.regions.maker.ExcHandlersRegionMaker.collectHandlerRegions(ExcHandlersRegionMaker.java:53)
        	at jadx.core.dex.visitors.regions.maker.ExcHandlersRegionMaker.process(ExcHandlersRegionMaker.java:38)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:27)
        */
    private static byte[] b(java.lang.String r6) throws java.io.IOException {
        /*
            java.lang.String r0 = "Input string was null."
            java.util.Objects.requireNonNull(r6, r0)
            java.lang.String r0 = "US-ASCII"
            byte[] r6 = r6.getBytes(r0)     // Catch: java.io.UnsupportedEncodingException -> Lc
            goto L10
        Lc:
            byte[] r6 = r6.getBytes()
        L10:
            int r0 = r6.length
            byte[] r6 = b(r6, r0)
            int r0 = r6.length
            r1 = 4
            if (r0 < r1) goto L86
            r0 = 0
            r1 = r6[r0]
            r1 = r1 & 255(0xff, float:3.57E-43)
            r2 = 1
            r2 = r6[r2]
            int r2 = r2 << 8
            r3 = 65280(0xff00, float:9.1477E-41)
            r2 = r2 & r3
            r1 = r1 | r2
            r2 = 35615(0x8b1f, float:4.9907E-41)
            if (r2 != r1) goto L86
            r1 = 2048(0x800, float:2.87E-42)
            byte[] r1 = new byte[r1]
            r2 = 0
            java.io.ByteArrayOutputStream r3 = new java.io.ByteArrayOutputStream     // Catch: java.lang.Throwable -> L6d java.io.IOException -> L71
            r3.<init>()     // Catch: java.lang.Throwable -> L6d java.io.IOException -> L71
            java.io.ByteArrayInputStream r4 = new java.io.ByteArrayInputStream     // Catch: java.lang.Throwable -> L63 java.io.IOException -> L68
            r4.<init>(r6)     // Catch: java.lang.Throwable -> L63 java.io.IOException -> L68
            java.util.zip.GZIPInputStream r5 = new java.util.zip.GZIPInputStream     // Catch: java.lang.Throwable -> L5d java.io.IOException -> L60
            r5.<init>(r4)     // Catch: java.lang.Throwable -> L5d java.io.IOException -> L60
        L41:
            int r2 = r5.read(r1)     // Catch: java.lang.Throwable -> L59 java.io.IOException -> L5b
            if (r2 < 0) goto L4b
            r3.write(r1, r0, r2)     // Catch: java.lang.Throwable -> L59 java.io.IOException -> L5b
            goto L41
        L4b:
            byte[] r6 = r3.toByteArray()     // Catch: java.lang.Throwable -> L59 java.io.IOException -> L5b
            r3.close()     // Catch: java.lang.Exception -> L52
        L52:
            r5.close()     // Catch: java.lang.Exception -> L55
        L55:
            r4.close()     // Catch: java.lang.Exception -> L86
            goto L86
        L59:
            r6 = move-exception
            goto L66
        L5b:
            r0 = move-exception
            goto L6b
        L5d:
            r6 = move-exception
            r5 = r2
            goto L66
        L60:
            r0 = move-exception
            r5 = r2
            goto L6b
        L63:
            r6 = move-exception
            r4 = r2
            r5 = r4
        L66:
            r2 = r3
            goto L7c
        L68:
            r0 = move-exception
            r4 = r2
            r5 = r4
        L6b:
            r2 = r3
            goto L74
        L6d:
            r6 = move-exception
            r4 = r2
            r5 = r4
            goto L7c
        L71:
            r0 = move-exception
            r4 = r2
            r5 = r4
        L74:
            r0.printStackTrace()     // Catch: java.lang.Throwable -> L7b
            r2.close()     // Catch: java.lang.Exception -> L52
            goto L52
        L7b:
            r6 = move-exception
        L7c:
            r2.close()     // Catch: java.lang.Exception -> L7f
        L7f:
            r5.close()     // Catch: java.lang.Exception -> L82
        L82:
            r4.close()     // Catch: java.lang.Exception -> L85
        L85:
            throw r6
        L86:
            return r6
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amap.api.col.p0003sl.jk.b(java.lang.String):byte[]");
    }

    private static byte[] a(byte[] bArr, int i, int i2, byte[] bArr2, int i3) {
        byte[] bArr3 = b;
        int i4 = (i2 > 0 ? (bArr[i] << 24) >>> 8 : 0) | (i2 > 1 ? (bArr[i + 1] << 24) >>> 16 : 0) | (i2 > 2 ? (bArr[i + 2] << 24) >>> 24 : 0);
        if (i2 == 1) {
            bArr2[i3] = bArr3[i4 >>> 18];
            bArr2[i3 + 1] = bArr3[(i4 >>> 12) & 63];
            bArr2[i3 + 2] = 61;
            bArr2[i3 + 3] = 61;
            return bArr2;
        }
        if (i2 == 2) {
            bArr2[i3] = bArr3[i4 >>> 18];
            bArr2[i3 + 1] = bArr3[(i4 >>> 12) & 63];
            bArr2[i3 + 2] = bArr3[(i4 >>> 6) & 63];
            bArr2[i3 + 3] = 61;
            return bArr2;
        }
        if (i2 != 3) {
            return bArr2;
        }
        bArr2[i3] = bArr3[i4 >>> 18];
        bArr2[i3 + 1] = bArr3[(i4 >>> 12) & 63];
        bArr2[i3 + 2] = bArr3[(i4 >>> 6) & 63];
        bArr2[i3 + 3] = bArr3[i4 & 63];
        return bArr2;
    }

    private static String a(byte[] bArr, int i) throws IOException {
        Objects.requireNonNull(bArr, "Cannot serialize a null array.");
        if (i < 0) {
            throw new IllegalArgumentException("Cannot have length offset: ".concat(String.valueOf(i)));
        }
        if (i + 0 > bArr.length) {
            throw new IllegalArgumentException(String.format("Cannot have offset of %d and length of %d with array of length %d", 0, Integer.valueOf(i), Integer.valueOf(bArr.length)));
        }
        int i2 = ((i / 3) * 4) + (i % 3 <= 0 ? 0 : 4);
        byte[] bArr2 = new byte[i2];
        int i3 = i - 2;
        int i4 = 0;
        int i5 = 0;
        while (i4 < i3) {
            a(bArr, i4 + 0, 3, bArr2, i5);
            i4 += 3;
            i5 += 4;
        }
        if (i4 < i) {
            a(bArr, i4 + 0, i - i4, bArr2, i5);
            i5 += 4;
        }
        if (i5 <= i2 - 1) {
            byte[] bArr3 = new byte[i5];
            System.arraycopy(bArr2, 0, bArr3, 0, i5);
            bArr2 = bArr3;
        }
        try {
            return new String(bArr2, MediaPlayer.CHARSET_US_ASCII);
        } catch (UnsupportedEncodingException unused) {
            return new String(bArr2);
        }
    }
}
