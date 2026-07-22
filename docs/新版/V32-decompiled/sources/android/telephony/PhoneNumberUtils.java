package android.telephony;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.CountryDetector;
import android.net.Uri;
import android.os.SystemProperties;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.SparseIntArray;
import com.android.i18n.phonenumbers.NumberParseException;
import com.android.i18n.phonenumbers.PhoneNumberUtil;
import com.android.i18n.phonenumbers.Phonenumber;
import com.android.i18n.phonenumbers.ShortNumberUtil;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.hssf.usermodel.HSSFErrorConstants;
import org.apache.tools.tar.TarConstants;

/* JADX INFO: loaded from: classes.dex */
public class PhoneNumberUtils {
    private static final int CCC_LENGTH;
    private static final String CLIR_OFF = "#31#";
    private static final String CLIR_ON = "*31#";
    private static final boolean[] COUNTRY_CALLING_CALL;
    private static final boolean DBG = false;
    public static final int FORMAT_JAPAN = 2;
    public static final int FORMAT_NANP = 1;
    public static final int FORMAT_UNKNOWN = 0;
    private static final SparseIntArray KEYPAD_MAP;
    static final String LOG_TAG = "PhoneNumberUtils";
    static final int MIN_MATCH = 7;
    private static final String NANP_IDP_STRING = "011";
    private static final int NANP_LENGTH = 10;
    private static final int NANP_STATE_DASH = 4;
    private static final int NANP_STATE_DIGIT = 1;
    private static final int NANP_STATE_ONE = 3;
    private static final int NANP_STATE_PLUS = 2;
    public static final char PAUSE = ',';
    private static final char PLUS_SIGN_CHAR = '+';
    private static final String PLUS_SIGN_STRING = "+";
    public static final int TOA_International = 145;
    public static final int TOA_Unknown = 129;
    public static final char WAIT = ';';
    public static final char WILD = 'N';
    private static final Pattern GLOBAL_PHONE_NUMBER_PATTERN = Pattern.compile("[\\+]?[0-9.-]+");
    private static final String[] NANP_COUNTRIES = {"US", "CA", "AS", "AI", "AG", "BS", "BB", "BM", "VG", "KY", "DM", "DO", "GD", "GU", "JM", "PR", "MS", "MP", "KN", "LC", "VC", "TT", "TC", "VI"};

    private static char bcdToChar(byte b) {
        if (b < 10) {
            return (char) (b + TarConstants.LF_NORMAL);
        }
        switch (b) {
            case 10:
                return '*';
            case 11:
                return '#';
            case 12:
                return PAUSE;
            case 13:
                return WILD;
            default:
                return (char) 0;
        }
    }

    public static final boolean is12Key(char c) {
        return (c >= '0' && c <= '9') || c == '*' || c == '#';
    }

    public static final boolean isDialable(char c) {
        return (c >= '0' && c <= '9') || c == '*' || c == '#' || c == '+' || c == 'N';
    }

    public static boolean isISODigit(char c) {
        return c >= '0' && c <= '9';
    }

    public static final boolean isNonSeparator(char c) {
        return (c >= '0' && c <= '9') || c == '*' || c == '#' || c == '+' || c == 'N' || c == ';' || c == ',';
    }

    private static boolean isPause(char c) {
        return c == 'p' || c == 'P';
    }

    public static final boolean isReallyDialable(char c) {
        return (c >= '0' && c <= '9') || c == '*' || c == '#' || c == '+';
    }

    public static final boolean isStartsPostDial(char c) {
        return c == ',' || c == ';';
    }

    private static boolean isToneWait(char c) {
        return c == 'w' || c == 'W';
    }

    private static boolean isTwoToNine(char c) {
        return c >= '2' && c <= '9';
    }

    private static int minPositive(int i, int i2) {
        if (i >= 0 && i2 >= 0) {
            return i < i2 ? i : i2;
        }
        if (i >= 0) {
            return i;
        }
        if (i2 >= 0) {
            return i2;
        }
        return -1;
    }

    private static int tryGetISODigit(char c) {
        if ('0' > c || c > '9') {
            return -1;
        }
        return c - '0';
    }

    static {
        SparseIntArray sparseIntArray = new SparseIntArray();
        KEYPAD_MAP = sparseIntArray;
        sparseIntArray.put(97, 50);
        sparseIntArray.put(98, 50);
        sparseIntArray.put(99, 50);
        sparseIntArray.put(65, 50);
        sparseIntArray.put(66, 50);
        sparseIntArray.put(67, 50);
        sparseIntArray.put(100, 51);
        sparseIntArray.put(101, 51);
        sparseIntArray.put(102, 51);
        sparseIntArray.put(68, 51);
        sparseIntArray.put(69, 51);
        sparseIntArray.put(70, 51);
        sparseIntArray.put(103, 52);
        sparseIntArray.put(104, 52);
        sparseIntArray.put(105, 52);
        sparseIntArray.put(71, 52);
        sparseIntArray.put(72, 52);
        sparseIntArray.put(73, 52);
        sparseIntArray.put(106, 53);
        sparseIntArray.put(107, 53);
        sparseIntArray.put(108, 53);
        sparseIntArray.put(74, 53);
        sparseIntArray.put(75, 53);
        sparseIntArray.put(76, 53);
        sparseIntArray.put(109, 54);
        sparseIntArray.put(110, 54);
        sparseIntArray.put(111, 54);
        sparseIntArray.put(77, 54);
        sparseIntArray.put(78, 54);
        sparseIntArray.put(79, 54);
        sparseIntArray.put(112, 55);
        sparseIntArray.put(113, 55);
        sparseIntArray.put(114, 55);
        sparseIntArray.put(115, 55);
        sparseIntArray.put(80, 55);
        sparseIntArray.put(81, 55);
        sparseIntArray.put(82, 55);
        sparseIntArray.put(83, 55);
        sparseIntArray.put(116, 56);
        sparseIntArray.put(117, 56);
        sparseIntArray.put(118, 56);
        sparseIntArray.put(84, 56);
        sparseIntArray.put(85, 56);
        sparseIntArray.put(86, 56);
        sparseIntArray.put(119, 57);
        sparseIntArray.put(120, 57);
        sparseIntArray.put(121, 57);
        sparseIntArray.put(122, 57);
        sparseIntArray.put(87, 57);
        sparseIntArray.put(88, 57);
        sparseIntArray.put(89, 57);
        sparseIntArray.put(90, 57);
        boolean[] zArr = {true, true, false, false, false, false, false, true, false, false, false, false, false, false, false, false, false, false, false, false, true, false, false, false, false, false, false, true, true, false, true, true, true, true, true, false, true, false, false, true, true, false, false, true, true, true, true, true, true, true, false, true, true, true, true, true, true, true, true, false, true, true, true, true, true, true, true, false, false, false, false, false, false, false, false, false, false, false, false, false, false, true, true, true, true, false, true, false, false, true, true, true, true, true, true, true, false, false, true, false};
        COUNTRY_CALLING_CALL = zArr;
        CCC_LENGTH = zArr.length;
    }

    private static boolean isSeparator(char c) {
        return !isDialable(c) && ('a' > c || c > 'z') && ('A' > c || c > 'Z');
    }

    public static String getNumberFromIntent(Intent intent, Context context) {
        String str;
        Uri data = intent.getData();
        if (data == null) {
            return null;
        }
        String scheme = data.getScheme();
        if (scheme.equals("tel") || scheme.equals("sip")) {
            return data.getSchemeSpecificPart();
        }
        if (scheme.equals("voicemail")) {
            return TelephonyManager.getDefault().getCompleteVoiceMailNumber();
        }
        if (context == null) {
            return null;
        }
        intent.resolveType(context);
        String authority = data.getAuthority();
        if (Contacts.AUTHORITY.equals(authority)) {
            str = "number";
        } else {
            str = ContactsContract.AUTHORITY.equals(authority) ? "data1" : null;
        }
        Cursor cursorQuery = context.getContentResolver().query(data, new String[]{str}, null, null, null);
        if (cursorQuery != null) {
            try {
                string = cursorQuery.moveToFirst() ? cursorQuery.getString(cursorQuery.getColumnIndex(str)) : null;
            } finally {
                cursorQuery.close();
            }
        }
        return string;
    }

    public static String extractNetworkPortion(String str) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            int iDigit = Character.digit(cCharAt, 10);
            if (iDigit == -1) {
                if (cCharAt == '+') {
                    String string = sb.toString();
                    if (string.length() == 0 || string.equals(CLIR_ON) || string.equals(CLIR_OFF)) {
                        sb.append(cCharAt);
                    }
                } else if (isDialable(cCharAt)) {
                    sb.append(cCharAt);
                } else if (isStartsPostDial(cCharAt)) {
                    break;
                }
            } else {
                sb.append(iDigit);
            }
        }
        return sb.toString();
    }

    public static String extractNetworkPortionAlt(String str) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        StringBuilder sb = new StringBuilder(length);
        boolean z = false;
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            if (cCharAt == '+') {
                if (z) {
                    continue;
                } else {
                    z = true;
                }
            }
            if (isDialable(cCharAt)) {
                sb.append(cCharAt);
            } else if (isStartsPostDial(cCharAt)) {
                break;
            }
        }
        return sb.toString();
    }

    public static String stripSeparators(String str) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            int iDigit = Character.digit(cCharAt, 10);
            if (iDigit != -1) {
                sb.append(iDigit);
            } else if (isNonSeparator(cCharAt)) {
                sb.append(cCharAt);
            }
        }
        return sb.toString();
    }

    public static String convertAndStrip(String str) {
        return stripSeparators(convertKeypadLettersToDigits(str));
    }

    public static String convertPreDial(String str) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            if (isPause(cCharAt)) {
                cCharAt = PAUSE;
            } else if (isToneWait(cCharAt)) {
                cCharAt = WAIT;
            }
            sb.append(cCharAt);
        }
        return sb.toString();
    }

    private static void log(String str) {
        Rlog.d(LOG_TAG, str);
    }

    private static int indexOfLastNetworkChar(String str) {
        int length = str.length();
        int iMinPositive = minPositive(str.indexOf(44), str.indexOf(59));
        return iMinPositive < 0 ? length - 1 : iMinPositive - 1;
    }

    public static String extractPostDialPortion(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int length = str.length();
        for (int iIndexOfLastNetworkChar = indexOfLastNetworkChar(str) + 1; iIndexOfLastNetworkChar < length; iIndexOfLastNetworkChar++) {
            char cCharAt = str.charAt(iIndexOfLastNetworkChar);
            if (isNonSeparator(cCharAt)) {
                sb.append(cCharAt);
            }
        }
        return sb.toString();
    }

    public static boolean compare(String str, String str2) {
        return compare(str, str2, false);
    }

    public static boolean compare(Context context, String str, String str2) {
        return compare(str, str2, context.getResources().getBoolean(17891366));
    }

    public static boolean compare(String str, String str2, boolean z) {
        return z ? compareStrictly(str, str2) : compareLoosely(str, str2);
    }

    public static boolean compareLoosely(String str, String str2) {
        boolean z;
        if (str == null || str2 == null) {
            return str == str2;
        }
        if (str.length() != 0 && str2.length() != 0) {
            int iIndexOfLastNetworkChar = indexOfLastNetworkChar(str);
            int iIndexOfLastNetworkChar2 = indexOfLastNetworkChar(str2);
            int i = 0;
            int i2 = 0;
            int i3 = 0;
            while (iIndexOfLastNetworkChar >= 0 && iIndexOfLastNetworkChar2 >= 0) {
                char cCharAt = str.charAt(iIndexOfLastNetworkChar);
                if (isDialable(cCharAt)) {
                    z = false;
                } else {
                    iIndexOfLastNetworkChar--;
                    i++;
                    z = true;
                }
                char cCharAt2 = str2.charAt(iIndexOfLastNetworkChar2);
                if (!isDialable(cCharAt2)) {
                    iIndexOfLastNetworkChar2--;
                    i2++;
                    z = true;
                }
                if (!z) {
                    if (cCharAt2 != cCharAt && cCharAt != 'N' && cCharAt2 != 'N') {
                        break;
                    }
                    iIndexOfLastNetworkChar--;
                    iIndexOfLastNetworkChar2--;
                    i3++;
                }
            }
            if (i3 < 7) {
                int length = str.length() - i;
                return length == str2.length() - i2 && length == i3;
            }
            if (i3 >= 7 && (iIndexOfLastNetworkChar < 0 || iIndexOfLastNetworkChar2 < 0)) {
                return true;
            }
            int i4 = iIndexOfLastNetworkChar + 1;
            if (matchIntlPrefix(str, i4) && matchIntlPrefix(str2, iIndexOfLastNetworkChar2 + 1)) {
                return true;
            }
            if (matchTrunkPrefix(str, i4) && matchIntlPrefixAndCC(str2, iIndexOfLastNetworkChar2 + 1)) {
                return true;
            }
            if (matchTrunkPrefix(str2, iIndexOfLastNetworkChar2 + 1) && matchIntlPrefixAndCC(str, i4)) {
                return true;
            }
        }
        return false;
    }

    public static boolean compareStrictly(String str, String str2) {
        return compareStrictly(str, str2, true);
    }

    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Not found exit edge by exit block: B:36:0x006f
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.checkLoopExits(LoopRegionMaker.java:226)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.makeLoopRegion(LoopRegionMaker.java:196)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:63)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:89)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.IfRegionMaker.process(IfRegionMaker.java:96)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:106)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.IfRegionMaker.process(IfRegionMaker.java:96)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:106)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.IfRegionMaker.process(IfRegionMaker.java:96)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:106)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.IfRegionMaker.process(IfRegionMaker.java:102)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:106)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:66)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeMthRegion(RegionMaker.java:48)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:25)
        */
    public static boolean compareStrictly(java.lang.String r17, java.lang.String r18, boolean r19) {
        /*
            Method dump skipped, instruction units count: 251
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.telephony.PhoneNumberUtils.compareStrictly(java.lang.String, java.lang.String, boolean):boolean");
    }

    public static String toCallerIDMinMatch(String str) {
        return internalGetStrippedReversed(extractNetworkPortionAlt(str), 7);
    }

    public static String getStrippedReversed(String str) {
        String strExtractNetworkPortionAlt = extractNetworkPortionAlt(str);
        if (strExtractNetworkPortionAlt == null) {
            return null;
        }
        return internalGetStrippedReversed(strExtractNetworkPortionAlt, strExtractNetworkPortionAlt.length());
    }

    private static String internalGetStrippedReversed(String str, int i) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder(i);
        int length = str.length();
        for (int i2 = length - 1; i2 >= 0 && length - i2 <= i; i2--) {
            sb.append(str.charAt(i2));
        }
        return sb.toString();
    }

    public static String stringFromStringAndTOA(String str, int i) {
        if (str == null) {
            return null;
        }
        return (i != 145 || str.length() <= 0 || str.charAt(0) == '+') ? str : PLUS_SIGN_STRING + str;
    }

    public static int toaFromString(String str) {
        return (str == null || str.length() <= 0 || str.charAt(0) != '+') ? 129 : 145;
    }

    public static String calledPartyBCDToString(byte[] bArr, int i, int i2) {
        StringBuilder sb;
        StringBuilder sb2 = new StringBuilder((i2 * 2) + 1);
        if (i2 < 2) {
            return "";
        }
        boolean z = (bArr[i] & 240) == 144;
        internalCalledPartyBCDFragmentToString(sb2, bArr, i + 1, i2 - 1);
        if (z && sb2.length() == 0) {
            return "";
        }
        if (z) {
            String string = sb2.toString();
            Matcher matcher = Pattern.compile("(^[#*])(.*)([#*])(.*)(#)$").matcher(string);
            if (matcher.matches()) {
                if ("".equals(matcher.group(2))) {
                    sb = new StringBuilder();
                    sb.append(matcher.group(1));
                    sb.append(matcher.group(3));
                    sb.append(matcher.group(4));
                    sb.append(matcher.group(5));
                    sb.append(PLUS_SIGN_STRING);
                } else {
                    sb = new StringBuilder();
                    sb.append(matcher.group(1));
                    sb.append(matcher.group(2));
                    sb.append(matcher.group(3));
                    sb.append(PLUS_SIGN_STRING);
                    sb.append(matcher.group(4));
                    sb.append(matcher.group(5));
                }
            } else {
                Matcher matcher2 = Pattern.compile("(^[#*])(.*)([#*])(.*)").matcher(string);
                if (matcher2.matches()) {
                    sb = new StringBuilder();
                    sb.append(matcher2.group(1));
                    sb.append(matcher2.group(2));
                    sb.append(matcher2.group(3));
                    sb.append(PLUS_SIGN_STRING);
                    sb.append(matcher2.group(4));
                } else {
                    sb2 = new StringBuilder();
                    sb2.append(PLUS_SIGN_CHAR);
                    sb2.append(string);
                }
            }
            sb2 = sb;
        }
        return sb2.toString();
    }

    private static void internalCalledPartyBCDFragmentToString(StringBuilder sb, byte[] bArr, int i, int i2) {
        char cBcdToChar;
        char cBcdToChar2;
        int i3 = i;
        while (true) {
            int i4 = i2 + i;
            if (i3 >= i4 || (cBcdToChar = bcdToChar((byte) (bArr[i3] & HSSFErrorConstants.ERROR_VALUE))) == 0) {
                return;
            }
            sb.append(cBcdToChar);
            byte b = (byte) ((bArr[i3] >> 4) & 15);
            if ((b == 15 && i3 + 1 == i4) || (cBcdToChar2 = bcdToChar(b)) == 0) {
                return;
            }
            sb.append(cBcdToChar2);
            i3++;
        }
    }

    public static String calledPartyBCDFragmentToString(byte[] bArr, int i, int i2) {
        StringBuilder sb = new StringBuilder(i2 * 2);
        internalCalledPartyBCDFragmentToString(sb, bArr, i, i2);
        return sb.toString();
    }

    private static int charToBCD(char c) {
        if (c >= '0' && c <= '9') {
            return c - '0';
        }
        if (c == '*') {
            return 10;
        }
        if (c == '#') {
            return 11;
        }
        if (c == ',') {
            return 12;
        }
        if (c == 'N') {
            return 13;
        }
        throw new RuntimeException("invalid char for BCD " + c);
    }

    public static boolean isWellFormedSmsAddress(String str) {
        String strExtractNetworkPortion = extractNetworkPortion(str);
        return (strExtractNetworkPortion.equals(PLUS_SIGN_STRING) || TextUtils.isEmpty(strExtractNetworkPortion) || !isDialable(strExtractNetworkPortion)) ? false : true;
    }

    public static boolean isGlobalPhoneNumber(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        return GLOBAL_PHONE_NUMBER_PATTERN.matcher(str).matches();
    }

    private static boolean isDialable(String str) {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            if (!isDialable(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    private static boolean isNonSeparator(String str) {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            if (!isNonSeparator(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static byte[] networkPortionToCalledPartyBCD(String str) {
        return numberToCalledPartyBCDHelper(extractNetworkPortion(str), false);
    }

    public static byte[] networkPortionToCalledPartyBCDWithLength(String str) {
        return numberToCalledPartyBCDHelper(extractNetworkPortion(str), true);
    }

    public static byte[] numberToCalledPartyBCD(String str) {
        return numberToCalledPartyBCDHelper(str, false);
    }

    private static byte[] numberToCalledPartyBCDHelper(String str, boolean z) {
        int length = str.length();
        char c = 0;
        boolean z2 = str.indexOf(43) != -1;
        int i = z2 ? length - 1 : length;
        if (i == 0) {
            return null;
        }
        int i2 = (i + 1) / 2;
        int i3 = z ? 2 : 1;
        int i4 = i2 + i3;
        byte[] bArr = new byte[i4];
        int i5 = 0;
        for (int i6 = 0; i6 < length; i6++) {
            char cCharAt = str.charAt(i6);
            if (cCharAt != '+') {
                int i7 = (i5 >> 1) + i3;
                bArr[i7] = (byte) (((byte) ((charToBCD(cCharAt) & 15) << ((i5 & 1) == 1 ? 4 : 0))) | bArr[i7]);
                i5++;
            }
        }
        if ((i5 & 1) == 1) {
            int i8 = i3 + (i5 >> 1);
            bArr[i8] = (byte) (bArr[i8] | 240);
        }
        if (z) {
            bArr[0] = (byte) (i4 - 1);
            c = 1;
        }
        bArr[c] = (byte) (z2 ? 145 : 129);
        return bArr;
    }

    public static String formatNumber(String str) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
        formatNumber(spannableStringBuilder, getFormatTypeForLocale(Locale.getDefault()));
        return spannableStringBuilder.toString();
    }

    public static String formatNumber(String str, int i) {
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(str);
        formatNumber(spannableStringBuilder, i);
        return spannableStringBuilder.toString();
    }

    public static int getFormatTypeForLocale(Locale locale) {
        return getFormatTypeFromCountryCode(locale.getCountry());
    }

    public static void formatNumber(Editable editable, int i) {
        if (editable.length() > 2 && editable.charAt(0) == '+') {
            if (editable.charAt(1) == '1') {
                i = 1;
            } else {
                i = (editable.length() >= 3 && editable.charAt(1) == '8' && editable.charAt(2) == '1') ? 2 : 0;
            }
        }
        if (i == 0) {
            removeDashes(editable);
        } else if (i == 1) {
            formatNanpNumber(editable);
        } else {
            if (i != 2) {
                return;
            }
            formatJapaneseNumber(editable);
        }
    }

    public static void formatNanpNumber(Editable editable) {
        int i;
        int length = editable.length();
        if (length <= 15 && length > 5) {
            CharSequence charSequenceSubSequence = editable.subSequence(0, length);
            removeDashes(editable);
            int length2 = editable.length();
            int[] iArr = new int[3];
            int i2 = 0;
            int i3 = 0;
            char c = 1;
            for (int i4 = 0; i4 < length2; i4++) {
                char cCharAt = editable.charAt(i4);
                if (cCharAt != '+') {
                    if (cCharAt != '-') {
                        switch (cCharAt) {
                            case '1':
                                if (i2 == 0 || c == 2) {
                                    c = 3;
                                    break;
                                }
                            case '0':
                            case '2':
                            case '3':
                            case '4':
                            case '5':
                            case '6':
                            case '7':
                            case '8':
                            case '9':
                                if (c == 2) {
                                    editable.replace(0, length2, charSequenceSubSequence);
                                } else {
                                    if (c != 3) {
                                        if (c != 4 && (i2 == 3 || i2 == 6)) {
                                            i = i3 + 1;
                                            iArr[i3] = i4;
                                        }
                                        i2++;
                                        c = 1;
                                    } else {
                                        i = i3 + 1;
                                        iArr[i3] = i4;
                                    }
                                    i3 = i;
                                    i2++;
                                    c = 1;
                                }
                                break;
                            default:
                                editable.replace(0, length2, charSequenceSubSequence);
                                break;
                        }
                        return;
                    }
                    c = 4;
                } else {
                    if (i4 != 0) {
                        editable.replace(0, length2, charSequenceSubSequence);
                        return;
                    }
                    c = 2;
                }
            }
            if (i2 == 7) {
                i3--;
            }
            for (int i5 = 0; i5 < i3; i5++) {
                int i6 = iArr[i5] + i5;
                editable.replace(i6, i6, "-");
            }
            for (int length3 = editable.length(); length3 > 0; length3--) {
                int i7 = length3 - 1;
                if (editable.charAt(i7) != '-') {
                    return;
                }
                editable.delete(i7, length3);
            }
        }
    }

    public static void formatJapaneseNumber(Editable editable) {
        JapanesePhoneNumberFormatter.format(editable);
    }

    private static void removeDashes(Editable editable) {
        int i = 0;
        while (i < editable.length()) {
            if (editable.charAt(i) == '-') {
                editable.delete(i, i + 1);
            } else {
                i++;
            }
        }
    }

    public static String formatNumberToE164(String str, String str2) {
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            Phonenumber.PhoneNumber phoneNumber = phoneNumberUtil.parse(str, str2);
            if (phoneNumberUtil.isValidNumber(phoneNumber)) {
                return phoneNumberUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
            }
            return null;
        } catch (NumberParseException unused) {
            return null;
        }
    }

    public static String formatNumber(String str, String str2) {
        if (str.startsWith("#") || str.startsWith("*")) {
            return str;
        }
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            return phoneNumberUtil.formatInOriginalFormat(phoneNumberUtil.parseAndKeepRawInput(str, str2), str2);
        } catch (NumberParseException unused) {
            return null;
        }
    }

    public static String formatNumber(String str, String str2, String str3) {
        int length = str.length();
        for (int i = 0; i < length; i++) {
            if (!isDialable(str.charAt(i))) {
                return str;
            }
        }
        PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        if (str2 != null && str2.length() >= 2 && str2.charAt(0) == '+') {
            try {
                String regionCodeForNumber = phoneNumberUtil.getRegionCodeForNumber(phoneNumberUtil.parse(str2, "ZZ"));
                if (!TextUtils.isEmpty(regionCodeForNumber)) {
                    if (normalizeNumber(str).indexOf(str2.substring(1)) <= 0) {
                        str3 = regionCodeForNumber;
                    }
                }
            } catch (NumberParseException unused) {
            }
        }
        String number = formatNumber(str, str3);
        return number != null ? number : str;
    }

    public static String normalizeNumber(String str) {
        StringBuilder sb = new StringBuilder();
        int length = str.length();
        for (int i = 0; i < length; i++) {
            char cCharAt = str.charAt(i);
            int iDigit = Character.digit(cCharAt, 10);
            if (iDigit != -1) {
                sb.append(iDigit);
            } else if (i == 0 && cCharAt == '+') {
                sb.append(cCharAt);
            } else if ((cCharAt >= 'a' && cCharAt <= 'z') || (cCharAt >= 'A' && cCharAt <= 'Z')) {
                return normalizeNumber(convertKeypadLettersToDigits(str));
            }
        }
        return sb.toString();
    }

    public static String replaceUnicodeDigits(String str) {
        StringBuilder sb = new StringBuilder(str.length());
        for (char c : str.toCharArray()) {
            int iDigit = Character.digit(c, 10);
            if (iDigit != -1) {
                sb.append(iDigit);
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public static boolean isEmergencyNumber(String str) {
        return isEmergencyNumberInternal(str, true);
    }

    public static boolean isPotentialEmergencyNumber(String str) {
        return isEmergencyNumberInternal(str, false);
    }

    private static boolean isEmergencyNumberInternal(String str, boolean z) {
        return isEmergencyNumberInternal(str, null, z);
    }

    public static boolean isEmergencyNumber(String str, String str2) {
        return isEmergencyNumberInternal(str, str2, true);
    }

    public static boolean isPotentialEmergencyNumber(String str, String str2) {
        return isEmergencyNumberInternal(str, str2, false);
    }

    private static boolean isEmergencyNumberInternal(String str, String str2, boolean z) {
        if (str == null || isUriNumber(str)) {
            return false;
        }
        String strExtractNetworkPortionAlt = extractNetworkPortionAlt(str);
        String str3 = SystemProperties.get("ril.ecclist");
        if (TextUtils.isEmpty(str3)) {
            str3 = SystemProperties.get("ro.ril.ecclist");
        }
        if (!TextUtils.isEmpty(str3)) {
            for (String str4 : str3.split(",")) {
                if (z || "BR".equalsIgnoreCase(str2)) {
                    if (strExtractNetworkPortionAlt.equals(str4)) {
                        return true;
                    }
                } else if (strExtractNetworkPortionAlt.startsWith(str4)) {
                    return true;
                }
            }
            return false;
        }
        Rlog.d(LOG_TAG, "System property doesn't provide any emergency numbers. Use embedded logic for determining ones.");
        if (str2 == null) {
            return z ? strExtractNetworkPortionAlt.equals("112") || strExtractNetworkPortionAlt.equals("911") : strExtractNetworkPortionAlt.startsWith("112") || strExtractNetworkPortionAlt.startsWith("911");
        }
        ShortNumberUtil shortNumberUtil = new ShortNumberUtil();
        if (z) {
            return shortNumberUtil.isEmergencyNumber(strExtractNetworkPortionAlt, str2);
        }
        return shortNumberUtil.connectsToEmergencyNumber(strExtractNetworkPortionAlt, str2);
    }

    public static boolean isLocalEmergencyNumber(String str, Context context) {
        return isLocalEmergencyNumberInternal(str, context, true);
    }

    public static boolean isPotentialLocalEmergencyNumber(String str, Context context) {
        return isLocalEmergencyNumberInternal(str, context, false);
    }

    private static boolean isLocalEmergencyNumberInternal(String str, Context context, boolean z) {
        String country;
        CountryDetector countryDetector = (CountryDetector) context.getSystemService(Context.COUNTRY_DETECTOR);
        if (countryDetector != null && countryDetector.detectCountry() != null) {
            country = countryDetector.detectCountry().getCountryIso();
        } else {
            country = context.getResources().getConfiguration().locale.getCountry();
            Rlog.w(LOG_TAG, "No CountryDetector; falling back to countryIso based on locale: " + country);
        }
        return isEmergencyNumberInternal(str, country, z);
    }

    public static boolean isVoiceMailNumber(String str) {
        try {
            String voiceMailNumber = TelephonyManager.getDefault().getVoiceMailNumber();
            String strExtractNetworkPortionAlt = extractNetworkPortionAlt(str);
            return !TextUtils.isEmpty(strExtractNetworkPortionAlt) && compare(strExtractNetworkPortionAlt, voiceMailNumber);
        } catch (SecurityException unused) {
            return false;
        }
    }

    public static String convertKeypadLettersToDigits(String str) {
        int length;
        if (str == null || (length = str.length()) == 0) {
            return str;
        }
        char[] charArray = str.toCharArray();
        for (int i = 0; i < length; i++) {
            char c = charArray[i];
            charArray[i] = (char) KEYPAD_MAP.get(c, c);
        }
        return new String(charArray);
    }

    public static String cdmaCheckAndProcessPlusCode(String str) {
        if (TextUtils.isEmpty(str) || !isReallyDialable(str.charAt(0)) || !isNonSeparator(str)) {
            return str;
        }
        String str2 = SystemProperties.get("gsm.operator.iso-country", "");
        String str3 = SystemProperties.get("gsm.sim.operator.iso-country", "");
        return (TextUtils.isEmpty(str2) || TextUtils.isEmpty(str3)) ? str : cdmaCheckAndProcessPlusCodeByNumberFormat(str, getFormatTypeFromCountryCode(str2), getFormatTypeFromCountryCode(str3));
    }

    public static String cdmaCheckAndProcessPlusCodeForSms(String str) {
        if (TextUtils.isEmpty(str) || !isReallyDialable(str.charAt(0)) || !isNonSeparator(str)) {
            return str;
        }
        String str2 = SystemProperties.get("gsm.sim.operator.iso-country", "");
        if (TextUtils.isEmpty(str2)) {
            return str;
        }
        int formatTypeFromCountryCode = getFormatTypeFromCountryCode(str2);
        return cdmaCheckAndProcessPlusCodeByNumberFormat(str, formatTypeFromCountryCode, formatTypeFromCountryCode);
    }

    public static String cdmaCheckAndProcessPlusCodeByNumberFormat(String str, int i, int i2) {
        String strProcessPlusCodeWithinNanp;
        if (str == null || str.lastIndexOf(PLUS_SIGN_STRING) == -1) {
            return str;
        }
        if (i != i2 || i != 1) {
            Rlog.e("checkAndProcessPlusCode:non-NANP not supported", str);
            return str;
        }
        String str2 = null;
        String strSubstring = str;
        while (true) {
            strProcessPlusCodeWithinNanp = processPlusCodeWithinNanp(extractNetworkPortion(strSubstring));
            if (TextUtils.isEmpty(strProcessPlusCodeWithinNanp)) {
                Rlog.e("checkAndProcessPlusCode: null newDialStr", strProcessPlusCodeWithinNanp);
                return str;
            }
            if (str2 != null) {
                strProcessPlusCodeWithinNanp = str2.concat(strProcessPlusCodeWithinNanp);
            }
            String strExtractPostDialPortion = extractPostDialPortion(strSubstring);
            if (!TextUtils.isEmpty(strExtractPostDialPortion)) {
                int iFindDialableIndexFromPostDialStr = findDialableIndexFromPostDialStr(strExtractPostDialPortion);
                if (iFindDialableIndexFromPostDialStr >= 1) {
                    strProcessPlusCodeWithinNanp = appendPwCharBackToOrigDialStr(iFindDialableIndexFromPostDialStr, strProcessPlusCodeWithinNanp, strExtractPostDialPortion);
                    strSubstring = strExtractPostDialPortion.substring(iFindDialableIndexFromPostDialStr);
                } else {
                    if (iFindDialableIndexFromPostDialStr < 0) {
                        strExtractPostDialPortion = "";
                    }
                    Rlog.e("wrong postDialStr=", strExtractPostDialPortion);
                }
            }
            if (TextUtils.isEmpty(strExtractPostDialPortion) || TextUtils.isEmpty(strSubstring)) {
                break;
            }
            str2 = strProcessPlusCodeWithinNanp;
        }
        return strProcessPlusCodeWithinNanp;
    }

    private static String getDefaultIdp() {
        SystemProperties.get("ro.cdma.idpstring", null);
        if (TextUtils.isEmpty(null)) {
            return NANP_IDP_STRING;
        }
        return null;
    }

    private static int getFormatTypeFromCountryCode(String str) {
        int length = NANP_COUNTRIES.length;
        for (int i = 0; i < length; i++) {
            if (NANP_COUNTRIES[i].compareToIgnoreCase(str) == 0) {
                return 1;
            }
        }
        return "jp".compareToIgnoreCase(str) == 0 ? 2 : 0;
    }

    private static boolean isNanp(String str) {
        if (str != null) {
            if (str.length() != 10 || !isTwoToNine(str.charAt(0)) || !isTwoToNine(str.charAt(3))) {
                return false;
            }
            for (int i = 1; i < 10; i++) {
                if (!isISODigit(str.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        Rlog.e("isNanp: null dialStr passed in", str);
        return false;
    }

    private static boolean isOneNanp(String str) {
        if (str != null) {
            return str.charAt(0) == '1' && isNanp(str.substring(1));
        }
        Rlog.e("isOneNanp: null dialStr passed in", str);
        return false;
    }

    public static boolean isUriNumber(String str) {
        return str != null && (str.contains("@") || str.contains("%40"));
    }

    public static String getUsernameFromUriNumber(String str) {
        int iIndexOf = str.indexOf(64);
        if (iIndexOf < 0) {
            iIndexOf = str.indexOf("%40");
        }
        if (iIndexOf < 0) {
            Rlog.w(LOG_TAG, "getUsernameFromUriNumber: no delimiter found in SIP addr '" + str + "'");
            iIndexOf = str.length();
        }
        return str.substring(0, iIndexOf);
    }

    private static String processPlusCodeWithinNanp(String str) {
        if (str == null || str.charAt(0) != '+' || str.length() <= 1) {
            return str;
        }
        String strSubstring = str.substring(1);
        return isOneNanp(strSubstring) ? strSubstring : str.replaceFirst("[+]", getDefaultIdp());
    }

    private static int findDialableIndexFromPostDialStr(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (isReallyDialable(str.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    private static String appendPwCharBackToOrigDialStr(int i, String str, String str2) {
        if (i == 1) {
            return str + str2.charAt(0);
        }
        return str.concat(str2.substring(0, i));
    }

    private static boolean matchIntlPrefix(String str, int i) {
        char c = 0;
        for (int i2 = 0; i2 < i; i2++) {
            char cCharAt = str.charAt(i2);
            if (c != 0) {
                if (c != 2) {
                    if (c != 4) {
                        if (isNonSeparator(cCharAt)) {
                            return false;
                        }
                    } else if (cCharAt == '1') {
                        c = 5;
                    } else if (isNonSeparator(cCharAt)) {
                        return false;
                    }
                } else if (cCharAt == '0') {
                    c = 3;
                } else if (cCharAt == '1') {
                    c = 4;
                } else if (isNonSeparator(cCharAt)) {
                    return false;
                }
            } else if (cCharAt == '+') {
                c = 1;
            } else if (cCharAt == '0') {
                c = 2;
            } else if (isNonSeparator(cCharAt)) {
                return false;
            }
        }
        return c == 1 || c == 3 || c == 5;
    }

    private static boolean matchIntlPrefixAndCC(String str, int i) {
        int i2 = 0;
        for (int i3 = 0; i3 < i; i3++) {
            char cCharAt = str.charAt(i3);
            switch (i2) {
                case 0:
                    if (cCharAt == '+') {
                        i2 = 1;
                    } else if (cCharAt == '0') {
                        i2 = 2;
                    } else {
                        if (isNonSeparator(cCharAt)) {
                            return false;
                        }
                    }
                    break;
                case 1:
                case 3:
                case 5:
                    if (isISODigit(cCharAt)) {
                        i2 = 6;
                    } else {
                        if (isNonSeparator(cCharAt)) {
                            return false;
                        }
                    }
                    break;
                case 2:
                    if (cCharAt == '0') {
                        i2 = 3;
                    } else if (cCharAt == '1') {
                        i2 = 4;
                    } else {
                        if (isNonSeparator(cCharAt)) {
                            return false;
                        }
                    }
                    break;
                case 4:
                    if (cCharAt == '1') {
                        i2 = 5;
                    } else {
                        if (isNonSeparator(cCharAt)) {
                            return false;
                        }
                    }
                    break;
                case 6:
                case 7:
                    if (isISODigit(cCharAt)) {
                        i2++;
                    } else {
                        if (isNonSeparator(cCharAt)) {
                            return false;
                        }
                    }
                    break;
                default:
                    if (isNonSeparator(cCharAt)) {
                        return false;
                    }
                    break;
                    break;
            }
        }
        return i2 == 6 || i2 == 7 || i2 == 8;
    }

    private static boolean matchTrunkPrefix(String str, int i) {
        boolean z = false;
        for (int i2 = 0; i2 < i; i2++) {
            char cCharAt = str.charAt(i2);
            if (cCharAt == '0' && !z) {
                z = true;
            } else if (isNonSeparator(cCharAt)) {
                return false;
            }
        }
        return z;
    }

    private static boolean isCountryCallingCode(int i) {
        return i > 0 && i < CCC_LENGTH && COUNTRY_CALLING_CALL[i];
    }

    private static class CountryCallingCodeAndNewIndex {
        public final int countryCallingCode;
        public final int newIndex;

        public CountryCallingCodeAndNewIndex(int i, int i2) {
            this.countryCallingCode = i;
            this.newIndex = i2;
        }
    }

    private static CountryCallingCodeAndNewIndex tryGetCountryCallingCodeAndNewIndex(String str, boolean z) {
        int length = str.length();
        int i = 0;
        int i2 = 0;
        for (int i3 = 0; i3 < length; i3++) {
            char cCharAt = str.charAt(i3);
            switch (i) {
                case 0:
                    if (cCharAt == '+') {
                        i = 1;
                    } else if (cCharAt == '0') {
                        i = 2;
                    } else if (cCharAt == '1') {
                        if (!z) {
                            return null;
                        }
                        i = 8;
                    } else {
                        if (isDialable(cCharAt)) {
                            return null;
                        }
                    }
                    break;
                case 1:
                case 3:
                case 5:
                case 6:
                case 7:
                    int iTryGetISODigit = tryGetISODigit(cCharAt);
                    if (iTryGetISODigit > 0) {
                        i2 = (i2 * 10) + iTryGetISODigit;
                        if (i2 >= 100 || isCountryCallingCode(i2)) {
                            return new CountryCallingCodeAndNewIndex(i2, i3 + 1);
                        }
                        i = (i != 1 && i != 3 && i != 5) ? i + 1 : 6;
                    } else {
                        if (isDialable(cCharAt)) {
                            return null;
                        }
                    }
                    break;
                case 2:
                    if (cCharAt == '0') {
                        i = 3;
                    } else if (cCharAt == '1') {
                        i = 4;
                    } else {
                        if (isDialable(cCharAt)) {
                            return null;
                        }
                    }
                    break;
                case 4:
                    if (cCharAt == '1') {
                        i = 5;
                    } else {
                        if (isDialable(cCharAt)) {
                            return null;
                        }
                    }
                    break;
                case 8:
                    if (cCharAt == '6') {
                        i = 9;
                    } else {
                        if (isDialable(cCharAt)) {
                            return null;
                        }
                    }
                    break;
                case 9:
                    if (cCharAt == '6') {
                        return new CountryCallingCodeAndNewIndex(66, i3 + 1);
                    }
                    return null;
                default:
                    return null;
            }
        }
        return null;
    }

    private static int tryGetTrunkPrefixOmittedIndex(String str, int i) {
        int length = str.length();
        while (i < length) {
            char cCharAt = str.charAt(i);
            if (tryGetISODigit(cCharAt) >= 0) {
                return i + 1;
            }
            if (isDialable(cCharAt)) {
                return -1;
            }
            i++;
        }
        return -1;
    }

    private static boolean checkPrefixIsIgnorable(String str, int i, int i2) {
        boolean z = false;
        while (i2 >= i) {
            if (tryGetISODigit(str.charAt(i2)) >= 0) {
                if (z) {
                    return false;
                }
                z = true;
            } else if (isDialable(str.charAt(i2))) {
                return false;
            }
            i2--;
        }
        return true;
    }
}
