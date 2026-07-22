package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;

/* JADX INFO: loaded from: classes3.dex */
public class CCUpdate extends ClearCase {
    public static final String FLAG_CURRENTTIME = "-ctime";
    public static final String FLAG_GRAPHICAL = "-graphical";
    public static final String FLAG_LOG = "-log";
    public static final String FLAG_NOVERWRITE = "-noverwrite";
    public static final String FLAG_OVERWRITE = "-overwrite";
    public static final String FLAG_PRESERVETIME = "-ptime";
    public static final String FLAG_RENAME = "-rename";
    private boolean mGraphical = false;
    private boolean mOverwrite = false;
    private boolean mRename = false;
    private boolean mCtime = false;
    private boolean mPtime = false;
    private String mLog = null;

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        Commandline commandline = new Commandline();
        Project project = getProject();
        if (getViewPath() == null) {
            setViewPath(project.getBaseDir().getPath());
        }
        commandline.setExecutable(getClearToolCommand());
        commandline.createArgument().setValue("update");
        checkOptions(commandline);
        getProject().log(commandline.toString(), 4);
        if (!getFailOnErr()) {
            getProject().log("Ignoring any errors that occur for: " + getViewPathBasename(), 3);
        }
        if (Execute.isFailure(run(commandline)) && getFailOnErr()) {
            throw new BuildException("Failed executing: " + commandline.toString(), getLocation());
        }
    }

    private void checkOptions(Commandline commandline) {
        if (getGraphical()) {
            commandline.createArgument().setValue(FLAG_GRAPHICAL);
        } else {
            if (getOverwrite()) {
                commandline.createArgument().setValue(FLAG_OVERWRITE);
            } else if (getRename()) {
                commandline.createArgument().setValue(FLAG_RENAME);
            } else {
                commandline.createArgument().setValue(FLAG_NOVERWRITE);
            }
            if (getCurrentTime()) {
                commandline.createArgument().setValue(FLAG_CURRENTTIME);
            } else if (getPreserveTime()) {
                commandline.createArgument().setValue("-ptime");
            }
            getLogCommand(commandline);
        }
        commandline.createArgument().setValue(getViewPath());
    }

    public void setGraphical(boolean z) {
        this.mGraphical = z;
    }

    public boolean getGraphical() {
        return this.mGraphical;
    }

    public void setOverwrite(boolean z) {
        this.mOverwrite = z;
    }

    public boolean getOverwrite() {
        return this.mOverwrite;
    }

    public void setRename(boolean z) {
        this.mRename = z;
    }

    public boolean getRename() {
        return this.mRename;
    }

    public void setCurrentTime(boolean z) {
        this.mCtime = z;
    }

    public boolean getCurrentTime() {
        return this.mCtime;
    }

    public void setPreserveTime(boolean z) {
        this.mPtime = z;
    }

    public boolean getPreserveTime() {
        return this.mPtime;
    }

    public void setLog(String str) {
        this.mLog = str;
    }

    public String getLog() {
        return this.mLog;
    }

    private void getLogCommand(Commandline commandline) {
        if (getLog() == null) {
            return;
        }
        commandline.createArgument().setValue("-log");
        commandline.createArgument().setValue(getLog());
    }
}
