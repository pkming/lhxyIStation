package org.apache.tools.ant.taskdefs.optional.jsp;

import android.provider.CallLog;
import java.io.File;
import org.apache.tools.ant.util.StringUtils;

/* JADX INFO: loaded from: classes3.dex */
public class JspNameMangler implements JspMangler {
    public static final String[] keywords = {"assert", "abstract", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", CallLog.Calls.NEW, "package", "private", "protected", "public", "return", "short", "static", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"};

    @Override // org.apache.tools.ant.taskdefs.optional.jsp.JspMangler
    public String mapPath(String str) {
        return null;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.jsp.JspMangler
    public String mapJspToJavaName(File file) {
        return mapJspToBaseName(file) + ".java";
    }

    private String mapJspToBaseName(File file) {
        String strStripExtension = stripExtension(file);
        int i = 0;
        while (true) {
            String[] strArr = keywords;
            if (i >= strArr.length) {
                break;
            }
            if (strStripExtension.equals(strArr[i])) {
                strStripExtension = strStripExtension + "%";
                break;
            }
            i++;
        }
        StringBuffer stringBuffer = new StringBuffer(strStripExtension.length());
        char cCharAt = strStripExtension.charAt(0);
        if (Character.isJavaIdentifierStart(cCharAt)) {
            stringBuffer.append(cCharAt);
        } else {
            stringBuffer.append(mangleChar(cCharAt));
        }
        for (int i2 = 1; i2 < strStripExtension.length(); i2++) {
            char cCharAt2 = strStripExtension.charAt(i2);
            if (Character.isJavaIdentifierPart(cCharAt2)) {
                stringBuffer.append(cCharAt2);
            } else {
                stringBuffer.append(mangleChar(cCharAt2));
            }
        }
        return stringBuffer.toString();
    }

    private String stripExtension(File file) {
        return StringUtils.removeSuffix(file.getName(), ".jsp");
    }

    private static String mangleChar(char c) {
        if (c == File.separatorChar) {
            c = '/';
        }
        String hexString = Integer.toHexString(c);
        int length = 5 - hexString.length();
        char[] cArr = new char[6];
        int i = 0;
        cArr[0] = '_';
        for (int i2 = 1; i2 <= length; i2++) {
            cArr[i2] = '0';
        }
        int i3 = length + 1;
        while (i3 < 6) {
            cArr[i3] = hexString.charAt(i);
            i3++;
            i++;
        }
        return new String(cArr);
    }
}
