package org.apache.tools.ant.taskdefs.condition;

import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public class Equals implements Condition {
    private static final int REQUIRED = 3;
    private Object arg1;
    private Object arg2;
    private int args;
    private boolean trim = false;
    private boolean caseSensitive = true;
    private boolean forcestring = false;

    public void setArg1(Object obj) {
        if (obj instanceof String) {
            setArg1((String) obj);
        } else {
            setArg1Internal(obj);
        }
    }

    public void setArg1(String str) {
        setArg1Internal(str);
    }

    private void setArg1Internal(Object obj) {
        this.arg1 = obj;
        this.args |= 1;
    }

    public void setArg2(Object obj) {
        if (obj instanceof String) {
            setArg2((String) obj);
        } else {
            setArg2Internal(obj);
        }
    }

    public void setArg2(String str) {
        setArg2Internal(str);
    }

    private void setArg2Internal(Object obj) {
        this.arg2 = obj;
        this.args |= 2;
    }

    public void setTrim(boolean z) {
        this.trim = z;
    }

    public void setCasesensitive(boolean z) {
        this.caseSensitive = z;
    }

    public void setForcestring(boolean z) {
        this.forcestring = z;
    }

    @Override // org.apache.tools.ant.taskdefs.condition.Condition
    public boolean eval() throws BuildException {
        if ((this.args & 3) != 3) {
            throw new BuildException("both arg1 and arg2 are required in equals");
        }
        Object obj = this.arg1;
        Object obj2 = this.arg2;
        if (obj == obj2) {
            return true;
        }
        if (obj != null && obj.equals(obj2)) {
            return true;
        }
        if (this.forcestring) {
            Object string = this.arg1;
            if (string != null && !(string instanceof String)) {
                string = string.toString();
            }
            this.arg1 = string;
            Object string2 = this.arg2;
            if (string2 != null && !(string2 instanceof String)) {
                string2 = string2.toString();
            }
            this.arg2 = string2;
        }
        Object obj3 = this.arg1;
        if ((obj3 instanceof String) && this.trim) {
            this.arg1 = ((String) obj3).trim();
        }
        Object obj4 = this.arg2;
        if ((obj4 instanceof String) && this.trim) {
            this.arg2 = ((String) obj4).trim();
        }
        Object obj5 = this.arg1;
        if (!(obj5 instanceof String)) {
            return false;
        }
        Object obj6 = this.arg2;
        if (!(obj6 instanceof String)) {
            return false;
        }
        String str = (String) obj5;
        String str2 = (String) obj6;
        return this.caseSensitive ? str.equals(str2) : str.equalsIgnoreCase(str2);
    }
}
