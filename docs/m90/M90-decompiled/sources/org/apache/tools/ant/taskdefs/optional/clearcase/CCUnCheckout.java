package org.apache.tools.ant.taskdefs.optional.clearcase;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Execute;
import org.apache.tools.ant.types.Commandline;

/* JADX INFO: loaded from: classes3.dex */
public class CCUnCheckout extends ClearCase {
    public static final String FLAG_KEEPCOPY = "-keep";
    public static final String FLAG_RM = "-rm";
    private boolean mKeep = false;

    @Override // org.apache.tools.ant.Task
    public void execute() throws BuildException {
        Commandline commandline = new Commandline();
        Project project = getProject();
        if (getViewPath() == null) {
            setViewPath(project.getBaseDir().getPath());
        }
        commandline.setExecutable(getClearToolCommand());
        commandline.createArgument().setValue(ClearCase.COMMAND_UNCHECKOUT);
        checkOptions(commandline);
        if (!getFailOnErr()) {
            getProject().log("Ignoring any errors that occur for: " + getViewPathBasename(), 3);
        }
        if (Execute.isFailure(run(commandline)) && getFailOnErr()) {
            throw new BuildException("Failed executing: " + commandline.toString(), getLocation());
        }
    }

    private void checkOptions(Commandline commandline) {
        if (getKeepCopy()) {
            commandline.createArgument().setValue("-keep");
        } else {
            commandline.createArgument().setValue(FLAG_RM);
        }
        commandline.createArgument().setValue(getViewPath());
    }

    public void setKeepCopy(boolean z) {
        this.mKeep = z;
    }

    public boolean getKeepCopy() {
        return this.mKeep;
    }
}
