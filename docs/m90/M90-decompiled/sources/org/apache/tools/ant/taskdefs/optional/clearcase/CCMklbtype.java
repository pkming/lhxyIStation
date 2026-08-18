package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;

/* JADX INFO: loaded from: classes3.dex */
public class CCMklbtype extends ClearCase {
    public static final String FLAG_COMMENT = "-c";
    public static final String FLAG_COMMENTFILE = "-cfile";
    public static final String FLAG_GLOBAL = "-global";
    public static final String FLAG_NOCOMMENT = "-nc";
    public static final String FLAG_ORDINARY = "-ordinary";
    public static final String FLAG_PBRANCH = "-pbranch";
    public static final String FLAG_REPLACE = "-replace";
    public static final String FLAG_SHARED = "-shared";
    private String mTypeName = null;
    private String mVOB = null;
    private String mComment = null;
    private String mCfile = null;
    private boolean mReplace = false;
    private boolean mGlobal = false;
    private boolean mOrdinary = true;
    private boolean mPbranch = false;
    private boolean mShared = false;

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        Commandline commandline = new Commandline();
        if (getTypeName() == null) {
            throw new BuildException("Required attribute TypeName not specified");
        }
        commandline.setExecutable(getClearToolCommand());
        commandline.createArgument().setValue(ClearCase.COMMAND_MKLBTYPE);
        checkOptions(commandline);
        if (!getFailOnErr()) {
            getProject().log("Ignoring any errors that occur for: " + getTypeSpecifier(), 3);
        }
        if (Execute.isFailure(run(commandline)) && getFailOnErr()) {
            throw new BuildException("Failed executing: " + commandline.toString(), getLocation());
        }
    }

    private void checkOptions(Commandline commandline) {
        if (getReplace()) {
            commandline.createArgument().setValue("-replace");
        }
        if (getOrdinary()) {
            commandline.createArgument().setValue(FLAG_ORDINARY);
        } else if (getGlobal()) {
            commandline.createArgument().setValue(FLAG_GLOBAL);
        }
        if (getPbranch()) {
            commandline.createArgument().setValue(FLAG_PBRANCH);
        }
        if (getShared()) {
            commandline.createArgument().setValue(FLAG_SHARED);
        }
        if (getComment() != null) {
            getCommentCommand(commandline);
        } else if (getCommentFile() != null) {
            getCommentFileCommand(commandline);
        } else {
            commandline.createArgument().setValue("-nc");
        }
        commandline.createArgument().setValue(getTypeSpecifier());
    }

    public void setTypeName(String str) {
        this.mTypeName = str;
    }

    public String getTypeName() {
        return this.mTypeName;
    }

    public void setVOB(String str) {
        this.mVOB = str;
    }

    public String getVOB() {
        return this.mVOB;
    }

    public void setReplace(boolean z) {
        this.mReplace = z;
    }

    public boolean getReplace() {
        return this.mReplace;
    }

    public void setGlobal(boolean z) {
        this.mGlobal = z;
    }

    public boolean getGlobal() {
        return this.mGlobal;
    }

    public void setOrdinary(boolean z) {
        this.mOrdinary = z;
    }

    public boolean getOrdinary() {
        return this.mOrdinary;
    }

    public void setPbranch(boolean z) {
        this.mPbranch = z;
    }

    public boolean getPbranch() {
        return this.mPbranch;
    }

    public void setShared(boolean z) {
        this.mShared = z;
    }

    public boolean getShared() {
        return this.mShared;
    }

    public void setComment(String str) {
        this.mComment = str;
    }

    public String getComment() {
        return this.mComment;
    }

    public void setCommentFile(String str) {
        this.mCfile = str;
    }

    public String getCommentFile() {
        return this.mCfile;
    }

    private void getCommentCommand(Commandline commandline) {
        if (getComment() != null) {
            commandline.createArgument().setValue("-c");
            commandline.createArgument().setValue(getComment());
        }
    }

    private void getCommentFileCommand(Commandline commandline) {
        if (getCommentFile() != null) {
            commandline.createArgument().setValue("-cfile");
            commandline.createArgument().setValue(getCommentFile());
        }
    }

    private String getTypeSpecifier() {
        String typeName = getTypeName();
        return getVOB() != null ? typeName + "@" + getVOB() : typeName;
    }
}
