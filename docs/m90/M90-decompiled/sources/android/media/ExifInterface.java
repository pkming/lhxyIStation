package android.media;

import android.text.format.Time;
import java.io.IOException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

/* JADX INFO: loaded from: classes.dex */
public class ExifInterface {
    public static final int ORIENTATION_FLIP_HORIZONTAL = 2;
    public static final int ORIENTATION_FLIP_VERTICAL = 4;
    public static final int ORIENTATION_NORMAL = 1;
    public static final int ORIENTATION_ROTATE_180 = 3;
    public static final int ORIENTATION_ROTATE_270 = 8;
    public static final int ORIENTATION_ROTATE_90 = 6;
    public static final int ORIENTATION_TRANSPOSE = 5;
    public static final int ORIENTATION_TRANSVERSE = 7;
    public static final int ORIENTATION_UNDEFINED = 0;
    public static final String TAG_APERTURE = "FNumber";
    public static final String TAG_DATETIME = "DateTime";
    public static final String TAG_EXPOSURE_TIME = "ExposureTime";
    public static final String TAG_FLASH = "Flash";
    public static final String TAG_FOCAL_LENGTH = "FocalLength";
    public static final String TAG_GPS_ALTITUDE = "GPSAltitude";
    public static final String TAG_GPS_ALTITUDE_REF = "GPSAltitudeRef";
    public static final String TAG_GPS_DATESTAMP = "GPSDateStamp";
    public static final String TAG_GPS_LATITUDE = "GPSLatitude";
    public static final String TAG_GPS_LATITUDE_REF = "GPSLatitudeRef";
    public static final String TAG_GPS_LONGITUDE = "GPSLongitude";
    public static final String TAG_GPS_LONGITUDE_REF = "GPSLongitudeRef";
    public static final String TAG_GPS_PROCESSING_METHOD = "GPSProcessingMethod";
    public static final String TAG_GPS_TIMESTAMP = "GPSTimeStamp";
    public static final String TAG_IMAGE_LENGTH = "ImageLength";
    public static final String TAG_IMAGE_WIDTH = "ImageWidth";
    public static final String TAG_ISO = "ISOSpeedRatings";
    public static final String TAG_MAKE = "Make";
    public static final String TAG_MODEL = "Model";
    public static final String TAG_ORIENTATION = "Orientation";
    public static final String TAG_WHITE_BALANCE = "WhiteBalance";
    public static final int WHITEBALANCE_AUTO = 0;
    public static final int WHITEBALANCE_MANUAL = 1;
    private static SimpleDateFormat sFormatter;
    private static final Object sLock;
    private HashMap<String, String> mAttributes;
    private String mFilename;
    private boolean mHasThumbnail;

    private native boolean appendThumbnailNative(String str, String str2);

    private native void commitChangesNative(String str);

    private native String getAttributesNative(String str);

    private native byte[] getThumbnailNative(String str);

    private native long[] getThumbnailRangeNative(String str);

    private native void saveAttributesNative(String str, String str2);

    static {
        System.loadLibrary("exif_jni");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        sFormatter = simpleDateFormat;
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone(Time.TIMEZONE_UTC));
        sLock = new Object();
    }

    public ExifInterface(String str) throws IOException {
        if (str == null) {
            throw new IllegalArgumentException("filename cannot be null");
        }
        this.mFilename = str;
        loadAttributes();
    }

    public String getAttribute(String str) {
        return this.mAttributes.get(str);
    }

    public int getAttributeInt(String str, int i) {
        String str2 = this.mAttributes.get(str);
        if (str2 == null) {
            return i;
        }
        try {
            return Integer.valueOf(str2).intValue();
        } catch (NumberFormatException unused) {
            return i;
        }
    }

    public double getAttributeDouble(String str, double d) {
        String str2 = this.mAttributes.get(str);
        if (str2 == null) {
            return d;
        }
        try {
            int iIndexOf = str2.indexOf("/");
            if (iIndexOf == -1) {
                return d;
            }
            double d2 = Double.parseDouble(str2.substring(iIndexOf + 1));
            return d2 == 0.0d ? d : Double.parseDouble(str2.substring(0, iIndexOf)) / d2;
        } catch (NumberFormatException unused) {
            return d;
        }
    }

    public void setAttribute(String str, String str2) {
        this.mAttributes.put(str, str2);
    }

    private void loadAttributes() throws IOException {
        String attributesNative;
        this.mAttributes = new HashMap<>();
        synchronized (sLock) {
            attributesNative = getAttributesNative(this.mFilename);
        }
        int iIndexOf = attributesNative.indexOf(32);
        int i = 0;
        int i2 = Integer.parseInt(attributesNative.substring(0, iIndexOf));
        int i3 = iIndexOf + 1;
        while (i < i2) {
            int iIndexOf2 = attributesNative.indexOf(61, i3);
            String strSubstring = attributesNative.substring(i3, iIndexOf2);
            int i4 = iIndexOf2 + 1;
            int iIndexOf3 = attributesNative.indexOf(32, i4);
            int i5 = Integer.parseInt(attributesNative.substring(i4, iIndexOf3));
            int i6 = iIndexOf3 + 1;
            int i7 = i5 + i6;
            String strSubstring2 = attributesNative.substring(i6, i7);
            if (strSubstring.equals("hasThumbnail")) {
                this.mHasThumbnail = strSubstring2.equalsIgnoreCase("true");
            } else {
                this.mAttributes.put(strSubstring, strSubstring2);
            }
            i++;
            i3 = i7;
        }
    }

    public void saveAttributes() throws IOException {
        StringBuilder sb = new StringBuilder();
        int size = this.mAttributes.size();
        if (this.mAttributes.containsKey("hasThumbnail")) {
            size--;
        }
        sb.append(size + " ");
        for (Map.Entry<String, String> entry : this.mAttributes.entrySet()) {
            String key = entry.getKey();
            if (!key.equals("hasThumbnail")) {
                String value = entry.getValue();
                sb.append(key + "=");
                sb.append(value.length() + " ");
                sb.append(value);
            }
        }
        String string = sb.toString();
        synchronized (sLock) {
            saveAttributesNative(this.mFilename, string);
            commitChangesNative(this.mFilename);
        }
    }

    public boolean hasThumbnail() {
        return this.mHasThumbnail;
    }

    public byte[] getThumbnail() {
        byte[] thumbnailNative;
        synchronized (sLock) {
            thumbnailNative = getThumbnailNative(this.mFilename);
        }
        return thumbnailNative;
    }

    public long[] getThumbnailRange() {
        long[] thumbnailRangeNative;
        synchronized (sLock) {
            thumbnailRangeNative = getThumbnailRangeNative(this.mFilename);
        }
        return thumbnailRangeNative;
    }

    public boolean getLatLong(float[] fArr) {
        String str = this.mAttributes.get(TAG_GPS_LATITUDE);
        String str2 = this.mAttributes.get(TAG_GPS_LATITUDE_REF);
        String str3 = this.mAttributes.get(TAG_GPS_LONGITUDE);
        String str4 = this.mAttributes.get(TAG_GPS_LONGITUDE_REF);
        if (str != null && str2 != null && str3 != null && str4 != null) {
            try {
                fArr[0] = convertRationalLatLonToFloat(str, str2);
                fArr[1] = convertRationalLatLonToFloat(str3, str4);
                return true;
            } catch (IllegalArgumentException unused) {
            }
        }
        return false;
    }

    public double getAltitude(double d) {
        double attributeDouble = getAttributeDouble(TAG_GPS_ALTITUDE, -1.0d);
        int attributeInt = getAttributeInt(TAG_GPS_ALTITUDE_REF, -1);
        if (attributeDouble < 0.0d || attributeInt < 0) {
            return d;
        }
        return attributeDouble * ((double) (attributeInt != 1 ? 1 : -1));
    }

    public long getDateTime() {
        String str = this.mAttributes.get(TAG_DATETIME);
        if (str == null) {
            return -1L;
        }
        try {
            Date date = sFormatter.parse(str, new ParsePosition(0));
            if (date == null) {
                return -1L;
            }
            return date.getTime();
        } catch (IllegalArgumentException unused) {
            return -1L;
        }
    }

    public long getGpsDateTime() {
        String str;
        String str2 = this.mAttributes.get(TAG_GPS_DATESTAMP);
        String str3 = this.mAttributes.get(TAG_GPS_TIMESTAMP);
        if (str2 == null || str3 == null || (str = str2 + ' ' + str3) == null) {
            return -1L;
        }
        try {
            Date date = sFormatter.parse(str, new ParsePosition(0));
            if (date == null) {
                return -1L;
            }
            return date.getTime();
        } catch (IllegalArgumentException unused) {
            return -1L;
        }
    }

    private static float convertRationalLatLonToFloat(String str, String str2) {
        try {
            String[] strArrSplit = str.split(",");
            String[] strArrSplit2 = strArrSplit[0].split("/");
            double d = Double.parseDouble(strArrSplit2[0].trim()) / Double.parseDouble(strArrSplit2[1].trim());
            String[] strArrSplit3 = strArrSplit[1].split("/");
            double d2 = Double.parseDouble(strArrSplit3[0].trim()) / Double.parseDouble(strArrSplit3[1].trim());
            String[] strArrSplit4 = strArrSplit[2].split("/");
            double d3 = d + (d2 / 60.0d) + ((Double.parseDouble(strArrSplit4[0].trim()) / Double.parseDouble(strArrSplit4[1].trim())) / 3600.0d);
            if (!str2.equals("S")) {
                if (!str2.equals("W")) {
                    return (float) d3;
                }
            }
            return (float) (-d3);
        } catch (ArrayIndexOutOfBoundsException unused) {
            throw new IllegalArgumentException();
        } catch (NumberFormatException unused2) {
            throw new IllegalArgumentException();
        }
    }
}
