package com.wuxiaolong.androidutils.library;

import android.telephony.PhoneNumberUtils;

/* JADX INFO: loaded from: classes2.dex */
public class CheckIdCardUtil {
    public static boolean checkBankCard(String str) {
        char bankCardCheckCode = getBankCardCheckCode(str.substring(0, str.length() - 1));
        return bankCardCheckCode != 'N' && str.charAt(str.length() - 1) == bankCardCheckCode;
    }

    public static char getBankCardCheckCode(String str) {
        if (str == null || str.trim().length() == 0 || !str.matches("\\d+")) {
            return PhoneNumberUtils.WILD;
        }
        char[] charArray = str.trim().toCharArray();
        int length = charArray.length - 1;
        int i = 0;
        int i2 = 0;
        while (length >= 0) {
            int i3 = charArray[length] - '0';
            if (i2 % 2 == 0) {
                int i4 = i3 * 2;
                i3 = (i4 % 10) + (i4 / 10);
            }
            i += i3;
            length--;
            i2++;
        }
        int i5 = i % 10;
        if (i5 == 0) {
            return '0';
        }
        return (char) ((10 - i5) + 48);
    }
}
