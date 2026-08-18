package org.apache.tools.ant.taskdefs.cvslib;

/* JADX INFO: loaded from: classes3.dex */
class RCSFile {
    private String name;
    private String previousRevision;
    private String revision;

    RCSFile(String str, String str2) {
        this(str, str2, null);
    }

    RCSFile(String str, String str2, String str3) {
        this.name = str;
        this.revision = str2;
        if (str2.equals(str3)) {
            return;
        }
        this.previousRevision = str3;
    }

    String getName() {
        return this.name;
    }

    String getRevision() {
        return this.revision;
    }

    String getPreviousRevision() {
        return this.previousRevision;
    }
}
