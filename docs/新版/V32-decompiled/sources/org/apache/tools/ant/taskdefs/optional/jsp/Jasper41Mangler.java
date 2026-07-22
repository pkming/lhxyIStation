package org.apache.tools.ant.taskdefs.optional.jsp;

import java.io.File;

/* JADX INFO: loaded from: classes3.dex */
public class Jasper41Mangler implements JspMangler {
    @Override // org.apache.tools.ant.taskdefs.optional.jsp.JspMangler
    public String mapPath(String str) {
        return null;
    }

    @Override // org.apache.tools.ant.taskdefs.optional.jsp.JspMangler
    public String mapJspToJavaName(File file) {
        String absolutePath = file.getAbsolutePath();
        int iLastIndexOf = absolutePath.lastIndexOf(File.separatorChar) + 1;
        int length = absolutePath.length();
        StringBuffer stringBuffer = new StringBuffer(absolutePath.length() - iLastIndexOf);
        if (!Character.isJavaIdentifierStart(absolutePath.charAt(iLastIndexOf)) || absolutePath.charAt(iLastIndexOf) == '_') {
            stringBuffer.append('_');
        }
        while (iLastIndexOf < length) {
            char cCharAt = absolutePath.charAt(iLastIndexOf);
            if (Character.isJavaIdentifierPart(cCharAt)) {
                stringBuffer.append(cCharAt);
            } else if (cCharAt == '.') {
                stringBuffer.append('_');
            } else {
                stringBuffer.append(mangleChar(cCharAt));
            }
            iLastIndexOf++;
        }
        return stringBuffer.toString();
    }

    private static String mangleChar(char c) {
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
