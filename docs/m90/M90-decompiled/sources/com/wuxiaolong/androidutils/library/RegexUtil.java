package com.wuxiaolong.androidutils.library;

import java.util.regex.Pattern;

/* JADX INFO: loaded from: classes2.dex */
public final class RegexUtil {
    public static String phoneNoHide(String str) {
        return str.replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2");
    }

    public static String cardIdHide(String str) {
        return str.replaceAll("\\d{15}(\\d{3})", "**** **** **** **** $1");
    }

    public static String idHide(String str) {
        return str.replaceAll("(\\d{4})\\d{10}(\\d{4})", "$1** **** ****$2");
    }

    public static boolean checkVehicleNo(String str) {
        return Pattern.compile("^[一-龥]{1}[a-zA-Z]{1}[a-zA-Z_0-9]{5}$").matcher(str).find();
    }

    public static boolean checkIdCard(String str) {
        return Pattern.matches("[1-9]\\d{13,16}[a-zA-Z0-9]{1}", str);
    }

    public static boolean checkMobile(String str) {
        return Pattern.matches("(\\+\\d+)?1[3458]\\d{9}$", str);
    }

    public static boolean checkPhone(String str) {
        return Pattern.matches("(\\+\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$", str);
    }

    public static boolean checkEmail(String str) {
        return Pattern.matches("\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?", str);
    }

    public static boolean checkDigit(String str) {
        return Pattern.matches("\\-?[1-9]\\d+", str);
    }

    public static boolean checkDecimals(String str) {
        return Pattern.matches("\\-?[1-9]\\d+(\\.\\d+)?", str);
    }

    public static boolean checkBlankSpace(String str) {
        return Pattern.matches("\\s+", str);
    }

    public static boolean checkChinese(String str) {
        return Pattern.matches("^[一-龥]+$", str);
    }

    public static boolean checkBirthday(String str) {
        return Pattern.matches("[1-9]{4}([-./])\\d{1,2}\\1\\d{1,2}", str);
    }

    public static boolean checkURL(String str) {
        return Pattern.matches("(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?", str);
    }

    public static boolean checkPostcode(String str) {
        return Pattern.matches("[1-9]\\d{5}", str);
    }

    public static boolean checkIpAddress(String str) {
        return Pattern.matches("[1-9](\\d{1,2})?\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))", str);
    }
}
