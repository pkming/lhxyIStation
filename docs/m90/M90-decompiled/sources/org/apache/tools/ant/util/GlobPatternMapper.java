package org.apache.tools.ant.util;

import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public class GlobPatternMapper implements FileNameMapper {
    protected int postfixLength;
    protected int prefixLength;
    protected String fromPrefix = null;
    protected String fromPostfix = null;
    protected String toPrefix = null;
    protected String toPostfix = null;
    private boolean fromContainsStar = false;
    private boolean toContainsStar = false;
    private boolean handleDirSep = false;
    private boolean caseSensitive = true;

    public void setHandleDirSep(boolean z) {
        this.handleDirSep = z;
    }

    public boolean getHandleDirSep() {
        return this.handleDirSep;
    }

    public void setCaseSensitive(boolean z) {
        this.caseSensitive = z;
    }

    @Override // org.apache.tools.ant.util.FileNameMapper
    public void setFrom(String str) {
        if (str != null) {
            int iLastIndexOf = str.lastIndexOf("*");
            if (iLastIndexOf == -1) {
                this.fromPrefix = str;
                this.fromPostfix = "";
            } else {
                this.fromPrefix = str.substring(0, iLastIndexOf);
                this.fromPostfix = str.substring(iLastIndexOf + 1);
                this.fromContainsStar = true;
            }
            this.prefixLength = this.fromPrefix.length();
            this.postfixLength = this.fromPostfix.length();
            return;
        }
        throw new BuildException("this mapper requires a 'from' attribute");
    }

    @Override // org.apache.tools.ant.util.FileNameMapper
    public void setTo(String str) {
        if (str != null) {
            int iLastIndexOf = str.lastIndexOf("*");
            if (iLastIndexOf == -1) {
                this.toPrefix = str;
                this.toPostfix = "";
                return;
            } else {
                this.toPrefix = str.substring(0, iLastIndexOf);
                this.toPostfix = str.substring(iLastIndexOf + 1);
                this.toContainsStar = true;
                return;
            }
        }
        throw new BuildException("this mapper requires a 'to' attribute");
    }

    @Override // org.apache.tools.ant.util.FileNameMapper
    public String[] mapFileName(String str) {
        String strModifyName = modifyName(str);
        if (this.fromPrefix == null || str.length() < this.prefixLength + this.postfixLength) {
            return null;
        }
        if (!this.fromContainsStar && !strModifyName.equals(modifyName(this.fromPrefix))) {
            return null;
        }
        if (this.fromContainsStar && (!strModifyName.startsWith(modifyName(this.fromPrefix)) || !strModifyName.endsWith(modifyName(this.fromPostfix)))) {
            return null;
        }
        String[] strArr = new String[1];
        strArr[0] = this.toPrefix + (this.toContainsStar ? extractVariablePart(str) + this.toPostfix : "");
        return strArr;
    }

    protected String extractVariablePart(String str) {
        return str.substring(this.prefixLength, str.length() - this.postfixLength);
    }

    private String modifyName(String str) {
        if (!this.caseSensitive) {
            str = str.toLowerCase();
        }
        return (!this.handleDirSep || str.indexOf(92) == -1) ? str : str.replace('\\', '/');
    }
}
