package org.apache.tools.ant.util;

import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.regexp.RegexpMatcher;
import org.apache.tools.ant.util.regexp.RegexpMatcherFactory;
import org.apache.tools.ant.util.regexp.RegexpUtil;

/* JADX INFO: loaded from: classes3.dex */
public class RegexpPatternMapper implements FileNameMapper {
    private static final int DECIMAL = 10;
    protected RegexpMatcher reg;
    protected char[] to = null;
    protected StringBuffer result = new StringBuffer();
    private boolean handleDirSep = false;
    private int regexpOptions = 0;

    public RegexpPatternMapper() throws BuildException {
        this.reg = null;
        this.reg = new RegexpMatcherFactory().newRegexpMatcher();
    }

    public void setHandleDirSep(boolean z) {
        this.handleDirSep = z;
    }

    public void setCaseSensitive(boolean z) {
        this.regexpOptions = RegexpUtil.asOptions(z);
    }

    @Override // org.apache.tools.ant.util.FileNameMapper
    public void setFrom(String str) throws BuildException {
        if (str != null) {
            try {
                this.reg.setPattern(str);
                return;
            } catch (NoClassDefFoundError e) {
                throw new BuildException("Cannot load regular expression matcher", e);
            }
        }
        throw new BuildException("this mapper requires a 'from' attribute");
    }

    @Override // org.apache.tools.ant.util.FileNameMapper
    public void setTo(String str) {
        if (str != null) {
            this.to = str.toCharArray();
            return;
        }
        throw new BuildException("this mapper requires a 'to' attribute");
    }

    @Override // org.apache.tools.ant.util.FileNameMapper
    public String[] mapFileName(String str) {
        if (this.handleDirSep && str.indexOf("\\") != -1) {
            str = str.replace('\\', '/');
        }
        RegexpMatcher regexpMatcher = this.reg;
        if (regexpMatcher == null || this.to == null || !regexpMatcher.matches(str, this.regexpOptions)) {
            return null;
        }
        return new String[]{replaceReferences(str)};
    }

    protected String replaceReferences(String str) {
        Vector groups = this.reg.getGroups(str, this.regexpOptions);
        this.result.setLength(0);
        int i = 0;
        while (true) {
            char[] cArr = this.to;
            if (i < cArr.length) {
                if (cArr[i] == '\\') {
                    i++;
                    if (i < cArr.length) {
                        int iDigit = Character.digit(cArr[i], 10);
                        if (iDigit > -1) {
                            this.result.append((String) groups.elementAt(iDigit));
                        } else {
                            this.result.append(this.to[i]);
                        }
                    } else {
                        this.result.append('\\');
                    }
                } else {
                    this.result.append(cArr[i]);
                }
                i++;
            } else {
                return this.result.substring(0);
            }
        }
    }
}
