package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;

/* JADX INFO: loaded from: classes3.dex */
public class CCRmtype extends ClearCase {
    public static final String FLAG_COMMENT = "-c";
    public static final String FLAG_COMMENTFILE = "-cfile";
    public static final String FLAG_FORCE = "-force";
    public static final String FLAG_IGNORE = "-ignore";
    public static final String FLAG_NOCOMMENT = "-nc";
    public static final String FLAG_RMALL = "-rmall";
    private String mTypeKind = null;
    private String mTypeName = null;
    private String mVOB = null;
    private String mComment = null;
    private String mCfile = null;
    private boolean mRmall = false;
    private boolean mIgnore = false;

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        Commandline commandline = new Commandline();
        if (getTypeKind() == null) {
            throw new BuildException("Required attribute TypeKind not specified");
        }
        if (getTypeName() == null) {
            throw new BuildException("Required attribute TypeName not specified");
        }
        commandline.setExecutable(getClearToolCommand());
        commandline.createArgument().setValue(ClearCase.COMMAND_RMTYPE);
        checkOptions(commandline);
        if (!getFailOnErr()) {
            getProject().log("Ignoring any errors that occur for: " + getTypeSpecifier(), 3);
        }
        if (Execute.isFailure(run(commandline)) && getFailOnErr()) {
            throw new BuildException("Failed executing: " + commandline.toString(), getLocation());
        }
    }

    private void checkOptions(Commandline commandline) {
        if (getIgnore()) {
            commandline.createArgument().setValue(FLAG_IGNORE);
        }
        if (getRmAll()) {
            commandline.createArgument().setValue(FLAG_RMALL);
            commandline.createArgument().setValue(FLAG_FORCE);
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

    public void setIgnore(boolean z) {
        this.mIgnore = z;
    }

    public boolean getIgnore() {
        return this.mIgnore;
    }

    public void setRmAll(boolean z) {
        this.mRmall = z;
    }

    public boolean getRmAll() {
        return this.mRmall;
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

    public void setTypeKind(String str) {
        this.mTypeKind = str;
    }

    public String getTypeKind() {
        return this.mTypeKind;
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

    private String getTypeSpecifier() {
        String str = getTypeKind() + ":" + getTypeName();
        return getVOB() != null ? str + "@" + getVOB() : str;
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
}
