package org.apache.tools.ant.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Vector;
import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public final class StringUtils {
    private static final long GIGABYTE = 1073741824;
    private static final long KILOBYTE = 1024;
    public static final String LINE_SEP = System.getProperty("line.separator");
    private static final long MEGABYTE = 1048576;
    private static final long PETABYTE = 1125899906842624L;
    private static final long TERABYTE = 1099511627776L;

    private StringUtils() {
    }

    public static Vector<String> lineSplit(String str) {
        return split(str, 10);
    }

    public static Vector<String> split(String str, int i) {
        Vector<String> vector = new Vector<>();
        int i2 = 0;
        while (true) {
            int iIndexOf = str.indexOf(i, i2);
            if (iIndexOf != -1) {
                vector.addElement(str.substring(i2, iIndexOf));
                i2 = iIndexOf + 1;
            } else {
                vector.addElement(str.substring(i2));
                return vector;
            }
        }
    }

    public static String replace(String str, String str2, String str3) {
        return str.replace(str2, str3);
    }

    public static String getStackTrace(Throwable th) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter((Writer) stringWriter, true);
        th.printStackTrace(printWriter);
        printWriter.flush();
        printWriter.close();
        return stringWriter.toString();
    }

    public static boolean endsWith(StringBuffer stringBuffer, String str) {
        if (str.length() > stringBuffer.length()) {
            return false;
        }
        int length = stringBuffer.length() - 1;
        for (int length2 = str.length() - 1; length2 >= 0; length2--) {
            if (stringBuffer.charAt(length) != str.charAt(length2)) {
                return false;
            }
            length--;
        }
        return true;
    }

    public static String resolveBackSlash(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        boolean z = false;
        for (int i = 0; i < str.length(); i++) {
            char cCharAt = str.charAt(i);
            if (z) {
                if (cCharAt != '\\') {
                    if (cCharAt != 'f') {
                        if (cCharAt == 'n') {
                            stringBuffer.append('\n');
                        } else {
                            switch (cCharAt) {
                                case 'r':
                                    stringBuffer.append('\r');
                                    break;
                                case 's':
                                    stringBuffer.append(" \t\n\r\f");
                                    break;
                                case 't':
                                    stringBuffer.append('\t');
                                    break;
                                default:
                                    stringBuffer.append(cCharAt);
                                    break;
                            }
                        }
                    } else {
                        stringBuffer.append('\f');
                    }
                } else {
                    stringBuffer.append('\\');
                }
                z = false;
            } else if (cCharAt == '\\') {
                z = true;
            } else {
                stringBuffer.append(cCharAt);
            }
        }
        return stringBuffer.toString();
    }

    public static long parseHumanSizes(String str) throws Exception {
        long j;
        char cCharAt = str.charAt(0);
        int i = 1;
        long j2 = 1;
        if (cCharAt == '+') {
            str = str.substring(1);
        } else if (cCharAt == '-') {
            j2 = -1;
            str = str.substring(1);
        }
        char cCharAt2 = str.charAt(str.length() - 1);
        if (!Character.isDigit(cCharAt2)) {
            if (cCharAt2 == 'G') {
                j = 1073741824;
            } else if (cCharAt2 == 'K') {
                j = 1024;
            } else if (cCharAt2 == 'M') {
                j = 1048576;
            } else if (cCharAt2 == 'P') {
                j = PETABYTE;
            } else if (cCharAt2 != 'T') {
                i = 0;
                str = str.substring(0, str.length() - i);
            } else {
                j = TERABYTE;
            }
            j2 *= j;
            str = str.substring(0, str.length() - i);
        }
        try {
            return j2 * Long.parseLong(str);
        } catch (NumberFormatException e) {
            throw new BuildException("Failed to parse \"" + str + "\"", e);
        }
    }

    public static String removeSuffix(String str, String str2) {
        return str.endsWith(str2) ? str.substring(0, str.length() - str2.length()) : str;
    }

    public static String removePrefix(String str, String str2) {
        return str.startsWith(str2) ? str.substring(str2.length()) : str;
    }
}
