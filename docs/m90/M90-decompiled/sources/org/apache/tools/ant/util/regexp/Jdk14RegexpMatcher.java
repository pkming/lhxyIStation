package org.apache.tools.ant.util.regexp;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public class Jdk14RegexpMatcher implements RegexpMatcher {
    private String pattern;

    @Override // org.apache.tools.ant.util.regexp.RegexpMatcher
    public void setPattern(String str) {
        this.pattern = str;
    }

    @Override // org.apache.tools.ant.util.regexp.RegexpMatcher
    public String getPattern() {
        return this.pattern;
    }

    protected Pattern getCompiledPattern(int i) throws BuildException {
        try {
            return Pattern.compile(this.pattern, getCompilerOptions(i));
        } catch (PatternSyntaxException e) {
            throw new BuildException(e);
        }
    }

    @Override // org.apache.tools.ant.util.regexp.RegexpMatcher
    public boolean matches(String str) throws BuildException {
        return matches(str, 0);
    }

    @Override // org.apache.tools.ant.util.regexp.RegexpMatcher
    public boolean matches(String str, int i) throws BuildException {
        try {
            return getCompiledPattern(i).matcher(str).find();
        } catch (Exception e) {
            throw new BuildException(e);
        }
    }

    @Override // org.apache.tools.ant.util.regexp.RegexpMatcher
    public Vector getGroups(String str) throws BuildException {
        return getGroups(str, 0);
    }

    @Override // org.apache.tools.ant.util.regexp.RegexpMatcher
    public Vector getGroups(String str, int i) throws BuildException {
        Matcher matcher = getCompiledPattern(i).matcher(str);
        if (!matcher.find()) {
            return null;
        }
        Vector vector = new Vector();
        int iGroupCount = matcher.groupCount();
        for (int i2 = 0; i2 <= iGroupCount; i2++) {
            String strGroup = matcher.group(i2);
            if (strGroup == null) {
                strGroup = "";
            }
            vector.addElement(strGroup);
        }
        return vector;
    }

    protected int getCompilerOptions(int i) {
        int i2 = RegexpUtil.hasFlag(i, 256) ? 3 : 1;
        if (RegexpUtil.hasFlag(i, 4096)) {
            i2 |= 8;
        }
        return RegexpUtil.hasFlag(i, 65536) ? i2 | 32 : i2;
    }
}
