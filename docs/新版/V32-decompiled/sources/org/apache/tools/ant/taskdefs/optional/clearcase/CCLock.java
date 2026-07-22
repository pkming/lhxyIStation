package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;

/* JADX INFO: loaded from: classes3.dex */
public class CCLock extends ClearCase {
    public static final String FLAG_COMMENT = "-comment";
    public static final String FLAG_NUSERS = "-nusers";
    public static final String FLAG_OBSOLETE = "-obsolete";
    public static final String FLAG_PNAME = "-pname";
    public static final String FLAG_REPLACE = "-replace";
    private boolean mReplace = false;
    private boolean mObsolete = false;
    private String mComment = null;
    private String mNusers = null;
    private String mPname = null;
    private String mObjselect = null;

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        Commandline commandline = new Commandline();
        Project project = getProject();
        if (getViewPath() == null) {
            setViewPath(project.getBaseDir().getPath());
        }
        commandline.setExecutable(getClearToolCommand());
        commandline.createArgument().setValue(ClearCase.COMMAND_LOCK);
        checkOptions(commandline);
        if (!getFailOnErr()) {
            getProject().log("Ignoring any errors that occur for: " + getOpType(), 3);
        }
        if (Execute.isFailure(run(commandline)) && getFailOnErr()) {
            throw new BuildException("Failed executing: " + commandline.toString(), getLocation());
        }
    }

    private void checkOptions(Commandline commandline) {
        if (getReplace()) {
            commandline.createArgument().setValue("-replace");
        }
        if (getObsolete()) {
            commandline.createArgument().setValue(FLAG_OBSOLETE);
        } else {
            getNusersCommand(commandline);
        }
        getCommentCommand(commandline);
        if (getObjselect() == null && getPname() == null) {
            throw new BuildException("Should select either an element (pname) or an object (objselect)");
        }
        getPnameCommand(commandline);
        if (getObjselect() != null) {
            commandline.createArgument().setValue(getObjselect());
        }
    }

    public void setReplace(boolean z) {
        this.mReplace = z;
    }

    public boolean getReplace() {
        return this.mReplace;
    }

    public void setObsolete(boolean z) {
        this.mObsolete = z;
    }

    public boolean getObsolete() {
        return this.mObsolete;
    }

    public void setNusers(String str) {
        this.mNusers = str;
    }

    public String getNusers() {
        return this.mNusers;
    }

    public void setComment(String str) {
        this.mComment = str;
    }

    public String getComment() {
        return this.mComment;
    }

    public void setPname(String str) {
        this.mPname = str;
    }

    public String getPname() {
        return this.mPname;
    }

    public void setObjSel(String str) {
        this.mObjselect = str;
    }

    public void setObjselect(String str) {
        this.mObjselect = str;
    }

    public String getObjselect() {
        return this.mObjselect;
    }

    private void getNusersCommand(Commandline commandline) {
        if (getNusers() == null) {
            return;
        }
        commandline.createArgument().setValue(FLAG_NUSERS);
        commandline.createArgument().setValue(getNusers());
    }

    private void getCommentCommand(Commandline commandline) {
        if (getComment() == null) {
            return;
        }
        commandline.createArgument().setValue("-comment");
        commandline.createArgument().setValue(getComment());
    }

    private void getPnameCommand(Commandline commandline) {
        if (getPname() == null) {
            return;
        }
        commandline.createArgument().setValue("-pname");
        commandline.createArgument().setValue(getPname());
    }

    private String getOpType() {
        if (getPname() != null) {
            return getPname();
        }
        return getObjselect();
    }
}
