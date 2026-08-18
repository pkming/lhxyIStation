package org.apache.tools.ant;

/* JADX INFO: loaded from: classes3.dex */
public class UnsupportedElementException extends BuildException {
    private static final long serialVersionUID = 1;
    private final String element;

    public UnsupportedElementException(String str, String str2) {
        super(str);
        this.element = str2;
    }

    public String getElement() {
        return this.element;
    }
}
