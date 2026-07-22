package org.apache.tools.ant.taskdefs.cvslib;

import org.apache.tools.ant.BuildException;

/* JADX INFO: loaded from: classes3.dex */
public class CvsUser {
    private String displayName;
    private String userID;

    public void setDisplayname(String str) {
        this.displayName = str;
    }

    public void setUserid(String str) {
        this.userID = str;
    }

    public String getUserID() {
        return this.userID;
    }

    public String getDisplayname() {
        return this.displayName;
    }

    public void validate() throws BuildException {
        if (this.userID == null) {
            throw new BuildException("Username attribute must be set.");
        }
        if (this.displayName == null) {
            throw new BuildException("Displayname attribute must be set for userID " + this.userID);
        }
    }
}
