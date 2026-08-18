package android.util;

import android.os.Build;
import android.text.TextUtils;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.HashMap;
import java.util.Map;

/* JADX INFO: loaded from: classes.dex */
public final class CharsetUtils {
    private static final String VENDOR_DOCOMO = "docomo";
    private static final String VENDOR_KDDI = "kddi";
    private static final String VENDOR_SOFTBANK = "softbank";
    private static final Map<String, String> sVendorShiftJisMap;

    static {
        HashMap map = new HashMap();
        sVendorShiftJisMap = map;
        map.put(VENDOR_DOCOMO, "docomo-shift_jis-2007");
        map.put(VENDOR_KDDI, "kddi-shift_jis-2007");
        map.put(VENDOR_SOFTBANK, "softbank-shift_jis-2007");
    }

    private CharsetUtils() {
    }

    public static String nameForVendor(String str, String str2) {
        String str3;
        return (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2) || !isShiftJis(str) || (str3 = sVendorShiftJisMap.get(str2)) == null) ? str : str3;
    }

    public static String nameForDefaultVendor(String str) {
        return nameForVendor(str, getDefaultVendor());
    }

    public static Charset charsetForVendor(String str, String str2) throws IllegalCharsetNameException, UnsupportedCharsetException {
        return Charset.forName(nameForVendor(str, str2));
    }

    public static Charset charsetForVendor(String str) throws IllegalCharsetNameException, UnsupportedCharsetException {
        return charsetForVendor(str, getDefaultVendor());
    }

    private static boolean isShiftJis(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 4 || length == 9) {
            return str.equalsIgnoreCase("shift_jis") || str.equalsIgnoreCase("shift-jis") || str.equalsIgnoreCase("sjis");
        }
        return false;
    }

    private static String getDefaultVendor() {
        return Build.BRAND;
    }
}
