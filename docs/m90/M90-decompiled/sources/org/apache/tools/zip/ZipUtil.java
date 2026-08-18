package org.apache.tools.zip;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.zip.CRC32;
import org.apache.tools.zip.UnsupportedZipFeatureException;

/* JADX INFO: loaded from: classes3.dex */
public abstract class ZipUtil {
    private static final byte[] DOS_TIME_MIN = ZipLong.getBytes(8448);

    public static long adjustToLong(int i) {
        return i < 0 ? ((long) i) + 4294967296L : i;
    }

    public static ZipLong toDosTime(Date date) {
        return new ZipLong(toDosTime(date.getTime()));
    }

    public static byte[] toDosTime(long j) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(j);
        if (calendar.get(1) < 1980) {
            return copy(DOS_TIME_MIN);
        }
        return ZipLong.getBytes((calendar.get(13) >> 1) | ((r5 - 1980) << 25) | ((calendar.get(2) + 1) << 21) | (calendar.get(5) << 16) | (calendar.get(11) << 11) | (calendar.get(12) << 5));
    }

    public static Date fromDosTime(ZipLong zipLong) {
        return new Date(dosToJavaTime(zipLong.getValue()));
    }

    public static long dosToJavaTime(long j) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1, ((int) ((j >> 25) & 127)) + 1980);
        calendar.set(2, ((int) ((j >> 21) & 15)) - 1);
        calendar.set(5, ((int) (j >> 16)) & 31);
        calendar.set(11, ((int) (j >> 11)) & 31);
        calendar.set(12, ((int) (j >> 5)) & 63);
        calendar.set(13, ((int) (j << 1)) & 62);
        calendar.set(14, 0);
        return calendar.getTime().getTime();
    }

    static void setNameAndCommentFromExtraFields(ZipEntry zipEntry, byte[] bArr, byte[] bArr2) {
        String unicodeStringIfOriginalMatches;
        UnicodePathExtraField unicodePathExtraField = (UnicodePathExtraField) zipEntry.getExtraField(UnicodePathExtraField.UPATH_ID);
        String name = zipEntry.getName();
        String unicodeStringIfOriginalMatches2 = getUnicodeStringIfOriginalMatches(unicodePathExtraField, bArr);
        if (unicodeStringIfOriginalMatches2 != null && !name.equals(unicodeStringIfOriginalMatches2)) {
            zipEntry.setName(unicodeStringIfOriginalMatches2);
        }
        if (bArr2 == null || bArr2.length <= 0 || (unicodeStringIfOriginalMatches = getUnicodeStringIfOriginalMatches((UnicodeCommentExtraField) zipEntry.getExtraField(UnicodeCommentExtraField.UCOM_ID), bArr2)) == null) {
            return;
        }
        zipEntry.setComment(unicodeStringIfOriginalMatches);
    }

    private static String getUnicodeStringIfOriginalMatches(AbstractUnicodeExtraField abstractUnicodeExtraField, byte[] bArr) {
        if (abstractUnicodeExtraField != null) {
            CRC32 crc32 = new CRC32();
            crc32.update(bArr);
            if (crc32.getValue() == abstractUnicodeExtraField.getNameCRC32()) {
                try {
                    return ZipEncodingHelper.UTF8_ZIP_ENCODING.decode(abstractUnicodeExtraField.getUnicodeName());
                } catch (IOException unused) {
                }
            }
        }
        return null;
    }

    static byte[] copy(byte[] bArr) {
        if (bArr == null) {
            return null;
        }
        int length = bArr.length;
        byte[] bArr2 = new byte[length];
        System.arraycopy(bArr, 0, bArr2, 0, length);
        return bArr2;
    }

    static boolean canHandleEntryData(ZipEntry zipEntry) {
        return supportsEncryptionOf(zipEntry) && supportsMethodOf(zipEntry);
    }

    private static boolean supportsEncryptionOf(ZipEntry zipEntry) {
        return !zipEntry.getGeneralPurposeBit().usesEncryption();
    }

    private static boolean supportsMethodOf(ZipEntry zipEntry) {
        return zipEntry.getMethod() == 0 || zipEntry.getMethod() == 8;
    }

    static void checkRequestedFeatures(ZipEntry zipEntry) throws UnsupportedZipFeatureException {
        if (!supportsEncryptionOf(zipEntry)) {
            throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.ENCRYPTION, zipEntry);
        }
        if (!supportsMethodOf(zipEntry)) {
            throw new UnsupportedZipFeatureException(UnsupportedZipFeatureException.Feature.METHOD, zipEntry);
        }
    }
}
