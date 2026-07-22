package org.apache.tools.ant;

/* JADX INFO: loaded from: classes3.dex */
public class UnsupportedAttributeException extends BuildException {
    private static final long serialVersionUID = 1;
    private final String attribute;

    public UnsupportedAttributeException(String str, String str2) {
        super(str);
        this.attribute = str2;
    }

    public String getAttribute() {
        return this.attribute;
    }
}
