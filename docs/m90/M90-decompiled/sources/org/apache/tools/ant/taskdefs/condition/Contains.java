package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public class Contains implements Condition {
    private boolean caseSensitive = true;
    private String string;
    private String subString;

    public void setString(String str) {
        this.string = str;
    }

    public void setSubstring(String str) {
        this.subString = str;
    }

    public void setCasesensitive(boolean z) {
        this.caseSensitive = z;
    }

    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() throws BuildException {
        String str;
        String str2 = this.string;
        if (str2 == null || (str = this.subString) == null) {
            throw new BuildException("both string and substring are required in contains");
        }
        if (this.caseSensitive) {
            if (str2.indexOf(str) > -1) {
                return true;
            }
        } else if (str2.toLowerCase().indexOf(this.subString.toLowerCase()) > -1) {
            return true;
        }
        return false;
    }
}
