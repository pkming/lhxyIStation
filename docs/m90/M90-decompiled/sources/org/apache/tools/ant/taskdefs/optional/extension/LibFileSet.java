package org.apache.tools.ant.taskdefs.optional.extension;

import org.apache.tools.ant.types.FileSet;

/* JADX INFO: loaded from: classes3.dex */
public class LibFileSet extends FileSet {
    private boolean includeImpl;
    private boolean includeURL;
    private String urlBase;

    public void setIncludeUrl(boolean z) {
        this.includeURL = z;
    }

    public void setIncludeImpl(boolean z) {
        this.includeImpl = z;
    }

    public void setUrlBase(String str) {
        this.urlBase = str;
    }

    boolean isIncludeURL() {
        return this.includeURL;
    }

    boolean isIncludeImpl() {
        return this.includeImpl;
    }

    String getUrlBase() {
        return this.urlBase;
    }
}
