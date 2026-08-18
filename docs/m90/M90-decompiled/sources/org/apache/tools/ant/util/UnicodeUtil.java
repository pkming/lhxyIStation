package org.apache.tools.ant.util;

/* JADX INFO: loaded from: classes3.dex */
public class UnicodeUtil {
    public static StringBuffer EscapeUnicode(char c) {
        StringBuffer stringBuffer = new StringBuffer("u0000");
        String hexString = Integer.toHexString(c);
        for (int i = 0; i < hexString.length(); i++) {
            stringBuffer.setCharAt((stringBuffer.length() - hexString.length()) + i, hexString.charAt(i));
        }
        return stringBuffer;
    }
}
