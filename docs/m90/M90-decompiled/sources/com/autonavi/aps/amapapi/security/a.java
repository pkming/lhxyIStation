package com.autonavi.aps.amapapi.security;

import android.text.format.DateFormat;
import com.amap.api.col.p0003sl.io;
import com.amap.api.col.p0003sl.it;
import com.amap.api.col.p0003sl.ny;
import com.autonavi.aps.amapapi.utils.b;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.apache.poi.hssf.record.PaletteRecord;
import org.apache.poi.hssf.record.formula.GreaterThanPtg;
import org.apache.poi.hssf.usermodel.HSSFErrorConstants;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: compiled from: Encrypt.java */
/* JADX INFO: loaded from: classes2.dex */
public final class a {
    private static final char[] a = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', DateFormat.AM_PM, 'b', 'c', DateFormat.DATE, 'e', 'f'};
    private static final byte[] b = {61, 61, 81, 65, 65, 69, 119, 65, 67, TarConstants.LF_NORMAL, 74, 80, 115, 116, TarConstants.LF_FIFO, TarConstants.LF_GNUTYPE_LONGLINK, 104, TarConstants.LF_GNUTYPE_LONGNAME, 122, 97, TarConstants.LF_PAX_EXTENDED_HEADER_UC, 99, TarConstants.LF_DIR, 71, TarConstants.LF_LINK, 122, 68, 70, 79, 104, 113, 113, 65, 97, TarConstants.LF_GNUTYPE_LONGNAME, TarConstants.LF_FIFO, 65, 66, 87, TarConstants.LF_DIR, TarConstants.LF_PAX_GLOBAL_EXTENDED_HEADER, 85, 84, 113, 71, 68, 69, TarConstants.LF_GNUTYPE_LONGNAME, 80, 82, 106, TarConstants.LF_CHR, 66, TarConstants.LF_GNUTYPE_LONGLINK, TarConstants.LF_GNUTYPE_LONGLINK, 69, 98, TarConstants.LF_CONTIG, 84, 108, 115, 122, TarConstants.LF_CHR, 106, TarConstants.LF_GNUTYPE_LONGNAME, TarConstants.LF_CONTIG, TarConstants.LF_PAX_EXTENDED_HEADER_UC, 122, 70, 121, 73, TarConstants.LF_GNUTYPE_LONGLINK, TarConstants.LF_BLK, TarConstants.LF_SYMLINK, 43, 101, 70, 121, PaletteRecord.STANDARD_PALETTE_SIZE, 105, 115, 105, 89, TarConstants.LF_PAX_EXTENDED_HEADER_LC, 117, 112, TarConstants.LF_DIR, TarConstants.LF_NORMAL, TarConstants.LF_GNUTYPE_LONGNAME, 81, 70, 86, 108, 110, 73, 65, 66, 74, 65, TarConstants.LF_GNUTYPE_SPARSE, 119, 65, 119, TarConstants.LF_GNUTYPE_SPARSE, 68, 65, 81, 66, 66, 69, 81, 65, 78, 99, 118, 104, 73, 90, 111, TarConstants.LF_GNUTYPE_LONGLINK, 74, 89, 81, 68, 119, 119, 70, 77};
    private static final byte[] c;
    private static final IvParameterSpec d;

    static {
        byte[] bArr = {0, 1, 1, 2, 3, 5, 8, GreaterThanPtg.sid, 8, 7, 6, 5, 4, 3, 2, 1};
        c = bArr;
        d = new IvParameterSpec(bArr);
    }

    public static byte[] a(byte[] bArr) throws Exception {
        PublicKey publicKeyGeneratePublic = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(ny.a(new StringBuilder(new String(b)).reverse().toString().getBytes())));
        Cipher cipher = Cipher.getInstance(it.c("WUlNBL0VDQi9PQUVQV0lUSFNIQS0xQU5ETUdGMVBBRERJTkc"));
        cipher.init(1, publicKeyGeneratePublic);
        return cipher.doFinal(bArr);
    }

    public static byte[] a(byte[] bArr, String str) {
        try {
            SecretKeySpec secretKeySpecB = b(str);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(a());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(1, secretKeySpecB, ivParameterSpec);
            return cipher.doFinal(bArr);
        } catch (Throwable th) {
            b.a(th, "Encrypt", "aesEncrypt");
            return null;
        }
    }

    public static byte[] b(byte[] bArr, String str) {
        try {
            SecretKeySpec secretKeySpecB = b(str);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(a());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(2, secretKeySpecB, ivParameterSpec);
            return cipher.doFinal(bArr);
        } catch (Throwable th) {
            b.a(th, "Encrypt", "aesDecrypt");
            return null;
        }
    }

    private static SecretKeySpec b(String str) {
        byte[] bytes;
        if (str == null) {
            str = "";
        }
        StringBuffer stringBuffer = new StringBuffer(16);
        stringBuffer.append(str);
        while (stringBuffer.length() < 16) {
            stringBuffer.append("0");
        }
        if (stringBuffer.length() > 16) {
            stringBuffer.setLength(16);
        }
        try {
            bytes = stringBuffer.toString().getBytes("UTF-8");
        } catch (Throwable th) {
            b.a(th, "Encrypt", "createKey");
            bytes = null;
        }
        return new SecretKeySpec(bytes, "AES");
    }

    public static String a(String str) {
        if (str != null) {
            try {
                if (str.length() != 0) {
                    return a("MD5", a("SHA1", str) + str);
                }
            } catch (Throwable th) {
                b.a(th, "Encrypt", "generatorKey");
            }
        }
        return null;
    }

    public static String a(String str, String str2) {
        if (str2 == null) {
            return null;
        }
        try {
            return c(io.a(str2.getBytes("UTF-8"), str));
        } catch (Throwable th) {
            b.a(th, "Encrypt", "encode");
            return null;
        }
    }

    private static String c(byte[] bArr) {
        int length = bArr.length;
        StringBuilder sb = new StringBuilder(length * 2);
        for (int i = 0; i < length; i++) {
            char[] cArr = a;
            sb.append(cArr[(bArr[i] >> 4) & 15]);
            sb.append(cArr[bArr[i] & HSSFErrorConstants.ERROR_VALUE]);
        }
        return sb.toString();
    }

    private static byte[] a() {
        return it.c();
    }

    public static byte[] b(byte[] bArr) {
        try {
            byte[] bArr2 = new byte[16];
            byte[] bArr3 = new byte[bArr.length - 16];
            System.arraycopy(bArr, 0, bArr2, 0, 16);
            System.arraycopy(bArr, 16, bArr3, 0, bArr.length - 16);
            SecretKeySpec secretKeySpec = new SecretKeySpec(bArr2, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(2, secretKeySpec, new IvParameterSpec(it.c()));
            return cipher.doFinal(bArr3);
        } catch (Throwable th) {
            b.a(th, "Encrypt", "decryptRsponse length = ".concat(String.valueOf(bArr != null ? bArr.length : 0)));
            return null;
        }
    }
}
