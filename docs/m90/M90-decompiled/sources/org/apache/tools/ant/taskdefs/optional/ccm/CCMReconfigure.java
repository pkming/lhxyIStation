package org.apache.tools.ant.taskdefs.optional.ccm;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;

/* JADX INFO: loaded from: classes3.dex */
public class CCMReconfigure extends Continuus {
    public static final String FLAG_PROJECT = "/project";
    public static final String FLAG_RECURSE = "/recurse";
    public static final String FLAG_VERBOSE = "/verbose";
    private String ccmProject = null;
    private boolean recurse = false;
    private boolean verbose = false;

    public CCMReconfigure() {
        setCcmAction(Continuus.COMMAND_RECONFIGURE);
    }

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        Commandline commandline = new Commandline();
        commandline.setExecutable(getCcmCommand());
        commandline.createArgument().setValue(getCcmAction());
        checkOptions(commandline);
        if (Execute.isFailure(run(commandline))) {
            throw new BuildException("Failed executing: " + commandline.toString(), getLocation());
        }
    }

    private void checkOptions(Commandline commandline) {
        if (isRecurse()) {
            commandline.createArgument().setValue(FLAG_RECURSE);
        }
        if (isVerbose()) {
            commandline.createArgument().setValue(FLAG_VERBOSE);
        }
        if (getCcmProject() != null) {
            commandline.createArgument().setValue(FLAG_PROJECT);
            commandline.createArgument().setValue(getCcmProject());
        }
    }

    public String getCcmProject() {
        return this.ccmProject;
    }

    public void setCcmProject(String str) {
        this.ccmProject = str;
    }

    public boolean isRecurse() {
        return this.recurse;
    }

    public void setRecurse(boolean z) {
        this.recurse = z;
    }

    public boolean isVerbose() {
        return this.verbose;
    }

    public void setVerbose(boolean z) {
        this.verbose = z;
    }
}
