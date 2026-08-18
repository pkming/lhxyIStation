package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;

/* JADX INFO: loaded from: classes3.dex */
public class CCUnlock extends ClearCase {
    public static final String FLAG_COMMENT = "-comment";
    public static final String FLAG_PNAME = "-pname";
    private String mComment = null;
    private String mPname = null;

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        Commandline commandline = new Commandline();
        Project project = getProject();
        if (getViewPath() == null) {
            setViewPath(project.getBaseDir().getPath());
        }
        commandline.setExecutable(getClearToolCommand());
        commandline.createArgument().setValue(ClearCase.COMMAND_UNLOCK);
        checkOptions(commandline);
        if (!getFailOnErr()) {
            getProject().log("Ignoring any errors that occur for: " + getOpType(), 3);
        }
        if (Execute.isFailure(run(commandline)) && getFailOnErr()) {
            throw new BuildException("Failed executing: " + commandline.toString(), getLocation());
        }
    }

    private void checkOptions(Commandline commandline) {
        getCommentCommand(commandline);
        if (getObjSelect() == null && getPname() == null) {
            throw new BuildException("Should select either an element (pname) or an object (objselect)");
        }
        getPnameCommand(commandline);
        if (getObjSelect() != null) {
            commandline.createArgument().setValue(getObjSelect());
        }
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

    public void setObjselect(String str) {
        setObjSelect(str);
    }

    public void setObjSel(String str) {
        setObjSelect(str);
    }

    public String getObjselect() {
        return getObjSelect();
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
        return getObjSelect();
    }
}
