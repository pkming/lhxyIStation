package org.apache.tools.ant.util.regexp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public class Jdk14RegexpRegexp extends Jdk14RegexpMatcher implements Regexp {
    private static final int DECIMAL = 10;

    protected int getSubsOptions(int i) {
        return RegexpUtil.hasFlag(i, 16) ? 16 : 1;
    }

    @Override // org.apache.tools.ant.util.regexp.Regexp
    public String substitute(String str, String str2, int i) throws BuildException {
        StringBuffer stringBuffer = new StringBuffer();
        int i2 = 0;
        while (i2 < str2.length()) {
            char cCharAt = str2.charAt(i2);
            if (cCharAt == '$') {
                stringBuffer.append('\\');
                stringBuffer.append('$');
            } else if (cCharAt == '\\') {
                i2++;
                if (i2 < str2.length()) {
                    char cCharAt2 = str2.charAt(i2);
                    int iDigit = Character.digit(cCharAt2, 10);
                    if (iDigit > -1) {
                        stringBuffer.append("$").append(iDigit);
                    } else {
                        stringBuffer.append(cCharAt2);
                    }
                } else {
                    stringBuffer.append('\\');
                }
            } else {
                stringBuffer.append(cCharAt);
            }
            i2++;
        }
        String string = stringBuffer.toString();
        int subsOptions = getSubsOptions(i);
        Pattern compiledPattern = getCompiledPattern(i);
        StringBuffer stringBuffer2 = new StringBuffer();
        Matcher matcher = compiledPattern.matcher(str);
        if (RegexpUtil.hasFlag(subsOptions, 16)) {
            stringBuffer2.append(matcher.replaceAll(string));
        } else if (matcher.find()) {
            matcher.appendReplacement(stringBuffer2, string);
            matcher.appendTail(stringBuffer2);
        } else {
            stringBuffer2.append(str);
        }
        return stringBuffer2.toString();
    }
}
