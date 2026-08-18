package android.net.http;

import org.apache.http.protocol.HTTP;
import org.apache.http.util.CharArrayBuffer;

/* JADX INFO: loaded from: classes.dex */
class CharArrayBuffers {
    static final char uppercaseAddon = ' ';

    private static char toLower(char c) {
        return (c < 'A' || c > 'Z') ? c : (char) (c + uppercaseAddon);
    }

    CharArrayBuffers() {
    }

    static boolean containsIgnoreCaseTrimmed(CharArrayBuffer charArrayBuffer, int i, String str) {
        int length = charArrayBuffer.length();
        char[] cArrBuffer = charArrayBuffer.buffer();
        while (i < length && HTTP.isWhitespace(cArrBuffer[i])) {
            i++;
        }
        int length2 = str.length();
        boolean z = length >= i + length2;
        for (int i2 = 0; z && i2 < length2; i2++) {
            char c = cArrBuffer[i + i2];
            char cCharAt = str.charAt(i2);
            if (c != cCharAt) {
                z = toLower(c) == toLower(cCharAt);
            }
        }
        return z;
    }

    static int setLowercaseIndexOf(CharArrayBuffer charArrayBuffer, int i) {
        int length = charArrayBuffer.length();
        char[] cArrBuffer = charArrayBuffer.buffer();
        for (int i2 = 0; i2 < length; i2++) {
            char c = cArrBuffer[i2];
            if (c == i) {
                return i2;
            }
            if (c >= 'A' && c <= 'Z') {
                cArrBuffer[i2] = (char) (c + uppercaseAddon);
            }
        }
        return -1;
    }
}
